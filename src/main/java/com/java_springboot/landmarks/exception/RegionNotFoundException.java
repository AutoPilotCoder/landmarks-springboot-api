package com.java_springboot.landmarks.exception;

public class RegionNotFoundException extends RuntimeException {
    public RegionNotFoundException(Long id) {
        super("Region not found with id: " + id);
    }
}

