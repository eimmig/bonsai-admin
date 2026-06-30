package com.utfpr.edu.br.pw45s.orders.service;

import com.utfpr.edu.br.pw45s.shared.base.service.BaseCrudServiceImpl;
import com.utfpr.edu.br.pw45s.attachments.entity.AttachmentType;
import com.utfpr.edu.br.pw45s.attachments.repository.AttachmentRepository;
import com.utfpr.edu.br.pw45s.audit.service.AuditService;
import com.utfpr.edu.br.pw45s.notifications.service.NotificationsService;
import com.utfpr.edu.br.pw45s.orders.entity.Order;
import com.utfpr.edu.br.pw45s.orders.entity.OrderStatus;
import com.utfpr.edu.br.pw45s.orders.repository.OrderRepository;
import com.utfpr.edu.br.pw45s.orders.repository.OrderSpecifications;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrdersService extends BaseCrudServiceImpl<Order, UUID, OrderRepository> {
	private static final Logger log = LoggerFactory.getLogger(OrdersService.class);
	private static final String PDF_MIME = "application/pdf";
	private final AttachmentRepository attachmentRepository;
	private final UserRepository userRepository;
	private final NotificationsService notificationsService;
	private final AuditService auditService;
	private final OrderStatusHistoryService historyService;

	public OrdersService(
		OrderRepository repository,
		AttachmentRepository attachmentRepository,
		UserRepository userRepository,
		NotificationsService notificationsService,
		AuditService auditService,
		OrderStatusHistoryService historyService
	) {
		super(repository);
		this.attachmentRepository = attachmentRepository;
		this.userRepository = userRepository;
		this.notificationsService = notificationsService;
		this.auditService = auditService;
		this.historyService = historyService;
	}

	@Transactional(readOnly = true)
	public List<UUID> listCustomerIds() {
		return repository.findDistinctCustomerIds();
	}

	@Transactional(readOnly = true)
	public Map<OrderStatus, Long> summary() {
		Map<OrderStatus, Long> counts = new EnumMap<>(OrderStatus.class);
		for (OrderStatus s : OrderStatus.values()) counts.put(s, 0L);
		repository.countGroupedByStatus()
			.forEach(row -> counts.put((OrderStatus) row[0], (Long) row[1]));
		return counts;
	}

	@Transactional(readOnly = true)
	public Page<Order> list(OrderStatus status, UUID customerId, Instant dateFrom, Instant dateTo, Pageable pageable) {
		Specification<Order> spec = Specification.where(OrderSpecifications.hasStatus(status))
			.and(OrderSpecifications.hasCustomerId(customerId))
			.and(OrderSpecifications.createdFrom(dateFrom))
			.and(OrderSpecifications.createdTo(dateTo));
		return repository.findAll(spec, pageable);
	}

	@Transactional
	public Order updateStatus(UUID id, OrderStatus status) {
		Order order = repository.findById(id)
			.orElseThrow(() -> {
				log.warn("Order not found: {}", id);
				return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
			});
		OrderStatus from = order.getStatus();
		if (status == OrderStatus.EM_TRANSPORTE) {
			boolean hasInvoice = attachmentRepository.existsByOrderIdAndTypeAndMimeType(
				id,
				AttachmentType.NOTA_FISCAL,
				PDF_MIME
			);
			if (!hasInvoice) {
				log.warn("Status update rejected — nota fiscal PDF missing for order {}", id);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nota fiscal PDF required");
			}
		}
		order.changeStatus(status);
		Order saved = repository.save(order);

		String actor = resolveActor();
		log.info("Order {} status updated: {} → {} by {}", saved.getId(), from.getLabel(), status.getLabel(), actor);
		historyService.registerEntry(saved.getId(), from, status, actor);
		auditService.log("ORDER", saved.getId(), "STATUS_CHANGE", "from=" + from + ", to=" + status + ", by=" + actor);
		sendStatusEmail(saved, from, status);
		return saved;
	}

	private void sendStatusEmail(Order order, OrderStatus from, OrderStatus to) {
		var user = userRepository.findById(order.getCustomerId());
		if (user.isEmpty()) {
			log.warn("Customer {} not found for order {} — skipping status email", order.getCustomerId(), order.getId());
			return;
		}
		String email = user.get().getEmail();
		try {
			notificationsService.sendStatusChangeEmail(email, order, from, to);
		} catch (Exception ex) {
			log.warn("Status email failed for order {} to {}: {}", order.getId(), email, ex.getMessage());
		}
	}

	private String resolveActor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getName() == null) {
			return "system";
		}
		return auth.getName();
	}
}