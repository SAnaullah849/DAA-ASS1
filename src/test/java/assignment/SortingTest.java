package assignment;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SortingTest {
    static Stream<Arguments> arrays() {
        return Stream.of(Arguments.of(new int[]{}), Arguments.of(new int[]{7}), Arguments.of(new int[]{4, 2, 4, 1, 0, -3}), Arguments.of(new int[]{9, 8, 7, 6, 5}), Arguments.of(new int[]{1, 2, 3, 4, 5}));
    }

    @ParameterizedTest
    @MethodSource("arrays")
    void mergeSortMatchesJdk(int[] input) {
        int[] expected = input.clone(); Arrays.sort(expected); new MergeSorter().sort(input); assertArrayEquals(expected, input);
    }

    @ParameterizedTest
    @MethodSource("arrays")
    void quickSortMatchesJdk(int[] input) {
        int[] expected = input.clone(); Arrays.sort(expected); new QuickSorter().sort(input); assertArrayEquals(expected, input);
    }
}
