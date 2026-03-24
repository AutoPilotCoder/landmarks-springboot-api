package com.java_springboot.landmarks.controller;

import com.java_springboot.landmarks.assembler.LandmarkModelAssembler;
import com.java_springboot.landmarks.entity.Category;
import com.java_springboot.landmarks.entity.Landmark;
import com.java_springboot.landmarks.entity.Region;
import com.java_springboot.landmarks.exception.CategoryNotFoundException;
import com.java_springboot.landmarks.exception.LandmarkNotFoundException;
import com.java_springboot.landmarks.exception.RegionNotFoundException;
import com.java_springboot.landmarks.repository.CategoryRepository;
import com.java_springboot.landmarks.repository.LandmarkRepository;
import com.java_springboot.landmarks.repository.RegionRepository;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/landmarks")
@AllArgsConstructor
public class LandmarkController {
    private final LandmarkRepository landmarkRepository;
    private final LandmarkModelAssembler landmarkModelAssembler;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public CollectionModel<EntityModel<Landmark>> getAllLandmarks() {
        List<EntityModel<Landmark>> landmarks = landmarkRepository.findAllFullData()
                .stream()
                .map(landmarkModelAssembler::toModel)
                .toList();

        return CollectionModel.of(
                landmarks,
                linkTo(methodOn(LandmarkController.class).getAllLandmarks()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public EntityModel<Landmark> getLandmarkById(@PathVariable Long id) {
        Landmark landmark = landmarkRepository.findByIdFullData(id)
                .orElseThrow(() -> new LandmarkNotFoundException(id));
        return landmarkModelAssembler.toModel(landmark);
    }

    @GetMapping("/search")
    public CollectionModel<EntityModel<Landmark>> getAllLandmarksBySearchCriteria(@RequestParam(required = false) String category, @RequestParam(required = false) String keyword) {
        List<Landmark> landmarks;
        if (keyword != null) {
            landmarks = landmarkRepository.findByNameContainingIgnoreCase(keyword);
        } else if (category != null) {
            landmarks = landmarkRepository.findByCategory(category);
        } else {
            landmarks = landmarkRepository.findAll();
        }

        List<EntityModel<Landmark>> models = landmarks.stream()
                .map(landmarkModelAssembler::toModel)
                .toList();

        return CollectionModel.of(
                models,
                linkTo(methodOn(LandmarkController.class).getAllLandmarks()).withSelfRel()
        );
    }

    @PostMapping
    public ResponseEntity<EntityModel<Landmark>> addLandmark(@RequestBody LandmarkRequest request) {
        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new RegionNotFoundException(request.regionId()));

        Landmark landmark = new Landmark();
        landmark.setName(request.name());
        landmark.setDescription(request.description());
        landmark.setLatitude(request.latitude());
        landmark.setLongitude(request.longitude());
        Set<Category> categories = request.categoryIds() == null ?
                new HashSet<>() :
                request.categoryIds()
                        .stream()
                        .map(valId -> categoryRepository.findById(valId)
                                .orElseThrow(() -> new CategoryNotFoundException(valId)))
                        .collect(Collectors.toSet());
        landmark.setCategories(categories);
        landmark.setRegion(region);

        Landmark savedLandmark = landmarkRepository.save(landmark);
        EntityModel<Landmark> model = landmarkModelAssembler.toModel(savedLandmark);

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);

    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Landmark>> updateLandmark(@PathVariable Long id, @RequestBody LandmarkRequest request) {
        Landmark landmark = landmarkRepository.findById(id)
                .orElseThrow(() -> new LandmarkNotFoundException(id));

        landmark.setName(request.name());
        landmark.setDescription(request.description());
        landmark.setLatitude(request.latitude());
        landmark.setLongitude(request.longitude());

        Set<Category> categories = request.categoryIds() == null ?
                new HashSet<>() :
                request.categoryIds()
                        .stream()
                        .map(valId -> categoryRepository.findById(valId)
                                .orElseThrow(() -> new CategoryNotFoundException(valId)))
                        .collect(Collectors.toSet());
        landmark.setCategories(categories);

        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new RegionNotFoundException(request.regionId()));
        landmark.setRegion(region);

        Landmark savedLandmark = landmarkRepository.save(landmark);
        EntityModel<Landmark> model = landmarkModelAssembler.toModel(savedLandmark);

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @PutMapping("./{id}/categories")
    public ResponseEntity<EntityModel<Landmark>> updateLandmarkCategories(@PathVariable Long id, @RequestBody Set<Long> categoryIds) {
        Landmark landmark = landmarkRepository.findById(id)
                .orElseThrow(() -> new LandmarkNotFoundException(id));
        Set<Category> categories = categoryIds.stream()
                .map(valId -> categoryRepository.findById(valId)
                        .orElseThrow(() -> new CategoryNotFoundException(valId)))
                .collect(Collectors.toSet());
        landmark.getCategories().addAll(categories);
        Landmark savedLandmark = landmarkRepository.save(landmark);
        EntityModel<Landmark> model = landmarkModelAssembler.toModel(savedLandmark);
        return ResponseEntity
                .ok(model);
    }

    @DeleteMapping("./{id}/categories")
    public ResponseEntity<EntityModel<Landmark>> deleteLandmarkCategories(@PathVariable Long id, @RequestBody Set<Long> categoryIds) {
        Landmark landmark = landmarkRepository.findById(id)
                .orElseThrow(() -> new LandmarkNotFoundException(id));
        Set<Category> categories = categoryIds.stream()
                .map(valId -> categoryRepository.findById(valId)
                        .orElseThrow(() -> new CategoryNotFoundException(valId)))
                .collect(Collectors.toSet());
        landmark.getCategories().removeAll(categories);
        Landmark savedLandmark = landmarkRepository.save(landmark);
        EntityModel<Landmark> model = landmarkModelAssembler.toModel(savedLandmark);
        return ResponseEntity
                .ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLandmark(@PathVariable Long id) {
        if (!landmarkRepository.existsById(id)) {
            throw new LandmarkNotFoundException(id);
        }
        landmarkRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    record LandmarkRequest(
            String name,
            String description,
            Double latitude,
            Double longitude,
            Set<Long> categoryIds,
            Long regionId   // client sends the region ID, not the whole Region object
    ) {
    }

}
