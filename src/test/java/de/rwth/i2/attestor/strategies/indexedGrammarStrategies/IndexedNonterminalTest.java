package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;


public class IndexedNonterminalTest {
	//private static final Logger logger = LogManager.getLogger( "IndexedNonterminalTest" );

	private IndexedNonterminal testNonterminal;
	private ConcreteIndexSymbol s;
	private ConcreteIndexSymbol bottom;


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Before
	public void testIndexedNonterminal() {
		s = ConcreteIndexSymbol.getIndexSymbol( "s", false );
		bottom = ConcreteIndexSymbol.getIndexSymbol( "Z", true );
		ArrayList<IndexSymbol> stack = new ArrayList<>();
		stack.add( s );
		stack.add( s );
		stack.add( s );
		stack.add( bottom );
		testNonterminal = new IndexedNonterminalImpl( "Test", 3, new boolean [] {false,false,false}, stack );
	}

	@Test
	public void testStackStartsWithPrefix() {
		List<IndexSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		assertTrue( testNonterminal.getIndex().startsWith( prefix ) );
	}
	
	@Test
	public void testStackStartsWithSame() {
		List<IndexSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		prefix.add( s );
		prefix.add( bottom );
		assertTrue( testNonterminal.getIndex().startsWith( prefix ) );
	}
	
	@Test
	public void testStackStartsWithDifferent() {
		ConcreteIndexSymbol s2 = ConcreteIndexSymbol.getIndexSymbol( "s2", false );
		List<IndexSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s2 );
		prefix.add( s );
		assertFalse( testNonterminal.getIndex().startsWith( prefix ) );
	}
	
	@Test
	public void testStackStartsWithLong() {
		List<IndexSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		prefix.add( s );
		prefix.add( bottom );
		prefix.add( bottom );
		assertFalse( testNonterminal.getIndex().startsWith( prefix ) );
	}

	@Test
	public void testStackEndsWith() {
		assertTrue( testNonterminal.getIndex().endsWith( bottom ) );
		assertFalse( testNonterminal.getIndex().endsWith( s ));
		
	}
	
	@Test
	public void testGetWithShortendedStack(){
		IndexedNonterminal res = testNonterminal.getWithShortenedStack();

		assertTrue(testNonterminal.getIndex().endsWith(bottom));
		assertTrue(res.getIndex().endsWith(s));
		List<IndexSymbol> expectedPrefix = new ArrayList<>();
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		assertTrue( res.getIndex().startsWith( expectedPrefix ) );
		assertEquals( 3, res.getIndex().size());
	}
	
	@Test
	public void testGetWithProlongedStack(){
		IndexedNonterminal res = testNonterminal.getWithProlongedStack( s );

		assertTrue(testNonterminal.getIndex().endsWith(bottom));
		assertTrue(res.getIndex().endsWith(s));
		List<IndexSymbol> expectedPrefix = new ArrayList<>();
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		expectedPrefix.add(bottom);
		expectedPrefix.add(s);
		assertTrue( res.getIndex().startsWith(expectedPrefix) );
		assertEquals( 5, res.getIndex().size());
	}


	@Test
	public void testHasConcreteStack() {
		assertTrue( testNonterminal.getIndex().hasConcreteStack() );
		IndexedNonterminal res = testNonterminal.getWithShortenedStack();
		assertFalse( res.getIndex().hasConcreteStack() );
	}

	@Test
	public void testStackSize() {
		assertEquals( 4, testNonterminal.getIndex().size() );
	}
}
