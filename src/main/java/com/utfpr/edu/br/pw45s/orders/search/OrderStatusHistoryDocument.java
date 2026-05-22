package com.utfpr.edu.br.pw45s.orders.search;

import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.UUID;

@Document(indexName = "order_status_history")
public class OrderStatusHistoryDocument {
	@Id
	private String id;

	@Field(type = FieldType.Keyword)
	private UUID orderId;

	@Field(type = FieldType.Keyword)
	private OrderStatus fromStatus;

	@Field(type = FieldType.Keyword)
	private OrderStatus toStatus;

	@Field(type = FieldType.Keyword)
	private String changedBy;

	@Field(type = FieldType.Date)
	private Instant changedAt;

	protected OrderStatusHistoryDocument() {
	}

	public OrderStatusHistoryDocument(UUID orderId, OrderStatus fromStatus, OrderStatus toStatus, String changedBy, Instant changedAt) {
		this.orderId = orderId;
		this.fromStatus = fromStatus;
		this.toStatus = toStatus;
		this.changedBy = changedBy;
		this.changedAt = changedAt;
	}

	public String getId() {
		return id;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public OrderStatus getFromStatus() {
		return fromStatus;
	}

	public OrderStatus getToStatus() {
		return toStatus;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public Instant getChangedAt() {
		return changedAt;
	}
}