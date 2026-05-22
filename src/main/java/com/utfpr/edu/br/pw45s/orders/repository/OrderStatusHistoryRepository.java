package com.utfpr.edu.br.pw45s.orders.repository;

import com.utfpr.edu.br.pw45s.orders.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
	List<OrderStatusHistory> findByOrderIdOrderByChangedAtDesc(UUID orderId);
}