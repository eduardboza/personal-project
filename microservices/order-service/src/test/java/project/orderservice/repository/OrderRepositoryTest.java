package project.orderservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import project.orderservice.model.DeliveryAddress;
import project.orderservice.model.Order;
import project.orderservice.model.OrderItem;
import project.orderservice.model.Product;

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
    Product product = Product.builder().name("Adidas Shoes").price(BigDecimal.valueOf(300)).build();
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
  @DisplayName(
      """
     The test is unsuccessful because the Order has DeliveryAddress field null, so the FK is not set
     """)
  void should_OrderHaveDeliveryAddressFieldNull_WhenOrderSaved() {
    // Create and save Order
    Order order = Order.builder().orderItemList(new HashSet<>()).build();
    // Use assertThrows to check for ConstraintViolationException
    ConstraintViolationException exception =
        assertThrows(
            ConstraintViolationException.class,
            () -> {
              orderRepository.save(order);
            });

    // Assert that the exception message contains the expected message
    String expectedMessage = "interpolatedMessage='must not be null', propertyPath=deliveryAddress";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @DisplayName(
      """
The test is unsuccessful because the Order entity has its FK not properly set, because the specific deliveryAddress does not exist in the database.
""")
  void should_failToSaveOrder_WhenFkIsNotSet() {
    // GIVEN
    Product product =
        Product.builder().id(1L).name("Adidas Shoes").price(BigDecimal.valueOf(300)).build();

    DeliveryAddress deliveryAddress = DeliveryAddress.builder().id(1L).street("Armeana 1").build();

    OrderItem orderItem =
        OrderItem.builder().id(1L).amount(BigDecimal.valueOf(3)).product(product).build();

    Order order = Order.builder().id(1L).deliveryAddress(deliveryAddress).build();

    DataIntegrityViolationException exception =
        Assertions.assertThrows(
            DataIntegrityViolationException.class,
            () -> {
              orderRepository.save(order);
            });
    String expectedMessage =
        "insert or update on table \"orders\" violates foreign key constraint \"fk_delivery_address\"";
    String actualMessage = exception.getMessage();
    // Assert that the exception message contains the expected message
    assert actualMessage.contains(expectedMessage);
  }

  @Test
  @DisplayName(
      """
The test is successful because the Order entity has its FK properly set, because deliveryAddress exists in the database.
""")
  void should_saveOrder_WhenFksIsSet() {
    // GIVEN
    // Create and save Product
    Product product = Product.builder().name("Adidas Shoes").price(BigDecimal.valueOf(300)).build();
    productRepository.save(product);

    // Create OrderItem
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

    // WHEN
    orderRepository.save(order);

    // THEN
    assertNotNull(order.getId());
  }

  @Test
  @DisplayName(
      """
 The test is successful because the Order has the correct OrderItem in its orderItemList
 """)
  void should_OrderHaveTheCorrectOrderItem_WhenEntitiesAreSaved() {
    // Create and save Product
    Product product = Product.builder().name("Adidas Shoes").price(BigDecimal.valueOf(300)).build();
    productRepository.save(product);

    // Create, save OrderItem and associate it with the Product
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

    order.addOrderItem(orderItem);

    // Fetch the saved Product from the database
    Product savedProduct =
        productRepository
            .findById(product.getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

    Order savedOrder =
        orderRepository
            .findById(order.getId())
            .orElseThrow(() -> new RuntimeException("Order not found"));
    assertTrue(savedOrder.getOrderItemList().contains(orderItem));
  }

  /* tests for @Builder.Default on a field. I do not use it, I wanted to see how it works
   @Column(name = "name")
   @Builder.Default
  private String name = "foo";
  *
  */

  // test is commented because I do not use @Builder.Default
  //  @Test
  //  public void givenBuilderWithDefaultValue_ThanDefaultValueIsPresent() {
  //    Product build = Product.builder().build();
  //    Assertions.assertEquals("foo", build.getName());
  //  }
  @Test
  public void givenBuilderWithoutDefaultValue_ThanSetValueIsNotPresent() {
    Product build = Product.builder().build();
    assertNull(build.getName());
  }

  // default builder usage
  @Test
  public void givenBuilderWithoutDefaultValue_AndSetValueInBuildingProcess_ThanSetValueIsPresent() {
    Product build = Product.builder().name("foo").build();
    Assertions.assertEquals("foo", build.getName());
  }
}
