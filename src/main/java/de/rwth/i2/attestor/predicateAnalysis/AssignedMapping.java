package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.predicateAnalysis.relativeIndex.RelativeIndex;
import de.rwth.i2.attestor.util.tree.TreeNode;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class AssignedMapping<T> {
    private final TIntObjectMap<TreeNode<TIntObjectMap<T>>> table = new TIntObjectHashMap<>();

    public AssignedMapping(TIntObjectMap<T> initialValue) {
        table.put(0, new TreeNode<>(initialValue));
    }

    public int update(int key, TIntObjectMap<T> fragment) {
        TreeNode<TIntObjectMap<T>> child = table.get(key).addChild(fragment);
        int newKey = table.size();
        table.put(newKey, child);
        return newKey;
    }

    public T get(int key, int ntEdge) {
        TreeNode<TIntObjectMap<T>> node = table.get(key);

        while (!node.isRoot()) {
            if (node.getValue().containsKey(ntEdge)) {
                return node.getValue().get(ntEdge);
            } else {
                node = node.getParent();
            }
        }

        return null;
    }
}
