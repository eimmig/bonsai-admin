package com.utfpr.edu.br.pw45s.orders.dto;

import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
	UUID id,
	UUID customerId,
	String customerEmail,
	OrderStatus status,
	Instant createdAt,
	Instant updatedAt,
	List<OrderItemResponse> items
) {
}