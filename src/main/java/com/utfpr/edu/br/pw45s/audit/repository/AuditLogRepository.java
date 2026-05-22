package com.utfpr.edu.br.pw45s.audit.repository;

import com.utfpr.edu.br.pw45s.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}