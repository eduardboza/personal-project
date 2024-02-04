package project.orderservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "price")
  private BigDecimal price;

  @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, orphanRemoval = true)
  private List<OrderItem> orderItemList = new ArrayList<>();

  public void addOrderItem(OrderItem orderItem) {
    orderItemList.add(orderItem);
    orderItem.setProduct(this);
  }

  public void removeOrderItem(OrderItem orderItem) {
    getOrderItemList().remove(orderItem);
    orderItem.setProduct(null);
  }
}
