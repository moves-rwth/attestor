package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.settings.InputSettings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleParser;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;

public class ParseProgramPhase extends AbstractPhase implements ProgramTransformer {

    private Program program;

    @Override
    public String getName() {

        return "Parse program";
    }

    @Override
    protected void executePhase() {

        InputSettings inputSettings = settings.input();
        JimpleParser programParser = new JimpleParser(new StandardAbstractSemantics());
        program = programParser.parse(
                inputSettings.getClasspath(),
                inputSettings.getClassName(),
                inputSettings.getMethodName()
        );
    }

    @Override
    public void logSummary() {

        // nothing to report
    }

    @Override
    public Program getProgram() {

        return program;
    }
}
