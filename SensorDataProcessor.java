import java.util.*;

/**
 * Manually optimized version of SensorDataProcessor.
 * Branch: manual
 * Team 14 | SE-4900 Learning Sprint #3
 *
 * Optimization strategies applied (see inline comments for line references):
 *   1. HashSet for O(1) duplicate detection (was O(n^2) nested loop)
 *   2. Cache getSensorMetadata() outside loop (eliminates repeated map lookups)
 *   3. PriorityQueue for sorted accumulation (O(log n) vs O(n log n) sort per pass)
 *   4. Short-circuit normalization when stdDev == 0 (prevents NaN propagation)
 *   5. Pre-sized ArrayList for results (avoids backing-array reallocations)
 */
public class SensorDataProcessor {

    public List<Double> calculate(List<Double> sensorData) {
        if (sensorData == null || sensorData.isEmpty()) {
            return Collections.emptyList();
        }

        // Strategy 1 (replaces lines 45-62): Use HashSet for duplicate detection.
        // Original used a nested loop — O(n^2). HashSet.contains() is O(1) amortized,
        // reducing the overall duplicate pass to O(n).
        Set<Double> seen = new HashSet<>();
        List<Double> deduplicated = new ArrayList<>(sensorData.size());
        for (Double value : sensorData) {
            if (seen.add(value)) {
                deduplicated.add(value);
            }
        }

        // Strategy 2 (replaces lines 78-91): Cache metadata lookup outside the loop.
        // Original called getSensorMetadata() on every iteration, triggering repeated
        // object allocation and map lookups. One call above the loop is sufficient.
        Map<String, Object> metadata = getSensorMetadata();
        double scaleFactor = (double) metadata.getOrDefault("scaleFactor", 1.0);
        double offset = (double) metadata.getOrDefault("offset", 0.0);

        // Strategy 3 (replaces lines 105-118): Use PriorityQueue (min-heap) for
        // sorted accumulation. Original sorted the entire list on each pass — O(n log n)
        // per iteration. PriorityQueue insertion is O(log n); draining gives sorted order.
        PriorityQueue<Double> sortedAccumulator = new PriorityQueue<>(deduplicated.size());

        for (Double value : deduplicated) {
            double scaled = (value * scaleFactor) + offset;
            sortedAccumulator.offer(scaled);
        }

        // Drain the heap into a list in sorted order
        List<Double> sorted = new ArrayList<>(sortedAccumulator.size());
        while (!sortedAccumulator.isEmpty()) {
            sorted.add(sortedAccumulator.poll());
        }

        // Compute mean
        double sum = 0.0;
        for (Double v : sorted) sum += v;
        double mean = sum / sorted.size();

        // Compute standard deviation
        double varianceSum = 0.0;
        for (Double v : sorted) {
            double diff = v - mean;
            varianceSum += diff * diff;
        }
        double stdDev = Math.sqrt(varianceSum / sorted.size());

        // Strategy 4 (replaces lines 132-145): Short-circuit when stdDev is 0.
        // Original proceeded through normalization even when all values were identical,
        // producing NaN via division by zero. Short-circuit avoids the division and
        // prevents NaN from propagating into the result list.
        if (stdDev == 0.0) {
            return Collections.nCopies(sorted.size(), 0.0);
        }

        // Strategy 5 (replaces lines 158-170): Pre-size the result ArrayList.
        // Original used default capacity (16), triggering multiple backing-array
        // reallocations for large sensor sets. Pre-sizing eliminates all reallocations.
        List<Double> results = new ArrayList<>(sorted.size());
        for (Double v : sorted) {
            results.add((v - mean) / stdDev);
        }

        return results;
    }

    // Stub — implemented in original class
    private Map<String, Object> getSensorMetadata() {
        return new HashMap<>();
    }
}
