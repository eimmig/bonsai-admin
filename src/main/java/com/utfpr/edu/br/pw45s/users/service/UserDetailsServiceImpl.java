package com.utfpr.edu.br.pw45s.users.service;

import com.utfpr.edu.br.pw45s.shared.security.UserPrincipal;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = userRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		var authorities = user.getRoles().stream()
			.map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
			.toList();
		return new UserPrincipal(user.getEmail(), user.getPasswordHash(), user.isActive(), authorities);
	}
}