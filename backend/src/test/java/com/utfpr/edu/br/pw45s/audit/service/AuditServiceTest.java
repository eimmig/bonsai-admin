package com.utfpr.edu.br.pw45s.audit.service;

import com.utfpr.edu.br.pw45s.audit.search.AuditLogDocument;
import com.utfpr.edu.br.pw45s.audit.search.AuditLogSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

	@Mock
	private AuditLogSearchRepository repository;

	@InjectMocks
	private AuditService service;

	@Test
	void logSavesDocument() {
		service.log("ORDER", UUID.randomUUID(), "STATUS_CHANGE", "details");

		verify(repository).save(any(AuditLogDocument.class));
	}
}
