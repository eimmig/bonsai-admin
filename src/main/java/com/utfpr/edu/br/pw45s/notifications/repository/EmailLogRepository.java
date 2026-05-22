package com.utfpr.edu.br.pw45s.notifications.repository;

import com.utfpr.edu.br.pw45s.notifications.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {
}