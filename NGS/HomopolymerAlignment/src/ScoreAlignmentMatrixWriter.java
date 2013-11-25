/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 26.11.13
 */
public class ScoreAlignmentMatrixWriter {
    protected Out out;
    protected HomopolymerSequence[] sequences;
    protected int size;
    protected int[][] alignMatrix;
    protected double[][] distMatrix;

    public ScoreAlignmentMatrixWriter(String outputFilename, HomopolymerSequence[] sequences, int[][] scoreAlignmentMatrix) {
        this.out = new Out(outputFilename);
        this.sequences = sequences;
        this.size = sequences.length;
        this.alignMatrix = scoreAlignmentMatrix;
    }

    public void writePhylipFormat() {
        buildDistMatrix();
        saveToFile();
    }

    /**
     * Normalize and convert to distance matrix
     */
    protected void buildDistMatrix() {
        distMatrix = new double[size][size];

        // find min and max values
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                min = (min > alignMatrix[i][j]) ? alignMatrix[i][j] : min;
                max = (max < alignMatrix[i][j]) ? alignMatrix[i][j] : max;
            }
        }
        if (min > 0) {
            min = 0;
        }
        long width = max - min;

        // diagonal dist = 0
        for (int i = 0; i < size; i++) {
            distMatrix[i][i] = 0.0;
        }

        // calc only upper triangle
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                distMatrix[i][j] = 1.0 - alignMatrix[i][j] * 1.0 / width;
            }
        }

        // copy to lower triangle
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < i; j++) {
                distMatrix[i][j] = distMatrix[j][i];
            }
        }
    }

    protected void saveToFile() {
        out.println(size);
        for (int i = 0; i < size; i++) {
            out.print(sequences[i].getDescription());
            for (int j = 0; j < size; j++) {
                out.printf(" %.6f", distMatrix[i][j]);
            }
            out.println();
        }
    }
}
