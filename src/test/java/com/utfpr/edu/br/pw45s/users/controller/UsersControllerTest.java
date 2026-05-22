package com.utfpr.edu.br.pw45s.users.controller;

import com.utfpr.edu.br.pw45s.shared.base.dto.PageResponse;
import com.utfpr.edu.br.pw45s.users.dto.AssignRolesRequest;
import com.utfpr.edu.br.pw45s.users.dto.RegisterUserRequest;
import com.utfpr.edu.br.pw45s.users.dto.UserResponse;
import com.utfpr.edu.br.pw45s.users.entity.Role;
import com.utfpr.edu.br.pw45s.users.entity.User;
import com.utfpr.edu.br.pw45s.users.service.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsersControllerTest {

	@Test
	void registerReturnsCreatedUser() {
		UsersService service = mock(UsersService.class);
		UsersController controller = new UsersController(service);
		User user = new User("user@example.com", "hash");
		when(service.register("user@example.com", "pass")).thenReturn(user);

		var response = controller.register(new RegisterUserRequest("user@example.com", "pass"));

		assertEquals(201, response.getStatusCode().value());
		assertEquals("user@example.com", response.getBody().email());
	}

	@Test
	void activateDelegatesToService() {
		UsersService service = mock(UsersService.class);
		UsersController controller = new UsersController(service);
		User user = new User("user@example.com", "hash");
		user.activate();
		UUID id = UUID.randomUUID();
		when(service.activate(id)).thenReturn(user);

		var response = controller.activate(id);

		assertEquals(200, response.getStatusCode().value());
		assertEquals(true, response.getBody().active());
	}

	@Test
	void assignRolesReturnsUpdatedUser() {
		UsersService service = mock(UsersService.class);
		UsersController controller = new UsersController(service);
		User user = new User("user@example.com", "hash");
		user.getRoles().add(new Role("ADMIN"));
		UUID id = UUID.randomUUID();
		when(service.assignRoles(id, List.of("ADMIN"))).thenReturn(user);

		var response = controller.assignRoles(id, new AssignRolesRequest(List.of("ADMIN")));

		assertEquals("user@example.com", response.getBody().email());
		assertEquals(1, response.getBody().roles().size());
	}

	@Test
	void listReturnsPage() {
		UsersService service = mock(UsersService.class);
		UsersController controller = new UsersController(service);
		User user = new User("user@example.com", "hash");
		var page = new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);
		when(service.findAll(PageRequest.of(0, 10))).thenReturn(page);

		PageResponse<UserResponse> response = controller.list(PageRequest.of(0, 10)).getBody();

		assertEquals(1, response.totalElements());
		assertEquals(1, response.content().size());
	}

	@Test
	void getByIdReturnsUser() {
		UsersService service = mock(UsersService.class);
		UsersController controller = new UsersController(service);
		User user = new User("user@example.com", "hash");
		UUID id = UUID.randomUUID();
		when(service.findById(id)).thenReturn(Optional.of(user));

		var response = controller.getById(id);

		assertEquals("user@example.com", response.getBody().email());
	}
}
