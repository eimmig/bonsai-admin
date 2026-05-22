package com.utfpr.edu.br.pw45s.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

	@Test
	void handleValidationReturnsProblemDetail() throws Exception {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/users");

		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Dummy(), "dummy");
		bindingResult.addError(new FieldError("dummy", "name", "required"));
		Method method = Dummy.class.getDeclaredMethod("setName", String.class);
		MethodParameter parameter = new MethodParameter(method, 0);
		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

		var response = handler.handleValidation(ex, request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("/users", response.getBody().getProperties().get("path"));
	}

	@Test
	void handleConstraintReturnsBadRequest() {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();
		HttpServletRequest request = new MockHttpServletRequest("POST", "/orders");
		ConstraintViolationException ex = new ConstraintViolationException("invalid", Set.of());

		var response = handler.handleConstraint(ex, request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	void handleAccessDeniedReturnsForbidden() {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();
		HttpServletRequest request = new MockHttpServletRequest("GET", "/orders");
		AccessDeniedException ex = new AccessDeniedException("denied");

		var response = handler.handleAccessDenied(ex, request);

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}

	private static class Dummy {
		public void setName(String name) {
		}
	}
}
