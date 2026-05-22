package com.utfpr.edu.br.pw45s.notifications.service;

import com.utfpr.edu.br.pw45s.notifications.entity.EmailLog;
import com.utfpr.edu.br.pw45s.notifications.repository.EmailLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailLogServiceTest {

	@Mock
	private EmailLogRepository repository;

	@InjectMocks
	private EmailLogService service;

	@Test
	void logSentPersistsEntry() {
		service.logSent(UUID.randomUUID(), "user@example.com", "subject");

		verify(repository).save(any(EmailLog.class));
	}

	@Test
	void logFailedPersistsEntry() {
		service.logFailed(UUID.randomUUID(), "user@example.com", "subject", "error");

		verify(repository).save(any(EmailLog.class));
	}
}
