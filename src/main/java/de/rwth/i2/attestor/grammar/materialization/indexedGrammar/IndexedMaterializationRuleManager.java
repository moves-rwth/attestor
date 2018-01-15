package de.rwth.i2.attestor.grammar.materialization.indexedGrammar;

import java.util.*;

import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.communication.*;
import de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;

/**
 * Computes and caches the rules for resolving a given violation point
 * for {@link IndexedNonterminal}s in addition to all {@link Nonterminal}s
 * handled by {@link DefaultMaterializationRuleManager}.
 *
 * @author Hannah
 */
public class IndexedMaterializationRuleManager extends DefaultMaterializationRuleManager {

    private final ViolationPointResolver violationPointResolver;

    IndexedRuleAdapter indexRuleAdapter;

	private final Map<GrammarRequest, GrammarResponse> instantiatedRuleGraphsCreatingSelector = new LinkedHashMap<>();

    public IndexedMaterializationRuleManager(ViolationPointResolver vioResolver, IndexMatcher indexMatcher) {

        super(vioResolver);
        this.violationPointResolver = vioResolver;
        this.indexRuleAdapter = new IndexedRuleAdapter(indexMatcher);
    }


    @Override
    public GrammarResponse getRulesFor(Nonterminal toReplace, int tentacle, String requestedSelector)
            throws UnexpectedNonterminalTypeException {

        GrammarRequest request = new GrammarRequest(toReplace, tentacle, requestedSelector);
        GrammarResponse response;

        if (instantiatedRuleGraphsCreatingSelector.containsKey(request)) {
            response = instantiatedRuleGraphsCreatingSelector.get(request);
        } else {

            response = computeResponse(toReplace, tentacle, requestedSelector);
            instantiatedRuleGraphsCreatingSelector.put(request, response);
        }
        return response;
    }


    /**
     * Computes the Correct Grammar Response for the given request.
     * For defaultNonterminals the response consists of all rules matching the nonterminal
     * and resolving the violation point
     * For indexedNonterminals the response consists of materializations which have to be applied
     * to the graph and all rules resolving the violation point which can be applied after this
     * materialization
     *
     * @param toReplace         the nonterminal to match
     * @param tentacle          the tentacle of the nonterminal at which the violation point sits
     * @param requestedSelector the selector creating the violation point
     * @return the response containing all necessary information
     * for materialization of this violation point
     * @throws UnexpectedNonterminalTypeException
     */
    private GrammarResponse computeResponse(Nonterminal toReplace,
                                            int tentacle,
                                            String requestedSelector)
            throws UnexpectedNonterminalTypeException {

        Map<Nonterminal, Collection<HeapConfiguration>> rulesResolvingViolationPoint =
                this.violationPointResolver.getRulesCreatingSelectorFor(toReplace,
                        tentacle,
                        requestedSelector);

        if (toReplace instanceof IndexedNonterminal) {
            IndexedNonterminal indexedToReplace = (IndexedNonterminal) toReplace;
            return indexRuleAdapter.computeMaterializationsAndRules( indexedToReplace, rulesResolvingViolationPoint);

        } else {
            return super.getRulesFor(toReplace, tentacle, requestedSelector);
        }
    }


}
