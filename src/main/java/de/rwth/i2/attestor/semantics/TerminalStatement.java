package de.rwth.i2.attestor.semantics;

import de.rwth.i2.attestor.grammar.materialization.ViolationPoints;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.stateSpaceGeneration.SymbolicExecutionObserver;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Terminal Statements are used to model the exit point of a method. They return
 * an empty result set.
 *
 * @author Hannah Arndt, Christoph
 */
public class TerminalStatement implements Semantics {

    /**
     * Stores whether canonicalization may be performed
     * after executing this statement.
     */
    private boolean isCanonicalizationPermitted = true;

    @Override
    public Set<ProgramState> computeSuccessors(ProgramState executable, SymbolicExecutionObserver options) {

        return new LinkedHashSet<>();
    }

    @Override
    public boolean needsMaterialization(ProgramState executable) {

        return false;
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return ViolationPoints.getEmptyViolationPoints();
    }

    @Override
    public Set<Integer> getSuccessorPCs() {

        return new LinkedHashSet<>();
    }

    @Override
    public boolean permitsCanonicalization() {

        return isCanonicalizationPermitted;
    }

    @Override
    public void setPermitCanonicalization(boolean permitted) {

        isCanonicalizationPermitted = permitted;
    }

    @Override
    public String toString() {

        return "program terminated";
    }

}
