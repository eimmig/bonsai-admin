package com.utfpr.edu.br.pw45s.attachments.controller;

import com.utfpr.edu.br.pw45s.attachments.service.AttachmentsService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/attachments")
public class AttachmentsController {
	private final AttachmentsService attachmentsService;

	public AttachmentsController(AttachmentsService attachmentsService) {
		this.attachmentsService = attachmentsService;
	}

	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping("/{id}/download")
	public ResponseEntity<InputStreamResource> download(@PathVariable UUID id) {
		var stream = attachmentsService.download(id);
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=download")
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(new InputStreamResource(stream));
	}
}
