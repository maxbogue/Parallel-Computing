import edu.rit.pj.Comm;
import edu.rit.pj.LongForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.reduction.SharedLongArray;
import edu.rit.util.Random;

/**
 * Computes the location of a randomly moving particle.
 */
public class hw2q3smp {
    
    private hw2q3smp() {}

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        if (args.length != 2) {
            System.out.println("Usage: java hw2q3smp <seed> <n>");
        }
        final long seed = Long.parseLong(args[0]);
        // The number of random movements.
        final long n = Long.parseLong(args[1]);

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Buckets for -x, +x, -y, +y, -z, and +z.
        final SharedLongArray counts = new SharedLongArray(6);

        new ParallelTeam().execute(new ParallelRegion() {
            public void run() throws Exception {
                execute(0, n - 1, new LongForLoop() {
                    Random random_t = Random.getInstance(seed);
                    long[] counts_t = new long[6];
                    public void run(long first, long last) {
                        random_t.setSeed(seed);
                        random_t.skip(first);
                        for (long i = first; i <= last; i++) {
                            int r = random_t.nextInt(Integer.MAX_VALUE) % 6;
                            counts_t[r]++;
                        }
                    }
                    public void finish() {
                        for (int i = 0; i < 6; i++) {
                            counts.addAndGet(i, counts_t[i]);
                        }
                    }
                });
            }
        });

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Calculate results.
        long x = counts.get(1) - counts.get(0);
        long y = counts.get(3) - counts.get(2);
        long z = counts.get(5) - counts.get(4);
        double dist = Math.sqrt(x*x + y*y + z*z);

        // Print results.
        System.out.println("(" + x + " " + y + " " + z + ")");
        System.out.println("distance: " + dist);
        System.out.println((t2-t1) + " ms");
    }

}
