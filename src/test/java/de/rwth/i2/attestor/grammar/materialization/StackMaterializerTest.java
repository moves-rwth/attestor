package de.rwth.i2.attestor.grammar.materialization;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.rwth.i2.attestor.grammar.materialization.communication.CannotMaterializeException;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.*;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;

public class StackMaterializerTest {

	private static final String UNIQUE_NT_LABEL = "StackMaterializerTest";
	private static final int RANK = 3;
	private static final boolean[] REDUCTION_TENTACLES = new boolean[]{true,false,true};


	@Test
	public void testOn_Materializable_EmptyStack_EmptyMaterialization() throws CannotMaterializeException {

		StackMaterializer stackMaterializer = new StackMaterializer();

		AbstractStackSymbol symbolToMaterialize = someAbstractSymbol();
		HeapConfiguration inputGraph = unmaterializedGraphWithEmptyStack( symbolToMaterialize );
		List<StackSymbol> inputMaterializationPostfix = emptyMaterialization();
		HeapConfiguration expected = materializedGraph_EmptyStack_EmptyMaterialization( symbolToMaterialize );
		HeapConfiguration actual = 
				stackMaterializer.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraphWithEmptyStack( symbolToMaterialize ), 
												inputGraph);	
	}

	@Test
	public void testOn_Materializable_EmptyStack_AbstractMaterialization() throws CannotMaterializeException{
		StackMaterializer stackMaterializer = new StackMaterializer();

		AbstractStackSymbol symbolToMaterialize = someAbstractSymbol();
		
		HeapConfiguration inputGraph = unmaterializedGraphWithEmptyStack( symbolToMaterialize );
		List<StackSymbol> inputMaterializationPostfix = abstractMaterialization();
		HeapConfiguration expected = materializedGraph_EmptyStack_AbstractMaterialization();
		HeapConfiguration actual = 
				stackMaterializer.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraphWithEmptyStack( symbolToMaterialize ), inputGraph);	
	}

	@Test
	public void testOn_Materializable_EmptyStack_ConcreteMaterialization() throws CannotMaterializeException{
		StackMaterializer stackMaterializer = new StackMaterializer();
		
		AbstractStackSymbol symbolToMaterialize = otherAbstractSymbol();

		HeapConfiguration inputGraph = unmaterializedGraphWithEmptyStack(symbolToMaterialize );
		List<StackSymbol> inputMaterializationPostfix = concreteMaterialization();
		HeapConfiguration expected = materializedGraph_EmptyStack_concreteMaterialization();
		HeapConfiguration actual = 
				stackMaterializer.getMaterializedCloneWith( inputGraph,  
															symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraphWithEmptyStack( symbolToMaterialize ), 
												inputGraph);
	}

	@Test
	public void testOn_Materializable_NonEmptyStack_ConcreteMaterialization() throws CannotMaterializeException{
		StackMaterializer stackMaterializer = new StackMaterializer();

		AbstractStackSymbol symbolToMaterialize = someAbstractSymbol();
		HeapConfiguration inputGraph = unmaterializedGraph_NonEmptyStack( symbolToMaterialize );
		List<StackSymbol> inputMaterializationPostfix = concreteMaterialization();
		HeapConfiguration expected = materializedGraph_NonEmptyStack_concreteMaterialization();
		HeapConfiguration actual = 
				stackMaterializer.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraph_NonEmptyStack(symbolToMaterialize), 
												inputGraph);
	}

	/**
	 * We assume that there might be independent stacks (i.e. using different abstract symbols).
	 * The stack materializer should only materialize those with the given symbol.
	 * Any other stacks (including concrete ones should simply be ignored)
	 * @throws CannotMaterializeException is not expected and thus indicates an error.
	 */
	@Test
	public void test_ConcreteStack_NonEmptyMaterialiation_ExpectNoException() throws CannotMaterializeException{
		StackMaterializer stackMaterializer = new StackMaterializer();

		HeapConfiguration inputGraph = unmaterializedGraph_ConcreteStack();
		List<StackSymbol> inputMaterializationPostfix = abstractMaterialization();

		stackMaterializer.getMaterializedCloneWith( inputGraph,  someAbstractSymbol(), inputMaterializationPostfix);

		assertEquals("input graph has changed", unmaterializedGraph_ConcreteStack(), inputGraph);
	}

	@Test
	public void testOn_ConcreteStack_EmptyMaterialization() throws CannotMaterializeException{
		StackMaterializer stackMaterializer = new StackMaterializer();

		HeapConfiguration inputGraph = unmaterializedGraph_ConcreteStack();
		List<StackSymbol> inputMaterializationPostfix = emptyMaterialization();
		HeapConfiguration expected = materializedGraph_ConcreteStack_emptyMaterialization();
		HeapConfiguration actual = 
				stackMaterializer.getMaterializedCloneWith( inputGraph,  null, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", unmaterializedGraph_ConcreteStack(), inputGraph);
	}

	@Test
	public void testOn_GraphWithTwoNonterminals_canMaterialize() throws CannotMaterializeException{
		StackMaterializer stackMaterializer = new StackMaterializer();

		AbstractStackSymbol symbolToMaterialize = otherAbstractSymbol();
		HeapConfiguration inputGraph = unmaterializedGraph_TwoNonterminals_canMaterialize( symbolToMaterialize );
		List<StackSymbol> inputMaterializationPostfix = abstractMaterialization();
		HeapConfiguration expected = materializedGraph_TwoNonterminals_abstractMaterialization();
		HeapConfiguration actual = 
				stackMaterializer.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", 
						unmaterializedGraph_TwoNonterminals_canMaterialize( symbolToMaterialize ), 
						inputGraph);
	}
	
	@Test
	public void testOn_GraphWithDefaultNonterminal() throws CannotMaterializeException{
		StackMaterializer stackMaterializer = new StackMaterializer();

		AbstractStackSymbol symbolToMaterialize = someAbstractSymbol();
		HeapConfiguration inputGraph = unmaterializedGraph_TwoNonterminals_oneDefault( symbolToMaterialize );
		List<StackSymbol> inputMaterializationPostfix = abstractMaterialization();
		HeapConfiguration expected = 
				materializedGraph_TwoNonterminals_oneDefault_abstractMaterialization();
		HeapConfiguration actual = 
				stackMaterializer.getMaterializedCloneWith( inputGraph,  symbolToMaterialize, inputMaterializationPostfix);

		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", 
					  unmaterializedGraph_TwoNonterminals_oneDefault( symbolToMaterialize), inputGraph);
	}
	
	@Test
	public void testOn_GraphWithTwoNonterminals_differentAbstractSymbols() throws CannotMaterializeException {
		StackMaterializer stackMaterializer = new StackMaterializer();
		
		AbstractStackSymbol symbolToMaterialize  = someAbstractSymbol();
		HeapConfiguration inputGraph = 
				inputTwoNonterminalsDifferentAbstractSymbols( symbolToMaterialize );
		List<StackSymbol> inputMaterializationPostFix = concreteMaterialization();
		HeapConfiguration expected = 
				expectedTwoNonterminalsDifferentAbstractSymbols( inputMaterializationPostFix );
		
		HeapConfiguration actual =
				stackMaterializer.getMaterializedCloneWith( inputGraph, 
														    symbolToMaterialize,
														    inputMaterializationPostFix );
		
		assertEquals("materialization not as expected",expected, actual);
		assertEquals("input graph has changed", 
				inputTwoNonterminalsDifferentAbstractSymbols( symbolToMaterialize ), inputGraph);
		
	}

	private AbstractStackSymbol someAbstractSymbol() {
		return AbstractStackSymbol.get("X");
	}

	private AbstractStackSymbol otherAbstractSymbol() {
		return AbstractStackSymbol.get("Y");
	}
	
	private List<StackSymbol> emptyMaterialization() {
		return new ArrayList<>();
	}

	private List<StackSymbol> abstractMaterialization() {
		StackSymbol someConcreteStackSymbol = ConcreteStackSymbol.getStackSymbol("b", false);
		StackSymbol someAbstractStackSymbol = AbstractStackSymbol.get("Y");

		List<StackSymbol> abstractMaterialization = new ArrayList<>();
		abstractMaterialization.add(someConcreteStackSymbol);
		abstractMaterialization.add(someAbstractStackSymbol);
		abstractMaterialization.add(someAbstractStackSymbol);
		return abstractMaterialization;
	}

	private List<StackSymbol> concreteMaterialization() {
		StackSymbol someConcreteStackSymbol = ConcreteStackSymbol.getStackSymbol("b", false);
		StackSymbol someBottomStackSymbol = ConcreteStackSymbol.getStackSymbol("Z", true );

		List<StackSymbol> abstractMaterialization = new ArrayList<>();
		abstractMaterialization.add(someConcreteStackSymbol);
		abstractMaterialization.add(someBottomStackSymbol);
		return abstractMaterialization;
	}
	
	private List<StackSymbol> emptyStack() {
		return new ArrayList<>();
	}
	
	private List<StackSymbol> nonEmptyStack(){
		StackSymbol someStackSymbol = ConcreteStackSymbol.getStackSymbol("c", false);
		StackSymbol otherStackSymbol = ConcreteStackSymbol.getStackSymbol("b", false);

		List<StackSymbol> nonEmptyStack = new ArrayList<>();
		nonEmptyStack.add(someStackSymbol);
		nonEmptyStack.add(otherStackSymbol);
		nonEmptyStack.add(someStackSymbol);

		return nonEmptyStack;
	}
	
	private List<StackSymbol> concreteStack() {
		StackSymbol someStackSymbol = ConcreteStackSymbol.getStackSymbol("s", false);
		StackSymbol someBottomSymbol = ConcreteStackSymbol.getStackSymbol("Z", true);

		List<StackSymbol> concreteStack = new ArrayList<>();
		concreteStack.add(someStackSymbol);
		concreteStack.add(someBottomSymbol);

		return concreteStack;
	}


	private HeapConfiguration unmaterializedGraphWithEmptyStack(AbstractStackSymbol symbolToMaterialize) {
		List<StackSymbol> materializableEmptyStack = emptyStack();
		materializableEmptyStack.add( symbolToMaterialize );
		return graphWithOneNonterminalAndStack( materializableEmptyStack );
	}

	private HeapConfiguration materializedGraph_EmptyStack_EmptyMaterialization(AbstractStackSymbol symbolToMaterialize) {
		return unmaterializedGraphWithEmptyStack( symbolToMaterialize );
	}

	private HeapConfiguration materializedGraph_EmptyStack_AbstractMaterialization() {
		List<StackSymbol> materializedEmptyStack = emptyStack();
		materializedEmptyStack.addAll( abstractMaterialization() );
		return graphWithOneNonterminalAndStack( materializedEmptyStack );
	}

	private HeapConfiguration materializedGraph_EmptyStack_concreteMaterialization() {
		List<StackSymbol> materializedEmptyStack = emptyStack();
		materializedEmptyStack.addAll( concreteMaterialization() );
		return graphWithOneNonterminalAndStack( materializedEmptyStack );
	}



	private HeapConfiguration unmaterializedGraph_NonEmptyStack(AbstractStackSymbol abstractStackSymbol) {
		List<StackSymbol> materializableNonEmptyStack = nonEmptyStack();
		materializableNonEmptyStack.add( abstractStackSymbol );
		return graphWithOneNonterminalAndStack( materializableNonEmptyStack );
	}

	private HeapConfiguration materializedGraph_NonEmptyStack_concreteMaterialization() {
		List<StackSymbol> materializedNonEmptyStack = nonEmptyStack();
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
			AbstractStackSymbol abstractStackSymbol) {
		
		List<StackSymbol> stack1 = emptyStack();
		stack1.add(abstractStackSymbol);
		List<StackSymbol> stack2 = nonEmptyStack();
		stack2.add(abstractStackSymbol);

		return graphWithTwoNonterminalsWithStacks( stack1, stack2 );
	}
	

	private HeapConfiguration materializedGraph_TwoNonterminals_abstractMaterialization() {
		List<StackSymbol> materializedStack1 = emptyStack();
		materializedStack1.addAll( abstractMaterialization() );
		List<StackSymbol> materializedStack2 = nonEmptyStack();
		materializedStack2.addAll( abstractMaterialization() );
		
		return graphWithTwoNonterminalsWithStacks(materializedStack1, materializedStack2);
	}

	private HeapConfiguration unmaterializedGraph_TwoNonterminals_oneDefault(
																AbstractStackSymbol abstractStackSymbol) {
		
		List<StackSymbol> stackForIndexedNonterminal = emptyStack();
		stackForIndexedNonterminal.add(abstractStackSymbol);
		
		return graphWithDefaultNonterminalAndIndexedWithStack( stackForIndexedNonterminal );
	}


	private HeapConfiguration materializedGraph_TwoNonterminals_oneDefault_abstractMaterialization() {
		List<StackSymbol> materializedStackForIndexedNonterminal = emptyStack();
		materializedStackForIndexedNonterminal.addAll( abstractMaterialization() );
		
		return graphWithDefaultNonterminalAndIndexedWithStack(materializedStackForIndexedNonterminal);
	}
	
	private HeapConfiguration inputTwoNonterminalsDifferentAbstractSymbols(AbstractStackSymbol symbolToMaterialize) {
		List<StackSymbol> stackToMaterialize = emptyStack();
		stackToMaterialize.add(symbolToMaterialize);
		List<StackSymbol> stackNotToMaterialize = emptyStack();
		stackNotToMaterialize.add( otherAbstractSymbol() );
		
		return graphWithTwoNonterminalsWithStacks( stackToMaterialize, stackNotToMaterialize );
	}
	
	private HeapConfiguration expectedTwoNonterminalsDifferentAbstractSymbols(
			List<StackSymbol> inputMaterializationPostFix) {
		
		List<StackSymbol> stackToMaterialize = emptyStack();
		stackToMaterialize.addAll( inputMaterializationPostFix );
		List<StackSymbol> stackNotToMaterialize = emptyStack();
		stackNotToMaterialize.add( otherAbstractSymbol() );
		
		return graphWithTwoNonterminalsWithStacks( stackToMaterialize, stackNotToMaterialize );
	}


	private HeapConfiguration graphWithOneNonterminalAndStack(List<StackSymbol> stack) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = TypeFactory.getInstance().getType("type");

		Nonterminal nt = new IndexedNonterminal( UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES, stack);

		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
				.addNonterminalEdge(nt)
				.addTentacle(0)
				.addTentacle(1)
				.addTentacle(1)
				.build()
				.build();
	}

	private HeapConfiguration graphWithTwoNonterminalsWithStacks( List<StackSymbol> stack1, 
			List<StackSymbol> stack2 ){
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = TypeFactory.getInstance().getType("type");

		Nonterminal nt1 = new IndexedNonterminal( UNIQUE_NT_LABEL, RANK, REDUCTION_TENTACLES, stack1);
		Nonterminal nt2 = new IndexedNonterminal(UNIQUE_NT_LABEL, stack2);

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
			List<StackSymbol> stackForIndexedNonterminal) {
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = TypeFactory.getInstance().getType("type");

		Nonterminal nt = new IndexedNonterminal( UNIQUE_NT_LABEL, RANK, 
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
