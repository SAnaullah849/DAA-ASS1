package assignment;

import java.util.Arrays;

public final class MergeSorter {
    private static final int CUTOFF = 16;
    private final Metrics metrics = new Metrics();

    public void sort(int[] values) {
        if (values == null || values.length < 2) return;
        int[] buffer = new int[values.length];
        mergeSort(values, buffer, 0, values.length - 1);
    }

    public Metrics metrics() { return metrics; }

    private void mergeSort(int[] values, int[] buffer, int low, int high) {
        metrics.call(); metrics.enter();
        try {
            if (high - low + 1 <= CUTOFF) {
                insertionSort(values, low, high);
                return;
            }
            int middle = low + (high - low) / 2;
            mergeSort(values, buffer, low, middle);
            mergeSort(values, buffer, middle + 1, high);
            metrics.comparison();
            if (values[middle] <= values[middle + 1]) return;
            merge(values, buffer, low, middle, high);
        } finally { metrics.leave(); }
    }

    private void insertionSort(int[] values, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int value = values[i];
            int j = i - 1;
            while (j >= low) {
                metrics.comparison();
                if (values[j] <= value) break;
                values[j + 1] = values[j]; j--;
            }
            values[j + 1] = value;
        }
    }

    private void merge(int[] values, int[] buffer, int low, int middle, int high) {
        System.arraycopy(values, low, buffer, low, high - low + 1);
        int left = low, right = middle + 1;
        for (int index = low; index <= high; index++) {
            if (left > middle) values[index] = buffer[right++];
            else if (right > high) values[index] = buffer[left++];
            else { metrics.comparison(); values[index] = buffer[left] <= buffer[right] ? buffer[left++] : buffer[right++]; }
        }
    }
}
