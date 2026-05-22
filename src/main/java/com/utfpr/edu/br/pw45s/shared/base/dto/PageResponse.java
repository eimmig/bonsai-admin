package com.utfpr.edu.br.pw45s.shared.base.dto;

import java.util.List;

public record PageResponse<T>(
	List<T> content,
	int page,
	int size,
	long totalElements,
	int totalPages
) {
}