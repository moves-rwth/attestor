package de.rwth.i2.attestor.dataFlowAnalysis;

import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public final class WorklistAlgorithm<D> implements EquationSolver<D> {
    @Override
    public TIntObjectMap<D> solve(DataFlowAnalysis<D> framework) {
        Flow flow = framework.getFlow();
        Lattice<D> lattice = framework.getLattice();
        D extremalValue = framework.getExtremalValue();
        TIntSet extremalLabels = framework.getExtremalLabels();

        TIntObjectMap<D> analysis = new TIntObjectHashMap<>();
        Stack<Pair<Integer, Integer>> worklist = new Stack<>();

        // initialization
        flow.getLabels().forEach(from -> {
            flow.getSuccessors(from).forEach(to -> {
                worklist.add(new Pair<>(from, to));

                return true;
            });

            return true;
        });

        flow.getLabels().forEach(label -> {
            if (extremalLabels.contains(label)) {
                analysis.put(label, extremalValue);
            } else {
                analysis.put(label, lattice.getLeastElement());
            }

            return true;
        });

        // iteration
        while (!worklist.isEmpty()) {
            Pair<Integer, Integer> pair = worklist.pop();
            int from = pair.first();
            int to = pair.second();

            D outState = framework.getTransferFunction(from, to).apply(analysis.get(from));
            if (!lattice.isLessOrEqual(outState, analysis.get(to))) {
                Set<D> s = new HashSet<>();
                s.add(analysis.get(to));
                s.add(outState);
                analysis.put(to, lattice.getLeastUpperBound(s));

                flow.getSuccessors(to).forEach(successor -> {
                    worklist.push(new Pair<>(to, successor));

                    return true;
                });
            }
        }

        return analysis;
    }
}
