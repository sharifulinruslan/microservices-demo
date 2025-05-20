package com.example.paymentservice.service;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    @Value("${order-service.url}")
    private String orderServiceUrl;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void createPayment(Payment payment) {
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PENDING");
        payment.setStatus("COMPLETED");
        paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public void deletePaymentById(Long id) {
        paymentRepository.deleteById(id);
    }

    public void updatePayment(Payment payment) {
        paymentRepository.save(payment);
    }
}
