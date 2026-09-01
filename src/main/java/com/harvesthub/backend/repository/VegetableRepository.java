package com.harvesthub.backend.repository;

import com.harvesthub.backend.entity.Category;
import com.harvesthub.backend.entity.Vegetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VegetableRepository extends JpaRepository<Vegetable, Long> {
    List<Vegetable> findByCategory(Category category);
    List<Vegetable> findByNameContainingIgnoreCase(String name);
    List<Vegetable> findByActiveTrue();
    List<Vegetable> findByCategoryAndActiveTrue(Category category);
    boolean existsByNameAndCategory(String name, Category category);
}
