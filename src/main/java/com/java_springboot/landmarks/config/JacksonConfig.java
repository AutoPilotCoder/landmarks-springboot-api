package com.java_springboot.landmarks.config;

import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    // Global jackson config that changes all raw json geometry data into
    // {
    //   "type": "Point",
    //   "coordinates": [
    //      101.7117,
    //      3.1578
    //   ]
    // }
    // instead of raw envelope geometry data

    @Bean
    public JtsModule jtsModule() {
        return new JtsModule();
    }
}
