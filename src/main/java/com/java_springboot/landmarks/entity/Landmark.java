package com.java_springboot.landmarks.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedBy;

@Entity
@Table(name = "landmark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "region")
public class Landmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 50)
    private String category;   // e.g. "monument", "park", "building"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    @JsonIgnoreProperties("landmarks")
    // name = "region_id" → the foreign key column in the landmark table
    private Region region;


}
