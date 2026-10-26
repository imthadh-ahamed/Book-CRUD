package com.bookCRUD.backend;

import com.bookCRUD.backend.domain.port.out.BookPersistencePort;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class BackendApplicationTests {

	// No real persistence adapter exists yet (Phase 4). Mock the port so
	// Spring can wire BookService without a concrete implementation bean.
	@MockitoBean
	BookPersistencePort persistencePort;

	@Test
	void contextLoads() {
	}

}
