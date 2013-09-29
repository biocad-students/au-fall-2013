import java.util.ArrayList;

/**
 * Basic tool for working with FASTA file format
 *
 * Author: Oleg Yasnev
 */
public class Fasta {
    /**
     * Symbol opening a description line
     */
    public static final String DESCR_SYMBOL = ">";

    /**
     * Array of strings read from the file
     */
    protected String[] strings;

    /**
     * Constructor
     * Reads strings from a given file
     * @param filename file with strings in FASTA format
     */
    public Fasta(String filename) {
        In in = new In(filename);
        readStrings(in);
        in.close();
    }

    /**
     * Get array of strings read from the file
     * @return array of strings read from the file
     */
    public String[] getStrings() {
        return strings;
    }

    /**
     * Read strings from the file and write them to the array
     * @param in input
     */
    protected void readStrings(In in) {
        ArrayList<String> arr = new ArrayList<String>(); // array list with read lines
        StringBuilder sb = new StringBuilder();          // string builder for multi-line strings
        boolean strFlag = false;                         // true if there is a string in the file
        String line;                                     // line read from the file
        while (!in.isEmpty()) {
            line = in.readLine();
            if (line.startsWith(DESCR_SYMBOL)) {
                // save previous string (if there is) and prepare for a new one
                if (strFlag) {
                    arr.add(sb.toString());
                }
                strFlag = true;
                sb = new StringBuilder();
            } else {
                // concat multi-line string
                sb.append(line);
            }
        }
        if (strFlag) {
            arr.add(sb.toString());
        }
        strings = arr.toArray(new String[arr.size()]);
    }
}
