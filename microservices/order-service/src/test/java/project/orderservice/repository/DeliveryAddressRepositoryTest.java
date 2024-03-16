package project.orderservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import project.orderservice.model.DeliveryAddress;
import project.orderservice.model.Order;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DeliveryAddressRepositoryTest {
  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

  @Autowired private OrderRepository orderRepository;
  @Autowired private OrderItemRepository orderItemRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private DeliveryAddressRepository deliveryAddressRepository;

  @Test
  void connectionEstablished() {
    assertThat(postgreSQLContainer.isCreated()).isTrue();
    assertThat(postgreSQLContainer.isRunning()).isTrue();
  }

  @Test
  @DisplayName(
      """
       The test is successful because the DeliveryAddress has the correct Order in its orderList
       """)
  void should_DeliveryAddressHaveTheCorrectOrder_WhenEntitiesAreSaved() {
    // Create and save DeliveryAddress
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();
    deliveryAddressRepository.save(deliveryAddress);

    // Create and save Order, associating it with DeliveryAddress
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();
    orderRepository.save(order);

    deliveryAddress.addOrder(order);

    DeliveryAddress savedDeliveryAddress =
        deliveryAddressRepository
            .findById(deliveryAddress.getId())
            .orElseThrow(() -> new RuntimeException("Delivery Address not found"));
    assertTrue(savedDeliveryAddress.getOrderList().contains(order));
  }
}
