package de.rwth.i2.attestor.dataFlowAnalysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlowImpl implements Flow {
    private final Set<Integer> initials = new HashSet<>();
    private final Set<Integer> finals = new HashSet<>();
    private final Map<Integer, Set<Integer>> flow = new HashMap<>();
    private final Map<Integer, Set<Integer>> reverseFlow = new HashMap<>();

    public FlowImpl() {
    }

    public FlowImpl(Flow flow) {
        this.initials.addAll(flow.getInitial());
        this.finals.addAll(flow.getFinal());

        for (Integer from : flow.getLabels()) {
            for (Integer to : flow.getSuccessors(from)) {
                add(from, to);
            }
        }
    }

    private static void addToMap(Map<Integer, Set<Integer>> map, int from, int to) {
        if (!map.containsKey(from)) {
            map.put(from, new HashSet<>());
        }

        map.get(from).add(to);
    }

    private static void removeFromMap(Map<Integer, Set<Integer>> map, int from, int to) {
        if (!map.containsKey(from)) {
            return;
        }

        map.get(from).remove(to);
    }

    private static Set<Integer> getFromMap(Map<Integer, Set<Integer>> map, int label) {
        Set<Integer> result = map.get(label);

        if (result == null) {
            result = new HashSet<>();
        }

        return Collections.unmodifiableSet(result);
    }

    private static void addOrRemove(Set<Integer> set, int label, boolean add) {
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
        addToMap(reverseFlow, to, from);
    }

    public void remove(int from, int to) {
        removeFromMap(flow, from, to);
        removeFromMap(reverseFlow, to, from);
    }

    @Override
    public Set<Integer> getLabels() {
        Set<Integer> result = new HashSet<>();
        result.addAll(flow.keySet());
        result.addAll(reverseFlow.keySet());
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<Integer> getInitial() {
        return Collections.unmodifiableSet(initials);
    }

    @Override
    public Set<Integer> getFinal() {
        return Collections.unmodifiableSet(finals);
    }

    @Override
    public Set<Integer> getSuccessors(int label) {
        return getFromMap(flow, label);
    }

    @Override
    public Set<Integer> getPredecessors(int label) {
        return getFromMap(reverseFlow, label);
    }
}
