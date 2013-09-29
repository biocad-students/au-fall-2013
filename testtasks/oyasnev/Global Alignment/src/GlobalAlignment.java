/**
 * Global alignment with backtracing
 *
 * Author: Oleg Yasnev
 */
public class GlobalAlignment {
    protected String s1, s2;
    protected int n, m;
    protected IMatchScoreMatrix matchScoreMatrix;
    protected int[][] matrix;
    protected char gapSymbol;
    protected int gapScore;
    protected int maxScore;
    protected String[] backtraceStrings;

    /**
     * Aligns strings, max score and performs backtrace
     * @param s1 first string
     * @param s2 second string
     * @param matchScoreMatrix match scoring matrix
     */
    public GlobalAlignment(String s1, String s2, IMatchScoreMatrix matchScoreMatrix) {
        // save gap info
        gapSymbol = matchScoreMatrix.getGapSymbol();
        gapScore = matchScoreMatrix.getGapScore();
        // add empty string to the beginning of every string
        this.s1 = gapSymbol + s1;
        this.s2 = gapSymbol + s2;
        n = this.s1.length();
        m = this.s2.length();
        // save match scoring matrix
        this.matchScoreMatrix = matchScoreMatrix;
        // create score matrix
        matrix = new int[m][n];
        align();
        // save max score
        maxScore = matrix[m-1][n-1];
        backtrace();
    }

    /**
     * Get max score
     * @return max score
     */
    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Get array of two aligned strings
     * @return array of two aligned strings
     */
    public String[] getBacktraceStrings() {
        return backtraceStrings;
    }

    /**
     * Align two strings
     */
    protected void align() {
        // First, align empty string
        // row (insert)
        int score = 0;
        for (int i = 0; i < n; i++) {
            matrix[0][i] = score;
            score += gapScore;
        }
        // column (delete)
        score = gapScore;
        for (int i = 1; i < m; i++) {
            matrix[i][0] = score;
            score += gapScore;
        }

        // align dinamycally
        for (int i = 1; i < m; i++) {
            char ch2 = s2.charAt(i);
            for (int j = 1; j < n; j++) {
                int matchScore = matrix[i-1][j-1] + matchScoreMatrix.getScore(s1.charAt(j), ch2);
                int inScore    = matrix[ i ][j-1] + gapScore;
                int delScore   = matrix[i-1][ j ] + gapScore;
                // choose max score
                matrix[i][j] = Math.max(Math.max(matchScore, inScore), delScore);
            }
        }

        if (Config.DEBUG) {
            // Print score matrix
            StdOut.print("\t");
            for (int j = 0; j < n; j++) {
                StdOut.print(s1.charAt(j) + "\t");
            }
            StdOut.println();
            for (int i = 0; i < m; i++) {
                StdOut.print(s2.charAt(i) + "\t");
                for (int j = 0; j < n; j++) {
                    StdOut.print(matrix[i][j] + "\t");
                }
                StdOut.println();
            }
        }
    }

    /**
     * Backtrace on score matrix
     */
    protected void backtrace() {
        // init
        backtraceStrings = new String[2];
        int i = m - 1;
        int j = n - 1;
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        // while not in a cell (0, 0)
        while (i != 0 || j != 0) {
            int curScore = matrix[i][j];
            char ch1 = s1.charAt(j);
            char ch2 = s2.charAt(i);
            if        (j > 0          && matrix[ i ][j-1] + gapScore == curScore) {
                // insert
                sb1.append(ch1);
                sb2.append(gapSymbol);
                j--;
            } else if (i > 0 && j > 0 && matrix[i-1][j-1] + matchScoreMatrix.getScore(ch1, ch2) == curScore) {
                // match/mismatch
                sb1.append(ch1);
                sb2.append(ch2);
                i--;
                j--;
            } else if (i > 0          && matrix[i-1][ j ] + gapScore == curScore) {
                // delete
                sb1.append(gapSymbol);
                sb2.append(ch2);
                i--;
            } else {
                StdOut.printf("ERROR while backtracing at row index: %d, column index: %d", i, j);
                return;
            }
        }
        // reverse as we went from the end to beginning
        sb1.reverse();
        sb2.reverse();
        backtraceStrings[0] = sb1.toString();
        backtraceStrings[1] = sb2.toString();
    }
}
