package project.orderservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_address")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DeliveryAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_address_id")
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    @Column(name = "postal_code")
    private String postal_code;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "deliveryAddress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orderList = new ArrayList<>();


    public void addOrder(Order order) {
        orderList.add(order);
        order.setDeliveryAddress(this);
    }


    public void removeOrder(Order order){
        orderList.remove(order);
        order.setDeliveryAddress(null);
    }

}
