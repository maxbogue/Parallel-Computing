import edu.rit.util.Random;

/**
 * Computes the location of a randomly moving particle.
 */
public class hw2q3seq {
    
    private hw2q3seq() {}

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java hw2q3seq <seed> <n>");
        }
        long seed = Long.parseLong(args[0]);
        // The number of random movements.
        long n = Long.parseLong(args[1]);

        // Start timing.
        long t1 = System.currentTimeMillis();

        Random random = Random.getInstance(seed);
        // Buckets for -x, +x, -y, +y, -z, and +z.
        long[] counts = new long[6];
        for (long i = 0; i < n; i++) {
            int r = random.nextInt(Integer.MAX_VALUE) % 6;
            counts[r]++;
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();

        long x = counts[1] - counts[0];
        long y = counts[3] - counts[2];
        long z = counts[5] - counts[4];
        double dist = Math.sqrt(x*x + y*y + z*z);
        System.out.println("(" + x + " " + y + " " + z + ")");
        System.out.println("distance: " + dist);
        System.out.println((t2-t1) + " ms");
    }

}
