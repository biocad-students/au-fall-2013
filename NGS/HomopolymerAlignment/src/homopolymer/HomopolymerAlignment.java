package homopolymer;

import edu.princeton.cs.introcs.Out;
import edu.princeton.cs.introcs.StdOut;
import settings.Config;

import java.util.Date;

/**
 * Performs pairwise sequences with homopolymers alignment.
 * Allows multithreading.
 * Two modes:
 *      1) Default. Suitable for medium data. Stores the resulting matrix in memory - available in "alignMatrix".
 *      2) Large. Suitable for large data. Writes the resulting matrix into the output file.
 *
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 20.11.13
 */
public class HomopolymerAlignment {
    /**
     * Size of the resulting matrix. Equals to number of sequences
     */
    public int size;
    /**
     * The resulting matrix. Default mode only.
     */
    public int[][] alignMatrix;
    /**
     * Minimum and maximum score of resultinf matrix
     */
    public int minScore;
    public int maxScore;

    protected HomopolymerSequence[] sequences;
    protected ScoreAlignmentFunction scoreFunction;

    /**
     * Output for saving the resulting matrix. Large mode only
     */
    protected Out out;

    protected int threadCount;

    /**
     * Constructor for default mode
     * @param sequences
     * @param scoreFunction
     * @param threadCount
     */
    public HomopolymerAlignment(HomopolymerSequence[] sequences, ScoreAlignmentFunction scoreFunction, int threadCount) {
        init(sequences, scoreFunction, threadCount);
        align(false);
    }

    /**
     * Constructor for large mode
     * @param sequences
     * @param scoreFunction
     * @param outputFilename
     * @param threadCount
     */
    public HomopolymerAlignment(HomopolymerSequence[] sequences, ScoreAlignmentFunction scoreFunction,
                                String outputFilename, int threadCount) {
        init(sequences, scoreFunction, threadCount);
        this.out = new Out(outputFilename);
        align(true);
        out.close();
    }

    protected void init(HomopolymerSequence[] sequences, ScoreAlignmentFunction scoreFunction, int threadCount) {
        this.sequences = sequences;
        this.size = sequences.length;
        this.scoreFunction = scoreFunction;
        this.threadCount = threadCount;
    }

    protected void align(boolean largeMode) {
        // init
        StdOut.printf("Total sequences: %d (%s)\n", size, new Date());
        maxScore = Integer.MIN_VALUE;
        minScore = Integer.MAX_VALUE;

        if (largeMode) {
            alignLargeMode();
        } else {
            fillAlignMatrix();
        }
    }

    /**
     * Fill alignMatrix. Default mode only
     */
    protected void fillAlignMatrix() {
        alignMatrix = new int[size][size];

        // align only upper triangle
        int i = 0;
        Thread[] threads = new Thread[threadCount];
        while (i < size) {
            int end = Math.min(threadCount, size - i);
            // start threads
            for (int j = 0; j < end; j++) {
                threads[j] = new Thread(
                        new AlignThread(alignMatrix[i + j], sequences[i + j].getHomopolymerSequence(), i + j));
                threads[j].start();
            }
            // waiting for all threads finished
            for (int j = 0; j < end; j++) {
                try {
                    threads[j].join();
                } catch (InterruptedException e) {}
            }
            // process results
            for (int j = 0; j < end; j++) {
                StdOut.printf("Processed %d sequence(s) of %d (%s)\n", i + j + 1, size, new Date());
            }

            i += end;
        }

        // copy to lower triangle
        for (i = 1; i < size; i++) {
            for (int j = 0; j < i; j++) {
                alignMatrix[i][j] = alignMatrix[j][i];
            }
        }
    }

    /**
     * Alignment in large mode only
     */
    protected void alignLargeMode() {
        int[][] alignMatrixRows = new int[threadCount][size];
        Thread[] threads = new Thread[threadCount];

        int i = 0;
        while (i < size) {
            int end = Math.min(threadCount, size - i);
            // start threads
            for (int j = 0; j < end; j++) {
                threads[j] = new Thread(
                        new AlignThread(alignMatrixRows[j], sequences[i + j].getHomopolymerSequence(), 0));
                threads[j].start();
            }
            // waiting for all threads finished
            for (int j = 0; j < end; j++) {
                try {
                    threads[j].join();
                } catch (InterruptedException e) {}
            }
            // process results
            for (int j = 0; j < end; j++) {
                writeAlignRow(alignMatrixRows[j]);
                StdOut.printf("Processed %d sequence(s) of %d (%s)\n", i + j + 1, size, new Date());
            }

            i += end;
        }
    }

