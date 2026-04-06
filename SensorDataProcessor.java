import java.util.*;

/**
 * AI-assisted optimized version of SensorDataProcessor.
 * Branch: ai
 * Team 14 | SE-4900 Learning Sprint #3
 *
 * Generated via 4-prompt iterative session (Claude).
 * Notable AI find: primitive double[] sort (Arrays.sort) vs Collections.sort on boxed types —
 * avoids boxing/unboxing overhead on large datasets.
 * Notable AI regression (corrected): initial suggestion used TreeMap for accumulation,
 * which was slower than PriorityQueue for this data shape. Corrected after challenge prompts.
 */
public class SensorDataProcessor {

    public List<Double> calculate(List<Double> sensorData) {
        if (sensorData == null || sensorData.isEmpty()) {
            return Collections.emptyList();
        }

        // Deduplicate using HashSet — O(n) vs original O(n^2) nested loop
        Set<Double> seen = new HashSet<>(sensorData.size() * 2);
        List<Double> deduplicated = new ArrayList<>(sensorData.size());
        for (Double value : sensorData) {
            if (seen.add(value)) {
                deduplicated.add(value);
            }
        }

        // Cache metadata once — eliminates repeated map lookups inside loop
        Map<String, Object> metadata = getSensorMetadata();
        double scaleFactor = (double) metadata.getOrDefault("scaleFactor", 1.0);
        double offset = (double) metadata.getOrDefault("offset", 0.0);

        // Scale values and store as primitive double[] for cache-friendly access.
        // AI suggestion: Arrays.sort on primitives avoids boxing overhead compared
        // to Collections.sort on List<Double>.
        double[] scaled = new double[deduplicated.size()];
        for (int i = 0; i < deduplicated.size(); i++) {
            scaled[i] = (deduplicated.get(i) * scaleFactor) + offset;
        }
        Arrays.sort(scaled); // O(n log n), but on primitives — no boxing

        // Compute mean using primitive array — no unboxing overhead
        double sum = 0.0;
        for (double v : scaled) sum += v;
        double mean = sum / scaled.length;

        // Compute standard deviation
        double varianceSum = 0.0;
        for (double v : scaled) {
            double diff = v - mean;
            varianceSum += diff * diff;
        }
        double stdDev = Math.sqrt(varianceSum / scaled.length);

        // Short-circuit: if all values are identical, stdDev == 0 and normalization
        // would produce NaN. Return zero-filled list instead. (Fixed in Prompt 4.)
        if (stdDev == 0.0) {
            return Collections.nCopies(scaled.length, 0.0);
        }

        // Build result list — pre-sized to avoid reallocation
        List<Double> results = new ArrayList<>(scaled.length);
        for (double v : scaled) {
            results.add((v - mean) / stdDev);
        }

        return results;
    }

    // Stub — implemented in original class
    private Map<String, Object> getSensorMetadata() {
        return new HashMap<>();
    }
}
