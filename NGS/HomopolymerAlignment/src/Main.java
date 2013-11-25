import java.util.ArrayList;

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

        Fasta fasta = new Fasta(paramParser.inputFilename, sequenceFactory);
        ArrayList<HomopolymerSequence> arList = (ArrayList<HomopolymerSequence>) fasta.getSequences();
        HomopolymerSequence[] sequences = new HomopolymerSequence[arList.size()];
        arList.toArray(sequences);

        ScoreMatrix scoreMatrix = new ScoreMatrix(paramParser.scoreMatrixFilename);
        ScoreAlignmentFunction scoreFunction = new ScoreAlignmentFunction(scoreMatrix, paramParser.gapScore);
        HomopolymerAlignment alignment = new HomopolymerAlignment(sequences, scoreFunction);

        ScoreAlignmentMatrixWriter writer = new ScoreAlignmentMatrixWriter(paramParser.outputFilename,
                    sequences, alignment.alignMatrix);
        writer.writePhylipFormat();
    }
}
