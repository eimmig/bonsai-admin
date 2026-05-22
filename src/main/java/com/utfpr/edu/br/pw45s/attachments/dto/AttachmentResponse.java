package com.utfpr.edu.br.pw45s.attachments.dto;

import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;

import java.time.Instant;
import java.util.UUID;

public record AttachmentResponse(
	UUID id,
	UUID orderId,
	AttachmentType type,
	String fileName,
	String mimeType,
	long sizeBytes,
	Instant createdAt
) {
}