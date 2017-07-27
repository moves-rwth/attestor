package de.rwth.i2.attestor.automata.implementations.reachability;

import de.rwth.i2.attestor.automata.HeapAutomaton;

/**
 * A specialized heap automaton that checks whether a given node is reachable from another node.
 * There are two ways to specify these two nodes:
 * <ol>
 *     <li>One may provide two variable names. In this case, the automaton checks whether the target of the first
 *         variable can reach the target of the second variable.</li>
 *     <li>One may provide the indices of two external nodes.</li>
 * </ol>
 *
 * @author Christoph
 */
public class ReachabilityHeapAutomaton extends HeapAutomaton {

    /**
     * Constructor to check reachability between variables in a heap configuration.
     * @param fromVariableName The source variable.
     * @param toVariableName The target variable.
     */
    public ReachabilityHeapAutomaton(String fromVariableName, String toVariableName) {

        super(new VariableReachabilityTransitionRelation(fromVariableName, toVariableName));
    }

    /**
     * Constructor to check reachability between two external nodes.
     * Note that the provided integers are the position of both nodes in the
     * sequence of external nodes -- <strong>not</strong> their IDs.
     * @param fromExtNode Position of the source external node.
     * @param toExtNode Position of the target external node.
     */
    public ReachabilityHeapAutomaton(int fromExtNode, int toExtNode) {

        super(new ExternalReachabilityTransitionRelation(fromExtNode, toExtNode));
    }
}
