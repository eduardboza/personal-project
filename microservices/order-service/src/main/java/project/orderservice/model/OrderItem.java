package project.orderservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderItem {

    //Product's PK will also be OrderItem's PK.
    //In OrderItem table, we have Product as FK which is OrderItem's PK
    //Both tables are sharing the same PKs
    //this is good for performance in OneToOne
    //PK and FK columns are most often indexed, so sharing the PK can reduce the index footprint by half, which is desirable since you want to store all your indexes into memory to speed up index scanning.
    @Id
    @Column(name = "order_item_id")
    private Long id;
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "order_item_id")
    // @EqualsAndHashCode.Exclude
    // @ToString.Exclude
    private Product product;

}
