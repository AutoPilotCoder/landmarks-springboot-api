package com.java_springboot.landmarks.assembler;

import com.java_springboot.landmarks.controller.CategoryController;
import com.java_springboot.landmarks.entity.Category;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CategoryModelAssembler
        implements RepresentationModelAssembler<Category, EntityModel<Category>> {

    @Override
    public EntityModel<Category> toModel(Category category) {
        return EntityModel.of(category,
                linkTo(methodOn(CategoryController.class).getCategory(category.getId()))
                        .withSelfRel(),
                linkTo(methodOn(CategoryController.class).getAllCategories())
                        .withRel("categories"),
                linkTo(methodOn(CategoryController.class)
                        .getLandmarksForCategory(category.getId()))
                        .withRel("landmarks")
        );
    }
}
