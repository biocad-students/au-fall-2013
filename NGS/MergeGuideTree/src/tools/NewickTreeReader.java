package tools;

import jebl.evolution.io.NewickImporter;
import jebl.evolution.trees.RootedTree;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 09.12.13
 */
public class NewickTreeReader {
    public static RootedTree readTree(String filename) {
        BufferedReader r;
        try {
            r = new BufferedReader(new FileReader(filename));
            NewickImporter newickImporter = new NewickImporter(r, true);
            return (RootedTree) newickImporter.importNextTree();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
