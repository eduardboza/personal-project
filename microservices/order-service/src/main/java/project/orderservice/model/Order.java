package project.orderservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "delivery_address_id")
  @NotNull
  private DeliveryAddress deliveryAddress;

  @OneToMany(
      mappedBy = "order",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.REMOVE)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<OrderItem> orderItemList = new HashSet<>();

  public void addOrderItem(OrderItem orderItem) {
    orderItemList.add(orderItem);
    orderItem.setOrder(this);
  }

  public void removeOrderItem(OrderItem orderItem) {
    getOrderItemList().remove(orderItem);
    orderItem.setOrder(null);
  }
}
