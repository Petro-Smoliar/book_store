package com.example.book.store.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.book.store.dto.cartitem.CartItemDto;
import com.example.book.store.dto.cartitem.CartItemRequestDto;
import com.example.book.store.dto.cartitem.CartItemRequestUpdateDto;
import com.example.book.store.dto.shoppingcart.ShoppingCartDto;
import com.example.book.store.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerIntegrationTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                      .webAppContextSetup(webApplicationContext)
                      .apply(springSecurity())
                      .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource(
                        "database/sql/add/add_shopping_cart_to_shopping_carts_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/add_cartitem_to_cartitems_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/add_user_to_users_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/add/add_books_to_books_table.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @Test
    @DisplayName("Should return user shopping cart in JSON format")
    @WithMockUser(username = "test@user.com",
                  roles = "USER")
    public void shouldReturnUserShoppingCartJson() throws Exception {
        //Given
        User user = new User().setId(1L).setEmail("test@user.com");
        CartItemDto cartItem1 = new CartItemDto().setId(1L);
        CartItemDto cartItem2 = new CartItemDto().setId(2L);
        Set<CartItemDto> cartItems = new HashSet<>();
        cartItems.add(cartItem1);
        cartItems.add(cartItem2);
        ShoppingCartDto expected = new ShoppingCartDto()
                                        .setId(1L)
                                        .setUserId(1L)
                                        .setCartItems(cartItems);
        //When
        MvcResult result =
                mockMvc.perform(
                        get("/cart")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        //When
        Assertions.assertEquals(expected.getCartItems().size(), actual.getCartItems().size());
        EqualsBuilder.reflectionEquals(expected, actual, "cartItems");
    }

    @Test
    @DisplayName("Should add a new cart item with valid request "
                     + "and receive the created CartItemDto")
    @WithMockUser(username = "test@user.com", roles = "USER")
    @Sql(scripts = "classpath:database/sql/remove/remove_cart_item_where_book_id_4.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldAddCartItemWithValidRequestAndReceiveCartItemDto() throws Exception {
        //Given
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(4L, 2L);
        String request = objectMapper.writeValueAsString(cartItemRequestDto);
        CartItemDto expected = new CartItemDto().setBookId(4L).setQuantity(2L);
        //When
        MvcResult result =
                mockMvc.perform(
                    post("/cart")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        CartItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CartItemDto.class);
        //Then
        Assertions.assertEquals(expected.getBookId(), actual.getBookId());
        Assertions.assertEquals(expected.getQuantity(), actual.getQuantity());
    }

    @Test
    @DisplayName("Should update a cart item with valid request and receive the updated CartItemDto")
    @WithMockUser(username = "test@user.com", roles = "USER")
    @Sql(scripts = "classpath:database/sql/update/update_cart_item_id_1.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldUpdateCartItemWithValidRequestAndReceiveUpdatedCartItemDto()
            throws Exception {
        //Given
        CartItemRequestUpdateDto cartItemRequestUpdateDto =
                new CartItemRequestUpdateDto(3);
        String request = objectMapper.writeValueAsString(cartItemRequestUpdateDto);
        CartItemDto expected = new CartItemDto().setBookId(1L).setId(1L).setQuantity(1L);
        //When
        MvcResult result =
                mockMvc.perform(
                    put("/cart/cart-items/{cartItemId}", 1L)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        CartItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CartItemDto.class);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("Should delete a cart item with valid request and return status 204 (No Content)")
    @WithMockUser(username = "test@user.com", roles = "USER")
    @Sql(scripts = "classpath:database/sql/add/add_cart_item_id_1.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldDeleteCartItemWithValidRequestAndReturnNoContent() throws Exception {
        MvcResult result =
                mockMvc.perform(
                    delete("/cart/cart-items/{cartItemId}", 1L))
                    .andExpect(status().isOk())
                    .andReturn();
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource("database/sql/remove/remove_all_from_cart_item_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource(
                        "database/sql/remove/remove_all_from_shopping_cart_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource(
                        "database/sql/remove/remove_all_from_users_table.sql"));
            ScriptUtils.executeSqlScript(connection,
                new ClassPathResource(
                    "database/sql/remove/remove_books_to_books_table.sql"));
        }
    }
}
