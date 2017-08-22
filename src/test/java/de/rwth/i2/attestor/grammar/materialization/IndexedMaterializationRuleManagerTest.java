package de.rwth.i2.attestor.grammar.materialization;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.*;

import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.communication.*;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager;
import de.rwth.i2.attestor.grammar.testUtil.*;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.*;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class IndexedMaterializationRuleManagerTest {

	public static final int RANK = 3;
	public static final String UNIQUE_NT_LABEL = "FakeViolationPointResolver";
	public static final boolean[] REDUCTION_TENTACLES = new boolean[]{false,false};
	
	IndexedNonterminal requestNonterminal = createRequestNonterminal();

	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}


	@Test
	public void checkOnRhsWithoutNonterminals(){
		
		Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();
		
		List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
		hardCodedViolationPointResolverResult.add( uninstantiatedRhsWithoutNonterminal() );
		
		List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
		expectedInstantiatedGraphs.add( instantiatedRhsWihtoutNonterminal() );
		
		performCheckFor( expectedViolationPointResultLhs,
						 hardCodedViolationPointResolverResult,
						 expectedInstantiatedGraphs );
	}
	
	@Test
	public void checkInRhsWithConcreteStack(){
		Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();
		
		List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
		hardCodedViolationPointResolverResult.add( uninstantiatedRhs_OneNonterminal_ConcreteStack() );
		
		List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
		expectedInstantiatedGraphs.add( instantiatedRhs_OneNonterminal_ConcreteStack() );
		
		performCheckFor( expectedViolationPointResultLhs,
						 hardCodedViolationPointResolverResult,
						 expectedInstantiatedGraphs );
	}
	
	@Test
	public void checkOnRhsWithInstantiableEmptyStack(){
		Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();
		
		List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
		hardCodedViolationPointResolverResult.add( uninstantiatedRhs_OneNonterminal_EmptyStack() );
		
		List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
		expectedInstantiatedGraphs.add( instantiatedRhs_OneNonterminal_EmptyStack() );
		
		performCheckFor( expectedViolationPointResultLhs,
						 hardCodedViolationPointResolverResult,
						 expectedInstantiatedGraphs );
	}
	
	@Test
	public void checkOnRhsWithInstantiableNonEmptyStack(){
		Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();
		
		List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
		hardCodedViolationPointResolverResult.add( uninstantiatedRhs_OneNonterminal_NonEmptyStack() );
		
		List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
		expectedInstantiatedGraphs.add( instantiatedRhs_OneNonterminal_NonEmptyStack() );
		
		performCheckFor( expectedViolationPointResultLhs,
						 hardCodedViolationPointResolverResult,
						 expectedInstantiatedGraphs );
	}
	
	@Test
	public void checkOnRhsWithTwoNonterminals(){
		Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();
		
		List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
		hardCodedViolationPointResolverResult.add( uninstantiatedRhs_TwoNonterminals() );
		
		List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
		expectedInstantiatedGraphs.add( instantiatedRhs_TwoNonterminals() );
		
		performCheckFor( expectedViolationPointResultLhs,
						 hardCodedViolationPointResolverResult,
						 expectedInstantiatedGraphs );
	}
	
	@Test
	public void checkOnMultipleRhs(){
		Collection<Nonterminal> expectedViolationPointResultLhs = createExampleNts();
		
		List<HeapConfiguration> hardCodedViolationPointResolverResult = new ArrayList<>();
		List<HeapConfiguration> expectedInstantiatedGraphs = new ArrayList<>();
		
		hardCodedViolationPointResolverResult.add( uninstantiatedRhsWithoutNonterminal() );
		             expectedInstantiatedGraphs.add( instantiatedRhsWihtoutNonterminal() );
		hardCodedViolationPointResolverResult.add( uninstantiatedRhs_OneNonterminal_EmptyStack() );
					expectedInstantiatedGraphs.add(  instantiatedRhs_OneNonterminal_EmptyStack() );
		hardCodedViolationPointResolverResult.add( uninstantiatedRhs_OneNonterminal_ConcreteStack() );
		            expectedInstantiatedGraphs.add( instantiatedRhs_OneNonterminal_ConcreteStack() );
		
		performCheckFor( expectedViolationPointResultLhs,
						 hardCodedViolationPointResolverResult,
						 expectedInstantiatedGraphs );
	}
	
	@Test
	public void testDefaultCaseOnIndexedManager(){
		ViolationPointResolver grammarLogik = new FakeViolationPointResolverForDefault();
		MaterializationRuleManager ruleManager = new IndexedMaterializationRuleManager( grammarLogik, null );
		
		GrammarResponse actualResponse;
		try {
			Nonterminal nonterminal = FakeViolationPointResolverForDefault.DEFAULT_NONTERMINAL;
			int tentacleForNext = 0;
			String requestLabel = "some label";
			actualResponse = ruleManager.getRulesFor(nonterminal, 
													tentacleForNext, 
													requestLabel);
		
			assertTrue( actualResponse instanceof DefaultGrammarResponse );
			DefaultGrammarResponse defaultResponse = (DefaultGrammarResponse) actualResponse;
			assertTrue(defaultResponse.getApplicableRules()
					.contains( FakeViolationPointResolverForDefault.RHS_CREATING_NEXT) );
			assertTrue( defaultResponse.getApplicableRules()
					.contains( FakeViolationPointResolverForDefault.RHS_CREATING_NEXT_PREV ));
			
		} catch (UnexpectedNonterminalTypeException e) {
			fail("Unexpected exception");
		}
	}


	private void performCheckFor( Collection<Nonterminal> expectedViolationPointResultLhs,
								  Collection<HeapConfiguration> hardCodedViolationPointResoverResult, 
								  Collection<HeapConfiguration> expectedInstantiatedGraphs ) {
		
		FakeViolationPointResolver fakeVioResolver = new FakeViolationPointResolver();
		fakeVioResolver.defineReturnedLhsForTest( expectedViolationPointResultLhs );
		fakeVioResolver.defineRhsForAllNonterminals( hardCodedViolationPointResoverResult );
		

		IndexMatcher fakeIndexMatcher = new FakeIndexMatcher();
		
		IndexedMaterializationRuleManager ruleManager = 
				new IndexedMaterializationRuleManager( fakeVioResolver, fakeIndexMatcher);
		
		GrammarResponse actualGrammarResponse;
		try {
			actualGrammarResponse = ruleManager.getRulesFor(requestNonterminal, 0, "some_label");
			
			
			assertTrue( actualGrammarResponse instanceof MaterializationAndRuleResponse );
			MaterializationAndRuleResponse indexedResponse = (MaterializationAndRuleResponse) actualGrammarResponse;
			
			final List<IndexSymbol> expectedMaterialization = FakeIndexMatcher.MATERIALIZATION;
			assertTrue( indexedResponse.getPossibleMaterializations().contains( expectedMaterialization) );
			for( HeapConfiguration expectedInstantiatedRhs : expectedInstantiatedGraphs ){
			assertTrue( indexedResponse.getRulesForMaterialization(expectedMaterialization)
					.contains( expectedInstantiatedRhs ));
			}
			
		} catch (UnexpectedNonterminalTypeException e) {
			fail("Unexpected Exception");
		}
	}

		
	private IndexedNonterminal createRequestNonterminal() {
		final ArrayList<IndexSymbol> stack = new ArrayList<>();

		final IndexSymbol someAbstractIndexSymbol = AbstractIndexSymbol.get("SomeAbstractStackSymbol");
		stack.add(someAbstractIndexSymbol);
		return new IndexedNonterminalImpl(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES, stack);
	}
	
	private static Collection<Nonterminal> createExampleNts() {
		
		IndexSymbol a = ConcreteIndexSymbol.getIndexSymbol("a", false);
		IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
		IndexSymbol bottom1 = ConcreteIndexSymbol.getIndexSymbol("Z", true);
		IndexSymbol var = IndexVariable.getGlobalInstance();
		
		List<IndexSymbol> stack1 = new ArrayList<>();
		stack1.add(a);
		stack1.add(s);
		stack1.add(bottom1);
		IndexedNonterminal nt1 = new IndexedNonterminalImpl(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES, stack1);
		
		List<IndexSymbol> stack2 = new ArrayList<>();
		stack2.add(s);
		stack2.add(var);
		IndexedNonterminal nt2 = new IndexedNonterminalImpl(UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES, stack2);
		
		Set<Nonterminal> result = new HashSet<>();
		result.add( nt1 );
		result.add( nt2 );
		
		return result;
	}

