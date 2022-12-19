public class ConvertToNNF {

    /**
     * Returns the specified sentence in its logically equivalent negation
     * normal form.
     *
     * @param s
     *            a propositional logic sentence
     *
     * @return the input sentence converted to it logically equivalent
     *         negation normal form.
     */
    public static Sentence apply(Sentence s) {
        Sentence biconditionalsRemoved = EliminateBiconditionals.apply(s);
        Sentence implicationsRemoved = EliminateImplications.apply(biconditionalsRemoved);
        return MoveNotInwards.apply(implicationsRemoved);
    }
}
