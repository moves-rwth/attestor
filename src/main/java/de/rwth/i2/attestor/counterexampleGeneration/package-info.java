/**
 * This package contains all necessary classes to perform counterexample generation.
 * That is, the class {@link de.rwth.i2.attestor.counterexampleGeneration.CounterexampleGenerator}
 * takes a {@link de.rwth.i2.attestor.counterexampleGeneration.Trace} -- a path through a previously generated state
 * space -- that violates a given LTL property.
 * It then generates a finite description of concrete program states that - if fed as input to the analyzed program -
 * lead to a violation of the property in question. The obtained input states can thus be allowed to generate
 * test cases to understand and debug a program with respect to LTL properties.
 *
 * @author Christoph
 */
package de.rwth.i2.attestor.counterexampleGeneration;