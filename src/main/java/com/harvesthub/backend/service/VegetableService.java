package com.harvesthub.backend.service;

import com.harvesthub.backend.dto.vegetable.VegetableMapper;
import com.harvesthub.backend.dto.vegetable.VegetableRequest;
import com.harvesthub.backend.dto.vegetable.VegetableResponse;
import com.harvesthub.backend.entity.Category;
import com.harvesthub.backend.entity.Inventory;
import com.harvesthub.backend.entity.Vegetable;
import com.harvesthub.backend.exception.ResourceNotFoundException;
import com.harvesthub.backend.repository.CategoryRepository;
import com.harvesthub.backend.repository.InventoryRepository;
import com.harvesthub.backend.repository.VegetableRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VegetableService {

    private final VegetableRepository vegetableRepository;
    private final CategoryRepository categoryRepository;
    private final VegetableMapper vegetableMapper;
    private final InventoryRepository inventoryRepository;

    public VegetableService(VegetableRepository vegetableRepository, CategoryRepository categoryRepository, VegetableMapper vegetableMapper, InventoryRepository inventoryRepository) {
        this.vegetableRepository = vegetableRepository;
        this.categoryRepository = categoryRepository;
        this.vegetableMapper = vegetableMapper;
        this.inventoryRepository = inventoryRepository;
    }

    public VegetableResponse createVegetable(VegetableRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        if (vegetableRepository.existsByNameAndCategory(request.getName(), category)) {
            throw new IllegalArgumentException("Vegetable already exists in this category");
        }

        Vegetable vegetable = Vegetable.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .imageUrl(request.getImageUrl())
                .category(category)
                .active(true)
                .build();

        vegetableRepository.save(vegetable);

        Inventory inventory = Inventory.builder()
                .vegetableId(vegetable.getId())
                .vegetableName(vegetable.getName())
                .totalStock(vegetable.getQuantity())
                .soldQuantity(0)
                .availableQuantity(vegetable.getQuantity())
                .build();
        inventoryRepository.save(inventory);

        return vegetableMapper.toResponse(vegetable);
    }

    public List<VegetableResponse> getAllVegetables() {
        return vegetableRepository.findAll()
                .stream()
                .map(vegetableMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<VegetableResponse> getActiveVegetables() {
        return vegetableRepository.findByActiveTrue()
                .stream()
                .map(vegetableMapper::toResponse)
                .collect(Collectors.toList());
    }

    public VegetableResponse getVegetableById(Long id) {
        Vegetable vegetable = vegetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vegetable not found with id: " + id));
        return vegetableMapper.toResponse(vegetable);
    }

    public List<VegetableResponse> getVegetablesByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return vegetableRepository.findByCategoryAndActiveTrue(category)
                .stream()
                .map(vegetableMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<VegetableResponse> searchVegetables(String keyword) {
        return vegetableRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(vegetableMapper::toResponse)
                .collect(Collectors.toList());
    }

    public VegetableResponse updateVegetable(Long id, VegetableRequest request) {
        Vegetable vegetable = vegetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vegetable not found with id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        if (!vegetable.getName().equals(request.getName()) && vegetableRepository.existsByNameAndCategory(request.getName(), category)) {
            throw new IllegalArgumentException("Vegetable already exists in this category");
        }

        vegetable.setName(request.getName());
        vegetable.setDescription(request.getDescription());
        vegetable.setPrice(request.getPrice());
        vegetable.setQuantity(request.getQuantity());
        vegetable.setImageUrl(request.getImageUrl());
        vegetable.setCategory(category);

        vegetableRepository.save(vegetable);

        Inventory inventory = inventoryRepository.findByVegetableId(id).orElse(null);
        if (inventory == null) {
            inventory = Inventory.builder()
                    .vegetableId(id)
                    .vegetableName(vegetable.getName())
                    .totalStock(vegetable.getQuantity())
                    .soldQuantity(0)
                    .availableQuantity(vegetable.getQuantity())
                    .build();
        } else {
            inventory.setVegetableName(vegetable.getName());
            inventory.setTotalStock(vegetable.getQuantity());
            inventory.setAvailableQuantity(vegetable.getQuantity() - inventory.getSoldQuantity());
        }
        inventoryRepository.save(inventory);

        return vegetableMapper.toResponse(vegetable);
    }

    public void deleteVegetable(Long id) {
        Vegetable vegetable = vegetableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vegetable not found with id: " + id));
        vegetable.setActive(false);
        vegetableRepository.save(vegetable);
    }
}
