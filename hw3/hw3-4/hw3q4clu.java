import java.io.PrintStream;

import edu.rit.mp.IntegerBuf;
import edu.rit.pj.Comm;
import edu.rit.util.Random;
import edu.rit.util.Range;

/**
 * Class for cluster version of matrix multiplication.
 */
public class hw3q4clu {

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        Comm world = Comm.world();
        int size = world.size();
        int rank = world.rank();

        // Process args.
        if (args.length != 3) {
            System.out.println("Usage: java hw3q4clu n seed outFileName");
            System.exit(1);
        }
        final int N = Integer.parseInt(args[0]);
        long seed = Long.parseLong(args[1]);
        String outFileName = args[2];

        // Using p as sqrt(size) instead of the total number of processors.
        final int p = (int)Math.sqrt(size);
        // The dimensions of each patch are n x n.
        final int n = N / p;
        // The row index and col index of this patch.
        final int ri = rank / p;
        final int ci = rank % p;
        // Matrices for this processor's patches of A, B, and C.
        int[][] a = new int[n][n];
        int[][] b = new int[n][n];
        int[][] c = new int[n][n];
        // Matrices for the rows of A and cols of B we need.
        int[][] rows = new int[n][N];
        int[][] cols = new int[N][n];
        Range[] ranges = new Range(0, N - 1).subranges(p);

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Generate numbers.
        Random random = Random.getInstance(seed);
        // Jump to the start of this patch of A and generate nums.
        random.skip(n * n * ri * p + n * ci);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = random.nextInt(100);
            }
            // Skip to the next row of this patch.
            random.skip(N - n);
        }
        // Skip to the start of B.
        random.setSeed(seed);
        random.skip(N * N);
        // Jump to the start of this patch of B and generate nums.
        random.skip(n * n * ri * p + n * ci);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                b[i][j] = random.nextInt(100);
            }
            // Skip to the next row of this patch.
            random.skip(N - n);
        }

        // Set up Comm objects for sharing rows and columns.
        Comm rowComm = null, colComm = null;
        for (int i = 0; i < p; i++) {
            if (i == ri) {
                rowComm = world.createComm(true);
            } else {
                world.createComm(false);
            }
            if (i == ci) {
                colComm = world.createComm(true);
            } else {
                world.createComm(false);
            }
        }

        // Buffers for the rows and cols of this patch of C.
        IntegerBuf[] rowSlices = IntegerBuf.colSliceBuffers(rows, ranges);
        IntegerBuf[] colSlices = IntegerBuf.rowSliceBuffers(cols, ranges);

        // Use the row index and col index as tags and gather the data.
        rowComm.allGather(IntegerBuf.buffer(a), rowSlices);
        colComm.allGather(IntegerBuf.buffer(b), colSlices);

        // Compute the final values for this patch of C.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < N; k++) {
                    c[i][j] += rows[i][k] * cols[k][j];
                }
            }
        }

        // Write output.
        if (rank == 0) {
            // Big matrix to gather all the data.
            int[][] C = new int[N][N];
            // Receive all patches of C.
            world.gather(0, IntegerBuf.buffer(c), IntegerBuf.patchBuffers(C, ranges, ranges));
            // Stop timing.
            long t2 = System.currentTimeMillis();
            // Print time results.
            System.out.println((t2-t1) + " ms");
            // Print the matrix to the output file.
            PrintStream out = new PrintStream(outFileName);
            printMatrix(C, out);
            out.close();
        } else {
            // Send this patch of C to process 0.
            world.gather(0, IntegerBuf.buffer(c), null);
        }

    }

    private static void printMatrix(int[][] m) {
        System.out.println();
        printMatrix(m, System.out);
    }

    private static void printMatrix(int[][] m, PrintStream out) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                out.print(m[i][j] + " ");
            }
            out.println();
        }
        out.flush();
    }

}
