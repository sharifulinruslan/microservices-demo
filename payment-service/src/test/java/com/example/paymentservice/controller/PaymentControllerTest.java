package com.example.paymentservice.controller;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private PaymentService paymentService;

    private Payment payment1;
    private Payment payment2;
    private List<Payment> payments;

    @BeforeEach
    void setUp() {
        payment1 = new Payment();
        payment1.setId(1L);
        payment1.setStatus("INIT");
        payment1.setOrderId(1L);
        payment1.setAmount(1.0);
        payment1.setPaymentDate(LocalDateTime.now());
        payment2 = new Payment();
        payment2.setId(2L);
        payment2.setStatus("INIT");
        payment2.setOrderId(2L);
        payment2.setAmount(2.0);
        payment2.setPaymentDate(LocalDateTime.now());
        payments = Arrays.asList(payment1, payment2);
    }

    @Test
    void getAllPayments_ShouldReturnAllPaymentsAndReturn200Status() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(payments);

        mockMvc.perform(get("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(payment1.getId()))
                .andExpect(jsonPath("$[0].status").value(payment1.getStatus()))
                .andExpect(jsonPath("$[0].orderId").value(payment1.getOrderId()))
                .andExpect(jsonPath("$[0].amount").value(payment1.getAmount()))
                .andExpect(jsonPath("$[1].id").value(payment2.getId()))
                .andExpect(jsonPath("$[1].status").value(payment2.getStatus()))
                .andExpect(jsonPath("$[1].orderId").value(payment2.getOrderId()))
                .andExpect(jsonPath("$[1].amount").value(payment2.getAmount()));

        verify(paymentService, times(1)).getAllPayments();
    }

    @Test
    void getPaymentById_ShouldReturnPaymentAnd200Status() throws Exception {
        when(paymentService.getPaymentById(1L)).thenReturn(payment1);

        mockMvc.perform(get("/api/payments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment1.getId()))
                .andExpect(jsonPath("$.status").value(payment1.getStatus()));
        verify(paymentService, times(1)).getPaymentById(1L);
    }

    @Test
    void createPayment_ShouldCreatePaymentAndReturn201Status() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(payment1.getId()))
                .andExpect(jsonPath("$.status").value(payment1.getStatus()));
        verify(paymentService, times(1)).createPayment(payment1);
    }

    @Test
    void updatePayment_ShouldUpdatePaymentAndReturn200Status() throws Exception {
        when(paymentService.updatePayment(payment1)).thenReturn(payment1);

        mockMvc.perform(put("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment1.getId()))
                .andExpect(jsonPath("$.status").value(payment1.getStatus()));

        verify(paymentService, times(1)).updatePayment(payment1);
    }

    @Test
    void deletePayment_ShouldDeletePaymentAndReturn200Status() throws Exception {
        mockMvc.perform(delete("/api/payments/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(paymentService, times(1)).deletePaymentById(any());
    }
}