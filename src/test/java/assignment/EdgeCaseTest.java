package assignment;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class EdgeCaseTest {
    @Test
    void selectorRejectsInvalidRank() { assertThrows(IllegalArgumentException.class, () -> new DeterministicSelector().select(new int[]{1, 2}, 2)); }
    @Test
    void closestPairRequiresTwoPoints() { assertThrows(IllegalArgumentException.class, () -> new ClosestPairSolver().solve(java.util.List.of(new Point(0, 0)))); }
}
