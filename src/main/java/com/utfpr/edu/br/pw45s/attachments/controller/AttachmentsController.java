package com.utfpr.edu.br.pw45s.attachments.controller;

import com.utfpr.edu.br.pw45s.attachments.dto.AttachmentMapper;
import com.utfpr.edu.br.pw45s.attachments.dto.AttachmentResponse;
import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;
import com.utfpr.edu.br.pw45s.attachments.service.AttachmentsService;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
@RestController
public class AttachmentsController {
	private final AttachmentsService attachmentsService;
	private final AttachmentMapper mapper = new AttachmentMapper();

	public AttachmentsController(AttachmentsService attachmentsService) {
		this.attachmentsService = attachmentsService;
	}

	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@PostMapping("/orders/{orderId}/attachments")
	public ResponseEntity<AttachmentResponse> upload(
		@PathVariable UUID orderId,
		@RequestParam @NotNull AttachmentType type,
		@RequestParam("file") MultipartFile file
	) {
		var attachment = attachmentsService.upload(orderId, type, file);
		return ResponseEntity.ok(mapper.toResponse(attachment));
	}

	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping("/attachments/{id}/download")
	public ResponseEntity<InputStreamResource> download(@PathVariable UUID id) {
		var stream = attachmentsService.download(id);
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=download")
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(new InputStreamResource(stream));
	}
}