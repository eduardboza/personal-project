package project.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
  @Builder.Default
  @JsonIgnore
  @OneToMany(mappedBy = "deliveryAddress", fetch = FetchType.LAZY, orphanRemoval = true)
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
// de facut alt branch pe care fac modificarile si dupa ce e ceva gata fac PR si ii dau lui Daniel
// de pus @Column la tot
// de folosit formatter
// de vazut care i treaba cu GenerationType.SEQUENCE si sa vad daca inserez in scripturile de
// flyway. Faza e sa am un sequence. sa vad daca se formeaza automat cu GenerationType.IDENTITY
// de facut relatie ONE Product   TO   MANY OrderItems
// de facut teste pe repository. TDD. mockito, junit5, hamcast?  faker?
