/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 26.11.13
 */
public class ScoreAlignmentMatrixWriter {
    protected Out out;
    protected HomopolymerSequence[] sequences;
    protected int size;
    protected int[][] alignMatrix;
    protected In scoreMatrixIn;
    protected long scoreWidth;

    public ScoreAlignmentMatrixWriter(String outputFilename, HomopolymerSequence[] sequences,
                                      int[][] scoreAlignmentMatrix, int minScore, int maxScore) {
        init(outputFilename, sequences, minScore, maxScore);
        alignMatrix = scoreAlignmentMatrix;
        writePhylipFormat(false);
    }

    public ScoreAlignmentMatrixWriter(String outputFilename, HomopolymerSequence[] sequences,
                                      String scoreAlignmentMatrixFilename, int minScore, int maxScore) {
        init(outputFilename, sequences, minScore, maxScore);
        scoreMatrixIn = new In(scoreAlignmentMatrixFilename);
        writePhylipFormat(true);
        scoreMatrixIn.close();
    }

    protected void init(String outputFilename, HomopolymerSequence[] sequences, int minScore, int maxScore) {
        this.out = new Out(outputFilename);
        this.sequences = sequences;
        this.size = sequences.length;
        if (minScore > 0) minScore = 0;
        scoreWidth = maxScore - minScore;
    }

    protected void writePhylipFormat(boolean largeMode) {
        if (largeMode) {
            saveDistMatrixRowsLargeMode();
        } else {
            saveDistMatrixRows();
        }
    }

    /**
     * Save dist matrix to file row by row
     */
    protected void saveDistMatrixRows() {
        out.println(size);
        double[][] distMatrixRows = new double[1][size];
        for (int i = 0; i < size; i++) {
            fillDistMatrixRow(distMatrixRows[0], alignMatrix[i], i);
            saveDistMatrixRow(distMatrixRows[0], sequences[i]);
        }
    }

    protected void saveDistMatrixRowsLargeMode() {
        out.println(size);
        double[][] distMatrixRows = new double[1][size];
        int[][] alignMatrixRows = new int[1][size];
        for (int i = 0; i < size; i++) {
            readAlignMatrixRow(alignMatrixRows[0]);
            fillDistMatrixRow(distMatrixRows[0], alignMatrixRows[0], i);
            saveDistMatrixRow(distMatrixRows[0], sequences[i]);
        }
    }

    protected void fillDistMatrixRow(double[] distMatrixRow, int[] alignMatrixRow, int rowInd) {
        for (int i = 0; i < size; i++) {
            distMatrixRow[i] = 1.0 - alignMatrixRow[i] * 1.0 / scoreWidth;
        }
        distMatrixRow[rowInd] = 0;  // diagonal dist = 0
    }

    protected void saveDistMatrixRow(double[] distMatrixRow, HomopolymerSequence seq) {
        out.print(seq.getDescription());
        for (int i = 0; i < size; i++) {
            out.printf(" %.6f", distMatrixRow[i]);
        }
        out.println();
    }

    protected void readAlignMatrixRow(int[] alignRow) {
        for (int i = 0; i < size; i++) {
            alignRow[i] = scoreMatrixIn.readInt();
        }
    }
}
