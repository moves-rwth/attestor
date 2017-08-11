package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;

import java.io.IOException;
import java.util.*;

/**
 * A StateSpace is an edge-labeled transition system.
 * States are of type {@link ProgramState} and correspond an abstract program state that comprises one or more
 * heap configurations at a given program location.
 *
 *  There are two kinds of transitions that are distinguished by their labeling.
 *
 * First, transitions labeled with program statements, see {@link Semantics#toString()}, correspond to the execution
 * of a single statement of the (abstract) operational semantics. In this case, the target of such a transition
 * is a (potentially further abstracted) state that results from executing the abstract semantics
 * (corresponding to the label of the transition) on the source state of the transition.
 *
 * Second, transitions may not be labeled. In this case, the transition corresponds to an application of
 * materialization.
 *
 * @author Christoph
 *
 */
public class StateSpace {

	/**
     * A list of all program states that belong to the state space.
     * The position in this list is used as an ID to reference states.
	 */
	private final List<ProgramState> states;

	/**
     * The successors of every state in the state space as a mapping from
     * source states to all successor states together with their transition
     * label.
	 * The label of the transition is either the label of an executed program statement
	 * or "" if materialization has been applied between two states.
	 */
	private final Map<ProgramState, List<StateSuccessor>> successors;

    /**
     * A list of all initial states.
     */
	private final List<ProgramState> initialStates;

    /**
     * A list of all final states.
     */
	private final List<ProgramState> finalStates;

    /**
     * The size of the largest program state contained in the state space.
     */
	private int maxSize;

    /**
     * Initializes an empty state space.
     */
	public StateSpace(){

		states = new ArrayList<>();
		successors = new HashMap<>();
		initialStates = new ArrayList<>();
		finalStates = new ArrayList<>();
		maxSize= 0;
	}

    /**
     * @param state A program state.
     * @return True if and only if state belongs to the state space.
     */
	public boolean contains( ProgramState state ){

		return states.contains(state);
		
	}

    /**
     * @return A list of all states that belong to the state space.
     */
	public List<ProgramState> getStates(){

		return new ArrayList<>( states );
	}

    /**
     * @return A list of all final states that belong to the state space.
     */
	public List<ProgramState> getFinalStates(){
		return new ArrayList<>(finalStates);
	}

    /**
     * @return A list of all initial states that belong to the state space.
     */
	public List<ProgramState> getInitialStates(){
		return this.initialStates;
	}

    /**
     * @return A mapping from program states to a list of its successor states together with the respective
     *         transition label.
     */
	public Map<ProgramState, List<StateSuccessor>> getSuccessors(){
		return successors;
	}


    /**
     * Adds a program state to the state space.
     * @param state The state that should be added.
     * @return The ID of the added state.
     */
	protected int addState( ProgramState state ){

		if(states.contains(state)) {
			return states.indexOf(state);
		}

		int result = states.size();		
		states.add( state );
	
		if(state.size() > maxSize) {
			maxSize = state.size();
		}

		if( Settings.getInstance().output().isExportBigStates() 
			&& state.size() > Settings.getInstance().output().getBigStatesThreshold()
			&& state.getHeap() != null){
			
			HeapConfiguration heap = state.getHeap();
			
			String location = Settings.getInstance().output().getLocationForBigStates();
			try {
				Settings.getInstance().factory()
                        .export(location, "largeState", heap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

    /**
     * @param programState A program state.
     * @return The ID in this state space corresponding to the given program state.
     */
	public int indexOf(ProgramState programState) {
		
		return states.indexOf(programState);
	}

    /**
     * Computes a list of all successor states of a given program state.
     * @param programState A program state.
     * @return A list of all states that are the target of a transition with programState as a source.
     */
	public List<ProgramState> successorsOf(ProgramState programState) {
		
		List<ProgramState> res = new ArrayList<>();

		if(!successors.containsKey(programState)) {
			return res;
		}
		
		for(StateSuccessor s : successors.get(programState)) {
			
			res.add(s.getTarget());
		}
		
		return res;
	}

    /**
     * @param id The ID of a state contained in this state space.
     * @return The program state corresponding to the given ID.
     */
	public ProgramState get(int id) {
		
		return states.get(id);
	}

    /**
     * Adds a transition between the two given states belonging to the state space
     * that corresponds to a materialization of state from.
     * @param from The source of the transition.
     * @param to The target of the transition.
     */
	void addMaterializedSuccessor(ProgramState from, ProgramState to){

		assert ( contains( from ) );
		assert ( contains( to ) );

		addSuccessor(from, "", to);
	}

    /**
     * Adds a transition between two states belonging to the state space that corresponds to the execution of a single
     * program statement on input state from to obtain state to.
     * @param from The source state of the transition.
     * @param label The label corresponding to the executed program statement.
     * @param to The target state of the transition.
     */
	protected void addControlFlowSuccessor( ProgramState from, String label, ProgramState to){

		assert ( contains( from ) );
		assert ( !label.isEmpty() ); // empty labels indicate materialized successors
		assert ( contains( to ) );

		addSuccessor(from, label, to);
	}

    /**
     * Adds a transition between the given two states with the provided label.
     * @param from The source state of the transition.
     * @param label The label of the transition.
     * @param to The target state of the transition.
     */
	private void addSuccessor( ProgramState from, String label, ProgramState to ) {
		
		if(!successors.containsKey(from)) {
			
			successors.put(from, new ArrayList<>());
		}

		StateSuccessor newSuccessor = new StateSuccessor(label, to);
		List<StateSuccessor> stateSuccessors = successors.get(from);
		boolean found = false;
		for(StateSuccessor s : stateSuccessors) {
		    if(s.equals(newSuccessor)) {
		        found = true;
		        break;
            }
        }

		if(!found) {
			stateSuccessors.add(newSuccessor);
		}
	}

    /**
     * Declares a program state belonging to the state space as a final state.
     * @param programState A program state that should be declared final.
     */
	void setFinal(ProgramState programState){

		assert ( contains( programState ) );
		finalStates.add( programState );
	}

    /**
     * Adds an initial state to the state space.
     * @param programState The state that should be added.
     */
	protected void addInitialState(ProgramState programState){

		if(!states.contains(programState)) {
			states.add(programState);
		}

		initialStates.add( programState );
		if(programState.size() > maxSize) {
			maxSize = programState.size();
		}
	}

    /**
     * @return The size of the largest state contained in the state space.
     */
	public int getMaximalStateSize() {
		
		return maxSize;
	}

    /**
     * @return A mapping from all program locations to the set of states within the
     *         state space at that program location.
     */
	public Map<Integer, Set<ProgramState>> getReachableStatesByPC() {
		
		Map<Integer, Set<ProgramState>> res = new HashMap<>();
		
		for(ProgramState state : states) {
			
			int pc = state.getProgramCounter();
			if(!res.containsKey(pc)) {
				res.put(pc, new HashSet<>() );
			}
			
			res.get(pc).add( state );
		}
		
		return res;		
	}

	public List<ProgramState> getSuccessorsWithoutMaterialisation(ProgramState currentState) {
		List<ProgramState> res = new ArrayList<>();

		if(!successors.containsKey(currentState)) {
			return res;
		}

		for(StateSuccessor s : successors.get(currentState)) {

			// The successor resulted from a materialisation step. Leap over and add all its successors!
			if(s.getLabel() == ""){
				for(StateSuccessor ms : successors.get(s)) {
					res.add(ms.getTarget());
				}
			} else {
				res.add(s.getTarget());
			}
		}

		return res;

	}
}
