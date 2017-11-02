package de.rwth.i2.attestor.ipa;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.*;

import org.junit.*;

import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.*;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

public class AbstractMethodIPATest_testComputeResult {

	private static final String thisLabel = "@this";
	private static final String parameterName = "@parameter0:";
	private static final Type listType = Settings.getInstance().factory().getType("List");
	
	IpaAbstractMethod ipa = new IpaAbstractMethod( "testMethod" );

	//sets a "next" pointer from "this" to "@parameter0:"
	@Before
	public void setupProgram(){
		List<Semantics> program = new ArrayList<>();
		
		Value baseValue = new Local(listType, thisLabel);
		SettableValue lhs = new Field( listType, baseValue, "next" );
		
		Value rhs = new Local(listType, parameterName);
		
		Statement assignStmt = new AssignStmt(lhs, rhs, 1, new HashSet<>(), false );
		Statement returnStmt = new ReturnVoidStmt();
		
		program.add(assignStmt);
		program.add(returnStmt);
		ipa.setControlFlow(program);
	}
	
	@Ignore
	@Test
	public void testGetResult() throws StateSpaceGenerationAbortedException {

		HeapConfiguration input = incompleteList();
		HeapConfiguration output = completedList();
		
		List<HeapConfiguration> result = ipa.getResult( input );
		assertThat( result, contains( output ) );
		
	}

	private HeapConfiguration completedList() {
HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		listHelper( hc, nodes );
		
		SelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel("next");
		
		return hc.builder()
				.addSelector(nodes.get(2), nextSel, nodes.get(0))
				.build();
	}

	private HeapConfiguration incompleteList() {
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		TIntArrayList nodes = new TIntArrayList();
		listHelper( hc, nodes );
		return hc.builder()
				.addVariableEdge(thisLabel, nodes.get(2))
				.addVariableEdge(parameterName, nodes.get(0))
				.build();
	}

	private void listHelper(HeapConfiguration hc, TIntArrayList nodes) {
		
		Type nullType = Settings.getInstance().factory().getType("NULL");
		
		SelectorLabel nextSel = BasicSelectorLabel.getSelectorLabel("next");
		
		hc.builder().addNodes(nullType, 1, nodes )
					.addNodes(listType, 2, nodes )
					.addVariableEdge("null", nodes.get(0))
					.addVariableEdge("x", nodes.get(1))
					.addSelector(nodes.get(1), nextSel , nodes.get(2))
					.build();
	}

}
