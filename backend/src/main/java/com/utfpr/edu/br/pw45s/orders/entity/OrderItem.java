package com.utfpr.edu.br.pw45s.orders.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {
	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@Column(name = "product_name", nullable = false)
	private String productName;

	@Column(nullable = false)
	private int quantity;

	@Column(name = "unit_price", nullable = false)
	private BigDecimal unitPrice;

	protected OrderItem() {
	}

	public OrderItem(Order order, String productName, int quantity, BigDecimal unitPrice) {
		this.order = order;
		this.productName = productName;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	public UUID getId() {
		return id;
	}

	public Order getOrder() {
		return order;
	}

	public String getProductName() {
		return productName;
	}

	public int getQuantity() {
		return quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
}