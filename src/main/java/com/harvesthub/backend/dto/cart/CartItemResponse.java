package com.harvesthub.backend.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;
    private Long vegetableId;
    private String vegetableName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String imageUrl;
    private LocalDateTime createdAt;

    public CartItemResponse(Long id, Long vegetableId, String vegetableName, BigDecimal price, Integer quantity, String imageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.vegetableId = vegetableId;
        this.vegetableName = vegetableName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }
}
