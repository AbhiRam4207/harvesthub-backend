package com.harvesthub.backend.controller;

import com.harvesthub.backend.dto.inventory.InventoryRequest;
import com.harvesthub.backend.dto.inventory.InventoryResponse;
import com.harvesthub.backend.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/vegetable/{vegetableId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InventoryResponse> getInventoryByVegetableId(@PathVariable Long vegetableId) {
        InventoryResponse response = inventoryService.getInventoryByVegetableId(vegetableId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/vegetable/{vegetableId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InventoryResponse> createOrUpdateInventory(@PathVariable Long vegetableId, @Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.createOrUpdateInventory(vegetableId, request);
        return ResponseEntity.ok(response);
    }
}
