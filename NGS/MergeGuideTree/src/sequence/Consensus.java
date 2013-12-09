package sequence;


import com.sun.deploy.util.StringUtils;
import sequence.interfaces.ISequence;
import sequence.interfaces.ISequenceFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 09.12.13
 */
public class Consensus {

    public static ISequence getConsensus(ISequence[] sequences, ISequenceFactory sequenceFactory) {
        if (sequences.length == 0) {
            return null;
        }
        // build array of strings
        String[] strings = new String[sequences.length];
        for (int i = 0; i < sequences.length; i++) {
            strings[i] = sequences[i].getSequence();
        }
        int len = strings[0].length();

        // init map
        HashMap<Character,Integer> map = new HashMap<Character, Integer>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            // reset map
            map.put('A', 0);
            map.put('C', 0);
            map.put('G', 0);
            map.put('T', 0);
            map.put('-', 0);
            // count symbols in current position
            for (String string : strings) {
                char c = string.charAt(i);
                map.put(c, map.get(c) + 1);
            }
            // find average (most often symbol)
            char c = ' ';
            int max = 0;
            for (Map.Entry<Character,Integer> entry : map.entrySet()) {
                if (entry.getValue() > max) {
                    c = entry.getKey();
                    max = entry.getValue();
                }
            }
            // add to consensus
            sb.append(c);
        }

        // consensus
        String seq = sb.toString();
        // trim leading/trailing residues
        seq = seq.replaceAll("(^-+)|(-+$)", "");

        // description
        ArrayList<String> descrList = new ArrayList<String>(sequences.length);
        for (ISequence sequence : sequences) {
            descrList.add(sequence.getDescription());
        }
        String descr = StringUtils.join(descrList, "_");
        if (descr.length() > 127) {
            descr = descr.substring(0, 127);
        }

        return sequenceFactory.createSequence(descr, seq);
    }
}
