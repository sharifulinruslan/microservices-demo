package com.example.orderservice.service;

import com.example.orderservice.dto.UserDTO;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    @Value("${user-service.url}")
    private String userServiceUrl;
    @Value("${inventory-service.url}")
    private String inventoryServiceUrl;


    @Autowired
    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void createOrder(Order order) {
        UserDTO userDTO = webClientBuilder.build().get()
                .uri(userServiceUrl + "/{id}", order.getUserId())
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();

        if (userDTO == null) {
            System.err.println("User not found");
        }

        for (Long productId : order.getProductIds()) {
            Boolean inStock = webClientBuilder.build().get()
                    .uri(inventoryServiceUrl + "/product/{productId}/in-stock", productId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (inStock == null) {
                System.err.println("Product out of stock");
            }
        }

        order.setStatus("CREATED");
        orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public void updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        orderRepository.save(order);
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }
}
