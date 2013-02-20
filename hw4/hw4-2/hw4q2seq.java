import edu.rit.pj.Comm;
import edu.rit.util.Random;

public class hw4q2seq {

    public static void main(String[] args) throws Exception {
        Comm.init(args);

        // Process args.
        if (args.length != 2) {
            System.out.println("Usage: java hw4q2seq <seed> <num_steps>");
            System.exit(1);
        }
        long seed = Long.parseLong(args[0]);
        int n = Integer.parseInt(args[1]);

        // Count movements; NSEW.
        int[] movements = new int[4];

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Our handy-dandy source of randomness.
        Random random = Random.getInstance(seed);

        // Perform movements.
        for (int i = 0; i < n; i++) {
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

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Print final location.
        int x = movements[2] - movements[3];
        int y = movements[0] - movements[1];
        System.out.println(x + ", " + y);
        // Print time results.
        System.out.println((t2-t1) + " ms");

    }

}
