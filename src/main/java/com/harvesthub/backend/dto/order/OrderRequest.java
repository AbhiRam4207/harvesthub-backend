package com.harvesthub.backend.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotBlank(message = "Delivery address is required")
    @Size(min = 5, max = 500, message = "Delivery address must be between 5 and 500 characters")
    private String deliveryAddress;
}
