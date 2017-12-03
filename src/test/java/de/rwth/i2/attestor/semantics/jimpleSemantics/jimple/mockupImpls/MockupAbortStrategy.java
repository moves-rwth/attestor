package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;

public class MockupAbortStrategy implements AbortStrategy {
    //private static final Logger logger = LogManager.getLogger( "TestAbortStrategy" );

    @Override
    public void checkAbort(StateSpace stateSpace) throws StateSpaceGenerationAbortedException {

        if (stateSpace.getStates().size() >= 50) {
            throw new StateSpaceGenerationAbortedException();
        }
    }


}
