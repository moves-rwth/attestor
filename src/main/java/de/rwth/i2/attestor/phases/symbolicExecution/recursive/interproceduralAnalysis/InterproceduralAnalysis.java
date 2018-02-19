package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;


import java.util.*;

import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

public class InterproceduralAnalysis {

    Map<ProcedureCall, Set<PartialStateSpace>> callingDependencies = new LinkedHashMap<>();
    Deque<ProcedureCall> remainingProcedureCalls = new ArrayDeque<>();
    Deque<PartialStateSpace> remainingPartialStateSpaces = new ArrayDeque<>();
    Map<StateSpace, ProcedureCall> stateSpaceToAnalyzedCall = new LinkedHashMap<>();


    public void addMainProcedureCall( StateSpace mainStateSpace, ProcedureCall mainCall) {

        stateSpaceToAnalyzedCall.put(mainStateSpace, mainCall);
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
                StateSpace stateSpace = call.execute();
                stateSpaceToAnalyzedCall.put( stateSpace, call );
            } else {
                PartialStateSpace partialStateSpace = remainingPartialStateSpaces.pop();
                call = stateSpaceToAnalyzedCall.get(partialStateSpace.unfinishedStateSpace());
                partialStateSpace.continueExecution(call);
            }
            updateDependencies(call);
        }
    }

    private void updateDependencies(ProcedureCall call) {

        Set<PartialStateSpace> dependencies = callingDependencies.getOrDefault(call, Collections.emptySet());
        remainingPartialStateSpaces.addAll(dependencies);
    }

}
