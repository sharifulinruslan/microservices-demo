package com.example.orderservice.service;

import com.example.orderservice.dto.UserDTO;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(
        MockitoExtension.class
)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderService orderService;
    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private Order order1;
    private Order order2;
    private List<Order> orders;
    private UserDTO user;

    @BeforeEach
    void setUp() {
        order1 = new Order();
        order1.setId(1L);
        order1.setStatus("INIT");
        order1.setProductIds(List.of(11L));
        order1.setUserId(111L);
        order1.setTotalPrice(1111.0);
        order2 = new Order();
        order2.setId(2L);
        order2.setStatus("INIT");
        order2.setProductIds(List.of(22L));
        order2.setUserId(222L);
        order2.setTotalPrice(2222.0);
        orders = List.of(order1, order2);
        user = new UserDTO();
        user.setId(1L);
        user.setName("Test User");
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(orders);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void createOrder_ShouldCreateOrder() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDTO.class)).thenReturn(Mono.just(user));
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
        when(orderRepository.save(any(Order.class))).thenReturn(order1);

        orderService.createOrder(order1);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getOrderById_WhenOrderExist_ShouldReturnOrder() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order1));

        Order result = orderService.getOrderById(order1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(order1);
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    void getOrderById_WhenOrderDoesNotExist_ShouldReturnNull() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        Order result = orderService.getOrderById(order1.getId());

        assertThat(result).isNull();
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateOrderStatus_ShouldUpdateOrderStatus() {
        when(orderRepository.save(any(Order.class))).thenReturn(order1);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order1));

        Order result = orderService.updateOrderStatus(order1.getId(), "CHANGED");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(order1);
        assertThat(order1.getStatus()).isEqualTo("CHANGED");
        verify(orderRepository, times(1)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void deleteOrderById_ShouldDeleteOrder() {
        orderService.deleteOrderById(order1.getId());

        verify(orderRepository, times(1)).deleteById(anyLong());
    }
}