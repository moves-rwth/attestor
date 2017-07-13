package de.rwth.i2.attestor.grammar.materialization;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.grammar.testUtil.StackGrammarForTests;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.*;

public class StackMatcherTest {

	private static final AbstractStackSymbol ABSTRACT_STACK_SYMBOL = AbstractStackSymbol.get("X");
	private static final boolean[] REDUCTION_TENTACLES = new boolean[]{false,false};
	private static final int NONTERMINAL_RANK = 2;
	private static final String NONTERMINAL_LABEL = "StackMatcherTest";
	static final StackSymbol s = ConcreteStackSymbol.getStackSymbol("s", false);
	static final StackSymbol a = ConcreteStackSymbol.getStackSymbol("a", false);
	static final StackSymbol bottom = ConcreteStackSymbol.getStackSymbol("Z", true);
	
	StackMatcher stackMatcher;
	
	@Before
	public void setUp() throws Exception {
		StackMaterializationStrategy stackGrammar = new StackGrammarForTests();
		stackMatcher = new StackMatcher( stackGrammar );
	}

	@Test
	public void testIdenticalStacks() {
		IndexedNonterminal nt1  = createConcreteNonterminal();
		IndexedNonterminal nt2 = createConcreteNonterminal();
		
		assertTrue( stackMatcher.canMatch( nt1, nt2) ); 
		assertFalse( stackMatcher.needsMaterialization( nt1, nt2) );
		assertThat( stackMatcher.getMaterializationRule(nt1, nt2).second(), empty() );
		assertFalse( stackMatcher.needsInstantiation( nt1, nt2) );
		assertThat( stackMatcher.getNecessaryInstantiation( nt1, nt2), empty() );
		
	}
	
	@Test
	public void testInstantiableStack(){
		IndexedNonterminal nt1 = createConcreteNonterminal();
		IndexedNonterminal instantiableNonterminal = createInstantiableNonterminal();
		
		assertTrue( stackMatcher.canMatch(nt1, instantiableNonterminal) );
		assertFalse( stackMatcher.needsMaterialization(nt1, instantiableNonterminal) );
		assertNull( stackMatcher.getMaterializationRule(nt1, instantiableNonterminal).first() );
		assertThat( stackMatcher.getMaterializationRule(nt1, instantiableNonterminal).second(),
					empty() );
		assertTrue( stackMatcher.needsInstantiation(nt1, instantiableNonterminal) );
		assertThat( stackMatcher.getNecessaryInstantiation(nt1, instantiableNonterminal),
				     contains( s, bottom ) );
	}
	
	@Test
	public void testMaterializableStack(){
		IndexedNonterminal materializableNonterminal = createMaterializableNonterminal();
		IndexedNonterminal nt2 = createConcreteNonterminal();
		
		assertTrue( stackMatcher.canMatch(materializableNonterminal, nt2) );
		assertTrue( stackMatcher.needsMaterialization(materializableNonterminal, nt2) );
		assertEquals( ABSTRACT_STACK_SYMBOL, 
					  stackMatcher.getMaterializationRule(materializableNonterminal, nt2).first());
		assertThat( stackMatcher.getMaterializationRule(materializableNonterminal, nt2).second(), 
				contains( a, s, bottom ));
		assertFalse( stackMatcher.needsInstantiation(materializableNonterminal, nt2) );
		assertThat( stackMatcher.getNecessaryInstantiation(materializableNonterminal, nt2),
				      empty());
	}
	
	@Test
	public void testMaterializableAndInstantiableStack(){
		IndexedNonterminal materializableNonterminal = createMaterializableNonterminal();
		IndexedNonterminal nt2 = createInstantiableNonterminal();
		
		assertTrue( stackMatcher.canMatch(materializableNonterminal, nt2) );
		assertTrue( stackMatcher.needsMaterialization(materializableNonterminal, nt2) );
		assertEquals( ABSTRACT_STACK_SYMBOL, 
					  stackMatcher.getMaterializationRule(materializableNonterminal, nt2).first());
		assertThat( stackMatcher.getMaterializationRule(materializableNonterminal, nt2).second(), 
				contains( a, ABSTRACT_STACK_SYMBOL ));
		assertTrue( stackMatcher.needsInstantiation(materializableNonterminal, nt2) );
		assertThat( stackMatcher.getNecessaryInstantiation(materializableNonterminal, nt2),
				      contains( ABSTRACT_STACK_SYMBOL));
	}

	private IndexedNonterminal createMaterializableNonterminal() {
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add( ABSTRACT_STACK_SYMBOL );
		return new IndexedNonterminal(NONTERMINAL_LABEL, NONTERMINAL_RANK, REDUCTION_TENTACLES, stack);
	}

	private IndexedNonterminal createInstantiableNonterminal() {
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(a);
		stack.add( StackVariable.getGlobalInstance() );
		return new IndexedNonterminal(NONTERMINAL_LABEL, NONTERMINAL_RANK, REDUCTION_TENTACLES, stack);
	}

	private IndexedNonterminal createConcreteNonterminal() {
			
		List<StackSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(a);
		stack.add(s);
		stack.add(bottom);
		return new IndexedNonterminal(NONTERMINAL_LABEL, 2, REDUCTION_TENTACLES, stack);
	}

}
