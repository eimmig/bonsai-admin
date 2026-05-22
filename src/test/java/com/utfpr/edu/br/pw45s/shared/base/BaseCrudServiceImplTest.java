package com.utfpr.edu.br.pw45s.shared.base;

import com.utfpr.edu.br.pw45s.shared.base.service.BaseCrudServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BaseCrudServiceImplTest {

	@Test
	void delegatesCrudOperations() {
		JpaRepository<DemoEntity, UUID> repository = Mockito.mock(JpaRepository.class);
		BaseCrudServiceImpl<DemoEntity, UUID, JpaRepository<DemoEntity, UUID>> service = new BaseCrudServiceImpl<>(repository) {};
		DemoEntity entity = new DemoEntity();
		UUID id = UUID.randomUUID();

		when(repository.save(entity)).thenReturn(entity);
		when(repository.findById(id)).thenReturn(Optional.of(entity));
		Page<DemoEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);
		when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);

		service.save(entity);
		service.findById(id);
		service.findAll(PageRequest.of(0, 10));
		service.deleteById(id);

		verify(repository).save(entity);
		verify(repository).findById(id);
		verify(repository).findAll(PageRequest.of(0, 10));
		verify(repository).deleteById(id);
	}

	private static class DemoEntity {
	}
}
