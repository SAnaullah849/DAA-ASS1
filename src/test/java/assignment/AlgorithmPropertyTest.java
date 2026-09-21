package assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.Test;

class AlgorithmPropertyTest {
    @Test
    void deterministicSelectMatchesSortedArrayFor100RandomInputs() {
        Random random = new Random(7);
        for (int test = 0; test < 100; test++) {
            int size = 1 + random.nextInt(100);
            int[] values = random.ints(size, -50, 51).toArray();
            int[] sorted = values.clone(); Arrays.sort(sorted);
            int k = random.nextInt(size);
            assertEquals(sorted[k], new DeterministicSelector().select(values, k));
        }
    }

    @Test
    void closestPairMatchesBruteForce() {
        Random random = new Random(9);
        for (int test = 0; test < 25; test++) {
            Point[] points = new Point[20];
            for (int i = 0; i < points.length; i++) points[i] = new Point(random.nextDouble() * 100, random.nextDouble() * 100);
            double expected = Double.POSITIVE_INFINITY;
            for (int i = 0; i < points.length; i++) for (int j = i + 1; j < points.length; j++) expected = Math.min(expected, points[i].distanceTo(points[j]));
            double actual = new ClosestPairSolver().solve(Arrays.asList(points)).distance();
            assertTrue(Math.abs(expected - actual) < 1e-9);
        }
    }
}
