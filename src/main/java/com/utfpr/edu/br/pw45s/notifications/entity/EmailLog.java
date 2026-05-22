package com.utfpr.edu.br.pw45s.notifications.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_logs")
public class EmailLog {
	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Column(name = "to_email", nullable = false)
	private String toEmail;

	@Column(nullable = false)
	private String subject;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EmailStatus status;

	@Column(name = "error_message")
	private String errorMessage;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected EmailLog() {
	}

	public EmailLog(UUID orderId, String toEmail, String subject, EmailStatus status, String errorMessage) {
		this.orderId = orderId;
		this.toEmail = toEmail;
		this.subject = subject;
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public UUID getId() {
		return id;
	}
}