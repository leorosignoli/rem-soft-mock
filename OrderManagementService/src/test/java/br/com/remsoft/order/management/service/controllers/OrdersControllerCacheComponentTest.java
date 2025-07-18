package br.com.remsoft.order.management.service.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.remsoft.order.management.service.repositories.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class OrdersControllerCacheComponentTest {

  @Container static PostgreSQLContainer<?> postgres;

  @Container
  static GenericContainer<?> redis =
      new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

  static {
    PostgreSQLContainer<?> container =
        new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");
    postgres = container;
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", redis::getFirstMappedPort);
  }

  @Autowired private MockMvc mockMvc;

  @SpyBean private OrdersRepository ordersRepository;

  @Autowired private CacheManager cacheManager;

  @BeforeEach
  void clearCache() {
    Cache cache = cacheManager.getCache("orders-page");
    if (cache != null) {
      cache.clear();
    }
  }

  @Test
  void getAllOrders_whenCalledMultipleTimes_shouldUseCache() throws Exception {
    final String cacheKey = "0-10-orderDate: DESC";
    Cache cache = cacheManager.getCache("orders-page");
    assertThat(cache.get(cacheKey)).isNull();

    // First call - should hit the database
    mockMvc
        .perform(
            get("/orders")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(ordersRepository, times(1))
        .findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class));
    assertThat(cache.get(cacheKey)).isNotNull();

    // Second call - should be served from cache
    mockMvc
        .perform(
            get("/orders")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(ordersRepository, times(1))
        .findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Pageable.class));
  }
}
