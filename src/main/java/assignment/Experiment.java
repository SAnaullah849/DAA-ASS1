package assignment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class Experiment {
    private Experiment() {}

    public static void run(String outputFile) {
        List<String> rows = new ArrayList<>();
        rows.add("algorithm,input_type,n,time_ns,max_recursion_depth,comparisons,swaps,recursive_calls");
        int[] sizes = {100, 1000, 5000, 10000};
        String[] types = {"random", "sorted", "reverse", "duplicate-heavy"};
        for (String type : types) for (int n : sizes) {
            int[] input = makeInput(n, type);
            rows.add(measureMerge(input, type));
            rows.add(measureQuick(input, type));
            if (n <= 5000) rows.add(measureSelect(input, type));
            if (n <= 2000) rows.add(measureClosest(n, type));
        }
        try {
            Path csv = Path.of(outputFile);
            Files.createDirectories(csv.getParent());
            Files.write(csv, rows);
            writePlots(rows, csv.getParent());
            printTerminalPlots(rows);
            System.out.println("Wrote " + (rows.size() - 1) + " measurements to " + csv);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not write experiment results", exception);
        }
    }

    private static String measureMerge(int[] input, String type) {
        MergeSorter sorter = new MergeSorter();
        long start = System.nanoTime(); sorter.sort(input.clone()); long elapsed = System.nanoTime() - start;
        return row("MergeSort", type, input.length, elapsed, sorter.metrics());
    }

    private static String measureQuick(int[] input, String type) {
        QuickSorter sorter = new QuickSorter();
        long start = System.nanoTime(); sorter.sort(input.clone()); long elapsed = System.nanoTime() - start;
        return row("QuickSort", type, input.length, elapsed, sorter.metrics());
    }

    private static String measureSelect(int[] input, String type) {
        DeterministicSelector selector = new DeterministicSelector();
        long start = System.nanoTime(); selector.select(input.clone(), input.length / 2); long elapsed = System.nanoTime() - start;
        return row("DeterministicSelect", type, input.length, elapsed, selector.metrics());
    }

    private static String measureClosest(int n, String type) {
        List<Point> points = new ArrayList<>();
        Random random = new Random(42L + n + type.hashCode());
        for (int i = 0; i < n; i++) {
            double x = type.equals("sorted") ? i : random.nextDouble() * n;
            double y = type.equals("reverse") ? n - i : random.nextDouble() * n;
            if (type.equals("duplicate-heavy")) { x = random.nextInt(Math.max(2, n / 10)); y = random.nextInt(Math.max(2, n / 10)); }
            points.add(new Point(x, y));
        }
        ClosestPairSolver solver = new ClosestPairSolver();
        long start = System.nanoTime(); solver.solve(points); long elapsed = System.nanoTime() - start;
        return row("ClosestPair", type, n, elapsed, solver.metrics());
    }

    private static String row(String algorithm, String type, int n, long time, Metrics metrics) {
        return String.format(Locale.ROOT, "%s,%s,%d,%d,%d,%d,%d,%d", algorithm, type, n, time,
                metrics.maxRecursionDepth(), metrics.comparisons(), metrics.swaps(), metrics.recursiveCalls());
    }

    private static int[] makeInput(int n, String type) {
        int[] values = new int[n]; Random random = new Random(1000L + n + type.hashCode());
        for (int i = 0; i < n; i++) values[i] = type.equals("duplicate-heavy") ? random.nextInt(Math.max(2, n / 20)) : random.nextInt(n * 10 + 1);
        if (type.equals("sorted")) Arrays.sort(values);
        if (type.equals("reverse")) { Arrays.sort(values); for (int i = 0; i < n / 2; i++) { int t = values[i]; values[i] = values[n - 1 - i]; values[n - 1 - i] = t; } }
        return values;
    }

    private static void writePlots(List<String> rows, Path directory) throws IOException {
        writePlot(rows, directory.resolve("time-vs-n.svg"), 3, "Execution time (ns)", "time_ns");
        writePlot(rows, directory.resolve("recursion-depth-vs-n.svg"), 4, "Maximum recursion depth", "max_recursion_depth");
    }

    private static void printTerminalPlots(List<String> rows) {
        String[] algorithms = {"MergeSort", "QuickSort", "DeterministicSelect", "ClosestPair"};
        System.out.println();
        System.out.println("TERMINAL GRAPHS - RANDOM INPUTS");
        System.out.println("Each # is relative to the largest value for that algorithm.");
        printTerminalPlot(rows, algorithms, 3, "Execution time (milliseconds)", true);
        printTerminalPlot(rows, algorithms, 4, "Maximum recursion depth", false);
        System.out.println();
    }

    private static void printTerminalPlot(List<String> rows, String[] algorithms, int column, String title, boolean timeInMilliseconds) {
        System.out.println();
        System.out.println("--- " + title + " ---");
        for (String algorithm : algorithms) {
            double max = 1;
            for (String row : rows) {
                String[] fields = row.split(",");
                if (fields[0].equals(algorithm) && fields[1].equals("random")) max = Math.max(max, Double.parseDouble(fields[column]));
            }
            System.out.println(algorithm + ":");
            for (String row : rows) {
                String[] fields = row.split(",");
                if (!fields[0].equals(algorithm) || !fields[1].equals("random")) continue;
                double value = Double.parseDouble(fields[column]);
                int barLength = Math.max(1, (int) Math.round(value / max * 36));
                String bar = "#".repeat(barLength);
                String displayed = timeInMilliseconds
                        ? String.format(Locale.ROOT, "%.3f ms", value / 1_000_000.0)
                        : String.format(Locale.ROOT, "%.0f", value);
                System.out.printf(Locale.ROOT, "  n=%-5s | %-36s | %s%n", fields[2], bar, displayed);
            }
        }
    }

    private static void writePlot(List<String> rows, Path file, int column, String title, String yLabel) throws IOException {
        StringBuilder svg = new StringBuilder("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"900\" height=\"500\"><rect width=\"100%\" height=\"100%\" fill=\"#fbfaf7\"/><text x=\"40\" y=\"35\" font-size=\"22\" font-family=\"sans-serif\">" + title + "</text>");
        int chartLeft = 80, chartTop = 70, chartWidth = 760, chartHeight = 340;
        svg.append("<line x1=\"80\" y1=\"410\" x2=\"840\" y2=\"410\" stroke=\"#333\"/><line x1=\"80\" y1=\"70\" x2=\"80\" y2=\"410\" stroke=\"#333\"/>");
        String[] colors = {"#126782", "#d05a3a", "#6f8f3d", "#8b5e83"};
        String[] algorithms = {"MergeSort", "QuickSort", "DeterministicSelect", "ClosestPair"};
        double max = 1;
        for (int i = 1; i < rows.size(); i++) max = Math.max(max, Double.parseDouble(rows.get(i).split(",")[column]));
        for (int algorithmIndex = 0; algorithmIndex < algorithms.length; algorithmIndex++) {
            String algorithm = algorithms[algorithmIndex]; StringBuilder points = new StringBuilder();
            for (int i = 1; i < rows.size(); i++) { String[] fields = rows.get(i).split(","); if (!fields[0].equals(algorithm) || !fields[1].equals("random")) continue; double x = chartLeft + Integer.parseInt(fields[2]) / 10000.0 * chartWidth; double y = chartTop + chartHeight - Double.parseDouble(fields[column]) / max * chartHeight; points.append(String.format(Locale.ROOT, "%.1f,%.1f ", x, y)); }
            if (points.length() > 0) svg.append("<polyline fill=\"none\" stroke=\"").append(colors[algorithmIndex]).append("\" stroke-width=\"3\" points=\"").append(points).append("\"/>");
            svg.append("<text x=\"").append(600 + (algorithmIndex % 2) * 120).append("\" y=\"").append(450 + (algorithmIndex / 2) * 20).append("\" fill=\"").append(colors[algorithmIndex]).append("\" font-family=\"sans-serif\" font-size=\"13\">").append(algorithm).append("</text>");
        }
        svg.append("<text x=\"400\" y=\"485\" font-family=\"sans-serif\" font-size=\"14\">Input size n (random inputs)</text><text x=\"15\" y=\"260\" transform=\"rotate(-90 15 260)\" font-family=\"sans-serif\" font-size=\"14\">").append(yLabel).append("</text></svg>");
        Files.writeString(file, svg);
    }
}
