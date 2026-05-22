package com.utfpr.edu.br.pw45s.attachments.controller;

import com.utfpr.edu.br.pw45s.attachments.dto.AttachmentResponse;
import com.utfpr.edu.br.pw45s.attachments.entity.Attachment;
import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;
import com.utfpr.edu.br.pw45s.attachments.service.AttachmentsService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AttachmentsControllerTest {

	@Test
	void uploadDelegatesToService() {
		AttachmentsService service = mock(AttachmentsService.class);
		AttachmentsController controller = new AttachmentsController(service);
		UUID orderId = UUID.randomUUID();
		Attachment attachment = new Attachment(orderId, AttachmentType.OUTRO, "file.txt", "text/plain", 4, "key");
		MultipartFile file = mock(MultipartFile.class);
		when(service.upload(orderId, AttachmentType.OUTRO, file)).thenReturn(attachment);

		var response = controller.upload(orderId, AttachmentType.OUTRO, file);

		AttachmentResponse body = response.getBody();
		assertEquals(orderId, body.orderId());
	}

	@Test
	void downloadReturnsStream() {
		AttachmentsService service = mock(AttachmentsService.class);
		AttachmentsController controller = new AttachmentsController(service);
		UUID id = UUID.randomUUID();
		when(service.download(id)).thenReturn(new ByteArrayInputStream("data".getBytes()));

		var response = controller.download(id);

		InputStreamResource resource = response.getBody();
		assertEquals(200, response.getStatusCode().value());
		assertNotNull(resource);
	}
}
