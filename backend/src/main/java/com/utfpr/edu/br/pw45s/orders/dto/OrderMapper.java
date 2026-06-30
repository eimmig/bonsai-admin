package com.utfpr.edu.br.pw45s.orders.dto;

import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;

public class OrderMapper {
	private final UserRepository userRepository;

	public OrderMapper(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public OrderResponse toResponse(Order order) {
		var items = order.getItems().stream()
			.map(i -> new OrderItemResponse(i.getId(), i.getProductName(), i.getQuantity(), i.getUnitPrice()))
			.toList();
		String customerEmail = userRepository.findById(order.getCustomerId())
			.map(u -> u.getEmail())
			.orElse(null);
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