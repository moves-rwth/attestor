package de.rwth.i2.attestor.grammar.materialization.communication;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;

public class DefaultGrammarResponse implements GrammarResponse {

    private final Collection<HeapConfiguration> applicableRules;

    public DefaultGrammarResponse(Collection<HeapConfiguration> rules) {

        applicableRules = rules;
    }


    public Collection<HeapConfiguration> getApplicableRules() {

        return applicableRules;
    }

}
