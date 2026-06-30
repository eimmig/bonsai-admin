package com.utfpr.edu.br.pw45s;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.Annotation;

class Pw45sApplicationTests {

	@Test
	void hasSpringBootApplicationAnnotation() {
		Annotation annotation = Pw45sApplication.class.getAnnotation(SpringBootApplication.class);
		org.junit.jupiter.api.Assertions.assertNotNull(annotation);
	}

}
