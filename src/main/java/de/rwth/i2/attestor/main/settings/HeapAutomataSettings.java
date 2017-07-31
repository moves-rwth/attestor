package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.automata.HeapAutomaton;

public class HeapAutomataSettings {

    private HeapAutomaton refinementAutomaton;
    private HeapAutomaton stateLabelingAutomaton;

    public HeapAutomaton getRefinementAutomaton() {

        return refinementAutomaton;
    }

    public HeapAutomaton getStateLabelingAutomaton() {

        return stateLabelingAutomaton;
    }

    public void setRefinementAutomaton(HeapAutomaton automaton) {

        this.refinementAutomaton = automaton;
    }

    public void setStateLabelingAutomaton(HeapAutomaton automaton) {

        this.stateLabelingAutomaton = automaton;
    }
}
