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
import com.java_springboot.landmarks.util.GeometryUtil;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.*;
import java.util.List;
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

    @GetMapping("/distanceBetween")
    public EntityModel<Map<String, Object>> getDistanceBetweenLandmarks(@RequestBody DistanceRequest request) {
        Double distance = landmarkRepository.findDistanceBetweenLandmarks(request.id1(), request.id2());

        Map<String, Object> result = new HashMap<>();
        result.put("id1", request.id1());
        result.put("id2", request.id2());
        result.put("distance_meters", Math.round(distance));
        // How the trick works (since there's no easy way to do 2 decimal places
        //
        //        1089.43 metres
        //
        //        Step 1: /1000.0   →1.08943 convert to km
        //        Step 2: *100.0    →108.943 shift decimal RIGHT 2 places
        //        Step 3:Math.round →109 now safe to round, nothing important is lost
        //        Step 4: /100.0    →1.09 shift decimal LEFT 2 places back
        //
        //  The `*100`shifts the digits you want to keep above the decimal point so `Math.round` does not destroy them.
        //        Then `/100`shifts everything back.
        result.put("distance_km", Math.round((distance / 1000.0 * 100.0)) / 100.0);

        return EntityModel.of(
                result,
                linkTo(methodOn(LandmarkController.class).getDistanceBetweenLandmarks(request)).withSelfRel(),
                linkTo(methodOn(LandmarkController.class).getAllLandmarks()).withRel("landmarks"),
                linkTo(methodOn(LandmarkController.class).getLandmarkById(request.id1())).withRel("landmarks1"),
                linkTo(methodOn(LandmarkController.class).getLandmarkById(request.id2())).withRel("landmarks2")
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
        landmark.setLocation(GeometryUtil.makePoint(request.longitude(), request.latitude()));

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
        landmark.setLocation(GeometryUtil.makePoint(request.longitude(), request.latitude()));

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

    record DistanceRequest(
            Long id1,
            Long id2
    ) {
    }

}
