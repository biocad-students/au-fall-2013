package homopolymer;

/**
 * Homopolymer. E.g., GGGGG => nucleotide = G, count = 5
 *
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 18.11.13
 */
public class Homopolymer {
    public char nucleotide;
    public int count;

    public Homopolymer(char nucleotide, int count) {
        this.nucleotide = nucleotide;
        this.count = count;
    }

    @Override
    public String toString() {
        return String.valueOf(nucleotide) + String.valueOf(count);
    }
}
