package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.Method;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TarjanAlgorithmTest {

    private SceneObject sceneObject;

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
    }

    //only single, non-recursive method
    @Test
    public void testTrivial() {

        TarjanAlgorithm algorithm = new TarjanAlgorithm();
        Method m1 = sceneObject.scene().getOrCreateMethod("non-recursive 1");
        algorithm.addMethodAsVertex(m1);
        algorithm.markRecursiveMethods();
        assertFalse(m1.isRecursive());
    }

    @Test
    public void testCallChain() {

        TarjanAlgorithm algorithm = new TarjanAlgorithm();

        Method m1 = sceneObject.scene().getOrCreateMethod("non-recursive 1");
        algorithm.addMethodAsVertex(m1);

        Method m2 = sceneObject.scene().getOrCreateMethod("non-recursive 2");
        algorithm.addMethodAsVertex(m2);
        algorithm.addCallEdge(m1, m2);

        Method m3 = sceneObject.scene().getOrCreateMethod("non-recursive 3");
        algorithm.addMethodAsVertex(m3);
        algorithm.addCallEdge(m2, m3);

        algorithm.markRecursiveMethods();

        assertFalse(m1.isRecursive());
        assertFalse(m2.isRecursive());
        assertFalse(m3.isRecursive());
    }

    @Test
    public void testDirectRecursion() {

        TarjanAlgorithm algorithm = new TarjanAlgorithm();

        Method m1 = sceneObject.scene().getOrCreateMethod("non-recursive 1");
        algorithm.addMethodAsVertex(m1);
        algorithm.addCallEdge(m1, m1);


        algorithm.markRecursiveMethods();

        assertTrue(m1.isRecursive());
    }


    @Test
    public void testIndirectRecursion() {

        TarjanAlgorithm algorithm = new TarjanAlgorithm();

        Method m1 = sceneObject.scene().getOrCreateMethod("non-recursive 1");
        algorithm.addMethodAsVertex(m1);

        Method m2 = sceneObject.scene().getOrCreateMethod("non-recursive 2");
        algorithm.addMethodAsVertex(m2);
        algorithm.addCallEdge(m1, m2);

        Method m3 = sceneObject.scene().getOrCreateMethod("non-recursive 3");
        algorithm.addMethodAsVertex(m3);
        algorithm.addCallEdge(m2, m3);
        algorithm.addCallEdge(m3, m1);

        algorithm.markRecursiveMethods();

        assertTrue(m1.isRecursive());
        assertTrue(m2.isRecursive());
        assertTrue(m3.isRecursive());
    }

    @Test
    public void testMixedSetting() {

        TarjanAlgorithm algorithm = new TarjanAlgorithm();

        Method m1 = sceneObject.scene().getOrCreateMethod("non-recursive 1");
        algorithm.addMethodAsVertex(m1);

        Method m2 = sceneObject.scene().getOrCreateMethod("non-recursive 2");
        algorithm.addMethodAsVertex(m2);
        algorithm.addCallEdge(m1, m2);

        Method m3 = sceneObject.scene().getOrCreateMethod("non-recursive 3");
        algorithm.addMethodAsVertex(m3);
        algorithm.addCallEdge(m2, m3);
        algorithm.addCallEdge(m3, m3);

        Method m4 = sceneObject.scene().getOrCreateMethod("non-recursive 4");
        algorithm.addMethodAsVertex(m4);
        algorithm.addCallEdge(m3, m4);

        algorithm.markRecursiveMethods();

        assertFalse("m1", m1.isRecursive());
        assertFalse("m2", m2.isRecursive());
        assertTrue("m3", m3.isRecursive());
        assertFalse("m4", m4.isRecursive());

    }

}
