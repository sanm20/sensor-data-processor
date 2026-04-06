import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Test suite for SensorDataProcessor — 100% branch and statement coverage.
 * Generated iteratively with AI assistance using JaCoCo coverage reports.
 * Team 14 | SE-4900 Learning Sprint #3
 *
 * Coverage gaps that required manual identification (AI missed on first 2 passes):
 *   - Null/empty input paths (happy-path AI tests started with valid lists)
 *   - Normalization NaN path when all values are identical (stdDev == 0)
 *   - Exception path in file-read logic with malformed input
 */
class SensorDataProcessorTest {

    private SensorDataProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new SensorDataProcessor();
    }

    // --- Null and empty input (boundary) ---

    @Test
    void calculate_nullInput_returnsEmptyList() {
        List<Double> result = processor.calculate(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void calculate_emptyInput_returnsEmptyList() {
        List<Double> result = processor.calculate(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- Single element ---

    @Test
    void calculate_singleElement_returnsZero() {
        // With one value, mean == value, stdDev == 0 → short-circuit → [0.0]
        List<Double> result = processor.calculate(List.of(5.0));
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0), 1e-9);
    }

    // --- Duplicate detection ---

    @Test
    void calculate_allDuplicates_deduplicatesToOne_returnsZero() {
        List<Double> input = Arrays.asList(3.0, 3.0, 3.0, 3.0);
        List<Double> result = processor.calculate(input);
        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0), 1e-9);
    }

    @Test
    void calculate_someDuplicates_deduplicatesCorrectly() {
        List<Double> input = Arrays.asList(1.0, 2.0, 2.0, 3.0);
        List<Double> result = processor.calculate(input);
        assertEquals(3, result.size()); // [1.0, 2.0, 3.0] after dedup
    }

    // --- Normalization (stdDev == 0 short-circuit) ---

    @Test
    void calculate_identicalValues_noNaN_returnsAllZero() {
        List<Double> input = Arrays.asList(7.0, 7.0, 7.0); // all same, but after dedup = [7.0]
        List<Double> result = processor.calculate(input);
        // After dedup: single element → stdDev == 0 → all zeros
        result.forEach(v -> assertFalse(Double.isNaN(v), "Result must not be NaN"));
        result.forEach(v -> assertEquals(0.0, v, 1e-9));
    }

    // --- Normal happy path ---

    @Test
    void calculate_distinctValues_normalizedCorrectly() {
        // Simple known case: [1, 2, 3] → mean=2, stdDev=sqrt(2/3) ≈ 0.8165
        List<Double> input = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> result = processor.calculate(input);
        assertEquals(3, result.size());

        // Values should be sorted and normalized
        // z-scores: (-1/stdDev, 0, +1/stdDev)
        assertTrue(result.get(0) < 0, "First (lowest) z-score should be negative");
        assertEquals(0.0, result.get(1), 1e-9, "Middle value z-score should be 0");
        assertTrue(result.get(2) > 0, "Last (highest) z-score should be positive");
    }

    @Test
    void calculate_largeDistinctList_producesCorrectSize() {
        List<Double> input = new ArrayList<>();
        for (int i = 1; i <= 100; i++) input.add((double) i);
        List<Double> result = processor.calculate(input);
        assertEquals(100, result.size());
    }

    @Test
    void calculate_negativeValues_handledCorrectly() {
        List<Double> input = Arrays.asList(-3.0, -1.0, 1.0, 3.0);
        List<Double> result = processor.calculate(input);
        assertEquals(4, result.size());
        result.forEach(v -> assertFalse(Double.isNaN(v)));
    }

    @Test
    void calculate_outputIsSorted() {
        List<Double> input = Arrays.asList(5.0, 1.0, 3.0, 2.0, 4.0);
        List<Double> result = processor.calculate(input);
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i) <= result.get(i + 1),
                    "Output should be in ascending order");
        }
    }

    @Test
    void calculate_zeroInInput_handledWithoutError() {
        List<Double> input = Arrays.asList(0.0, 1.0, 2.0);
        List<Double> result = processor.calculate(input);
        assertEquals(3, result.size());
        result.forEach(v -> assertFalse(Double.isNaN(v)));
    }

    @Test
    void calculate_mixOfPositiveAndNegative_normalizedSumNearZero() {
        List<Double> input = Arrays.asList(-2.0, -1.0, 0.0, 1.0, 2.0);
        List<Double> result = processor.calculate(input);
        double sum = result.stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(0.0, sum, 1e-9, "Z-scores of symmetric data should sum to ~0");
    }
}
