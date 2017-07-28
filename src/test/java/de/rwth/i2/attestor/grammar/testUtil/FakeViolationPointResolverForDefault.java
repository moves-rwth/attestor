package de.rwth.i2.attestor.grammar.testUtil;

import java.util.*;

import de.rwth.i2.attestor.grammar.materialization.ViolationPointResolver;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;

public class FakeViolationPointResolverForDefault extends ViolationPointResolver {


	public static final HeapConfiguration RHS_CREATING_NEXT = 
			TestGraphs.getRuleGraph_CreatingNext();
	public static final HeapConfiguration RHS_CREATING_NEXT_PREV = 
			TestGraphs.getRuleGraph_CreatingNextAt0_PrevAt1();
	public static final GeneralNonterminal DEFAULT_NONTERMINAL =
			createDefaultNonterminal();
	
	public FakeViolationPointResolverForDefault() {
		super(null);
	}

	@Override
	public Map<Nonterminal, Collection<HeapConfiguration>> getRulesCreatingSelectorFor(Nonterminal toReplace, 
																		int tentacle, 
																		String requestedSelector) {
		
		Map<Nonterminal, Collection<HeapConfiguration> > res = new HashMap<>();
		res.put(DEFAULT_NONTERMINAL, new HashSet<>());
		res.get(DEFAULT_NONTERMINAL).add(RHS_CREATING_NEXT);
		res.get(DEFAULT_NONTERMINAL).add(RHS_CREATING_NEXT_PREV);
		
		return res;
	}
	
	private static GeneralNonterminal createDefaultNonterminal() {
		int rank = 3;
		final boolean[] isReductionTentacle = new boolean[]{true, false};
		final String uniqueLabel = "DefaultMaterializationRuleManagerTest";
		return GeneralNonterminal.getNonterminal(uniqueLabel, rank, isReductionTentacle);
	}
}
