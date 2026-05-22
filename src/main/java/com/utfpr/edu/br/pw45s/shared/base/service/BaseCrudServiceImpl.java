package com.utfpr.edu.br.pw45s.shared.base.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public abstract class BaseCrudServiceImpl<E, ID, R extends JpaRepository<E, ID>>
	implements BaseCrudService<E, ID> {

	protected final R repository;

	protected BaseCrudServiceImpl(R repository) {
		this.repository = repository;
	}

	@Override
	public E save(E entity) {
		return repository.save(entity);
	}

	@Override
	public Optional<E> findById(ID id) {
		return repository.findById(id);
	}

	@Override
	public Page<E> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	@Override
	public void deleteById(ID id) {
		repository.deleteById(id);
	}
}