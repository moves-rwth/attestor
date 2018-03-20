package de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

/**
 * This is an algorithm class storing some of its
 * intermediate results as fields to reduce parameters.
 *
 * @author hannah
 */
class ReachableFragmentComputer extends SceneObject {

    HeapConfiguration input;
    HeapConfigurationBuilder reachableFragmentBuilder;
    HeapConfigurationBuilder remainingFragmentBuilder;
    // maps the nodeIds in the input on nodeIds in the reachable fragment
    Map<Integer, Integer> idMapping;
    private String displayName;


    public ReachableFragmentComputer(SceneObject sceneObject, String displayName, HeapConfiguration input) {

        super(sceneObject);
        this.displayName = displayName;
        this.input = input;
    }

    /**
     * Computes the fragment reachable from the parameters and
     * the nodes within the fragment reachable from the variables (cutpoints).
     * Replaces this fragment in the remaining fragment by a nonterminalEdge
     * attached to all cutpoints.
     *
     * @return <reachableFragment,<remainingFragment, nt position>>
     */
    public Pair<HeapConfiguration, Pair<HeapConfiguration, Integer>> prepareInput() {

        this.reachableFragmentBuilder = input.getEmpty().builder();
        this.remainingFragmentBuilder = input.clone().builder();

        idMapping = new LinkedHashMap<>();

        Queue<Integer> queue = findAccessibleNodes();
        computeReachableFragment(queue);
        TIntArrayList tentacles = computeCutpoints();
        cutReachableFragment(tentacles);
        int idOfNonterminal = addIpaNonterminal(tentacles);

        HeapConfiguration reachableFragment = reachableFragmentBuilder.build();
        HeapConfiguration remainingFragment = remainingFragmentBuilder.build();
        Pair<HeapConfiguration, Integer> remainingInformation
                = new Pair<>(remainingFragment, idOfNonterminal);
        return new Pair<>(reachableFragment, remainingInformation);
    }


    /**
     * finds the nodes in the heap accessible by the callee.
     * This includes all nodes referenced by parameters or "this"
     * and all constants.
     * These nodes are included in the reachable Fragment
     * and enqueued as starting points for the computation of the reachable fragment.
     * Constants are not enqueued since they do not have outgoing transitions
     * (this is not solely a matter of performance, but could also result in unnecessary large
     * reachable fragments if nonterminal edges are attached to the constant).
     *
     * @return a queue containing the nodes accessed by parameters
     */
    private Queue<Integer> findAccessibleNodes() {

        Queue<Integer> queue = new ArrayDeque<>();

        TIntArrayList variables = input.variableEdges();
        for (int i = 0; i < variables.size(); i++) {
            final int variableEdge = variables.get(i);
            String variableName = input.nameOf(variableEdge);

            if (isParameter(variableName)) {
                handleParameter(variableEdge, variableName, queue);

            } else if (Constants.isConstant(variableName)) {
                handleConstant(variableEdge, variableName);
            }
        }
        return queue;
    }

    /**
     * Computes the fragment reachable from the nodes in the queue
     *
     * @param queue containing all nodes already determined to be reachable,
     *              but not yet handled.
     */
    private void computeReachableFragment(Queue<Integer> queue) {

        Set<Integer> visitedNonterminalEdges = new LinkedHashSet<>();

        while (!queue.isEmpty()) {

            int nodeId = queue.poll();

            addSelectorEdgesOf(nodeId, queue);
            addNonterminalEdgesOf(nodeId, queue, visitedNonterminalEdges);
        }
    }


