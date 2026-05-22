package com.utfpr.edu.br.pw45s.notifications.service;

import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationsService {
	private final JavaMailSender mailSender;

	public NotificationsService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendStatusChangeEmail(String toEmail, Order order, OrderStatus from, OrderStatus to) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			helper.setTo(toEmail);
			helper.setSubject("Atualização do pedido #" + order.getId());
			helper.setText(buildHtmlBody(order, from, to), true);
			
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Erro ao enviar email", e);
		}
	}

	private String buildHtmlBody(Order order, OrderStatus from, OrderStatus to) {
		return """
			<!DOCTYPE html>
			<html>
			<head>
				<meta charset="UTF-8">
				<style>
					body {
						font-family: 'Arial', sans-serif;
						background-color: #f4f4f4;
						margin: 0;
						padding: 20px;
					}
					.container {
						max-width: 600px;
						margin: 0 auto;
						background-color: #ffffff;
						border-radius: 8px;
						box-shadow: 0 2px 4px rgba(0,0,0,0.1);
						overflow: hidden;
					}
					.header {
						background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
						color: white;
						padding: 30px;
						text-align: center;
					}
					.header h1 {
						margin: 0;
						font-size: 24px;
					}
					.content {
						padding: 30px;
					}
					.status-card {
						background-color: #f8f9fa;
						border-left: 4px solid #667eea;
						padding: 20px;
						margin: 20px 0;
						border-radius: 4px;
					}
					.status-change {
						display: flex;
						align-items: center;
						justify-content: center;
						margin: 20px 0;
					}
					.status-badge {
						padding: 10px 20px;
						border-radius: 20px;
						font-weight: bold;
						font-size: 14px;
					}
					.status-from {
						background-color: #ffc107;
						color: #000;
					}
					.status-to {
						background-color: #28a745;
						color: white;
					}
					.arrow {
						margin: 0 15px;
						font-size: 24px;
						color: #667eea;
					}
					.order-details {
						margin-top: 20px;
						padding-top: 20px;
						border-top: 1px solid #e0e0e0;
					}
					.detail-row {
						margin: 10px 0;
						color: #555;
					}
					.detail-label {
						font-weight: bold;
						color: #333;
					}
					.footer {
						background-color: #f8f9fa;
						padding: 20px;
						text-align: center;
						color: #666;
						font-size: 12px;
					}
				</style>
			</head>
			<body>
				<div class="container">
					<div class="header">
						<h1>📦 Atualização de Pedido</h1>
					</div>
					<div class="content">
						<p>Olá!</p>
						<p>Seu pedido foi atualizado com sucesso.</p>
						
						<div class="status-card">
							<div class="status-change">
								<span class="status-badge status-from">%s</span>
								<span class="arrow">→</span>
								<span class="status-badge status-to">%s</span>
							</div>
						</div>
						
						<div class="order-details">
							<div class="detail-row">
								<span class="detail-label">Número do Pedido:</span> #%s
							</div>
							<div class="detail-row">
								<span class="detail-label">Status Atual:</span> %s
							</div>
						</div>
						
						<p style="margin-top: 30px;">
							Obrigado por escolher nossos serviços! 🎉
						</p>
					</div>
					<div class="footer">
						<p>Este é um email automático. Por favor, não responda.</p>
							<p>&copy; 2026 - Todos os direitos reservados</p>
					</div>
				</div>
			</body>
			</html>
			""".formatted(from, to, order.getId(), to);
	}
}