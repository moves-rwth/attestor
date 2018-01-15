package de.rwth.i2.attestor.grammar.materialization.strategies;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.materialization.strategies.GeneralMaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.grammar.testUtil.TestGraphs;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.util.Pair;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneralMaterializationStrategyTest_getActualViolationPoint {

    public static final String ANNOTATED_VARIABLE = "variableToAnnotatedSel";
    public static final String ANNOTATED_SELECTOR = "annotatedSel";
    public static final String DEFAULT_VARIABLE = "variableToDefaultSel";
    public static final String DEFAULT_SELECTOR = "defaultSel";
    public static final String ANNOTATION = "ann";
    public static final int NODE_FOR_ANNOTATED_VARIABLE = 0;
    public static final int NODE_FOR_DEFAULT_VARIABLE = 1;
    private static final String MISSING_SELECTOR = "extra selector";
    private GeneralMaterializationStrategy materializer;
    private SceneObject sceneObject;
    private TestGraphs testGraphs;

    @Before
    public void setUp() throws Exception {

        sceneObject = new MockupSceneObject();
        testGraphs = new TestGraphs(sceneObject);
        materializer = new GeneralMaterializationStrategy(null, null);
    }


    @Test
    public void testGetActualViolationPoint_Default_Present() {

        ViolationPoints testPoint = new ViolationPoints(DEFAULT_VARIABLE, DEFAULT_SELECTOR);
        DefaultProgramState testState =
                new DefaultProgramState(testGraphs.getInput_getActualViolationPoints_Default());
        testState.prepareHeap();

        Pair<Integer, String> result
                = materializer.getActualViolationPoint(testState.getHeap(), testPoint);
        assertNull(result);
    }

    @Test
    public void testGetActualViolationPoint_Default_NotPresent() {

        ViolationPoints testPoint = new ViolationPoints(DEFAULT_VARIABLE, DEFAULT_SELECTOR);
        testPoint.add(DEFAULT_VARIABLE, MISSING_SELECTOR);
        DefaultProgramState testState =
                new DefaultProgramState(testGraphs.getInput_getActualViolationPoints_Default());
        testState.prepareHeap();

        Pair<Integer, String> result
                = materializer.getActualViolationPoint(testState.getHeap(), testPoint);
        assertNotNull(result);
        assertEquals(MISSING_SELECTOR, result.second());
        assertEquals(new Integer(0), result.first());
    }

    @Test
    public void testGetActualViolationPoint_Indexed_Present() {

        ViolationPoints testPoint = new ViolationPoints(ANNOTATED_VARIABLE, ANNOTATED_SELECTOR);
        DefaultProgramState testState =
                new DefaultProgramState(testGraphs.getInput_getActualViolationPoints_Indexed());
        testState.prepareHeap();

        Pair<Integer, String> result
                = materializer.getActualViolationPoint(testState.getHeap(), testPoint);
        assertNull(result);
    }

    @Test
    public void testGetActualViolationPoint_Indexed_NotPresent() {

        ViolationPoints testPoint = new ViolationPoints(ANNOTATED_VARIABLE, MISSING_SELECTOR);
        testPoint.add(ANNOTATED_VARIABLE, ANNOTATED_SELECTOR);
        DefaultProgramState testState =
                new DefaultProgramState(testGraphs.getInput_getActualViolationPoints_Indexed());
        testState.prepareHeap();

        Pair<Integer, String> result
                = materializer.getActualViolationPoint(testState.getHeap(), testPoint);
        assertNotNull(result);
        assertEquals(MISSING_SELECTOR, result.second());
        assertEquals(new Integer(0), result.first());
    }

    @Test
    public void testGetActualViolationPoint_Mixed_DefaultNotPresent() {

        ViolationPoints testPoints
                = new ViolationPoints(ANNOTATED_VARIABLE, ANNOTATED_SELECTOR);
        testPoints.add(ANNOTATED_VARIABLE, DEFAULT_SELECTOR);

        DefaultProgramState testState =
                new DefaultProgramState(testGraphs.getInput_getActualViolationPoints_Mixed());
        testState.prepareHeap();

        Pair<Integer, String> result
                = materializer.getActualViolationPoint(testState.getHeap(), testPoints);
        assertNotNull(result);
        assertEquals(DEFAULT_SELECTOR, result.second());
        assertEquals(new Integer(NODE_FOR_ANNOTATED_VARIABLE), result.first());

    }

    @Test
    public void testGetActualViolationPoint_Mixed_IndexedNotPresent() {

        ViolationPoints testPoints
                = new ViolationPoints(DEFAULT_VARIABLE, ANNOTATED_SELECTOR);
        testPoints.add(DEFAULT_VARIABLE, DEFAULT_SELECTOR);

        DefaultProgramState testState =
                new DefaultProgramState(testGraphs.getInput_getActualViolationPoints_Mixed());
        testState.prepareHeap();

        Pair<Integer, String> result
                = materializer.getActualViolationPoint(testState.getHeap(), testPoints);
        assertNotNull(result);
        assertEquals(ANNOTATED_SELECTOR, result.second());
        assertEquals(new Integer(NODE_FOR_DEFAULT_VARIABLE), result.first());
    }

    @Test
    public void testGetActualViolationPoint_Mixed_DefaultPresent() {

        ViolationPoints testPoints
                = new ViolationPoints();
        testPoints.add(DEFAULT_VARIABLE, DEFAULT_SELECTOR);

        DefaultProgramState testState =
                new DefaultProgramState(testGraphs.getInput_getActualViolationPoints_Mixed());
        testState.prepareHeap();

        Pair<Integer, String> result
                = materializer.getActualViolationPoint(testState.getHeap(), testPoints);
        assertNull(result);
    }

    @Test
    public void testGetActualViolationPoint_Mixed_IndexedPresent() {

        ViolationPoints testPoints
                = new ViolationPoints();
        testPoints.add(ANNOTATED_VARIABLE, ANNOTATED_SELECTOR);

        DefaultProgramState testState =
                new DefaultProgramState(testGraphs.getInput_getActualViolationPoints_Mixed());
        testState.prepareHeap();

        Pair<Integer, String> result
                = materializer.getActualViolationPoint(testState.getHeap(), testPoints);
        assertNull(result);
    }

}
