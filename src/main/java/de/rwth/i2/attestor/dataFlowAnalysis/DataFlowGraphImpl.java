package de.rwth.i2.attestor.dataFlowAnalysis;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class DataFlowGraphImpl<T> implements DataFlowGraph<T> {
    private final TIntObjectMap<T> values = new TIntObjectHashMap<>();
    private final TIntSet initials = new TIntHashSet();
    private final TIntSet finals = new TIntHashSet();
    private final TIntObjectMap<TIntSet> flow = new TIntObjectHashMap<>();
    private final TIntObjectMap<TIntSet> reverseFlow = new TIntObjectHashMap<>();

    private void add(TIntObjectMap<TIntSet> map, int from, int to) {
        if (!map.containsKey(from)) {
            map.put(from, new TIntHashSet());
        }

        map.get(from).add(to);
    }

    private TIntSet get(TIntObjectMap<TIntSet> map, int label) {
        TIntSet result = map.get(label);

        if (result == null) {
            result = new TIntHashSet();
        }

        return TCollections.unmodifiableSet(result);
    }

    private void addOrRemove(TIntSet set, int label, boolean add) {
        if (add) {
            set.add(label);
        } else {
            set.remove(label);
        }
    }

    public void setInitial(int label, boolean isInitial) {
        addOrRemove(initials, label, isInitial);
    }

    public void setFinal(int label, boolean isFinal) {
        addOrRemove(finals, label, isFinal);
    }

    public int addLabel(T value) {
        int label = values.size();
        values.put(label, value);
        return label;
    }

    public void addFlow(int from, int to) {
        add(flow, from, to);
        add(reverseFlow, from, to);
    }

    @Override
    public T getNode(int label) {
        return values.get(label);
    }

    @Override
    public TIntSet getLabels() {
        TIntSet result = new TIntHashSet();
        result.addAll(flow.keySet());
        result.addAll(reverseFlow.keySet());
        return TCollections.unmodifiableSet(result);
    }

    @Override
    public TIntSet getInitial() {
        return TCollections.unmodifiableSet(initials);
    }

    @Override
    public TIntSet getFinal() {
        return TCollections.unmodifiableSet(finals);
    }

    @Override
    public TIntSet getSuccessors(int label) {
        return get(flow, label);
    }

    @Override
    public TIntSet getPredecessors(int label) {
        return get(reverseFlow, label);
    }
}
