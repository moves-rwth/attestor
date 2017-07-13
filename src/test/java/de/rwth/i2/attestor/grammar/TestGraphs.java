package de.rwth.i2.attestor.grammar;

import de.rwth.i2.attestor.grammar.materialization.GeneralMaterializationStrategyTest_getActualViolationPoint;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;

class TestGraphs {
	
	public static HeapConfiguration getInput_getActualViolationPoints_Default(){
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = TypeFactory.getInstance().getType("type");
		GeneralSelectorLabel sel = GeneralSelectorLabel.getSelectorLabel(
				GeneralMaterializationStrategyTest_getActualViolationPoint.DEFAULT_SELECTOR);
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
			.addVariableEdge(GeneralMaterializationStrategyTest_getActualViolationPoint.DEFAULT_VARIABLE, nodes.get(0) )
			.addSelector(nodes.get(0), sel, nodes.get(1) )
			.build();
	}
	
	public static HeapConfiguration getInput_getActualViolationPoints_Indexed(){
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = TypeFactory.getInstance().getType("type");
		AnnotatedSelectorLabel annotatedSel = new AnnotatedSelectorLabel(
				GeneralMaterializationStrategyTest_getActualViolationPoint.ANNOTATED_SELECTOR, 
				GeneralMaterializationStrategyTest_getActualViolationPoint.ANNOTATION );
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
			.addVariableEdge(GeneralMaterializationStrategyTest_getActualViolationPoint.ANNOTATED_VARIABLE, nodes.get(0))
			.addSelector(nodes.get(0), annotatedSel, nodes.get(1) )
			.build();
	}

	public static HeapConfiguration getInput_getActualViolationPoints_Mixed(){
		HeapConfiguration hc = new InternalHeapConfiguration();

		Type type = TypeFactory.getInstance().getType("type");
		AnnotatedSelectorLabel annotatedSel = new AnnotatedSelectorLabel(
				GeneralMaterializationStrategyTest_getActualViolationPoint.ANNOTATED_SELECTOR, 
				GeneralMaterializationStrategyTest_getActualViolationPoint.ANNOTATION );
		GeneralSelectorLabel defaultSel = GeneralSelectorLabel.getSelectorLabel(
				GeneralMaterializationStrategyTest_getActualViolationPoint.DEFAULT_SELECTOR );
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder().addNodes(type, 2, nodes)
			.addVariableEdge(
					GeneralMaterializationStrategyTest_getActualViolationPoint.ANNOTATED_VARIABLE, 
					GeneralMaterializationStrategyTest_getActualViolationPoint.NODE_FOR_ANNOTATED_VARIABLE
					)	
			.addSelector(
					GeneralMaterializationStrategyTest_getActualViolationPoint.NODE_FOR_ANNOTATED_VARIABLE,
					annotatedSel,
					nodes.get(1)
					)
			.addVariableEdge(
					GeneralMaterializationStrategyTest_getActualViolationPoint.DEFAULT_VARIABLE,
					GeneralMaterializationStrategyTest_getActualViolationPoint.NODE_FOR_DEFAULT_VARIABLE)
			.addSelector(GeneralMaterializationStrategyTest_getActualViolationPoint.NODE_FOR_DEFAULT_VARIABLE, 
					defaultSel,
					nodes.get(0)
					)
			.build();
	}

}
