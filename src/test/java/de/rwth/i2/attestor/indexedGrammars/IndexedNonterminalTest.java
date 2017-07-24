package de.rwth.i2.attestor.indexedGrammars;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.indexedGrammars.stack.ConcreteStackSymbol;
import de.rwth.i2.attestor.indexedGrammars.stack.StackSymbol;

public class IndexedNonterminalTest {
	//private static final Logger logger = LogManager.getLogger( "IndexedNonterminalTest" );

	private IndexedNonterminal testNonterminal;
	private ConcreteStackSymbol s;
	private ConcreteStackSymbol bottom;


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Before
	public void testIndexedNonterminal() {
		s = ConcreteStackSymbol.getStackSymbol( "s", false );
		bottom = ConcreteStackSymbol.getStackSymbol( "Z", true );
		ArrayList<StackSymbol> stack = new ArrayList<>();
		stack.add( s );
		stack.add( s );
		stack.add( s );
		stack.add( bottom );
		testNonterminal = new IndexedNonterminal( "Test", 3, new boolean [] {false,false,false}, stack );
	}

	@Test
	public void testStackStartsWithPrefix() {
		List<StackSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		assertTrue( testNonterminal.stackStartsWith( prefix ) );
	}
	
	@Test
	public void testStackStartsWithSame() {
		List<StackSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		prefix.add( s );
		prefix.add( bottom );
		assertTrue( testNonterminal.stackStartsWith( prefix ) );
	}
	
	@Test
	public void testStackStartsWithDifferent() {
		ConcreteStackSymbol s2 = ConcreteStackSymbol.getStackSymbol( "s2", false );
		List<StackSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s2 );
		prefix.add( s );
		assertFalse( testNonterminal.stackStartsWith( prefix ) );
	}
	
	@Test
	public void testStackStartsWithLong() {
		List<StackSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		prefix.add( s );
		prefix.add( bottom );
		prefix.add( bottom );
		assertFalse( testNonterminal.stackStartsWith( prefix ) );
	}

	@Test
	public void testStackEndsWith() {
		assertTrue( testNonterminal.stackEndsWith( bottom ) );
		assertFalse( testNonterminal.stackEndsWith( s ));
		
	}
	
	@Test
	public void testGetWithShortendedStack(){
		IndexedNonterminal res = testNonterminal.getWithShortenedStack();
		assertTrue(testNonterminal.stackEndsWith(bottom));
		assertTrue(res.stackEndsWith(s));
		List<StackSymbol> expectedPrefix = new ArrayList<>();
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		assertTrue( res.stackStartsWith( expectedPrefix ) );
		assertEquals( 3, res.stackSize());
	}
	
	@Test
	public void testGetWithProlongedStack(){
		IndexedNonterminal res = testNonterminal.getWithProlongedStack( s );
		assertTrue(testNonterminal.stackEndsWith(bottom));
		assertTrue(res.stackEndsWith(s));
		List<StackSymbol> expectedPrefix = new ArrayList<>();
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		expectedPrefix.add(bottom);
		expectedPrefix.add(s);
		assertTrue( res.stackStartsWith( expectedPrefix ) );
		assertEquals( 5, res.stackSize());
	}


	@Test
	public void testHasConcreteStack() {
		assertTrue( testNonterminal.hasConcreteStack() );
		IndexedNonterminal res = testNonterminal.getWithShortenedStack();
		assertFalse( res.hasConcreteStack() );
	}

	@Test
	public void testStackSize() {
		assertEquals( 4, testNonterminal.stackSize() );
	}
}
