import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.StdOut;

/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 25.11.13
 */

public class ParamParser {
    public static final String INPUT_FILE_KEY   = "-i";
    public static final String OUTPUT_FILE_KEY  = "-o";
    public static final String SCORE_MATRIX_KEY = "--score-matrix";
    public static final String GAP_SCORE_KEY    = "--gap";
    public static final String LARGE_MODE_KEY   = "--large";
    public static final String THREADS_KEY      = "--threads";

    public static final String README = "readme.txt";

    public boolean state = false;

    public String inputFilename = "";
    public String outputFilename = "";
    public String scoreMatrixFilename = "ScoreMatrix.txt";
    public int gapScore = -4;
    public boolean largeMode = false;
    public int threadCount = 1;

    public ParamParser(String[] args) {
        if (args.length == 0 || args[0].equals("--help") || args[0].equals("?") || args[0].equals("-h")) {
            printHelp();
            return;
        }

        for (int i = 0; i < args.length; i++) {
            String key = args[i];
            if        (key.equals(INPUT_FILE_KEY)) {
                i++;
                inputFilename = args[i];
            } else if (key.equals(OUTPUT_FILE_KEY)) {
                i++;
                outputFilename = args[i];
            } else if (key.equals(SCORE_MATRIX_KEY)) {
                i++;
                scoreMatrixFilename = args[i];
            } else if (key.equals(GAP_SCORE_KEY)) {
                i++;
                gapScore = Integer.parseInt(args[i]);
            } else if (key.equals(LARGE_MODE_KEY)) {
                largeMode = true;
            } else if (key.equals(THREADS_KEY)) {
                i++;
                threadCount = Integer.parseInt(args[i]);
            }
        }

        // check state
        state = true;
        if (inputFilename.isEmpty()) {
            state = false;
            StdOut.println("Input file must be specified");
        }
        if (outputFilename.isEmpty()) {
            state = false;
            StdOut.println("Output file must be specified");
        }

        if (!state) {
            StdOut.println("For more information run the program with the key '--help'");
        }
    }

    public static void printHelp() {
        In in = new In(README);
        while (!in.isEmpty()) {
            StdOut.println(in.readLine());
        }
    }
}
