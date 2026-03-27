package com.java_springboot.landmarks.config;

import ch.qos.logback.core.util.LocationUtil;
import com.java_springboot.landmarks.entity.Category;
import com.java_springboot.landmarks.entity.Landmark;
import com.java_springboot.landmarks.entity.Region;
import com.java_springboot.landmarks.repository.CategoryRepository;
import com.java_springboot.landmarks.repository.LandmarkRepository;
import com.java_springboot.landmarks.repository.RegionRepository;
import com.java_springboot.landmarks.util.GeometryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner loadData(RegionRepository regionRepo, LandmarkRepository landmarkRepo, CategoryRepository categoryRepo, JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_landmark_location ON landmark USING GIST ((location::geography));");
            jdbcTemplate.execute("EXPLAIN ANALYZE SELECT * FROM landmark  WHERE ST_DWithin(location::geography,  ST_SetSRID(ST_MakePoint(101.71, 3.15), 4326)::geography, 5000);");
            // Only seed if DB is empty
            if (regionRepo.count() > 0) return;

            Region kl = new Region();
            kl.setName("Kuala Lumpur");
            kl.setDescription("Capital city of Malaysia");
            kl.setCountry("Malaysia");
            regionRepo.save(kl);

            Region penang = new Region();
            penang.setName("Penang");
            penang.setDescription("Pearl of the Orient");
            penang.setCountry("Malaysia");
            regionRepo.save(penang);

            Category park = new Category();
            park.setName("Park");
            park.setDescription("Has an open park.");
            categoryRepo.save(park);

            Category mall = new Category();
            mall.setName("Mall");
            mall.setDescription("Has an open park.");
            categoryRepo.save(mall);

            // Landmarks for KL
            Landmark petronas = new Landmark();
            petronas.setName("Petronas Twin Towers");
            petronas.setDescription("Iconic twin skyscrapers, tallest in the world 1998-2004");
            petronas.setLatitude(3.1578);
            petronas.setLongitude(101.7117);
            petronas.setCategories(new HashSet<>(Set.of(mall, park)));
            petronas.setRegion(kl);
            petronas.setLocation(GeometryUtil.makePoint(petronas.getLongitude(), petronas.getLatitude()));
            landmarkRepo.save(petronas);

            Landmark klTower = new Landmark();
            klTower.setName("KL Tower");
            klTower.setDescription("Telecommunications tower with observation deck");
            klTower.setLatitude(3.1528);
            klTower.setLongitude(101.7037);
            klTower.setCategories(new HashSet<>(Set.of(mall)));
            klTower.setRegion(kl);
            klTower.setLocation(GeometryUtil.makePoint(klTower.getLongitude(), klTower.getLatitude()));
            landmarkRepo.save(klTower);

            // Landmark for Penang
            Landmark penangHill = new Landmark();
            penangHill.setName("Penang Hill");
            penangHill.setDescription("Historic hill station with panoramic views");
            penangHill.setLatitude(5.4209);
            penangHill.setLongitude(100.2700);
            penangHill.setCategories(new HashSet<>(Set.of(park)));
            penangHill.setRegion(penang);
            penangHill.setLocation(GeometryUtil.makePoint(penangHill.getLongitude(), penangHill.getLatitude()));
            landmarkRepo.save(penangHill);

            log.info("Seeded {} regions and {} landmarks",
                    regionRepo.count(), landmarkRepo.count());
        };
    }
}

