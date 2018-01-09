package de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl;

import de.rwth.i2.attestor.semantics.TerminalStatement;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Abstraction of a program that is symbolically executed to generate a state space.
 * In particular, this class provides access to program statements located at a requested position.
 *
 * @author Christoph
 */
public class ProgramImpl implements Program {

    /**
     * The internal representation of the program as a list of program statements.
     * The position in the lost corresponds to the value of the program counter.
     */
    private final List<SemanticsCommand> program;
    private final TIntArrayList predecessorCounter;
    private int terminalPredecessorCounter;

    /**
     * Initialize this program.
     *
     * @param program The list of statements that make up this program.
     */
    public ProgramImpl(List<SemanticsCommand> program) {

        this.program = program;
        this.predecessorCounter = new TIntArrayList(program.size());
        this.terminalPredecessorCounter = 0;
        for(int i=0; i < program.size(); i++) {
            this.predecessorCounter.add(0);
        }

        countPredecessors();
    }

    public static ProgramBuilder builder() {

        return new ProgramBuilder();
    }

    @Override
    public SemanticsCommand getStatement(int programCounter) {

        if (isExit(programCounter)) {
            return new TerminalStatement();
        }
        return program.get(programCounter);
    }

    @Override
    public int countPredecessors(int programCounter) {

        if(isExit(programCounter)) {
            return terminalPredecessorCounter;
        } else {
            return predecessorCounter.get(programCounter);
        }
    }

    private void countPredecessors() {

        for (SemanticsCommand aProgram : program) {
            Set<Integer> out = aProgram.getSuccessorPCs();
            for (Integer pc : out) {
                if(isExit(pc)) {
                    ++terminalPredecessorCounter;
                } else {
                    int inc = predecessorCounter.get(pc) + 1;
                    predecessorCounter.set(pc, inc);
                }
            }
        }
    }

    private boolean isExit(int programCounter) {

        return programCounter >= program.size() || programCounter < 0;
    }

    public final static class ProgramBuilder {

        private List<SemanticsCommand> commands = new ArrayList<>();

        protected ProgramBuilder() {

        }

        public ProgramImpl build() {

            assert !commands.isEmpty();
            ProgramImpl result = new ProgramImpl(commands);
            commands = null;
            return result;
        }

        public ProgramBuilder addStatement(SemanticsCommand semanticsCommand) {

            commands.add(semanticsCommand);
            return this;
        }
    }

    public String toString(){
    	StringBuilder res = new StringBuilder();
    	for( int i = 0; i < program.size(); i++ ){
    		res.append(i).append(" ").append(program.get(i)).append("\n");
    	}
    	return res.toString();
    }
}
