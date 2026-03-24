package com.java_springboot.landmarks.service;

import com.java_springboot.landmarks.entity.Category;
import com.java_springboot.landmarks.entity.Landmark;
import com.java_springboot.landmarks.exception.LandmarkNotFoundException;
import com.java_springboot.landmarks.repository.LandmarkRepository;
import org.springframework.stereotype.Service;
import lombok.*;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class LandmarkService {
    private final LandmarkRepository landmarkRepository;
    private final CategoryService categoryService;

    public void syncCategories(long landmarkId, List<String> newNames) {
        Landmark landmark = landmarkRepository.findById(landmarkId)
                .orElseThrow(() -> new LandmarkNotFoundException(landmarkId));

//        Set<Category> newCategories = newNames.stream()
//                .map()
    }

}
