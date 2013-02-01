import java.io.BufferedWriter;
import java.io.FileWriter;

import edu.rit.pj.Comm;

/**
 * Class for sequential version of homework 1 question 4.
 */
public class hw3q1seq {

    /** Constant to mod x by for cos. */
    private static final double TAU = 2 * Math.PI;

    /**
     * Calculates the factorial of an integer.
     * Does not handle negative numbers because I don't need it to.
     * 
     * @param n The number to calculate the factorial of.
     * @return  The factorial of n.
     */
    private static double fact(int n) {
        if (n == 0) {
            return 1.0;
        }
        double x = 1.0;
        for (; n > 0; n--) {
            x = x * n;
        }
        return x;
    }

    /**
     * Estimates the cosine of a number.
     *
     * @param x A number.
     * @return  The cosine of x.
     */
    public static double cos(double x) {
        x = x % TAU;
        int n = 0;
        double sum = 0.0;
        double term;
        do {
            term = Math.pow(-1, n) * Math.pow(x, 2 * n) / fact(2 * n);
            sum += term;
            n++;
        } while (Math.abs(term / sum) > 0.001);
        return sum;
    }

    /**
     * Main!
     *
     * @param args ""
     */
    public static void main (String [] args) throws Exception {
        Comm.init(args);
        if (args.length != 1) {
            System.out.println("Usage: java hw3q1seq outFile");
            System.exit(1);
        }
        String fileName = args[0];
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

        // The number of values to check.
        final int n = 150001;

        // The cos results.
        final double[] coss = new double[n];

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Calculate the cos values.
        for (int i = 0; i < n; i++) {
            double x = i / 10.0;
            coss[i] = cos(x);
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Print the results.
        for (int i = 0; i < n; i++) {
            double x = i / 10.0;
            out.write(x + ":\t" + coss[i] + "\n");
        }
        System.out.println((t2-t1) + " ms");
    }

    /**
     * Simple test function to compare the output of cos to the builtin
     * Math.cos.
     *
     * @param x The text value.
     */
    private static void testCos(double x) {
        System.out.println(cos(x));
        System.out.println(Math.cos(x));
    }

}
