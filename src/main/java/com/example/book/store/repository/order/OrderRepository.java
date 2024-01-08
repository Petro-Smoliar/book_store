package com.example.book.store.repository.order;

import com.example.book.store.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "orderItems")
    List<Order> findAllByUserEmail(String email);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findByIdAndUserEmail(Long orderId, String email);
}
