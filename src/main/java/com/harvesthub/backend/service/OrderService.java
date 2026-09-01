package com.harvesthub.backend.service;

import com.harvesthub.backend.dto.cart.CartItemResponse;
import com.harvesthub.backend.dto.cart.CartResponse;
import com.harvesthub.backend.dto.order.OrderMapper;
import com.harvesthub.backend.dto.order.OrderRequest;
import com.harvesthub.backend.dto.order.OrderResponse;
import com.harvesthub.backend.entity.CartItem;
import com.harvesthub.backend.entity.Inventory;
import com.harvesthub.backend.entity.Order;
import com.harvesthub.backend.entity.OrderItem;
import com.harvesthub.backend.entity.OrderStatus;
import com.harvesthub.backend.entity.Vegetable;
import com.harvesthub.backend.exception.ResourceNotFoundException;
import com.harvesthub.backend.repository.CartItemRepository;
import com.harvesthub.backend.repository.InventoryRepository;
import com.harvesthub.backend.repository.OrderItemRepository;
import com.harvesthub.backend.repository.OrderRepository;
import com.harvesthub.backend.repository.VegetableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final VegetableRepository vegetableRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartItemRepository cartItemRepository, VegetableRepository vegetableRepository, InventoryRepository inventoryRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.vegetableRepository = vegetableRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Vegetable vegetable = cartItem.getVegetable();

            if (!vegetable.getActive()) {
                throw new IllegalArgumentException("Vegetable '" + vegetable.getName() + "' is not available");
            }

            if (vegetable.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for '" + vegetable.getName() + "'. Available: " + vegetable.getQuantity());
            }

            vegetable.setQuantity(vegetable.getQuantity() - cartItem.getQuantity());
            vegetableRepository.save(vegetable);

            Inventory inventory = inventoryRepository.findByVegetableId(vegetable.getId()).orElse(null);
            if (inventory != null) {
                inventory.setSoldQuantity(inventory.getSoldQuantity() + cartItem.getQuantity());
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() - cartItem.getQuantity());
                inventoryRepository.save(inventory);
            }

            BigDecimal itemTotal = vegetable.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .vegetableId(vegetable.getId())
                    .vegetableName(vegetable.getName())
                    .price(vegetable.getPrice())
                    .quantity(cartItem.getQuantity())
                    .totalPrice(itemTotal)
                    .build();

            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .deliveryAddress(request.getDeliveryAddress())
                .build();

        for (OrderItem item : orderItems) {
            order.addItem(item);
        }

        orderRepository.save(order);

        cartItemRepository.deleteByUserId(userId);

        return orderMapper.toResponse(order);
    }

    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found");
        }

        return orderMapper.toResponse(order);
    }

    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(status);
        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Only pending orders can be cancelled");
        }

        for (OrderItem item : order.getItems()) {
            Vegetable vegetable = vegetableRepository.findById(item.getVegetableId())
                    .orElse(null);
            if (vegetable != null) {
                vegetable.setQuantity(vegetable.getQuantity() + item.getQuantity());
                vegetableRepository.save(vegetable);
            }

            Inventory inventory = inventoryRepository.findByVegetableId(item.getVegetableId()).orElse(null);
            if (inventory != null) {
                inventory.setSoldQuantity(inventory.getSoldQuantity() - item.getQuantity());
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() + item.getQuantity());
                inventoryRepository.save(inventory);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }
}
