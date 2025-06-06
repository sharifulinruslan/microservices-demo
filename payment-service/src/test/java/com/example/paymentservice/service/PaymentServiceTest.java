package com.example.paymentservice.service;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(
        MockitoExtension.class
)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
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
    void createPayment_ShouldCreatePayment() {
        paymentService.createPayment(payment1);

        verify(paymentRepository, times(1)).save(payment1);
    }

    @Test
    void getAllPayments_ShouldReturnAllPayments() {
        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> result = paymentService.getAllPayments();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(payments);
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void getPaymentById_WhenPaymentExists_ShouldReturnPayment() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment1));

        Payment result = paymentService.getPaymentById(1L);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(payment1);
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void getPaymentById_WhenPaymentDoesNotExist_ShouldReturnNull() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        Payment result = paymentService.getPaymentById(1L);

        assertThat(result).isNull();
        verify(paymentRepository, times(1)).findById(any());
    }

    @Test
    void deletePaymentById_ShouldDeletePayment() {
        paymentService.deletePaymentById(1L);

        verify(paymentRepository, times(1)).deleteById(1L);
    }

    @Test
    void updatePayment_ShouldUpdatePayment() {
        when(paymentRepository.save(payment1)).thenReturn(payment1);

        Payment result = paymentService.updatePayment(payment1);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(payment1);
        verify(paymentRepository, times(1)).save(payment1);
    }
}