package com.utfpr.edu.br.pw45s.orders.dto;

import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
	@NotNull OrderStatus status
) {
}