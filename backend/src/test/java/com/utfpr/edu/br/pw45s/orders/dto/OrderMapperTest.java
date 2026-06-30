package com.utfpr.edu.br.pw45s.orders.dto;

import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderItem;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderMapperTest {

	@Test
	void mapsOrderToResponse() {
		UUID customerId = UUID.randomUUID();
		Order order = new Order(customerId);
		OrderItem item = new OrderItem(order, "Keyboard", 1, BigDecimal.valueOf(10));
		order.getItems().add(item);

		UserRepository userRepository = mock(UserRepository.class);
		when(userRepository.findById(customerId)).thenReturn(Optional.empty());

		OrderMapper mapper = new OrderMapper(userRepository);
		OrderResponse response = mapper.toResponse(order);

		assertEquals(1, response.items().size());
		assertEquals("Keyboard", response.items().get(0).productName());
		assertNull(response.customerEmail());
	}
}
