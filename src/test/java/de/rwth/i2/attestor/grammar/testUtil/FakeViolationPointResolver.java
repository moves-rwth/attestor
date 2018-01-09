package de.rwth.i2.attestor.grammar.testUtil;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class FakeViolationPointResolver extends ViolationPointResolver {

    Collection<Nonterminal> nonterminalsInResultingKeySet;
    Collection<HeapConfiguration> heapsForResult;

    public FakeViolationPointResolver() {

        super(null);
    }

    @Override
    public Map<Nonterminal, Collection<HeapConfiguration>> getRulesCreatingSelectorFor(Nonterminal nonterminal,
                                                                                       int tentacle,
                                                                                       String selectorName) {

        Map<Nonterminal, Collection<HeapConfiguration>> res = new LinkedHashMap<>();

        for (Nonterminal nt : nonterminalsInResultingKeySet) {

            res.put(nt, heapsForResult);
        }

        return res;

    }

    public void defineReturnedLhsForTest(Collection<Nonterminal> nonterminalsInResultingKeySet) {

        this.nonterminalsInResultingKeySet = nonterminalsInResultingKeySet;
    }

    public void defineRhsForAllNonterminals(Collection<HeapConfiguration> heapsForResult) {

        this.heapsForResult = heapsForResult;
    }

}
