package de.rwth.i2.attestor.ipa;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.*;

public class IpaContractCollectionTest {


    private final SceneObject sceneObject = new MockupSceneObject();
    private final Type type = sceneObject.scene().getType("type");
    private final SelectorLabel SEL = sceneObject.scene().getSelectorLabel("sel");

    @Test
    public void test() {

        IpaContractCollection contracts = new IpaContractCollection();

        HeapConfiguration h1 = simpleGraph();
        HeapConfiguration h2 = otherSimpleGraphWithSameHash();
        assertEquals(h1.hashCode(), h2.hashCode());

        assertFalse("should not contain p1", contracts.hasMatchingPrecondition(h1));
        assertFalse("should not contain p2", contracts.hasMatchingPrecondition(h2));
        assertNull("h1 contract should be null", contracts.getPostconditions(h1));
        assertNull("h2 contract should be null", contracts.getPostconditions(h2));
        assertNull("h1 should not get a reordering", contracts.getReordering(h1));
        assertNull("h2 should not get a reordering", contracts.getReordering(h2));

        contracts.addContract(h1, new ArrayList<>());

        assertTrue("should have p1", contracts.hasMatchingPrecondition(h1));
        assertFalse("should not have p2", contracts.hasMatchingPrecondition(h2));
        assertNotNull("should have contract h1", contracts.getPostconditions(h1));
        assertNotNull("should have a reordering", contracts.getReordering(h1));
        assertTrue("reordering should be identity", Arrays.equals(identityReordering(), contracts.getReordering(h1)));
        assertThat("postconditon of contract h1 should be empty", contracts.getPostconditions(h1), empty());
        assertNull("should not have contract h2", contracts.getPostconditions(h2));

        HeapConfiguration somePostCondition = simpleGraph();
        contracts.getPostconditions(h1).add(somePostCondition);

        assertThat("", contracts.getPostconditions(h1), contains(somePostCondition));


    }

    private HeapConfiguration otherSimpleGraphWithSameHash() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), SEL, nodes.get(1))
                .addVariableEdge("x", nodes.get(0))
                .build();
    }

    private HeapConfiguration simpleGraph() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(1), SEL, nodes.get(0))
                .addVariableEdge("x", nodes.get(0))
                .build();
    }

    private int[] identityReordering() {

        return new int[]{0, 1};
    }

}
