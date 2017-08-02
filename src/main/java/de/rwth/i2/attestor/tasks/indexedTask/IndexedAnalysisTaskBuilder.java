package de.rwth.i2.attestor.tasks.indexedTask;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.StackMatcher;
import de.rwth.i2.attestor.grammar.materialization.*;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.indexedGrammars.IndexedCanonicalizationStrategy;
import de.rwth.i2.attestor.indexedGrammars.IndexedState;
import de.rwth.i2.attestor.indexedGrammars.stack.DefaultStackMaterialization;
import de.rwth.i2.attestor.io.JsonToIndexedHC;
import de.rwth.i2.attestor.main.AnalysisTask;
import de.rwth.i2.attestor.main.AnalysisTaskBuilder;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleParser;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SSGBuilder;
import de.rwth.i2.attestor.tasks.GeneralAnalysisTaskBuilder;
import de.rwth.i2.attestor.util.FileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * A task builder to create and customize an analysis based on indexed hyperedge
 * replacement grammars.
 *
 * @author Christoph
 */
public class IndexedAnalysisTaskBuilder extends GeneralAnalysisTaskBuilder {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger( "IndexedAnalysisTaskBuilder" );

    @Override
    public AnalysisTask build() {
        checkElements();

        Grammar grammar = getGrammar();
        setMaterializationStrategy(buildGeneralMaterializationStrategy(grammar));
        setCanonicalizationStrategy(new IndexedCanonicalizationStrategy(grammar, true));

        SSGBuilder builder = setupStateSpaceGeneratorBuilder();

        return new IndexedAnalysisTask(builder.build());
    }

    /**
     * Constructs the materialization strategy applied by this task.
     * @param grammar The grammar that is used.
     * @return The materialization strategy.
     */
	private GeneralMaterializationStrategy buildGeneralMaterializationStrategy(Grammar grammar) {
		
		ViolationPointResolver vioResolver = new ViolationPointResolver( grammar );
		StackMatcher stackMatcher = new StackMatcher( new DefaultStackMaterialization() );
		MaterializationRuleManager grammarManager = 
				new IndexedMaterializationRuleManager(vioResolver, stackMatcher);
		
		GrammarResponseApplier ruleApplier = 
				new IndexedGrammarResponseApplier( new StackMaterializer(), 
												   new GraphMaterializer() );
		
		return new GeneralMaterializationStrategy( grammarManager, ruleApplier );
	}

    @Override
    protected List<ProgramState> setupInitialStates() {

	    List<ProgramState> initialStates = new ArrayList<>();

	    for(HeapConfiguration input : inputs) {
            IndexedState state;
            if (scopeDepth > 0) {
                state = new IndexedState(input, scopeDepth);
            } else {
                state = new IndexedState(input);
            }
            state.prepareHeap();
            state.setProgramCounter(0);
            initialStates.add(state);
        }
        return initialStates;
    }

    @Override
    public AnalysisTaskBuilder loadInput(String filename) throws FileNotFoundException {
        String str = FileReader.read(filename);
        JSONObject jsonObj = new JSONObject(str);
        HeapConfiguration inputGraph = JsonToIndexedHC.jsonToHC( jsonObj );
        return this.setInput( inputGraph );
    }

    @Override
    public AnalysisTaskBuilder loadProgram(String classpath, String filename, String entryPoint) {
        JimpleParser programParser = new JimpleParser(new StandardAbstractSemantics());
        return setProgram(programParser.parse(classpath, filename, entryPoint));
    }

}
