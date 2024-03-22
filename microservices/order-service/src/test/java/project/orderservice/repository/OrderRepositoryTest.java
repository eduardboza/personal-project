package project.orderservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.*;
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
    Product product = Product.builder().name("Adidas13").price(BigDecimal.valueOf(300.3D)).build();
    OrderItem orderItem =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();

    order.addOrderItem(orderItem);
    orderItem.setOrder(order);
    Order saveOrder = orderRepository.save(order);
    assertNotNull(saveOrder.getId());
  }

  @Test
  @DisplayName(
      """
The test is unsuccessful because Order entity has not all mandatory fields filled in
""")
  void should_notSaveOrder_becauseMandatoryFieldsAreNotFilledIn() {
    Order order = Order.builder().build();
    // Use assertThrows to check for ConstraintViolationException
    ConstraintViolationException exception =
        assertThrows(ConstraintViolationException.class, () -> orderRepository.save(order));

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
    Product product = Product.builder().name("Adidas13").price(BigDecimal.valueOf(300.3D)).build();
    OrderItem orderItem =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();

    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();
    order.addOrderItem(orderItem);
    orderItem.setOrder(order);
    orderRepository.save(order);
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
    OrderItem orderItem =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();
    orderItemRepository.save(orderItem);
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();

    order.addOrderItem(orderItem);
    orderItem.setOrder(order);
    orderRepository.save(order);

    Order savedOrder =
        orderRepository
            .findById(order.getId())
            .orElseThrow(() -> new RuntimeException("Order not found"));
    assertTrue(savedOrder.getOrderItemList().contains(orderItem));
  }

  @Test
  @DisplayName(
      """
The test is successful because the Order entity has its FK properly set, because deliveryAddress already exists in the database.
""")
  void should_saveOrder_WhenFksIsSet() {
    Product product = Product.builder().name("Adidas13").price(BigDecimal.valueOf(300.3D)).build();
    OrderItem orderItem =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();
    deliveryAddressRepository.save(deliveryAddress);
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();

    order.addOrderItem(orderItem);
    orderItem.setOrder(order);
    // WHEN
    orderRepository.save(order);
    // THEN
    assertNotNull(order.getId());
  }

  @Test
  @DisplayName(
      """
    The test is successful because Order entity has all mandatory fields filled in
    """)
  void should_persistDeliveryAddress_whenSavingOrder() {
    Product product = Product.builder().name("Adidas13").price(BigDecimal.valueOf(300.3D)).build();
    OrderItem orderItem =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();
    order.addOrderItem(orderItem);
    orderItem.setOrder(order);

    Order saveOrder = orderRepository.save(order);
    assertNotNull(saveOrder.getId());
    assertThat(deliveryAddress.getId()).isEqualTo(saveOrder.getDeliveryAddress().getId());
  }

  @Test
  @DisplayName(
      """
              The test is successful because we have List<Order>orderList in DeliveryAddress with multiple elements and want to delete just order1
              When order 1 is deleted, OrderItem1 is also deleted because of orphanRemoval=true on orderItemList in Order entity
              """)
  void should_deleteOrder1_andOrderItem1_whenMultipleElementsAreInTheListFromDeliveryAddress() {
    Product product = Product.builder().name("Adidas13").price(BigDecimal.valueOf(300.3D)).build();

    OrderItem orderItem1 =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(1)).build();

    OrderItem orderItem2 =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(2)).build();

    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();

    Order order1 = Order.builder().orderItemList(new HashSet<>()).build();
    deliveryAddress.addOrder(order1);
    order1.setDeliveryAddress(deliveryAddress);
    order1.addOrderItem(orderItem1);
    orderItem1.setOrder(order1);

    Order order2 = Order.builder().orderItemList(new HashSet<>()).build();
    deliveryAddress.addOrder(order2);
    order2.setDeliveryAddress(deliveryAddress);
    order2.addOrderItem(orderItem2);
    orderItem2.setOrder(order2);

    productRepository.save(product);
    orderItemRepository.save(orderItem1);
    orderItemRepository.save(orderItem2);
    orderRepository.save(order1);
    orderRepository.save(order2);

    // Assert that the Orders and DeliveryAddress are saved
    assertTrue(orderRepository.existsById(order1.getId()));
    assertTrue(orderRepository.existsById(order2.getId()));
    assertTrue(deliveryAddressRepository.existsById(deliveryAddress.getId()));

    // Delete order1
    try {
      orderRepository.deleteById(order1.getId());
    } catch (Exception e) {
      fail("Failed to delete order1: " + e.getMessage());
    }
    // Remove order1 from deliveryAddress
    deliveryAddress.removeOrder(order1);
    List<DeliveryAddress> updatedDeliveryAddresses = deliveryAddressRepository.findAll();

    // Assert that the order1 and orderItem1 are deleted. OrderItem1 is deleted because of
    // orphanRemoval=true on orderItemList in Order entity
    assertFalse(orderRepository.existsById(order1.getId()));
    assertFalse(orderItemRepository.existsById(orderItem1.getId()));

    // Assert that the order2 and orderItem2 exists
    assertTrue(orderRepository.existsById(order2.getId()));
    assertTrue(orderItemRepository.existsById(orderItem2.getId()));
  }

  @Test
  @DisplayName(
      """
          The test is successful when removing orderItem2 from Set<OrderItem> of 3 items. The remaining list has orderItem1 and orderItem3
          """)
  void should_deleteOrderItem2_whenMultipleElementsAreInOrderItemSet() {
    Product product = Product.builder().name("Adidas13").price(BigDecimal.valueOf(300.3D)).build();

    OrderItem orderItem1 =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(1)).build();

    OrderItem orderItem2 =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(2)).build();

    OrderItem orderItem3 =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();

    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();

    Order order1 = Order.builder().orderItemList(new HashSet<>()).build();
    deliveryAddress.addOrder(order1);
    order1.setDeliveryAddress(deliveryAddress);
    order1.addOrderItem(orderItem1);
    orderItem1.setOrder(order1);
    order1.addOrderItem(orderItem2);
    orderItem2.setOrder(order1);
    order1.addOrderItem(orderItem3);
    orderItem3.setOrder(order1);

    productRepository.save(product);
    orderItemRepository.save(orderItem1);
    orderItemRepository.save(orderItem2);
    orderItemRepository.save(orderItem3);
    orderRepository.save(order1);

    // Delete orderItem2
    try {
      orderItemRepository.deleteById(orderItem2.getId());
    } catch (Exception e) {
      fail("Failed to delete orderItem2: " + e.getMessage());
    }
    // Remove orderItem2 from order1
    order1.removeOrderItem(orderItem2);

    List<OrderItem> updatedOrderItemSet = orderItemRepository.findAll();
    Set<OrderItem> actualOrderItemSet = order1.getOrderItemList();

    List<OrderItem> sortedUpdatedOrderItemSet = new ArrayList<>(updatedOrderItemSet);
    List<OrderItem> sortedActualOrderItemSet = new ArrayList<>(actualOrderItemSet);
    Collections.sort(sortedUpdatedOrderItemSet, Comparator.comparing(OrderItem::getId));
    Collections.sort(sortedActualOrderItemSet, Comparator.comparing(OrderItem::getId));

    Assertions.assertArrayEquals(
        sortedActualOrderItemSet.toArray(), sortedUpdatedOrderItemSet.toArray());
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
