package com.utfpr.edu.br.pw45s.users.dto;

import com.utfpr.edu.br.pw45s.users.entity.Role;
import com.utfpr.edu.br.pw45s.users.entity.User;

public class UserMapper {
	public UserResponse toResponse(User user) {
		var roles = user.getRoles().stream()
			.map(Role::getName)
			.toList();
		return new UserResponse(user.getId(), user.getEmail(), user.isActive(), roles);
	}
}