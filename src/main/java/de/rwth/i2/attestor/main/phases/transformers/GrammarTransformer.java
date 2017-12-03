package de.rwth.i2.attestor.main.phases.transformers;

import de.rwth.i2.attestor.grammar.Grammar;

import java.util.Map;

public interface GrammarTransformer {

    Grammar getGrammar();

    Map<String, String> getRenamingMap();
}
