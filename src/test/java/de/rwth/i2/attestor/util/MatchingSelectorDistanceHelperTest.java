package de.rwth.i2.attestor.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.*;
import java.util.function.Predicate;

import org.junit.Test;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.indexedGrammars.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;

public class MatchingSelectorDistanceHelperTest {


	@Test
	public void testHasMatch_WithEquals() {
		List<Integer> testList = new ArrayList<>();
		testList.add(1);
		testList.add(2);
		testList.add(3);
		
		Predicate<Integer> matchingFunction = a -> a.equals(1);
		assertTrue( MatchingUtil.containsMatch( testList, matchingFunction ) );
	}
	
	@Test
	public void testHasMatch_OnSelectors(){
		List<SelectorLabel> selectorList = new ArrayList<>();
		selectorList.add( GeneralSelectorLabel.getSelectorLabel("a") );
		selectorList.add( new AnnotatedSelectorLabel("b", "a") );
		selectorList.add( new AnnotatedSelectorLabel("c","c") );
		
		Predicate<SelectorLabel> matchingFunction = a -> a.hasLabel("a");
		assertTrue( MatchingUtil.containsMatch(selectorList, matchingFunction) );
		assertTrue( MatchingUtil.containsMatch(selectorList, a -> a.hasLabel("b")) );
		assertTrue( MatchingUtil.containsMatch(selectorList, a -> a.hasLabel("c")) );
		assertFalse( MatchingUtil.containsMatch(selectorList, a -> a.hasLabel("d")) );
	}
	
	@Test
	public void testHasMatch_OnCollection(){
		Collection<SelectorLabel> selectorCollection = new HashSet<>();
		selectorCollection.add( GeneralSelectorLabel.getSelectorLabel("a") );
		selectorCollection.add( new AnnotatedSelectorLabel("b", "a") );
		selectorCollection.add( new AnnotatedSelectorLabel("c", "c") );
		
		Predicate<SelectorLabel> matchingFunction = a -> a.hasLabel("a");
		assertTrue( MatchingUtil.containsMatch(selectorCollection, matchingFunction) );
		assertTrue( MatchingUtil.containsMatch(selectorCollection, a -> a.hasLabel("b")) );
		assertTrue( MatchingUtil.containsMatch(selectorCollection, a -> a.hasLabel("c")) );
		assertFalse(MatchingUtil.containsMatch(selectorCollection, a -> a.hasLabel("d")) );
	}

}
