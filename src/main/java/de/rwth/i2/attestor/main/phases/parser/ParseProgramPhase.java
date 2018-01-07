package de.rwth.i2.attestor.main.phases.parser;

import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.communication.InputSettings;
import de.rwth.i2.attestor.main.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleParser;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;

public class ParseProgramPhase extends AbstractPhase implements ProgramTransformer {

    private Program program;

    public ParseProgramPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Parse program";
    }

    @Override
    protected void executePhase() {

        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        JimpleParser programParser = new JimpleParser(this, new StandardAbstractSemantics(this));
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
    public boolean isVerificationPhase() {

        return false;
    }

    @Override
    public Program getProgram() {

        return program;
    }
}