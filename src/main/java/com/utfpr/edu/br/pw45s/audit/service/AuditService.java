package com.utfpr.edu.br.pw45s.audit.service;

import com.utfpr.edu.br.pw45s.audit.search.AuditLogDocument;
import com.utfpr.edu.br.pw45s.audit.search.AuditLogSearchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuditService {
	private final AuditLogSearchRepository repository;

	public AuditService(AuditLogSearchRepository repository) {
		this.repository = repository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void log(String entityType, UUID entityId, String action, String details) {
		repository.save(new AuditLogDocument(entityType, entityId, action, details, Instant.now()));
	}
}