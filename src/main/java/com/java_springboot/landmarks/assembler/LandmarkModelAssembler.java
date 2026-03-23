package com.java_springboot.landmarks.assembler;

import com.java_springboot.landmarks.controller.LandmarkController;
import com.java_springboot.landmarks.controller.RegionController;
import com.java_springboot.landmarks.entity.Landmark;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class LandmarkModelAssembler implements RepresentationModelAssembler<Landmark, EntityModel<Landmark>> {

    @Override
    public EntityModel<Landmark> toModel(Landmark landmark) {
        // ====================
        // N+1 Problem
        // ====================
        // landmark.getRegion().getId() adds one extra query for every landmark
        //
        //        return EntityModel.of(
        //                landmark,
        //                // Self link
        //                linkTo(methodOn(LandmarkController.class)
        //                        .getLandmarkById(landmark.getId())).withSelfRel(),
        //                // Collection link
        //                linkTo(methodOn(LandmarkController.class)
        //                        .getAllLandmarks()).withRel("landmarks"),
        //                // Parent region link
        //                linkTo(methodOn(RegionController.class)
        //                        .getRegionByID(landmark.getRegion().getId())).withRel("region")
        //        );

        // =====================
        // Solution
        // =====================
        return EntityModel.of(
                landmark,
                // Self link
                linkTo(methodOn(LandmarkController.class)
                        .getLandmarkById(landmark.getId())).withSelfRel(),
                // Collection link
                linkTo(methodOn(LandmarkController.class)
                        .getAllLandmarks()).withRel("landmarks"),
                // Parent region link
                linkTo(methodOn(RegionController.class)
                        .getRegionByID(landmark.getRegion().getId())).withRel("region")
        );
    }
}

