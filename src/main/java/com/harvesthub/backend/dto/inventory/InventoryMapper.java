package com.harvesthub.backend.dto.inventory;

import com.harvesthub.backend.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getVegetableId(),
                inventory.getVegetableName(),
                inventory.getTotalStock(),
                inventory.getSoldQuantity(),
                inventory.getAvailableQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}
