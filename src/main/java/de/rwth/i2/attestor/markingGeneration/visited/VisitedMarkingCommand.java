package de.rwth.i2.attestor.markingGeneration.visited;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import gnu.trove.iterator.TIntIterator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class VisitedMarkingCommand implements SemanticsCommand {

    public static final String MARKING_NAME = Markings.MARKING_PREFIX + "visited";

    private final Collection<String> availableSelectorNames;
    private final ViolationPoints potentialViolationPoints;
    private final int nextPc;

    public VisitedMarkingCommand(Collection<String> availableSelectorNames, int nextPc) {

        this.availableSelectorNames = availableSelectorNames;

        potentialViolationPoints = new ViolationPoints();
        for(String selName : availableSelectorNames) {
            potentialViolationPoints.add(MARKING_NAME, selName);
        }

        this.nextPc = nextPc;
    }

    @Override
    public Collection<ProgramState> computeSuccessors(ProgramState programState) {

        HeapConfiguration hc = programState.getHeap();
        int variable = hc.variableWith(MARKING_NAME);

        if(variable == HeapConfiguration.INVALID_ELEMENT) {
            throw new IllegalArgumentException("Provided state is not marked.");
        }
        int baseNode = hc.targetOf(variable);

        Collection<ProgramState> result = new LinkedHashSet<>();
        for(SelectorLabel sel : hc.selectorLabelsOf(baseNode)) {

            String selName = sel.getLabel();
            if(!availableSelectorNames.contains(selName)) {
                throw new IllegalArgumentException("Unknown selector label found: " + selName);
            }
            int target = hc.selectorTargetOf(baseNode, sel);

            if(!isAttachedToConstant(hc, target)) {
                ProgramState successorState = programState.clone();
                successorState.getHeap().builder()
                        .removeVariableEdge(variable)
                        .addVariableEdge(MARKING_NAME, target)
                        .build();
                result.add(successorState);
            }
        }

        return result;
    }

    private boolean isAttachedToConstant(HeapConfiguration heap, int node) {

        TIntIterator variables =  heap.attachedVariablesOf(node).iterator();
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

        return Collections.singleton(nextPc);
    }

    @Override
    public boolean needsCanonicalization() {
        return true;
    }
}
