package com.harvesthub.backend.controller;

import com.harvesthub.backend.dto.payment.PaymentRequest;
import com.harvesthub.backend.dto.payment.PaymentResponse;
import com.harvesthub.backend.entity.User;
import com.harvesthub.backend.repository.UserRepository;
import com.harvesthub.backend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public PaymentController(PaymentService paymentService, UserRepository userRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> createPayment(@PathVariable Long orderId, @Valid @RequestBody PaymentRequest request) {
        Long userId = getCurrentUserId();
        PaymentResponse response = paymentService.createPayment(userId, orderId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        PaymentResponse response = paymentService.getPaymentByOrderId(userId, orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentResponse>> getMyPayments() {
        Long userId = getCurrentUserId();
        List<PaymentResponse> response = paymentService.getMyPayments(userId);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.harvesthub.backend.exception.ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
