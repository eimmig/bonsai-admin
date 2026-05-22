package com.utfpr.edu.br.pw45s.attachments.service;

import com.utfpr.edu.br.pw45s.attachments.config.MinioProperties;
import com.utfpr.edu.br.pw45s.attachments.entity.Attachment;
import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;
import com.utfpr.edu.br.pw45s.attachments.repository.AttachmentRepository;
import com.utfpr.edu.br.pw45s.orders.repository.OrderRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.UUID;

@Service
public class AttachmentsService {
	private static final String PDF_MIME = "application/pdf";

	private final AttachmentRepository attachmentRepository;
	private final OrderRepository orderRepository;
	private final MinioClient minioClient;
	private final MinioProperties properties;

	public AttachmentsService(
		AttachmentRepository attachmentRepository,
		OrderRepository orderRepository,
		MinioClient minioClient,
		MinioProperties properties
	) {
		this.attachmentRepository = attachmentRepository;
		this.orderRepository = orderRepository;
		this.minioClient = minioClient;
		this.properties = properties;
	}

	@Transactional
	public Attachment upload(UUID orderId, AttachmentType type, MultipartFile file) {
		validateOrder(orderId);
		validateFile(type, file);
		ensureBucket();

		String storageKey = buildStorageKey(orderId, file.getOriginalFilename());
		try (InputStream input = file.getInputStream()) {
			minioClient.putObject(
				PutObjectArgs.builder()
					.bucket(properties.bucket())
					.object(storageKey)
					.stream(input, file.getSize(), -1L)
					.contentType(file.getContentType())
					.build()
			);
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file", ex);
		}

		Attachment attachment = new Attachment(
			orderId,
			type,
			file.getOriginalFilename(),
			file.getContentType(),
			file.getSize(),
			storageKey
		);
		return attachmentRepository.save(attachment);
	}

	@Transactional(readOnly = true)
	public InputStream download(UUID attachmentId) {
		Attachment attachment = attachmentRepository.findById(attachmentId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
		try {
			return minioClient.getObject(
				GetObjectArgs.builder()
					.bucket(properties.bucket())
					.object(attachment.getStorageKey())
					.build()
			);
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download file", ex);
		}
	}

	private void validateOrder(UUID orderId) {
		if (!orderRepository.existsById(orderId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
		}
	}

	private void validateFile(AttachmentType type, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
		}
		if (type == AttachmentType.NOTA_FISCAL && !PDF_MIME.equals(file.getContentType())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOTA_FISCAL must be a PDF");
		}
	}

	private void ensureBucket() {
		try {
			boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.bucket()).build());
			if (!exists) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.bucket()).build());
			}
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to ensure bucket", ex);
		}
	}

	private String buildStorageKey(UUID orderId, String originalName) {
		String safeName = originalName == null ? "file" : originalName.replaceAll("\\s+", "-");
		return orderId + "/" + UUID.randomUUID() + "-" + safeName;
	}
}