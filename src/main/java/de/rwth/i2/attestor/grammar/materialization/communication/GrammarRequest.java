package de.rwth.i2.attestor.grammar.materialization.communication;

import de.rwth.i2.attestor.graph.Nonterminal;

public class GrammarRequest {

    private final Nonterminal nonterminal;
    private final int tentacle;
    private final String SelectorLabel;


    public GrammarRequest(Nonterminal nonterminal2, int tentacle, String selectorLabel) {

        super();
        this.nonterminal = nonterminal2;
        this.tentacle = tentacle;
        SelectorLabel = selectorLabel;
    }

    public Nonterminal getNonterminal() {

        return nonterminal;
    }

    public int getTentacle() {

        return tentacle;
    }

    public String getSelectorLabel() {

        return SelectorLabel;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((SelectorLabel == null) ? 0 : SelectorLabel.hashCode());
        result = prime * result + ((nonterminal == null) ? 0 : nonterminal.hashCode());
        result = prime * result + tentacle;
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GrammarRequest other = (GrammarRequest) obj;
        if (SelectorLabel == null) {
            if (other.SelectorLabel != null)
                return false;
        } else if (!SelectorLabel.equals(other.SelectorLabel))
            return false;
        if (nonterminal == null) {
            if (other.nonterminal != null)
                return false;
        } else if (!nonterminal.equals(other.nonterminal)) {
            return false;
        }
        return tentacle == other.tentacle;
    }


}
