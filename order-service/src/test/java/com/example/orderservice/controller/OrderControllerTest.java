package com.example.orderservice.controller;

import com.example.orderservice.dto.UserDTO;
import com.example.orderservice.entity.Order;
import com.example.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private OrderService orderService;

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
    void getAllOrders_ShouldReturnAllOrdersAnd200Status() throws Exception {
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order1.getId()))
                .andExpect(jsonPath("$[0].status").value(order1.getStatus()))
                .andExpect(jsonPath("$[1].id").value(order2.getId()))
                .andExpect(jsonPath("$[1].status").value(order2.getStatus()));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getOrderById_ShouldReturnOrderByIdAnd200Status() throws Exception {
        when(orderService.getOrderById(order1.getId())).thenReturn(order1);

        mockMvc.perform(get("/api/orders/{id}", order1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order1.getId()))
                .andExpect(jsonPath("$.status").value(order1.getStatus()));

        verify(orderService, times(1)).getOrderById(order1.getId());
    }

    @Test
    void createOrder_ShouldCreateOrderAndReturn201Status() throws Exception {
        doNothing().when(orderService).createOrder(order1);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(order1.getId()));

        verify(orderService, times(1)).createOrder(order1);
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedStatusAndReturn200Status() throws Exception {
        order1.setStatus("UPDATED");
        when(orderService.updateOrderStatus(order1.getId(), "UPDATED")).thenReturn(order1);
        when(orderService.getOrderById(order1.getId())).thenReturn(order1);

        mockMvc.perform(patch("/api/orders/{id}/status", order1.getId())
                        .param("status", "UPDATED").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order1.getId()))
                .andExpect(jsonPath("$.status").value("UPDATED"));

        verify(orderService, times(1)).updateOrderStatus(order1.getId(), "UPDATED");
    }

    @Test
    void deleteOrder_ShouldDeleteOrderAndReturn200Status() throws Exception {
        mockMvc.perform(delete("/api/orders/{id}", order1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(orderService, times(1)).deleteOrderById(order1.getId());
    }
}