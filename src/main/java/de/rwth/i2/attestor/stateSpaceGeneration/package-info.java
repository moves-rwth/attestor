/**
 * A generic model of a state space and state space generation.
 * Clients may want to use {@link de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator#builder()}
 * to construct a StateSpaceGenerator and generate a state space.
 * <p>
 * State space generation is parametrized by various strategy classes that are passed to configure
 * the actually performed generation. This includes the following programState:
 * <ol>
 * <li>
 * {@link de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy}
 * determines when to give up.
 * </li>
 * <li>
 * {@link de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy}
 * determines how states are abstracted
 * </li>
 * <li>
 * {@link de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy}
 * determines how to materialize states to make concrete semantics applicable.
 * </li>
 * <li>
 * {@link de.rwth.i2.attestor.stateSpaceGeneration.StateLabelingStrategy}
 * determines how states are labeled with atomic propositions for further analysis.
 * </li>
 * </ol>
 *
 * @author Christoph
 */
package de.rwth.i2.attestor.stateSpaceGeneration;