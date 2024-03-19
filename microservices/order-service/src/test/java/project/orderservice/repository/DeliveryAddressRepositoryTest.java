package project.orderservice.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.orderservice.model.DeliveryAddress;
import project.orderservice.model.Order;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DeliveryAddressRepositoryTest extends BaseRepositoryTest {
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

  //    @Test
  //  @DisplayName("""
  //  The test is successful because DeliveryAddress is deleted and the Order associated to it is
  // not deleted
  // """)
  //    void
  // should_deleteDeliveryAddress_andNotDeleteOrder_whenDeliveryAddressIsAssociatedWithOrder() {
  ////      Product product = Product.builder().name("Adidas
  // 13").price(BigDecimal.valueOf(300.3D)).build();
  ////      productRepository.save(product);
  ////      OrderItem orderItem =
  ////              OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();
  ////      orderItemRepository.save(orderItem);
  //
  //      DeliveryAddress deliveryAddress =
  //              DeliveryAddress.builder().street("Armeana 1").orderList(new
  // ArrayList<>()).build();
  //    Order order =
  //        Order.builder().deliveryAddress(deliveryAddress).orderItemList(new HashSet<>()).build();
  //      deliveryAddress.addOrder(order);
  //      orderRepository.save(order);
  //
  //      // Assert that the Order and DeliveryAddress are saved
  //      assertTrue(orderRepository.existsById(order.getId()));
  //      assertTrue(deliveryAddressRepository.existsById(deliveryAddress.getId()));
  //
  //      // Delete the DeliveryAddress
  //      deliveryAddressRepository.deleteById(deliveryAddress.getId());
  //
  //      // Assert that the DeliveryAddress is deleted
  //      assertFalse(deliveryAddressRepository.existsById(deliveryAddress.getId()));
  //
  //      // Assert that the Order is NOT deleted
  //      assertTrue(orderRepository.existsById(order.getId()));
  //    }
}
