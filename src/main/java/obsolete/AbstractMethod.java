package obsolete;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

import java.util.Set;

public abstract class AbstractMethod extends SceneObject {

    /**
     * the abstract semantic of the method.
     */
    protected Program method;
    /**
     * the methodExecution signature
     */
    protected String displayName;
    private boolean reuseResultsEnabled = true;

    public AbstractMethod(SceneObject sceneObject) {

        super(sceneObject);
    }

    public void setReuseResults(boolean enabled) {

        this.reuseResultsEnabled = enabled;
    }

    public boolean isReuseResultsEnabled() {

        return reuseResultsEnabled;
    }

    /**
     * Provides the results of symbolically executing the method represented by this object
     * on the given input.
     *
     * @param input   The program state determining the input of the method.
     * @param options The current state space generation options.
     * @return The state space obtained from symbolically executing this AbstractMethod on the
     * given input.
     * @throws StateSpaceGenerationAbortedException
     */
    public abstract Set<ProgramState> getResult(ProgramState input, SymbolicExecutionObserver options)
            throws StateSpaceGenerationAbortedException;

    public void setDisplayName(String displayName) {

        this.displayName = displayName;
    }

    public abstract Set<ProgramState> getFinalStates(ProgramState input, SymbolicExecutionObserver observer);

    /**
     * @return the method body / abstract semantics
     */
    public Program getControlFlow() {

        return this.method;
    }

    /**
     * sets the methodExecution semantic to the control flow of the given list of
     * abstract semantics
     *
     * @param program a program corresponding to the method body
     */
    public void setControlFlow(Program program) {

        this.method = program;
    }

    public String toString() {

        return this.displayName;
    }

}