package de.rwth.i2.attestor.main.scene;

import java.util.LinkedHashSet;
import java.util.Set;

public class Labels {

    private final Set<String> keptVariables = new LinkedHashSet<>();
    private final Set<String> usedSelectorLabels = new LinkedHashSet<>();
    private final Set<String> grammarSelectorLabels = new LinkedHashSet<>();


    public void addKeptVariable(String variableName) {

        keptVariables.add(variableName);
    }

    public boolean isKeptVariableName(String variableName) {

        return keptVariables.contains(variableName);
    }

    public void addGrammarSelectorLabel(String selector) {

        grammarSelectorLabels.add(selector);
    }

    public Set<String> getGrammarSelectorLabels() {

        return grammarSelectorLabels;
    }

    public void addUsedSelectorLabel(String selector) {

        usedSelectorLabels.add(selector);
    }

    public Set<String> getUsedSelectorLabels() {

        return usedSelectorLabels;
    }
}
