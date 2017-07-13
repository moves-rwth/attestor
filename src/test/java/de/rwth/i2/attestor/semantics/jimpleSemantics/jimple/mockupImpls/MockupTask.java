package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;
import de.rwth.i2.attestor.tasks.GeneralAnalysisTask;

public class MockupTask extends GeneralAnalysisTask {

	public MockupTask(StateSpaceGenerator stateSpaceGenerator) {
		super(stateSpaceGenerator);
	}

	@Override
	public void exportAllStates() {
		// not needed
	}

	@Override
	public void exportTerminalStates() {
		// not needed
	}
}
