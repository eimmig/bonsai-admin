package com.utfpr.edu.br.pw45s.attachments.controller;

import com.utfpr.edu.br.pw45s.attachments.dto.AttachmentMapper;
import com.utfpr.edu.br.pw45s.attachments.dto.AttachmentResponse;
import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;
import com.utfpr.edu.br.pw45s.attachments.service.AttachmentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Anexos")
@RestController
@RequestMapping("/orders")
public class OrderAttachmentsController {
	private final AttachmentsService attachmentsService;
	private final AttachmentMapper mapper;

	public OrderAttachmentsController(AttachmentsService attachmentsService, AttachmentMapper mapper) {
		this.attachmentsService = attachmentsService;
		this.mapper = mapper;
	}

	@Operation(summary = "Fazer upload de anexo", description = "Envia arquivo para o MinIO e associa ao pedido. Para NOTA_FISCAL o arquivo deve ser PDF. Requer ADMIN ou OPERATOR.")
	@ApiResponse(responseCode = "200", description = "Anexo salvo")
	@ApiResponse(responseCode = "400", description = "Arquivo inválido (vazio, muito grande ou tipo errado)")
	@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@PostMapping("/{orderId}/attachments")
	public ResponseEntity<AttachmentResponse> upload(
		@PathVariable UUID orderId,
		@RequestParam @NotNull AttachmentType type,
		@RequestParam("file") MultipartFile file
	) {
		var attachment = attachmentsService.upload(orderId, type, file);
		return ResponseEntity.ok(mapper.toResponse(attachment));
	}
}
