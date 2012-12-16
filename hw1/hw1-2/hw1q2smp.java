import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.rit.pj.Comm;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;

/**
 * Class for parallel version of homework 1 question 2.
 */
public class hw1q2smp {

    private static int palindromeCount = 0;

    public static boolean isPalindrome(String s) {
        int n = s.length();
        int i = 0;
        int j = n - 1;
        // Find the first and last valid characters.
        while (i < n && !Character.isLetterOrDigit(s.charAt(i))) i++;
        while (j >= 0 && !Character.isLetterOrDigit(s.charAt(j))) j--;
        // While inside bounds and pointers haven't crossed.
        while (i < n && j >= 0 && i <= j) {
            // Test for non-palindromes.
            if (s.charAt(i) != s.charAt(j)) {
                return false;
            }
            // Find the next valid characters for i and j.
            do {
                i++;
            } while (i < n && !Character.isLetterOrDigit(s.charAt(i)));
            do {
                j--;
            } while (j >= 0 && !Character.isLetterOrDigit(s.charAt(j)));
        }
        // No mismatch before i and j cross -> palindrome!
        return true;
    }

    /**
     * Main!
     *
     * @param args  "fileName"
     */
    public static void main (String [] args) throws Exception {
        Comm.init(args);

        // Handle arguments.
        if (args.length < 1) {
            System.out.println("Usage: java hw1q2smp inputFile");
            System.exit(1);
        }
        
        // Read input.
        FileInputStream fis = new FileInputStream(args[0]);
        BufferedReader file = new BufferedReader(new InputStreamReader(fis));
        final int n = Integer.parseInt(file.readLine());
        final String[] list = new String[n];
        for (int i = 0; i < n; i++) {
            list[i] = file.readLine();
        }

        // Start timing.
        long t1 = System.currentTimeMillis();

        palindromeCount = 0;

        // Calculate which strings are palindromes.
        new ParallelTeam().execute(new ParallelRegion() {
            public void run() throws Exception {
                execute(0, n - 1, new IntegerForLoop() {
                    public void run(int first, int last) {
                        for (int i = first; i <= last; i++) {
                            if (isPalindrome(list[i])) {
                                palindromeCount++;
                                System.out.println(list[i]);
                            }
                        }
                    }
                });
            }
        });

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Print the results.
        System.out.println(palindromeCount + " palindromes found.");
        System.out.println((t2-t1) + " ms");
    }

    private static void testPalindrome(String s) {
        System.out.println(s + ": " + isPalindrome(s));
    }

    private static void test() {
        testPalindrome("");
        testPalindrome("a");
        testPalindrome(".");
        testPalindrome("aa");
        testPalindrome(" aa");
        testPalindrome("a a");
        testPalindrome("a a ");
        testPalindrome("aba");
        testPalindrome("ab a");
        testPalindrome(" a ba");
        testPalindrome("ab");
        testPalindrome("abc");
        testPalindrome("never odd or even");
    }
}
