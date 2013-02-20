import edu.rit.mp.IntegerBuf;
import edu.rit.pj.Comm;
import edu.rit.pj.reduction.IntegerOp;
import edu.rit.util.Random;
import edu.rit.util.Range;

public class hw4q2clu {

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        Comm world = Comm.world();
        int size = world.size();
        int rank = world.rank();

        // Process args.
        if (args.length != 2) {
            System.out.println("Usage: java hw4q2clu <seed> <num_steps>");
            System.exit(1);
        }
        long seed = Long.parseLong(args[0]);
        int n = Integer.parseInt(args[1]);

        // Ranges for which part of the PRNG we use.
        Range[] ranges = new Range(0, n - 1).subranges(size);
        Range range = ranges[rank];
        int lb = range.lb();
        int ub = range.ub();

        // Count movements; NSEW.
        int[] movements = new int[4];

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Our handy-dandy source of randomness.
        Random random = Random.getInstance(seed);
        // Skip to this processor's section.
        random.skip(lb);

        // Perform movements.
        for (int i = lb; i <= ub; i++) {
            double r = random.nextDouble();
            if (r < 0.5) {
                movements[2]++;
            } else if (r < 0.75) {
                movements[3]++;
            } else if (r < 0.875) {
                movements[0]++;
            } else {
                movements[1]++;
            }
        }

        // Gather results.
        world.reduce(0, IntegerBuf.buffer(movements), IntegerOp.SUM);

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Write output.
        if (rank == 0) {
            // Print final location.
            int x = movements[2] - movements[3];
            int y = movements[0] - movements[1];
            System.out.println(x + ", " + y);
            // Print time results.
            System.out.println((t2-t1) + " ms");
        }

    }

}
