package com.utfpr.edu.br.pw45s.users.service;

import com.utfpr.edu.br.pw45s.shared.base.service.BaseCrudServiceImpl;
import com.utfpr.edu.br.pw45s.users.entity.RoleName;
import com.utfpr.edu.br.pw45s.users.entity.User;
import com.utfpr.edu.br.pw45s.users.repository.RoleRepository;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UsersService extends BaseCrudServiceImpl<User, UUID, UserRepository> {
	private static final Logger log = LoggerFactory.getLogger(UsersService.class);

	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;

	public UsersService(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		super(repository);
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
	}

	@Transactional
	public User register(String email, String rawPassword) {
		if (repository.existsByEmail(email)) {
			log.warn("Registration rejected — email already in use: {}", email);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
		}
		String hash = passwordEncoder.encode(rawPassword);
		User user = new User(email, hash);
		User saved = repository.save(user);
		log.info("User registered: {}", email);
		return saved;
	}

	@Transactional
	public User activate(UUID id) {
		User user = getByIdOrThrow(id);
		user.activate();
		User saved = repository.save(user);
		log.info("User activated: {}", id);
		return saved;
	}

	@Transactional
	public User assignRoles(UUID id, List<String> roles) {
		User user = getByIdOrThrow(id);
		var roleEntities = roles.stream()
			.map(this::validateRole)
			.map(roleName -> roleRepository.findByName(roleName.name())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")))
			.toList();

		user.getRoles().clear();
		user.getRoles().addAll(roleEntities);
		User saved = repository.save(user);
		log.info("Roles {} assigned to user {}", roles, id);
		return saved;
	}

	public Page<User> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	private User getByIdOrThrow(UUID id) {
		return repository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}

	private RoleName validateRole(String role) {
		try {
			return RoleName.valueOf(role);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + role);
		}
	}
}