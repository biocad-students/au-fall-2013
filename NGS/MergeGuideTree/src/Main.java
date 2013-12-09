import jebl.evolution.trees.RootedTree;
import merge.MergeTree;
import tools.NewickTreeReader;

public class Main {

    public static void main(String[] args) {
        ParamParser paramParser = new ParamParser(args);
        if (!paramParser.state) {
            return;
        }

        RootedTree tree = NewickTreeReader.readTree(paramParser.inputTreeFilename);

        MergeTree mergeTree = new MergeTree(tree);


    }
}
