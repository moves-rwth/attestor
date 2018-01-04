package de.rwth.i2.attestor.interprocedural;

import java.util.*;

public class InterproceduralAnalysis {

    Map<ProcedureCall, Set<PartialStateSpace>> callingDependencies = new LinkedHashMap<>();
    Deque<ProcedureCall> remainingProcedureCalls = new ArrayDeque<>();
    Deque<PartialStateSpace> remainingPartialStateSpaces = new ArrayDeque<>();

    private ProcedureCall mainProcedure;

    public void setMainProcedure(ProcedureCall mainProcedure) {

        this.mainProcedure = mainProcedure;
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

        assert mainProcedure != null;
        remainingProcedureCalls.push(mainProcedure);

        while(!remainingProcedureCalls.isEmpty() || !remainingPartialStateSpaces.isEmpty()) {
            ProcedureCall call;
            if(!remainingProcedureCalls.isEmpty()) {
                call = remainingProcedureCalls.pop();
                call.execute();
            } else {
                PartialStateSpace partialStateSpace = remainingPartialStateSpaces.pop();
                call = partialStateSpace.continueExecution();
            }
            updateDependencies(call);
        }

    }

    private void updateDependencies(ProcedureCall call) {
        remainingPartialStateSpaces.addAll(
                callingDependencies.get(call)
        );
    }

}
