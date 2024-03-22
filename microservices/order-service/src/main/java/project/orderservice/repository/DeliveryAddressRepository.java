package project.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.orderservice.model.DeliveryAddress;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {}
