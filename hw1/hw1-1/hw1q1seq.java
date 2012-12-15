import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class for sequential version of homework 1.
 */
public class hw1q1seq {

    /** Used for formating decimals to two places. */
    private static DecimalFormat df = new DecimalFormat("0.00");

    /**
     * Inner class to group an INS and a student ID.
     *
     * In a decent programming language, a pair (ins, student) would have sufficed.
     */
    public static class INS implements Comparable<INS> {

        public final int student;
        public final double ins;

        public INS(int student, double ins) {
            this.student = student;
            this.ins = ins;
        }

        /**
         * Compare using ins.
         *
         * @param o The other INS object.
         * @return  The usual.
         */
        public int compareTo(INS o) {
            if (ins < o.ins) {
                return -1;
            } else if (ins == o.ins) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    /**
     * Generates a random two-dimensional matrix of size m x n, using a seed.
     *
     * @param m     The number of rows.
     * @param n     The number of columns.
     * @param seed  A seed for the random number generator.
     * @return      A 2D matrix full of random values.
     */
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

    /**
     * Calculates the INS value for a row of grades.
     *
     * @param grades    The student's grades.
     * @return          The initial normalized score.
     */
    public static double calculateINS(double[] grades) {
        double total = 0.0;
        for (int i = 0; i < grades.length; i++) {
            total += grades[i] * grades[i];
        }
        return total / grades.length;
    }

    /**
     * Calculates the FNS value for a student ranked i/numStudents.
     *
     * @param i             The rank of this student.
     * @param numStudents   The total number of students.
     * @return              The final normalized score.
     */
    public static double calculateFNS(int i, int numStudents) {
        if (i < numStudents * 0.1) {
            return 4.0;
        } else if (i < numStudents * 0.3) {
            return 3.0;
        } else if (i < numStudents * 0.6) {
            return 2.0;
        } else if (i < numStudents * 0.8) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    /**
     * Main!
     *
     * @param args  "m n seed"
     */
    public static void main (String [] args) {

        // Read arguments.
        if (args.length < 3) {
            System.out.println("Usage: java hw1q1seq m n seed");
            System.exit(1);
        }
        int m = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);
        long seed = Long.parseLong(args[2]);

        // Generate the matrix.
        double[][] studentGrades = randomMatrix(m, n, seed);

        // Calculate the INS of each student.
        List<INS> inss = new ArrayList<INS>(m);
        for (int i = 0; i < m; i++) {
            inss.add(i, new INS(i, calculateINS(studentGrades[i])));
        }

        // Sort the INS list.
        List<INS> sortedINSs = new ArrayList<INS>(inss);
        Collections.sort(sortedINSs, Collections.reverseOrder());

        // Calculate the FNS of each student.
        double[] fnss = new double[m];
        for (int i = 0; i < m; i++) {
            // i is the rank from the sorted INS list; we then have to find the
            // matching student ID to store the FNS in the right place.
            fnss[sortedINSs.get(i).student] = calculateFNS(i, m);
        }

        // Print the results.
        for (int i = 0; i < m; i++) {
            System.out.println("#" + (i + 1) + ":\tINS=" + df.format(inss.get(i).ins)
                + "\tFNS=" + fnss[i]);
        }
    }
}
