package com.utfpr.edu.br.pw45s.audit.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AuditLogSearchRepository extends ElasticsearchRepository<AuditLogDocument, String> {
}