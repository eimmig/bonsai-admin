package com.utfpr.edu.br.pw45s.shared.base.controller;

import com.utfpr.edu.br.pw45s.shared.base.dto.PageResponse;
import com.utfpr.edu.br.pw45s.shared.base.mapper.BaseMapper;
import com.utfpr.edu.br.pw45s.shared.base.service.BaseCrudService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseCrudController<E, D, ID> {

	protected final BaseCrudService<E, ID> service;
	protected final BaseMapper<E, D> mapper;

	protected BaseCrudController(BaseCrudService<E, ID> service, BaseMapper<E, D> mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	@GetMapping("/{id}")
	public ResponseEntity<D> getById(@PathVariable ID id) {
		return service.findById(id)
			.map(mapper::toDto)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<PageResponse<D>> list(Pageable pageable) {
		Page<E> page = service.findAll(pageable);
		PageResponse<D> response = new PageResponse<>(
			page.map(mapper::toDto).toList(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages()
		);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<D> create(@RequestBody @Valid D dto) {
		E saved = service.save(mapper.toEntity(dto));
		return ResponseEntity.ok(mapper.toDto(saved));
	}

	@PutMapping("/{id}")
	public ResponseEntity<D> update(@PathVariable ID id, @RequestBody @Valid D dto) {
		E entity = mapper.toEntity(dto);
		E saved = service.save(entity);
		return ResponseEntity.ok(mapper.toDto(saved));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable ID id) {
		service.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}