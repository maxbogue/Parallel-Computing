import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class State implements java.io.Serializable {

    public final int[] buckets;

    public State(final int[] buckets) {
        this.buckets = buckets;
    }

    public boolean hasBucket(int amount) {
        for (int v : buckets) {
            if (v == amount) {
                return true;
            }
        }
        return false;
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
