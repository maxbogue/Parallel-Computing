import java.util.Random;

class hw1q1seq {

    public static double[][] randomMatrix(int m, int n, long seed) {
        Random random = new Random(seed);
        double[][] matrix = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = random.nextDouble() * 100;
            }
        }
        return matrix;
    }

    public static void main (String [] args) {
        if (args.length < 3) {
            System.out.println("Usage: java hw1q1seq m n seed");
            System.exit(1);
        }
        int m = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);
        long seed = Long.parseLong(args[2]);
        double[][] grades = randomMatrix(m, n, seed);
    }
}
