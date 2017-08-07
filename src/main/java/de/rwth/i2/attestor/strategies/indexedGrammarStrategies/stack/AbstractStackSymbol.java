package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack;

import java.util.HashMap;
import java.util.Map;

public class AbstractStackSymbol implements StackSymbol {

	private static final Map<String, AbstractStackSymbol> existingSymbols = new HashMap<>();

	public static synchronized AbstractStackSymbol get( String label ){
		if( ! existingSymbols.containsKey(label) ){
			existingSymbols.put(label, new AbstractStackSymbol(label));
		}
		return existingSymbols.get(label);
	}

	private final String label;

	private AbstractStackSymbol(String label) {
		super();
		this.label = label;
	}

	@Override
	public boolean isBottom() {
		return false;
	}

	public boolean equals( Object other ){

		return this == other;

	}

	public int hashCode(){
		return label.hashCode();
	}

	public String toString(){
		return this.label;
	}
}
