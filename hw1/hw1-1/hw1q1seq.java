import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class hw1q1seq {

    private static DecimalFormat df = new DecimalFormat("#.##");

    public static class INS implements Comparable<INS> {
        public final int student;
        public final double ins;
        public INS(int student, double ins) {
            this.student = student;
            this.ins = ins;
        }
        public int compareTo(INS o) {
            if (ins < o.ins) {
                return -1;
            } else if (ins == o.ins) {
                return 0;
            } else {
                return 1;
            }
        }
        public String toString() {
            return "#" + student + ": " + df.format(ins);
        }
    }

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

    public static double calculateINS(double[] grades) {
        double total = 0.0;
        for (int i = 0; i < grades.length; i++) {
            total += grades[i] * grades[i];
        }
        return total / grades.length;
    }

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

    public static void main (String [] args) {
        if (args.length < 3) {
            System.out.println("Usage: java hw1q1seq m n seed");
            System.exit(1);
        }
        int m = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);
        long seed = Long.parseLong(args[2]);
        double[][] studentGrades = randomMatrix(m, n, seed);
        List<INS> inss = new ArrayList<INS>(m);
        for (int i = 0; i < m; i++) {
            inss.add(i, new INS(i, calculateINS(studentGrades[i])));
        }
        List<INS> sortedINSs = new ArrayList<INS>(inss);
        Collections.sort(sortedINSs, Collections.reverseOrder());
        System.out.println(sortedINSs);
        double[] fnss = new double[m];
        for (int i = 0; i < m; i++) {
            fnss[sortedINSs.get(i).student] = calculateFNS(i, m);
        }
        for (int i = 0; i < m; i++) {
            System.out.println("#" + (i + 1) + ":\tINS=" + df.format(inss.get(i).ins)
                + "\tFNS=" + fnss[i]);
        }
    }
}
