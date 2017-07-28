package de.rwth.i2.attestor.tasks.defaultTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.StateLabel;

public class DefaultLabelledState extends DefaultState {
	
	private static final Logger logger = LogManager.getLogger( "DefaultLabelledState" );
	
	public DefaultLabelledState( HeapConfiguration heap ) {
		super(heap);
	}
	
	public DefaultLabelledState( HeapConfiguration heap, int scopeDepth ) {
		
		super( heap, scopeDepth);
	}
	
	public DefaultLabelledState( DefaultLabelledState state ) {
		
		super( state );
	}
	
	/**
	 * The provided label is added to the the program state.
	 * 
	 * @param label, the state label that should be added to the program state
	 */
	public void addLabel(StateLabel label){
		if(this.label == null){
			this.label = label;
		} else {
			DefaultLabelledState.logger.warn("Trying to add a label to a state that already carries one. Ignoring statement!");
		}
	}
	
	public boolean satisfiesAP(String ap) {
		return this.label.contains(ap);
	}

}
