package de.rwth.i2.attestor.markingGeneration.neighbourhood;

import de.rwth.i2.attestor.grammar.materialization.ViolationPoints;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class NeighbourhoodMarkingCommand implements SemanticsCommand {

    private final Set<Integer> successorPCs;
    private final String markingName;
    private final String markingSeparator;
    private final Collection<String> availableSelectorNames;
    private final ViolationPoints potentialViolationPoints;

    public NeighbourhoodMarkingCommand(int nextPC, String markingName, String markingSeparator,
                                       Collection<String> availableSelectorNames) {

        successorPCs = Collections.singleton(nextPC);
        this.markingName = markingName;
        this.markingSeparator = markingSeparator;
        this.availableSelectorNames = availableSelectorNames;

        this.potentialViolationPoints = new ViolationPoints();
        for(String selectorName : availableSelectorNames) {
            this.potentialViolationPoints.add(markingName, selectorName);
            String variableName = getVariableName(selectorName);
            for(String selName: availableSelectorNames) {
                this.potentialViolationPoints.add(variableName, selName);
            }
        }
    }

    private String getVariableName(String selectorName) {

        return markingName + markingSeparator + selectorName;
    }


    @Override
    public Collection<ProgramState> computeSuccessors(ProgramState programState)
            throws NotSufficientlyMaterializedException, StateSpaceGenerationAbortedException {

        Collection<ProgramState> result = new LinkedHashSet<>();
        HeapConfiguration heap = programState.getHeap();

        int origin = heap.variableTargetOf(markingName);

        Set<Integer> nextOrigins  = getNextOrigins(heap, origin);
        ProgramState cleanedState = getCleanedState(programState);

        for(int nextOrigin : nextOrigins) {
            ProgramState markedState = markState(cleanedState, nextOrigin);
            result.add(markedState);
        }

        return result;
    }

    private Set<Integer> getNextOrigins(HeapConfiguration heap, int origin) {

        Set<Integer> newOrigins = new LinkedHashSet<>();
        for(SelectorLabel sel : heap.selectorLabelsOf(origin)) {
            newOrigins.add(heap.selectorTargetOf(origin, sel));
        }
        return newOrigins;
    }

    private ProgramState getCleanedState(ProgramState programState) {

        ProgramState result = programState.clone();
        HeapConfiguration heap = programState.getHeap();
        result.removeVariable(markingName);
        TIntIterator iterator = programState.getHeap().variableEdges().iterator();
        while(iterator.hasNext()) {
            int varEdge = iterator.next();
            if(heap.nameOf(varEdge).startsWith(markingName+markingSeparator)) {
                result.getHeap().builder().removeVariableEdge(varEdge).build();
            }
        }
        return result;
    }

    private ProgramState markState(ProgramState cleanedState, int nextOrigin) {

        ProgramState result = cleanedState.clone();
        HeapConfiguration heap = result.getHeap();
        HeapConfigurationBuilder builder = heap.builder();
        builder.addVariableEdge(markingName, nextOrigin);
        for(SelectorLabel sel : heap.selectorLabelsOf(nextOrigin)) {

            int target = heap.selectorTargetOf(nextOrigin, sel);
            if(!isAttachedToConstant(heap, target)) {
                builder.addVariableEdge(getVariableName(sel.getLabel()), target);
            }
        }

        return result;
    }

    private boolean isAttachedToConstant(HeapConfiguration heap, int node) {

        TIntIterator variables = heap.attachedVariablesOf(node).iterator();
        while(variables.hasNext()) {
            int varEdge = variables.next();
            String name = heap.nameOf(varEdge);
            if(Constants.isConstant(name)) {
                return true;
            }
        }
        return false;
    }



    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return potentialViolationPoints;
    }

    @Override
    public Set<Integer> getSuccessorPCs() {

        return successorPCs;
    }

    @Override
    public boolean needsCanonicalization() {

        return true;
    }
}