    /**
     * for all paths starting at a variable, computes the first node in the reachable fragment.
     * If a variable points to a node inside the reachable fragment, this is the node itself.
     * <p>
     * marks these nodes as external in the reachable fragment
     * and adds them to the tentacles of the new nonteminal
     *
     * @return the tentacles of the new nonterminal
     */
    private TIntArrayList computeCutpoints() {

        TIntArrayList tentacles = new TIntArrayList();

        Deque<Integer> nodesReachableFromVariables = new ArrayDeque<>();
        Set<Integer> visited = new LinkedHashSet<>();

        addNodesReferencedByVariablesTo(nodesReachableFromVariables);
        addExternalNodesTo(nodesReachableFromVariables);

        while (!nodesReachableFromVariables.isEmpty()) {
            int nodeId = nodesReachableFromVariables.pop();
            if (idMapping.containsKey(nodeId)) {
                setCutpoint(nodeId, tentacles);
            } else {
                addNodesReachableThroughSelectorFrom(nodeId, nodesReachableFromVariables, visited);
                addNodesReachableThroughNonterminalEdge(nodeId, nodesReachableFromVariables, visited);
            }
        }

        return tentacles;
    }

    /**
     * removes all nodes which are in the reachable fragment and not cutpoints
     * from the remaining fragment
     *
     * @param cutpoints the list of cutpoints
     */
    private void cutReachableFragment(TIntArrayList cutpoints) {

        Set<Integer> nodesInReachableFragment = idMapping.keySet();
        for (Integer n : nodesInReachableFragment) {
            if (!cutpoints.contains(n)) {
                remainingFragmentBuilder.removeIsolatedNode(n);
            }
        }
    }


    /**
     * Adds the nonterminal edge representing the heap modified by the method
     * to all cutpoints
     *
     * @param tentacles the cutpoints
     * @return the public id of the freshly inserted nonterminal
     */
    private int addIpaNonterminal(TIntArrayList tentacles) {

        Nonterminal nt = getContractNonterminal(tentacles.size());

        int idOfNonterminal = remainingFragmentBuilder.addNonterminalEdgeAndReturnId(nt, tentacles);

        return idOfNonterminal;
    }

    // methodExecution to construct the reachable fragment

    /**
     * adds the variable edge and its target to the reachable fragment,
     * removes it from the remaining fragment and enques it for further reachability search
     *
     * @param variableEdge the variable edge in the input
     * @param variableName the id of the variable edge in the input
     * @param queue        the queue of the reachability search
     */
    private void handleParameter(final int variableEdge, String variableName, Queue<Integer> queue) {

        int targetedNode = input.targetOf(variableEdge);
        if (!idMapping.containsKey(targetedNode)) {
            queue.add(targetedNode);
            addNodeToReachableFragment(targetedNode);
        }
        handleVariableEdge(variableEdge, variableName, targetedNode);
    }

    /**
     * adds the target of the variable edge (here: refering to a constant)
     *  and its target to the reachable fragment
     * and removes it from the remaining fragment
     * (but does not consider it for further reachability search, since
     * constants cannot have outgoing selectors)
     *
     * @param variableEdge the id of variable edge (labelled with a constant) in the input
     * @param constantName the name of the variable edge
     */
    private void handleConstant(final int variableEdge, String constantName) {

        int targetedNode = input.targetOf(variableEdge);
        if (!idMapping.containsKey(targetedNode)) {
            addNodeToReachableFragment(targetedNode);
        }
        handleVariableEdge(variableEdge, constantName, targetedNode);
    }

    /**
     * adds the variable edge to the reachable fragment and removes it from the remaining fragment
     *
     * @param variableEdge the id of the variable edge in the input
     * @param variableName the name of the variable edge
     * @param targetedNode the id of the targeted node in the input
     */
    private void handleVariableEdge(final int variableEdge, String variableName, int targetedNode) {

        reachableFragmentBuilder.addVariableEdge(variableName, idMapping.get(targetedNode));
        remainingFragmentBuilder.removeVariableEdge(variableEdge);
    }

    /**
     * adds a node to the reachable fragment and marks it as the translation
     * of nodeToTranslate
     *
     * @param nodeToTranslate a node in the input which should be added to the rechable framgent
     */
    private void addNodeToReachableFragment(int nodeToTranslate) {

        Type type = input.nodeTypeOf(nodeToTranslate);
        TIntArrayList insertedNode = new TIntArrayList();
        reachableFragmentBuilder.addNodes(type, 1, insertedNode);
        idMapping.put(nodeToTranslate, insertedNode.get(0));
    }

