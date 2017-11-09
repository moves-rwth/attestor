/**
 *
 * This package allows to traverse all graphs in the language of an (abstract) HeapConfiguration with respect to
 * a graph grammar. During this traverse, every node reachable from some program variable (and potentially some of its
 * selectors) are marked by a special variable. The exact variables used for marking are specified by an object of type
 * {@link de.rwth.i2.attestor.markings.Marking}.
 *
 * The procedure to generate marked HeapConfigurations is started by creating a
 * {@link de.rwth.i2.attestor.markings.MarkedHcGenerator}.
 *
 * The goal of this procedure is to track object identities during state space generation.
 * For example, one could place markings on an initial state to verify that every element of a data structure has been
 * visited by some variable at least once.
 *
 * @author Christoph
 */
package de.rwth.i2.attestor.markings;
