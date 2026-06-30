package com.utfpr.edu.br.pw45s.orders.service;

import com.utfpr.edu.br.pw45s.orders.dto.OrderStatusHistoryResponse;
import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import com.utfpr.edu.br.pw45s.orders.search.OrderStatusHistoryDocument;
import com.utfpr.edu.br.pw45s.orders.search.OrderStatusHistorySearchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderStatusHistoryService {
	private final OrderStatusHistorySearchRepository repository;

	public OrderStatusHistoryService(OrderStatusHistorySearchRepository repository) {
		this.repository = repository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void registerEntry(UUID orderId, OrderStatus from, OrderStatus to, String actor) {
		repository.save(new OrderStatusHistoryDocument(orderId, from, to, actor, Instant.now()));
	}

	@Transactional(readOnly = true)
	public List<OrderStatusHistoryResponse> list(UUID orderId) {
		return repository.findByOrderIdOrderByChangedAtDesc(orderId).stream()
			.map(h -> new OrderStatusHistoryResponse(
				h.getFromStatus(),
				h.getToStatus(),
				h.getChangedBy(),
				h.getChangedAt()
			))
			.toList();
	}
}