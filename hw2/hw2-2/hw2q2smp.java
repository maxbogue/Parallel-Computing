import edu.rit.pj.Comm;
import edu.rit.pj.LongForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.reduction.SharedLong;

/**
 * Compute Collatz numbers in parallel.
 */
public class hw2q2smp {
    
    private hw2q2smp() {}

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

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        if (args.length != 2) {
            System.out.println("Usage: java hw2q2smp <n> <max_iter>");
        }
        final long n = Long.parseLong(args[0]);
        final long max_iter = Long.parseLong(args[1]);

        // Start timing.
        long t1 = System.currentTimeMillis();

        final SharedLong count = new SharedLong(0L);
        new ParallelTeam().execute(new ParallelRegion() {
            public void run() throws Exception {
                execute(0, n - 1, new LongForLoop() {
                    long count_t = 0;
                    public void run(long first, long last) {
                        for (long i = first; i <= last; i++) {
                            if (collatz(i, max_iter) == max_iter) {
                                count_t++;
                            }
                        }
                    }
                    public void finish() {
                        count.addAndGet(count_t);
                    }
                });
            }
        });

        // Stop timing.
        long t2 = System.currentTimeMillis();
        System.out.println((t2-t1) + " ms");
        System.out.println(count);
    }

}
