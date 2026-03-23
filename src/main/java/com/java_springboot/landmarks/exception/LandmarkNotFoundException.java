package com.java_springboot.landmarks.exception;

public class LandmarkNotFoundException extends RuntimeException {
    public LandmarkNotFoundException(Long id) {
        super("Landmark not found with id: " + id);
    }
}

