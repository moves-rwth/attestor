package de.rwth.i2.attestor.grammar.canonicalization;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;

public interface CanonicalizationHelper {

	ProgramState tryReplaceMatching( ProgramState toAbstract, 
										  HeapConfiguration rhs, Nonterminal lhs,
										  Semantics semantics );
	
	ProgramState prepareHeapForCanonicalization( ProgramState toAbstract );

}