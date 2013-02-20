import java.io.PrintStream;

import edu.rit.pj.Comm;
import edu.rit.util.Random;

/**
 * Class for sequential version of the Bellman-Ford-Moore single-source shortest
 * path algorithm.
 */
public class hw4q1seq {

    public static void main(String[] args) throws Exception {
        Comm.init(args);

        // Process args.
        if (args.length != 4) {
            System.out.println("Usage: java hw4q1seq <num_nodes> <seed> <max_distance> <source>");
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

        // Generate the graph.
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
        int[] predecessor = new int[n];
        for (int i = 0; i < n; i++) {
            distance[i] = i == source ? 0 : Integer.MAX_VALUE;
            predecessor[i] = Integer.MAX_VALUE;
        }

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Loop n-1 times.
        for (int i = 0; i < n - 1; i++) {
            // For the edges in our range.
            for (int e = 0; e < m; e++) {
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
                        predecessor[v] = u;
                    }
                }
            }
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();

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
