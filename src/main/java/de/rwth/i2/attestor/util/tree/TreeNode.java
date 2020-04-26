package de.rwth.i2.attestor.util.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TreeNode<T> {
    private T value;
    private TreeNode<T> parent;
    private final List<TreeNode<T>> children = new ArrayList<>();

    public TreeNode(T value) {
        this.value = value;
    }

    public TreeNode<T> addChild(T value) {
        TreeNode<T> child = new TreeNode<>(value);
        this.addChild(child);
        return child;
    }

    public void addChild(TreeNode<T> child) {
        child.parent = this;
        this.children.add(child);
    }

    public void addChildren(Collection<TreeNode<T>> children) {
        children.forEach(this::addChild);
    }

    public List<TreeNode<T>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isLeaf() {
        return this.children.isEmpty();
    }
}
