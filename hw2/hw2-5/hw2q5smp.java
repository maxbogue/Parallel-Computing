import edu.rit.pj.Comm;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.IntegerSchedule;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.reduction.SharedBooleanArray;

/**
 * Finds prime numbers using the Sieve of Eratosthenes.
 */
public class hw2q5smp {

    private hw2q5smp() {}

    /**
     * Count the number of primes <= n using the Sieve of Eratosthenes.
     *
     * @param n     The maximum number to check for primality.
     * @return      The number of primes between 2 and n.
     */
    public static int eratosthenes(final int n) throws Exception {

        // Init flag array.
        // Note: I decided to not use a SharedBooleanArray because it made the
        // program run 3x slower and there are no race conditions in this
        // algorithm when using a naive array.
        final boolean[] isPrime = new boolean[n + 1];
        for (int i = 0; i <= n; i++) {
            isPrime[i] = true;
        }
        //final SharedBooleanArray isPrime = new SharedBooleanArray(n + 1);
        //final boolean[] test = new boolean[2];
        //for (int i = 0; i <= n; i++) {
            //isPrime.set(i, true);
        //}

        // Perform the Sieve.
        new ParallelTeam().execute(new ParallelRegion() {
            public void run() throws Exception {
                execute(2, (int)Math.sqrt(n), new IntegerForLoop() {
                    public IntegerSchedule schedule() {
                        // Dynamic schedule because we want to do them as close
                        // to sequentially as possible.
                        return IntegerSchedule.dynamic();
                    }
                    public void run(int first, int last) {
                        for (int k = first; k <= last; k++) {
                            if (isPrime[k]) {
                                for (int i = k * k; i <= n; i += k) {
                                    isPrime[i] = false;
                                }
                            }
                        }
                    }
                });
            }
        });

        // Count the primes remaining.
        int count = 0;
        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) {
                count++;
            }
        }

        return count;
    }

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        if (args.length != 1) {
            System.out.println("Usage: java hw2q5smp <n>");
        }
        int n = Integer.parseInt(args[0]);

        // Start timing.
        long t1 = System.currentTimeMillis();

        int count = eratosthenes(n);

        // Stop timing.
        long t2 = System.currentTimeMillis();

        System.out.println(count + " primes found.");
        System.out.println((t2-t1) + " ms");
    }
}
