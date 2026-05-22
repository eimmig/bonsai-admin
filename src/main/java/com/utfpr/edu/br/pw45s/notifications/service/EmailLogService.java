package com.utfpr.edu.br.pw45s.notifications.service;

import com.utfpr.edu.br.pw45s.notifications.entity.EmailLog;
import com.utfpr.edu.br.pw45s.notifications.entity.EmailStatus;
import com.utfpr.edu.br.pw45s.notifications.repository.EmailLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmailLogService {
	private final EmailLogRepository repository;

	public EmailLogService(EmailLogRepository repository) {
		this.repository = repository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logSent(UUID orderId, String toEmail, String subject) {
		repository.save(new EmailLog(orderId, toEmail, subject, EmailStatus.SENT, null));
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void logFailed(UUID orderId, String toEmail, String subject, String error) {
		repository.save(new EmailLog(orderId, toEmail, subject, EmailStatus.FAILED, error));
	}
}