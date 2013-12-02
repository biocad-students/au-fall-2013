import java.util.Date;

/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 20.11.13
 */
public class HomopolymerAlignment {
    HomopolymerSequence[] sequences;
    ScoreAlignmentFunction scoreFunction;
    int[][] alignMatrix;

    public HomopolymerAlignment(HomopolymerSequence[] sequences, ScoreAlignmentFunction scoreFunction) {
        this.sequences = sequences;
        this.scoreFunction = scoreFunction;
        align();
    }

    protected void align() {
        int n = sequences.length;
        alignMatrix = new int[n][n];
        StdOut.printf("Total sequences: %d (%s)\n", n, new Date());
        // align self with self = 0
        for (int i = 0; i < n; i++) {
            alignMatrix[i][i] = 0;
        }
        // align only upper triangle
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                alignMatrix[i][j] =
                        alignPair(sequences[i].getHomopolymerSequence(), sequences[j].getHomopolymerSequence());
            }
            StdOut.printf("Processed %d sequence(s) of %d (%s)\n", i + 1, n, new Date());
        }
        // copy to lower triangle
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                alignMatrix[i][j] = alignMatrix[j][i];
            }
        }
    }

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

        return matrix[m][n];
    }
}
