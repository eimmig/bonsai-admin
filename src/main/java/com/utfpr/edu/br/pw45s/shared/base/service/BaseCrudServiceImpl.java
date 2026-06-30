package com.utfpr.edu.br.pw45s.shared.base.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public abstract class BaseCrudServiceImpl<E, ID, R extends JpaRepository<E, ID>> {

	protected final R repository;

	protected BaseCrudServiceImpl(R repository) {
		this.repository = repository;
	}

	public Optional<E> findById(ID id) {
		return repository.findById(id);
	}
}