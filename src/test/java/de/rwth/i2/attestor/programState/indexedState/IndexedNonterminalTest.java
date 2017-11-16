package de.rwth.i2.attestor.programState.indexedState;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;


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
		ArrayList<IndexSymbol> index = new ArrayList<>();
		index.add( s );
		index.add( s );
		index.add( s );
		index.add( bottom );
		testNonterminal = new IndexedNonterminalImpl( "Test", 3, new boolean [] {false,false,false}, index );
	}

	@Test
	public void testIndexStartsWithPrefix() {
		List<IndexSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		assertTrue( testNonterminal.getIndex().startsWith( prefix ) );
	}
	
	@Test
	public void testIndexStartsWithSame() {
		List<IndexSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		prefix.add( s );
		prefix.add( bottom );
		assertTrue( testNonterminal.getIndex().startsWith( prefix ) );
	}
	
	@Test
	public void testIndexStartsWithDifferent() {
		ConcreteIndexSymbol s2 = ConcreteIndexSymbol.getIndexSymbol( "s2", false );
		List<IndexSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s2 );
		prefix.add( s );
		assertFalse( testNonterminal.getIndex().startsWith( prefix ) );
	}
	
	@Test
	public void testIndexStartsWithLong() {
		List<IndexSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		prefix.add( s );
		prefix.add( bottom );
		prefix.add( bottom );
		assertFalse( testNonterminal.getIndex().startsWith( prefix ) );
	}

	@Test
	public void testIndexEndsWith() {
		assertTrue( testNonterminal.getIndex().endsWith( bottom ) );
		assertFalse( testNonterminal.getIndex().endsWith( s ));
		
	}
	
	@Test
	public void testGetWithShortendedIndex(){
		IndexedNonterminal res = testNonterminal.getWithShortenedIndex();

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
	public void testGetWithProlongedIndex(){
		IndexedNonterminal res = testNonterminal.getWithProlongedIndex( s );

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
	public void testHasConcreteIndex() {
		assertTrue( testNonterminal.getIndex().hasConcreteIndex() );
		IndexedNonterminal res = testNonterminal.getWithShortenedIndex();
		assertFalse( res.getIndex().hasConcreteIndex() );
	}

	@Test
	public void testIndexSize() {
		assertEquals( 4, testNonterminal.getIndex().size() );
	}
}
