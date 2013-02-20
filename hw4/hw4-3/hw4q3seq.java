import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;

import edu.rit.pj.Comm;

public class hw4q3seq {

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
        int[] capacities = new int[k];
        for (int i = 0; i < k; i++) {
            capacities[i] = Integer.parseInt(args[i + 1]);
        }
        State.capacities = capacities;

        // The initial state of each bucket.
        final int[] emptyBuckets = new int[k];

        // Start timing.
        long t1 = System.currentTimeMillis();

        Set<State> seen = new HashSet<State>();
        Queue<State> queue = new LinkedList<State>();
        queue.add(new State(emptyBuckets));

        State s = null;
        while (!queue.isEmpty()) {
            s = queue.remove();
            if (seen.contains(s)) continue;
            if (s.hasBucket(target)) break;
            seen.add(s);
            queue.addAll(s.nextState());
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Print the result.
        if (s.hasBucket(target)) {
            s.printSteps();
        } else {
            System.out.println("No solution found.");
        }

        // Print time results.
        System.err.println((t2-t1) + " ms");

    }

}
