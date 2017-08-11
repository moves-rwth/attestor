package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index;

import java.util.HashMap;
import java.util.Map;

public class ConcreteIndexSymbol implements IndexSymbol {
	//private static final Logger logger = LogManager.getLogger( "IndexSymbol" );

	private static final Map<String, ConcreteIndexSymbol> existingStackSymbols = new HashMap<>();
	
	public static ConcreteIndexSymbol getStackSymbol(String label, boolean isBottom ){
		if(! existingStackSymbols.containsKey(label) ){
			existingStackSymbols.put(label, new ConcreteIndexSymbol(label, isBottom));
		}
		
		return existingStackSymbols.get(label);
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
	 * @see de.rwth.i2.attestor.strategies.indexedGrammarStrategies.StackSymbol2#toString()
	 */
	@Override
	public String toString(){
		return this.label;
	}
}
