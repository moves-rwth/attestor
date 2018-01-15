package de.rwth.i2.attestor.grammar.materialization.communication;

import de.rwth.i2.attestor.grammar.materialization.util.GrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.util.GraphMaterializer;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Applies the rules in a {@link DefaultGrammarResponse} to a graph
 *
 * @author Hannah
 */
public class DefaultGrammarResponseApplier implements GrammarResponseApplier {

    final GraphMaterializer graphMaterializer;

    public DefaultGrammarResponseApplier(GraphMaterializer graphMaterializer) {

        this.graphMaterializer = graphMaterializer;
    }

    /**
     * @throws WrongResponseTypeException if the grammarResponse is not instanceof
     *                                    {@link DefaultGrammarResponse}.
     * @see de.rwth.i2.attestor.grammar.materialization.util.GrammarResponseApplier#applyGrammarResponseTo(de.rwth.i2.attestor.graph.heap.HeapConfiguration, int, de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse)
     */
    @Override
    public Collection<HeapConfiguration> applyGrammarResponseTo(HeapConfiguration inputGraph,
                                                                int edgeId,
                                                                GrammarResponse grammarResponse)
            throws WrongResponseTypeException {

        if (grammarResponse instanceof DefaultGrammarResponse) {
            DefaultGrammarResponse defaultGrammarResponse = (DefaultGrammarResponse) grammarResponse;

            return applyRulesInGrammarResponseTo(inputGraph, edgeId, defaultGrammarResponse);

        } else {
            throw new WrongResponseTypeException("can only handle DefaultGrammarResponse");
        }

    }

    /**
     * uses the graphMaterializer to apply each rule in the grammarResponse to
     * the inputGraph
     *
     * @param inputGraph             the graph which will be materialized
     * @param edgeId                 the id of the nonterminal edge which will be materialized
     * @param defaultGrammarResponse a DefaultGrammarResponse holding all the rules
     *                               which will be applied
     * @return a collection holding all the materialization results.
     */
    private Collection<HeapConfiguration> applyRulesInGrammarResponseTo(
            HeapConfiguration inputGraph, int edgeId,
            DefaultGrammarResponse defaultGrammarResponse) {

        Collection<HeapConfiguration> materializedGraphs = new ArrayList<>();

        for (HeapConfiguration rhsToApply : defaultGrammarResponse.getApplicableRules()) {

            HeapConfiguration materializedGraph =
                    graphMaterializer.getMaterializedCloneWith(inputGraph, edgeId, rhsToApply);
            materializedGraphs.add(materializedGraph);
        }

        return materializedGraphs;
    }

}
