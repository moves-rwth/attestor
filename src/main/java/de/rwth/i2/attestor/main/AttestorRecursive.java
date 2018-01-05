package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.main.phases.counterexamples.CounterexampleGenerationPhase;
import de.rwth.i2.attestor.main.phases.modelChecking.ModelCheckingPhase;
import de.rwth.i2.attestor.main.phases.parser.*;
import de.rwth.i2.attestor.main.phases.preprocessing.AbstractionPreprocessingPhase;
import de.rwth.i2.attestor.main.phases.preprocessing.GrammarRefinementPhase;
import de.rwth.i2.attestor.main.phases.preprocessing.MarkingGenerationPhase;
import de.rwth.i2.attestor.main.phases.report.ReportGenerationPhase;
import de.rwth.i2.attestor.main.phases.report.ReportOutputPhase;
import de.rwth.i2.attestor.main.phases.symbolicExecution.interprocedural.InterproceduralAnalysisPhase;

public class AttestorRecursive extends AbstractAttestor {

    public static void main(String[] args) {

        AbstractAttestor main = new AttestorRecursive();
        main.run(args);
    }

    @Override
    protected void registerPhases(String[] args) {

        registry
                .addPhase(new CLIPhase(scene, args))
                .addPhase(new ParseProgramPhase(scene))
                .addPhase(new ParseGrammarPhase(scene))
                .addPhase(new ParseInputPhase(scene))
                .addPhase(new ParseContractsPhase(scene))
                .addPhase(new MarkingGenerationPhase(scene))
                .addPhase(new GrammarRefinementPhase(scene))
                .addPhase(new AbstractionPreprocessingPhase(scene))
                .addPhase(new InterproceduralAnalysisPhase(scene))
                .addPhase(new ModelCheckingPhase(scene))
                .addPhase(new CounterexampleGenerationPhase(scene))
                .addPhase(new ReportGenerationPhase(scene))
                .addPhase( new ReportOutputPhase(scene, registry.getPhases()) )
                .execute();
    }
}
