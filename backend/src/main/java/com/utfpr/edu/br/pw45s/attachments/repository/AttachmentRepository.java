package com.utfpr.edu.br.pw45s.attachments.repository;

import com.utfpr.edu.br.pw45s.attachments.entity.Attachment;
import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
	boolean existsByOrderIdAndTypeAndMimeType(UUID orderId, AttachmentType type, String mimeType);
}