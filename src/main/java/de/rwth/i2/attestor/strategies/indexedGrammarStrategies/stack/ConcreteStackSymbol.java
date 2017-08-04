package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack;

import java.util.HashMap;
import java.util.Map;

public class ConcreteStackSymbol implements StackSymbol {
	//private static final Logger logger = LogManager.getLogger( "StackSymbol" );

	private static final Map<String, ConcreteStackSymbol> existingStackSymbols = new HashMap<>();
	
	public static ConcreteStackSymbol getStackSymbol( String label, boolean isBottom ){
		if(! existingStackSymbols.containsKey(label) ){
			existingStackSymbols.put(label, new ConcreteStackSymbol(label, isBottom));
		}
		
		return existingStackSymbols.get(label);
	}
	
	private final String label;
	private final boolean isBottom;
	
	private ConcreteStackSymbol( String label, boolean isBottom) {
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
