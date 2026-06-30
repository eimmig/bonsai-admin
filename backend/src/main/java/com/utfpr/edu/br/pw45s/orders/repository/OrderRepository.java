package com.utfpr.edu.br.pw45s.orders.repository;

import com.utfpr.edu.br.pw45s.orders.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countGroupedByStatus();

    @Override
    @EntityGraph(attributePaths = {"items"})
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findById(UUID id);
}