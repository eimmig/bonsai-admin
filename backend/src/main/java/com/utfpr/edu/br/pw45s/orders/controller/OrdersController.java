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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Pedidos")
@RestController
@RequestMapping("/orders")
public class OrdersController {
	private final OrdersService ordersService;
	private final OrderStatusHistoryService historyService;
	private final OrderMapper mapper;

	public OrdersController(OrdersService ordersService, OrderStatusHistoryService historyService, OrderMapper mapper) {
		this.ordersService = ordersService;
		this.historyService = historyService;
		this.mapper = mapper;
	}

	@Operation(summary = "IDs de clientes com pedidos", description = "Retorna página de UUIDs distintos de clientes que possuem pelo menos um pedido. Requer ADMIN ou OPERATOR.")
	@ApiResponse(responseCode = "200", description = "Página de UUIDs")
	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping("/customers")
	public ResponseEntity<PageResponse<UUID>> customers(Pageable pageable) {
		return ResponseEntity.ok(PageResponse.of(ordersService.listCustomerIds(pageable)));
	}

	@Operation(summary = "Totalizadores por status", description = "Retorna a contagem de pedidos em cada status. Requer ADMIN ou OPERATOR.")
	@ApiResponse(responseCode = "200", description = "Mapa de status → quantidade")
	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping("/summary")
	public ResponseEntity<Map<OrderStatus, Long>> summary() {
		return ResponseEntity.ok(ordersService.summary());
	}

	@Operation(summary = "Listar pedidos", description = "Filtros opcionais: status, customerId, dateFrom, dateTo. Requer ADMIN ou OPERATOR.")
	@ApiResponse(responseCode = "200", description = "Página de pedidos")
	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping
	public ResponseEntity<PageResponse<OrderResponse>> list(
		@RequestParam(required = false) OrderStatus status,
		@RequestParam(required = false) UUID customerId,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateFrom,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant dateTo,
		Pageable pageable
	) {
		var page = ordersService.list(status, customerId, dateFrom, dateTo, pageable);
		Set<UUID> ids = page.stream().map(Order::getCustomerId).collect(Collectors.toSet());
		Map<UUID, String> emailMap = ordersService.resolveCustomerEmails(ids);
		return ResponseEntity.ok(PageResponse.of(page.map(o -> mapper.toResponse(o, emailMap.get(o.getCustomerId())))));
	}

	@Operation(summary = "Buscar pedido por ID", description = "Requer ADMIN ou OPERATOR.")
	@ApiResponse(responseCode = "200", description = "Pedido encontrado")
	@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {
		var order = ordersService.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
		return ResponseEntity.ok(mapper.toResponse(order, ordersService.resolveCustomerEmail(order.getCustomerId())));
	}

	@Operation(summary = "Atualizar status do pedido", description = "Para EM_TRANSPORTE é obrigatório ter nota fiscal PDF anexada. Requer ADMIN ou OPERATOR.")
	@ApiResponse(responseCode = "200", description = "Status atualizado")
	@ApiResponse(responseCode = "400", description = "Nota fiscal PDF ausente para EM_TRANSPORTE")
	@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@PutMapping("/{id}/status")
	public ResponseEntity<OrderResponse> updateStatus(
		@PathVariable UUID id,
		@RequestBody @Valid UpdateOrderStatusRequest request
	) {
		var order = ordersService.updateStatus(id, request.status());
		return ResponseEntity.status(HttpStatus.OK).body(mapper.toResponse(order, ordersService.resolveCustomerEmail(order.getCustomerId())));
	}

	@Operation(summary = "Histórico de status", description = "Lista todas as mudanças de status de um pedido. Requer ADMIN ou OPERATOR.")
	@ApiResponse(responseCode = "200", description = "Lista de entradas do histórico")
	@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
	@GetMapping("/{id}/history")
	public ResponseEntity<List<OrderStatusHistoryResponse>> history(@PathVariable UUID id) {
		return ResponseEntity.ok(historyService.list(id));
	}
}
