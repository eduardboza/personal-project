package project.orderservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.orderservice.model.DeliveryAddress;
import project.orderservice.model.Order;
import project.orderservice.model.OrderItem;
import project.orderservice.model.Product;

@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DeliveryAddressRepositoryTest extends BaseRepositoryTest {

  @Test
  @DisplayName(
      """
        The test is successful because the DeliveryAddress has the correct Order in its orderList
        """)
  void should_DeliveryAddressHaveTheCorrectOrder_WhenEntitiesAreSaved() {
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder()
            .street("Armeana 1")
            .city("Iasi")
            .state("Europe")
            .country("Ro")
            .postalCode("XYZ")
            .orderList(new ArrayList<>())
            .build();

    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();
    deliveryAddress.addOrder(order);

    Order savedOrder = orderRepository.save(order);
    assertTrue(deliveryAddress.getOrderList().contains(savedOrder));

    DeliveryAddress findByIdDeliveryAddress =
        deliveryAddressRepository
            .findById(deliveryAddress.getId())
            .orElseThrow(() -> new RuntimeException("Delivery Address not found"));
    assertTrue(findByIdDeliveryAddress.getOrderList().contains(savedOrder));
  }

  @Test
  @DisplayName("""
        The test is successful because DeliveryAddress is deleted
        """)
  void should_deleteDeliveryAddress() {
    DeliveryAddress deliveryAddress = DeliveryAddress.builder().street("Armeana 1").build();
    deliveryAddress = deliveryAddressRepository.save(deliveryAddress);
    Long deliveryAddressId = deliveryAddress.getId();

    deliveryAddressRepository.deleteById(deliveryAddressId);

    assertFalse(deliveryAddressRepository.existsById(deliveryAddressId));
  }

  @Test
  void shouldGetAllDeliveryAddresses() {
    List<DeliveryAddress> deliveryAddressList =
        List.of(
            DeliveryAddress.builder().street("Armeana 100").build(),
            DeliveryAddress.builder().street("Armeana 200").build());
    deliveryAddressRepository.saveAll(deliveryAddressList);
    List<DeliveryAddress> addressesReceived = deliveryAddressRepository.findAll();
    assertThat(addressesReceived).hasSize(2);
  }

  @Test
  @DisplayName(
      """
        The test is successful because DeliveryAddress is deleted and the Order associated to it is deleted
       and also the OrderItem associated to the Order is deleted because we have orphanRemoval = true
       """)
  void
      should_deleteDeliveryAddress_andOrder_andOrderItem_whenDeliveryAddressIsAssociatedWithOrder() {
    Product product = Product.builder().name("Adidas13").price(BigDecimal.valueOf(300.3D)).build();

    OrderItem orderItem =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();

    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();

    Order order = Order.builder().orderItemList(new HashSet<>()).build();
    deliveryAddress.addOrder(order);
    order.setDeliveryAddress(deliveryAddress);

    order.addOrderItem(orderItem);
    orderItem.setOrder(order);

    productRepository.save(product);
    orderItemRepository.save(orderItem);
    orderRepository.save(order);

    // Assert that the Order and DeliveryAddress are saved
    assertTrue(orderRepository.existsById(order.getId()));
    assertTrue(deliveryAddressRepository.existsById(deliveryAddress.getId()));

    // Delete the DeliveryAddress
    deliveryAddressRepository.deleteById(deliveryAddress.getId());

    // Assert that the DeliveryAddress is deleted
    assertFalse(deliveryAddressRepository.existsById(deliveryAddress.getId()));

    // Assert that the Order and OrderItem are deleted
    assertFalse(orderRepository.existsById(order.getId()));
    assertFalse(orderItemRepository.existsById(orderItem.getId()));
  }
}
