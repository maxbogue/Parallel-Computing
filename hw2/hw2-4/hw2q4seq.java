import java.io.BufferedWriter;
import java.io.FileWriter;

import edu.rit.pj.BarrierAction;
import edu.rit.pj.Comm;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.reduction.SharedIntegerArray;
import edu.rit.util.Random;

/**
 * Compute all partial summations.
 */
public class hw2q4smp {
    
    private hw2q4smp() {}

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        if (args.length != 3) {
            System.out.println("Usage: java hw2q4smp <n> <seed> <outFile>");
        }
        final int n = Integer.parseInt(args[0]);
        long seed = Long.parseLong(args[1]);
        String fileName = args[2];

        final BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Our array of numbers.
        final SharedIntegerArray ns = new SharedIntegerArray(n);
        final SharedIntegerArray ns_new = new SharedIntegerArray(n);

        Random random = Random.getInstance(seed);
        out.write("Original data\n");
        for (int i = 0; i < n; i++) {
            int v = random.nextInt(10);
            ns.set(i, v);
            ns_new.set(i, v);
            out.write(v + " ");
        }
        out.newLine();
        out.newLine();

        new ParallelTeam().execute(new ParallelRegion() {
            public void run() throws Exception {
                int jMax = (int)(Math.log(n) / Math.log(2));
                for (int j_ = 0; j_ <= jMax; j_++) {
                    final int j = j_;
                    execute(0, n - 1, new IntegerForLoop() {
                        public void run(int first, int last) {
                            for (int i = first; i <= last; i++) {
                                int k = i - (int)Math.pow(2, j);
                                if (k >= 0) {
                                    ns_new.addAndGet(i, ns.get(k));
                                }
                            }
                        }
                    }, new BarrierAction() {
                        public void run() throws Exception {
                            for (int i = 0; i < n; i++) {
                                ns.set(i, ns_new.get(i));
                            }
                            out.write(ns.toString());
                            out.newLine();
                        }
                    });
                }
            }
        });

        // Stop timing.
        long t2 = System.currentTimeMillis();

        out.write("\nFinal data\n");
        for (int i = 0; i < n; i++) {
            out.write(ns.get(i) + " ");
        }
        out.newLine();

        System.out.println((t2-t1) + " ms");
        out.close();
    }

}
