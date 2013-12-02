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
        int score = scoreMatrix.getScore(hFirst.nucleotide, hSecond.nucleotide);
        int max = Math.max(hFirst.count,  hSecond.count);
        int min = Math.min(hFirst.count, hSecond.count);
        int delta = max - min;
        return score * max - Math.abs(score) * delta * delta;
    }

    public int getGapScore(Homopolymer h) {
        return gapScore * h.count;
    }
}
