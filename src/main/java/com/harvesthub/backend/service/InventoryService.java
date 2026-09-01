package com.harvesthub.backend.service;

import com.harvesthub.backend.dto.inventory.InventoryMapper;
import com.harvesthub.backend.dto.inventory.InventoryRequest;
import com.harvesthub.backend.dto.inventory.InventoryResponse;
import com.harvesthub.backend.entity.Inventory;
import com.harvesthub.backend.entity.Vegetable;
import com.harvesthub.backend.exception.ResourceNotFoundException;
import com.harvesthub.backend.repository.InventoryRepository;
import com.harvesthub.backend.repository.VegetableRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final VegetableRepository vegetableRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, VegetableRepository vegetableRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.vegetableRepository = vegetableRepository;
        this.inventoryMapper = inventoryMapper;
    }

    public InventoryResponse getInventoryByVegetableId(Long vegetableId) {
        Inventory inventory = inventoryRepository.findByVegetableId(vegetableId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for vegetable id: " + vegetableId));
        return inventoryMapper.toResponse(inventory);
    }

    public InventoryResponse createOrUpdateInventory(Long vegetableId, InventoryRequest request) {
        Vegetable vegetable = vegetableRepository.findById(vegetableId)
                .orElseThrow(() -> new ResourceNotFoundException("Vegetable not found with id: " + vegetableId));

        Inventory inventory = inventoryRepository.findByVegetableId(vegetableId).orElse(null);

        if (inventory == null) {
            inventory = Inventory.builder()
                    .vegetableId(vegetableId)
                    .vegetableName(vegetable.getName())
                    .totalStock(request.getTotalStock())
                    .soldQuantity(0)
                    .availableQuantity(request.getTotalStock())
                    .build();
        } else {
            inventory.setTotalStock(request.getTotalStock());
            inventory.setAvailableQuantity(request.getTotalStock() - inventory.getSoldQuantity());
        }

        inventoryRepository.save(inventory);

        vegetable.setQuantity(inventory.getAvailableQuantity());
        vegetableRepository.save(vegetable);

        return inventoryMapper.toResponse(inventory);
    }
}
