package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.Set;

import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * An abstraction of abstract program semantics that is executed on objects of type {@link ProgramState}.
 *
 * @author Christoph
 */
public interface Semantics {

    /**
     * Executes a single step of the abstract program semantics on the given program state.
     * Since the abstract program semantics may be non-deterministic (for example if a conditional statement cannot
     * be evaluated), this results in a set of successor program states in general.
     *
     * @param programState The state on which the abstract program semantics shall be executed.
     * @return The set of all states resulting from executing the program semantics on programState.
     * @throws NotSufficientlyMaterializedException This exception is thrown if the semantics cannot be executed on
     *                                              programState due to missing fields.
     */
    Set<ProgramState> computeSuccessors(ProgramState programState) throws NotSufficientlyMaterializedException;

    /**
     * Checks whether execution of a single step of the abstract program semantics on the given program state
     * requires materialization first.
     * @param programState The program state on which the semantics should be
     * @return true, if the semantics statement requires materialization
     */
    boolean needsMaterialization(ProgramState programState);

    /**
     * @return True if and only if executing this statement always yields at most one successor.
     */
    boolean hasUniqueSuccessor();

    /**
     * @return All potential violation points that may prevent execution of this statement.
     */
    ViolationPoints getPotentialViolationPoints();

    /**
     * @return The set of all program locations that are direct successors of this program statement in
     *         the underlying control flow graph.
     */
    Set<Integer> getSuccessorPCs();

    /**
     * @return True if and only if canonicalization may be performed after execution of this program statement.
     */
    boolean permitsCanonicalization();

    /**
     * Determines whether canonicalization may be performed after execution of this program statement.
     * @param permitted True if and only if canonicalization may be performed after execution of this program statement.
     */
    void setPermitCanonicalization(boolean permitted);
    
    /**
     * A string representing the semantics statement both for display and
     * for identification.
     * @return the string representation of the semantics
     */
    @Override
    String toString();
}
