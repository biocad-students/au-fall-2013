/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 25.11.13
 */

public class ParamParser {
    public static final String INPUT_FILE_KEY   = "-i";
    public static final String OUTPUT_FILE_KEY  = "-o";
    public static final String SCORE_MATRIX_KEY = "--score-matrix";
    public static final String GAP_SCORE_KEY    = "--gap";

    public boolean state = false;

    public String inputFilename = "";
    public String outputFilename = "";
    public String scoreMatrixFilename = "ScoreMatrix.txt";
    public int gapScore = -4;

    public ParamParser(String[] args) {
        if (args.length == 0 || args[1].equals("--help") || args[1].equals("?")) {
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
        //TODO write help
        StdOut.println("Help");
    }
}
