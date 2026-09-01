package com.harvesthub.backend.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private Integer totalItems;
}
