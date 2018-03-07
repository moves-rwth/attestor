package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeHelper;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.util.SingleElementUtil;

import java.util.Collection;
import java.util.Set;

/**
 * InvokeStmt models statements like foo(); or bar(1,2);
 *
 * @author Hannah Arndt
 */
public class InvokeStmt extends Statement implements InvokeCleanup {

    /**
     * the abstract representation of the called method
     */
    private final Method method;
    /**
     * handles arguments, and if applicable the this-reference.
     */
    private final InvokeHelper invokePrepare;
    /**
     * the program location of the successor state
     */
    private final int nextPC;

    public InvokeStmt(SceneObject sceneObject, Method method, InvokeHelper invokePrepare, int nextPC) {

        super(sceneObject);
        this.method = method;
        this.invokePrepare = invokePrepare;
        this.nextPC = nextPC;
    }

    /**
     * gets the fixpoint from the method
     * for the input heap and returns it for the successor
     * location.<br>
     * <p>
     * If any variable appearing in the arguments is not live at this point,
     * it will be removed from the heap to enable abstraction.
     */
    @Override
    public Collection<ProgramState> computeSuccessors(ProgramState programState) {

        ProgramState preparedState = programState.clone();
        invokePrepare.prepareHeap(preparedState);

        Collection<ProgramState> methodResult = method
                .getMethodExecutor()
                .getResultStates(programState, preparedState);

        methodResult.forEach(invokePrepare::cleanHeap);
        methodResult.forEach(ProgramState::clone);
        methodResult.forEach(x -> x.setProgramCounter(nextPC));

        return methodResult;
    }

    public ProgramState getCleanedResultState(ProgramState state) {

        invokePrepare.cleanHeap(state);
        return state;
    }

    public boolean needsMaterialization(ProgramState programState) {

        return invokePrepare.needsMaterialization(programState);
    }


    public String toString() {

        return invokePrepare.baseValueString() + method.toString() + "(" + invokePrepare.argumentString() + ");";
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return invokePrepare.getPotentialViolationPoints();
    }

    @Override
    public Set<Integer> getSuccessorPCs() {

        return SingleElementUtil.createSet(nextPC);
    }

    @Override
    public boolean needsCanonicalization() {
        return true;
    }

}
