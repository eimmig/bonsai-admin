package com.utfpr.edu.br.pw45s.audit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(name = "entity_type", nullable = false)
	private String entityType;

	@Column(name = "entity_id", nullable = false)
	private UUID entityId;

	@Column(nullable = false)
	private String action;

	@Column
	private String details;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected AuditLog() {
	}

	public AuditLog(String entityType, UUID entityId, String action, String details) {
		this.entityType = entityType;
		this.entityId = entityId;
		this.action = action;
		this.details = details;
	}

	public UUID getId() {
		return id;
	}

	public String getEntityType() {
		return entityType;
	}

	public UUID getEntityId() {
		return entityId;
	}

	public String getAction() {
		return action;
	}

	public String getDetails() {
		return details;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}