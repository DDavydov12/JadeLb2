public class ConvertToCNF {

    /**
     * Returns the specified sentence in its logically equivalent conjunctive
     * normal form.
     *
     * @param s
     *            a propositional logic sentence
     *
     * @return the input sentence converted to it logically equivalent
     *         conjunctive normal form.
     */
    public static Sentence apply(Sentence s) {
        Sentence nnfSentence = ConvertToNNF.apply(s);
        return DistributeOrOverAnd.apply(nnfSentence);
    }
}