package com.bookCRUD.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class BackendApplicationTests {

    // Spring Boot autoconfigures JdbcTemplate from the DataSource. In the test
    // slice the DataSource autoconfiguration is excluded (test application.properties),
    // so we supply a mock JdbcTemplate so that BookRepository can be wired up.
    @MockitoBean
    JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
    }
}
