package assignment;

public final class DeterministicSelector {
    private final Metrics metrics = new Metrics();

    public int select(int[] values, int k) {
        if (values == null || values.length == 0) throw new IllegalArgumentException("values must not be empty");
        if (k < 0 || k >= values.length) throw new IllegalArgumentException("k must be between 0 and n - 1");
        return select(values, 0, values.length - 1, k);
    }

    public Metrics metrics() { return metrics; }

    private int select(int[] values, int low, int high, int rank) {
        metrics.call(); metrics.enter();
        try {
            if (low == high) return values[low];
            int pivotValue = medianOfMedians(values, low, high);
            int pivotIndex = partitionAround(values, low, high, pivotValue);
            if (rank == pivotIndex) return values[pivotIndex];
            if (rank < pivotIndex) return select(values, low, pivotIndex - 1, rank);
            return select(values, pivotIndex + 1, high, rank);
        } finally { metrics.leave(); }
    }

    private int medianOfMedians(int[] values, int low, int high) {
        int count = high - low + 1;
        if (count <= 5) { insertionSort(values, low, high); return values[low + count / 2]; }
        int medianCount = 0;
        for (int start = low; start <= high; start += 5) {
            int end = Math.min(start + 4, high);
            insertionSort(values, start, end);
            int median = start + (end - start) / 2;
            swap(values, low + medianCount++, median);
        }
        return select(values, low, low + medianCount - 1, low + medianCount / 2);
    }

    private int partitionAround(int[] values, int low, int high, int pivotValue) {
        int pivotIndex = low;
        while (values[pivotIndex] != pivotValue) pivotIndex++;
        swap(values, pivotIndex, high);
        int boundary = low;
        for (int index = low; index < high; index++) {
            metrics.comparison();
            if (values[index] < pivotValue) swap(values, boundary++, index);
        }
        swap(values, boundary, high);
        return boundary;
    }

    private void insertionSort(int[] values, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int value = values[i], j = i - 1;
            while (j >= low) {
                metrics.comparison();
                if (values[j] <= value) break;
                values[j + 1] = values[j--];
            }
            values[j + 1] = value;
        }
    }

    private void swap(int[] values, int first, int second) {
        if (first == second) return;
        int temporary = values[first]; values[first] = values[second]; values[second] = temporary;
        metrics.swap();
    }
}
