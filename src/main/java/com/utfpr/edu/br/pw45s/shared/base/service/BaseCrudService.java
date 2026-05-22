package com.utfpr.edu.br.pw45s.shared.base.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BaseCrudService<E, ID> {
	E save(E entity);
	Optional<E> findById(ID id);
	Page<E> findAll(Pageable pageable);
	void deleteById(ID id);
}