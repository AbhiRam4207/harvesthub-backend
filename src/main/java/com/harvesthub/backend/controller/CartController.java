package com.harvesthub.backend.controller;

import com.harvesthub.backend.dto.cart.CartItemRequest;
import com.harvesthub.backend.dto.cart.CartResponse;
import com.harvesthub.backend.entity.User;
import com.harvesthub.backend.repository.UserRepository;
import com.harvesthub.backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> getCart() {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.addToCart(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable Long cartItemId, @Valid @RequestBody CartItemRequest request) {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.updateCartItem(userId, cartItemId, request.getQuantity());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> removeCartItem(@PathVariable Long cartItemId) {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.removeCartItem(userId, cartItemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> clearCart() {
        Long userId = getCurrentUserId();
        CartResponse response = cartService.clearCart(userId);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.harvesthub.backend.exception.ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
