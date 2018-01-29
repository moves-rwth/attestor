package de.rwth.i2.attestor.graph.heap.internal;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.digraph.LabeledDigraph;
import de.rwth.i2.attestor.graph.heap.*;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

/**
 * All the messy details of a {@link HeapConfigurationBuilder} for {@link InternalHeapConfiguration}s.
 * All identifiers of nodes, nonterminal edges, and variable edges used by InternalHeapConfigurationBuilder
 * are public identifiers.
 *
 * @author Christoph
 */
public class InternalHeapConfigurationBuilder implements HeapConfigurationBuilder {

    /**
     * The HeapConfiguration that is manipulated by this InternalHeapConfigurationBuilder.
     */
    private InternalHeapConfiguration heapConf;

    /**
     * Creates a new InternalHeapConfigurationBuilder for the provided InternalHeapConfiguration.
     * Note that an InternalHeapConfigurationBuilder is assumed to be unique for each InternalHeapConfiguration
     * and should thus only be created in InternalHeapConfiguration.builder().
     *
     * @param heapConf The InternalHeapConfiguration that should be changed by this builder.
     */
    InternalHeapConfigurationBuilder(InternalHeapConfiguration heapConf) {

        if (heapConf == null) {

            throw new NullPointerException();
        }

        this.heapConf = heapConf;

    }

    @Override
    public HeapConfiguration build() {

        cleanupGraphAndIDs();

        // invalidate this builder
        heapConf.builder = null;
        HeapConfiguration result = heapConf;
        heapConf = null;

        return result;
    }

    /**
     * Restores a compact graph representation while keeping all public IDs
     * of elements that have not been deleted unchanged.
     */
    private void cleanupGraphAndIDs() {
        // Swap all deleted elements to the end and remove them from the
        // graph to get a tight representation.
        // The obtained map stores all performed swaps
        int[] swaps = heapConf.graph.pack();

        // Update the mapping from private to public IDs such that swapped
        // private IDs still refer to the same public ID as before.
        heapConf.publicToPrivateIDs.transformValues(value -> {

            if (swaps[value] != HeapConfiguration.INVALID_ELEMENT) {
                return swaps[value];
            } else {
                return value;
            }
        });
    }

    @Override
    public HeapConfigurationBuilder addNodes(Type type, int count, TIntArrayList buffer) {

        if (type == null || buffer == null) {
            throw new NullPointerException();
        }

        if (count < 0) {
            throw new IllegalArgumentException("Provided count must be positive.");
        }

        for (int i = 0; i < count; i++) {

            int publicId = addPrivatePublicIdPair();
            heapConf.graph.addNode(type, 10, 10);
            buffer.add(publicId);
            ++heapConf.countNodes;
        }

        return this;
    }

    /**
     * Computes a new public and private ID and adds it to the HeapConfiguration.
     *
     * @return The computed public id.
     */
    private int addPrivatePublicIdPair() {

        int privateId = getNextPrivateId();
        int publicId = getNextPublicId();
        heapConf.publicToPrivateIDs.put(publicId, privateId);
        return publicId;
    }

    /**
     * @return The next private ID available in the graph.
     */
    private int getNextPrivateId() {

        return heapConf.graph.size();
    }

    /**
     * @return The next public ID available for the underlying HeapConfiguration
     */
    private int getNextPublicId() {

        int result = 0;
        while (heapConf.publicToPrivateIDs.containsKey(result)) {
            ++result;
        }

        return result;
    }

