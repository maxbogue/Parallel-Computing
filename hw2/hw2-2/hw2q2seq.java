/**
 * Attempts to test the validity of the Collatz Conjecture.
 */
public class hw2q2seq {
    
    private hw2q2seq() {}

    /**
     * Computes the number of steps to reach 1 using the Collatz conjecture.
     *
     * @param n         The number to start at.
     * @param max_iter  The maximum number of iterations to try.
     * @return          The number of steps to reach 1, or max_iter.
     */
    public static long collatz(long n, long max_iter) {
        long i = 0;
        while (n > 1 && i < max_iter) {
            if (n % 2 == 0) {
                n = n / 2;
            } else {
                n = n * 3 + 1;
            }
            i++;
        }
        return i;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java hw2q2seq <n> <max_iter>");
        }
        long n = Long.parseLong(args[0]);
        long max_iter = Long.parseLong(args[1]);

        // Start timing.
        long t1 = System.currentTimeMillis();

        long count = 0;
        for (long i = 0; i < n; i++) {
            if (collatz(i, max_iter) == max_iter) {
                count++;
            }
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();
        System.out.println((t2-t1) + " ms");
        System.out.println(count);
    }

}
