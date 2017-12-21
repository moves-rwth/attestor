package de.rwth.i2.attestor.grammar.languageInclusion;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.concretization.SingleStepConcretizationStrategy;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.util.Constants;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Iterator;

public class LanguageInclusionImpl extends SceneObject implements LanguageInclusionStrategy {

    private final CanonicalizationStrategy canonicalizationStrategy;
    private final SingleStepConcretizationStrategy singleStepConcretizationStrategy;

    public LanguageInclusionImpl(SceneObject sceneObject) {
        super(sceneObject);

        this.canonicalizationStrategy = scene().strategies().getLenientCanonicalizationStrategy();
        this.singleStepConcretizationStrategy = scene().strategies().getSingleStepConcretizationStrategy();
    }

    @Override
    public boolean includes(HeapConfiguration left, HeapConfiguration right) {

        if(left == right) {
            return true;
        }

        if(left == null) {
            return false;
        }

        if(left.equals(right)) {
            return true;
        }

        HeapConfiguration canonicalLeft = canonicalizationStrategy.canonicalize(left);

        if(canonicalLeft.equals(right)) {
            return true;
        }

        if(scene().options().getAbstractionDistance() == 0 || scene().options().isIndexedMode()) {
            return false;
        }

        // if abstraction distance is 1, we might have missed some abstraction. Hence, we have to concretize
        // some edges of the heap configuration on the right-hand side to check language inclusion.
        TIntArrayList criticalEdges = computeCriticalEdges(right);
        return subsumes(canonicalLeft, right, criticalEdges);
    }

    private boolean subsumes(HeapConfiguration left, HeapConfiguration right, TIntArrayList criticalEdges) {

        for (int i = 0; i < criticalEdges.size(); i++) {
            int edge = criticalEdges.get(i);
            Iterator<HeapConfiguration> concretizations = singleStepConcretizationStrategy.concretize(right, edge);
            while(concretizations.hasNext()) {
                HeapConfiguration hc = concretizations.next();
                TIntArrayList updatedCriticalEdges = new TIntArrayList(criticalEdges);
                updatedCriticalEdges.remove(edge);
                if (left.equals(hc) || subsumes(left, hc, updatedCriticalEdges)) {
                    return true;
                }
            }
        }

        return false;
    }

    private TIntArrayList computeCriticalEdges(HeapConfiguration hc) {

        TIntSet variables = new TIntHashSet(hc.countVariableEdges());
        TIntIterator varIterator = hc.variableEdges().iterator();
        while (varIterator.hasNext()) {
            int varEdge = varIterator.next();
            if (!Constants.isConstant(hc.nameOf(varEdge))) {
                variables.add(varEdge);
            }
        }

        TIntArrayList criticalEdges = new TIntArrayList(hc.countNonterminalEdges());
        TIntIterator ntIterator = hc.nonterminalEdges().iterator();

        nonterminalCheck:
        while (ntIterator.hasNext()) {
            int ntEdge = ntIterator.next();
            Nonterminal label = hc.labelOf(ntEdge);
            TIntArrayList att = hc.attachedNodesOf(ntEdge);
            for (int i = 0; i < label.getRank(); i++) {
                if (label.isReductionTentacle(i)) {
                    int node = att.get(i);
                    TIntArrayList attVars = hc.attachedVariablesOf(node);
                    for (int j = 0; j < attVars.size(); j++) {
                        if (variables.contains(attVars.get(j))) {
                            criticalEdges.add(ntEdge);
                            continue nonterminalCheck;
                        }
                    }
                }
            }
        }

        return criticalEdges;
    }
}
