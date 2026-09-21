package assignment;

public final class Metrics {
    private long comparisons;
    private long swaps;
    private long recursiveCalls;
    private int currentDepth;
    private int maxRecursionDepth;

    public void comparison() { comparisons++; }
    public void swap() { swaps++; }
    public void call() { recursiveCalls++; }
    public void enter() { currentDepth++; maxRecursionDepth = Math.max(maxRecursionDepth, currentDepth); }
    public void leave() { currentDepth--; }
    public long comparisons() { return comparisons; }
    public long swaps() { return swaps; }
    public long recursiveCalls() { return recursiveCalls; }
    public int maxRecursionDepth() { return maxRecursionDepth; }
}
