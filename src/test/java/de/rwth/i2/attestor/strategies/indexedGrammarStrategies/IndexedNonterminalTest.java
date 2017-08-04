package de.rwth.i2.attestor.strategies.indexedGrammarStrategies;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.ConcreteStackSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack.StackSymbol;

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
		testNonterminal = new IndexedNonterminalImpl( "Test", 3, new boolean [] {false,false,false}, stack );
	}

	@Test
	public void testStackStartsWithPrefix() {
		List<StackSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		assertTrue( testNonterminal.getStack().startsWith( prefix ) );
	}
	
	@Test
	public void testStackStartsWithSame() {
		List<StackSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		prefix.add( s );
		prefix.add( bottom );
		assertTrue( testNonterminal.getStack().startsWith( prefix ) );
	}
	
	@Test
	public void testStackStartsWithDifferent() {
		ConcreteStackSymbol s2 = ConcreteStackSymbol.getStackSymbol( "s2", false );
		List<StackSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s2 );
		prefix.add( s );
		assertFalse( testNonterminal.getStack().startsWith( prefix ) );
	}
	
	@Test
	public void testStackStartsWithLong() {
		List<StackSymbol> prefix = new ArrayList<>();
		prefix.add( s );
		prefix.add( s );
		prefix.add( s );
		prefix.add( bottom );
		prefix.add( bottom );
		assertFalse( testNonterminal.getStack().startsWith( prefix ) );
	}

	@Test
	public void testStackEndsWith() {
		assertTrue( testNonterminal.getStack().stackEndsWith( bottom ) );
		assertFalse( testNonterminal.getStack().stackEndsWith( s ));
		
	}
	
	@Test
	public void testGetWithShortendedStack(){
		IndexedNonterminal res = testNonterminal.getWithShortenedStack();
		assertTrue(testNonterminal.getStack().stackEndsWith(bottom));
		assertTrue(res.getStack().stackEndsWith(s));
		List<StackSymbol> expectedPrefix = new ArrayList<>();
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		assertTrue( res.getStack().startsWith( expectedPrefix ) );
		assertEquals( 3, res.getStack().size());
	}
	
	@Test
	public void testGetWithProlongedStack(){
		IndexedNonterminal res = testNonterminal.getWithProlongedStack( s );
		assertTrue(testNonterminal.getStack().stackEndsWith(bottom));
		assertTrue(res.getStack().stackEndsWith(s));
		List<StackSymbol> expectedPrefix = new ArrayList<>();
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		expectedPrefix.add( s );
		expectedPrefix.add(bottom);
		expectedPrefix.add(s);
		assertTrue( res.getStack().startsWith(expectedPrefix) );
		assertEquals( 5, res.getStack().size());
	}


	@Test
	public void testHasConcreteStack() {
		assertTrue( testNonterminal.getStack().hasConcreteStack() );
		IndexedNonterminal res = testNonterminal.getWithShortenedStack();
		assertFalse( res.getStack().hasConcreteStack() );
	}

	@Test
	public void testStackSize() {
		assertEquals( 4, testNonterminal.getStack().size() );
	}
}
