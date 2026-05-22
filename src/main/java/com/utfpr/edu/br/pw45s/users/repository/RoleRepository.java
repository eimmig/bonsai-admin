package com.utfpr.edu.br.pw45s.users.repository;

import com.utfpr.edu.br.pw45s.users.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
	Optional<Role> findByName(String name);
}