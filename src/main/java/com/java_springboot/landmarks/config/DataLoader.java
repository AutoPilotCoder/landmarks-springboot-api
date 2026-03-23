package com.java_springboot.landmarks.config;

import com.java_springboot.landmarks.entity.Landmark;
import com.java_springboot.landmarks.entity.Region;
import com.java_springboot.landmarks.repository.LandmarkRepository;
import com.java_springboot.landmarks.repository.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner loadData(RegionRepository regionRepo, LandmarkRepository landmarkRepo) {
        return args -> {
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

            // Landmarks for KL
            Landmark petronas = new Landmark();
            petronas.setName("Petronas Twin Towers");
            petronas.setDescription("Iconic twin skyscrapers, tallest in the world 1998-2004");
            petronas.setLatitude(3.1578);
            petronas.setLongitude(101.7117);
            petronas.setCategory("monument");
            petronas.setRegion(kl);
            landmarkRepo.save(petronas);

            Landmark klTower = new Landmark();
            klTower.setName("KL Tower");
            klTower.setDescription("Telecommunications tower with observation deck");
            klTower.setLatitude(3.1528);
            klTower.setLongitude(101.7037);
            klTower.setCategory("monument");
            klTower.setRegion(kl);
            landmarkRepo.save(klTower);

            // Landmark for Penang
            Landmark penangHill = new Landmark();
            penangHill.setName("Penang Hill");
            penangHill.setDescription("Historic hill station with panoramic views");
            penangHill.setLatitude(5.4209);
            penangHill.setLongitude(100.2700);
            penangHill.setCategory("park");
            penangHill.setRegion(penang);
            landmarkRepo.save(penangHill);

            log.info("Seeded {} regions and {} landmarks",
                    regionRepo.count(), landmarkRepo.count());
        };
    }
}

