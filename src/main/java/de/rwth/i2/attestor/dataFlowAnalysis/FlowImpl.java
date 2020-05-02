package de.rwth.i2.attestor.dataFlowAnalysis;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class FlowImpl implements Flow {
    private final TIntSet initials = new TIntHashSet();
    private final TIntSet finals = new TIntHashSet();
    private final TIntObjectMap<TIntSet> flow = new TIntObjectHashMap<>();
    private final TIntObjectMap<TIntSet> reverseFlow = new TIntObjectHashMap<>();

    public FlowImpl() {
    }

    public FlowImpl(Flow flow) {
        this.initials.addAll(flow.getInitial());
        this.finals.addAll(flow.getFinal());

        flow.getLabels().forEach(from -> {
            flow.getSuccessors(from).forEach(to -> {
                add(from, to);

                return true;
            });

            return true;
        });
    }

    private void addToMap(TIntObjectMap<TIntSet> map, int from, int to) {
        if (!map.containsKey(from)) {
            map.put(from, new TIntHashSet());
        }

        map.get(from).add(to);
    }

    private void removeFromMap(TIntObjectMap<TIntSet> map, int from, int to) {
        if (!map.containsKey(from)) {
            return;
        }

        map.get(from).remove(to);
    }

    private TIntSet getFromMap(TIntObjectMap<TIntSet> map, int label) {
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

    public void add(int from, int to) {
        addToMap(flow, from, to);
        addToMap(reverseFlow, from, to);
    }

    public void remove(int from, int to) {
        removeFromMap(flow, from, to);
        removeFromMap(reverseFlow, from, to);
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
        return getFromMap(flow, label);
    }

    @Override
    public TIntSet getPredecessors(int label) {
        return getFromMap(reverseFlow, label);
    }
}
