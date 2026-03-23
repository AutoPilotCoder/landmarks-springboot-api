package com.java_springboot.landmarks.repository;

import com.java_springboot.landmarks.entity.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandmarkRepository extends JpaRepository<Landmark, Long> {

    List<Landmark> findByRegionId(Long regionId);
    // SELECT * FROM landmark WHERE region_id = ?

    List<Landmark> findByCategory(String category);
    // SELECT * FROM landmark WHERE category = ?

    List<Landmark> findByNameContainingIgnoreCase(String keyword);
    // SELECT * FROM landmark WHERE LOWER(name) LIKE LOWER('%keyword%')

    // JPQL query (object-oriented SQL using class/field names, not table/column names)
    // Alternative: List<Landmark> findByRegionIdOrderByNameAsc(Long regionId);
    @Query("SELECT l FROM Landmark l WHERE l.region.id = :regionId ORDER BY l.name")
    List<Landmark> findByRegionIdOrdered(@Param("regionId") Long regionId);

    // Solution to lazy loading and jackson bytebuddy error, jackson does not understand lazy loading return proxy
    // Only need special fetch when querying region as all landmarks always only have 1 region (one to many)
    @Query("SELECT l FROM Landmark l JOIN FETCH l.region")
    List<Landmark> findAllWithRegion();

    @Query("SELECT l FROM Landmark l JOIN FETCH l.region WHERE l.id = :id")
    Optional<Landmark> findByIdWithRegion(@Param("id") Long id);
}
