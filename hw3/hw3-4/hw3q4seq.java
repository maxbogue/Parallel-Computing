import java.io.PrintStream;

import edu.rit.pj.Comm;
import edu.rit.util.Random;

/**
 * Class for sequential version of matrix multiplication.
 */
public class hw3q4seq {

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

        final int p = (int)Math.sqrt(size);
        final int n = N / p;
        final int ri = rank / p;
        final int ci = rank % p;
        int[][] A = new int[N][N];
        int[][] B = new int[N][N];
        int[][] C = new int[N][N];

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Generate numbers.
        Random random = Random.getInstance(seed);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = random.nextInt(200) - 100;
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                B[i][j] = random.nextInt(200) - 100;
            }
        }

        // Do the matrix multiplication.
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();
        System.out.println((t2-t1) + " ms");

        // Write output.
        PrintStream out = new PrintStream(outFileName);
        printMatrix(C, out);
        out.close();

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
