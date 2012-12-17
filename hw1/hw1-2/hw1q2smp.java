import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.rit.pj.Comm;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;

/**
 * Class for parallel version of homework 1 question 2.
 */
public class hw1q2smp {

    /**
     * Determines whether or not a string is a palindrome.
     *
     * @param s     The string to test.
     * @return      Whether s is a palindrome.
     */
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
     * @param args "fileName"
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

        // Need a thread-safe structure to collect the palindromes in.
        final Collection<String> palindromes = new ConcurrentLinkedQueue<String>();

        // Calculate which strings are palindromes.
        new ParallelTeam().execute(new ParallelRegion() {
            public void run() throws Exception {
                execute(0, n - 1, new IntegerForLoop() {
                    public void run(int first, int last) {
                        for (int i = first; i <= last; i++) {
                            if (isPalindrome(list[i])) {
                                palindromes.add(list[i]);
                            }
                        }
                    }
                });
            }
        });

        // Stop timing.
        long t2 = System.currentTimeMillis();

        // Print the results.
        for (String s : palindromes) {
            System.out.println(s);
        }
        System.out.println(palindromes.size() + " palindromes found.");
        System.out.println((t2-t1) + " ms");
    }

    /**
     * Test helper function
     *
     * @param s The string to test.
     */
    private static void testPalindrome(String s) {
        System.out.println(s + ": " + isPalindrome(s));
    }

    /**
     * Runs some sanity checks.
     */
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
