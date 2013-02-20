import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class State {

    public static int[] capacities = new int[0];

    public final int[] buckets;
    public final State prev;

    public State(final int[] buckets, State prev) {
        this.buckets = buckets;
        this.prev = prev;
    }

    public State(final int[] buckets) {
        this(buckets, null);
    }

    public List<State> nextState() {
        int[] b;
        List<State> next = new LinkedList<State>();
        for (int i = 0; i < buckets.length; i++) {

            // Fill i.
            if (buckets[i] != capacities[i]) {
                b = (int[])buckets.clone();
                b[i] = capacities[i];
                next.add(new State(b, this));
            }

            // Empty i.
            if (buckets[i] != 0) {
                b = (int[])buckets.clone();
                b[i] = 0;
                next.add(new State(b, this));
            }

            // Pour from i to j.
            for (int j = 0; j < buckets.length; j++) {
                if (i == j) continue;
                if (buckets[j] != capacities[j] && buckets[i] != 0) {
                    int v = Math.min(capacities[j] - buckets[j], buckets[i]);
                    b = (int[])buckets.clone();
                    b[i] -= v;
                    b[j] += v;
                    next.add(new State(b, this));
                }
            }

        }
        return next;       
    }

    public boolean hasBucket(int amount) {
        for (int v : buckets) {
            if (v == amount) {
                return true;
            }
        }
        return false;
    }

    public void printSteps() {
        if (prev != null) {
            prev.printSteps();
        }
        System.out.println(this);
    }

    public int hashCode() {
        return Arrays.hashCode(buckets);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof State)) return false;
        return Arrays.equals(buckets, ((State)obj).buckets);
    }

    public String toString() {
        String s = "";
        for (int v : buckets) {
            s += String.format("% 3d ", v);
        }
        return s;
    }

}
