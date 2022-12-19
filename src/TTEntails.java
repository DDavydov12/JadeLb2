import java.util.ArrayList;
import java.util.List;

public class TTEntails implements EntailmentChecker {

    /**
     * function TT-ENTAILS?(KB, &alpha;) returns true or false.
     *
     * @param kb
     *            KB, the knowledge base, a sentence in propositional logic
     * @param alpha
     *            &alpha;, the query, a sentence in propositional logic
     *
     * @return true if KB entails &alpha;, false otherwise.
     */
    public boolean isEntailed(KnowledgeBase kb, Sentence alpha) {
        // symbols <- a list of proposition symbols in KB and &alpha
        List<PropositionSymbol> symbols = new ArrayList<PropositionSymbol>(
                SymbolCollector.getSymbolsFrom(kb.asSentence(), alpha));

        // return TT-CHECK-ALL(KB, &alpha; symbols, {})
        return ttCheckAll(kb, alpha, symbols, new Model());
    }

    //
    /**
     * function TT-CHECK-ALL(KB, &alpha; symbols, model) returns true or false
     *
     * @param kb
     *            KB, the knowledge base, a sentence in propositional logic
     * @param alpha
     *            &alpha;, the query, a sentence in propositional logic
     * @param symbols
     *            a list of currently unassigned propositional symbols in the
     *            model.
     * @param model
     *            a partially or fully assigned model for the given KB and
     *            query.
     * @return true if KB entails &alpha;, false otherwise.
     */
    public boolean ttCheckAll(KnowledgeBase kb, Sentence alpha,
                              List<PropositionSymbol> symbols, Model model) {
        // if EMPTY?(symbols) then
        if (symbols.isEmpty()) {
            // if PL-TRUE?(KB, model) then return PL-TRUE?(&alpha;, model)
            if (model.isTrue(kb.asSentence())) {
                return model.isTrue(alpha);
            } else {
                // else return true // when KB is false, always return true
                return true;
            }
        }

        // else do
        // P <- FIRST(symbols)
        PropositionSymbol p = Util.first(symbols);
        // rest <- REST(symbols)
        List<PropositionSymbol> rest = Util.rest(symbols);
        // return (TT-CHECK-ALL(KB, &alpha;, rest, model &cup; { P = true })
        // and
        // TT-CHECK-ALL(KB, &alpha;, rest, model U { P = false }))
        return ttCheckAll(kb, alpha, rest, model.union(p, true))
                && ttCheckAll(kb, alpha, rest, model.union(p, false));
    }
}