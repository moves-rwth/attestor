package de.rwth.i2.attestor.graph.morphism;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Captures a current, possibly incomplete, candidate for a graph morphism that is constructed step by step
 * by a {@link VF2Algorithm}. In particular, VF2State supports method {@link #backtrack()} to reset it to
 * its previous state.
 *
 * @author Christoph
 */
public class VF2State {

    /**
     * Used for debug output and error reporting.
     */
    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger("VF2State");

    /**
     * The currently found partial mapping from pattern to target together
     * with additional data to prune the search space.
     */
    private final VF2GraphData pattern;

    /**
     * The currently found partial mapping from target to pattern together
     * with additional data to prune the search space.
     */
    private final VF2GraphData target;
    private final int countPatternNodes;
    private final int countTargetNodes;
    private int patternCandidate;
    private int targetCandidate;
    private int patternMin;

    /**
     * Computes a new initial state from two graphs
     *
     * @param patternGraph The pattern graph that should be mapped into the target graph.
     * @param targetGraph  The target graph.
     */
    public VF2State(Graph patternGraph, Graph targetGraph) {

        pattern = new VF2GraphData(patternGraph);
        target = new VF2GraphData(targetGraph);
        countPatternNodes = pattern.getGraph().size();
        countTargetNodes = target.getGraph().size();
        patternCandidate = 0;
        targetCandidate = -1;
        patternMin = AbstractVF2GraphData.NULL_NODE;
    }

    /**
     * Creates a copy of a given state.
     * Note that apart from a few constants, a shallow copy is created.
     * In particular, the underlying partial matching is shared between the state and its copy.
     *
     * @param state The state to be copied.
     */
    private VF2State(VF2State state) {

        pattern = new VF2GraphData(state.pattern);
        target = new VF2GraphData(state.target);
        countPatternNodes = state.countPatternNodes;
        countTargetNodes = state.countTargetNodes;
        patternCandidate = 0;
        targetCandidate = -1;
        patternMin = AbstractVF2GraphData.NULL_NODE;
    }

    /**
     * @return A shallow copy of this state.
     */
    public VF2State shallowCopy() {

        return new VF2State(this);
    }

    /**
     * @return The data stored for the pattern graph within this state.
     */
    public VF2GraphData getPattern() {

        return pattern;
    }

    /**
     * @return The data stored for the target graph within this state.
     */
    public VF2GraphData getTarget() {

        return target;
    }

    /**
     * Undoes the last change to the state.
     */
    public void backtrack() {

        pattern.backtrack();
        target.backtrack();
    }

    /**
     * Adds a new candidate to the underlying partial matching.
     *
     * @param p The pattern candidate to add to the matching described by this state.
     * @param t The target candidate to add to the matching described by this state.
     */
    public void addCandidate(int p, int t) {

        pattern.setMatch(p, t);
        target.setMatch(t, p);
    }

    /**
     * Computes the next candidate pair to be considered.
     * This pair is accessible through the methodExecution {@link #getPatternCandidate()} and {@link #getTargetCandidate()}.
     *
     * @return True if and only if another candidate pair has been found.
     */
    public boolean nextCandidate() {

        if (!pattern.isOutgoingEmpty() && !target.isOutgoingEmpty()) {
            return computeOutgoingCandidates();
        } else if (!pattern.isIngoingEmpty() && !target.isIngoingEmpty()) {
            return computeIngoingCandidates();
        } else {
            return computeAllCandidates();
        }
    }

    /**
     * Computes possible candidate pairs based on nodes reachable via outgoing edges from nodes that already have
     * been matched.
     */
    private boolean computeOutgoingCandidates() {

        int start = targetCandidate + 1;

        for (int p = patternCandidate; p < countPatternNodes; p++) {

            if (pattern.containsOutgoingUnmatched(p) && !pattern.isLessThan(patternMin, p)) {

                for (int t = start; t < countTargetNodes; t++) {

                    if (target.containsOutgoing(t) && !target.containsMatch(t)) {
                        patternCandidate = p;
                        targetCandidate = t;
                        patternMin = p;
                        return true;
                    }
                }
                start = 0;
            }
        }
        return false;
    }


    /**
     * Computes possible candidate pairs based on nodes reachable via ingoing edges from nodes that already have
     * been matched.
     */
    private boolean computeIngoingCandidates() {

        int start = targetCandidate + 1;
        for (int p = patternCandidate; p < countPatternNodes; p++) {


            if (pattern.containsIngoingUnmatched(p) && !pattern.isLessThan(patternMin, p)) {

                for (int t = start; t < countTargetNodes; t++) {

                    if (target.containsIngoing(t) && !target.containsMatch(t)) {
                        patternCandidate = p;
                        targetCandidate = t;
                        patternMin = p;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Computes all possible candidate pairs based on nodes that have not been matched yet.
     */
    private boolean computeAllCandidates() {

        int start = targetCandidate + 1;

        for (int p = patternCandidate; p < countPatternNodes; p++) {

            if (!pattern.containsMatch(p) && !pattern.isLessThan(patternMin, p)) {

                for (int t = start; t < countTargetNodes; t++) {

                    if (!target.containsMatch(t)) {
                        patternCandidate = p;
                        targetCandidate = t;
                        patternMin = p;
                        return true;
                    }
                }
                start = 0;
            }
        }
        return false;
    }

    public int getPatternCandidate() {

        return patternCandidate;
    }

    public int getTargetCandidate() {

        return targetCandidate;
    }

}
