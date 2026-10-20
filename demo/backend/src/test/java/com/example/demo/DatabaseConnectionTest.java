package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class DatabaseConnectionTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldConnectToDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(2), "Соединение с БД должно быть валидным");
            System.out.println("✅ Успешное подключение к: " + connection.getMetaData().getURL());
        }
    }
}