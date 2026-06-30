package com.utfpr.edu.br.pw45s.shared.config;

import com.utfpr.edu.br.pw45s.attachments.dto.AttachmentMapper;
import com.utfpr.edu.br.pw45s.orders.dto.OrderMapper;
import com.utfpr.edu.br.pw45s.users.dto.UserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

	@Bean
	public UserMapper userMapper() {
		return new UserMapper();
	}

	@Bean
	public OrderMapper orderMapper() {
		return new OrderMapper();
	}

	@Bean
	public AttachmentMapper attachmentMapper() {
		return new AttachmentMapper();
	}
}
