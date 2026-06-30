package com.utfpr.edu.br.pw45s.attachments.service;

import com.utfpr.edu.br.pw45s.attachments.config.MinioProperties;
import com.utfpr.edu.br.pw45s.attachments.entity.Attachment;
import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;
import com.utfpr.edu.br.pw45s.attachments.repository.AttachmentRepository;
import com.utfpr.edu.br.pw45s.orders.repository.OrderRepository;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentsServiceTest {

	@Mock
	private AttachmentRepository attachmentRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private MinioClient minioClient;

	@Mock
	private MinioProperties properties;

	@InjectMocks
	private AttachmentsService attachmentsService;

	@Test
	void uploadStoresFileAndSavesMetadata() throws Exception {
		UUID orderId = UUID.randomUUID();
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"invoice.pdf",
			"application/pdf",
			"data".getBytes()
		);

		when(orderRepository.existsById(orderId)).thenReturn(true);
		when(properties.bucket()).thenReturn("bucket");
		when(minioClient.bucketExists(any())).thenReturn(true);
		when(attachmentRepository.save(any(Attachment.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Attachment saved = attachmentsService.upload(orderId, AttachmentType.NOTA_FISCAL, file);

		assertEquals(orderId, saved.getOrderId());
		assertEquals("invoice.pdf", saved.getFileName());
	}

	@Test
	void uploadRejectsNonPdfInvoice() {
		UUID orderId = UUID.randomUUID();
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"invoice.txt",
			"text/plain",
			"data".getBytes()
		);

		when(orderRepository.existsById(orderId)).thenReturn(true);

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> attachmentsService.upload(orderId, AttachmentType.NOTA_FISCAL, file)
		);

		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
	}

	@Test
	void downloadReturnsStream() throws Exception {
		UUID attachmentId = UUID.randomUUID();
		Attachment attachment = new Attachment(attachmentId, AttachmentType.OUTRO, "file.txt", "text/plain", 4, "key");
		when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));
		when(properties.bucket()).thenReturn("bucket");
		GetObjectResponse response = org.mockito.Mockito.mock(GetObjectResponse.class);
		when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

		var stream = attachmentsService.download(attachmentId);

		assertNotNull(stream);
	}

	@Test
	void downloadThrowsWhenMissing() {
		UUID attachmentId = UUID.randomUUID();
		when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> attachmentsService.download(attachmentId)
		);

		assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
	}
}
