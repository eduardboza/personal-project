package project.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {}
