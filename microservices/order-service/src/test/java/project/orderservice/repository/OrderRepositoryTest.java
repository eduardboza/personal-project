package project.orderservice.repository;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.orderservice.model.DeliveryAddress;
import project.orderservice.model.Order;
import project.orderservice.model.OrderItem;
import project.orderservice.model.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest extends BaseRepositoryTest {
  @Test
  @DisplayName(
      """
The test is successful because Order entity has all mandatory fields filled in
""")
  void should_saveOrder() {
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();

    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();

    Order saveOrder = orderRepository.save(order);
    assertNotNull(order.getId());
  }

  @Test
  @DisplayName(
      """
The test is unsuccessful because Order entity has not all mandatory fields filled in
""")
  void should_notSaveOrder_becauseMandatoryFieldsAreNotFilledIn() {
    // Create and save Order
    Order order = Order.builder().build();
    // Use assertThrows to check for ConstraintViolationException
    ConstraintViolationException exception =
        assertThrows(
            ConstraintViolationException.class,
            () ->
              orderRepository.save(order)
            );

    // Assert that the exception message contains the expected message
    String expectedMessage = "interpolatedMessage='must not be null', propertyPath=deliveryAddress";
    String expectedMessage2 = "interpolatedMessage='must not be null', propertyPath=orderItemList";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
    assertTrue(actualMessage.contains(expectedMessage2));
  }

  @Test
  @DisplayName(
      """
The test is successful because Order has the correct DeliveryAddress saved in db and is successfully associated with
""")
  void should_orderHaveCorrectDeliveryAddress_whenSaveOrderAndDeliveryAddressAtTheSameTime() {
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();

    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();

    Order saveOrder = orderRepository.save(order);

    deliveryAddress.addOrder(order);

    DeliveryAddress savedDeliveryAddress =
        deliveryAddressRepository
            .findById(deliveryAddress.getId())
            .orElseThrow(() -> new RuntimeException("DeliveryAddress not found"));
    assertTrue(savedDeliveryAddress.getOrderList().contains(order));
  }

  @Test
  @DisplayName(
      """
     The test is successful because the Order has the correct OrderItem in its orderItemList
     """)
  void should_OrderHaveTheCorrectOrderItem_WhenEntitiesAreSaved() {
    Product product = Product.builder().name("Adidas 13").price(BigDecimal.valueOf(300.3D)).build();
    productRepository.save(product);
    // Create, save OrderItem
    OrderItem orderItem =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();
    orderItemRepository.save(orderItem);

    // Create and save DeliveryAddress
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();

    // Create and save Order, associating it with DeliveryAddress
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();
    orderRepository.save(order);

    order.addOrderItem(orderItem);

    Order savedOrder =
        orderRepository
            .findById(order.getId())
            .orElseThrow(() -> new RuntimeException("Order not found"));
    assertTrue(savedOrder.getOrderItemList().contains(orderItem));
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
            () ->
              orderRepository.save(order)
            );

    // Assert that the exception message contains the expected message
    String expectedMessage = "interpolatedMessage='must not be null', propertyPath=deliveryAddress";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @DisplayName(
      """
The test is successful because the Order entity has its FK properly set, because deliveryAddress already exists in the database.
""")
  void should_saveOrder_WhenFksIsSet() {
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
