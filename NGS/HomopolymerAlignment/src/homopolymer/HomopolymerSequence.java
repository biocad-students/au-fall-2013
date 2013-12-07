package homopolymer;

import common.interfaces.ISequence;
import settings.Config;

import java.util.ArrayList;

/**
 * Sequence that also has homopolymer presentation
 *
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 18.11.13
 */
public class HomopolymerSequence implements ISequence {
    protected String description;
    protected String sequence;
    protected Homopolymer[] homopolymerSequence;

    public HomopolymerSequence(String description, String sequence) {
        setDescription(description);
        setSequence(sequence);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getSequence() {
        return sequence;
    }

    @Override
    public void setSequence(String sequence) {
        this.sequence = sequence;
        buildHomopolymerSequence();
    }

    public Homopolymer[] getHomopolymerSequence() {
        return homopolymerSequence;
    }

    protected void buildHomopolymerSequence() {
        ArrayList<Homopolymer> arList = new ArrayList<Homopolymer>();
        char[] chArr = sequence.toCharArray();
        if (chArr.length == 0) {
            homopolymerSequence = new Homopolymer[0];
            return;
        }
        if (Config.COMBINE_HOMOPOLYMERS) {
            // combine homopolymers, i.e. "GGGGG" => "G5"
            char nucl = chArr[0];
            int count = 1;
            int i = 1;
            while (i < chArr.length) {
                char nextNucl = chArr[i];
                if (nextNucl == nucl) {
                    count++;
                } else {
                    arList.add(new Homopolymer(nucl, count));
                    nucl = nextNucl;
                    count = 1;
                }
                i++;
            }
            arList.add(new Homopolymer(nucl, count));
        } else {
            // use conventional presentation
            for (char c : chArr) {
                arList.add(new Homopolymer(c, 1));
            }
        }
        homopolymerSequence = new Homopolymer[arList.size()];
        arList.toArray(homopolymerSequence);
    }
}
