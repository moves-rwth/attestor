package de.rwth.i2.attestor.io.htmlExport;


import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultState;

class TestStateSpaceInput extends StateSpace {
	//private static final Logger logger = LogManager.getLogger( "TestStateSpaceInput.java" );
	
	
	public TestStateSpaceInput(){
		super();
		
		DefaultState s1 = new DefaultState( new InternalHeapConfiguration() );
		s1.setProgramCounter( 0 );
		DefaultState s2 = new DefaultState( new InternalHeapConfiguration() );
		s2.setProgramCounter( 1 );
		
		super.addState( s1 );
		super.addState( s2 );
		super.addControlFlowSuccessor( s1, "label", s2 );
	}
}
