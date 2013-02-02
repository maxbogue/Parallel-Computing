import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import edu.rit.image.PJGColorImage;
import edu.rit.image.PJGImage;
import edu.rit.mp.IntegerBuf;
import edu.rit.mp.buf.IntegerMatrixBuf;
import edu.rit.pj.Comm;
import edu.rit.util.Arrays;
import edu.rit.util.Range;

/**
 * Class for cluster version of shifting an image.
 */
public class hw3q2clu {

    public static void main(String[] args) throws Exception {
        Comm.init(args);
        Comm world = Comm.world();
        int size = world.size();
        int rank = world.rank();

        // Process args.
        if (args.length != 4) {
            System.out.println("Usage: java hw3q2clu imageFile dx dy outFile");
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

        // Set up ranges!
        Range[] ranges = new Range(0, h - 1).subranges(size);
        Range range = ranges[rank];
        int lb = range.lb();
        int ub = range.ub();

        // And arrays!
        int[][] n = new int[h][];
        if (rank == 0) {
            Arrays.allocate(n, w);
        } else {
            Arrays.allocate(n, range, w);
        }

        // And buffers to hold them!
        IntegerBuf[] slices = IntegerBuf.rowSliceBuffers(n, ranges);
        IntegerBuf slice = slices[rank];

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Calculate things!
        for (int i = lb; i <= ub; i++) {
            for (int j = 0; j < w; j++) {
                int x = i - dx;
                int y = j - dy;
                n[i - lb][j] = m[(y % h + h) % h][(x % w + w) % w];
            }
        }

        // Rock that communication.
        world.gather(0, slice, slices);

        if (rank == 0) {
            PJGColorImage shifted = new PJGColorImage(h, w, n);
            PJGImage.Writer writer = shifted.prepareToWrite(
                    new BufferedOutputStream(new FileOutputStream(outFile)));
            writer.write();
            writer.close();
        }

        // Stop timing.
        long t2 = System.currentTimeMillis();
        System.out.println((t2-t1) + " ms");
    }

}
