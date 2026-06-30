package com.utfpr.edu.br.pw45s.shared.base.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
	List<T> content,
	int page,
	int size,
	long totalElements,
	int totalPages
) {
	public static <T> PageResponse<T> of(Page<T> page) {
		return new PageResponse<>(
			page.toList(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages()
		);
	}
}