package de.rwth.i2.attestor.stateSpaceGeneration;

import java.util.*;

import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;

/**
 * A StateSpaceGenerator takes an analysis and generates a
 * state space from it. <br/>
 * Initialization of a StateSpaceGenerator has to be performed
 * by calling the static method StateSpaceGenerator.<T>builder().
 * This yields a builder object to configure the analysis used during
 * state space generation.
 * <br/>
 *  The generation of a StateSpace itself is started by invoking generate().
 *  
 * @author christoph
 *
 */
public class StateSpaceGenerator {

	/**
	 * @return An SSGBuilder which is the only means to create a new
	 * StateSpaceGenerator object.
	 */
	public static SSGBuilder builder() {
		return new SSGBuilder();
	}	


	/**
	 * Stores the state space generated upon instantiation of
	 * this generator.
	 */
	final StateSpace stateSpace;

	/**
	 * Stores the program configurations that still have
	 * to be executed by the state space generation
	 */
	private final Stack<ProgramState> unexploredConfigurations;

	/**
	 * The control flow of the program together with the
	 * abstract semantics of each statement
	 */
	Program program;

	/**
	 * Strategy guiding the materialization of states.
	 * This strategy is invoked whenever an abstract transfer
	 * function cannot be executed.
	 */
	MaterializationStrategy materializer;

	/**
	 * Strategy guiding the canonicalization of states.
	 * This strategy is invoked after execution of abstract transfer
	 * functions in order to generalize.
	 */
	CanonicalizationStrategy canonicalizationStrategy;

	/**
	 * Strategy determining when to give up on further state space
	 * exploration.
	 */
	AbortStrategy abortStrategy;

	/**
	 * Strategy determining which (approximation of) an inclusion
	 * check is used.
	 */
	InclusionStrategy inclusionStrategy;

	/**
	 * Strategy determining the labels of states in the state space
	 */
	StateLabelingStrategy stateLabelingStrategy;

	/**
     * Initializes a state space generator with an empty state space.
	 */
	StateSpaceGenerator() {
		this.materializer = null;
		this.canonicalizationStrategy = null;
		this.program = null;
		this.abortStrategy = null;
		this.inclusionStrategy = null;
		this.stateLabelingStrategy = null;

		this.unexploredConfigurations = new Stack<>();

		this.stateSpace = new StateSpace();
	}

    /**
     * @return The strategy determining when state space generation is aborted.
     */
	public AbortStrategy getAbortStrategy() {
		return abortStrategy;
	}

    /**
     * @return The strategy determining how materialization is performed.
     */
	public MaterializationStrategy getMaterializationStrategy() {
		return materializer;
	}

    /**
     * @return The strategy determining how canonicalization is performed.
     */
	public CanonicalizationStrategy getCanonizationStrategy() {
		return canonicalizationStrategy;
	}

    /**
     * @return The strategy determining how the inclusion problem between heap configurations is discharged.
     */
	public InclusionStrategy getInclusionStrategy() {
		return inclusionStrategy;
	}

    /**
     * @return The strategy determining how states are labeled with atomic propositions.
     */
	public StateLabelingStrategy getStateLabelingStrategy() {
		return stateLabelingStrategy;
	}

	/**
	 * Attempts to generate a StateSpace according to the
	 * underlying analysis.
	 * @return The generated StateSpace.
	 */
	public StateSpace generate(){

		while( hasUnexploredStates() ){

			ProgramState state = unexploredConfigurations.pop();


			Semantics stmt = program.getStatement(state.getProgramCounter());
			
			List<ProgramState> materialized = materializer.materialize(state, stmt.getPotentialViolationPoints());
			
			if(materialized.isEmpty()) {
	
				computeSuccessors(state);
				
			} else {
				for( ProgramState mat : materialized ){

					ProgramState matInSS = getSubsumingStateInSSOrAddToSS(mat);					
					stateSpace.addMaterializedSuccessor(state, matInSS);
				}
			}
		}

		return stateSpace;
	}


	/**
	 * @return true iff further states can and should be generated.
	 */
	private boolean hasUnexploredStates(){

		return !unexploredConfigurations.isEmpty() && abortStrategy.isAllowedToContinue( stateSpace );
	}


    /**
     * First attempts to find a state in the already generated state space that subsumes the given state.
     * If such a state cannot be found, the state is added to the state space instead.
     * @param state A state that should be added to the state space if no subsuming state exists.
     * @return A subsuming state already belonging to the state space or the state that has been added to
     *         the state space.
     */
	ProgramState getSubsumingStateInSSOrAddToSS(ProgramState state) {

		if(stateSpace.contains(state)) {
			return state;
		}
		
		ProgramState result = null;

		for(ProgramState candidate : stateSpace.getStates()) {

			if(inclusionStrategy.isIncludedIn(state, candidate)) {
				result = candidate;
				break;

			}
		}

		if(result == null) {

			result = state;
			stateSpace.addState(state);
			unexploredConfigurations.add(state);
		}

		return result;
	}

	/**
	 * Computes canonical successors of the given program state.
	 * 
	 * @param state The program state whose successor states shall be computed.
	 */
	private void computeSuccessors( ProgramState state ){

		boolean finalState = true;

		Semantics semantics = program.getStatement( state.getProgramCounter() );

		try {

			for( ProgramState succ : semantics.computeSuccessors( state )) {
				
				canonicalizeAndAddSuccessorStates(semantics, state, succ);
				finalState = false;
			}
		} catch (NotSufficientlyMaterializedException e) {

			e.printStackTrace();
		}

		if( finalState ){

			stateSpace.setFinal( state );
		}
	}

    /**
     * Adds a (control flow) transition from the given state to the given state that is labeled with the
     * executed program statement. Prior to adding the edge, canonicalization may be performed. In this
     * case the obtained abstracted states are used instead of the provided state to.
     * @param semantics The program statement that has been executed and thus determines the label of the control
     *                  flow transition that shall be added.
     * @param from The source of the transition.
     * @param to The target of the transition.
     */
	private void canonicalizeAndAddSuccessorStates(Semantics semantics, ProgramState from, ProgramState to ) {

		Set<ProgramState> canonized = canonicalizationStrategy.canonicalize(semantics, to);
		String edgeLabel = semantics.toString();
		
		if(semantics.permitsCanonicalization()) {

			for(ProgramState c : canonized) {

				ProgramState subState = getSubsumingStateInSSOrAddToSS(c);
				stateSpace.addControlFlowSuccessor(from, edgeLabel, subState);
			}				
		} else {
			
			for(ProgramState c : canonized) {

				stateSpace.addState(c);
				
				unexploredConfigurations.add(c);
				stateSpace.addControlFlowSuccessor(from, edgeLabel, c);				
			}
		}
	}

    /**
     * @return The initial state of the generated state space.
     */
	public ProgramState getInitialState() {
		return stateSpace.getInitialStates().get(0);
	}
}
