package de.rwth.i2.attestor.grammar.materialization;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.testUtil.IndexGrammarForTests;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.*;

public class IndexMatcherTest {

	private static final AbstractIndexSymbol ABSTRACT_INDEX_SYMBOL = AbstractIndexSymbol.get("X");
	private static final boolean[] REDUCTION_TENTACLES = new boolean[]{false,false};
	private static final int NONTERMINAL_RANK = 2;
	private static final String NONTERMINAL_LABEL = "IndexMatcherTest";
	static final IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
	static final IndexSymbol a = ConcreteIndexSymbol.getIndexSymbol("a", false);
	static final IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
	
	IndexMatcher indexMatcher;

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}


	@Before
	public void setUp() throws Exception {
		IndexMaterializationStrategy indexGrammar = new IndexGrammarForTests();
		indexMatcher = new IndexMatcher( indexGrammar );
	}

	@Test
	public void testIdenticalIndices() {
		IndexedNonterminal nt1  = createConcreteNonterminal();
		IndexedNonterminal nt2 = createConcreteNonterminal();
		
		assertTrue( indexMatcher.canMatch( nt1, nt2) );
		assertFalse( indexMatcher.needsMaterialization( nt1, nt2) );
		assertThat( indexMatcher.getMaterializationRule(nt1, nt2).second(), empty() );
		assertFalse( indexMatcher.needsInstantiation( nt1, nt2) );
		assertThat( indexMatcher.getNecessaryInstantiation( nt1, nt2), empty() );
		
	}
	
	@Test
	public void testInstantiableIndex(){
		IndexedNonterminal nt1 = createConcreteNonterminal();
		IndexedNonterminal instantiableNonterminal = createInstantiableNonterminal();
		
		assertTrue( indexMatcher.canMatch(nt1, instantiableNonterminal) );
		assertFalse( indexMatcher.needsMaterialization(nt1, instantiableNonterminal) );
		assertNull( indexMatcher.getMaterializationRule(nt1, instantiableNonterminal).first() );
		assertThat( indexMatcher.getMaterializationRule(nt1, instantiableNonterminal).second(),
					empty() );
		assertTrue( indexMatcher.needsInstantiation(nt1, instantiableNonterminal) );
		assertThat( indexMatcher.getNecessaryInstantiation(nt1, instantiableNonterminal),
				     contains( s, bottom ) );
	}
	
	@Test
	public void testMaterializableIndex(){
		IndexedNonterminal materializableNonterminal = createMaterializableNonterminal();
		IndexedNonterminal nt2 = createConcreteNonterminal();
		
		assertTrue( indexMatcher.canMatch(materializableNonterminal, nt2) );
		assertTrue( indexMatcher.needsMaterialization(materializableNonterminal, nt2) );
		assertEquals( ABSTRACT_INDEX_SYMBOL, 
					  indexMatcher.getMaterializationRule(materializableNonterminal, nt2).first());
		assertThat( indexMatcher.getMaterializationRule(materializableNonterminal, nt2).second(),
				contains( a, s, bottom ));
		assertFalse( indexMatcher.needsInstantiation(materializableNonterminal, nt2) );
		assertThat( indexMatcher.getNecessaryInstantiation(materializableNonterminal, nt2),
				      empty());
	}
	
	@Test
	public void testMaterializableAndInstantiableIndex(){
		IndexedNonterminal materializableNonterminal = createMaterializableNonterminal();
		IndexedNonterminal nt2 = createInstantiableNonterminal();
		
		assertTrue( indexMatcher.canMatch(materializableNonterminal, nt2) );
		assertTrue( indexMatcher.needsMaterialization(materializableNonterminal, nt2) );
		assertEquals( ABSTRACT_INDEX_SYMBOL, 
					  indexMatcher.getMaterializationRule(materializableNonterminal, nt2).first());
		assertThat( indexMatcher.getMaterializationRule(materializableNonterminal, nt2).second(),
				contains( a, ABSTRACT_INDEX_SYMBOL ));
		assertTrue( indexMatcher.needsInstantiation(materializableNonterminal, nt2) );
		assertThat( indexMatcher.getNecessaryInstantiation(materializableNonterminal, nt2),
				      contains( ABSTRACT_INDEX_SYMBOL));
	}

	private IndexedNonterminal createMaterializableNonterminal() {
		List<IndexSymbol> index = new ArrayList<>();
		index.add(s);
		index.add( ABSTRACT_INDEX_SYMBOL );
		return new IndexedNonterminalImpl(NONTERMINAL_LABEL, NONTERMINAL_RANK, REDUCTION_TENTACLES, index);
	}

	private IndexedNonterminal createInstantiableNonterminal() {
		List<IndexSymbol> index = new ArrayList<>();
		index.add(s);
		index.add(a);
		index.add( IndexVariable.getGlobalInstance() );
		return new IndexedNonterminalImpl(NONTERMINAL_LABEL, NONTERMINAL_RANK, REDUCTION_TENTACLES, index);
	}

	private IndexedNonterminal createConcreteNonterminal() {
			
		List<IndexSymbol> index = new ArrayList<>();
		index.add(s);
		index.add(a);
		index.add(s);
		index.add(bottom);
		return new IndexedNonterminalImpl(NONTERMINAL_LABEL, 2, REDUCTION_TENTACLES, index);
	}

}
