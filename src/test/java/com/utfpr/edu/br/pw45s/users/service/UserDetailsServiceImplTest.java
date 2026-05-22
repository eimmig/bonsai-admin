package com.utfpr.edu.br.pw45s.users.service;

import com.utfpr.edu.br.pw45s.users.entity.Role;
import com.utfpr.edu.br.pw45s.users.entity.User;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserDetailsServiceImpl service;

	@Test
	void throwsWhenUserNotFound() {
		when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@example.com"));
	}

	@Test
	void returnsUserDetailsWithAuthorities() {
		User user = new User("user@example.com", "hash");
		user.getRoles().add(new Role("ADMIN"));
		when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

		var details = service.loadUserByUsername("user@example.com");

		assertEquals("user@example.com", details.getUsername());
		assertEquals(1, details.getAuthorities().size());
	}
}