    /**
     * adds the selector edges of the specified node to the reachable fragment.
     * If a target node of a selector is not yet included in the reachable fragment,
     * it is added and enqueued for further reachability search
     *
     * @param nodeId the node in the input whose selectors should be considered
     * @param queue  the queue of the reachability search
     */
    private void addSelectorEdgesOf(int nodeId, Queue<Integer> queue) {

        for (SelectorLabel sel : input.selectorLabelsOf(nodeId)) {
            int successorNode = input.selectorTargetOf(nodeId, sel);
            if (!idMapping.containsKey(successorNode)) {
                queue.add(successorNode);

                addNodeToReachableFragment(successorNode);
            }
            reachableFragmentBuilder.addSelector(idMapping.get(nodeId), sel, idMapping.get(successorNode));
            remainingFragmentBuilder.removeSelector(nodeId, sel);
        }
    }

    /**
     * nonterminal edges attached to the specified are included in the reachable fragment
     * and removed from the remaining fragment
     *
     * @param nodeId                  the node in the input to consider
     * @param queue                   the queue used for the reachability search
     * @param visitedNonterminalEdges the set of nonterminalEdges visited in the reachability
     *                                search (necessary, since a nonterminalEdge could be approached from multiple sides)
     */
    private void addNonterminalEdgesOf(int nodeId,
                                       Queue<Integer> queue, Set<Integer> visitedNonterminalEdges) {

        TIntArrayList nonterminalEdges = input.attachedNonterminalEdgesOf(nodeId);
        for (int i = 0; i < nonterminalEdges.size(); i++) {
            int nonterminalEdge = nonterminalEdges.get(i);
            if (visitedNonterminalEdges.contains(nonterminalEdge)) {
                continue; //each nonterminalEdge should only be considered once.
            }
            TIntArrayList translatedAttachedNodes = handleTentacles(queue, nonterminalEdge);
            reachableFragmentBuilder.addNonterminalEdge(input.labelOf(nonterminalEdge), translatedAttachedNodes);
            remainingFragmentBuilder.removeNonterminalEdge(nonterminalEdge);
            visitedNonterminalEdges.add(nonterminalEdge);
        }
    }

    /**
     * For all tentacles of the nonterminal edge
     * if they are not yet in the reachable fragment they are added
     * and the translated list of tentacles is computed
     *
     * @param queue           the queue used for the reachability search
     * @param nonterminalEdge the nonterminal edge to consider
     * @return the nodes corresponding to the tentacles in the reachable fragment
     */
    private TIntArrayList handleTentacles(Queue<Integer> queue, int nonterminalEdge) {

        TIntArrayList attachedNodes = input.attachedNodesOf(nonterminalEdge);
        TIntArrayList translatedAttachedNodes = new TIntArrayList();
        for (int n = 0; n < attachedNodes.size(); n++) {
            int attachedNode = attachedNodes.get(n);
            if (!idMapping.containsKey(attachedNode)) {
                queue.add(attachedNode);

                addNodeToReachableFragment(attachedNode);
            }
            translatedAttachedNodes.add(idMapping.get(attachedNode));
        }
        return translatedAttachedNodes;
    }

    // methodExecution for cutpoint computation

    /**
     * add all nodes referenced by variables (not parameters but constants)
     * to the dequeue
     *
     * @param nodesReachableFromVariables the dequeue to which the nodes are added
     */
    private void addNodesReferencedByVariablesTo(Deque<Integer> nodesReachableFromVariables) {

        TIntArrayList variables = input.variableEdges();
        for (int i = 0; i < variables.size(); i++) {
            int variableEdge = variables.get(i);
            if (!isParameter(input.nameOf(variableEdge))) {
                int referencedNode = input.targetOf(variableEdge);
                nodesReachableFromVariables.add(referencedNode);
            }
        }
    }

