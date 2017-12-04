package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.grammar.materialization.communication.GrammarResponse;
import de.rwth.i2.attestor.grammar.materialization.communication.UnexpectedNonterminalTypeException;
import de.rwth.i2.attestor.grammar.materialization.communication.WrongResponseTypeException;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.MaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ViolationPoints;
import de.rwth.i2.attestor.util.MatchingUtil;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.iterator.TIntIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

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
    public List<ProgramState> materialize(ProgramState state,
                                          ViolationPoints potentialViolationPoints) {

        List<ProgramState> res = new ArrayList<>();

        Stack<ProgramState> worklist = new Stack<>();
        worklist.add(state);

        boolean appliedMaterialization = false;

        while (!worklist.isEmpty()) {

            ProgramState current = worklist.pop();

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

    private Collection<? extends ProgramState> resolveViolationPoint(ProgramState current,
                                                                     Pair<Integer, String> actualViolationPoint) {

        List<ProgramState> res = new ArrayList<>();

        int vioNode = actualViolationPoint.first();
        String requiredSelectorLabel = actualViolationPoint.second();

        HeapConfiguration hc = current.getHeap();
        TIntIterator attachedNtIter = hc.attachedNonterminalEdgesOf(vioNode).iterator();
        while (attachedNtIter.hasNext()) {

            int ntEdge = attachedNtIter.next();
            Nonterminal nt = hc.labelOf(ntEdge);
            int tentacle = hc.attachedNodesOf(ntEdge).indexOf(vioNode);


            GrammarResponse rulesToApply;
            try {
                rulesToApply = ruleManager.getRulesFor(nt, tentacle, requiredSelectorLabel);
                Collection<HeapConfiguration> materializationResults =
                        ruleApplier.applyGrammarResponseTo(hc, ntEdge, rulesToApply);

                materializationResults.forEach(materilizedGraph ->
                        res.add(current.shallowCopyWithUpdateHeap(materilizedGraph)));

            } catch (UnexpectedNonterminalTypeException e) {
                logger.error("rule Manager cannot deal with this nonterminal type: " + nt.getClass());
            } catch (WrongResponseTypeException e) {
                logger.error("ruleApplier cannot handle the GrammarResponse created by ruleManager");
            }


        }

        return res;
    }

    Pair<Integer, String> getActualViolationPoint(ProgramState state,
                                                  ViolationPoints potentialViolationPoints) {

        for (String variableName : potentialViolationPoints.getVariables()) {

            String scopedName = state.getVariableNameInHeap(variableName);

            try {
                int variableTarget = state.getHeap().targetOf(state.getHeap().variableWith(scopedName));


                List<SelectorLabel> selectors = state.getHeap().selectorLabelsOf(variableTarget);

                for (String label : potentialViolationPoints.getSelectorsOf(variableName)) {

                    if (!MatchingUtil.containsMatch(selectors, s -> s.hasLabel(label))) {
                        return new Pair<>(variableTarget, label);
                    }
                }

            } catch (IllegalArgumentException e) {
                logger.debug("the variable " + scopedName + "of the violationPoint was not present");
            }
        }

        return null;
    }
}
