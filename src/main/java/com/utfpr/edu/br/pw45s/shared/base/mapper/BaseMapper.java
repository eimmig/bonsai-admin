package com.utfpr.edu.br.pw45s.shared.base.mapper;

public interface BaseMapper<E, D> {
	D toDto(E entity);
	E toEntity(D dto);
}