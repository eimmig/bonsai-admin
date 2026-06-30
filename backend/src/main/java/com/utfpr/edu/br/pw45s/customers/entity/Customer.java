package com.utfpr.edu.br.pw45s.customers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {
	@Id
	private UUID id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String name;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	protected Customer() {
	}

	public Customer(UUID id, String email, String name) {
		this.id = id;
		this.email = email;
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
