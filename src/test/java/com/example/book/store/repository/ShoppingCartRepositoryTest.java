package com.example.book.store.repository;

import com.example.book.store.model.ShoppingCart;
import com.example.book.store.repository.shoppingcart.ShoppingCartRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource
    ) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource(
                    "database/sql/add/add_books_to_books_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/add_user_to_users_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/"
                                          + "add_shopping_cart_to_shopping_carts_table.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    void findByUserEmail_ShouldReturnShoppingCartForUser() {
        // Given
        String userEmail = "test@user.com";

        // When
        Optional<ShoppingCart> foundShoppingCart =
                shoppingCartRepository.findByUserEmail(userEmail);

        // Then
        Assertions.assertTrue(foundShoppingCart.isPresent());
        Assertions.assertEquals(userEmail, foundShoppingCart.get().getUser().getEmail());
    }

    @Test
    void findByUserEmail_ShouldNotReturnShoppingCartForOtherUser() {
        // Given
        String userEmail = "test@example.com";

        // When
        Optional<ShoppingCart> foundShoppingCart =
                shoppingCartRepository.findByUserEmail(userEmail);

        // Then
        Assertions.assertTrue(foundShoppingCart.isEmpty());
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/remove/remove_all_from_users_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource(
                    "database/sql/remove/remove_books_to_books_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource(
                    "database/sql/remove/remove_all_from_shopping_cart_table.sql"));
        }
    }
}
