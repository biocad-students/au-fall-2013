package merge;

import edu.princeton.cs.introcs.StdOut;
import jebl.evolution.graphs.Node;
import jebl.evolution.trees.RootedTree;
import settings.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 09.12.13
 */
public class MergeTree {
    protected RootedTree tree;

    public MergeTree(RootedTree tree) {
        this.tree = tree;
        merge();
    }

    public class SubtreeInfo {
        public boolean canMerge = true;
        public ArrayList<String> leaves;

        public SubtreeInfo() {
            leaves = new ArrayList<String>();
        }
    }

    protected void merge() {
        SubtreeInfo subtreeInfo = dfs(tree, tree.getRootNode());
        if (subtreeInfo.canMerge) {
            mergeSubtree(subtreeInfo);
        }
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
                // if at least one subtree cannot be merged - all the tree cannot be merged
                if (leftSubtree.canMerge) mergeSubtree(leftSubtree);
                if (rightSubtree.canMerge) mergeSubtree(rightSubtree);
                subtreeInfo.canMerge = false;
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

    public void mergeSubtree(SubtreeInfo subtreeInfo) {
        if (Config.DEBUG) {
            for (String leaf : subtreeInfo.leaves) {
                StdOut.println(leaf);
            }
            StdOut.println("-------------------------------");
        }
    }
}
