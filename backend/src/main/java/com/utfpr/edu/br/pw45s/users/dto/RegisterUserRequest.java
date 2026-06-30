package com.utfpr.edu.br.pw45s.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
	@Email @NotBlank String email,
	@NotBlank @Size(min = 6, max = 100) String password
) {
}