package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Types;
import gnu.trove.list.array.TIntArrayList;

public class MockupProgramStateFactory extends SceneObject {

    public MockupProgramStateFactory(SceneObject otherObject) {
        super(otherObject);
    }

    public ProgramState getInitialState() {
        return new DefaultProgramState(this, scene().createHeapConfiguration());

    }
    public ProgramState getNormalState() {

        HeapConfiguration hc = scene()
                .createHeapConfiguration()
                .builder()
                .addNodes(Types.NULL, 1, new TIntArrayList())
                .build();
        return new DefaultProgramState(this, hc);
    }

    public ProgramState getRequiredFinalState() {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration hc = scene()
                .createHeapConfiguration()
                .builder()
                .addNodes(scene().getType("nodeType"), 2, nodes)
                .addNonterminalEdge(scene().createNonterminal("X", 2, new boolean[]{false,false}), nodes)
                .build();
        return new DefaultProgramState(this, hc);
    }

    public ProgramState getIrrelevantFinalState() {

        TIntArrayList nodes = new TIntArrayList();
        HeapConfiguration hc = scene()
                .createHeapConfiguration()
                .builder()
                .addNodes(scene().getType("nodeType"), 2, nodes)
                .addNonterminalEdge(scene().createNonterminal("Y", 2, new boolean[]{false,false}), nodes)
                .build();
        return new DefaultProgramState(this, hc);
    }
}
