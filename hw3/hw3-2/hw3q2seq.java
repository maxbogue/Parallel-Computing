import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import edu.rit.image.PJGColorImage;
import edu.rit.image.PJGImage;
import edu.rit.pj.Comm;

/**
 * Class for seuential version of shifting an image.
 */
public class hw3q2seq {

    public static void main(String[] args) throws Exception {
        Comm.init(args);

        // Process args.
        if (args.length != 4) {
            System.out.println("Usage: java hw3q2seq imageFile dx dy outFile");
            System.exit(1);
        }
        String imageFile = args[0];
        int dx = Integer.parseInt(args[1]);
        int dy = Integer.parseInt(args[2]);
        String outFile = args[3];

        // Read in the original file.
        PJGColorImage original = new PJGColorImage();
        PJGImage.Reader reader = original.prepareToRead(
                new BufferedInputStream(new FileInputStream(imageFile)));
        reader.read();
        reader.close();

        // Original data.
        final int w = original.getWidth();
        final int h = original.getHeight();
        final int[][] m = original.getMatrix();

        // And arrays!
        int[][] n = new int[h][w];

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Calculate things!
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int x = i - dx;
                int y = j - dy;
                n[i][j] = m[(y % h + h) % h][(x % w + w) % w];
            }
        }

        // Write results.
        PJGColorImage shifted = new PJGColorImage(h, w, n);
        PJGImage.Writer writer = shifted.prepareToWrite(
                new BufferedOutputStream(new FileOutputStream(outFile)));
        writer.write();
        writer.close();

        // Stop timing.
        long t2 = System.currentTimeMillis();
        System.out.println((t2-t1) + " ms");
    }

}
