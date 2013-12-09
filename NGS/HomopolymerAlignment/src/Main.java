import common.ScoreMatrix;
import edu.princeton.cs.introcs.StdOut;
import homopolymer.HomopolymerAlignment;
import homopolymer.HomopolymerSequence;
import homopolymer.HomopolymerSequenceFactory;
import homopolymer.ScoreAlignmentFunction;
import tools.Fasta;
import tools.ScoreAlignmentMatrixConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Alignment with homopolymers
 *
 * Author: Oleg Yasnev
 */
public class Main {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ParamParser paramParser = new ParamParser(args);
        if (!paramParser.state) {
            return;
        }

        HomopolymerSequenceFactory sequenceFactory = new HomopolymerSequenceFactory();

        StdOut.printf("Reading data from %s (%s)\n", paramParser.inputFilename, new Date());
        Fasta fasta = new Fasta(paramParser.inputFilename, sequenceFactory);
        ArrayList<HomopolymerSequence> arList = (ArrayList<HomopolymerSequence>) fasta.getSequences();
        HomopolymerSequence[] sequences = new HomopolymerSequence[arList.size()];
        arList.toArray(sequences);

        StdOut.printf("Alignment\n");
        ScoreMatrix scoreMatrix = new ScoreMatrix(paramParser.scoreMatrixFilename);
        ScoreAlignmentFunction scoreFunction = new ScoreAlignmentFunction(scoreMatrix, paramParser.gapScore);
        if (paramParser.largeMode) {
            String scoreMatrixFilename = "~" + paramParser.outputFilename + ".tmp";

            HomopolymerAlignment alignment = new HomopolymerAlignment(sequences, scoreFunction,
                    scoreMatrixFilename, paramParser.threadCount);

            StdOut.printf("Writing data to %s\n", paramParser.outputFilename);

            ScoreAlignmentMatrixConverter writer = new ScoreAlignmentMatrixConverter(paramParser.outputFilename,
                        sequences, scoreMatrixFilename, alignment.minScore, alignment.maxScore);

            // remove temp score alignment matrix file
            File file = new File(scoreMatrixFilename);
            file.delete();
        } else {
            HomopolymerAlignment alignment = new HomopolymerAlignment(sequences, scoreFunction, paramParser.threadCount);

            StdOut.printf("Writing data to %s\n", paramParser.outputFilename);

            ScoreAlignmentMatrixConverter writer = new ScoreAlignmentMatrixConverter(paramParser.outputFilename,
                    sequences, alignment.alignMatrix, alignment.minScore, alignment.maxScore);
        }

        StdOut.printf("Processing successfully finished!");
    }
}
