package com.java_springboot.landmarks.controller;

import com.java_springboot.landmarks.assembler.CategoryModelAssembler;
import com.java_springboot.landmarks.assembler.LandmarkModelAssembler;
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
    public CollectionModel<EntityModel<Category>> getAllCategories() {
        List<EntityModel<Category>> categories = categoryRepository.findAll()
                .stream()
                .map(categoryModelAssembler::toModel)
                .toList();

        return CollectionModel.of(
                categories,
                linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public EntityModel<Category> getCategory(@PathVariable long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryModelAssembler.toModel(category);
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
    public ResponseEntity<EntityModel<Category>> addCategory(@RequestBody Category category) {
        String name = category.getName().trim().toLowerCase();
        category.setName(name);
        Category savedCategory = categoryRepository.save(category);
        EntityModel<Category> entityModel = categoryModelAssembler.toModel(savedCategory);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Category>> updateCategory(@PathVariable long id, @RequestBody Category category) {
        Category exisitingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        exisitingCategory.setName(category.getName());
        exisitingCategory.setDescription(category.getDescription());
        categoryRepository.save(exisitingCategory);
        EntityModel<Category> entityModel = categoryModelAssembler.toModel(category);

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