    /**
     * Fill one row of the resulting matrix
     * @param alignRow this row will be filled
     * @param seqFirst sequence corresponding to the row
     * @param rowInd   index of the row in the resulting matrix
     */
    protected void alignRow(int[] alignRow, Homopolymer[] seqFirst, int rowInd) {
        for (int i = rowInd; i < size; i++) {
            alignRow[i] = alignPair(seqFirst, sequences[i].getHomopolymerSequence());
        }
    }

    /**
     * Align two sequences
     * @param seqFirst
     * @param seqSecond
     * @return alignment score
     */
    protected int alignPair(Homopolymer[] seqFirst, Homopolymer[] seqSecond) {
        int n = seqFirst.length;
        int m = seqSecond.length;
        int[][] matrix = new int[m+1][n+1];

        // First, align empty string
        matrix[0][0] = 0;
        // row (insert)
        int score = 0;
        for (int i = 1; i <= n; i++) {
            score += scoreFunction.getGapScore(seqFirst[i - 1]);
            matrix[0][i] = score;
        }
        // column (delete)
        score = 0;
        for (int i = 1; i <= m; i++) {
            score += scoreFunction.getGapScore(seqSecond[i - 1]);
            matrix[i][0] = score;
        }

        // align dinamycally
        for (int i = 1; i <= m; i++) {
            Homopolymer hSecond = seqSecond[i-1];
            for (int j = 1; j <= n; j++) {
                Homopolymer hFirst = seqFirst[j-1];
                int matchScore = matrix[i-1][j-1] + scoreFunction.getScore(hFirst, hSecond);
                int inScore    = matrix[ i ][j-1] + scoreFunction.getGapScore(hFirst);
                int delScore   = matrix[i-1][ j ] + scoreFunction.getGapScore(hSecond);
                // choose max score
                matrix[i][j] = Math.max(Math.max(matchScore, inScore), delScore);
            }
        }

        if (Config.DEBUG) {
            String tab = "%5s";
            String gap = "_";
            // Print score matrix
            StdOut.printf(tab, "");
            StdOut.printf(tab, gap); // gap
            for (int j = 0; j < n; j++) {
                StdOut.printf(tab, seqFirst[j]);
            }
            StdOut.println();
            // gap
            StdOut.printf(tab, gap);
            for (int j = 0; j <= n; j++) {
                StdOut.printf(tab, matrix[0][j]);
            }
            StdOut.println();
            for (int i = 1; i <= m; i++) {
                StdOut.printf(tab, seqSecond[i-1]);
                for (int j = 0; j <= n; j++) {
                    StdOut.printf(tab, matrix[i][j]);
                }
                StdOut.println();
            }
            StdOut.println();
        }

        score = matrix[m][n];
        minScore = Math.min(minScore, score);
        maxScore = Math.max(maxScore, score);
        return score;
    }

    /**
     * Write one row of the resulting matrix into the output
     * @param alignRow
     */
    protected void writeAlignRow(int[] alignRow) {
        for (int i = 0; i < size; i++) {
            out.printf(" %d", alignRow[i]);
        }
        out.println();
    }

    /**
     * Thread used in alignment
     */
    protected class AlignThread implements Runnable {
        protected int[] alignRow;
        protected Homopolymer[] seqFirst;
        protected int rowInd;

        /**
         * Constructor
         * @param alignRow one row of the resulting matrix
         * @param seqFirst sequence
         * @param rowInd
         */
        public AlignThread(int[] alignRow, Homopolymer[] seqFirst, int rowInd) {
            this.alignRow = alignRow;
            this.seqFirst = seqFirst;
            this.rowInd = rowInd;
        }

        @Override
        public void run() {
            alignRow(alignRow, seqFirst, rowInd);
        }
    }
}
