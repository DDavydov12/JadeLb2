import javax.lang.model.SourceVersion;

public class PropositionSymbol extends AtomicSentence {
    //
    public static final String TRUE_SYMBOL  = "True";
    public static final String FALSE_SYMBOL = "False";
    public static final PropositionSymbol TRUE  = new PropositionSymbol(TRUE_SYMBOL);
    public static final PropositionSymbol FALSE = new PropositionSymbol(FALSE_SYMBOL);
    //
    private String symbol;

    /**
     * Constructor.
     *
     * @param symbol
     *            the symbol uniquely identifying the proposition.
     */
    public PropositionSymbol(String symbol) {
        // Ensure differing cases for the 'True' and 'False'
        // propositional constants are represented in a canonical form.
        if (TRUE_SYMBOL.equalsIgnoreCase(symbol)) {
            this.symbol = TRUE_SYMBOL;
        } else if (FALSE_SYMBOL.equalsIgnoreCase(symbol)) {
            this.symbol = FALSE_SYMBOL;
        } else if (isPropositionSymbol(symbol)){
            this.symbol = symbol;
        }
        else {
            throw new IllegalArgumentException("Not a legal proposition symbol: "+symbol);
        }
    }

    /**
     *
     * @return true if this is the always 'True' proposition symbol, false
     *         otherwise.
     */
    public boolean isAlwaysTrue() {
        return TRUE_SYMBOL.equals(symbol);
    }

    /**
     *
     * @return true if the symbol passed in is the always 'True' proposition
     *         symbol, false otherwise.
     */
    public static boolean isAlwaysTrueSymbol(String symbol) {
        return TRUE_SYMBOL.equalsIgnoreCase(symbol);
    }

    /**
     *
     * @return true if this is the always 'False' proposition symbol, false
     *         other.
     */
    public boolean isAlwaysFalse() {
        return FALSE_SYMBOL.equals(symbol);
    }

    /**
     *
     * @return true if the symbol passed in is the always 'False' proposition
     *         symbol, false other.
     */
    public static boolean isAlwaysFalseSymbol(String symbol) {
        return FALSE_SYMBOL.equalsIgnoreCase(symbol);
    }

    /**
     * Determine if the given symbol is a legal proposition symbol.
     *
     * @param symbol
     *            a symbol to be tested.
     * @return true if the given symbol is a legal proposition symbol, false
     *         otherwise.
     */
    public static boolean isPropositionSymbol(String symbol) {
        return SourceVersion.isIdentifier(symbol);
    }

    /**
     * Determine if the given character can be at the beginning of a proposition
     * symbol.
     *
     * @param ch
     *            a character.
     * @return true if the given character can be at the beginning of a
     *         proposition symbol representation, false otherwise.
     */
    public static boolean isPropositionSymbolIdentifierStart(char ch) {
        return Character.isJavaIdentifierStart(ch);
    }

    /**
     * Determine if the given character is part of a proposition symbol.
     *
     * @param ch
     *            a character.
     * @return true if the given character is part of a proposition symbols
     *         representation, false otherwise.
     */
    public static boolean isPropositionSymbolIdentifierPart(char ch) {
        return Character.isJavaIdentifierPart(ch);
    }

    /**
     *
     * @return the symbol uniquely identifying the proposition.
     */
    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if ((o == null) || (this.getClass() != o.getClass())) {
            return false;
        }
        PropositionSymbol sym = (PropositionSymbol) o;
        return symbol.equals(sym.symbol);

    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}