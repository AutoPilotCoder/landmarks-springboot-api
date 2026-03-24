package com.java_springboot.landmarks.service;

import com.java_springboot.landmarks.assembler.CategoryModelAssembler;
import com.java_springboot.landmarks.controller.CategoryController;
import com.java_springboot.landmarks.dto.response.CategoryResponseDTO;
import com.java_springboot.landmarks.entity.Category;
import com.java_springboot.landmarks.entity.Landmark;
import com.java_springboot.landmarks.exception.CategoryNotFoundException;
import com.java_springboot.landmarks.exception.LandmarkNotFoundException;
import com.java_springboot.landmarks.mapper.CategoryMapper;
import com.java_springboot.landmarks.repository.CategoryRepository;
import com.java_springboot.landmarks.repository.LandmarkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final LandmarkRepository landmarkRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryModelAssembler categoryModelAssembler;

    public List<EntityModel<CategoryResponseDTO.CategorySummary>> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toSummary)
                .map(categoryModelAssembler::toModel)
                .toList();
    }

    public CategoryResponseDTO.CategorySummary findById(long id) {
        return categoryMapper.toSummary(categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id)));
    }

    @Transactional
    public CategoryResponseDTO.CategorySummary createCategory(Category category) {
        String name = category.getName().trim().toLowerCase();
        category.setName(name);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toSummary(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO.CategorySummary updateCategory(long id, Category category) {
        Category exisitingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        exisitingCategory.setName(category.getName());
        exisitingCategory.setDescription(category.getDescription());
        categoryRepository.save(exisitingCategory);

        return categoryMapper.toSummary(exisitingCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        category.getLandmarks().forEach(landmark -> {
            landmark.getCategories().remove(category);
        });

        categoryRepository.flush();
        categoryRepository.delete(category);
    }

}
