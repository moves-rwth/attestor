package de.rwth.i2.attestor.modelChecking;

import de.rwth.i2.attestor.counterexamples.CounterexampleTrace;

import java.util.List;

public interface ModelCheckingTrace extends CounterexampleTrace {

    List<Integer> getStateIdTrace();
}
