package de.rwth.i2.attestor.strategies.indexedGrammarStrategies.stack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Stack {

   private List<IndexSymbol> stackSymbols;

   public Stack(List<IndexSymbol> stackSymbols) {
       this.stackSymbols = stackSymbols;
   }

   public Stack(Stack stack) {
       this.stackSymbols = stack.stackSymbols;
   }

   public boolean startsWith(Iterable<IndexSymbol> prefix) {

       Iterator<IndexSymbol> stackIterator = stackSymbols.iterator();
       Iterator<IndexSymbol> prefixIterator = prefix.iterator();

       while( stackIterator.hasNext() && prefixIterator.hasNext() ){
           if( ! stackIterator.next().equals( prefixIterator.next() ) ){
               return false;
           }
       }

       return !prefixIterator.hasNext();
   }


    public boolean stackEndsWith( IndexSymbol symbol ){
        return (! stackSymbols.isEmpty() ) && stackSymbols.get( stackSymbols.size() -1 ).equals(symbol);
    }


    public IndexSymbol getLastStackSymbol(){
        assert( stackSymbols.size() > 0 );
        return stackSymbols.get( stackSymbols.size() -1 );
    }

    public Stack getWithShortenedStack(){
        assert( stackSymbols.size() > 0 );
        List<IndexSymbol> stackCopy = new ArrayList<>(stackSymbols);
        stackCopy.remove(stackCopy.size() -1 );
        return new Stack(stackCopy);
    }

    public Stack getWithProlongedStack( IndexSymbol s ){
        List<IndexSymbol> stackCopy = new ArrayList<>(stackSymbols);
        stackCopy.add(s);
        return new Stack(stackCopy);
    }

    public Stack getWithInstantiation(){
        List<IndexSymbol> stackCopy = new ArrayList<>(stackSymbols);
        if( stackSymbols.size() > 0 && this.getLastStackSymbol() instanceof IndexVariable ){
            IndexVariable lastSymbol = (IndexVariable)stackCopy.get(stackCopy.size() - 1);
            stackCopy.remove( stackCopy.size() - 1 );
            lastSymbol.getInstantiation().forEach(stackCopy::add);
        }
        return new Stack(stackCopy);
    }

    public Stack getWithProlongedStack( List<IndexSymbol> postfix ){
        assert( this.size() > 0 );
        IndexSymbol lastSymbol = this.getLastStackSymbol();
        assert( !( lastSymbol instanceof ConcreteIndexSymbol ) );

        List<IndexSymbol> stackCopy = new ArrayList<>(stackSymbols);
        stackCopy.remove( stackCopy.size() - 1 );
        stackCopy.addAll(postfix) ;

        return new Stack(stackCopy);
    }

    public boolean hasConcreteStack(){
        return (! stackSymbols.isEmpty() ) && this.stackSymbols.get( stackSymbols.size() -1 ).isBottom();
    }

    public int size(){
        return stackSymbols.size();
    }

    public boolean isEmpty() {
        return stackSymbols.isEmpty();
    }

    public boolean matchStack( Stack other ){
        List<IndexSymbol> otherStack = other.stackSymbols;
        for( int i = 0; i < this.size() && i < otherStack.size(); i++ ){
            IndexSymbol s1 = this.get( i );
            IndexSymbol s2 = otherStack.get( i );
            if( s1 instanceof IndexVariable ){
                return ( (IndexVariable) s1 ).matchInstantiation( otherStack.subList( i, otherStack.size() ) );
            }else if( s2 instanceof IndexVariable ){
                return ( (IndexVariable) s2 ).matchInstantiation( this.stackSymbols.subList( i, stackSymbols.size() ) );
            }
            if( ! s1.equals( s2 )){
                return false;
            }
        }

        return otherStack.size() == this.size();
    }

    public IndexSymbol get( int pos ){
        return stackSymbols.get( pos );
    }

    public boolean equals(Object other) {
        if(other instanceof Stack) {
            Stack stack = (Stack) other;
            return stackSymbols.equals(stack.stackSymbols);
        }
        return false;
    }

    public int hashCode() {
        return stackSymbols.hashCode();
    }

    public String toString() {
        return stackSymbols.toString();
    }

}
