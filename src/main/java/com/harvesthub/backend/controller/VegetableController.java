package com.harvesthub.backend.controller;

import com.harvesthub.backend.dto.vegetable.VegetableRequest;
import com.harvesthub.backend.dto.vegetable.VegetableResponse;
import com.harvesthub.backend.service.VegetableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vegetables")
public class VegetableController {

    private final VegetableService vegetableService;

    public VegetableController(VegetableService vegetableService) {
        this.vegetableService = vegetableService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VegetableResponse> createVegetable(@Valid @RequestBody VegetableRequest request) {
        VegetableResponse response = vegetableService.createVegetable(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VegetableResponse>> getAllVegetables() {
        List<VegetableResponse> response = vegetableService.getAllVegetables();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VegetableResponse>> getActiveVegetables() {
        List<VegetableResponse> response = vegetableService.getActiveVegetables();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VegetableResponse> getVegetableById(@PathVariable Long id) {
        VegetableResponse response = vegetableService.getVegetableById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VegetableResponse>> getVegetablesByCategory(@PathVariable Long categoryId) {
        List<VegetableResponse> response = vegetableService.getVegetablesByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VegetableResponse>> searchVegetables(@RequestParam String keyword) {
        List<VegetableResponse> response = vegetableService.searchVegetables(keyword);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VegetableResponse> updateVegetable(@PathVariable Long id, @Valid @RequestBody VegetableRequest request) {
        VegetableResponse response = vegetableService.updateVegetable(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteVegetable(@PathVariable Long id) {
        vegetableService.deleteVegetable(id);
        return ResponseEntity.noContent().build();
    }
}
