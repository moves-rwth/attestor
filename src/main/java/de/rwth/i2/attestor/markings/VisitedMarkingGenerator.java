package de.rwth.i2.attestor.markings;

import de.rwth.i2.attestor.grammar.materialization.ViolationPoints;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.NoPostProcessingStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.ProgramImpl;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

import java.util.*;

public class VisitedMarkingGenerator extends SceneObject {

    private static final String markingVariableName = "%visited";

    private final List<HeapConfiguration> markedHeapConfigurations = new ArrayList<>();
    private final Set<String> requiredSelectors;

    public VisitedMarkingGenerator(SceneObject sceneObject, HeapConfiguration input, Set<String> requiredSelectors)
            throws StateSpaceGenerationAbortedException {

        super(sceneObject);

        this.requiredSelectors = requiredSelectors;

        Program program = setupProgram();
        List<ProgramState> initialStates = setupInitialStates(input);


        StateSpaceGenerator generator = StateSpaceGenerator.builder()
                .setProgram(program)
                .setPostProcessingStrategy(new NoPostProcessingStrategy())
                .setCanonizationStrategy(scene().strategies().getAggressiveCanonicalizationStrategy())
                .setMaterializationStrategy(scene().strategies().getMaterializationStrategy())
                .build();

        Set<ProgramState> states = generator.generate().getStates();
        List<HeapConfiguration> result = new ArrayList<>(states.size());
        for(ProgramState state: states) {
            result.add(state.getHeap());
        }
    }

    private List<ProgramState> setupInitialStates(HeapConfiguration input) {

        List<ProgramState> result = new ArrayList<>();

        return result;
    }

    public Collection<HeapConfiguration> getMarkedHeapConfigurations() {

        return markedHeapConfigurations;
    }

    private Program setupProgram() {

        ViolationPoints violationPoints = new ViolationPoints();
        for(String selectorName : requiredSelectors) {
            violationPoints.add(markingVariableName, selectorName);
        }

        return ProgramImpl.builder().addStatement(
               new VisitedMarkingStatement(markingVariableName, violationPoints)
        ).build();
    }
}

class VisitedMarkingStatement implements SemanticsCommand {

    private String markingVariableName;
    private ViolationPoints violationPoints;

    public VisitedMarkingStatement(String markingVariableName, ViolationPoints violationPoints) {

        this.markingVariableName = markingVariableName;
        this.violationPoints = violationPoints;
    }

    @Override
    public Set<ProgramState> computeSuccessors(ProgramState programState)
            throws NotSufficientlyMaterializedException, StateSpaceGenerationAbortedException {

        HeapConfiguration heap = programState.getHeap();
        Set<ProgramState> result = new LinkedHashSet<>();

        int varEdge = heap.variableWith(markingVariableName);
        int varTarget = heap.targetOf(varEdge);

        heap = heap.clone().builder().removeVariableEdge(varEdge).build();

        for(SelectorLabel selectorLabel : heap.selectorLabelsOf(varTarget)) {
            int successorNode = heap.selectorTargetOf(varTarget, selectorLabel);
            result.add(programState.shallowCopyWithUpdateHeap(heap.clone()
                    .builder()
                    .addVariableEdge(markingVariableName, successorNode)
                    .build()
            ));
        }

        return result;
    }

    @Override
    public ViolationPoints getPotentialViolationPoints() {

        return violationPoints;
    }

    @Override
    public Set<Integer> getSuccessorPCs() {

        return Collections.singleton(0);
    }

    @Override
    public boolean needsCanonicalization() {
        return true;
    }

    @Override
    public String toString() {

        return "VisitedStatement";
    }
}
