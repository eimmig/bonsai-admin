package com.utfpr.edu.br.pw45s.attachments.dto;

import com.utfpr.edu.br.pw45s.attachments.entity.Attachment;

public class AttachmentMapper {
	public AttachmentResponse toResponse(Attachment attachment) {
		return new AttachmentResponse(
			attachment.getId(),
			attachment.getOrderId(),
			attachment.getType(),
			attachment.getFileName(),
			attachment.getMimeType(),
			attachment.getSizeBytes(),
			attachment.getCreatedAt()
		);
	}
}