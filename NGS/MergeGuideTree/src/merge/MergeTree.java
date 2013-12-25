package merge;

import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.Out;
import edu.princeton.cs.introcs.StdOut;
import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import sequence.Consensus;
import sequence.interfaces.ISequence;
import sequence.interfaces.ISequenceFactory;
import settings.Config;
import tools.ClustalO;
import tools.Fasta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 09.12.13
 */
public class MergeTree {
    protected RootedTree tree;
    protected Map<String, ISequence> seqMap;
    protected ClustalO clustalo;
    protected ISequenceFactory sequenceFactory;

    protected ArrayList<ISequence> resultSequences;

    protected static final String SEQ_TEMP_FILE = "~merge_tree.tmp";
    protected static final String ALIGN_SEQ_TEMP_FILE = "~merge_tree_align.tmp";

    public MergeTree(RootedTree tree, Map<String, ISequence> sequenceMap, ClustalO clustalo, ISequenceFactory sequenceFactory) {
        this.tree = tree;
        this.seqMap = sequenceMap;
        this.clustalo = clustalo;
        this.sequenceFactory = sequenceFactory;
        merge();
    }

    public class SubtreeInfo {
        public boolean canMerge = true;
        public ArrayList<String> leaves;

        public SubtreeInfo() {
            leaves = new ArrayList<String>();
        }
    }

    /**
     * Get array list of sequences
     * @return array list of sequences
     */
    public ArrayList<? extends ISequence> getSequences() {
        return resultSequences;
    }

    protected void merge() {
        resultSequences = new ArrayList<ISequence>();
        SubtreeInfo subtreeInfo = dfs(tree, tree.getRootNode());
        if (subtreeInfo.canMerge) {
            mergeSubtree(subtreeInfo);
        }

        // remove temp files
        File file = new File(SEQ_TEMP_FILE);
        file.delete();
        file = new File(ALIGN_SEQ_TEMP_FILE);
        file.delete();
    }

    protected SubtreeInfo dfs(RootedTree tree, Node v) {
        List<Node> children = tree.getChildren(v);
        SubtreeInfo subtreeInfo = new SubtreeInfo();
        if (children.size() == 0) {
            // leaf
            subtreeInfo.leaves.add(tree.getTaxon(v).getName());
            return subtreeInfo;
        } else if (children.size() == 2) {
            // process subtrees
            SubtreeInfo leftSubtree = dfs(tree, children.get(0));
            SubtreeInfo rightSubtree = dfs(tree, children.get(1));
            // We believe that:
            //      1) Leaf can be merged
            //      2) Tree can be merged if one its subtree is a leaf and second can be merged
            if (!leftSubtree.canMerge || !rightSubtree.canMerge) {
                if (!leftSubtree.canMerge && !rightSubtree.canMerge) {
                    subtreeInfo.canMerge = false;
                } else {
                    if (leftSubtree.canMerge) {
                        subtreeInfo.leaves.addAll(leftSubtree.leaves);
                    } else {
                        subtreeInfo.leaves.addAll(rightSubtree.leaves);
                    }
                }
                // if at least one subtree cannot be merged - all the tree cannot be merged
                //if (leftSubtree.canMerge) mergeSubtree(leftSubtree);
                //if (rightSubtree.canMerge) mergeSubtree(rightSubtree);
                //subtreeInfo.canMerge = false;
                return subtreeInfo;
            } else if (leftSubtree.leaves.size() == 1 || rightSubtree.leaves.size() == 1) {
                // if one subtree is a leaf and second can be merged - all the tree can be merged
                // (the condition that a subtree cannot be merged was processed above)

                // unite leaves
                subtreeInfo.leaves.addAll(leftSubtree.leaves);
                subtreeInfo.leaves.addAll(rightSubtree.leaves);
                return subtreeInfo;
            } else {
                // two subtrees can be merged but no one is a leaf - all the tree cannot be merged
                mergeSubtree(leftSubtree);
                mergeSubtree(rightSubtree);
                subtreeInfo.canMerge = false;
                return subtreeInfo;
            }
        } else {
            // The guide tree has to be binary. If not - it's strange. Let's ignore
            subtreeInfo.canMerge = false;
            return subtreeInfo;
        }
    }

    @SuppressWarnings("unchecked")
    protected void mergeSubtree(SubtreeInfo subtreeInfo) {
        if (Config.DEBUG) {
            for (String leaf : subtreeInfo.leaves) {
                StdOut.println(leaf);
            }
            StdOut.println("-------------------------------");
        }

        ISequence[] sequences = new ISequence[subtreeInfo.leaves.size()];
        for (int i = 0; i < subtreeInfo.leaves.size(); i++) {
            String leaf = subtreeInfo.leaves.get(i);
            sequences[i] = seqMap.get(leaf);
        }

        if (sequences.length > 1) {
            Fasta.writeSequences(SEQ_TEMP_FILE, sequences);
            clustalo.run(SEQ_TEMP_FILE, ALIGN_SEQ_TEMP_FILE);
            ArrayList<ISequence> arList = (ArrayList<ISequence>) Fasta.readSequences(ALIGN_SEQ_TEMP_FILE, sequenceFactory);
            sequences = new ISequence[arList.size()];
            arList.toArray(sequences);
            resultSequences.add(Consensus.getConsensus(sequences, sequenceFactory));
        } else {
            resultSequences.add(sequences[0]);
        }
    }
}
