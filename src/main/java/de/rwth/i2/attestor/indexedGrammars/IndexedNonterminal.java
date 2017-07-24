package de.rwth.i2.attestor.indexedGrammars;

import java.util.*;

import de.rwth.i2.attestor.indexedGrammars.stack.Stack;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.indexedGrammars.stack.*;

public class IndexedNonterminal implements Nonterminal{

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger( "IndexedNonterminal" );

	private Stack stack;
	private final GeneralNonterminal generalNonterminal;

	public IndexedNonterminal( String label, 
			int rank, 
			boolean[] isReductionTentacle, 
			List<StackSymbol> stack){

		this.generalNonterminal = GeneralNonterminal.getNonterminal( label, rank, isReductionTentacle );
		this.stack = new Stack(stack);
	}


	public IndexedNonterminal( String label, List<StackSymbol> index ){
		this.generalNonterminal = GeneralNonterminal.getNonterminal(label);
		this.stack = new Stack(index);
	}

	private IndexedNonterminal(GeneralNonterminal generalNonterminal, List<StackSymbol> index ){
		this.generalNonterminal = generalNonterminal;
        this.stack = new Stack(index);
	}

	private IndexedNonterminal(GeneralNonterminal generalNonterminal, Stack stack) {
	    this.generalNonterminal = generalNonterminal;
	    this.stack = stack;
    }

    public Stack getStack() {
	    return stack;
    }

    public IndexedNonterminal getWithShortenedStack(){
	    return new IndexedNonterminal(generalNonterminal, stack.getWithShortenedStack());
	}

	public IndexedNonterminal getWithProlongedStack( StackSymbol s ){
		return new IndexedNonterminal(generalNonterminal, stack.getWithProlongedStack(s));
	}

	public IndexedNonterminal getWithInstantiation(){
        return new IndexedNonterminal(generalNonterminal, stack.getWithInstantiation());
	}

	/**
	 * removes the last symbol (stackVariable () or abstractStackSymbol) and
	 * adds all elements in postfix
	 * @param postfix The postfix to prolong the stack
	 * @return The nonterminal with prolonged stack
	 */
	public IndexedNonterminal getWithProlongedStack( List<StackSymbol> postfix ){
        return new IndexedNonterminal(generalNonterminal, stack.getWithProlongedStack(postfix));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((generalNonterminal == null) ? 0 : generalNonterminal.hashCode());
		for(int i=0; i < stack.size(); i++) {
		    StackSymbol symb = stack.get(i);
			result = prime * symb.hashCode();
		}
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexedNonterminal other = (IndexedNonterminal) obj;
		if (generalNonterminal == null) {
			if (other.generalNonterminal != null)
				return false;
		} else if (!generalNonterminal.equals(other.generalNonterminal))
			return false;
		if (stack == null) {
			if (other.stack != null)
				return false;
		} else if (!this.stack.equals( other.stack ) )
			return false;
		return true;
	}

	@Override
	public boolean matches( NodeLabel obj ){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexedNonterminal other = (IndexedNonterminal) obj;
		if (generalNonterminal == null) {
			if (other.generalNonterminal != null)
				return false;
		} else if (!generalNonterminal.equals(other.generalNonterminal))
			return false;
		return true;
	}

	@Override
	public int getRank() {
		return generalNonterminal.getRank();
	}

	@Override
	public boolean isReductionTentacle(int tentacle) {
		return generalNonterminal.isReductionTentacle(tentacle);
	}

	@Override
	public void setReductionTentacle( int tentacle ){
		generalNonterminal.setReductionTentacle(tentacle);
	}

	@Override
	public void unsetReductionTentacle( int tentacle ){
		generalNonterminal.unsetReductionTentacle(tentacle);
	}

	@Override
	public String toString(){
		return generalNonterminal.toString() + this.stack.toString();
	}

	@Override
	public String getLabel() {
		return generalNonterminal.getLabel();
	}
}
