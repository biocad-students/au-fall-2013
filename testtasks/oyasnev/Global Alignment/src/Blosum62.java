import java.util.HashMap;
import java.util.Map;

/**
 * Operates with Blosum62 scoring matrix
 *
 * Author: Oleg Yasnev
 */
public class Blosum62 implements IMatchScoreMatrix {
    /**
     * Scoring map. Key is two concatenated amino acids or empty string
     */
    protected Map<String, Integer> map;

    /**
     * A character symbol used as gap symbol
     */
    protected char gapSymbol;

    /**
     * A gap score for insertion or deletion
     */
    protected int gapScore;

    /**
     * Constructor.
     * Reads Blosum62 from a given file
     * @param filename file with Blosum62
     * @param commentPrefix prefix string opening line-comment
     */
    public Blosum62(String filename, String commentPrefix) {
        readFromFile(filename, commentPrefix);
    }

    public int getScore(char ch1, char ch2) {
        return map.get(String.valueOf(ch1) + String.valueOf(ch2));
    }

    public char getGapSymbol() {
        return gapSymbol;
    }

    public int getGapScore() {
        return gapScore;
    }

    /**
     * Reads Blosum62 from a given file
     * @param filename file with Blosum62
     * @param commentPrefix prefix string opening line-comment
     */
    protected void readFromFile(String filename, String commentPrefix) {
        In in = new In(filename);

        String str;
        // Skip comments
        do {
            str = in.readLine();
        } while (str.startsWith(commentPrefix));

        // Parse header
        String[] symbols = str.trim().split("\\s+");

        map = new HashMap<String, Integer>(symbols.length * 2);
        // length is twice because AB and BA are two records in map

        // Read score and fill map
        while (!in.isEmpty()) {
            // read header of current line
            char curSymbol = in.readString().charAt(0);
            if (curSymbol < 'A' || curSymbol > 'Z') {
                gapSymbol = curSymbol;
            }
            // add scores for combination row + count
            for (String symbol : symbols) {
                int score = in.readInt();
                map.put(symbol + curSymbol, score);
            }
        }

        gapScore = map.get("A" + gapSymbol);
        // gap score is equal for every combination except two empty strings

        in.close();
    }
}
