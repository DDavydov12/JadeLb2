import java.util.Set;

public class ConvertToConjunctionOfClauses {

    /**
     * Returns the specified sentence in its logically equivalent conjunction of
     * clauses.
     *
     * @param s
     *            a propositional logic sentence
     *
     * @return the input sentence converted to it logically equivalent
     *         conjunction of clauses.
     */
    public static ConjunctionOfClauses apply(Sentence s) {
        Sentence cnfSentence = ConvertToCNF.apply(s);
        Set<Clause> clauses = ClauseCollector.getClausesFrom(cnfSentence);
        return new ConjunctionOfClauses(clauses);
    }
}