package com.utfpr.edu.br.pw45s.orders.service;

import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;
import com.utfpr.edu.br.pw45s.attachments.repository.AttachmentRepository;
import com.utfpr.edu.br.pw45s.audit.service.AuditService;
import com.utfpr.edu.br.pw45s.notifications.service.EmailLogService;
import com.utfpr.edu.br.pw45s.notifications.service.NotificationsService;
import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import com.utfpr.edu.br.pw45s.orders.repository.OrderRepository;
import com.utfpr.edu.br.pw45s.users.entity.User;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private AttachmentRepository attachmentRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private NotificationsService notificationsService;

	@Mock
	private EmailLogService emailLogService;

	@Mock
	private AuditService auditService;

	@Mock
	private OrderStatusHistoryService historyService;

	@InjectMocks
	private OrdersService ordersService;

	@Test
	void listReturnsPage() {
		UUID customerId = UUID.randomUUID();
		Order order = new Order(customerId);
		var page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
		when(orderRepository.findAll(any(Specification.class), eq(PageRequest.of(0, 10)))).thenReturn(page);

		var result = ordersService.list(OrderStatus.AGUARDANDO_PAGAMENTO, customerId, Instant.now(), Instant.now(), PageRequest.of(0, 10));

		assertEquals(1, result.getTotalElements());
	}

	@Test
	void updateStatusRequiresInvoiceForTransport() {
		UUID orderId = UUID.randomUUID();
		Order order = new Order(UUID.randomUUID());
		setId(order, orderId);
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(attachmentRepository.existsByOrderIdAndTypeAndMimeType(orderId, AttachmentType.NOTA_FISCAL, "application/pdf"))
			.thenReturn(false);

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> ordersService.updateStatus(orderId, OrderStatus.EM_TRANSPORTE)
		);

		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
	}

	@Test
	void updateStatusSendsEmailAndLogs() {
		UUID orderId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		Order order = new Order(customerId);
		setId(order, orderId);
		order.changeStatus(OrderStatus.AGUARDANDO_PAGAMENTO);

		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(attachmentRepository.existsByOrderIdAndTypeAndMimeType(orderId, AttachmentType.NOTA_FISCAL, "application/pdf"))
			.thenReturn(true);
		when(userRepository.findById(customerId)).thenReturn(Optional.of(new User("user@example.com", "hash")));

		Order updated = ordersService.updateStatus(orderId, OrderStatus.EM_TRANSPORTE);

		assertEquals(OrderStatus.EM_TRANSPORTE, updated.getStatus());
		verify(historyService).record(eq(orderId), eq(OrderStatus.AGUARDANDO_PAGAMENTO), eq(OrderStatus.EM_TRANSPORTE), any());
		verify(auditService).log(eq("ORDER"), eq(orderId), eq("STATUS_CHANGE"), any());
		verify(emailLogService).logSent(eq(orderId), eq("user@example.com"), any());
	}

	@Test
	void updateStatusLogsFailedEmailWhenNotificationFails() {
		UUID orderId = UUID.randomUUID();
		UUID customerId = UUID.randomUUID();
		Order order = new Order(customerId);
		setId(order, orderId);
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(attachmentRepository.existsByOrderIdAndTypeAndMimeType(orderId, AttachmentType.NOTA_FISCAL, "application/pdf"))
			.thenReturn(true);
		when(userRepository.findById(customerId)).thenReturn(Optional.of(new User("user@example.com", "hash")));
		doThrow(new RuntimeException("fail")).when(notificationsService)
			.sendStatusChangeEmail(eq("user@example.com"), any(), eq(OrderStatus.AGUARDANDO_PAGAMENTO), eq(OrderStatus.EM_TRANSPORTE));

		Order updated = ordersService.updateStatus(orderId, OrderStatus.EM_TRANSPORTE);

		assertNotNull(updated);
		verify(emailLogService).logFailed(eq(orderId), eq("user@example.com"), any(), eq("fail"));
	}

	@Test
	void updateStatusThrowsWhenOrderMissing() {
		UUID orderId = UUID.randomUUID();
		when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> ordersService.updateStatus(orderId, OrderStatus.PAGO)
		);

		assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
	}

	private static void setId(Order order, UUID id) {
		try {
			Field field = Order.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(order, id);
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}
}
