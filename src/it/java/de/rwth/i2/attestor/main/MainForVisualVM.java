package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.phases.counterexamples.CounterexampleGenerationPhase;
import de.rwth.i2.attestor.phases.modelChecking.ModelCheckingPhase;
import de.rwth.i2.attestor.phases.parser.*;
import de.rwth.i2.attestor.phases.preprocessing.AbstractionPreprocessingPhase;
import de.rwth.i2.attestor.phases.preprocessing.GrammarRefinementPhase;
import de.rwth.i2.attestor.phases.preprocessing.MarkingGenerationPhase;
import de.rwth.i2.attestor.phases.report.ReportOutputPhase;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.RecursiveStateSpaceGenerationPhase;

public class MainForVisualVM extends AbstractAttestor{

    public static void main(String[] args) {

        AbstractAttestor main = new MainForVisualVM();
        main.run(args);
    }

    @Override
    protected void registerPhases(String[] args) throws Exception {

        registry.addPhase(new CLIPhase(scene, args));
        registry.addPhase(new ParseProgramPhase(scene));
        registry.addPhase(new ParseGrammarPhase(scene));
        registry.addPhase(new ParseInputPhase(scene));
        registry.addPhase(new ParseContractsPhase(scene));
        registry.addPhase(new MarkingGenerationPhase(scene));
        registry.addPhase(new GrammarRefinementPhase(scene));
        registry.addPhase(new AbstractionPreprocessingPhase(scene));
        registry.addPhase(new DelayedPhase(scene,10000));
        registry.addPhase(new RecursiveStateSpaceGenerationPhase(scene));
        registry.addPhase(new ModelCheckingPhase(scene));
        registry.addPhase(new CounterexampleGenerationPhase(scene));
        registry.addPhase(new ReportOutputPhase(scene, registry.getPhases()));
        registry.execute();
    }
}
