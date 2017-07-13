package de.rwth.i2.attestor.grammar.testUtil;

import java.util.ArrayList;
import java.util.List;

import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.indexedGrammars.IndexedNonterminal;
import de.rwth.i2.attestor.indexedGrammars.stack.*;
import de.rwth.i2.attestor.util.Pair;

/**
 * matches if and only if instantiableNonterminal is actualy intantiable (i.e. ends in stackVariable).
 * In this case, always returns materialization: MATERIALIZATION and instantiation INSTANTIATION
 * both globally accessible.
 * @author Hannah
 *
 */
public class FakeStackMatcher extends StackMatcher {

	public static final List<StackSymbol> MATERIALIZATION = createMaterialization();
	public static final List<StackSymbol> INSTANTIATION = createInstantiation();

	public FakeStackMatcher() {
		super(null);
	}

	private static List<StackSymbol> createInstantiation() {
		StackSymbol a = ConcreteStackSymbol.getStackSymbol("a", false);
		StackSymbol abs = AbstractStackSymbol.get("Y");
		
		List<StackSymbol> res = new ArrayList<>();
		res.add(a);
		res.add(abs);
		return res;
	}

	private static List<StackSymbol> createMaterialization() {
		StackSymbol s = ConcreteStackSymbol.getStackSymbol("a", false);
		StackSymbol abs = AbstractStackSymbol.get("Y");
		
		List<StackSymbol> res = new ArrayList<>();
		res.add(s);
		res.add(abs);
		return res;
	}

	public boolean canMatch(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal ){
		
		return ! instantiableNonterminal.hasConcreteStack();
	}
	
	public boolean needsMaterialization(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		
		return canMatch(materializableNonterminal, instantiableNonterminal) ? true : false;
	}
	
	public Pair<AbstractStackSymbol, List<StackSymbol> > getMaterializationRule(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		
		if( needsMaterialization( materializableNonterminal, instantiableNonterminal ) ) {
			AbstractStackSymbol lhs = (AbstractStackSymbol) materializableNonterminal.getLastStackSymbol();
			return new Pair<>( lhs, getNecessaryMaterialization(materializableNonterminal, instantiableNonterminal) );
		}else {
			return new Pair<>( null, new ArrayList<>() );
		}
	}
	
	private List<StackSymbol> getNecessaryMaterialization(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		if (canMatch(materializableNonterminal, instantiableNonterminal))
			return MATERIALIZATION;
		else
			return null;
	}
	
	public boolean needsInstantiation(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal )  {
		
		return canMatch(materializableNonterminal, instantiableNonterminal) ? true : false;
	}
	
	public List<StackSymbol> getNecessaryInstantiation(IndexedNonterminal materializableNonterminal, 
			   IndexedNonterminal instantiableNonterminal ) {
		if (canMatch(materializableNonterminal, instantiableNonterminal))
			return INSTANTIATION;
		else
			return null;
	}
}
