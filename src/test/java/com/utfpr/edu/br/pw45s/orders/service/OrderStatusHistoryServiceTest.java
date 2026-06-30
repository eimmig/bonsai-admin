package com.utfpr.edu.br.pw45s.orders.service;

import com.utfpr.edu.br.pw45s.orders.dto.OrderStatusHistoryResponse;
import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import com.utfpr.edu.br.pw45s.orders.search.OrderStatusHistoryDocument;
import com.utfpr.edu.br.pw45s.orders.search.OrderStatusHistorySearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderStatusHistoryServiceTest {

	@Mock
	private OrderStatusHistorySearchRepository repository;

	@InjectMocks
	private OrderStatusHistoryService service;

	@Test
	void recordSavesDocument() {
		UUID orderId = UUID.randomUUID();
		service.registerEntry(orderId, OrderStatus.PAGO, OrderStatus.EM_TRANSPORTE, "admin");

		verify(repository).save(any(OrderStatusHistoryDocument.class));
	}

	@Test
	void listMapsResponses() {
		UUID orderId = UUID.randomUUID();
		OrderStatusHistoryDocument doc = new OrderStatusHistoryDocument(
			orderId,
			OrderStatus.PAGO,
			OrderStatus.EM_TRANSPORTE,
			"admin",
			Instant.now()
		);
		when(repository.findByOrderIdOrderByChangedAtDesc(orderId)).thenReturn(List.of(doc));

		List<OrderStatusHistoryResponse> result = service.list(orderId);

		assertEquals(1, result.size());
		assertEquals(OrderStatus.PAGO, result.get(0).fromStatus());
	}
}
