package project.orderservice.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "delivery_address")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "delivery_address_id")
  private Long id;

  @Column(name = "street")
  private String street;

  @Column(name = "city")
  private String city;

  @Column(name = "state")
  private String state;

  @Column(name = "country")
  private String country;

  @Column(name = "postal_code")
  private String postalCode;

  @OneToMany(
      mappedBy = "deliveryAddress",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.PERSIST)
  private List<Order> orderList = new ArrayList<>();

  public void addOrder(Order order) {
    orderList.add(order);
    order.setDeliveryAddress(this);
  }

  public void removeOrder(Order order) {
    orderList.remove(order);
    order.setDeliveryAddress(null);
  }
}
