package com.utfpr.edu.br.pw45s.users.dto;

import com.utfpr.edu.br.pw45s.users.entity.Role;
import com.utfpr.edu.br.pw45s.users.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

	@Test
	void mapsUserToResponse() {
		User user = new User("user@example.com", "hash");
		user.getRoles().add(new Role("ADMIN"));

		UserMapper mapper = new UserMapper();
		UserResponse response = mapper.toResponse(user);

		assertEquals("user@example.com", response.email());
		assertEquals(1, response.roles().size());
	}
}
