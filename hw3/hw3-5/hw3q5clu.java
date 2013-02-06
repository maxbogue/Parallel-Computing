import java.io.PrintStream;

import edu.rit.mp.IntegerBuf;
import edu.rit.pj.Comm;
import edu.rit.pj.reduction.IntegerOp;
import edu.rit.util.Random;
import edu.rit.util.Range;

/**
 * Class for cluster version of matrix multiplication.
 */
public class hw3q5clu {

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        Comm world = Comm.world();
        int size = world.size();
        int rank = world.rank();

        // Process args.
        if (args.length != 3) {
            System.err.println("Usage: java hw3q5clu n seed outFileName");
            System.exit(1);
        }
        final int N = Integer.parseInt(args[0]);
        long seed = Long.parseLong(args[1]);
        String outFileName = args[2];

        // Using p as cbrt(size) instead of the total number of processors.
        int p = 0;
        for (int i = 1; i < 100; i++) {
            if (i * i * i == size) {
                p = i;
            }
        }
        if (p == 0) {
            System.err.println("The number of processors must be a cubic number!");
            System.exit(1);
        }

        // The dimensions of each patch are n x n.
        final int n = N / p;
        // The row index and col index of this patch.
        final int ri = rank / p % p;
        final int ci = rank % p;
        // The k-index of this processor.
        final int ki = rank / (p * p);
        // Matrices for this processor's patches of A, B, and C.
        int[][] a = new int[n][n];
        int[][] b = new int[n][n];
        int[][] c = new int[n][n];
        Range[] ranges = new Range(0, N - 1).subranges(p);

        // Start timing.
        long t1 = System.currentTimeMillis();

        if (ki == 0) {
            // Generate numbers.
            Random random = Random.getInstance(seed);
            // Jump to the start of this patch of A and generate nums.
            random.skip(n * n * ri * p + n * ci);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    a[i][j] = random.nextInt(200) - 100;
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
                    b[i][j] = random.nextInt(200) - 100;
                }
                // Skip to the next row of this patch.
                random.skip(N - n);
            }
        }

        Comm rowComm = null, colComm = null, kComm = null;
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) {
                if (i == ri && j == ki) {
                    rowComm = world.createComm(true);
                } else {
                    world.createComm(false);
                }
                if (i == ci && j == ki) {
                    colComm = world.createComm(true);
                } else {
                    world.createComm(false);
                }
                if (i == ri && j == ci) {
                    kComm = world.createComm(true);
                } else {
                    world.createComm(false);
                }
            }
        }
        Comm gatherComm = world.createComm(ki == 0);

        IntegerBuf aBuf = IntegerBuf.buffer(a);
        IntegerBuf bBuf = IntegerBuf.buffer(b);
        IntegerBuf cBuf = IntegerBuf.buffer(c);

        // Share cols of A to nodes with ki == ci.
        if (ki == 0 && ci > 0) {
            world.send(ci * p * p + ri * p + ci, aBuf);
        } else if (ki > 0 && ki == ci) {
            world.receive(ri * p + ci, aBuf);
        }

        // Share rows of B to nodes with ki == ri.
        if (ki == 0 && ri > 0) {
            world.send(ri * p * p + ri * p + ci, bBuf);
        } else if (ki > 0 && ki == ri) {
            world.receive(ri * p + ci, bBuf);
        }

        rowComm.broadcast(ki, aBuf);
        colComm.broadcast(ki, bBuf);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        kComm.reduce(0, cBuf, IntegerOp.SUM);

        // Write output.
        if (rank == 0) {
            // Big matrix to gather all the data.
            int[][] C = new int[N][N];
            // Receive reduced patches of C.
            gatherComm.gather(0, cBuf, IntegerBuf.patchBuffers(C, ranges, ranges));
            // Stop timing.
            long t2 = System.currentTimeMillis();
            // Print time results.
            System.out.println((t2-t1) + " ms");
            // Print the matrix to the output file.
            PrintStream out = new PrintStream(outFileName);
            printMatrix(C, out);
            out.close();
        } else if (ki == 0) {
            // Send the reduced portion of C to rank 0.
            gatherComm.gather(0, IntegerBuf.buffer(c), null);
        }

    }

    private static void printMatrix(int[][] m) {
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
