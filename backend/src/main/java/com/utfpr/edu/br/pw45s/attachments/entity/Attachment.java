package com.utfpr.edu.br.pw45s.attachments.entity;

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
@Table(name = "attachments")
public class Attachment {
	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AttachmentType type;

	@Column(name = "file_name", nullable = false)
	private String fileName;

	@Column(name = "mime_type", nullable = false)
	private String mimeType;

	@Column(name = "size_bytes", nullable = false)
	private long sizeBytes;

	@Column(name = "storage_key", nullable = false)
	private String storageKey;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected Attachment() {
	}

	public Attachment(UUID orderId, AttachmentType type, String fileName, String mimeType, long sizeBytes, String storageKey) {
		this.orderId = orderId;
		this.type = type;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.sizeBytes = sizeBytes;
		this.storageKey = storageKey;
	}

	public UUID getId() {
		return id;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public AttachmentType getType() {
		return type;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public long getSizeBytes() {
		return sizeBytes;
	}

	public String getStorageKey() {
		return storageKey;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}