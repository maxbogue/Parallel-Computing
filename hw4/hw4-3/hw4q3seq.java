import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.rit.pj.Comm;

public class hw4q3seq {

    private static Map<State,State> prevs = new HashMap<State,State>();
    private static int[] capacities;

    private static void putInListChecked(List<State> ls, int[] b, State prev) {
        State newState = new State(b);
        if (!prevs.containsKey(newState)) {
            ls.add(newState);
            prevs.put(newState, prev);
        }
    }

    private static List<State> nextStates(State s) {
        int[] buckets = s.buckets;
        int[] b;
        List<State> next = new LinkedList<State>();
        for (int i = 0; i < buckets.length; i++) {

            // Fill i.
            if (buckets[i] != capacities[i]) {
                b = (int[])buckets.clone();
                b[i] = capacities[i];
                putInListChecked(next, b, s);
            }

            // Empty i.
            if (buckets[i] != 0) {
                b = (int[])buckets.clone();
                b[i] = 0;
                putInListChecked(next, b, s);
            }

            // Pour from i to j.
            for (int j = 0; j < buckets.length; j++) {
                if (i == j) continue;
                if (buckets[j] != capacities[j] && buckets[i] != 0) {
                    int v = Math.min(capacities[j] - buckets[j], buckets[i]);
                    b = (int[])buckets.clone();
                    b[i] -= v;
                    b[j] += v;
                    putInListChecked(next, b, s);
                }
            }

        }
        return next;       
    }

    public static void main(String[] args) throws Exception {
        Comm.init(args);

        // Process args.
        if (args.length < 2) {
            System.out.println("Usage: java hw4q3seq <target_amount> <bucket1> [... <bucketk>]");
            System.exit(1);
        }
        // Goal amount to have in a bucket.
        int target = Integer.parseInt(args[0]);
        // The number of buckets.
        int k = args.length - 1;
        // The capacity of each bucket.
        capacities = new int[k];
        for (int i = 0; i < k; i++) {
            capacities[i] = Integer.parseInt(args[i + 1]);
        }

        // Start timing.
        long t1 = System.currentTimeMillis();

        State initial = new State(new int[k]);
        prevs.put(initial, null);

        Queue<State> queue = new LinkedList<State>();
        queue.add(initial);

        State s = null;
        while (!queue.isEmpty()) {
            s = queue.remove();
            if (s.hasBucket(target)) break;
            queue.addAll(nextStates(s));
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Print the result.
        if (s != null && s.hasBucket(target)) {
            printSteps(s);
        } else {
            System.out.println("No solution found.");
        }

        // Print time results.
        System.err.println((t2-t1) + " ms");

    }

    private static void printSteps(State s) {
        State p = prevs.get(s);
        if (p != null) {
            printSteps(p);
        }
        System.out.println(s);
    }

}
