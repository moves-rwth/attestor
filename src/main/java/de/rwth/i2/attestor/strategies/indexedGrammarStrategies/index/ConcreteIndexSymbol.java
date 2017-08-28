package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index;

import java.util.HashMap;
import java.util.Map;

public class ConcreteIndexSymbol implements IndexSymbol {
	//private static final Logger logger = LogManager.getLogger( "IndexSymbol" );

	private static final Map<String, ConcreteIndexSymbol> existingIndexSymbols = new HashMap<>();
	
	public static ConcreteIndexSymbol getIndexSymbol(String label, boolean isBottom ){
		if(! existingIndexSymbols.containsKey(label) ){
			existingIndexSymbols.put(label, new ConcreteIndexSymbol(label, isBottom));
		}
		
		return existingIndexSymbols.get(label);
	}
	
	private final String label;
	private final boolean isBottom;
	
	private ConcreteIndexSymbol(String label, boolean isBottom) {
		this.label = label;
		this.isBottom = isBottom;
	}
	
	public boolean isBottom(){
		return this.isBottom;
	}
	
	/* (non-Javadoc)
	 * @see de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexSymbol2#toString()
	 */
	@Override
	public String toString(){
		return this.label;
	}
}
