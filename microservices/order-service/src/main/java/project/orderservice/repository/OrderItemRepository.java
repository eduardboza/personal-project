package project.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.orderservice.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
