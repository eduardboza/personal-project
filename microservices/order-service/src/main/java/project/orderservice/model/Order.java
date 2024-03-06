package project.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "delivery_address_id", nullable = false)
  private DeliveryAddress deliveryAddress;
  @Builder.Default
  @JsonIgnore
  @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, orphanRemoval = true)
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
