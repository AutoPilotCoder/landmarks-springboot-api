package com.java_springboot.landmarks.dto.response;

import java.util.List;

public class CategoryResponseDTO {
    public record CategorySummary(
            Long id,
            String name,
            String description
    ) {
    }
}
