package com.utfpr.edu.br.pw45s.auth.dto;

import java.util.List;

public record LoginResponse(
	String token,
	String email,
	List<String> roles
) {
}