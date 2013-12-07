package tools;

import common.interfaces.ISequence;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.Out;

/**
 * Converts score alignment matrix into the distance matrix (in phylip format for ClustalO) and writes into output file
 *      1) In default mode accepts scoreAlignmentMatrix
 *      2) In large mode accepts scoreAlignmentMatrixFile
 *
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 26.11.13
 */
public class ScoreAlignmentMatrixConverter {
    /**
     * Output for saving converted matrix
     */
    protected Out out;

    protected ISequence[] sequences;

    /**
     * Size of the resulting matrix. Equals to number of sequences
     */
    protected int size;

    /**
     * Score alignment matrix. Default mode only
     */
    protected int[][] alignMatrix;

    /**
     * Input for score alignment matrix. Large mode only
     */
    protected In scoreMatrixIn;

    /**
     * score width = maxScore - minScore
     * (for distance calculation)
     */
    protected long scoreWidth;

    /**
     * Constructor for default mode
     * @param outputFilename
     * @param sequences
     * @param scoreAlignmentMatrix
     * @param minScore
     * @param maxScore
     */
    public ScoreAlignmentMatrixConverter(String outputFilename, ISequence[] sequences,
                                         int[][] scoreAlignmentMatrix, int minScore, int maxScore) {
        init(outputFilename, sequences, minScore, maxScore);
        alignMatrix = scoreAlignmentMatrix;
        writePhylipFormat(false);
    }

    /**
     * Constructor for large mode
     * @param outputFilename
     * @param sequences
     * @param scoreAlignmentMatrixFilename
     * @param minScore
     * @param maxScore
     */
    public ScoreAlignmentMatrixConverter(String outputFilename, ISequence[] sequences,
                                         String scoreAlignmentMatrixFilename, int minScore, int maxScore) {
        init(outputFilename, sequences, minScore, maxScore);
        scoreMatrixIn = new In(scoreAlignmentMatrixFilename);
        writePhylipFormat(true);
        scoreMatrixIn.close();
    }

    protected void init(String outputFilename, ISequence[] sequences, int minScore, int maxScore) {
        this.out = new Out(outputFilename);
        this.sequences = sequences;
        this.size = sequences.length;
        if (minScore > 0) minScore = 0;
        scoreWidth = maxScore - minScore;
    }

    protected void writePhylipFormat(boolean largeMode) {
        if (largeMode) {
            writeDistRowsLargeMode();
        } else {
            writeDistRows();
        }
    }

    /**
     * Converts the scoring matrix and writes the distance matrix row by row. Default mode only
     */
    protected void writeDistRows() {
        out.println(size);
        double[][] distRows = new double[1][size];
        for (int i = 0; i < size; i++) {
            fillDistRow(distRows[0], alignMatrix[i], i);
            writeDistRow(distRows[0], sequences[i]);
        }
    }

    /**
     * Converts the scoring matrix and writes the distance matrix row by row. Large mode only
     */
    protected void writeDistRowsLargeMode() {
        out.println(size);
        double[][] distRows = new double[1][size];
        int[][] alignRows = new int[1][size];
        for (int i = 0; i < size; i++) {
            readAlignRow(alignRows[0]);
            fillDistRow(distRows[0], alignRows[0], i);
            writeDistRow(distRows[0], sequences[i]);
        }
    }

    /**
     * Fill one row of the distance matrix
     * @param distRow this row will be filled
     * @param alignRow the row of scoring matrix which will be converted
     * @param rowInd index of the row
     */
    protected void fillDistRow(double[] distRow, int[] alignRow, int rowInd) {
        for (int i = 0; i < size; i++) {
            distRow[i] = 1.0 - alignRow[i] * 1.0 / scoreWidth;
        }
        distRow[rowInd] = 0;  // diagonal dist = 0
    }

    /**
     * Writes one row of the distance matrix into the output file
     * @param distRow
     * @param seq sequence corresponding to the row
     */
    protected void writeDistRow(double[] distRow, ISequence seq) {
        out.print(seq.getDescription());
        for (int i = 0; i < size; i++) {
            out.printf(" %.6f", distRow[i]);
        }
        out.println();
    }

    /**
     * Reads row of score alignment matrix from input file
     * @param alignRow
     */
    protected void readAlignRow(int[] alignRow) {
        for (int i = 0; i < size; i++) {
            alignRow[i] = scoreMatrixIn.readInt();
        }
    }
}
