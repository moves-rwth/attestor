package de.rwth.i2.attestor.semantics;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Terminal Statements are used to model the exit point of a method. They return
 * an empty result set.
 *
 * @author Hannah Arndt, Christoph
 */
public class TerminalStatement implements SemanticsCommand {


    @Override
    public Collection<ProgramState> computeSuccessors(ProgramState executable) {

        return new LinkedHashSet<>();
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
    public boolean needsCanonicalization() {
        return true;
    }

    @Override
    public String toString() {

        return "program terminated";
    }

}
