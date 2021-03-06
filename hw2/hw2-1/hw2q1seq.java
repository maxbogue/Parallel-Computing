//******************************************************************************
//
// This Java source file is copyright (C) 2007 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the Parallel Java Library ("PJ"). PJ is free
// software; you can redistribute it and/or modify it under the terms of the GNU
// General Public License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// PJ is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

import edu.rit.color.HSB;

import edu.rit.image.PJGColorImage;
import edu.rit.image.PJGImage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Class JuliaSetSeq is a sequential program that calculates the Julia Set.
 * <P>
 * Usage: java edu.rit.smp.fractal.JuliaSetSeq <I>width</I> <I>height</I>
 * <I>xcenter</I> <I>ycenter</I> <I>dx</I> <I>dy</I> <I>resolution</I>
 * <I>maxiter</I> <I>gamma</I> <I>filename</I>
 * <BR><I>width</I> = Image width (pixels)
 * <BR><I>height</I> = Image height (pixels)
 * <BR><I>xcenter</I> = X coordinate of center point
 * <BR><I>ycenter</I> = Y coordinate of center point
 * <BR><I>dx</I> = the real component of C
 * <BR><I>dy</I> = the imaginary component of C
 * <BR><I>resolution</I> = Pixels per unit
 * <BR><I>maxiter</I> = Maximum number of iterations
 * <BR><I>gamma</I> = Used to calculate pixel hues
 * <BR><I>filename</I> = PJG image file name
 * <P>
 * The program considers a rectangular region of the complex plane centered at
 * (<I>xcenter,ycenter</I>) of <I>width</I> pixels by <I>height</I> pixels,
 * where the distance between adjacent pixels is 1/<I>resolution</I>. The
 * program takes each pixel's location as a complex number <I>c</I> and performs
 * the following iteration:
 * <P>
 * <I>z</I><SUB>0</SUB> = x + yi
 * <BR><I>z</I><SUB><I>i</I>+1</SUB> = <I>z</I><SUB><I>i</I></SUB><SUP>3</SUP> + <I>C</I>
 * <P>
 * until <I>z</I><SUB><I>i</I></SUB>'s magnitude becomes greater than or equal
 * to 2, or <I>i</I> reaches a limit of <I>maxiter</I>. The complex numbers
 * <I>c</I> where <I>i</I> reaches a limit of <I>maxiter</I> are considered to
 * be in the Julia Set. (Actually, a number is in the Julia Set only
 * if the iteration would continue forever without <I>z</I><SUB><I>i</I></SUB>
 * becoming infinite; the foregoing is just an approximation.) The program
 * creates an image with the pixels corresponding to the complex numbers
 * <I>c</I> and the pixels' colors corresponding to the value of <I>i</I>
 * achieved by the iteration. Following the traditional practice, points in the
 * Julia set are black, and the other points are brightly colored in a
 * range of colors depending on <I>i</I>. The exact hue of each pixel is
 * (<I>i</I>/<I>maxiter</I>)<SUP><I>gamma</I></SUP>. The image is stored in a
 * Parallel Java Graphics (PJG) file specified on the command line.
 * <P>
 * The computation is performed sequentially in a single processor. The program
 * measures the computation's running time, including the time to write the
 * image file. This establishes a benchmark for measuring the computation's
 * running time on a parallel processor.
 *
 * @author  Alan Kaminsky
 * @author  Max Bogue
 * @version 16-Jan-2013
 */
public class hw2q1seq {

// Program shared variables.

    // Command line arguments.
    static int width;
    static int height;
    static double xcenter;
    static double ycenter;
    static double dx;
    static double dy;
    static double resolution;
    static int maxiter;
    static double gamma;
    static File filename;

    // Initial pixel offsets from center.
    static int xoffset;
    static int yoffset;

    // Image matrix.
    static int[][] matrix;
    static PJGColorImage image;

    // Table of hues.
    static int[] huetable;

// Main program.

    /**
     * Julia Set main program.
     */
    public static void main(String[] args) throws Exception {

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Validate command line arguments.
        if (args.length != 10) usage();
        width = Integer.parseInt(args[0]);
        height = Integer.parseInt(args[1]);
        xcenter = Double.parseDouble(args[2]);
        ycenter = Double.parseDouble(args[3]);
        dx = Double.parseDouble(args[4]);
        dy = Double.parseDouble(args[5]);
        resolution = Double.parseDouble(args[6]);
        maxiter = Integer.parseInt(args[7]);
        gamma = Double.parseDouble(args[8]);
        filename = new File(args[9]);

        // Initial pixel offsets from center.
        xoffset = -(width - 1) / 2;
        yoffset = (height - 1) / 2;

        // Create image matrix to store results.
        matrix = new int[height][width];
        image = new PJGColorImage(height, width, matrix);

        // Create table of hues for different iteration counts.
        huetable = new int[maxiter+1];
        for (int i = 0; i < maxiter; ++ i) {
            huetable[i] = HSB.pack
                (/*hue*/ (float) Math.pow (((double)i)/((double)maxiter),gamma),
                 /*sat*/ 1.0f,
                 /*bri*/ 1.0f);
        }
        huetable[maxiter] = HSB.pack(1.0f, 1.0f, 0.0f);

        long t2 = System.currentTimeMillis();

        // Compute all rows and columns.
        for (int r = 0; r < height; ++ r) {
            int[] matrix_r = matrix[r];
            double y_const = ycenter + (yoffset - r) / resolution;

            for (int c = 0; c < width; ++ c) {
                double x = xcenter + (xoffset + c) / resolution;
                double y = y_const;

                // (x + yi)^3 = (x^2 - y^2 + 2xyi)(x + yi)
                // = x^3 - xy^2 + 2x^2yi + x^2yi - y^3i - 2xy^2
                // = x^3 - 3xy^2 + i(3x^2y - y^3)

                // Iterate until convergence.
                int i = 0;
                double x_;
                double y_;
                while (i < maxiter && x*x + y*y <= 4.0) {
                    x_ = (x * x * x) - (3 * x * y * y) + dx;
                    y_ = (3 * x * x * y) - (y * y * y) + dy;
                    x = x_;
                    y = y_;
                    i++;
                }

                // Record number of iterations for pixel.
                matrix_r[c] = huetable[i];
            }
        }

        long t3 = System.currentTimeMillis();

        // Write image to PJG file.
        PJGImage.Writer writer =
            image.prepareToWrite
                (new BufferedOutputStream
                    (new FileOutputStream(filename)));
        writer.write();
        writer.close();

        // Stop timing.
        long t4 = System.currentTimeMillis();
        System.out.println((t2-t1) + " msec pre");
        System.out.println((t3-t2) + " msec calc");
        System.out.println((t4-t3) + " msec post");
        System.out.println((t4-t1) + " msec total");
    }

// Hidden operations.

    /**
     * Print a usage message and exit.
     */
    private static void usage() {
        System.err.println("Usage: java hw2q1seq <width> <height> <xcenter> <ycenter> <dx> <dy> <resolution> <maxiter> <gamma> <filename>");
        System.err.println("<width> = Image width (pixels)");
        System.err.println("<height> = Image height (pixels)");
        System.err.println("<xcenter> = X coordinate of center point");
        System.err.println("<ycenter> = Y coordinate of center point");
        System.err.println("<dx> = real component of C");
        System.err.println("<dy> = imaginary component of C");
        System.err.println("<resolution> = Pixels per unit");
        System.err.println("<maxiter> = Maximum number of iterations");
        System.err.println("<gamma> = Used to calculate pixel hues");
        System.err.println("<filename> = PJG image file name");
        System.exit(1);
    }

}