    @Override
    public HeapConfigurationBuilder removeIsolatedNode(int node) {

        int privateId = heapConf.getPrivateId(node);

        if (!heapConf.isNode(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a node.");
        }

        if (heapConf.graph.successorSizeOf(privateId) > 0
                || heapConf.graph.predecessorSizeOf(privateId) > 0) {
            throw new IllegalArgumentException("Provided node is not isolated.");
        }

        removeElement(node, privateId);
        --heapConf.countNodes;

        return this;
    }

    @Override
    public HeapConfigurationBuilder removeNode(int node) {

        int privateId = heapConf.getPrivateId(node);

        if (!heapConf.isNode(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a node.");
        }

        removeAttachedVariables(node);
        removeAttachedNonterminalEdges(node);
        removeIncomingSelectors(node);
        removeOutgoingSelectors(node);

        removeElement(node, privateId);
        --heapConf.countNodes;

        return this;
    }

    private void removeAttachedVariables(int node) {

        TIntArrayList varEdges = heapConf.attachedVariablesOf(node);
        for (int i = 0; i < varEdges.size(); i++) {
            int edge = varEdges.get(i);
            removeVariableEdge(edge);
        }
    }

    private void removeAttachedNonterminalEdges(int node) {

        TIntArrayList ntEdges = heapConf.attachedNonterminalEdgesOf(node);
        for (int i = 0; i < ntEdges.size(); i++) {
            int edge = ntEdges.get(i);
            removeNonterminalEdge(edge);
        }
    }

    private void removeIncomingSelectors(int node) {

        TIntIterator predecessorIterator = heapConf.predecessorNodesOf(node).iterator();
        while (predecessorIterator.hasNext()) {
            int predecessor = predecessorIterator.next();
            for (SelectorLabel sel : heapConf.selectorLabelsOf(predecessor)) {
                if (heapConf.selectorTargetOf(predecessor, sel) == node) {
                    removeSelector(predecessor, sel);
                }
            }
        }
    }

    private void removeOutgoingSelectors(int node) {

        for (SelectorLabel sel : heapConf.selectorLabelsOf(node)) {
            removeSelector(node, sel);
        }
    }

    /**
     * Removes an existing private ID from the underlying HeapConfiguration.
     *
     * @param publicId  The public ID of the removed element.
     * @param privateId The private ID of the removed element.
     * @return true iff the element with the given private ID was successfully removed
     */
    private boolean removeElement(int publicId, int privateId) {

        heapConf.publicToPrivateIDs.remove(publicId);
        return heapConf.graph.removeNodeAt(privateId);
    }

    @Override
    public HeapConfigurationBuilder addSelector(int from, SelectorLabel sel, int to) {

        int pFrom = heapConf.getPrivateId(from);
        int pTo = heapConf.getPrivateId(to);

        if (!heapConf.isNode(pFrom)) {
            throw new IllegalArgumentException("ID 'from' does not refer to a valid node.");
        }

        if (sel == null) {
            throw new NullPointerException();
        }

        if (!heapConf.isNode(pTo)) {
            throw new IllegalArgumentException("ID 'to' does not refer to a valid node.");
        }

        if (heapConf.graph.containsEdgeLabel(pFrom, sel)) {
            throw new IllegalArgumentException("Provided selector already exists.");
        }

        heapConf.graph.addEdge(pFrom, sel, pTo);

        return this;

    }

    @Override
    public HeapConfigurationBuilder removeSelector(int node, SelectorLabel sel) {

        int privateId = heapConf.getPrivateId(node);

        if (!heapConf.isNode(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a node: " + node);
        }

        if (sel == null) {
            throw new NullPointerException();
        }

        heapConf.graph.removeEdgeLabelAt(privateId, sel);

        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceSelector(int node, SelectorLabel oldSel, SelectorLabel newSel) {

        int privateId = heapConf.getPrivateId(node);

        if (!heapConf.isNode(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a node.");
        }

        if (oldSel == null || newSel == null) {
            throw new NullPointerException();
        }

        heapConf.graph.replaceEdgeLabel(privateId, oldSel, newSel);

        return this;
    }

    @Override
    public HeapConfigurationBuilder setExternal(int node) {

        int privateId = heapConf.getPrivateId(node);

        if (!heapConf.isNode(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a node: " + node);
        }

        if (heapConf.graph.isExternal(privateId)) {
            throw new IllegalArgumentException("Provided node is already external.");
        }

        heapConf.graph.setExternal(privateId);

        return this;
    }

    @Override
    public HeapConfigurationBuilder unsetExternal(int node) {

        int privateId = heapConf.getPrivateId(node);

        if (!heapConf.isNode(privateId)) {
            throw new IllegalArgumentException("Provided ID is not a node: " + node);
        }

        if (!heapConf.graph.isExternal(privateId)) {
            throw new IllegalArgumentException("Provided node is not external.");
        }

        heapConf.graph.unsetExternal(privateId);

        return this;
    }

    @Override
    public HeapConfigurationBuilder addVariableEdge(String name, int target) {

        int tId = heapConf.getPrivateId(target);

        if (name == null) {
            throw new NullPointerException();
        }

        if (!heapConf.isNode(tId)) {
            throw new IllegalArgumentException("Provided target does not correspond to a node.");
        }

        if (heapConf.variableWith(name) != HeapConfiguration.INVALID_ELEMENT) {
            throw new IllegalArgumentException("Variable already exists");
        }

        int publicId = addPrivatePublicIdPair();
        int privateId = heapConf.getPrivateId(publicId);

        // variable edges are attached to exactly one node and have no
        // incoming edges in the underlying graph
        heapConf.graph.addNode(new Variable(name), 1, 0);
        heapConf.graph.addEdge(privateId, 1, tId);
        ++heapConf.countVariableEdges;

        return this;
    }

    @Override
    public HeapConfigurationBuilder removeVariableEdge(int varEdge) {

        int privateId = heapConf.getPrivateId(varEdge);

        if (!isVariable(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a variable edge.");
        }

        if (removeElement(varEdge, privateId)) {
            --heapConf.countVariableEdges;
        }

        return this;
    }

    /**
     * Checks whether a provided private ID belonging to the underlying InternalHeapConfiguration
     * corresponds to a variable.
     *
     * @param privateId A private ID belonging to an element of the underlying InternalHeapConfiguration.
     * @return True if and only if privateId corresponds to a variable.
     */
    private boolean isVariable(int privateId) {

        return heapConf.graph.nodeLabelOf(privateId) instanceof Variable;
    }

    @Override
    public HeapConfigurationBuilder addNonterminalEdge(Nonterminal label, TIntArrayList attachedNodes) {

        addNonterminalEdgeAndReturnId(label, attachedNodes);
        return this;
    }

    @Override
    public int addNonterminalEdgeAndReturnId(Nonterminal label, TIntArrayList attachedNodes) {

        if (label == null || attachedNodes == null) {
            throw new NullPointerException();
        }

        if (label.getRank() != attachedNodes.size()) {
            throw new IllegalArgumentException("The rank of the provided label and the size of the list of attached nodes do not coincide.");
        }

        int publicId = addPrivatePublicIdPair();
        int privateId = heapConf.getPrivateId(publicId);

        heapConf.graph.addNode(label, attachedNodes.size(), 0);
        for (int i = 0; i < attachedNodes.size(); i++) {
            int to = heapConf.getPrivateId(attachedNodes.get(i));
            if (!heapConf.isNode(to)) {
                throw new IllegalArgumentException("ID of one attached node does not actually correspond to a node.");
            }
            heapConf.graph.addEdge(privateId, i, to);
        }
        ++heapConf.countNonterminalEdges;

        return publicId;
    }

    @Override
    public NonterminalEdgeBuilder addNonterminalEdge(Nonterminal nt) {

        return new InternalNonterminalEdgeBuilder(nt, this);
    }

    @Override
    public HeapConfigurationBuilder removeNonterminalEdge(int ntEdge) {

        int privateId = heapConf.getPrivateId(ntEdge);

        if (!heapConf.isNonterminalEdge(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a nonterminal edge.");
        }

        if (removeElement(ntEdge, privateId)) {
            --heapConf.countNonterminalEdges;
        }

        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceNonterminal(int ntEdge, Nonterminal newNt) {

        int privateId = heapConf.getPrivateId(ntEdge);

        if (newNt == null) {
            throw new NullPointerException();
        }

        if (!heapConf.isNonterminalEdge(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a nonterminal edge.");
        }

        int rank = ((Nonterminal) heapConf.graph.nodeLabelOf(privateId)).getRank();
        if (rank != newNt.getRank()) {
            throw new IllegalArgumentException("The rank of the provided nonterminal is " +
                    "different from the original rank: " + rank + " vs. " + newNt.getRank());
        }

        heapConf.graph.replaceNodeLabel(privateId, newNt);

        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceNonterminalEdge(int ntEdge, HeapConfiguration replacement) {

        if (replacement == null) {
            throw new NullPointerException();
        }

        if (!(replacement instanceof InternalHeapConfiguration)) {
            throw new IllegalArgumentException("Provided replacement is not an InternalHeapConfiguration.");
        }

        InternalHeapConfiguration replacementHc = (InternalHeapConfiguration) replacement;
        int ntPrivateId = heapConf.getPrivateId(ntEdge);

        if (!heapConf.isNonterminalEdge(ntPrivateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a nonterminal edge.");
        }

        // store originally attached nodes, because these are merged with the external nodes of replacementHc.
        TIntArrayList tentacles = heapConf.graph.successorsOf(ntPrivateId);

        if (tentacles.size() != replacement.countExternalNodes()) {
            throw new IllegalArgumentException("The rank of the nonterminal edge to be replaced " +
                    "does not match the rank of the replacement.");
        }

        removeNonterminalEdge(ntEdge);

        addReplacementGraph(replacementHc, tentacles);

        return this;
    }

    /**
     * Adds the provided InternalHeapConfiguration to the one underlying this builder.
     * Its external nodes will be merged with the provided list of nodes.
     *
     * @param replacement The InternalHeapConfiguration that should be added to the underlying
     *                    InternalHeapConfiguration.
     * @param tentacles   A list of nodes that determines how replacement and the underlying HeapConfiguration are
     *                    glued together. Thus, we require that replacement.countExternalNodes() equals tentacles.size().
     */
    private void addReplacementGraph(InternalHeapConfiguration replacement, TIntArrayList tentacles) {

        int replSize = replacement.graph.size();
        TIntArrayList newElements = computeNewElements(replacement, tentacles);

        // In the second pass we add all selectors for nodes as well as nonterminal hyperedges
        // and their tentacles.
        for (int i = 0; i < replSize; i++) {

            if (replacement.isNode(i)) {

                addNodeFromReplacement(replacement, newElements, i);

            } else if (replacement.isNonterminalEdge(i)) {

                addNtEdgeFromReplacement(replacement, newElements, i);

            } else if (replacement.isVariable(i)) {

                addVariableFromReplacement(replacement, newElements, i);
            }
        }
    }

    /**
     * Creates a map that containsSubsumingState the new private IDs assigned to elements of a provided InternalHeapConfiguration
     * that is added to the underlying InternalHeapConfiguration.
     * For all elements except external nodes new private IDs are created.
     * For each external node, the map containsSubsumingState the private ID of a node in the underlying InternalHeapConfiguration
     * that is merged with it.
     *
     * @param replacement The InternalHeapConfiguration that should be added to the underlying HeapConfiguration.
     * @param tentacles   A list of nodes in the underlying HeapConfiguration that determines the nodes that are
     *                    merged with the external nodes of replacement.
     * @return A list that maps each element of replacement to its private ID in the underlying HeapConfiguration.
     */
    private TIntArrayList computeNewElements(InternalHeapConfiguration replacement, TIntArrayList tentacles) {

        int replSize = replacement.graph.size();
        TIntArrayList newElements = new TIntArrayList(replSize);

        for (int i = 0; i < replSize; i++) {

            if (replacement.isNode(i)) {

                int extPos = replacement.graph.externalPosOf(i);
                if (extPos != LabeledDigraph.INVALID) {
                    newElements.add(tentacles.get(extPos));
                } else {

                    int privateId = getNextPrivateId();
                    addPrivatePublicIdPair();
                    heapConf.graph.addNode(replacement.graph.nodeLabelOf(i), 10, 10);
                    ++heapConf.countNodes;
                    newElements.add(privateId);
                }
            } else {

                newElements.add(LabeledDigraph.INVALID);
            }
        }

        return newElements;
    }

    /**
     * Adds a single node from a HeapConfiguration that should be added to the underlying HeapConfiguration with
     * the provided private ID. Furthermore, all edges of the node are added to the underlying HeapConfiguration.
     *
     * @param replacement The HeapConfiguration that should be added to the underlying HeapConfiguration.
     * @param newElements A list mapping all elements of replacement to their new private IDs in the underlying
     *                    InternalHeapConfiguration.
     * @param nodeIdToAdd The private ID of the node that should be added.
     */
    private void addNodeFromReplacement(InternalHeapConfiguration replacement,
                                        TIntArrayList newElements, int nodeIdToAdd) {

        int privateId = newElements.get(nodeIdToAdd);
        TIntArrayList successors = replacement.graph.successorsOf(nodeIdToAdd);
        for (int j = 0; j < successors.size(); j++) {
            Object label = replacement.graph.edgeLabelAt(nodeIdToAdd, j);
            int to = newElements.get(successors.get(j));
            heapConf.graph.addEdge(privateId, label, to);
        }
    }

    /**
     * Adds a nonterminal edge from a HeapConfiguration that should be added to the underlying HeapConfiguration
     * with the provided privateID. Furthermore, all tentacles to nodes are set.
     *
     * @param replacement The HeapConfiguration that should be added to the underlying HeapConfiguration.
     * @param newElements A list mapping all elements of replacement to their new private IDs in the underlying
     *                    InternalHeapConfiguration.
     * @param ntIdToAdd   The private ID of the nonterminal edge that should be added.
     */
    private void addNtEdgeFromReplacement(InternalHeapConfiguration replacement, TIntArrayList newElements, int ntIdToAdd) {

        int freshPrivateId = getNextPrivateId();
        addPrivatePublicIdPair();
        TIntArrayList successors = replacement.graph.successorsOf(ntIdToAdd);
        heapConf.graph.addNode(replacement.graph.nodeLabelOf(ntIdToAdd), successors.size(), 0);
        ++heapConf.countNonterminalEdges;
        for (int j = 0; j < successors.size(); j++) {
            int to = newElements.get(successors.get(j));
            heapConf.graph.addEdge(freshPrivateId, j, to);
        }
    }

    /**
     * Adds a variable edge from a HeapConfiguration that should be added to the underlying HeapConfiguration
     * with the provided privateID. Furthermore it is attached to the node corresponding to its target.
     *
     * @param replacement The HeapConfiguration that should e added to the underlying HeapConfiguration
     * @param newElements A list mapping all nodes of replacement to their new private IDs in the
     *                    underlying Internal HeapConfiguration.
     * @param varIDtoAdd  The private ID of the variable edge in replacement that should be added.
     */
    private void addVariableFromReplacement(InternalHeapConfiguration replacement, TIntArrayList newElements, int varIDtoAdd) {

        int freshPrivateId = getNextPrivateId();
        addPrivatePublicIdPair();
        int target = replacement.graph.successorsOf(varIDtoAdd).get(0);
        heapConf.graph.addNode(replacement.graph.nodeLabelOf(varIDtoAdd), 1, 0);
        ++heapConf.countVariableEdges;
        heapConf.graph.addEdge(freshPrivateId, 1, newElements.get(target));
    }

    @Override
    public HeapConfigurationBuilder replaceMatching(Matching matching, Nonterminal nonterminal) {

        if (matching == null || nonterminal == null) {
            throw new NullPointerException();
        }

        InternalHeapConfiguration pattern = (InternalHeapConfiguration) matching.pattern();

        if (pattern.countExternalNodes() != nonterminal.getRank()) {
            throw new IllegalArgumentException("The number of external nodes in pattern must " +
                    "match the rank of the provided nonterminal.");
        }

        InternalMatching internalMatching = (InternalMatching) matching;

        // First remove all selector edges and tentacles that also occur in pattern
        removeSelectorAndTentacleEdges(internalMatching, pattern);

        removeNonExternalNodes(internalMatching, pattern);

        addMatchingNonterminalEdge(internalMatching, pattern, nonterminal);

        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceNodeType(int node, Type newType) {

        if (newType == null) {
            throw new NullPointerException();
        }

        int privateId = heapConf.getPrivateId(node);
        if (!heapConf.isNode(privateId)) {
            throw new IllegalArgumentException("Provided ID does not correspond to a node.");
        }

        heapConf.graph.replaceNodeLabel(privateId, newType);
        return this;
    }

    @Override
    public HeapConfigurationBuilder replaceMatchingWithCollapsedExternals(Matching matching,
                                                                          Nonterminal nonterminal,
                                                                          TIntArrayList externalIndicesMap) {

        if (matching == null || nonterminal == null) {
            throw new NullPointerException();
        }

        InternalHeapConfiguration pattern = (InternalHeapConfiguration) matching.pattern();

        // Use the mapping of externals instead of the actual smaller number of external nodes
        if (externalIndicesMap.size() != nonterminal.getRank()) {
            throw new IllegalArgumentException("The number of external nodes in pattern must " +
                    "match the rank of the provided nonterminal.");
        }

        InternalMatching internalMatching = (InternalMatching) matching;

        // First remove all selector edges and tentacles that also occur in pattern
        removeSelectorAndTentacleEdges(internalMatching, pattern);

        removeNonExternalNodes(internalMatching, pattern);

        addMatchingNonterminalEdgeWithCollapsedExternals(internalMatching, pattern, nonterminal, externalIndicesMap);

        return this;
    }

    @Override
    public HeapConfigurationBuilder mergeExternals(TIntArrayList extIndicesMap) {

        TIntArrayList originalExternalNodes = heapConf.externalNodes();

        TIntIterator iterator = originalExternalNodes.iterator();
        while (iterator.hasNext()) {
            int ext = iterator.next();
            unsetExternal(ext);
        }

        int countNewExternalNodes = extIndicesMap.max();
        TIntArrayList newExternalNodes = new TIntArrayList(countNewExternalNodes);

        for(int newExt = 0; newExt <= countNewExternalNodes; newExt++) {

            int mergedNode = mergeNodesMappedTo(newExt, extIndicesMap, originalExternalNodes);
            newExternalNodes.add(mergedNode);
        }

        iterator = newExternalNodes.iterator();
        while (iterator.hasNext()) {
            int ext = iterator.next();
            setExternal(ext);
        }

        return this;
    }

    private int mergeNodesMappedTo(int newExt, TIntArrayList extIndicesMap, TIntArrayList originalExternalNodes) {

        int mergedNode = HeapConfiguration.INVALID_ELEMENT;
        for(int oldIndex = 0; oldIndex < originalExternalNodes.size(); oldIndex++) {
            int newIndex = extIndicesMap.get(oldIndex);
            if(newIndex == newExt) {
                int oldNode = originalExternalNodes.get(oldIndex);
                if(mergedNode == HeapConfiguration.INVALID_ELEMENT) {
                    mergedNode = oldNode;
                } else {
                    mergeNodeInto(oldNode, mergedNode);
                }
            }
        }

        if(mergedNode == HeapConfiguration.INVALID_ELEMENT) {
            throw new IllegalStateException("Unable to merge external nodes.");
        }
        return mergedNode;
    }

    private void mergeNodeInto(int oldNode, int mergedNode) {

        mergeOutgoingSelectorsInto(oldNode, mergedNode);
        mergeIngoingSelectorsInto(oldNode, mergedNode);
        mergeNonterminalTentaclesInto(oldNode, mergedNode);
        removeIsolatedNode(oldNode);
    }



    private void mergeOutgoingSelectorsInto(int oldNode, int mergedNode) {

        for(SelectorLabel sel : heapConf.selectorLabelsOf(oldNode)) {
            int target = heapConf.selectorTargetOf(oldNode, sel);
            addSelector(mergedNode, sel, target);
            removeSelector(oldNode, sel);
        }
    }

    private void mergeIngoingSelectorsInto(int oldNode, int mergedNode) {

        TIntIterator predecessorIterator = heapConf.predecessorNodesOf(oldNode).iterator();
        while(predecessorIterator.hasNext()) {
            int predecessor = predecessorIterator.next();
            for(SelectorLabel sel : heapConf.selectorLabelsOf(predecessor)) {
                int target = heapConf.selectorTargetOf(predecessor, sel);
                if(target == oldNode) {
                    removeSelector(predecessor, sel);
                    addSelector(predecessor, sel, mergedNode);
                }

            }
        }
    }

    private void mergeNonterminalTentaclesInto(int oldNode, int mergedNode) {

        TIntIterator edgeIterator = heapConf.attachedNonterminalEdgesOf(oldNode).iterator();
        while (edgeIterator.hasNext()) {
            int edge = edgeIterator.next();
            TIntArrayList tentacles = heapConf.attachedNodesOf(edge);
            TIntArrayList newTentacles = new TIntArrayList(tentacles);
            boolean changed = false;
            for(int i=0; i < tentacles.size(); i++) {
                int target = tentacles.get(i);
                if(target == oldNode) {
                    newTentacles.set(i, mergedNode);
                    changed = true;
                }
            }
            if(changed) {
                Nonterminal nt = heapConf.labelOf(edge);
                addNonterminalEdge(nt, newTentacles);
                removeNonterminalEdge(edge);
            }

        }

    }

    /**
     * Removes all selector and tentacle edges in the underlying HeapConfiguration that belong to the provided
     * HeapConfiguration according to the provided matching.
     *
     * @param matching A mapping from the provided HeapConfiguration to elements of the underlying HeapConfiguration.
     * @param pattern  A HeapConfiguration that is embedded in the underlying HeapConfiguration.
     */
    private void removeSelectorAndTentacleEdges(InternalMatching matching, InternalHeapConfiguration pattern) {

        for (int i = 0; i < pattern.graph.size(); i++) {

            int match = matching.internalMatch(i);

            for (int j = 0; j < pattern.graph.successorSizeOf(i); j++) {

                Object l = pattern.graph.edgeLabelAt(i, j);
                if (l instanceof SelectorLabel) {

                    heapConf.graph.removeEdgeLabelAt(match, l);
                }
            }
        }
    }

    /**
     * Removes all nodes in the underlying HeapConfiguration that belong to the provided HeapConfiguration
     * according to the provided matching.
     *
     * @param matching A mapping from the provided HeapConfiguration to elements of the underlying HeapConfiguration.
     * @param pattern  A HeapConfiguration that is embedded in the underlying HeapConfiguration.
     */
    private void removeNonExternalNodes(InternalMatching matching, InternalHeapConfiguration pattern) {

        for (int i = 0; i < pattern.graph.size(); i++) {

            if (!pattern.graph.isExternal(i)) {
                int match = matching.internalMatch(i);

                if (pattern.isNode(i)) {
                    --heapConf.countNodes;
                } else if (pattern.isNonterminalEdge(i)) {
                    --heapConf.countNonterminalEdges;
                }

                heapConf.graph.removeNodeAt(match);
            }
        }

        heapConf.publicToPrivateIDs.retainEntries(
                (key, value) -> heapConf.graph.containsNode(value)
        );
    }

    /**
     * Add a nonterminal hyperedge in the provided HeapConfiguration to the underlying HeapConfiguration
     * that is labeled with the provided Nonterminal. Its tentacles are determined by the provided matching.
     *
     * @param matching    A mapping from the provided HeapConfiguration to elements of the underlying HeapConfiguration.
     * @param pattern     A HeapConfiguration that is embedded in the underlying HeapConfiguration.
     * @param nonterminal The label of the added hyperedge.
     */
    private void addMatchingNonterminalEdge(InternalMatching matching,
                                            InternalHeapConfiguration pattern, Nonterminal nonterminal) {

        int privateId = getNextPrivateId();
        addPrivatePublicIdPair();
        heapConf.graph.addNode(nonterminal, nonterminal.getRank(), 0);
        for (int i = 0; i < nonterminal.getRank(); i++) {

            int extId = pattern.graph.externalNodeAt(i);
            if (extId == LabeledDigraph.INVALID) {
                throw new IllegalArgumentException("One of the patterns external nodes could not be matched");
            }

            int t = matching.internalMatch(extId);
            heapConf.graph.addEdge(privateId, i, t);
        }
        ++heapConf.countNonterminalEdges;
    }

    private void addMatchingNonterminalEdgeWithCollapsedExternals(InternalMatching matching,
                                                                  InternalHeapConfiguration pattern,
                                                                  Nonterminal nonterminal,
                                                                  TIntArrayList externalIndicesMap) {

        int privateId = getNextPrivateId();
        addPrivatePublicIdPair();
        heapConf.graph.addNode(nonterminal, nonterminal.getRank(), 0);
        for (int i = 0; i < nonterminal.getRank(); i++) {

            int extPosition = externalIndicesMap.get(i);
            int extId = pattern.graph.externalNodeAt(extPosition);
            if (extId == LabeledDigraph.INVALID) {
                throw new IllegalArgumentException("One of the patterns external nodes could not be matched");
            }

            int t = matching.internalMatch(extId);
            heapConf.graph.addEdge(privateId, i, t);
        }
        ++heapConf.countNonterminalEdges;
    }



}
