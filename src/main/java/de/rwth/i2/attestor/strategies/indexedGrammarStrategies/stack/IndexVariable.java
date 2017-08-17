package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack;

import java.util.ArrayList;
import java.util.List;

public class IndexVariable implements IndexSymbol {

	private static final IndexVariable instance = new IndexVariable();
	
	public static IndexVariable getGlobalInstance(){
		return instance;
	}
	
	private final List<IndexSymbol> instantiation = new ArrayList<>();
	
	@Override
	public boolean isBottom() {
		return false;
	}
	
	public void prolongInstantiation( IndexSymbol instantiation ){
		this.instantiation.add(instantiation);
	}
	
	public boolean matchInstantiation( List<IndexSymbol> matchAgainst ){
		if( matchAgainst.size() < instantiation.size() ){
			return false;
		}
		for( int i = 0; i < instantiation.size(); i++ ){
			if( ! matchAgainst.get( i ).equals( instantiation.get( i ) ) ){
				return false;
			}
		}
		for( int j = instantiation.size(); j < matchAgainst.size(); j++  ){
			prolongInstantiation( matchAgainst.get( j ) );
		}
		return true;
	}
	
	public Iterable<IndexSymbol> getInstantiation(){
		return this.instantiation;
	}
	
	public void resetInstantiation(){
		this.instantiation.clear();
	}
	
	public String toString(){
		
		return "()";
		
		/* toString() is used in grammar to compare stacks. Thus, instantiations should not be visible.
		StringBuilder res = new StringBuilder("(");
		instantiation.forEach( symb -> res.append(symb));
		res.append(")");
		return res.toString();
		*/
	}
	
	public boolean equals( Object other ){
		return other == this;
	}
	
	public int hashCode(){
		return 0;
	}
	
}
