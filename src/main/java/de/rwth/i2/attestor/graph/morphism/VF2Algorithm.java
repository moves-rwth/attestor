package de.rwth.i2.attestor.graph.morphism;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Stack;

/**
 * This class implements the VF2 (sub)graph searching algorithm in order to find graph morphisms
 * between two Graphs.
 * More precisely, we are given a pattern Graph and target Graph and search for a morphism from the pattern
 * into the target Graph.
 * <p>
 * Essentially VF2Algorithm implements a backtracking algorithm to search for possible graph morphisms.
 * Each element of the search space is represented by a {@link VF2State}, which represents a partial morphism together
 * with additional data that allow efficient backtracking.
 * The search tree is constructed in a way such that no copies of the partial morphism are required.
 * VF2Algorithm depends on a list of {@link FeasibilityFunction}s that determine possible pairs
 * of pattern-target nodes that are matched onto each other.
 * The FeasibilityFunctions thus determine the type of Morphism that is actually computed.
 * <p>
 * A detailed examination of the Algorithm is found in the following paper by
 *
 * @author Christoph
 * @see <a href="http://dblp.uni-trier.de/rec/html/journals/pami/CordellaFSV04">Cordella et al.</a>
 * <p>
 * There are two main methodExecution in this class for clients:
 * <p>
 * {@link VF2Algorithm#builder()} creates a builder to create a customized VF2Algorithm that, for example, includes
 * the desired FeasibilityFunctions.
 * <p>
 * {@link VF2Algorithm#match(Graph, Graph)} executes the algorithm for the provided pair of Graphs.
 */
public class VF2Algorithm {

    public static final boolean DEBUG = false;
    private static final Logger logger = LogManager.getLogger("VF2Algorithm");

    /**
     * The FeasibilityFunctions that are evaluated to determine whether a candidate pair
     * represents a pair of pattern-target nodes that can be added to the current state without invalidating
     * the Morphism we search for.
     */
    FeasibilityFunction[] feasibilityChecks;

    /**
     * A function that determines whether we found a complete Morphism and can thus successfully terminate.
     */
    TerminationFunction morphismFoundCheck;

    /**
     * The morphism that has been found by the algorithm. Null otherwise.
     */
    private Morphism foundMorphism = null;

    /**
     * Construct a useless VF2Algorithm that has to be customized by a {@link VF2AlgorithmBuilder}.
     */
    VF2Algorithm() {

        morphismFoundCheck = null;
    }

    /**
     * @return A builder to create a customized VF2Algorithm.
     */
    public static VF2AlgorithmBuilder builder() {

        return new VF2AlgorithmBuilder();
    }

    /**
     * Executes the algorithm to find Morphisms from pattern into target.
     *
     * @param pattern The Graph that should be searched for.
     * @param target  The Graph we search in.
     * @return true if and only if at least one Morphism exists.
     */
    public boolean match(Graph pattern, Graph target) {

        VF2State initialState = new VF2State(pattern, target);
        return match(initialState);
    }

    /**
     * Executes the next step of the algorithm for a given state.
     *
     * @param state The VF2State that determines the current position of the algorithm in its search tree.
     * @return true if and only if at least one Morphism exists.
     */
    private boolean match(VF2State state) {

        Stack<VF2State> stateStack = new Stack<>();
        stateStack.push(state);

        mainLoop:
        while (!stateStack.isEmpty()) {

            state = stateStack.peek();

            if (morphismFoundCheck.eval(state)) {
                storeMorphism(state);
                return true;
            }

			/* Since it is possible that some Morphism exists, we continue
           	   searching for one. To this end we go through all (reachable)
               pairs (patternNode, targetNode) of candidates that might be
               added to the partial morphism. */
            while (state.nextCandidate()) {
                int p = state.getPatternCandidate();
                int t = state.getTargetCandidate();
                if (isFeasible(state, p, t)) {

                    if (DEBUG) {
                        logger.error("found feasible candidate (" + p + "," + t + ")");
                    }
					
					/* A shallow copy only copies data required for backtracking
				       such as the last candidate. After that we move further
				       down in the search tree. */
                    VF2State nextState = state.shallowCopy();
                    nextState.addCandidate(p, t);
                    stateStack.push(nextState);
                    continue mainLoop; // simulate recursion by adjusting stack and jumping to the outer loop.
                }
            }

            if (DEBUG) {
                logger.error("Backtracking...");
            }
			/* We stored all morphisms found so far and finished going through all search trees
		   	   after adding all available candidate pairs to the current state.
               Hence, we backtrack and remove the last pair added to the current state before. */
            state.backtrack();
            stateStack.pop();
        }
        return false;
    }

    /**
     * Checks whether adding a candidate pair (patternNode, targetNode) to the current state
     * results in a state that might still lead to a desired Morphism.
     *
     * @param state The current position in the search tree.
     * @param p     The pattern candidate node that should be added to the current state of the algorithm.
     * @param t     The target candidate node that should be added to the current state of the algorithm.
     * @return true if and only if adding candidate to state might still result in a desired morphism.
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    private boolean isFeasible(VF2State state, int p, int t) {

        for (int i = 0; i < feasibilityChecks.length; i++) {
            if (!feasibilityChecks[i].eval(state, p, t)) {
                if (DEBUG) {
                    logger.error(feasibilityChecks[i].getClass().getSimpleName() + " rejected candidate (" + p + "," + t + ")");
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Stores the morphism contained in the provided state in the list of all morphisms found so far.
     *
     * @param state A state representing a found morphism.
     */
    private void storeMorphism(VF2State state) {

        foundMorphism = new Morphism(state.getPattern().getMatching());
    }

    /**
     * @return The morphism that has been found. Null otherwise.
     */
    public Morphism getMorphism() {

        return foundMorphism;
    }
}
