package com.example.book.store.repository.shoppingcart;

import com.example.book.store.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Long> {
    @EntityGraph(attributePaths = "user")
    Optional<ShoppingCart> findByUserEmail(String username);
}
