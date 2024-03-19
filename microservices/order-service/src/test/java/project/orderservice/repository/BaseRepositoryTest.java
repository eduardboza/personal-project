package project.orderservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class BaseRepositoryTest {
  @Autowired protected OrderRepository orderRepository;
  @Autowired protected OrderItemRepository orderItemRepository;
  @Autowired protected ProductRepository productRepository;
  @Autowired protected DeliveryAddressRepository deliveryAddressRepository;

  static PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

  static {
    postgreSQLContainer.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }

  @BeforeAll
  static void connectionEstablished() {
    assertThat(postgreSQLContainer.isCreated()).isTrue();
    assertThat(postgreSQLContainer.isRunning()).isTrue();
  }

  @BeforeEach
  void setUp() {
    deliveryAddressRepository.deleteAll();
    orderRepository.deleteAll();
    orderItemRepository.deleteAll();
    productRepository.deleteAll();
  }
}
