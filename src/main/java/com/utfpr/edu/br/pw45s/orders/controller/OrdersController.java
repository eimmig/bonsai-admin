package com.utfpr.edu.br.pw45s.orders.controller;

import com.utfpr.edu.br.pw45s.shared.base.dto.PageResponse;
import com.utfpr.edu.br.pw45s.orders.dto.OrderMapper;
import com.utfpr.edu.br.pw45s.orders.dto.OrderResponse;
import com.utfpr.edu.br.pw45s.orders.dto.OrderStatusHistoryResponse;
import com.utfpr.edu.br.pw45s.orders.dto.UpdateOrderStatusRequest;
import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import com.utfpr.edu.br.pw45s.orders.service.OrdersService;
import com.utfpr.edu.br.pw45s.orders.service.OrderStatusHistoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersController {
	private final OrdersService ordersService;
	private final OrderStatusHistoryService historyService;
	private final OrderMapper mapper = new OrderMapper();

	public OrdersController(OrdersService ordersService, OrderStatusHistoryService historyService) {
		this.ordersService = ordersService;
		this.historyService = historyService;
	}

	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping
	public ResponseEntity<PageResponse<OrderResponse>> list(
		@RequestParam(required = false) OrderStatus status,
		@RequestParam(required = false) UUID customerId,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateFrom,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTo,
		Pageable pageable
	) {
		Page<Order> page = ordersService.list(status, customerId, dateFrom, dateTo, pageable);
		var response = new PageResponse<>(
			page.map(mapper::toResponse).toList(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements(),
			page.getTotalPages()
		);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {
		var order = ordersService.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
		return ResponseEntity.ok(mapper.toResponse(order));
	}

	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@PutMapping("/{id}/status")
	public ResponseEntity<OrderResponse> updateStatus(
		@PathVariable UUID id,
		@RequestBody @Valid UpdateOrderStatusRequest request
	) {
		var order = ordersService.updateStatus(id, request.status());
		return ResponseEntity.status(HttpStatus.OK).body(mapper.toResponse(order));
	}

	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping("/{id}/history")
	public ResponseEntity<List<OrderStatusHistoryResponse>> history(@PathVariable UUID id) {
		return ResponseEntity.ok(historyService.list(id));
	}
}