import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import edu.rit.image.PJGColorImage;
import edu.rit.image.PJGImage;
import edu.rit.mp.buf.IntegerMatrixBuf;
import edu.rit.pj.Comm;
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

        final int w = original.getWidth();
        final int h = original.getHeight();
        final int[][] m = original.getMatrix();

        Range range = new Range(0, h - 1).subranges(size)[rank];
        int lb = range.lb();
        int ub = range.ub();

        int[][] n  = new int[ub - lb + 1][w];

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Calculate things!
        for (int i = lb; i <= ub; i++) {
            for (int j = 0; j < w; j++) {
                n[i - lb][j] = m[(i - dx) % w][(j - dy) % h];
            }
        }

        IntegerMatrixBuf[] bufs = null;
        if (rank == 0) {
            bufs = new IntegerMatrixBuf[size];
        }
        world.gather(0, IntegerMatrixBuf.buffer(n), bufs);
        if (rank == 0) {
            int[][] result = new int[h][w];
            int r = 0;
            for (IntegerMatrixBuf buf : bufs) {
                for (int i = 0; i < buf.length(); i++) {
                    result[r + i / w][i % w] = buf.get(i);
                }
                r += buf.length() / w;
            }
            PJGColorImage shifted = new PJGColorImage(h, w, result);
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
