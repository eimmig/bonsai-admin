package com.utfpr.edu.br.pw45s.orders.repository;

import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.UUID;

public final class OrderSpecifications {
	private OrderSpecifications() {
	}

	public static Specification<Order> hasStatus(OrderStatus status) {
		return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
	}

	public static Specification<Order> hasCustomerId(UUID customerId) {
		return (root, query, cb) -> customerId == null ? null : cb.equal(root.get("customerId"), customerId);
	}

	public static Specification<Order> createdFrom(Instant from) {
		return (root, query, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
	}

	public static Specification<Order> createdTo(Instant to) {
		return (root, query, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), to);
	}
}