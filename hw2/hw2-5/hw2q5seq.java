/**
 * Finds prime numbers using the Sieve of Eratosthenes.
 */
public class hw2q5seq {

    private hw2q5seq() {}

    /**
     * Count the number of primes <= n using the Sieve of Eratosthenes.
     *
     * @param n     The maximum number to check for primality.
     * @return      The number of primes between 2 and n.
     */
    public static int eratosthenes(int n) {

        // Init flag array.
        boolean[] isPrime = new boolean[n + 1];
        for (int i = 0; i <= n; i++) {
            isPrime[i] = true;
        }

        // Perform the Sieve.
        for (int k = 2; k * k <= n; k++) {
            if (isPrime[k]) {
                for (int i = k * k; i <= n; i += k) {
                    isPrime[i] = false;
                }
            }
        }

        // Count the primes remaining.
        int count = 0;
        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) {
                count++;
            }
        }

        return count;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java hw2q5seq <n>");
        }
        int n = Integer.parseInt(args[0]);

        // Start timing.
        long t1 = System.currentTimeMillis();

        int count = eratosthenes(n);

        // Stop timing.
        long t2 = System.currentTimeMillis();

        System.out.println(count + " primes found.");
        System.out.println((t2-t1) + " ms");
    }
}
