package com.utfpr.edu.br.pw45s.orders.dto;

import com.utfpr.edu.br.pw45s.orders.entity.Order;

public class OrderMapper {

	public OrderResponse toResponse(Order order, String customerEmail) {
		var items = order.getItems().stream()
			.map(i -> new OrderItemResponse(i.getId(), i.getProductName(), i.getQuantity(), i.getUnitPrice()))
			.toList();
		return new OrderResponse(
			order.getId(),
			order.getCustomerId(),
			customerEmail,
			order.getStatus(),
			order.getCreatedAt(),
			order.getUpdatedAt(),
			items
		);
	}
}