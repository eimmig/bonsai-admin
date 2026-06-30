package com.utfpr.edu.br.pw45s.orders.controller;

import com.utfpr.edu.br.pw45s.orders.dto.OrderMapper;
import com.utfpr.edu.br.pw45s.orders.dto.OrderResponse;
import com.utfpr.edu.br.pw45s.orders.dto.OrderStatusHistoryResponse;
import com.utfpr.edu.br.pw45s.orders.dto.UpdateOrderStatusRequest;
import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import com.utfpr.edu.br.pw45s.orders.service.OrderStatusHistoryService;
import com.utfpr.edu.br.pw45s.orders.service.OrdersService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrdersControllerTest {

	@Test
	void listReturnsPage() {
		OrdersService ordersService = mock(OrdersService.class);
		OrderStatusHistoryService historyService = mock(OrderStatusHistoryService.class);
		OrdersController controller = new OrdersController(ordersService, historyService, new OrderMapper());

		Order order = new Order(UUID.randomUUID());
		var page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
		when(ordersService.list(null, null, null, null, PageRequest.of(0, 10))).thenReturn(page);
		when(ordersService.resolveCustomerEmails(anySet())).thenReturn(Map.of());

		var response = controller.list(null, null, null, null, PageRequest.of(0, 10)).getBody();

		assertEquals(1, response.totalElements());
	}

	@Test
	void getByIdReturnsOrder() {
		OrdersService ordersService = mock(OrdersService.class);
		OrderStatusHistoryService historyService = mock(OrderStatusHistoryService.class);
		OrdersController controller = new OrdersController(ordersService, historyService, new OrderMapper());

		UUID id = UUID.randomUUID();
		Order order = new Order(UUID.randomUUID());
		when(ordersService.findById(id)).thenReturn(Optional.of(order));
		when(ordersService.resolveCustomerEmail(any())).thenReturn(null);

		OrderResponse response = controller.getById(id).getBody();

		assertEquals(OrderStatus.AGUARDANDO_PAGAMENTO, response.status());
	}

	@Test
	void updateStatusDelegatesToService() {
		OrdersService ordersService = mock(OrdersService.class);
		OrderStatusHistoryService historyService = mock(OrderStatusHistoryService.class);
		OrdersController controller = new OrdersController(ordersService, historyService, new OrderMapper());

		UUID id = UUID.randomUUID();
		Order order = new Order(UUID.randomUUID());
		order.changeStatus(OrderStatus.PAGO);
		when(ordersService.updateStatus(id, OrderStatus.PAGO)).thenReturn(order);
		when(ordersService.resolveCustomerEmail(any())).thenReturn(null);

		var response = controller.updateStatus(id, new UpdateOrderStatusRequest(OrderStatus.PAGO));

		assertEquals(OrderStatus.PAGO, response.getBody().status());
	}

	@Test
	void historyReturnsList() {
		OrdersService ordersService = mock(OrdersService.class);
		OrderStatusHistoryService historyService = mock(OrderStatusHistoryService.class);
		OrdersController controller = new OrdersController(ordersService, historyService, new OrderMapper());

		UUID id = UUID.randomUUID();
		var history = List.of(new OrderStatusHistoryResponse(OrderStatus.PAGO, OrderStatus.EM_TRANSPORTE, "admin", Instant.now()));
		when(historyService.list(id)).thenReturn(history);

		var response = controller.history(id).getBody();

		assertEquals(1, response.size());
	}
}
