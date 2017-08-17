package de.rwth.i2.attestor.grammar.testUtil;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.index.*;
import de.rwth.i2.attestor.util.Pair;

/**
 * matches if and only if instantiableNonterminal is actualy intantiable (i.e. ends in stackVariable).
 * In this case, always returns materialization: MATERIALIZATION and instantiation INSTANTIATION
 * both globally accessible.
 * @author Hannah
 *
 */
public class FakeIndexMatcher extends IndexMatcher {

	public static final List<IndexSymbol> MATERIALIZATION = createMaterialization();
	public static final List<IndexSymbol> INSTANTIATION = createInstantiation();

	public FakeIndexMatcher() {
		super(null);
	}

	private static List<IndexSymbol> createInstantiation() {
		IndexSymbol a = ConcreteIndexSymbol.getStackSymbol("a", false);
		IndexSymbol abs = AbstractIndexSymbol.get("Y");
		
		List<IndexSymbol> res = new ArrayList<>();
		res.add(a);
		res.add(abs);
		return res;
	}

	private static List<IndexSymbol> createMaterialization() {
		IndexSymbol s = ConcreteIndexSymbol.getStackSymbol("a", false);
		IndexSymbol abs = AbstractIndexSymbol.get("Y");
		
		List<IndexSymbol> res = new ArrayList<>();
		res.add(s);
		res.add(abs);
		return res;
	}

	public boolean canMatch(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal ){
		
		return ! instantiableNonterminal.getIndex().hasConcreteStack();
	}
	
	public boolean needsMaterialization(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		
		return canMatch(materializableNonterminal, instantiableNonterminal);
	}
	
    
	public Pair<AbstractIndexSymbol, List<IndexSymbol> > getMaterializationRule(IndexedNonterminal materializableNonterminal,
																				IndexedNonterminal instantiableNonterminal )  {
		
		if( needsMaterialization( materializableNonterminal, instantiableNonterminal ) ) {
			AbstractIndexSymbol lhs = (AbstractIndexSymbol) materializableNonterminal.getIndex().getLastStackSymbol();
			return new Pair<>( lhs, getNecessaryMaterialization(materializableNonterminal, instantiableNonterminal) );
		}else {
			return new Pair<>( null, new ArrayList<>() );
		}
	}
	
	private List<IndexSymbol> getNecessaryMaterialization(IndexedNonterminal materializableNonterminal,
														  IndexedNonterminal instantiableNonterminal )  {
		if (canMatch(materializableNonterminal, instantiableNonterminal))
			return MATERIALIZATION;
		else
			return null;
	}
	
	public boolean needsInstantiation(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		
		return canMatch(materializableNonterminal, instantiableNonterminal);
	}
	
	public List<IndexSymbol> getNecessaryInstantiation(IndexedNonterminal materializableNonterminal,
													   IndexedNonterminal instantiableNonterminal ) {
		if (canMatch(materializableNonterminal, instantiableNonterminal))
			return INSTANTIATION;
		else
			return null;
	}
}
