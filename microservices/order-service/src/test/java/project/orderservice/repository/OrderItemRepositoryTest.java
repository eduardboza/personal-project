package project.orderservice.repository;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.orderservice.model.OrderItem;
import project.orderservice.model.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderItemRepositoryTest extends BaseRepositoryTest {

  @Test
  @DisplayName(
      """
    The test is successful because OrderItem entity has all mandatory fields filled in
    """)
  void should_saveOrderItem() {
    Product product = Product.builder().name("Adidas 13").price(BigDecimal.valueOf(300.3D)).build();
    productRepository.save(product);

    OrderItem orderItem =
        OrderItem.builder().product(product).amount(BigDecimal.valueOf(3)).build();
    orderItemRepository.save(orderItem);

    assertNotNull(orderItem.getId());
  }

  @Test
  @DisplayName(
      """
       The test is unsuccessful because the OrderItem entity has its FK not properly set, because the
       specific Product does not exist in the database.
       """)
  void should_failToSaveOrderItem_WhenFkProductIsNull() {
    OrderItem orderItem = OrderItem.builder().amount(BigDecimal.valueOf(3)).build();

    ConstraintViolationException exception =
        assertThrows(ConstraintViolationException.class, () -> orderItemRepository.save(orderItem));

    String expectedMessage = "interpolatedMessage='must not be null', propertyPath=product";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
}
