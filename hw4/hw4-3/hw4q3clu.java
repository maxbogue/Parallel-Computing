import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.rit.mp.IntegerBuf;
import edu.rit.mp.ObjectBuf;
import edu.rit.mp.buf.ObjectItemBuf;
import edu.rit.pj.Comm;
import edu.rit.pj.CommStatus;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelSection;
import edu.rit.pj.ParallelTeam;
import edu.rit.util.Range;

public class hw4q3clu {

    private static final int CHUNK_SIZE = 1024;
    private static final int M = 0;
    private static final int W = 0;

    private static Map<State,State> prevs = new HashMap<State,State>();
    private static Map<State,State> newPrevs = new HashMap<State,State>();

    private static Comm world;
    private static int size;
    private static int rank;

    private static int target;
    private static int k;
    private static int[] capacities;
    private static long t1;

    private static void putInListChecked(List<State> ls, int[] b, State prev) {
        State newState = new State(b);
        if (!prevs.containsKey(newState) && !newPrevs.containsKey(newState)) {
            ls.add(newState);
            prevs.put(newState, prev);
            newPrevs.put(newState, prev);
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
        world = Comm.world();
        size = world.size();
        rank = world.rank();

        // Process args.
        if (args.length < 2) {
            System.out.println("Usage: java hw4q3clu <target_amount> <bucket1> [... <bucketk>]");
            System.exit(1);
        }
        // Goal amount to have in a bucket.
        target = Integer.parseInt(args[0]);
        // The number of buckets.
        k = args.length - 1;
        // The capacity of each bucket.
        capacities = new int[k];
        for (int i = 0; i < k; i++) {
            capacities[i] = Integer.parseInt(args[i + 1]);
        }

        // The initial state of each bucket.
        final int[] emptyBuckets = new int[k];

        // Start timing.
        t1 = System.currentTimeMillis();

        if (rank == 0) {
            new ParallelTeam(2).execute(new ParallelRegion() {
                public void run() throws Exception {
                    execute(new ParallelSection() {
                        public void run() throws Exception {
                            master();
                        }
                    }, new ParallelSection() {
                        public void run() throws Exception {
                            worker(target);
                        }
                    });
                }
            });
        } else {
            worker(target);
        }

    }

    private static Queue<State> splitChunk(Queue<State> queue, int n) {
        Queue<State> newQueue = new LinkedList<State>();
        for (int i = 0; i < n && !queue.isEmpty(); i++) {
            newQueue.add(queue.remove());
        }
        return newQueue;
    }

    private static void master() throws Exception {

        Map<State,State> masterPrevs = new HashMap<State,State>();
        Queue<State> masterQueue = new LinkedList<State>();
        State initial = new State(new int[k]);
        masterPrevs.put(initial, null);
        masterQueue.addAll(nextStates(initial));

        // Figure out how many each subprocess gets.
        Range[] ranges;
        if (masterQueue.size() / size < CHUNK_SIZE) {
            ranges = new Range(0, masterQueue.size() - 1).subranges(size);
        } else {
            ranges = new Range(0, size * CHUNK_SIZE - 1).subranges(size);
        }

        // Send the states to the subprocesses.
        for (int i = 0; i < size; i++) {
            Queue<State> queueChunk = splitChunk(masterQueue, ranges[i].length());
            world.send(i, W, ObjectBuf.buffer(queueChunk));
        }

        ObjectItemBuf<State> inSolutionBuf = ObjectBuf.buffer();
        ObjectItemBuf<Queue<State>> inQueueBuf = ObjectBuf.buffer();
        ObjectItemBuf<Map<State,State>> inPrevsBuf = ObjectBuf.buffer();
        State solution = null;
        int workerCount = size;

        boolean[] empty = new boolean[size];

        do {

            CommStatus status = world.receive(null, M, inSolutionBuf);
            world.receive(status.fromRank, M, inQueueBuf);
            world.receive(status.fromRank, M, inPrevsBuf);

            solution = inSolutionBuf.item;
            masterQueue.addAll(inQueueBuf.item);
            for (Map.Entry<State,State> entry : inPrevsBuf.item.entrySet()) {
                if (!masterPrevs.containsKey(entry.getKey())) {
                    masterPrevs.put(entry.getKey(), entry.getValue());
                }
            }

            Queue<State> queueChunk;
            boolean allEmpty = true;
            for (int i = 0; i < size; i++) {
                allEmpty = allEmpty && empty[i];
            }
            if (solution == null && !allEmpty) {
                queueChunk = splitChunk(masterQueue, CHUNK_SIZE);
            } else {
                queueChunk = null;
                workerCount--;
            }
            world.send(status.fromRank, W, ObjectBuf.buffer(queueChunk));
            if (queueChunk == null || queueChunk.isEmpty()) {
                empty[status.fromRank] = true;
            } else {
                for (int i = 0; i < size; i++) {
                    empty[i] = false;
                }
            }


        } while (workerCount > 0);

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Print the result.
        if (solution != null) {
            printSteps(solution, masterPrevs);
        } else {
            System.out.println("No solution found.");
        }

        // Print time results.
        System.err.println((t2-t1) + " ms");

    }

    private static void worker(int target) throws Exception {

        State initial = new State(new int[k]);
        prevs.put(initial, null);

        Queue<State> queue;
        Queue<State> newQueue = new LinkedList<State>();

        ObjectItemBuf<Queue<State>> queueBuf = ObjectBuf.buffer();

        do {

            world.receive(0, W, queueBuf);
            queue = queueBuf.item;
            if (queue == null) break;

            newPrevs.clear();
            newQueue.clear();

            State s = null;
            while (!queue.isEmpty()) {
                s = queue.remove();
                if (s.hasBucket(target)) break;
                newQueue.addAll(nextStates(s));
            }

            if (s != null && s.hasBucket(target)) {
                world.send(0, M, ObjectBuf.buffer(s));
            } else {
                world.send(0, M, ObjectBuf.emptyBuffer());
            }
            world.send(0, M, ObjectBuf.buffer(newQueue));
            world.send(0, M, ObjectBuf.buffer(newPrevs));

        } while (true);

    }

    private static void printSteps(State s, Map<State,State> ps) {
        State p = ps.get(s);
        if (p != null) {
            printSteps(p, ps);
        }
        System.out.println(s);
    }

}
