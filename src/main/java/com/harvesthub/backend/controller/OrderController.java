package com.harvesthub.backend.controller;

import com.harvesthub.backend.dto.order.OrderRequest;
import com.harvesthub.backend.dto.order.OrderResponse;
import com.harvesthub.backend.entity.OrderStatus;
import com.harvesthub.backend.entity.User;
import com.harvesthub.backend.repository.UserRepository;
import com.harvesthub.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        Long userId = getCurrentUserId();
        OrderResponse response = orderService.createOrder(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        Long userId = getCurrentUserId();
        List<OrderResponse> response = orderService.getMyOrders(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        OrderResponse response = orderService.getOrderById(userId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        OrderResponse response = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        OrderResponse response = orderService.cancelOrder(userId, id);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.harvesthub.backend.exception.ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
