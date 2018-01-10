package de.rwth.i2.attestor.grammar.materialization.strategies;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.grammar.materialization.communication.*;
import de.rwth.i2.attestor.grammar.materialization.util.ApplicableRulesFinder;
import de.rwth.i2.attestor.grammar.materialization.util.GrammarResponseApplier;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.util.Pair;

public class OneStepMaterializationStrategy {
	
    private static final Logger logger = LogManager.getLogger("OneStepMaterializationStrategy");
	
	private final ApplicableRulesFinder ruleFinder;
	private final GrammarResponseApplier ruleApplier;
	
	

    public OneStepMaterializationStrategy(ApplicableRulesFinder ruleFinder, GrammarResponseApplier ruleApplier) {
		super();
		this.ruleFinder = ruleFinder;
		this.ruleApplier = ruleApplier;
	}



	/**
     * Attempts to apply all possible rules once to the heap, i.e. for n possible rule applications
     * (possibly the same rule at several nonterminals) n heapconfigurations are returend each corresponding
     * to one of the possibilities.
     *
     * @param heapConfiguration The program state that should be materialized
     * @return A list of materialized program states each materilized by exactly one step
     */
    public Collection<HeapConfiguration> materialize( HeapConfiguration heapConfiguration ){
    	  List<HeapConfiguration> res = new ArrayList<>();

          Deque<Pair<Integer, GrammarResponse>> applicableRules = new ArrayDeque<>();
		try {
			applicableRules = ruleFinder.findApplicableRules( heapConfiguration );
		} catch (UnexpectedNonterminalTypeException e1) {
			logger.error(e1.getMessage());
		} 

          while (!applicableRules.isEmpty()) {

              Pair<Integer,GrammarResponse> grammarRule = applicableRules.pop();
              try {
				res.addAll( ruleApplier.applyGrammarResponseTo(heapConfiguration, grammarRule.first(), grammarRule.second()));
			} catch (WrongResponseTypeException e) {
				logger.error("ruleApplier cannot handle the GrammarResponse created by ruleManager");
			}
         
          }

          return res;
    }

}
