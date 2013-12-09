import edu.princeton.cs.introcs.StdOut;
import jebl.evolution.trees.RootedTree;
import merge.MergeTree;
import sequence.Sequence;
import sequence.SequenceFactory;
import sequence.interfaces.ISequence;
import tools.ClustalO;
import tools.Fasta;
import tools.NewickTreeReader;

import java.util.Map;

public class Main {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ParamParser paramParser = new ParamParser(args);
        if (!paramParser.state) {
            return;
        }

        SequenceFactory seqFactory = new SequenceFactory();

        Map<String, ISequence> seqMap = (Map<String, ISequence>)
                Fasta.readMap(paramParser.inputSeqFilename, seqFactory);

        RootedTree tree = NewickTreeReader.readTree(paramParser.inputTreeFilename);

        ClustalO clustalo = new ClustalO(paramParser.clustalo);

        MergeTree mergeTree = new MergeTree(tree, seqMap, clustalo, seqFactory);

        Fasta.writeSequences(paramParser.outputFilename, mergeTree.getSequences());
    }
}
