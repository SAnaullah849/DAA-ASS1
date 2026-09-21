package assignment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClosestPairSolver {
    public record Result(Point first, Point second, double distance) {}
    private final Metrics metrics = new Metrics();

    public Result solve(List<Point> points) {
        if (points == null || points.size() < 2) throw new IllegalArgumentException("at least two points are required");
        Point[] byX = points.toArray(Point[]::new);
        Arrays.sort(byX, Comparator.comparingDouble(Point::x).thenComparingDouble(Point::y));
        Point[] byY = byX.clone();
        Arrays.sort(byY, Comparator.comparingDouble(Point::y).thenComparingDouble(Point::x));
        return closest(byX, byY, 0, byX.length);
    }

    public Metrics metrics() { return metrics; }

    private Result closest(Point[] byX, Point[] byY, int start, int length) {
        metrics.call(); metrics.enter();
        try {
            if (length <= 3) return bruteForce(byX, start, length);
            int half = length / 2;
            double splitX = byX[start + half].x();
            Point[] leftY = new Point[half]; Point[] rightY = new Point[length - half];
            Map<Point, Integer> leftCounts = new HashMap<>();
            for (int index = start; index < start + half; index++) leftCounts.merge(byX[index], 1, Integer::sum);
            int leftCount = 0, rightCount = 0;
            for (Point point : byY) {
                Integer occurrences = leftCounts.get(point);
                if (occurrences != null && occurrences > 0) {
                    leftY[leftCount++] = point;
                    if (occurrences == 1) leftCounts.remove(point); else leftCounts.put(point, occurrences - 1);
                } else rightY[rightCount++] = point;
            }
            Result left = closest(byX, leftY, start, half);
            Result right = closest(byX, rightY, start + half, length - half);
            Result best = left.distance() <= right.distance() ? left : right;
            Point[] strip = new Point[length]; int stripSize = 0;
            for (Point point : byY) if (Math.abs(point.x() - splitX) < best.distance()) strip[stripSize++] = point;
            for (int i = 0; i < stripSize; i++) {
                for (int j = i + 1; j < stripSize && strip[j].y() - strip[i].y() < best.distance(); j++) {
                    metrics.comparison();
                    double distance = strip[i].distanceTo(strip[j]);
                    if (distance < best.distance()) best = new Result(strip[i], strip[j], distance);
                }
            }
            return best;
        } finally { metrics.leave(); }
    }

    private Result bruteForce(Point[] points, int start, int length) {
        Result best = new Result(points[start], points[start + 1], points[start].distanceTo(points[start + 1]));
        for (int i = start; i < start + length; i++) for (int j = i + 1; j < start + length; j++) {
            metrics.comparison();
            double distance = points[i].distanceTo(points[j]);
            if (distance < best.distance()) best = new Result(points[i], points[j], distance);
        }
        return best;
    }
}
