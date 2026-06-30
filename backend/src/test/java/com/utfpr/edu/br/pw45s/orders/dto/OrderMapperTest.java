package com.utfpr.edu.br.pw45s.orders.dto;

import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderMapperTest {

	@Test
	void mapsOrderToResponse() {
		UUID customerId = UUID.randomUUID();
		Order order = new Order(customerId);
		OrderItem item = new OrderItem(order, "Keyboard", 1, BigDecimal.valueOf(10));
		order.getItems().add(item);

		OrderMapper mapper = new OrderMapper();
		OrderResponse response = mapper.toResponse(order, "cliente@email.com");

		assertEquals(1, response.items().size());
		assertEquals("Keyboard", response.items().get(0).productName());
		assertEquals("cliente@email.com", response.customerEmail());
	}

	@Test
	void mapsOrderToResponseWithNullEmail() {
		Order order = new Order(UUID.randomUUID());

		OrderMapper mapper = new OrderMapper();
		OrderResponse response = mapper.toResponse(order, null);

		assertNull(response.customerEmail());
	}
}
