package bentleyottmann;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Locale;

public class Point implements Comparable<Point> {
    final public double x;
    final public double y;

    final private static double EPSILON = 1E-12;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof Point)) {
            return false;
        }

        final Point p = (Point) o;
        return Math.abs(x - p.x) < EPSILON && Math.abs(y - p.y) < EPSILON;
    }

    @Override
    public int hashCode() {
        return 31 * Double.valueOf(x).hashCode() + Double.valueOf(y).hashCode();
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[%s, %s]", x, y);
    }

    @Override
    public int compareTo(@NotNull Point p) {
        if (p.x < x || (Math.abs(p.x - x) < EPSILON && p.y < y)) {
            return 1;
        }

        if (p.x > x || (Math.abs(p.x - x) < EPSILON && p.y > y)) {
            return -1;
        }

        return 0;
    }
}