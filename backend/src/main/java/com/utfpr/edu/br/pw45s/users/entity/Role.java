package com.utfpr.edu.br.pw45s.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue
	@UuidGenerator
	private UUID id;

	@Column(nullable = false, unique = true)
	private String name;

	protected Role() {
	}

	public Role(String name) {
		this.name = name;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}