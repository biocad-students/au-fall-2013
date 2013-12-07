package homopolymer;

import common.interfaces.ISequence;
import common.interfaces.ISequenceFactory;

/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 18.11.13
 */
public class HomopolymerSequenceFactory implements ISequenceFactory {
    @Override
    public ISequence createSequence(String description, String sequence) {
        return new HomopolymerSequence(description, sequence);
    }
}