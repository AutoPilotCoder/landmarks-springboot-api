package com.java_springboot.landmarks.util;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Component;

@Component
public class GeometryUtil {
    public static Point makePoint(double lng, double lat) {
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
        return gf.createPoint(new Coordinate(lng, lat));
        // NOTE: PostGIS uses (longitude, latitude) order — not (lat, lng)
    }
}
