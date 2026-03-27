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

    List<Landmark> findByNameContainingIgnoreCase(String keyword);
    // SELECT * FROM landmark WHERE LOWER(name) LIKE LOWER('%keyword%')

    @Query("SELECT DISTINCT l from Landmark l " +
            "JOIN FETCH l.region r " +
            "JOIN FETCH l.categories c " +
            "WHERE LOWER(c.name) LIKE LOWER(:category)")
    List<Landmark> findByCategory(@Param("category") String category);

    // JPQL query (object-oriented SQL using class/field names, not table/column names)
    // Alternative: List<Landmark> findByRegionIdOrderByNameAsc(Long regionId);
    @Query("SELECT l FROM Landmark l WHERE l.region.id = :regionId ORDER BY l.name")
    List<Landmark> findByRegionIdOrdered(@Param("regionId") Long regionId);

    // Solution to lazy loading and jackson bytebuddy error, jackson does not understand lazy loading return proxy
    // Only need special fetch when querying region as all landmarks always only have 1 region (one to many)
    @Query("SELECT l FROM Landmark l JOIN FETCH l.region JOIN FETCH l.categories")
    List<Landmark> findAllFullData();

    @Query("SELECT DISTINCT l FROM Landmark l JOIN FETCH l.region JOIN FETCH l.categories WHERE l.id = :id")
    Optional<Landmark> findByIdFullData(@Param("id") Long id);

    @Query(value = """
            SELECT 
                l.*, 
                ST_Distance(
                    l.location::geography,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
                ) AS distance
            FROM landmark l
            WHERE ST_DWithin(
                l.location::geography,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                :radiusMetres
            )
            ORDER BY distance
            """, nativeQuery = true)
    List<Object[]> findNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres
    );

    @Query(value = """
                SELECT 
                    ST_Distance(
                        a.location::geography,
                        b.location::geography
                    )
                FROM landmark a, landmark b 
                WHERE 
                    a.id = :id1 AND
                    b.id = :id2
            """, nativeQuery = true)
    Double findDistanceBetweenLandmarks(@Param("id1") Long id1, @Param("id2") Long id2);
}
