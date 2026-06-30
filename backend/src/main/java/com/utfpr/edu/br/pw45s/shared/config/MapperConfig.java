package com.utfpr.edu.br.pw45s.shared.config;

import com.utfpr.edu.br.pw45s.attachments.dto.AttachmentMapper;
import com.utfpr.edu.br.pw45s.orders.dto.OrderMapper;
import com.utfpr.edu.br.pw45s.users.dto.UserMapper;
import com.utfpr.edu.br.pw45s.users.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

	@Bean
	public UserMapper userMapper() {
		return new UserMapper();
	}

	@Bean
	public OrderMapper orderMapper(UserRepository userRepository) {
		return new OrderMapper(userRepository);
	}

	@Bean
	public AttachmentMapper attachmentMapper() {
		return new AttachmentMapper();
	}
}
