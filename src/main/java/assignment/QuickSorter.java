package assignment;

import java.util.concurrent.ThreadLocalRandom;

public final class QuickSorter {
    private final Metrics metrics = new Metrics();

    public void sort(int[] values) {
        if (values == null || values.length < 2) return;
        quickSort(values, 0, values.length - 1);
    }

    public Metrics metrics() { return metrics; }

    private void quickSort(int[] values, int low, int high) {
        while (low < high) {
            metrics.call(); metrics.enter();
            int pivot = partition(values, low, high);
            int leftSize = pivot - low;
            int rightSize = high - pivot;
            if (leftSize < rightSize) {
                quickSort(values, low, pivot - 1);
                low = pivot + 1;
            } else {
                quickSort(values, pivot + 1, high);
                high = pivot - 1;
            }
            metrics.leave();
        }
    }

    private int partition(int[] values, int low, int high) {
        int pivotIndex = ThreadLocalRandom.current().nextInt(low, high + 1);
        swap(values, pivotIndex, high);
        int pivot = values[high];
        int boundary = low;
        for (int index = low; index < high; index++) {
            metrics.comparison();
            if (values[index] <= pivot) swap(values, boundary++, index);
        }
        swap(values, boundary, high);
        return boundary;
    }

    private void swap(int[] values, int first, int second) {
        if (first == second) return;
        int temporary = values[first]; values[first] = values[second]; values[second] = temporary;
        metrics.swap();
    }
}
