/**
 * This package containsSubsumingState all the classes necessary for materialization.
 * <p>
 * The main-class is {@link de.rwth.i2.attestor.grammar.materialization.strategies.GeneralMaterializationStrategy}
 * which is the only class which should be called from outside.
 * <br>
 * The GeneralMaterializationStrategy gets two components from outside to configure
 * the materialization prozess (e.g. to adapt the process to handle indexed grammars).
 * <br>
 * The first component is a {@link de.rwth.i2.attestor.grammar.materialization.util.MaterializationRuleManager}
 * this component is responsible for choosing the correct rules to apply for a given
 * violation point.
 * Choose {@link de.rwth.i2.attestor.grammar.materialization.defaultGrammar.DefaultMaterializationRuleManager}
 * if you have only basic grammars and
 * {@link de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager} for
 * indexed or mixed grammars.
 * <br>
 * The MaterializationRuleManager itself gets a {@link de.rwth.i2.attestor.grammar.materialization.util.ViolationPointResolver}
 * which is responsible to get and cache the rules resolving a specific violation point.
 * An {@link de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager}
 * <p>
 * additionally gets a {@link de.rwth.i2.attestor.grammar.IndexMatcher} which ensures that
 * indexed rules are instantiated appropriately and that also provides the necessary materialization
 * whichh has to be applied before applying the rule graph.
 * <br>
 * The second component of the {@link de.rwth.i2.attestor.grammar.materialization.strategies.GeneralMaterializationStrategy}
 * is a {@link de.rwth.i2.attestor.grammar.materialization.util.GrammarResponseApplier}. It is
 * responsible for materializing the graph with the rules determined by the {@link de.rwth.i2.attestor.grammar.materialization.util.MaterializationRuleManager}.
 * The {@link de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier} consists
 * only of a {@link de.rwth.i2.attestor.grammar.materialization.util.GraphMaterializer} which replaces
 * the nonterminal by the rule graph. The {@link de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedGrammarResponseApplier}
 * additionally consists of a {@link de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy}
 * which applies the materilaization to indices.
 * <br>
 * Note that you may get an error if you combine an {@link de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexedMaterializationRuleManager}
 * with a {@link de.rwth.i2.attestor.grammar.materialization.communication.DefaultGrammarResponseApplier} since
 * the latter is not able to handle the response of the first.
 *
 * @author Hannah
 */
package de.rwth.i2.attestor.grammar.materialization;