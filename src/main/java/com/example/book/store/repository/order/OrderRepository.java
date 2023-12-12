package com.example.book.store.repository.order;

import com.example.book.store.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserEmail(String email);

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findByIdAndUserEmail(Long orderId, String email);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :orderId")
    Optional<Order> updateOrderStatus(@Param("orderId") Long orderId,
                               @Param("status") Order.Status status);
}
