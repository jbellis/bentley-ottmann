package bentleyottmann;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.*;

// See:
// http://geomalgorithms.com/a09-_intersect-3.html
// https://en.wikipedia.org/wiki/Bentley%E2%80%93Ottmann_algorithm
// https://www.hackerearth.com/practice/math/geometry/line-intersection-using-bentley-ottmann-algorithm/tutorial/
final public class BentleyOttmann {
    @NotNull
    private final Queue<Event> mEventQueue = new PriorityQueue<>(new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return Double.compare(o1.priority(), o2.priority());
        }
    });

    @NotNull
    private final NavigableSet<SweepSegment> mSweepLine = new TreeSet<>(new Comparator<SweepSegment>() {
        @Override
        public int compare(SweepSegment s1, SweepSegment s2) {
            return Double.compare(s1.position(), s2.position());
        }
    });

    @NotNull
    private final List<Point> mIntersections = new ArrayList<>();

    public void addSegments(@NotNull List<Segment> segments) {
        for (Segment segment : segments) {
            mEventQueue.add(new Event(segment.firstPoint(), new SweepSegment(segment), Event.POINT_LEFT));
            mEventQueue.add(new Event(segment.secondPoint(), new SweepSegment(segment), Event.POINT_RIGHT));
        }
    }

    @NotNull
    public List<Point> findIntersections() {
        while (!mEventQueue.isEmpty()) {
            final Event E = mEventQueue.poll();
            if (E.pointType() == Event.POINT_LEFT) {
                final SweepSegment segE = E.firstSegment();

                addSweepLineStatus(segE);

                final SweepSegment segA = above(segE);
                final SweepSegment segB = below(segE);

                addEventIfIntersection(segE, segA, false, E);
                addEventIfIntersection(segE, segB, false, E);
            } else if (E.pointType() == Event.POINT_RIGHT) {
                final SweepSegment segE = E.firstSegment();
                final SweepSegment segA = above(segE);
                final SweepSegment segB = below(segE);

                removeSweepLineStatus(segE);
                addEventIfIntersection(segA, segB, true, E);
            } else {
                mIntersections.add(E);
                SweepSegment segE1 = E.firstSegment();
                SweepSegment segE2 = E.secondSegment();

                // ensure segE1 is above segE2
                if (!(segE1.position() > segE2.position())) {
                    final SweepSegment swap = segE1;
                    segE1 = segE2;
                    segE2 = swap;
                }

                swap(segE1, segE2);

                final SweepSegment segA = above(segE2);
                final SweepSegment segB = below(segE1);
                addEventIfIntersection(segE2, segA, true, E);
                addEventIfIntersection(segE1, segB, true, E);
            }
        }

        return mIntersections;
    }

    @NotNull
    public List<Point> intersections() {
        return Collections.unmodifiableList(mIntersections);
    }

    public void reset() {
        mIntersections.clear();
        mEventQueue.clear();
        mSweepLine.clear();
    }

    private void addEventIfIntersection(@Nullable SweepSegment s1, @Nullable SweepSegment s2, boolean check, @NotNull Event E) {
        if (s1 != null && s2 != null) {
            final Point i = Segment.intersection(s1, s2);
            // Check if a further event point intersection is ahead
            // relative the current sweep line position at x.
            if (i != null && i.x > E.x) {
                final Event e = new Event(i, s1, s2);
                if (check) {
                    if (mEventQueue.contains(e)) {
                        return;
                    }
                }

                mEventQueue.add(e);
            }
        }
    }

    private void swap(@NotNull SweepSegment s1, @NotNull SweepSegment s2) {
        mSweepLine.remove(s1);
        mSweepLine.remove(s2);

        final double swap = s1.position();
        s1.setPosition(s2.position());
        s2.setPosition(swap);

        mSweepLine.add(s1);
        mSweepLine.add(s2);
    }

    @Nullable
    private SweepSegment above(@NotNull SweepSegment s) {
        return mSweepLine.higher(s);
    }

    @Nullable
    private SweepSegment below(@NotNull SweepSegment s) {
        return mSweepLine.lower(s);
    }

    private void addSweepLineStatus(@NotNull SweepSegment s) {
        mSweepLine.add(s);
    }

    private void removeSweepLineStatus(@NotNull SweepSegment s) {
        mSweepLine.remove(s);
    }
}
