public interface PLVisitor<A, R> {
    /**
     * Visit a proposition symbol (e.g A).
     *
     * @param sentence
     *            a Sentence that is a propositional symbol.
     * @param arg
     *            optional argument to be used by the visitor.
     * @return optional return value to be used by the visitor.
     */
    R visitPropositionSymbol(PropositionSymbol sentence, A arg);

    /**
     * Visit a unary complex sentence (e.g. ~A).
     *
     * @param sentence
     *            a Sentence that is a unary complex sentence.
     * @param arg
     *            optional argument to be used by the visitor.
     * @return optional return value to be used by the visitor.
     */
    R visitUnarySentence(ComplexSentence sentence, A arg);

    /**
     * Visit a binary complex sentence (e.g. A & B).
     *
     * @param sentence
     *            a Sentence that is a binary complex sentence.
     * @param arg
     *            optional argument to be used by the visitor.
     * @return optional return value to be used by the visitor.
     */
    R visitBinarySentence(ComplexSentence sentence, A arg);
}