package br.com.remsoft.order.management.service.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.remsoft.order.management.service.controllers.dtos.response.CustomPageDTO;
import br.com.remsoft.order.management.service.controllers.dtos.response.GetOrderResponseDTO;
import br.com.remsoft.order.management.service.repositories.ManufacturersRepository;
import br.com.remsoft.order.management.service.repositories.OrdersRepository;
import br.com.remsoft.order.management.service.repositories.ProductsRepository;
import br.com.remsoft.order.management.service.repositories.UsersRepository;
import br.com.remsoft.order.management.service.repositories.entities.Manufacturer;
import br.com.remsoft.order.management.service.repositories.entities.Order;
import br.com.remsoft.order.management.service.repositories.entities.OrderItem;
import br.com.remsoft.order.management.service.repositories.entities.Product;
import br.com.remsoft.order.management.service.repositories.entities.User;
import br.com.remsoft.order.management.service.services.OrdersService;
import br.com.remsoft.order.management.service.utils.TestDataBuilder;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrdersControllerComponentTest {

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

  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  @Autowired private EntityManager entityManager;

  @Autowired private OrdersRepository ordersRepository;

  @Autowired private ProductsRepository productsRepository;

  @Autowired private UsersRepository usersRepository;

  @Autowired private ManufacturersRepository manufacturersRepository;

  @Autowired private OrdersService ordersService;

  @Autowired private CacheManager cacheManager;

  private User testUser;
  private Manufacturer testManufacturer;
  private Product testProduct;
  private Order testOrder;

  @BeforeEach
  void setUp() {
    ordersRepository.deleteAll();
    productsRepository.deleteAll();
    usersRepository.deleteAll();
    manufacturersRepository.deleteAll();

    if (cacheManager.getCache("orders") != null) {
      cacheManager.getCache("orders").clear();
    }
    if (cacheManager.getCache("orders-page") != null) {
      cacheManager.getCache("orders-page").clear();
    }

    testUser = TestDataBuilder.createDefaultUser();
    testManufacturer = TestDataBuilder.createDefaultManufacturer();
    testProduct =
        TestDataBuilder.createProduct(
            1L, "Test Product", new BigDecimal("99.99"), testManufacturer);
    testOrder =
        TestDataBuilder.createOrder(1L, OffsetDateTime.now(), new BigDecimal("199.98"), testUser);

    testUser = usersRepository.save(testUser);
    testManufacturer = manufacturersRepository.save(testManufacturer);
    testProduct = productsRepository.save(testProduct);
    testOrder = ordersRepository.save(testOrder);

    OrderItem orderItem = TestDataBuilder.createDefaultOrderItem(testOrder, testProduct);
    testOrder.setOrderItems(Set.of(orderItem));
    testOrder = ordersRepository.save(testOrder);
  }

  @Test
  void getOrderById_ValidId_ReturnsOrder() {
    ResponseEntity<GetOrderResponseDTO> response =
        restTemplate.getForEntity(
            "http://localhost:" + port + "/orders/" + testOrder.getId(), GetOrderResponseDTO.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GetOrderResponseDTO order = response.getBody();
    assertThat(order).isNotNull();
    assertThat(order.id()).isEqualTo(testOrder.getId());
    assertThat(order.totalAmount()).isEqualTo(testOrder.getTotalAmount());
    assertThat(order.user().id()).isEqualTo(testUser.getId());
    assertThat(order.user().name()).isEqualTo(testUser.getName());
    assertThat(order.user().email()).isEqualTo(testUser.getEmail());
    assertThat(order.orderItems()).hasSize(1);
    assertThat(order.orderItems().iterator().next().quantity()).isEqualTo(2);
    assertThat(order.orderItems().iterator().next().product().name())
        .isEqualTo(testProduct.getName());
    assertThat(order.orderItems().iterator().next().product().manufacturerName())
        .isEqualTo(testManufacturer.getName());
  }

  @Test
  void getOrderById_InvalidId_ReturnsNotFound() {
    ResponseEntity<String> response =
        restTemplate.getForEntity("http://localhost:" + port + "/orders/999", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void getAllOrders_DefaultPagination_ReturnsPagedResults() {
    ResponseEntity<CustomPageDTO<GetOrderResponseDTO>> response =
        restTemplate.exchange(
            "http://localhost:" + port + "/orders",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<CustomPageDTO<GetOrderResponseDTO>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    CustomPageDTO<GetOrderResponseDTO> page = response.getBody();
    assertThat(page).isNotNull();
    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getContent().get(0).id()).isEqualTo(testOrder.getId());
    assertThat(page.getPageSize()).isEqualTo(20);
    assertThat(page.getPageNumber()).isZero();
    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getTotalPages()).isEqualTo(1);
  }

  @Test
  void getAllOrders_CustomPagination_ReturnsCorrectPage() {
    ResponseEntity<CustomPageDTO<GetOrderResponseDTO>> response =
        restTemplate.exchange(
            "http://localhost:"
                + port
                + "/orders?page=0&size=5&sortBy=orderDate&sortDirection=desc",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<CustomPageDTO<GetOrderResponseDTO>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    CustomPageDTO<GetOrderResponseDTO> page = response.getBody();
    assertThat(page).isNotNull();
    assertThat(page.getPageSize()).isEqualTo(5);
    assertThat(page.getPageNumber()).isZero();
  }

  @Test
  void getAllOrders_AscendingSort_ReturnsCorrectOrder() {
    Order secondOrder =
        TestDataBuilder.createOrder(
            2L, OffsetDateTime.now().minusDays(1), new BigDecimal("150.00"), testUser);
    ordersRepository.save(secondOrder);

    ResponseEntity<CustomPageDTO<GetOrderResponseDTO>> response =
        restTemplate.exchange(
            "http://localhost:" + port + "/orders?sortBy=orderDate&sortDirection=asc",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<CustomPageDTO<GetOrderResponseDTO>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    CustomPageDTO<GetOrderResponseDTO> page = response.getBody();
    assertThat(page).isNotNull();
    assertThat(page.getContent()).hasSize(2);
    assertThat(page.getContent().get(0).id()).isEqualTo(secondOrder.getId());
    assertThat(page.getContent().get(1).id()).isEqualTo(testOrder.getId());
  }

  @Test
  void getAllOrders_EmptyDatabase_ReturnsEmptyPage() {
    ordersRepository.deleteAll();

    ResponseEntity<CustomPageDTO<GetOrderResponseDTO>> response =
        restTemplate.exchange(
            "http://localhost:" + port + "/orders",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<CustomPageDTO<GetOrderResponseDTO>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    CustomPageDTO<GetOrderResponseDTO> page = response.getBody();
    assertThat(page).isNotNull();
    assertThat(page.getContent()).isEmpty();
    assertThat(page.getTotalElements()).isZero();
    assertThat(page.getTotalPages()).isZero();
  }
}
