package de.rwth.i2.attestor.grammar.materialization;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.materialization.communication.CannotMaterializeException;
import de.rwth.i2.attestor.graph.GeneralNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminalImpl;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.IndexSymbol;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IndexMaterializationStrategyTest {

	private static final String UNIQUE_NT_LABEL = "IndexMaterializationStrategyTest";
	private static final int RANK = 3;
	private static final boolean[] REDUCTION_TENTACLES = new boolean[]{true,false,true};


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}


	@Test
	public void testOn_Materializable_EmptyStack_EmptyMaterialization() throws CannotMaterializeException {

		IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

		AbstractIndexSymbol symbolToMaterialize = someAbstractSymbol();
		HeapConfiguration inputGraph = unmaterializedGraphWithEmptyStack( symbolToMaterialize );
		List<IndexSymbol> inputMaterializationPostfix = emptyMaterialization();
		HeapConfiguration expected = materializedGraph_EmptyStack_EmptyMaterialization( symbolToMaterialize );
		HeapConfiguration actual = 
				indexMaterializationStrategy.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraphWithEmptyStack( symbolToMaterialize ), 
												inputGraph);	
	}

	@Test
	public void testOn_Materializable_EmptyStack_AbstractMaterialization() throws CannotMaterializeException{
		IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

		AbstractIndexSymbol symbolToMaterialize = someAbstractSymbol();
		
		HeapConfiguration inputGraph = unmaterializedGraphWithEmptyStack( symbolToMaterialize );
		List<IndexSymbol> inputMaterializationPostfix = abstractMaterialization();
		HeapConfiguration expected = materializedGraph_EmptyStack_AbstractMaterialization();
		HeapConfiguration actual = 
				indexMaterializationStrategy.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraphWithEmptyStack( symbolToMaterialize ), inputGraph);	
	}

	@Test
	public void testOn_Materializable_EmptyStack_ConcreteMaterialization() throws CannotMaterializeException{
		IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();
		
		AbstractIndexSymbol symbolToMaterialize = otherAbstractSymbol();

		HeapConfiguration inputGraph = unmaterializedGraphWithEmptyStack(symbolToMaterialize );
		List<IndexSymbol> inputMaterializationPostfix = concreteMaterialization();
		HeapConfiguration expected = materializedGraph_EmptyStack_concreteMaterialization();
		HeapConfiguration actual = 
				indexMaterializationStrategy.getMaterializedCloneWith( inputGraph,
															symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraphWithEmptyStack( symbolToMaterialize ), 
												inputGraph);
	}

	@Test
	public void testOn_Materializable_NonEmptyStack_ConcreteMaterialization() throws CannotMaterializeException{
		IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

		AbstractIndexSymbol symbolToMaterialize = someAbstractSymbol();
		HeapConfiguration inputGraph = unmaterializedGraph_NonEmptyStack( symbolToMaterialize );
		List<IndexSymbol> inputMaterializationPostfix = concreteMaterialization();
		HeapConfiguration expected = materializedGraph_NonEmptyStack_concreteMaterialization();
		HeapConfiguration actual = 
				indexMaterializationStrategy.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraph_NonEmptyStack(symbolToMaterialize), 
												inputGraph);
	}

	/**
	 * We assume that there might be independent stacks (i.e. using different abstract symbols).
	 * The index materializer should only materialize those with the given symbol.
	 * Any other stacks (including concrete ones should simply be ignored)
	 * @throws CannotMaterializeException is not expected and thus indicates an error.
	 */
	@Test
	public void test_ConcreteStack_NonEmptyMaterialiation_ExpectNoException() throws CannotMaterializeException{
		IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

		HeapConfiguration inputGraph = unmaterializedGraph_ConcreteStack();
		List<IndexSymbol> inputMaterializationPostfix = abstractMaterialization();

		indexMaterializationStrategy.getMaterializedCloneWith( inputGraph,  someAbstractSymbol(), inputMaterializationPostfix);

		assertEquals("input graph has changed", unmaterializedGraph_ConcreteStack(), inputGraph);
	}

	@Test
	public void testOn_ConcreteStack_EmptyMaterialization() throws CannotMaterializeException{
		IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

		HeapConfiguration inputGraph = unmaterializedGraph_ConcreteStack();
		List<IndexSymbol> inputMaterializationPostfix = emptyMaterialization();
		HeapConfiguration expected = materializedGraph_ConcreteStack_emptyMaterialization();
		HeapConfiguration actual = 
				indexMaterializationStrategy.getMaterializedCloneWith( inputGraph,  null, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraph_ConcreteStack(), inputGraph);
	}

	@Test
	public void testOn_GraphWithTwoNonterminals_canMaterialize() throws CannotMaterializeException{
		IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

		AbstractIndexSymbol symbolToMaterialize = otherAbstractSymbol();
		HeapConfiguration inputGraph = unmaterializedGraph_TwoNonterminals_canMaterialize( symbolToMaterialize );
		List<IndexSymbol> inputMaterializationPostfix = abstractMaterialization();
		HeapConfiguration expected = materializedGraph_TwoNonterminals_abstractMaterialization();
		HeapConfiguration actual = 
				indexMaterializationStrategy.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", 
						unmaterializedGraph_TwoNonterminals_canMaterialize( symbolToMaterialize ), 
						inputGraph);
	}
	
	@Test
	public void testOn_GraphWithDefaultNonterminal() throws CannotMaterializeException{
		IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();

		AbstractIndexSymbol symbolToMaterialize = someAbstractSymbol();
		HeapConfiguration inputGraph = unmaterializedGraph_TwoNonterminals_oneDefault( symbolToMaterialize );
		List<IndexSymbol> inputMaterializationPostfix = abstractMaterialization();
		HeapConfiguration expected = 
				materializedGraph_TwoNonterminals_oneDefault_abstractMaterialization();
		HeapConfiguration actual = 
				indexMaterializationStrategy.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", 
					  unmaterializedGraph_TwoNonterminals_oneDefault( symbolToMaterialize), inputGraph);
	}
	
	@Test
	public void testOn_GraphWithTwoNonterminals_differentAbstractSymbols() throws CannotMaterializeException {
		IndexMaterializationStrategy indexMaterializationStrategy = new IndexMaterializationStrategy();
		
		AbstractIndexSymbol symbolToMaterialize  = someAbstractSymbol();
		HeapConfiguration inputGraph = 
				inputTwoNonterminalsDifferentAbstractSymbols( symbolToMaterialize );
		List<IndexSymbol> inputMaterializationPostFix = concreteMaterialization();
		HeapConfiguration expected = 
				expectedTwoNonterminalsDifferentAbstractSymbols( inputMaterializationPostFix );
		
		HeapConfiguration actual =
				indexMaterializationStrategy.getMaterializedCloneWith( inputGraph,
														    symbolToMaterialize,
														    inputMaterializationPostFix );
		
		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", 
				inputTwoNonterminalsDifferentAbstractSymbols( symbolToMaterialize ), inputGraph);
		
	}

	private AbstractIndexSymbol someAbstractSymbol() {
		return AbstractIndexSymbol.get("X");
	}

	private AbstractIndexSymbol otherAbstractSymbol() {
		return AbstractIndexSymbol.get("Y");
	}
	
	private List<IndexSymbol> emptyMaterialization() {
		return new ArrayList<>();
	}

	private List<IndexSymbol> abstractMaterialization() {
		IndexSymbol someConcreteIndexSymbol = ConcreteIndexSymbol.getStackSymbol("b", false);
		IndexSymbol someAbstractIndexSymbol = AbstractIndexSymbol.get("Y");

		List<IndexSymbol> abstractMaterialization = new ArrayList<>();
		abstractMaterialization.add(someConcreteIndexSymbol);
		abstractMaterialization.add(someAbstractIndexSymbol);
		abstractMaterialization.add(someAbstractIndexSymbol);
		return abstractMaterialization;
	}

	private List<IndexSymbol> concreteMaterialization() {
		IndexSymbol someConcreteIndexSymbol = ConcreteIndexSymbol.getStackSymbol("b", false);
		IndexSymbol someBottomIndexSymbol = ConcreteIndexSymbol.getStackSymbol("Z", true );

		List<IndexSymbol> abstractMaterialization = new ArrayList<>();
		abstractMaterialization.add(someConcreteIndexSymbol);
		abstractMaterialization.add(someBottomIndexSymbol);
		return abstractMaterialization;
	}
	
	private List<IndexSymbol> emptyStack() {
		return new ArrayList<>();
	}
	
	private List<IndexSymbol> nonEmptyStack(){
		IndexSymbol someIndexSymbol = ConcreteIndexSymbol.getStackSymbol("c", false);
		IndexSymbol otherIndexSymbol = ConcreteIndexSymbol.getStackSymbol("b", false);

		List<IndexSymbol> nonEmptyStack = new ArrayList<>();
		nonEmptyStack.add(someIndexSymbol);
		nonEmptyStack.add(otherIndexSymbol);
		nonEmptyStack.add(someIndexSymbol);

		return nonEmptyStack;
	}
	
	private List<IndexSymbol> concreteStack() {
		IndexSymbol someIndexSymbol = ConcreteIndexSymbol.getStackSymbol("s", false);
		IndexSymbol someBottomSymbol = ConcreteIndexSymbol.getStackSymbol("Z", true);

		List<IndexSymbol> concreteStack = new ArrayList<>();
		concreteStack.add(someIndexSymbol);
		concreteStack.add(someBottomSymbol);

		return concreteStack;
	}


	private HeapConfiguration unmaterializedGraphWithEmptyStack(AbstractIndexSymbol symbolToMaterialize) {
		List<IndexSymbol> materializableEmptyStack = emptyStack();
		materializableEmptyStack.add( symbolToMaterialize );
		return graphWithOneNonterminalAndStack( materializableEmptyStack );
	}

	private HeapConfiguration materializedGraph_EmptyStack_EmptyMaterialization(AbstractIndexSymbol symbolToMaterialize) {
		return unmaterializedGraphWithEmptyStack( symbolToMaterialize );
	}

	private HeapConfiguration materializedGraph_EmptyStack_AbstractMaterialization() {
		List<IndexSymbol> materializedEmptyStack = emptyStack();
		materializedEmptyStack.addAll( abstractMaterialization() );
		return graphWithOneNonterminalAndStack( materializedEmptyStack );
	}

	private HeapConfiguration materializedGraph_EmptyStack_concreteMaterialization() {
		List<IndexSymbol> materializedEmptyStack = emptyStack();
		materializedEmptyStack.addAll( concreteMaterialization() );
		return graphWithOneNonterminalAndStack( materializedEmptyStack );
	}



	private HeapConfiguration unmaterializedGraph_NonEmptyStack(AbstractIndexSymbol abstractIndexSymbol) {
		List<IndexSymbol> materializableNonEmptyStack = nonEmptyStack();
		materializableNonEmptyStack.add(abstractIndexSymbol);
		return graphWithOneNonterminalAndStack( materializableNonEmptyStack );
	}

	private HeapConfiguration materializedGraph_NonEmptyStack_concreteMaterialization() {
		List<IndexSymbol> materializedNonEmptyStack = nonEmptyStack();
		materializedNonEmptyStack.addAll( concreteMaterialization() );
		return graphWithOneNonterminalAndStack( materializedNonEmptyStack );
	}



	private HeapConfiguration unmaterializedGraph_ConcreteStack() {
		return graphWithOneNonterminalAndStack( concreteStack() );
	}

	private HeapConfiguration materializedGraph_ConcreteStack_emptyMaterialization() {
		return unmaterializedGraph_ConcreteStack();
	}


	private HeapConfiguration unmaterializedGraph_TwoNonterminals_canMaterialize(
			AbstractIndexSymbol abstractIndexSymbol) {
		
		List<IndexSymbol> stack1 = emptyStack();
		stack1.add(abstractIndexSymbol);
		List<IndexSymbol> stack2 = nonEmptyStack();
		stack2.add(abstractIndexSymbol);

		return graphWithTwoNonterminalsWithStacks( stack1, stack2 );
	}
	

	private HeapConfiguration materializedGraph_TwoNonterminals_abstractMaterialization() {
		List<IndexSymbol> materializedStack1 = emptyStack();
		materializedStack1.addAll( abstractMaterialization() );
		List<IndexSymbol> materializedStack2 = nonEmptyStack();
		materializedStack2.addAll( abstractMaterialization() );
		
		return graphWithTwoNonterminalsWithStacks(materializedStack1, materializedStack2);
	}

	private HeapConfiguration unmaterializedGraph_TwoNonterminals_oneDefault(
																AbstractIndexSymbol abstractIndexSymbol) {
		
		List<IndexSymbol> stackForIndexedNonterminal = emptyStack();
		stackForIndexedNonterminal.add(abstractIndexSymbol);
		
		return graphWithDefaultNonterminalAndIndexedWithStack( stackForIndexedNonterminal );
	}


	private HeapConfiguration materializedGraph_TwoNonterminals_oneDefault_abstractMaterialization() {
		List<IndexSymbol> materializedStackForIndexedNonterminal = emptyStack();
		materializedStackForIndexedNonterminal.addAll( abstractMaterialization() );
		
		return graphWithDefaultNonterminalAndIndexedWithStack(materializedStackForIndexedNonterminal);
	}
	
	private HeapConfiguration inputTwoNonterminalsDifferentAbstractSymbols(AbstractIndexSymbol symbolToMaterialize) {
		List<IndexSymbol> stackToMaterialize = emptyStack();
		stackToMaterialize.add(symbolToMaterialize);
		List<IndexSymbol> stackNotToMaterialize = emptyStack();
		stackNotToMaterialize.add( otherAbstractSymbol() );
		
		return graphWithTwoNonterminalsWithStacks( stackToMaterialize, stackNotToMaterialize );
	}
	
	private HeapConfiguration expectedTwoNonterminalsDifferentAbstractSymbols(
			List<IndexSymbol> inputMaterializationPostFix) {
		
		List<IndexSymbol> stackToMaterialize = emptyStack();
		stackToMaterialize.addAll( inputMaterializationPostFix );
		List<IndexSymbol> stackNotToMaterialize = emptyStack();
		stackNotToMaterialize.add( otherAbstractSymbol() );
		
		return graphWithTwoNonterminalsWithStacks( stackToMaterialize, stackNotToMaterialize );
	}


	private HeapConfiguration graphWithOneNonterminalAndStack(List<IndexSymbol> stack) {
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
	
	private HeapConfiguration graphWithDefaultNonterminalAndIndexedWithStack(
			List<IndexSymbol> stackForIndexedNonterminal) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = Settings.getInstance().factory().getType("type");

		Nonterminal nt = new IndexedNonterminalImpl( UNIQUE_NT_LABEL, RANK,
												 REDUCTION_TENTACLES, 
												 stackForIndexedNonterminal);
		GeneralNonterminal defaultNt = GeneralNonterminal.getNonterminal(UNIQUE_NT_LABEL,
																RANK, REDUCTION_TENTACLES);
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt)
					.addTentacle(0)
					.addTentacle(1)
					.addTentacle(1)
					.build()
				.addNonterminalEdge(defaultNt)
					.addTentacle( nodes.get(0))
					.addTentacle( nodes.get(1) )
					.addTentacle(nodes.get(1) )
					.build()
				.build();
	}
}
