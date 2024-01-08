package com.example.book.store.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.book.store.dto.order.OrderDto;
import com.example.book.store.dto.order.OrderItemResponseDto;
import com.example.book.store.dto.order.OrderRequestDto;
import com.example.book.store.exception.EntityNotFoundException;
import com.example.book.store.mapper.OrderMapper;
import com.example.book.store.model.Book;
import com.example.book.store.model.CartItem;
import com.example.book.store.model.Order;
import com.example.book.store.model.OrderItem;
import com.example.book.store.model.ShoppingCart;
import com.example.book.store.model.User;
import com.example.book.store.repository.order.OrderRepository;
import com.example.book.store.repository.shoppingcart.CartItemRepository;
import com.example.book.store.repository.shoppingcart.ShoppingCartRepository;
import com.example.book.store.service.impl.OrderServiceImpl;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("addOrder - Successfully add order")
    void addOrder_SuccessfullyAddOrder_ShouldReturnOrderDto() {
        // Given
        CartItem cartItem = new CartItem();
        User user = new User()
                        .setEmail("user@com")
                        .setPassword("password");
        ShoppingCart shoppingCart = createShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>(List.of(cartItem)));
        OrderDto expected = new OrderDto();
        expected.setId(1L);
        SecurityContextHolder.getContext().setAuthentication(
            new TestingAuthenticationToken(
                    "user@com",
                    "password",
                    "ROLE_USER"
            )
        );
        when(orderMapper.toOrderItem(cartItem))
                .thenReturn(new OrderItem().setPrice(BigDecimal.valueOf(10.00)));
        when(shoppingCartRepository.findByUserEmail("user@com"))
                .thenReturn(Optional.of(shoppingCart));
        Order newOrder = new Order().setId(1L);
        when(orderMapper.toOrderDto(any(Order.class))).thenReturn(expected);
        when(orderRepository.save(any(Order.class))).thenReturn(newOrder);
        OrderRequestDto orderRequestDto = new OrderRequestDto("Address");
        // When
        OrderDto actual = orderService.addOrder(orderRequestDto);
        // Then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderMapper, times(1)).toOrderDto(any(Order.class));
    }

    @Test
    @DisplayName("getAllOrders - Successfully get all orders")
    void getAllOrders_SuccessfullyGetAllOrders_ShouldReturnOrderList() {
        // Given
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(createSecurityContext(authentication));
        List<Order> orders = Arrays.asList(createOrder(), createOrder());
        Mockito.when(orderRepository.findAllByUserEmail(authentication.getName()))
                .thenReturn(orders);
        Mockito.when(orderMapper.toOrderDto(any(Order.class))).thenReturn(new OrderDto());
        // When
        List<OrderDto> actual = orderService.getAllOrders();
        // Then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.size());
        Mockito.verify(orderRepository, Mockito.times(1))
                .findAllByUserEmail(authentication.getName());
    }

    @Test
    @DisplayName("updateOrderStatus - Successfully update order status")
    void updateOrderStatus_SuccessfullyUpdateOrderStatus_ShouldNotThrowException() {
        // Given
        Long orderId = 1L;
        Order.Status status = Order.Status.COMPLETED;
        Order order = new Order()
                          .setId(orderId)
                          .setStatus(Order.Status.DELIVERED);
        Order updateOrder = new Order()
                                .setId(orderId)
                                .setStatus(status);
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(updateOrder);

        // When and Then
        Assertions.assertDoesNotThrow(() -> orderService.updateOrderStatus(orderId, status));
        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
        Mockito.verify(orderRepository, Mockito.times(2)).findById(orderId);
    }

    @Test
    @DisplayName("updateOrderStatus - Order not found, should throw EntityNotFoundException")
    void updateOrderStatus_OrderNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        Long orderId = 1L;
        Order.Status status = Order.Status.COMPLETED;
        Mockito.when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());
        // When and Then
        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> orderService.updateOrderStatus(orderId, status)
        );
        Assertions.assertEquals("Not found order by id: " + orderId, exception.getMessage());
        Mockito.verify(orderRepository, Mockito.times(1))
                .findById(orderId);
    }

    @Test
    @DisplayName("getListOrderItem - Successfully get list of order items")
    void getListOrderItem_SuccessfullyGetListOrderItem_ShouldReturnOrderItemResponseDtoList() {
        // Given
        Long orderId = 1L;
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        Order order = new Order();
        order.setId(orderId);
        order.setOrderItems(new HashSet<>(Arrays.asList(orderItem1, orderItem2)));
        Mockito.when(orderRepository.findByIdAndUserEmail(orderId, "user@example.com"))
                .thenReturn(Optional.of(order));
        Mockito.when(orderMapper.toOrderItemDto(any(OrderItem.class)))
                .thenReturn(new OrderItemResponseDto());
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(
                    "user@example.com",
                    "password",
                    "ROLE_USER"));
        // When
        List<OrderItemResponseDto> actualOrderItems = orderService.getListOrderItem(orderId);
        // Then
        Assertions.assertNotNull(actualOrderItems);
        Assertions.assertEquals(2, actualOrderItems.size());
        Mockito.verify(orderRepository, Mockito.times(1))
                .findByIdAndUserEmail(orderId, "user@example.com");
        Mockito.verify(orderMapper, Mockito.times(2))
                .toOrderItemDto(any(OrderItem.class));
    }

    @Test
    @DisplayName("getListOrderItem - Order not found, should throw EntityNotFoundException")
    void getListOrderItem_OrderNotFound_ShouldThrowEntityNotFoundException() {
        // Given
        Long orderId = 1L;
        Mockito.when(orderRepository.findByIdAndUserEmail(orderId, "user@example.com"))
                .thenReturn(Optional.empty());
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken(
                    "user@example.com",
                    "password",
                    "ROLE_USER"));

        // When and Then
        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> orderService.getListOrderItem(orderId)
        );
        Assertions.assertEquals("Not found order by id: "
                                    + orderId, exception.getMessage());
        Mockito.verify(orderRepository, Mockito.times(1))
                .findByIdAndUserEmail(orderId, "user@example.com");
        Mockito.verify(orderMapper, Mockito.never()).toOrderItemDto(any(OrderItem.class));
    }

    private ShoppingCart createShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(createUser());
        shoppingCart.setCartItems(createCartItems(shoppingCart));
        return shoppingCart;
    }

    private Set<CartItem> createCartItems(ShoppingCart shoppingCart) {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setPrice(BigDecimal.valueOf(20));

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setPrice(BigDecimal.valueOf(25));

        CartItem cartItem1 = new CartItem();
        cartItem1.setBook(book1);
        cartItem1.setQuantity(2);
        cartItem1.setShoppingCart(shoppingCart);

        CartItem cartItem2 = new CartItem();
        cartItem2.setBook(book2);
        cartItem2.setQuantity(1);
        cartItem2.setShoppingCart(shoppingCart);

        return new HashSet<>(Arrays.asList(cartItem1, cartItem2));
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");
        return user;
    }

    private Order createOrder(ShoppingCart shoppingCart) {
        Order order = new Order();
        order.setId(1L);
        order.setUser(shoppingCart.getUser());
        order.setOrderItems(toSetOrderItem(shoppingCart.getCartItems(), order));
        order.setTotal(getTotal(order.getOrderItems()));
        order.setShippingAddress("Shipping Address");
        return order;
    }

    private Order createOrder() {
        return createOrder(createShoppingCart());
    }

    private Set<OrderItem> toSetOrderItem(Set<CartItem> cartItems, Order order) {
        return cartItems.stream()
                   .map(cartItem -> {
                       OrderItem orderItem = new OrderItem();
                       orderItem.setOrder(order);
                       orderItem.setBook(cartItem.getBook());
                       orderItem.setQuantity(cartItem.getQuantity());
                       orderItem.setPrice(cartItem.getBook().getPrice());
                       return orderItem;
                   })
                   .collect(Collectors.toSet());
    }

    private BigDecimal getTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                   .map(orderItem -> orderItem.getPrice().multiply(
                       BigDecimal.valueOf(orderItem.getQuantity())))
                   .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    private SecurityContext createSecurityContext(Authentication authentication) {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        return context;
    }
}
