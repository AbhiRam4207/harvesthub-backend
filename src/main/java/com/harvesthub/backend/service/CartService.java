package com.harvesthub.backend.service;

import com.harvesthub.backend.dto.cart.CartItemRequest;
import com.harvesthub.backend.dto.cart.CartItemResponse;
import com.harvesthub.backend.dto.cart.CartResponse;
import com.harvesthub.backend.entity.CartItem;
import com.harvesthub.backend.entity.Vegetable;
import com.harvesthub.backend.exception.ResourceNotFoundException;
import com.harvesthub.backend.repository.CartItemRepository;
import com.harvesthub.backend.repository.VegetableRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final VegetableRepository vegetableRepository;

    public CartService(CartItemRepository cartItemRepository, VegetableRepository vegetableRepository) {
        this.cartItemRepository = cartItemRepository;
        this.vegetableRepository = vegetableRepository;
    }

    public CartResponse getCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return buildCartResponse(cartItems);
    }

    public CartResponse addToCart(Long userId, CartItemRequest request) {
        Vegetable vegetable = vegetableRepository.findById(request.getVegetableId())
                .orElseThrow(() -> new ResourceNotFoundException("Vegetable not found with id: " + request.getVegetableId()));

        if (!vegetable.getActive()) {
            throw new IllegalArgumentException("Vegetable is not available");
        }

        if (vegetable.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + vegetable.getQuantity());
        }

        CartItem existingItem = cartItemRepository.findByUserIdAndVegetableId(userId, request.getVegetableId())
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > vegetable.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + vegetable.getQuantity() + ", In cart: " + existingItem.getQuantity());
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .userId(userId)
                    .vegetable(vegetable)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(cartItem);
        }

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return buildCartResponse(cartItems);
    }

    public CartResponse updateCartItem(Long userId, Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Cart item not found in your cart");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Vegetable vegetable = cartItem.getVegetable();
        if (vegetable.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + vegetable.getQuantity());
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return buildCartResponse(cartItems);
    }

    public CartResponse removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Cart item not found in your cart");
        }

        cartItemRepository.delete(cartItem);

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        return buildCartResponse(cartItems);
    }

    public CartResponse clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
        return new CartResponse(List.of(), BigDecimal.ZERO, 0);
    }

    private CartResponse buildCartResponse(List<CartItem> cartItems) {
        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(item -> new CartItemResponse(
                        item.getId(),
                        item.getVegetable().getId(),
                        item.getVegetable().getName(),
                        item.getVegetable().getPrice(),
                        item.getQuantity(),
                        item.getVegetable().getImageUrl(),
                        item.getCreatedAt()
                ))
                .collect(Collectors.toList());

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = itemResponses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return new CartResponse(itemResponses, totalAmount, totalItems);
    }
}
