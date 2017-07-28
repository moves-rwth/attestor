package de.rwth.i2.attestor.indexedGrammars;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.digraph.NodeLabel;
import de.rwth.i2.attestor.indexedGrammars.stack.*;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;

public class IndexedNonterminal implements Nonterminal{

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger( "IndexedNonterminal" );

	private final List<StackSymbol>  stack;
	private final GeneralNonterminal generalNonterminal;

	public IndexedNonterminal( String label, 
			int rank, 
			boolean[] isReductionTentacle, 
			List<StackSymbol> stack){

		this.generalNonterminal = GeneralNonterminal.getNonterminal( label, rank, isReductionTentacle );
		this.stack = stack;
	}


	public IndexedNonterminal( String label, List<StackSymbol> index ){
		this.generalNonterminal = GeneralNonterminal.getNonterminal(label);
		this.stack = index;
	}

	private IndexedNonterminal(GeneralNonterminal generalNonterminal, List<StackSymbol> index ){
		this.generalNonterminal = generalNonterminal;
		this.stack = index;
	}

	public Iterator<StackSymbol> getStackIterator(){
		return this.stack.iterator();
	}

	public GeneralNonterminal label(){
		return this.generalNonterminal;
	}

	//might be useless
	public boolean stackStartsWith( Iterable<StackSymbol> prefix ){

		Iterator<StackSymbol> stackIterator = stack.iterator();
		Iterator<StackSymbol> prefixIterator = prefix.iterator();

		while( stackIterator.hasNext() && prefixIterator.hasNext() ){
			if( ! stackIterator.next().equals( prefixIterator.next() ) ){
				return false;
			}
		}

		return !prefixIterator.hasNext();
	}

	public boolean stackEndsWith( StackSymbol symbol ){
		return (! stack.isEmpty() ) && stack.get( stack.size() -1 ).equals(symbol);
	}

	public StackSymbol getLastStackSymbol(){
		assert( stack.size() > 0 );
		return stack.get( stack.size() -1 );
	}

	public IndexedNonterminal getWithShortenedStack(){
		assert( stack.size() > 0 );
		ArrayList<StackSymbol> stackCopy = new ArrayList<>(stack);
		stackCopy.remove(stackCopy.size() -1 );
		return new IndexedNonterminal(generalNonterminal, stackCopy);
	}

	public IndexedNonterminal getWithProlongedStack( StackSymbol s ){
		List<StackSymbol> stackCopy = new ArrayList<>(stack);
		stackCopy.add(s);
		return new IndexedNonterminal(generalNonterminal, stackCopy);
	}



	public IndexedNonterminal getWithInstantiation(){
		List<StackSymbol> stackCopy = new ArrayList<>(stack);
		if( this.stackSize() > 0 && this.getLastStackSymbol() instanceof StackVariable ){
			StackVariable lastSymbol = (StackVariable)stackCopy.get(stackCopy.size() - 1);
			stackCopy.remove( stackCopy.size() - 1 );
			lastSymbol.getInstantiation().forEach(stackCopy::add);
		}
		return new IndexedNonterminal(generalNonterminal, stackCopy);
	}

	/**
	 * removes the last symbol (stackVariable () or abstractStackSymbol) and
	 * adds all elements in postfix
	 * @param postfix a list of stack symbols representing the postfix
	 * @return a new IndexedNonterminal with the prolonged stack
	 */
	public IndexedNonterminal getWithProlongedStack( List<StackSymbol> postfix ){
		assert( this.stackSize() > 0 );
		StackSymbol lastSymbol = this.getLastStackSymbol();
		assert( !( lastSymbol instanceof ConcreteStackSymbol ) );

		List<StackSymbol> stackCopy = new ArrayList<>(stack);
		stackCopy.remove( stackCopy.size() - 1 );
		stackCopy.addAll(postfix) ;

		return new IndexedNonterminal(generalNonterminal, stackCopy);
	}


	public boolean hasConcreteStack(){
		return (! stack.isEmpty() ) && this.stack.get( stack.size() -1 ).isBottom();
	}

	public int stackSize(){
		return stack.size();
	}

	@Override
	public int compareTo(Nonterminal o) {
		if( this.equals(o) ){
			return 0;
		}
		return -1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((generalNonterminal == null) ? 0 : generalNonterminal.hashCode());
		for( StackSymbol symb : stack ){
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

	/**
	 * returns true if the stacks are elementwise equal. 
	 * @param other the nonterminal with which to compare
	 * @return true, if the stacks of the two nonterminals are elementwise equal
	 */
	public boolean matchStack( IndexedNonterminal other ){
		List<StackSymbol> otherStack = other.stack;
		for( int i = 0; i < this.stackSize() && i < otherStack.size(); i++ ){
			StackSymbol s1 = this.getStackAt( i );
			StackSymbol s2 = otherStack.get( i );
			if( s1 instanceof StackVariable ){
				return ( (StackVariable) s1 ).matchInstantiation( otherStack.subList( i, otherStack.size() ) );
			}else if( s2 instanceof StackVariable ){
				return ( (StackVariable) s2 ).matchInstantiation( this.stack.subList( i, stack.size() ) );
			}
			if( ! s1.equals( s2 )){
				return false;
			}
		}

		return otherStack.size() == this.stackSize();
	}

	public StackSymbol getStackAt( int pos ){
		return stack.get( pos );
	}


	@Override
	public int getRank() {
		return generalNonterminal.getRank();
	}

	@Override
	public boolean isReductionTentacle(int tentacle) {
		return generalNonterminal.isReductionTentacle(tentacle);
	}

	public void setReductionTentacle( int tentacle ){
		generalNonterminal.setReductionTentacle(tentacle);
	}

	public void unsetReductionTentacle( int tentacle ){
		generalNonterminal.unsetReductionTentacle(tentacle);
	}


	public String toString(){
		return generalNonterminal.toString() + this.stack.toString();
	}

	public IndexedNonterminal clone(){
		List<StackSymbol> stackCopy = new ArrayList<>(stack);
		return new IndexedNonterminal(generalNonterminal, stackCopy);
	}


	@Override
	public boolean labelMatches(Nonterminal nonterminal) {
		if( !( nonterminal instanceof IndexedNonterminal )){
			return false;
		}else{
			IndexedNonterminal indexedNonterminal = (IndexedNonterminal) nonterminal;
			return this.label().equals(indexedNonterminal.label() );
		}
	}


}
