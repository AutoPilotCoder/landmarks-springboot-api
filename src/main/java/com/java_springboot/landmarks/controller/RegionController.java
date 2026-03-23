package com.java_springboot.landmarks.controller;

import com.java_springboot.landmarks.assembler.LandmarkModelAssembler;
import com.java_springboot.landmarks.assembler.RegionModelAssembler;
import com.java_springboot.landmarks.entity.Landmark;
import com.java_springboot.landmarks.entity.Region;
import com.java_springboot.landmarks.exception.RegionNotFoundException;
import com.java_springboot.landmarks.repository.LandmarkRepository;
import com.java_springboot.landmarks.repository.RegionRepository;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/regions")
@AllArgsConstructor // Declares constructor
public class RegionController {

    private final RegionRepository regionRepository;
    private final LandmarkRepository landmarkRepository;
    private final LandmarkModelAssembler landmarkModelAssembler;
    private final RegionModelAssembler regionModelAssembler;

    // =========================================
    // CRUD OPERATIONS
    // =========================================

    @GetMapping
    public CollectionModel<EntityModel<Region>> getAllRegions() {
        List<EntityModel<Region>> regions = regionRepository.findAll()
                .stream()
                .map(regionModelAssembler::toModel)
                .toList();

        return CollectionModel.of(
                regions,
                linkTo(methodOn(RegionController.class).getAllRegions()).withSelfRel()
        );
    }

    @GetMapping("/{id}")
    public EntityModel<Region> getRegionByID(@PathVariable Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new RegionNotFoundException(id));
        return regionModelAssembler.toModel(region);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Region>> createRegion(@RequestBody Region newRegion) {
        Region saved = regionRepository.save(newRegion);

        EntityModel<Region> regionModel = regionModelAssembler.toModel(saved);
        return ResponseEntity
                .created(regionModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(regionModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Region>> updateRegion(@PathVariable Long id, @RequestBody Region updatedRegion) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new RegionNotFoundException(id));
        region.setName(updatedRegion.getName());
        region.setDescription(updatedRegion.getDescription());
        region.setCountry(updatedRegion.getCountry());

        Region saved = regionRepository.save(region);
        EntityModel<Region> regionModel = regionModelAssembler.toModel(saved);
        return ResponseEntity
                .created(regionModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(regionModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        if (!regionRepository.existsById(id)) {
            throw new RegionNotFoundException(id);
        }

        regionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/landmarks")
    public CollectionModel<EntityModel<Landmark>> getAllLandmarksByID(@PathVariable Long id) {
        if (!regionRepository.existsById(id)) {
            throw new RegionNotFoundException(id);
        }

        List<EntityModel<Landmark>> landmarks = landmarkRepository.findByRegionId(id)
                .stream()
                .map(landmarkModelAssembler::toModel)
                .toList();
        return CollectionModel.of(
                landmarks,
                linkTo(methodOn(RegionController.class).getAllLandmarksByID(id)).withSelfRel()
        );
    }
}
