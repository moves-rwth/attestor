package de.rwth.i2.attestor.grammar.util;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ExternalNodesPartitionerTest {

    private SceneObject sceneObject;

    @Before
    public void setup() {

        sceneObject = new MockupSceneObject();
    }

    @Test
    public void testSimple() {

        Type type = sceneObject.scene().getType("t1");

        TIntArrayList nodes = new TIntArrayList();

        HeapConfiguration hc = sceneObject.scene().createHeapConfiguration()
                .builder()
                .addNodes(type, 4, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .build();

        boolean reductionTentacles[] = new boolean[]{true,true,true,true};
        ExternalNodesPartitioner partitioner = new ExternalNodesPartitioner(hc, reductionTentacles);


        for(TIntArrayList part : partitioner.getPartitions()) {
            int max = part.max();
            for(int i=0; i <= max; i++) {
                assertTrue(part.contains(i));
            }
        }
    }

    @Test
    public void testWithNodeTypes() {

        Type t1 = sceneObject.scene().getType("t1");
        Type t2 = sceneObject.scene().getType("t2");

        TIntArrayList nodes = new TIntArrayList();

        HeapConfiguration hc = sceneObject.scene().createHeapConfiguration()
                .builder()
                .addNodes(t1, 2, nodes)
                .addNodes(t2, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .build();

        boolean reductionTentacles[] = new boolean[]{true,true,true,true};
        ExternalNodesPartitioner partitioner = new ExternalNodesPartitioner(hc, reductionTentacles);

        for(TIntArrayList part : partitioner.getPartitions()) {
            assertNotEquals(part.get(0), part.get(2));
            assertNotEquals(part.get(0), part.get(3));
            assertNotEquals(part.get(1), part.get(2));
            assertNotEquals(part.get(1), part.get(3));
        }
    }

    @Test
    public void testWithReductionTentacles() {

        Type type = sceneObject.scene().getType("t1");

        TIntArrayList nodes = new TIntArrayList();

        HeapConfiguration hc = sceneObject.scene().createHeapConfiguration()
                .builder()
                .addNodes(type, 4, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .build();

        boolean reductionTentacles[] = new boolean[]{true,false,false,true};

        ExternalNodesPartitioner partitioner = new ExternalNodesPartitioner(hc, reductionTentacles);

        for(TIntArrayList part : partitioner.getPartitions()) {
            assertNotEquals(part.get(1), part.get(2));
        }
    }

}
