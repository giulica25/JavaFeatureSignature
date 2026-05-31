package org.example;

import java.util.HashMap;
import java.util.Map;

public class FeatureUsageMap {
    private final Map<Feature, Integer> map;

    public FeatureUsageMap() {
        map = new HashMap<>();
        for (Feature feature: Feature.values()) {
            map.put(feature, 0);
        }
    }

    public int get(Feature feature) {
        return map.get(feature);
    }

    public void increment(Feature feature) {
        map.put(feature, get(feature) + 1);
    }

    public void add(FeatureUsageMap other) {
        for (Feature feature: Feature.values()) {
            map.put(feature, get(feature) + other.get(feature));
        }
    }

    public String toJSON() {
        StringBuilder result = new StringBuilder("{");
        boolean comma = false;
        for (Feature feature: Feature.values()) {
            if (comma) {
                result.append(",");
            }
            comma = true;

            result.append("\"").append(feature.toString()).append("\":").append(get(feature));
        }
        result.append("}");
        return result.toString();
    }
}
