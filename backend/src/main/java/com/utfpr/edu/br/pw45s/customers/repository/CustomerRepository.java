package com.utfpr.edu.br.pw45s.customers.repository;

import com.utfpr.edu.br.pw45s.customers.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