    /**
     * adds all external nodes of the input to the dequeue
     *
     * @param nodesReachableFromVariables the dequeue to which the external nodes are added
     */
    private void addExternalNodesTo(Deque<Integer> nodesReachableFromVariables) {

        TIntArrayList externals = input.externalNodes();
        for (int i = 0; i < externals.size(); i++) {
            nodesReachableFromVariables.add(externals.get(i));
        }
    }

    /**
     * adds all successor nodes of the specified node to the dequeue used
     *
     * @param nodeId  the node to consider
     * @param dequeue the deque to which the successors are added
     * @param visited the nodes already visited
     */
    private void addNodesReachableThroughSelectorFrom(int nodeId,
                                                      Deque<Integer> dequeue,
                                                      Set<Integer> visited) {

        TIntArrayList successors = input.successorNodesOf(nodeId);
        for (int i = 0; i < successors.size(); i++) {
            int reachableNode = successors.get(i);
            addReachableNode(reachableNode, dequeue, visited);
        }
    }

    /**
     * adds all nodes reachable by a hyperedge from the specified node to the dequeue
     *
     * @param nodeId  the node to consider
     * @param dequeue the dequeue to which the nodes are added
     * @param visited the nodes already visited
     */
    private void addNodesReachableThroughNonterminalEdge(int nodeId,
                                                         Deque<Integer> dequeue, Set<Integer> visited) {

        TIntArrayList nonterminalEdges = input.attachedNonterminalEdgesOf(nodeId);
        for (int edgeIndex = 0; edgeIndex < nonterminalEdges.size(); edgeIndex++) {

            TIntArrayList tentaclesOfEdge = input.attachedNodesOf(nonterminalEdges.get(edgeIndex));
            for (int i = 0; i < tentaclesOfEdge.size(); i++) {
                int reachableNode = tentaclesOfEdge.get(i);
                if (reachableNode != nodeId) {
                    addReachableNode(reachableNode, dequeue, visited);
                }
            }
        }
    }

    /**
     * adds the specified node both to the dequeue and the set of visited nodes
     *
     * @param nodeId  the node to add
     * @param dequeue the dequeue to add to
     * @param visited the set to add to
     */
    private void addReachableNode(int nodeId,
                                  Deque<Integer> dequeue,
                                  Set<Integer> visited) {

        if (!visited.contains(nodeId)) {
            dequeue.add(nodeId);
            visited.add(nodeId);
        }
    }


    /**
     * if this node is not already in the set of cutpoints
     * (there might be multiple paths to a cutpoint),
     * it is marked external in the reachable fragment
     * and added to the tentacles
     *
     * @param nodeId    the id of the cutpoint in the input graph
     * @param tentacles the list of tentacles = the list of cutpoints
     */
    private void setCutpoint(int nodeId, TIntArrayList tentacles) {

        if (!tentacles.contains(nodeId)) {
            reachableFragmentBuilder.setExternal(idMapping.get(nodeId));
            tentacles.add(nodeId);
        }
    }

// general utility functions

    /**
     * determines whether the given variable name is a parameter (including this)
     *
     * @param variableName the variable name to check
     * @return true, if the name belongs to a parameter, false otherwise
     */
    private boolean isParameter(String variableName) {

        return variableName.startsWith("@param") || variableName.startsWith("@this");
    }

    /**
     * constructs the nonterminal used to replace the reachable fragment in the remaining fragment,
     * the label will consist of the method name and the rank (since a given nonterminal label
     * must have a unique rank and the same method might be called with different numbers of cutpoints)
     *
     * @param rank the required rank of the nonterminal
     * @return the constructed nonterminal
     */
    private Nonterminal getContractNonterminal(int rank) {

        final boolean[] isReductionTentacle = new boolean[rank];
        Nonterminal nt = scene().createNonterminal(displayName + rank, rank, isReductionTentacle);
        return nt;
    }

}
