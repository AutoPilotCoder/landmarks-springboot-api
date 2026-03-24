package com.java_springboot.landmarks.controller;

import com.java_springboot.landmarks.assembler.CategoryModelAssembler;
import com.java_springboot.landmarks.assembler.LandmarkModelAssembler;
import com.java_springboot.landmarks.dto.response.CategoryResponseDTO;
import com.java_springboot.landmarks.entity.Category;
import com.java_springboot.landmarks.entity.Landmark;
import com.java_springboot.landmarks.exception.CategoryNotFoundException;
import com.java_springboot.landmarks.repository.CategoryRepository;
import com.java_springboot.landmarks.repository.LandmarkRepository;
import com.java_springboot.landmarks.service.CategoryService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final LandmarkRepository landmarkRepository;
    private final CategoryModelAssembler categoryModelAssembler;
    private final LandmarkModelAssembler landmarkModelAssembler;
    private final CategoryService categoryService;

    @GetMapping
    public CollectionModel<EntityModel<CategoryResponseDTO.CategorySummary>> getAllCategories() {
        return CollectionModel.of(
                categoryService.findAll(),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public EntityModel<CategoryResponseDTO.CategorySummary> getCategory(@PathVariable long id) {
        return categoryModelAssembler.toModel(categoryService.findById(id));
    }

    @GetMapping("/{id}/landmarks")
    public CollectionModel<EntityModel<Landmark>> getLandmarksForCategory(@PathVariable long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        List<EntityModel<Landmark>> landmarks = category.getLandmarks()
                .stream()
                .map(landmarkModelAssembler::toModel)
                .toList();

        return CollectionModel.of(
                landmarks,
                linkTo(methodOn(CategoryController.class).getLandmarksForCategory(id)).withSelfRel()
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<CategoryResponseDTO.CategorySummary>> addCategory(@RequestBody Category category) {
        EntityModel<CategoryResponseDTO.CategorySummary> entityModel = categoryModelAssembler.toModel(categoryService.createCategory(category));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CategoryResponseDTO.CategorySummary>> updateCategory(@PathVariable long id, @RequestBody Category category) {
        EntityModel<CategoryResponseDTO.CategorySummary> entityModel = categoryModelAssembler.toModel(categoryService.updateCategory(id, category));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
