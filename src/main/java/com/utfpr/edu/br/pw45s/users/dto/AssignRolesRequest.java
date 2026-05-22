package com.utfpr.edu.br.pw45s.users.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignRolesRequest(
	@NotEmpty List<String> roles
) {
}