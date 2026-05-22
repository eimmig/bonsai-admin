package com.utfpr.edu.br.pw45s.orders.dto;

import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderMapperTest {

	@Test
	void mapsOrderToResponse() {
		Order order = new Order(UUID.randomUUID());
		OrderItem item = new OrderItem(order, "Keyboard", 1, BigDecimal.valueOf(10));
		order.getItems().add(item);

		OrderMapper mapper = new OrderMapper();
		OrderResponse response = mapper.toResponse(order);

		assertEquals(1, response.items().size());
		assertEquals("Keyboard", response.items().get(0).productName());
	}
}
