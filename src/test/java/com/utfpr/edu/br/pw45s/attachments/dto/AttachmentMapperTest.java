package com.utfpr.edu.br.pw45s.attachments.dto;

import com.utfpr.edu.br.pw45s.attachments.entity.Attachment;
import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttachmentMapperTest {

	@Test
	void mapsAttachmentToResponse() {
		UUID orderId = UUID.randomUUID();
		Attachment attachment = new Attachment(orderId, AttachmentType.OUTRO, "file.txt", "text/plain", 4, "key");

		AttachmentMapper mapper = new AttachmentMapper();
		AttachmentResponse response = mapper.toResponse(attachment);

		assertEquals(orderId, response.orderId());
		assertEquals("file.txt", response.fileName());
	}
}
