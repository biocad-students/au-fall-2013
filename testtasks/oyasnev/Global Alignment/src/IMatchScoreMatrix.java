/**
 * Interface for match scoring matrix (like Blosum62)
 *
 * Author: Oleg Yasnev
 */
public interface IMatchScoreMatrix {
    /**
     * Get a score of substitution for two amino acids (aa) or empty string (-)
     * @param ch1 aa or -
     * @param ch2 aa or -
     * @return a score
     */
    public int getScore(char ch1, char ch2);

    /**
     * Get a character symbol used as gap symbol
     * @return a gap symbol
     */
    public char getGapSymbol();

    /**
     * Get a gap score for insertion or deletion
     * @return a gap score
     */
    public int getGapScore();
}
