package com.utfpr.edu.br.pw45s.orders.dto;

import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;

import java.time.Instant;

public record OrderStatusHistoryResponse(
	String id,
	OrderStatus fromStatus,
	OrderStatus toStatus,
	String changedBy,
	Instant changedAt
) {
}