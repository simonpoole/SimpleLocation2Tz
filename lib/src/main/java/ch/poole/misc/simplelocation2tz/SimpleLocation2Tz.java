/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ch.poole.misc.simplelocation2tz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mapbox.geojson.BoundingBox;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.MultiPolygon;
import com.mapbox.geojson.Point;
import com.mapbox.turf.TurfJoins;

public class SimpleLocation2Tz {
    private final FeatureCollection fc;

    public SimpleLocation2Tz() throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = loader.getResourceAsStream("natural-earth-timezones-bbox.geojson")) {
            fc = parseTzFile(is);
        }
    }

    /**
     * Parse the TZ information from an INputStream
     * 
     * @param is an InputStream with a GeoJSON FeatureCollection
     * @throws IOException if reading or parsing fails
     */
    private FeatureCollection parseTzFile(@NotNull InputStream is) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return FeatureCollection.fromJson(sb.toString());
    }

    /**
     * Get the time zone for a coordinate tupel
     * 
     * @param lon the WGS84 longitude
     * @param lat the WGS84 latitude
     * @return the timezone id or null
     */
    @Nullable
    public String getTimeZone(double lon, double lat) {
        final Point point = Point.fromLngLat(lon, lat);
        for (Feature f : fc.features()) {
            BoundingBox box = f.bbox();
            if (box.west() <= lon && lon <= box.east() && box.south() <= lat && lat <= box.north()) {
                Geometry g = f.geometry();
                if (g != null && TurfJoins.inside(point, (MultiPolygon) g)) {
                    return f.getStringProperty("tz_name1st");
                }
            }
        }
        return null;
    }
}
