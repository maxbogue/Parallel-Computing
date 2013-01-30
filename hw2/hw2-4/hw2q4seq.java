import java.io.BufferedWriter;
import java.io.FileWriter;
import edu.rit.util.Random;

/**
 * Compute all partial summations.
 */
public class hw2q4seq {
    
    private hw2q4seq() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: java hw2q4seq <n> <seed> <outFile>");
        }
        final int n = Integer.parseInt(args[0]);
        long seed = Long.parseLong(args[1]);
        String fileName = args[2];

        final BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Our array of numbers.
        final int[] ns = new int[n];
        final int[] ns_new = new int[n];

        Random random = Random.getInstance(seed);
        out.write("Original data\n");
        for (int i = 0; i < n; i++) {
            int v = random.nextInt(10);
            ns[i] = v;
            ns_new[i] = v;
            out.write(v + " ");
        }
        out.newLine();
        out.newLine();

        int jMax = (int)(Math.log(n) / Math.log(2));
        for (int j = 0; j <= jMax; j++) {
            for (int i = 0; i < n; i++) {
                int k = i - (int)Math.pow(2, j);
                if (k >= 0) {
                    ns_new[i] += ns[k];
                }
            }
            for (int i = 0; i < n; i++) {
                ns[i] = ns_new[i];
            }
            out.write("[" + ns[0]);
            for (int i = 1; i < n; i++) {
                out.write(", " + ns[i]);
            }
            out.write("]\n");
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();

        out.write("\nFinal data\n");
        for (int i = 0; i < n; i++) {
            out.write(ns[i] + " ");
        }
        out.newLine();

        System.out.println((t2-t1) + " ms");
        out.close();
    }

}
