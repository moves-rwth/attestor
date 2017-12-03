package de.rwth.i2.attestor.grammar.inclusion;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.defaultState.HeapInclusionStrategy;
import de.rwth.i2.attestor.semantics.util.Constants;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class MinDistanceInclusionStrategy implements HeapInclusionStrategy {

    private final Grammar grammar;

    public MinDistanceInclusionStrategy(Grammar grammar) {

        this.grammar = grammar;
    }

    @Override
    public boolean subsumes(HeapConfiguration left, HeapConfiguration right) {

        if (left == null || right == null) {
            return false;
        }

        if (left.equals(right)) {
            return true;
        }

        TIntArrayList criticalEdges = computeCriticalEdges(right);
        return subsumes(left, right, criticalEdges);
    }

    private boolean subsumes(HeapConfiguration left, HeapConfiguration right, TIntArrayList criticalEdges) {

        for (int i = 0; i < criticalEdges.size(); i++) {
            int edge = criticalEdges.get(i);
            Nonterminal label = right.labelOf(edge);

            for (HeapConfiguration hc : grammar.getRightHandSidesFor(label)) {

                HeapConfiguration materializedRight = right.clone().builder()
                        .replaceNonterminalEdge(edge, hc)
                        .build();
                TIntArrayList updatedCriticalEdges = new TIntArrayList(criticalEdges);
                updatedCriticalEdges.remove(edge);

                if (left.equals(materializedRight) || subsumes(left, materializedRight, updatedCriticalEdges)) {
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
