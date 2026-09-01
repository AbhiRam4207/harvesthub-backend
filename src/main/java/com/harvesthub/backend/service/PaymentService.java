package com.harvesthub.backend.service;

import com.harvesthub.backend.dto.payment.PaymentMapper;
import com.harvesthub.backend.dto.payment.PaymentRequest;
import com.harvesthub.backend.dto.payment.PaymentResponse;
import com.harvesthub.backend.entity.Order;
import com.harvesthub.backend.entity.Payment;
import com.harvesthub.backend.entity.PaymentMethod;
import com.harvesthub.backend.entity.PaymentStatus;
import com.harvesthub.backend.exception.ResourceNotFoundException;
import com.harvesthub.backend.repository.OrderRepository;
import com.harvesthub.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentMapper = paymentMapper;
    }

    public PaymentResponse createPayment(Long userId, Long orderId, PaymentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (paymentRepository.existsByOrderId(orderId)) {
            throw new IllegalArgumentException("Payment already exists for this order");
        }

        PaymentStatus paymentStatus = (request.getPaymentMethod() == PaymentMethod.COD)
                ? PaymentStatus.COMPLETED
                : PaymentStatus.PENDING;

        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(order.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(paymentStatus)
                .build();

        paymentRepository.save(payment);
        return paymentMapper.toResponse(payment);
    }

    public PaymentResponse getPaymentByOrderId(Long userId, Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if (!payment.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Payment not found");
        }

        return paymentMapper.toResponse(payment);
    }

    public List<PaymentResponse> getMyPayments(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
