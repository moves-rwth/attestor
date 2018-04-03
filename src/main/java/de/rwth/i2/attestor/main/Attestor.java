package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.phases.commandLineInterface.CommandLinePhase;
import de.rwth.i2.attestor.phases.counterexamples.CounterexampleGenerationPhase;
import de.rwth.i2.attestor.phases.modelChecking.ModelCheckingPhase;
import de.rwth.i2.attestor.phases.parser.ParseContractsPhase;
import de.rwth.i2.attestor.phases.parser.ParseGrammarPhase;
import de.rwth.i2.attestor.phases.parser.ParseInputPhase;
import de.rwth.i2.attestor.phases.parser.ParseProgramPhase;
import de.rwth.i2.attestor.phases.preprocessing.AbstractionPreprocessingPhase;
import de.rwth.i2.attestor.phases.preprocessing.GrammarRefinementPhase;
import de.rwth.i2.attestor.phases.preprocessing.MarkingGenerationPhase;
import de.rwth.i2.attestor.phases.report.ReportGenerationPhase;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.RecursiveStateSpaceGenerationPhase;


/**
 * The main class to run Attestor.
 * <p>
 * To start a program analysis it suffices to call {Attestor#run(args)}, where args are the command line arguments
 * passed, for example, to a main method.
 * In particular, these arguments have to include the path to a communication file customizing the analysis.
 * <br>
 * The execution of Attestor consists of phases. Any fatal failure of a phase (that is an exception caught
 * by the method starting the phase) aborts further execution.
 * The main phases are executed in the following order:
 * <ol>
 * <li>Setup phase: Validates the provided command line options and populates the global Settings.</li>
 * <li>Parsing phase: Parses all supplied input files, such as the program to be analyzed,
 * the grammar, input state, etc.</li>
 * <li>Preprocessing phase: Applies all pre-computation steps that should be applied to programs, grammars, etc.
 * For example, grammar refinement is performed in this phase.</li>
 * <li>State space generation phase: Applies the abstract semantics defined by the provided graph grammar and
 * the input program until a fixed point is reached.</li>
 * <li>Model-checking phase: If temporal logic formulas have been provided, this phase checks whether they
 * are satisfied by the state space generated in the previous phase.</li>
 * <li>Report phase: Exports the previously computed results. </li>
 * </ol>
 *
 * @author Christoph
 */
public class Attestor extends AbstractAttestor {

    @Override
    protected void registerPhases(String[] args) throws Exception {

        registry
                //.addPhase(new CLIPhase(scene, args))
                .addPhase(new CommandLinePhase(scene, args))
                .addPhase(new ParseProgramPhase(scene))
                .addPhase(new ParseGrammarPhase(scene))
                .addPhase(new ParseInputPhase(scene))
                .addPhase(new ParseContractsPhase(scene))
                .addPhase(new MarkingGenerationPhase(scene))
                .addPhase(new GrammarRefinementPhase(scene))
                .addPhase(new AbstractionPreprocessingPhase(scene))
                .addPhase(new RecursiveStateSpaceGenerationPhase(scene))
                .addPhase(new ModelCheckingPhase(scene))
                .addPhase(new CounterexampleGenerationPhase(scene))
                .addPhase( new ReportGenerationPhase(registry, scene) )
                .execute();
    }
}
