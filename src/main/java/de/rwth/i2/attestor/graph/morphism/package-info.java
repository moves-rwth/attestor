/**
 * This package implements algorithms to compute various kinds of graph morphisms.
 * In other words, the algorithms in this package compute mappings between to instances of
 * {@link de.rwth.i2.attestor.graph.morphism.Graph}.
 * <p>
 * The result of executing such an algorithm is provided as a {@link de.rwth.i2.attestor.graph.morphism.Morphism}.
 * Furthermore, all algorithms are executed through the interface
 * {@link de.rwth.i2.attestor.graph.morphism.MorphismChecker}. Concrete implementations of this interface are found
 * in {@link de.rwth.i2.attestor.graph.morphism.checkers}.
 * <p>
 * <p>
 * The general algorithm to search and generate graph morphisms implemented in this package is the well-known
 * VF2 Algorithm. Details are found in the paper
 *
 * @author Christoph
 * @see <a href="http://dblp.uni-trier.de/rec/html/journals/pami/CordellaFSV04"> Cordella et al.</a>
 * <p>
 * A key feature of the version of VF2 in this package is that it is highly modifiable through
 * so-called feasibility functions. Their interface is given by
 * {@link de.rwth.i2.attestor.graph.morphism.FeasibilityFunction}.
 * The set and order of feasibility functions employed by a concrete MorphismChecker determines what kind of graph
 * morphism, such as isomorphism, embedding, and so on, is computed.
 * A collection of already implemented feasibility functions is found in
 * {@link de.rwth.i2.attestor.graph.morphism.feasibility}.
 * Apart from that the termination criterion used by the algorithm is configurable through a TerminationFunction.
 * Existing implementations of these functions are found in
 * {@link de.rwth.i2.attestor.graph.morphism.terminationFunctions}.
 */
package de.rwth.i2.attestor.graph.morphism;