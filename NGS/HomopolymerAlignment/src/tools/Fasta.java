package tools;

import common.interfaces.ISequence;
import common.interfaces.ISequenceFactory;
import edu.princeton.cs.introcs.In;

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
     * Array list of sequences read from the file
     */
    protected ArrayList<ISequence> sequences;

    protected ISequenceFactory sequenceFactory;

    /**
     * Constructor
     * Reads sequences from a given file
     * @param filename file with sequences in FASTA format
     */
    public Fasta(String filename, ISequenceFactory sequenceFactory) {
        In in = new In(filename);
        this.sequenceFactory = sequenceFactory;
        readSequences(in);
        in.close();
    }

    /**
     * Get array list of sequences read from the file
     * @return array list of sequences read from the file
     */
    public ArrayList<? extends ISequence> getSequences() {
        return sequences;
    }

    /**
     * Read sequences from the file and write them to the array list
     * @param in input
     */
    protected void readSequences(In in) {
        sequences = new ArrayList<ISequence>();
        String descr = "";                               // sequence description
        StringBuilder sb = new StringBuilder();          // string builder for multi-line strings
        boolean strFlag = false;                         // true if there is a string in the file
        String line;                                     // line read from the file
        while (!in.isEmpty()) {
            line = in.readLine();
            if (line.startsWith(DESCR_SYMBOL)) {
                // save previous string (if there is) and prepare for a new one
                if (strFlag) {
                    sequences.add(sequenceFactory.createSequence(descr, sb.toString()));
                }
                strFlag = true;
                descr = line.substring(DESCR_SYMBOL.length());
                sb = new StringBuilder();
            } else {
                // concat multi-line string
                sb.append(line);
            }
        }
        if (strFlag) {
            sequences.add(sequenceFactory.createSequence(descr, sb.toString()));
        }
    }
}
