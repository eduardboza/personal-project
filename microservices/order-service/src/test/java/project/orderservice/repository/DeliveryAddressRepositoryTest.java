package project.orderservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.orderservice.model.DeliveryAddress;
import project.orderservice.model.Order;

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

    List<Order> orders = orderRepository.findAll();

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

    DeliveryAddress savedDeliveryAddress =
        deliveryAddressRepository
            .findById(1L)
            .orElseThrow(() -> new RuntimeException("Delivery Address not found"));
  }

  @Test
  @DisplayName(
      """
    The test is successful because DeliveryAddress is deleted and the Order associated to it is deleted
   because we have just 1 order associated to this delivery address and have orphanRemoval = true on orderList and Order is not associated with other entities
   """)
  void should_deleteDeliveryAddress_andDeleteOrder_whenDeliveryAddressIsAssociatedWithOrder() {
    DeliveryAddress deliveryAddress =
        DeliveryAddress.builder().street("Armeana 1").orderList(new ArrayList<>()).build();
    Order order =
        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();
    deliveryAddress.addOrder(order);
    orderRepository.save(order);

    // Assert that the Order and DeliveryAddress are saved
    assertTrue(orderRepository.existsById(order.getId()));
    assertTrue(deliveryAddressRepository.existsById(deliveryAddress.getId()));

    // Delete the DeliveryAddress
    deliveryAddressRepository.deleteById(deliveryAddress.getId());

    // Assert that the DeliveryAddress is deleted
    assertFalse(deliveryAddressRepository.existsById(deliveryAddress.getId()));

    // Assert that the Order is deleted because we have just 1 order associated to this delivery
    // address and have orphanRemoval = true on orderList
    // The orphanRemoval = true attribute ensures that if an Order entity is removed from the
    // orderList, it will also be deleted from the database if it's not associated with any other
    // DeliveryAddress.
    assertFalse(orderRepository.existsById(order.getId()));
  }
}
