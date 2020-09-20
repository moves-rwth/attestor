package de.rwth.i2.attestor.dataFlowAnalysis;

import de.rwth.i2.attestor.domain.Lattice;
import de.rwth.i2.attestor.util.Pair;

import java.util.*;

public final class WorklistAlgorithm<D> implements EquationSolver<D> {
    private final DataFlowAnalysis<D> framework;
    private final Flow flow;
    private final Lattice<D> lattice;
    private final D extremalValue;
    private final Set<Integer> extremalLabels;
    private final WideningOperator<D> wideningOperator;
    private final Deque<Pair<Integer, Integer>> worklist = new ArrayDeque<>();


    public WorklistAlgorithm(DataFlowAnalysis<D> framework) {
        this.framework = framework;
        flow = framework.getFlow();
        lattice = framework.getLattice();
        extremalValue = framework.getExtremalValue();
        extremalLabels = framework.getExtremalLabels();
        wideningOperator = framework.getWideningOperator();
    }

    @Override
    public Map<Integer, D> solve() {
        worklist.clear();
        Map<Integer, D> analysis = new HashMap<>();

        // initialization
        for (Integer label : flow.getLabels()) {
            for (Integer successor : flow.getSuccessors(label)) {
                worklist.push(new Pair<>(label, successor));
            }

            if (extremalLabels.contains(label)) {
                analysis.put(label, extremalValue);
            } else {
                analysis.put(label, lattice.leastElement());
            }
        }

        // iteration
        iterate(analysis);
        return analysis;
    }

    @Override
    public Map<Integer, D> narrow(Map<Integer, D> initial) {
        worklist.clear();
        Map<Integer, D> analysis = new HashMap<>(initial);

        // initialization
        for (Integer label : flow.getLabels()) {
            for (Integer successor : flow.getSuccessors(label)) {
                worklist.push(new Pair<>(label, successor));
            }
        }

        // iteration
        iterate(analysis);
        return analysis;
    }

    private void iterate(Map<Integer, D> analysis) {
        while (!worklist.isEmpty()) {
            Pair<Integer, Integer> pair = worklist.pop();
            int from = pair.first();
            int to = pair.second();

            D outState = framework.getTransferFunction(from, to).apply(analysis.get(from));
            if (!lattice.isLessOrEqual(outState, analysis.get(to))) {
                Set<D> s = new HashSet<>();
                s.add(analysis.get(to));
                s.add(outState);
                analysis.put(to, wideningOperator.widen(s));

                for (Integer successor : flow.getSuccessors(to)) {
                    Pair<Integer, Integer> n = new Pair<>(to, successor);
                    if (!worklist.contains(n)) {
                        worklist.push(n);
                    }
                }
            }
        }
    }
}
