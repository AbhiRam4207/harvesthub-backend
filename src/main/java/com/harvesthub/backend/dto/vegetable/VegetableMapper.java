package com.harvesthub.backend.dto.vegetable;

import com.harvesthub.backend.entity.Category;
import com.harvesthub.backend.entity.Vegetable;
import org.springframework.stereotype.Component;

@Component
public class VegetableMapper {

    public VegetableResponse toResponse(Vegetable vegetable) {
        VegetableResponse.CategoryResponse categoryResponse = new VegetableResponse.CategoryResponse(
                vegetable.getCategory().getId(),
                vegetable.getCategory().getName()
        );

        return new VegetableResponse(
                vegetable.getId(),
                vegetable.getName(),
                vegetable.getDescription(),
                vegetable.getPrice(),
                vegetable.getQuantity(),
                vegetable.getImageUrl(),
                categoryResponse,
                vegetable.getActive(),
                vegetable.getCreatedAt(),
                vegetable.getUpdatedAt()
        );
    }
}
