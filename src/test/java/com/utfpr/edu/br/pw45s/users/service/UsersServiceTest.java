package com.utfpr.edu.br.pw45s.users.service;

import com.utfpr.edu.br.pw45s.users.entity.Role;
import com.utfpr.edu.br.pw45s.users.entity.RoleName;
import com.utfpr.edu.br.pw45s.users.entity.User;
import com.utfpr.edu.br.pw45s.users.repository.RoleRepository;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UsersService usersService;

	@Test
	void registerCreatesInactiveUser() {
		when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
		when(passwordEncoder.encode("pass")).thenReturn("hash");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		User saved = usersService.register("user@example.com", "pass");

		assertEquals("user@example.com", saved.getEmail());
		assertFalse(saved.isActive());
	}

	@Test
	void registerRejectsDuplicateEmail() {
		when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> usersService.register("user@example.com", "pass")
		);
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
	}

	@Test
	void activateUpdatesUser() {
		User user = new User("user@example.com", "hash");
		UUID id = UUID.randomUUID();
		when(userRepository.findById(id)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		User updated = usersService.activate(id);

		assertTrue(updated.isActive());
	}

	@Test
	void assignRolesReplacesExistingRoles() {
		User user = new User("user@example.com", "hash");
		UUID id = UUID.randomUUID();
		Role role = new Role("ADMIN");

		when(userRepository.findById(id)).thenReturn(Optional.of(user));
		when(roleRepository.findByName(RoleName.ADMIN.name())).thenReturn(Optional.of(role));
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		User updated = usersService.assignRoles(id, List.of("ADMIN"));

		assertEquals(1, updated.getRoles().size());
	}

	@Test
	void assignRolesRejectsInvalidRole() {
		UUID id = UUID.randomUUID();
		when(userRepository.findById(id)).thenReturn(Optional.of(new User("user@example.com", "hash")));

		ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> usersService.assignRoles(id, List.of("UNKNOWN"))
		);
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
	}
}
