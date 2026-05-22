package com.utfpr.edu.br.pw45s.shared.base;

import com.utfpr.edu.br.pw45s.shared.base.controller.BaseCrudController;
import com.utfpr.edu.br.pw45s.shared.base.dto.PageResponse;
import com.utfpr.edu.br.pw45s.shared.base.mapper.BaseMapper;
import com.utfpr.edu.br.pw45s.shared.base.service.BaseCrudService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseCrudControllerTest {

	@Test
	void getByIdReturnsDto() {
		DemoService service = new DemoService();
		DemoMapper mapper = new DemoMapper();
		DemoController controller = new DemoController(service, mapper);
		UUID id = UUID.randomUUID();
		service.entity = new DemoEntity(id, "name");

		var response = controller.getById(id);

		assertEquals("name", response.getBody().name());
	}

	@Test
	void listReturnsPageResponse() {
		DemoService service = new DemoService();
		DemoMapper mapper = new DemoMapper();
		DemoController controller = new DemoController(service, mapper);
		service.entity = new DemoEntity(UUID.randomUUID(), "name");

		PageResponse<DemoDto> response = controller.list(PageRequest.of(0, 10)).getBody();

		assertEquals(1, response.totalElements());
		assertEquals(1, response.content().size());
	}

	private static class DemoEntity {
		private final UUID id;
		private final String name;

		private DemoEntity(UUID id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	private record DemoDto(UUID id, String name) {
	}

	private static class DemoService implements BaseCrudService<DemoEntity, UUID> {
		private DemoEntity entity;

		@Override
		public DemoEntity save(DemoEntity entity) {
			this.entity = entity;
			return entity;
		}

		@Override
		public Optional<DemoEntity> findById(UUID id) {
			return Optional.ofNullable(entity);
		}

		@Override
		public org.springframework.data.domain.Page<DemoEntity> findAll(org.springframework.data.domain.Pageable pageable) {
			return new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);
		}

		@Override
		public void deleteById(UUID id) {
		}
	}

	private static class DemoMapper implements BaseMapper<DemoEntity, DemoDto> {
		@Override
		public DemoDto toDto(DemoEntity entity) {
			return new DemoDto(entity.id, entity.name);
		}

		@Override
		public DemoEntity toEntity(DemoDto dto) {
			return new DemoEntity(dto.id(), dto.name());
		}
	}

	private static class DemoController extends BaseCrudController<DemoEntity, DemoDto, UUID> {
		protected DemoController(BaseCrudService<DemoEntity, UUID> service, BaseMapper<DemoEntity, DemoDto> mapper) {
			super(service, mapper);
		}
	}
}
