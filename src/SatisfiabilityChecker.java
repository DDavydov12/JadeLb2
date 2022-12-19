public interface SatisfiabilityChecker {
    /**
     * Checks the satisfiability of a sentence in propositional logic.
     *
     * @param s
     *            a sentence in propositional logic.
     * @return true if the sentence is satisfiable, false otherwise.
     */
    boolean isSatisfiable(Sentence s);
}

