package com.java_springboot.landmarks.assembler;

import com.java_springboot.landmarks.controller.RegionController;
import com.java_springboot.landmarks.entity.Region;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
// linkTo() and methodOn() are static helpers that build type-safe links

@Component  // Spring creates one instance of this and injects it where needed
public class RegionModelAssembler implements RepresentationModelAssembler<Region, EntityModel<Region>> {

    @Override
    public EntityModel<Region> toModel(Region region) {
        return EntityModel.of(region,
                // Self link: GET /regions/{id}
                linkTo(methodOn(RegionController.class)
                        .getRegionByID(region.getId())).withSelfRel(),

                // Collection link: GET /regions
                linkTo(methodOn(RegionController.class)
                        .getAllRegions()).withRel("regions"),

                // Nested resource link: GET /regions/{id}/landmarks
                linkTo(methodOn(RegionController.class)
                        .getAllLandmarksByID(region.getId())).withRel("landmarks")
        );
    }
}