//##### No Nonterminal ####
	
	private HeapConfiguration uninstantiatedRhsWithoutNonterminal(){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		Type type = Settings.getInstance().factory().getType("type");
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
			.build();
	}
	
	private HeapConfiguration instantiatedRhsWihtoutNonterminal() {
		return uninstantiatedRhsWithoutNonterminal();
	}
	
//##### One Nonterminal #######
	
	private static HeapConfiguration graphWithOneNonterminalWithStack( List<IndexSymbol> stack) {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		Type type = Settings.getInstance().factory().getType("type");
		
		Nonterminal nt = new IndexedNonterminalImpl( UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES, stack);
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
			.addNonterminalEdge(nt)
				.addTentacle(0)
				.addTentacle(1)
				.addTentacle(1)
				.build()
			.build();
	}
	
	//----- Empty Instantiable Index ----------

	private List<IndexSymbol> emptyStack(){
		return new ArrayList<>();
	}
	
	private HeapConfiguration uninstantiatedRhs_OneNonterminal_EmptyStack(){
		List<IndexSymbol> uninstantiatedEmptyStack = emptyStack();
		uninstantiatedEmptyStack.add( IndexVariable.getGlobalInstance() );
		return graphWithOneNonterminalWithStack( uninstantiatedEmptyStack );
	}
	
	private HeapConfiguration instantiatedRhs_OneNonterminal_EmptyStack(){
		List<IndexSymbol> instantiatedEmptyStack = emptyStack();
		instantiatedEmptyStack.addAll( FakeIndexMatcher.INSTANTIATION );
		return graphWithOneNonterminalWithStack( instantiatedEmptyStack );
	}
	
	//------ Non-Empty Instantiable Index ------
	
	private List<IndexSymbol> nonEmptyStack(){
		IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
		IndexSymbol a = ConcreteIndexSymbol.getIndexSymbol("a", false);
		
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(a);
		stack.add(s);
		stack.add(s);
		
		return stack;
	}
	
	private HeapConfiguration uninstantiatedRhs_OneNonterminal_NonEmptyStack(){
		List<IndexSymbol> uninstantiatedNonEmptyStack = nonEmptyStack();
		uninstantiatedNonEmptyStack.add( IndexVariable.getGlobalInstance() );
		return graphWithOneNonterminalWithStack( uninstantiatedNonEmptyStack );
	}
	
	private HeapConfiguration instantiatedRhs_OneNonterminal_NonEmptyStack(){
		List<IndexSymbol> instantiatedNonEmptyStack = nonEmptyStack();
		instantiatedNonEmptyStack.addAll( FakeIndexMatcher.INSTANTIATION );
		return graphWithOneNonterminalWithStack( instantiatedNonEmptyStack );
	}
	
	//---------- Concrete Index --------------------------
	
	private List<IndexSymbol> concreteStack(){
		IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
		IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
		
		List<IndexSymbol> stack = new ArrayList<>();
		stack.add(s);
		stack.add(s);
		stack.add(bottom);
		
		return stack;
	}
	
	private HeapConfiguration uninstantiatedRhs_OneNonterminal_ConcreteStack(){
		List<IndexSymbol> concreteStack = concreteStack();
		return graphWithOneNonterminalWithStack( concreteStack );
	}
	
	private HeapConfiguration instantiatedRhs_OneNonterminal_ConcreteStack(){
		return uninstantiatedRhs_OneNonterminal_ConcreteStack();
	}
	
	//=============== Two Nonterminals ======================

	private HeapConfiguration graphWithTwoNonterminalsWithStacks( List<IndexSymbol> stack1,
																  List<IndexSymbol> stack2 ){
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		Type type = Settings.getInstance().factory().getType("type");
		
		Nonterminal nt1 = new IndexedNonterminalImpl( UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES, stack1);
		Nonterminal nt2 = new IndexedNonterminalImpl(UNIQUE_NT_LABEL, stack2);
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
			.addNonterminalEdge(nt1)
				.addTentacle( nodes.get(0) )
				.addTentacle( nodes.get(0) )
				.addTentacle( nodes.get(1) )
				.build()
			.addNonterminalEdge(nt2)
				.addTentacle(nodes.get(1) )
				.addTentacle( nodes.get(0) )
				.addTentacle( nodes.get(1) )
				.build()
			.build();
	}
	
	private HeapConfiguration uninstantiatedRhs_TwoNonterminals(){
		List<IndexSymbol> stack1 = emptyStack();
		stack1.add( IndexVariable.getGlobalInstance() );
		List<IndexSymbol> stack2 = nonEmptyStack();
		stack2.add( IndexVariable.getGlobalInstance() );
		return graphWithTwoNonterminalsWithStacks( stack1, stack2 );
	}
	
	private HeapConfiguration instantiatedRhs_TwoNonterminals(){
		List<IndexSymbol> stack1 = emptyStack();
		stack1.addAll( FakeIndexMatcher.INSTANTIATION  );
		List<IndexSymbol> stack2 = nonEmptyStack();
		stack2.addAll( FakeIndexMatcher.INSTANTIATION );
		return graphWithTwoNonterminalsWithStacks( stack1, stack2 );
	}
	
}
