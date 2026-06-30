package com.utfpr.edu.br.pw45s.shared.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPrincipalTest {

	@Test
	void exposesUserDetails() {
		var principal = new UserPrincipal(
			"user@example.com",
			"hash",
			true,
			List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
		);

		assertEquals("user@example.com", principal.getUsername());
		assertEquals("hash", principal.getPassword());
		assertEquals(1, principal.getAuthorities().size());
		assertTrue(principal.isEnabled());
		assertTrue(principal.isAccountNonExpired());
		assertTrue(principal.isAccountNonLocked());
		assertTrue(principal.isCredentialsNonExpired());
	}

	@Test
	void disabledUserIsNotEnabled() {
		var principal = new UserPrincipal("user@example.com", "hash", false, List.of());
		assertFalse(principal.isEnabled());
	}
}
