package de.rwth.i2.attestor.dataFlowAnalysis;

import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;

import java.util.*;

public final class WorklistAlgorithm<D> implements EquationSolver<D> {
    @Override
    public Map<Integer, D> solve(DataFlowAnalysis<D> framework) {
        Flow flow = framework.getFlow();
        Lattice<D> lattice = framework.getLattice();
        D extremalValue = framework.getExtremalValue();
        Set<Integer> extremalLabels = framework.getExtremalLabels();

        Map<Integer, D> analysis = new HashMap<>();
        Stack<Pair<Integer, Integer>> worklist = new Stack<>();

        // initialization
        for (Integer label : flow.getLabels()) {
            for (Integer successor : flow.getSuccessors(label)) {
                worklist.add(new Pair<>(label, successor));
            }

            if (extremalLabels.contains(label)) {
                analysis.put(label, extremalValue);
            } else {
                analysis.put(label, lattice.leastElement());
            }
        }

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

                for (Integer successor : flow.getSuccessors(to)) {
                    Pair<Integer, Integer> n = new Pair<>(to, successor);
                    if (!worklist.contains(n)) {
                        worklist.push(n);
                    }
                }
            }
        }

        return analysis;
    }
}
