import edu.rit.pj.Comm;
import edu.rit.util.Random;

/**
 * Class for sequential version of merge sort.
 */
public class hw3q3seq {

    public static int[] merge(int[] a, int[] b) {
        int n = a.length;
        int m = b.length;
        int[] c = new int[n + m];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < n && j < m) {
            if (a[i] < b[j]) {
                c[k++] = a[i++];
            } else {
                c[k++] = b[j++];
            }
        }
        while (i < n) {
            c[k++] = a[i++];
        }
        while (j < m) {
            c[k++] = b[j++];
        }
        return c;
    }

    public static int[] mergeSort(int[] a) {
        int n = a.length;
        if (n <= 1) {
            return a;
        }
        int[] b = new int[n / 2];
        int[] c = new int[n % 2 == 0 ? n / 2 : n / 2 + 1];
        for (int i = 0; i < n / 2; i++) {
            b[i] = a[i];
        }
        for (int i = n / 2; i < n; i++) {
            c[i - n / 2] = a[i];
        }
        return merge(mergeSort(b), mergeSort(c));
    }

    public static void main(String[] args) throws Exception {
        Comm.init(args);

        // Process args.
        if (args.length != 2) {
            System.out.println("Usage: java hw3q3clu N seed");
            System.exit(1);
        }
        final int N = Integer.parseInt(args[0]);
        long seed = Long.parseLong(args[1]);

        // Array of nums!
        int[] nums = new int[N];

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Generate my random numbers.
        Random random = Random.getInstance(seed);
        for (int i = 0; i < nums.length; i++) {
            nums[i] = random.nextInt(2000000) - 1000000;
        }

        // Sorting of the numbers.
        nums = mergeSort(nums);

        // Stop timing.
        long t2 = System.currentTimeMillis();

        printArray(nums);
        System.err.println((t2-t1) + " ms");
    }

    private static void printArray(int[] a) {
        if (a.length == 0) {
            System.out.println("[]");
        } else {
            System.out.print("[" + a[0]);
            for (int i = 1; i < a.length; i++) {
                System.out.print(", " + a[i]);
            }
            System.out.println("]");
        }
    }

}
