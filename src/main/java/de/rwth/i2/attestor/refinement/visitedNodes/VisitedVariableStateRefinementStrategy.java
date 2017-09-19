package de.rwth.i2.attestor.refinement.visitedNodes;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRefinementStrategy;
import de.rwth.i2.attestor.types.Type;

/**
 *
 * A state refinement strategy that marks every node in a heap configuration that is the target of a program
 * variable as "visited". Previously marked nodes remain marked.
 *
 * @author Christoph
 */
public class VisitedVariableStateRefinementStrategy implements StateRefinementStrategy {

    private String variableName;

    public VisitedVariableStateRefinementStrategy(String variableName) {

        this.variableName = variableName;
    }

    @Override
    public ProgramState refine(ProgramState state) {

        markConstant( state, "null" );
        markConstant( state, "true" );
        markConstant( state, "false" );
        markConstant( state, "0" );
        markConstant( state, "1" );
        markVariable( state, variableName );

        return state;
    }

    private void markConstant(ProgramState state, String constName) {

        markVisited(state.getHeap(), constName);
    }

    private void markVariable(ProgramState state, String varName) {

        HeapConfiguration heapConf = state.getHeap();
        markVisited(heapConf, state.getVariableNameInHeap(varName));
    }

    private void markVisited(HeapConfiguration heapConf, String varName) {

        int var = heapConf.variableWith( varName );

        if(var == HeapConfiguration.INVALID_ELEMENT) {
            return;
        }

        int node = heapConf.targetOf(var);
        Type type = heapConf.nodeTypeOf(node);
        if(!VisitedTypeHelper.isVisited(type)) {
            Type newType = VisitedTypeHelper.getVisitedType(type);
            heapConf.builder().replaceNodeType(node, newType).build();
        }
    }
}
