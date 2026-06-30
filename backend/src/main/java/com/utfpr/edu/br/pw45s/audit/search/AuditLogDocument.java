package com.utfpr.edu.br.pw45s.audit.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.UUID;

@Document(indexName = "audit_logs")
public class AuditLogDocument {
	@Id
	private String id;

	@Field(type = FieldType.Keyword)
	private String entityType;

	@Field(type = FieldType.Keyword)
	private UUID entityId;

	@Field(type = FieldType.Keyword)
	private String action;

	@Field(type = FieldType.Text)
	private String details;

	@Field(type = FieldType.Date)
	private Instant createdAt;

	protected AuditLogDocument() {
	}

	public AuditLogDocument(String entityType, UUID entityId, String action, String details, Instant createdAt) {
		this.entityType = entityType;
		this.entityId = entityId;
		this.action = action;
		this.details = details;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}
}