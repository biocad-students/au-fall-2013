/**
 * Author: Oleg Yasnev (oyasnev@gmail.com)
 * Date: 20.11.13
 */
public class ScoreAlignmentFunction {
    protected IMatchScoreMatrix scoreMatrix;
    protected int gapScore;

    public ScoreAlignmentFunction(IMatchScoreMatrix scoreMatrix, int gapScore) {
        this.scoreMatrix = scoreMatrix;
        this.gapScore = gapScore;
    }

    public int getScore(Homopolymer hFirst, Homopolymer hSecond) {
        if (hFirst.nucleotide == hSecond.nucleotide) {
            return scoreMatrix.getScore(hFirst.nucleotide, hSecond.nucleotide);
        }
        return Math.max(hFirst.count, hSecond.count) * scoreMatrix.getScore(hFirst.nucleotide, hSecond.nucleotide);
    }

    public int getGapScore(Homopolymer h) {
        return gapScore * h.count;
    }
}
