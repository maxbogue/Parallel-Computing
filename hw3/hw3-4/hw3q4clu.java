import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import edu.rit.mp.IntegerBuf;
import edu.rit.mp.buf.IntegerMatrixBuf;
import edu.rit.pj.Comm;
import edu.rit.util.Arrays;
import edu.rit.util.Random;
import edu.rit.util.Range;

/**
 * Class for cluster version of shifting an image.
 */
public class hw3q4clu {

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        Comm world = Comm.world();
        int size = world.size();
        int rank = world.rank();

        // Process args.
        if (args.length != 4) {
            System.out.println("Usage: java hw3q4clu n seed outputFile");
            System.exit(1);
        }
        final int N = Integer.parseInt(args[0]);
        long seed = Long.parseLong(args[1]);
        String outFile = args[2];

        final int p = (int)Math.sqrt(size);
        final int n = N / p;
        final int ri = rank / p;
        final int ci = rank % p;
        int[][] a = new int[N][N];
        int[][] b = new int[N][N];
        int[][] c = new int[N][N];
        Range[] ranges = new Range(0, N - 1).subranges(p);

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Generate numbers.
        Random random = Random.getInstance(seed);
        // Jump to the start of this patch of A and generate nums.
        random.skip(n * n * (ri * p + ci));
        for (int i = ri * n; i < ri * n + n; i++) {
            for (int j = ci * n; j < ci * n + n; j++) {
                a[i][j] = random.nextInt(100);
            }
            // Skip to the next row of this patch.
            random.skip(N - n);
        }
        // Skip to the start of B.
        random.setSeed(seed);
        random.skip(N * N);
        // Jump to the start of this patch of B and generate nums.
        random.skip(n * n * (ri * p + ci));
        for (int i = ri * n; i < ri * n + n; i++) {
            for (int j = ci * n; j < ci * n + n; j++) {
                b[i][j] = random.nextInt(100);
            }
            // Skip to the next row of this patch.
            random.skip(N - n);
        }
        printMatrix(a);
        printMatrix(b);

        IntegerBuf[] aBufs = IntegerBuf.patchBuffers(a, ranges, ranges);
        IntegerBuf[] bBufs = IntegerBuf.patchBuffers(b, ranges, ranges);

        // Use the row index and col index as tags and gather the data.
        world.allGather(ri, aBufs[rank], aBufs);
        world.allGather(ci, bBufs[rank], bBufs);

        for (int i = ri * n; i < ri * n + n; i++) {
            for (int j = ci * n; j < ci * n + n; j++) {
                for (int k = 0; k < N; k++) {
                    c[i][j] = a[i][k] * b[k][j];
                }
            }
        }
        printMatrix(c);

        // Write output.
        if (rank == 0) {
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();
        if (rank == 0) {
            System.out.println((t2-t1) + " ms");
        }
    }

    private static void printMatrix(int[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

}
