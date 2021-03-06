import java.io.PrintStream;

import edu.rit.mp.IntegerBuf;
import edu.rit.pj.Comm;
import edu.rit.pj.reduction.IntegerOp;
import edu.rit.util.Random;
import edu.rit.util.Range;

/**
 * Class for cluster version of the Bellman-Ford-Moore single-source shortest
 * path algorithm.
 */
public class hw4q1clu {

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        Comm world = Comm.world();
        int size = world.size();
        int rank = world.rank();

        // Process args.
        if (args.length != 4) {
            System.out.println("Usage: java hw4q1clu <num_nodes> <seed> <max_distance> <source>");
            System.exit(1);
        }
        int n = Integer.parseInt(args[0]);
        long seed = Long.parseLong(args[1]);
        int maxDistance = Integer.parseInt(args[2]);
        int source = Integer.parseInt(args[3]);

        Random random;

        // Count how many edges there are going to be in the graph.
        int m = 0;
        random = Random.getInstance(seed);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && random.nextInt(maxDistance + 2) > 0) {
                    m++;
                }
            }
        }

        // The graph's adjacency matrix.
        int[][] adjacency = new int[n][n];
        // A list of the edges (u,v pairs).
        int[][] edges = new int[m][2];

        // Generate the graph on each processor.
        random.setSeed(seed);
        int mi = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    // -1 indicates no edge; 0 is an edge length 0.
                    int w = random.nextInt(maxDistance + 2) - 1;
                    if (w >= 0) {
                        adjacency[i][j] = w;
                        edges[mi][0] = i;
                        edges[mi][1] = j;
                        mi++;
                    } else {
                        adjacency[i][j] = -1;
                    }
                }
            }
        }

        // Initialize distances.
        int[] distance = new int[n];
        int[] distanceCopy = new int[n];
        int[] predecessor = new int[n];
        for (int i = 0; i < n; i++) {
            distance[i] = i == source ? 0 : Integer.MAX_VALUE;
            distanceCopy[i] = distance[i];
            predecessor[i] = Integer.MAX_VALUE;
        }

        // Ranges for which edges each processor handles.
        Range[] ranges = new Range(0, m - 1).subranges(size);
        Range range = ranges[rank];

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Create buffers for sharing data.
        IntegerBuf distanceBuf = IntegerBuf.buffer(distance);
        IntegerBuf predecessorBuf = IntegerBuf.buffer(predecessor);

        // Loop n-1 times.
        for (int i = 0; i < n - 1; i++) {
            // For the edges in our range.
            for (int e = range.lb(); e <= range.ub(); e++) {
                int u = edges[e][0];
                int v = edges[e][1];
                int w = adjacency[u][v];
                // Don't have to check for invalid edge; just invalid distance.
                if (distance[u] != Integer.MAX_VALUE) {
                    // We want the lowest number valid predecessor,
                    // for consistency with different #'s of processors.
                    if (distance[u] + w < distance[v] ||
                       (distance[u] + w == distance[v] && predecessor[v] > u))
                    {
                        distance[v] = distance[u] + w;
                        distanceCopy[v] = distance[u] + w;
                        predecessor[v] = u;
                    }
                }
            }
            // Get the new best distances to each node.
            world.allReduce(distanceBuf, IntegerOp.MINIMUM);
            // Invalidate the predecessors for any updated distances.
            for (int u = 0; u < n; u++) {
                if (distance[u] != distanceCopy[u]) {
                    predecessor[u] = Integer.MAX_VALUE;
                    distanceCopy[u] = distance[u];
                }
            }
            // Collect the new predecessors.
            world.allReduce(predecessorBuf, IntegerOp.MINIMUM);
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Write output.
        if (rank == 0) {
            // Print shortest path results.
            System.out.println("Shortest distances:");
            for (int i = 0; i < n; i++) {
                if (distance[i] == Integer.MAX_VALUE) {
                    System.out.printf("%d: inf\n", i);
                } else if (predecessor[i] == Integer.MAX_VALUE) {
                    System.out.printf("%d: %d (source)\n", i, distance[i]);
                } else {
                    System.out.printf("%d: %d (from %d)\n", i, distance[i], predecessor[i]);
                }
            }
            // Print time results.
            System.err.println((t2-t1) + " ms");
        }

    }

}
