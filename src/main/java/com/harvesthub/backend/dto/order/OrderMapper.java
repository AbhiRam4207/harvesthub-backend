package com.harvesthub.backend.dto.order;

import com.harvesthub.backend.entity.Order;
import com.harvesthub.backend.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderResponse.OrderItemResponse(
                        item.getId(),
                        item.getVegetableId(),
                        item.getVegetableName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                itemResponses,
                order.getTotalAmount(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
