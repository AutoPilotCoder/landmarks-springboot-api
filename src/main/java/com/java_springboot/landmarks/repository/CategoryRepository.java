package com.java_springboot.landmarks.repository;

import com.java_springboot.landmarks.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Boolean existsByName(String name);

    @Query("SELECT c FROM Category c " +
            "JOIN FETCH c.landmarks l " +
            "JOIN FETCH l.region r ")
    List<Category> findAllWithLandmarks();

}
