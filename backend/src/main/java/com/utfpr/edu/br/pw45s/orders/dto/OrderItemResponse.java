package com.utfpr.edu.br.pw45s.orders.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
	UUID id,
	String productName,
	int quantity,
	BigDecimal unitPrice
) {
}