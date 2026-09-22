docs(report): add analysis and plots
# Assignment 1: Divide and Conquer

## What This Project Does

This is a small Java project for studying how divide-and-conquer algorithms work in theory and in practice. It implements four algorithms, tests them against simple reference solutions, and records timing and operation counts.

The four algorithms are:

1. **Merge sort** with a reusable buffer and a small-input insertion-sort cutoff.
2. **Randomized quicksort** using in-place partitioning and smaller-side recursion.
3. **Deterministic select** using the median-of-medians method and groups of five.
4. **Closest pair of points** using x-sorting, recursion, and a y-sorted strip.

The project uses Java, Maven, JUnit 5, `System.nanoTime()`, and CSV files. The implementation is deliberately straightforward so that the algorithm can be followed easily.

## Folder Layout

```text
assignment1-divide-and-conquer/
├── src/main/java/assignment/   main Java classes
├── src/test/java/assignment/   correctness tests
├── docs/screenshots/           program and test-result images
├── docs/plots/                 report plots
├── results/results.csv         measured results
├── pom.xml                     Maven configuration
└── README.md                   this report
```

## How to Run It

Install JDK 17 or newer and Maven 3.9 or newer. From the project folder, run:

```text
mvn clean test
mvn package
java -cp target/classes assignment.Main
```

The first command runs the tests. The last command runs the experiments and writes:

- `results/results.csv`
- `results/time-vs-n.svg`
- `results/recursion-depth-vs-n.svg`

The test suite has 14 tests. It includes 100 random tests for deterministic select.

## How the Algorithms Work

### Merge Sort

Merge sort splits the array into two halves, sorts both halves, and then merges them. The merge step takes linear time. Small ranges use insertion sort because it is simple and efficient for short arrays.

- Recurrence: $T(n)=2T(n/2)+\Theta(n)$.
- Running time: $\Theta(n\log n)$ by the Master Theorem.
- Extra space: $\Theta(n)$ for the reusable buffer.

### Randomized Quick Sort

Quicksort chooses a random pivot and places smaller values on one side and larger values on the other. The implementation recursively processes the smaller side and uses a loop for the larger side.

- Expected running time: $\Theta(n\log n)$.
- Worst-case running time: $\Theta(n^2)$.
- Stack space: $\Theta(\log n)$ because the smaller partition is handled recursively.

Processing the smaller partition first is useful because it prevents a long chain of recursive calls, even when the partition sizes are unbalanced.

### Deterministic Select

This algorithm finds the element with rank $k$ without sorting the whole array. It sorts groups of five, finds the median of those group medians, and uses that value as the pivot. It then continues only in the part that can contain rank $k$.

- Recurrence: $T(n)\leq T(n/5)+T(7n/10)+\Theta(n)$.
- Running time: $\Theta(n)$ in the worst case.
- Reason for the guarantee: the median-of-medians pivot always removes a fixed fraction of the input.

### Closest Pair of Points

The points are sorted by x-coordinate and split into two halves. The closest pair is found in each half. The algorithm then checks a narrow strip around the dividing line, using points already ordered by y-coordinate.

- Recurrence: $T(n)=2T(n/2)+\Theta(n)$.
- Running time: $\Theta(n\log n)$.
- Extra space: $\Theta(n)$ for temporary point arrays.

This is faster than checking every pair, which would take $\Theta(n^2)$ time.

## Testing

The tests check:

- Empty, one-element, sorted, reverse-sorted, random, and duplicate-heavy arrays.
- Merge sort and quicksort against `Arrays.sort()`.
- 100 random deterministic-select cases against a sorted copy.
- Closest pair against a brute-force $O(n^2)$ solution on small point sets.
- Invalid ranks and too-small point lists.

## Experimental Results

The experiments use sizes 100, 1,000, 5,000, and 10,000 where appropriate. They include random, sorted, reverse-sorted, and duplicate-heavy inputs. Each CSV row records:

- elapsed time in nanoseconds;
- maximum recursion depth;
- comparisons;
- swaps; and
- recursive calls.

Here are the random-input timings currently stored in `results/results.csv`:

| Algorithm | n=100 | n=1,000 | n=5,000 | n=10,000 |
|---|---:|---:|---:|---:|
| Merge sort | 494,800 ns | 917,500 ns | 4,361,700 ns | 3,578,600 ns |
| Quick sort | 661,900 ns | 1,709,800 ns | 2,824,200 ns | 4,553,700 ns |
| Deterministic select | 153,700 ns | 876,900 ns | 1,858,200 ns | not measured |
| Closest pair | 51,116,900 ns | 50,960,000 ns | not measured | not measured |

These are single-run measurements. They can change between runs because of JVM warm-up, JIT compilation, cache effects, garbage collection, and other programs running on the computer. Comparisons and recursion depth are more useful for seeing the general growth pattern.

### Plots

![Execution time versus input size](results/time-vs-n.svg)

![Recursion depth versus input size](results/recursion-depth-vs-n.svg)

Additional output and test-result images are stored in `docs/screenshots/`.

## Discussion

The results generally agree with the theory. Merge sort grows like $n\log n$, deterministic select grows linearly, and closest pair avoids the much slower all-pairs comparison. Quicksort is usually fast, but its exact time changes with the pivot choices and the input arrangement.

Sorted and duplicate-heavy data can change practical performance, especially for quicksort. Random pivots reduce the chance of repeatedly choosing a bad split. Recursing only on the smaller quicksort partition also keeps the stack small.

Median-of-medians is slower than a typical quickselect on some small inputs, but it has a worst-case linear guarantee. Closest pair is much better than $O(n^2)$ for large inputs because it does not compare every possible pair; it only checks a small number of nearby points in the strip.

## Reflection

This assignment helped me connect recurrence analysis with actual program behaviour. Measuring comparisons and recursion depth made the difference between theoretical growth and noisy wall-clock time easier to see. Reusing the merge buffer also showed how a simple memory decision can avoid repeated allocations.

The most difficult part was handling edge cases correctly, especially duplicate values and points with the same x-coordinate. The reference tests were useful because they exposed mistakes that were not obvious from a few manual examples.

## Screenshots and Git History

The `docs/screenshots/` folder contains readable SVG images of the program output and test results. The `docs/plots/` folder contains copies of the generated plots.

The repository history records the main stages of the work:

```text
init: project structure and algorithms
feat(metrics): add performance measurements

```
