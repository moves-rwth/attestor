package de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.ContractMatch;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import static org.junit.Assert.*;

public class InternalContractCollectionTest {

    private final SceneObject sceneObject = new MockupSceneObject();
    private final Type type = sceneObject.scene().getType("type");
    private final SelectorLabel SEL = sceneObject.scene().getSelectorLabel("sel");

    @Test
    public void test() {

        InternalContractCollection contractCollection = new InternalContractCollection(
                new InternalPreconditionMatchingStrategy()
        );

        HeapConfiguration h1 = simpleGraph();
        HeapConfiguration h2 = otherSimpleGraphWithSameHash();
        assertEquals(h1.hashCode(), h2.hashCode());

        ContractMatch firstMatch = contractCollection.matchContract(h1);
        assertFalse(firstMatch.hasMatch());
        assertNull(firstMatch.getPostconditions());
        assertNull(firstMatch.getExternalReordering());

        ContractMatch secondMatch = contractCollection.matchContract(h2);
        assertFalse(secondMatch.hasMatch());
        assertNull(secondMatch.getPostconditions());
        assertNull(secondMatch.getExternalReordering());

        Contract contract = new InternalContract(h1, new LinkedHashSet<>());
        contractCollection.addContract(contract);

        ContractMatch thirdMatch = contractCollection.matchContract(h1);
        assertTrue(thirdMatch.hasMatch());
        assertNotNull(thirdMatch.getPostconditions());
        assertArrayEquals(identityReordering(), thirdMatch.getExternalReordering());
        assertTrue("Postcondition of contract should be empty", thirdMatch.getPostconditions().isEmpty());

        ContractMatch fourthMatch = contractCollection.matchContract(h2);
        assertFalse(fourthMatch.hasMatch());
        assertNull(fourthMatch.getPostconditions());
        assertNull(fourthMatch.getExternalReordering());

        HeapConfiguration somePostcondition = simpleGraph();
        Collection<HeapConfiguration> postconditions = new ArrayList<>();
        postconditions.add(somePostcondition);
        Contract updatedContract = new InternalContract(h1, postconditions);
        contractCollection.addContract(updatedContract);

        ContractMatch fifthMatch = contractCollection.matchContract(h1);
        assertTrue(fifthMatch.hasMatch());
        assertTrue(fifthMatch.getPostconditions().contains(somePostcondition));

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
