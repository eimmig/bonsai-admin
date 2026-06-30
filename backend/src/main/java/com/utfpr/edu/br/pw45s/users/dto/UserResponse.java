package com.utfpr.edu.br.pw45s.users.dto;

import java.util.List;
import java.util.UUID;

public record UserResponse(
	UUID id,
	String email,
	boolean active,
	List<String> roles
) {
}