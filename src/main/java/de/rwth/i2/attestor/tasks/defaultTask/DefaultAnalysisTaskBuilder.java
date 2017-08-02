package de.rwth.i2.attestor.tasks.defaultTask;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.JsonToDefaultHC;
import de.rwth.i2.attestor.main.AnalysisTask;
import de.rwth.i2.attestor.main.AnalysisTaskBuilder;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleParser;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SSGBuilder;
import de.rwth.i2.attestor.tasks.GeneralAnalysisTaskBuilder;
import de.rwth.i2.attestor.util.FileReader;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple builder to create an analysis task based on standard hyperedge
 * replacement grammars.
 *
 * @author Christoph
 */
public class DefaultAnalysisTaskBuilder extends GeneralAnalysisTaskBuilder {

    @Override
    public AnalysisTask build() {
        checkElements();
        SSGBuilder builder = setupStateSpaceGeneratorBuilder();
        return new DefaultAnalysisTask(builder.build());
    }

    @Override
    protected List<ProgramState> setupInitialStates() {

        List<ProgramState> initialStates = new ArrayList<>();
        for(HeapConfiguration input : inputs) {
            ProgramState initialState;
            if (scopeDepth > 0) {
                initialState = new DefaultState(input, scopeDepth);
            } else {
                initialState = new DefaultState(input);
                initialState.prepareHeap();
            }
            initialState.setProgramCounter(0);
            initialStates.add(initialState);
        }
        return initialStates;
    }

    @Override
    public AnalysisTaskBuilder loadInput(String filename) throws FileNotFoundException {
        String str = FileReader.read(filename);
        JSONObject jsonObj = new JSONObject(str);
        HeapConfiguration inputGraph = JsonToDefaultHC.jsonToHC( jsonObj );
        return this.setInput( inputGraph );
    }

    @Override
    public AnalysisTaskBuilder loadProgram(String classpath, String filename, String entryPoint) {
        JimpleParser programParser = new JimpleParser(new StandardAbstractSemantics());
        return setProgram(programParser.parse(classpath, filename, entryPoint));
    }

}
