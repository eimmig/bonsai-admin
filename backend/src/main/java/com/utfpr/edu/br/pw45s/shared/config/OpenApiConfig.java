package com.utfpr.edu.br.pw45s.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
	info = @Info(
		title = "PW45S — API Administrativa de E-commerce",
		version = "1.0.0",
		description = "API REST para gestão de pedidos, usuários e anexos na camada administrativa."
	),
	security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
	name = "bearerAuth",
	type = SecuritySchemeType.HTTP,
	scheme = "bearer",
	bearerFormat = "JWT"
)
public class OpenApiConfig {
}
