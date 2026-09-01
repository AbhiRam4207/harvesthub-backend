package com.harvesthub.backend.dto.inventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

    @NotNull(message = "Total stock is required")
    @Min(value = 0, message = "Total stock cannot be negative")
    private Integer totalStock;
}
