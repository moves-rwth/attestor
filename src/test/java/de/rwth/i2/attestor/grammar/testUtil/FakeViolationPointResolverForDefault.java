package de.rwth.i2.attestor.grammar.testUtil;

import java.util.*;

import de.rwth.i2.attestor.grammar.materialization.ViolationPointResolver;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.environment.SceneObject;

public class FakeViolationPointResolverForDefault extends ViolationPointResolver {

	private SceneObject sceneObject;

	public HeapConfiguration RHS_CREATING_NEXT;
	public HeapConfiguration RHS_CREATING_NEXT_PREV;
	public Nonterminal DEFAULT_NONTERMINAL;

	public FakeViolationPointResolverForDefault(SceneObject sceneObject) {
		super(null);
		this.sceneObject = sceneObject;
		TestGraphs testGraphs = new TestGraphs(sceneObject);
		RHS_CREATING_NEXT = testGraphs.getRuleGraph_CreatingNext();
		RHS_CREATING_NEXT_PREV = testGraphs.getRuleGraph_CreatingNextAt0_PrevAt1();
		DEFAULT_NONTERMINAL = createDefaultNonterminal();
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
	
	private Nonterminal createDefaultNonterminal() {
		int rank = 3;
		final boolean[] isReductionTentacle = new boolean[]{true, false};
		final String uniqueLabel = "DefaultMaterializationRuleManagerTest";
		return sceneObject.scene().createNonterminal(uniqueLabel, rank, isReductionTentacle);
	}
}
