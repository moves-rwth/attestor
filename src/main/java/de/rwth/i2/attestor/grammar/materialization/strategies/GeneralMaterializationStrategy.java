package de.rwth.i2.attestor.grammar.materialization.strategies;

import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.grammar.materialization.communication.WrongResponseTypeException;
import de.rwth.i2.attestor.grammar.materialization.util.GrammarResponseApplier;
import de.rwth.i2.attestor.grammar.materialization.util.MaterializationRuleManager;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.util.MatchingUtil;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.iterator.TIntIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Determines concrete violation points and resolves them.
 * <br>
 * The class itself is oblivious of the used grammars and
 * materializationMechanisms, i.e. it doesn't know whether
 * only nonterminal edges are replaced by graphs or if
 * additional index materializations are applied (These
 * mechanisms are provided by the MaterializationRuleManager
 * and GrammarResponseApplier).
 *
 * @author Hannah
 */
public class GeneralMaterializationStrategy implements MaterializationStrategy {

    private static final Logger logger = LogManager.getLogger("GeneralMaterializationStrategy");

    private final MaterializationRuleManager ruleManager;
    private final GrammarResponseApplier ruleApplier;

    /**
     * Creates a new GeneralMaterializationStrategy using ruleManager to determine
     * which rules to apply to resolve a violationPoint and ruleApplier to apply
     * those rules to the graph.
     * Note that the ruleApplier needs to handle the GrammarResponse created by
     * the ruleManager. Therefore, you have to ensure that the return type of the
     * first is handled by the latter.
     *
     * @param ruleManager a MaterializationRuleManager providing the rules to apply
     *                    to a given violationPoint
     * @param ruleApplier the GrammarResponseApplier applying the rules to the graph
     *                    (e.g. performing materialization of stacks, replacing the nonterminal by the rule
     *                    graph)
     */
    public GeneralMaterializationStrategy(MaterializationRuleManager ruleManager,
                                          GrammarResponseApplier ruleApplier) {

        this.ruleManager = ruleManager;
        this.ruleApplier = ruleApplier;
    }

    @Override
    public Collection<HeapConfiguration> materialize(HeapConfiguration heapConfiguration,
                                                ViolationPoints potentialViolationPoints) {

        List<HeapConfiguration> res = new ArrayList<>();

        Stack<HeapConfiguration> worklist = new Stack<>();
        worklist.add(heapConfiguration);

        boolean appliedMaterialization = false;

        while (!worklist.isEmpty()) {

            HeapConfiguration current = worklist.pop();

            Pair<Integer, String> actualViolationPoint = getActualViolationPoint(current, potentialViolationPoints);

            if (actualViolationPoint == null) { // all potential violation points have been removed

                if (appliedMaterialization) {
                    res.add(current);
                }

            } else {

                worklist.addAll(resolveViolationPoint(current, actualViolationPoint));
                appliedMaterialization = true;
            }
        }

        return res;
    }

    private Collection<? extends HeapConfiguration> resolveViolationPoint(HeapConfiguration current,
                                                                     Pair<Integer, String> actualViolationPoint) {

        int vioNode = actualViolationPoint.first();
        String requiredSelectorLabel = actualViolationPoint.second();

        TIntIterator attachedNtIter = current.attachedNonterminalEdgesOf(vioNode).iterator();
        while (attachedNtIter.hasNext()) {

            int ntEdge = attachedNtIter.next();
            Nonterminal nt = current.labelOf(ntEdge);
            int tentacle = current.attachedNodesOf(ntEdge).indexOf(vioNode);


            GrammarResponse rulesToApply;
            try {
                rulesToApply = ruleManager.getRulesFor(nt, tentacle, requiredSelectorLabel);
                Collection<HeapConfiguration> materializationResults =
                        ruleApplier.applyGrammarResponseTo(current, ntEdge, rulesToApply);

                if(!materializationResults.isEmpty()) {
                    return materializationResults;
                }

            } catch (UnexpectedNonterminalTypeException e) {
                logger.error("rule Manager cannot deal with this nonterminal type: " + nt.getClass());
            } catch (WrongResponseTypeException e) {
                logger.error("ruleApplier cannot handle the GrammarResponse created by ruleManager");
            }


        }

        return Collections.emptyList();
    }

    Pair<Integer, String> getActualViolationPoint(HeapConfiguration heapConfiguration,
                                                  ViolationPoints potentialViolationPoints) {

        for (String variableName : potentialViolationPoints.getVariables()) {

            try {
                int variableTarget = heapConfiguration.targetOf(heapConfiguration.variableWith(variableName));
                List<SelectorLabel> selectors = heapConfiguration.selectorLabelsOf(variableTarget);
                for (String label : potentialViolationPoints.getSelectorsOf(variableName)) {
                    if (!MatchingUtil.containsMatch(selectors, s -> s.hasLabel(label))) {
                        return new Pair<>(variableTarget, label);
                    }
                }

            } catch (IllegalArgumentException e) {
                logger.debug("the variable " + variableName + "of the violationPoint was not present");
            }
        }

        return null;
    }
}
