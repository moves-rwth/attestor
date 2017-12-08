package de.rwth.i2.attestor.util;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MatchingSelectorDistanceHelperTest {

    @Test
    public void testHasMatch_WithEquals() {

        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);
        testList.add(3);

        Predicate<Integer> matchingFunction = a -> a.equals(1);
        assertTrue(MatchingUtil.containsMatch(testList, matchingFunction));
    }

    @Test
    public void testHasMatch_OnSelectors() {

        SceneObject sceneObject = new MockupSceneObject();
        SelectorLabel basicA = sceneObject.scene().getSelectorLabel("a");
        SelectorLabel basicB = sceneObject.scene().getSelectorLabel("b");
        SelectorLabel basicC = sceneObject.scene().getSelectorLabel("c");

        List<SelectorLabel> selectorList = new ArrayList<>();
        selectorList.add(basicA);
        selectorList.add(new AnnotatedSelectorLabel(basicB, "a"));
        selectorList.add(new AnnotatedSelectorLabel(basicC, "c"));

        Predicate<SelectorLabel> matchingFunction = a -> a.hasLabel("a");
        assertTrue(MatchingUtil.containsMatch(selectorList, matchingFunction));
        assertTrue(MatchingUtil.containsMatch(selectorList, a -> a.hasLabel("b")));
        assertTrue(MatchingUtil.containsMatch(selectorList, a -> a.hasLabel("c")));
        assertFalse(MatchingUtil.containsMatch(selectorList, a -> a.hasLabel("d")));
    }

    @Test
    public void testHasMatch_OnCollection() {

        SceneObject sceneObject = new MockupSceneObject();
        SelectorLabel basicA = sceneObject.scene().getSelectorLabel("a");
        SelectorLabel basicB = sceneObject.scene().getSelectorLabel("b");
        SelectorLabel basicC = sceneObject.scene().getSelectorLabel("c");

        Collection<SelectorLabel> selectorCollection = new LinkedHashSet<>();
        selectorCollection.add(basicA);
        selectorCollection.add(new AnnotatedSelectorLabel(basicB, "a"));
        selectorCollection.add(new AnnotatedSelectorLabel(basicC, "c"));

        Predicate<SelectorLabel> matchingFunction = a -> a.hasLabel("a");
        assertTrue(MatchingUtil.containsMatch(selectorCollection, matchingFunction));
        assertTrue(MatchingUtil.containsMatch(selectorCollection, a -> a.hasLabel("b")));
        assertTrue(MatchingUtil.containsMatch(selectorCollection, a -> a.hasLabel("c")));
        assertFalse(MatchingUtil.containsMatch(selectorCollection, a -> a.hasLabel("d")));
    }

}
