package project.orderservice.RepositoryTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import project.orderservice.model.DeliveryAddress;
import project.orderservice.model.Order;
import project.orderservice.model.OrderItem;
import project.orderservice.model.Product;
import project.orderservice.repository.DeliveryAddressRepository;
import project.orderservice.repository.OrderItemRepository;
import project.orderservice.repository.OrderRepository;
import project.orderservice.repository.ProductRepository;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {
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
  @DisplayName("The test is successful because all entities can be saved")
  void should_saveAllEntities() {
    // GIVEN
    // Create and save Product
    Product product =
        Product.builder()
            .name("Adidas Shoes")
            .price(BigDecimal.valueOf(300))
            .orderItemList(new ArrayList<>())
            .build();
    // WHEN
    productRepository.save(product);

    // Create OrderItem and associate it with the Product
    OrderItem orderItem =
        OrderItem.builder().amount(BigDecimal.valueOf(3)).product(product).build();
    // WHEN
    orderItemRepository.save(orderItem);

    // Create and save DeliveryAddress
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();
    // WHEN
    deliveryAddressRepository.save(deliveryAddress);

    // Create and save Order, associating it with DeliveryAddress
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();
    // WHEN
    orderRepository.save(order);

    // THEN
    assertNotNull(product.getId());
    assertNotNull(orderItem.getId());
    assertNotNull(deliveryAddress.getId());
    assertNotNull(order.getId());
  }

  @Test
  @DisplayName("The test is unsuccessful because the Order entity has its FK not properly set")
  void should_failToSaveOrder_WhenFkIsNotSet() {
    // GIVEN
    Product product =
        Product.builder().id(1L).name("Adidas Shoes").price(BigDecimal.valueOf(300)).build();

    DeliveryAddress deliveryAddress = DeliveryAddress.builder().id(1L).street("Armeana 1").build();

    OrderItem orderItem =
        OrderItem.builder().id(1L).amount(BigDecimal.valueOf(3)).product(product).build();

    Order order = Order.builder().id(1L).deliveryAddress(deliveryAddress).build();

    // WHEN .save(order)
    // THEN assertThrows
    // Attempt to save an Order with an invalid foreign key (delivery address id)
    assertThrows(
        DataIntegrityViolationException.class,
        () -> {
          orderRepository.save(order);
        });
  }

  @Test
  @DisplayName("The test is successful because the Order entity has its FK properly set")
  void should_saveOrder_WhenFksIsSet() {
    // GIVEN
    // Create and save Product
    Product product =
        Product.builder()
            .name("Adidas Shoes")
            .price(BigDecimal.valueOf(300))
            .orderItemList(new ArrayList<>())
            .build();
    productRepository.save(product);

    // Create OrderItem and associate it with the Product
    OrderItem orderItem =
        OrderItem.builder().amount(BigDecimal.valueOf(3)).product(product).build();
    orderItemRepository.save(orderItem);
    product.addOrderItem(orderItem); // should I do this or not?

    // Create and save DeliveryAddress
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();
    deliveryAddressRepository.save(deliveryAddress);

    // Create and save Order, associating it with DeliveryAddress
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();

    // WHEN
    orderRepository.save(order);

    // THEN
    assertNotNull(order.getId());
  }

  @Test
  @DisplayName(
      "The test is successful because the Product has the correct OrderItem in its OrderItemList")
  void should_productHaveTheCorrectOrderItem_WhenEntitiesAreSaved() {
    // Create and save Product
    Product product =
        Product.builder()
            .name("Adidas Shoes")
            .price(BigDecimal.valueOf(300))
            .orderItemList(new ArrayList<>())
            .build();
    productRepository.save(product);

    // Create OrderItem and associate it with the Product
    OrderItem orderItem =
        OrderItem.builder().amount(BigDecimal.valueOf(3)).product(product).build();
    orderItemRepository.save(orderItem);

    // Create and save DeliveryAddress
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();
    deliveryAddressRepository.save(deliveryAddress);

    // Create and save Order, associating it with DeliveryAddress
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();
    orderRepository.save(order);

    product.addOrderItem(orderItem); // should I do this or not? seems like a service thing
//        deliveryAddress.addOrder(order);
//        order.addOrderItem(orderItem);

    // Fetch the saved Product from the database
    Product savedProduct =
        productRepository
            .findById(product.getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
    // Check that the Product has the correct OrderItem in its OrderItemList
    assertTrue(savedProduct.getOrderItemList().contains(orderItem));
  }
}
