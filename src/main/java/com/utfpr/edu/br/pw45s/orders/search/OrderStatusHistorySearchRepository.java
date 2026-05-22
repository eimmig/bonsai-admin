package com.utfpr.edu.br.pw45s.orders.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface OrderStatusHistorySearchRepository extends ElasticsearchRepository<OrderStatusHistoryDocument, String> {
	List<OrderStatusHistoryDocument> findByOrderIdOrderByChangedAtDesc(UUID orderId);
}