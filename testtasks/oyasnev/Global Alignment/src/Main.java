/**
 * Global Alignment with Scoring Matrix (with backtracing)
 * http://rosalind.info/problems/glob/
 *
 * Requirements:
 * 1. Text file with BLOSUM62
 *
 * Author: Oleg Yasnev
 */
public class Main {
    public static final String BLOSUM_FILE = "BLOSUM62.txt";
    public static final String INPUT_FILE = "input.fasta";

    public static void main(String[] args) {
        Blosum62 blosum62 = new Blosum62(BLOSUM_FILE, "#");

        Fasta fasta = new Fasta(INPUT_FILE);
        String[] strings = fasta.getStrings();
        StdOut.println("Align strings:");
        StdOut.println(strings[0]);
        StdOut.println(strings[1]);

        GlobalAlignment glob = new GlobalAlignment(strings[0], strings[1], blosum62);
        StdOut.printf("Max score alignment: %d\n", glob.getMaxScore());
        String[] backtrace = glob.getBacktraceStrings();
        StdOut.println("Backtrace:");
        StdOut.println(backtrace[0]);
        StdOut.println(backtrace[1]);
    }
}
