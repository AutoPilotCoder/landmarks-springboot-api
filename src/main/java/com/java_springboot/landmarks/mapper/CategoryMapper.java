package com.java_springboot.landmarks.mapper;

import com.java_springboot.landmarks.dto.response.CategoryResponseDTO;
import com.java_springboot.landmarks.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {
    public CategoryResponseDTO.CategorySummary toSummary(Category category) {
        return new CategoryResponseDTO.CategorySummary(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
