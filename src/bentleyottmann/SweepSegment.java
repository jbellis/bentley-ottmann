package bentleyottmann;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

final public class SweepSegment extends Segment {

    private double mPosition;

    public SweepSegment(@NotNull Point p1, @NotNull Point p2, @Nullable String name) {
        super(p1, p2, name);
        updatePosition(firstPoint().x);
    }

    public SweepSegment(@NotNull Segment s) {
        this(s.firstPoint(), s.secondPoint(), s.name());
    }

    public double position() {
        return mPosition;
    }

    public void setPosition(double position) {
        mPosition = position;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!(o instanceof SweepSegment)) {
            return false;
        }

        final SweepSegment s = (SweepSegment) o;
        return super.equals(s);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private void updatePosition(double x) {
        final double x1 = firstPoint().x;
        final double y1 = firstPoint().y;
        final double x2 = secondPoint().x;
        final double y2 = secondPoint().y;

        final double y = y1 + ( ((y2 - y1) * (x - x1)) / (x2 - x1) );
        this.setPosition(y);
    }
}