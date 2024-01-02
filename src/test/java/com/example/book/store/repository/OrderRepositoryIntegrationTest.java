package com.example.book.store.repository;

import com.example.book.store.model.Order;
import com.example.book.store.model.OrderItem;
import com.example.book.store.repository.book.BookRepository;
import com.example.book.store.repository.order.OrderRepository;
import com.example.book.store.repository.user.UserRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.test.web.servlet.MockMvc;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryIntegrationTest {
    protected static MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

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
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    void findAllByUserEmail_ShouldReturnOrdersForUser() {
        // Given
        String userEmail = "test@user.com";
        Order order1 = createOrder(userEmail);
        Order order2 = createOrder(userEmail);
        Order order3 = createOrder("test2@user.com");
        orderRepository.saveAll(Arrays.asList(order1, order2, order3));

        // When
        List<Order> orders = orderRepository.findAllByUserEmail(userEmail);

        // Then
        Assertions.assertEquals(2, orders.size());
        Assertions.assertTrue(orders.stream()
                                  .allMatch(order -> userEmail.equals(order.getUser().getEmail())));
    }

    @Test
    void findByIdAndUserEmail_ShouldReturnOrderForUser() {
        // Given
        String userEmail = "test@user.com";
        Order order = createOrder(userEmail);
        orderRepository.save(order);

        // When
        Optional<Order> foundOrder = orderRepository.findByIdAndUserEmail(order.getId(), userEmail);

        // Then
        Assertions.assertTrue(foundOrder.isPresent());
        Assertions.assertEquals(userEmail, foundOrder.get().getUser().getEmail());
    }

    private Order createOrder(String userEmail) {
        Order order = new Order();
        OrderItem orderItem = new OrderItem()
                                  .setPrice(BigDecimal.valueOf(10.00))
                                  .setQuantity(3)
                                  .setOrder(order)
                                  .setBook(bookRepository.findById(1L).get());

        return order
                   .setShippingAddress("Address")
                   .setOrderDate(LocalDateTime.now())
                   .setOrderItems(new HashSet<>(Collections.singleton(orderItem)))
                   .setTotal(BigDecimal.valueOf(22.00))
                   .setUser(userRepository.findByEmail(userEmail).get())
                   .setShippingAddress("Address");
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
        }
    }
}
