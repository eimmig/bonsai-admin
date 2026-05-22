package com.utfpr.edu.br.pw45s.notifications.service;

import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationsServiceTest {

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private NotificationsService service;

	@Test
	void sendStatusChangeEmailSendsMessage() {
		MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));
		when(mailSender.createMimeMessage()).thenReturn(message);

		Order order = new Order(UUID.randomUUID());
		service.sendStatusChangeEmail("user@example.com", order, OrderStatus.PAGO, OrderStatus.EM_TRANSPORTE);

		verify(mailSender).send(message);
	}
}
