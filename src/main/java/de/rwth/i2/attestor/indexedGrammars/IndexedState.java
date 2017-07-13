package de.rwth.i2.attestor.indexedGrammars;

import de.rwth.i2.attestor.tasks.GeneralProgramState;
import de.rwth.i2.attestor.tasks.GeneralConcreteValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.DebugMode;

public class IndexedState extends GeneralProgramState {
	private static final Logger logger = LogManager.getLogger( "IndexedState" );
	private static final String[] CONSTANT_NAMES = {"true", "false", "null", "1", "0", "-1"};
	
	public IndexedState( HeapConfiguration heap ) {
		super( heap);
	}
	
	public IndexedState( HeapConfiguration heap, int scopeDepth ) {
		
		super( heap, scopeDepth);
	}
	
	private IndexedState(IndexedState state) {
		
		super( state );
	}
	

	@Override
	public ProgramState shallowCopy() {
		
		return new IndexedState(this);
	}



	public int hashCode() {
		if( getHeap() != null ){
			return getHeap().hashCode();
		}else{
			return 0;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof IndexedState) {
			
			IndexedState state = (IndexedState) other;
			
			if(programCounter != state.programCounter
					|| scopeDepth != state.scopeDepth) {
				
				return false;
			}

			return heap.equals(state.getHeap());
		}
		
		return false;
	}

	@Override
	public GeneralConcreteValue getSelectorTarget(ConcreteValue from, String selectorName) {
			if(from instanceof GeneralConcreteValue) {
			
			GeneralConcreteValue dFrom = (GeneralConcreteValue) from;
			
			if(dFrom.isUndefined()) {
				logger.warn("getSelectorTarget: origin is undefined");
				return dFrom;
			}

			int node = dFrom.getNode();
			
			for( SelectorLabel label : getHeap().selectorLabelsOf(node) ){
				
				AnnotatedSelectorLabel sel = (AnnotatedSelectorLabel) label;
				
				if( sel.hasLabel(selectorName) ){
					int target = getHeap().selectorTargetOf(node, sel);
					Type type = getHeap().nodeTypeOf(target);
					return new GeneralConcreteValue( type, target );
					
				}
			}
			if(DebugMode.ENABLED) {
				logger.warn("getSelectorTarget: source node didnt have selector " + selectorName);
			}
			
		} else {
			
			if(DebugMode.ENABLED) {
				logger.warn("getSelectorTarget got invalid source");
			}
		}
		
		return GeneralConcreteValue.getUndefined();
	}

	@Override
	public void setSelector(ConcreteValue from, String selectorName, ConcreteValue to) {
		if(from.isUndefined() || to.isUndefined()) {
			
			logger.warn("Specified edge has invalid source or target.");
			return;
		}
		
		if(from instanceof GeneralConcreteValue && to instanceof GeneralConcreteValue) {

			GeneralConcreteValue dFrom = (GeneralConcreteValue) from;
			GeneralConcreteValue dTo = (GeneralConcreteValue) to;
			
			int fromNode = dFrom.getNode();
			
			for( SelectorLabel label : getHeap().selectorLabelsOf(fromNode)  ){
				
				AnnotatedSelectorLabel sel = (AnnotatedSelectorLabel) label;
				
				if( sel.hasLabel(selectorName)){
					this.getHeap()
						.builder()
						.removeSelector(fromNode, sel)
						.build();
				}
			}
			
			AnnotatedSelectorLabel newSel = new AnnotatedSelectorLabel(selectorName, "");
			
			this.getHeap()
			.builder()
			.addSelector(fromNode, newSel, dTo.getNode())
			.build();
		}
		
	}

	@Override
	public ProgramState shallowCopyUpdatePC(int newPC) {
		IndexedState result = new IndexedState(this);
		result.setProgramCounter(newPC);
		return result;
	}

	@Override
	public IndexedState clone() {
		HeapConfiguration newHeap = heap.clone();
		IndexedState result = new IndexedState(newHeap);
		result.setProgramCounter( programCounter );
		result.scopeDepth = scopeDepth;
		return result;
	}
	
	@Override
	protected String[] getConstants() {
		return CONSTANT_NAMES;
	}
	
	@Override
	public boolean satisfiesAP(String ap){
		return false;
	}
	
}
