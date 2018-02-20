package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;


import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

public class InterproceduralAnalysis {

	Deque<ProcedureCall> remainingProcedureCalls = new ArrayDeque<>();
	Deque<PartialStateSpace> remainingPartialStateSpaces = new ArrayDeque<>();

	Map<ProcedureCall, Set<PartialStateSpace>> callingDependencies = new LinkedHashMap<>();
	Map<StateSpace, ProcedureCall> stateSpaceToAnalyzedCall = new LinkedHashMap<>();


	public void registerStateSpace( ProcedureCall call, StateSpace stateSpace) {

		stateSpaceToAnalyzedCall.put(stateSpace, call);
	}


	public void registerDependency(ProcedureCall procedureCall, PartialStateSpace dependentPartialStateSpace) {

		if(!callingDependencies.containsKey(procedureCall)) {
			Set<PartialStateSpace> dependencies = new LinkedHashSet<>();
			dependencies.add(dependentPartialStateSpace);
			callingDependencies.put(procedureCall, dependencies);
		} else {
			callingDependencies.get(procedureCall).add(dependentPartialStateSpace);
		}
	}

	public void registerProcedureCall(ProcedureCall procedureCall) {

		if(!remainingProcedureCalls.contains(procedureCall)) {
			remainingProcedureCalls.push(procedureCall);
		}
	}

	public void run() {

		while(!remainingProcedureCalls.isEmpty() || !remainingPartialStateSpaces.isEmpty()) {
			ProcedureCall call;
			if(!remainingProcedureCalls.isEmpty()) {
				call = remainingProcedureCalls.pop();
				call.execute();
			} else {
				PartialStateSpace partialStateSpace = remainingPartialStateSpaces.pop();
				call = stateSpaceToAnalyzedCall.get( partialStateSpace.unfinishedStateSpace() );
				partialStateSpace.continueExecution(call);
			}
			notifyDependencies(call);
		}
	}

	private void notifyDependencies(ProcedureCall call) {

		Set<PartialStateSpace> dependencies = callingDependencies.getOrDefault(call, Collections.emptySet());
		remainingPartialStateSpaces.addAll(dependencies);
	}


}
