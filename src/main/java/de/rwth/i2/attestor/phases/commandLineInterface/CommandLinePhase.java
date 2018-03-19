package de.rwth.i2.attestor.phases.commandLineInterface;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommandLinePhase extends AbstractPhase {

    private final String[] originalCommandLineArguments;

    private final InputSettings inputSettings = new InputSettings();
    private final OutputSettings outputSettings = new OutputSettings();
    private final ModelCheckingSettings modelCheckingSettings = new ModelCheckingSettings();

    private CommandLineReader commandLineReader;
    CommandLine commandLine;

    public CommandLinePhase(Scene scene, String[] args) {

        super(scene);
        this.originalCommandLineArguments = args;
    }

    @Override
    public String getName() {

        return "Command Line Interface";
    }

    @Override
    public void executePhase() {

        commandLineReader = new CommandLineReader();
        commandLine = commandLineReader.read(originalCommandLineArguments);

        determineRootPath();

        Iterator<Option> optionIterator = commandLine.iterator();
        while (optionIterator.hasNext()) {
            Option option = optionIterator.next();
            processOption(option);
        }
    }

    private void determineRootPath() {

        String rootPath = "";
        if(commandLine.hasOption("root-path")) {
            rootPath = commandLine.getOptionValue("root-path");
        }
        inputSettings.setRootPath(rootPath);
        outputSettings.setRootPath(rootPath);
    }

    private void processOption(Option option) {

        String optionName = option.getLongOpt();
        switch(optionName) {

            case "description":
                description(option);
                break;
            case "help":
                help(option);
                break;
            case "load":
                // was already processed before
                break;
            case "root-path":
                // was already set before
                break;
            case "class":
                setClass(option);
                break;
            case "classpath":
                setClasspath(option);
                break;
            case "contract":
                contract(option);
                break;
            case "grammar":
                grammar(option);
                break;
            case "initial":
                initial(option);
                break;
            case "method":
                method(option);
                break;
            case "predefined-grammar":
                predefinedGrammar(option);
                break;
            case "admissibleAbstraction":
                admissibleAbstraction(option);
                break;
            case "admissibleConstants":
                admissibleConstants(option);
                break;
            case "admissibleMarkings":
                admissibleMarkings(option);
                break;
            case "admissibleFull":
                admissibleFull(option);
                break;
            case "no-chain-abstraction":
                noChainAbstraction(option);
                break;
            case "no-rule-collapsing":
                noRuleCollapsing(option);
                break;
            case "indexed":
                indexed(option);
                break;
            case "post-processing":
                postProcessing(option);
                break;
            case "canonical":
                canonical(option);
                break;
            case "model-checking":
                modelChecking(option);
                break;
            case "no-garbage-collector":
                noGarbageCollector(option);
                break;
            case "max-state-space":
                maxStateSpace(option);
                break;
            case "max-heap":
                maxHeap(option);
                break;
            case "export":
                export(option);
                break;
            case "export-grammar":
                exportGrammar(option);
                break;
            case "export-large-states":
                exportLargeStates(option);
                break;
            default:
                throw new IllegalArgumentException("Unknown command line option: " + optionName);
        }


    }

    private void description(Option option) {

        inputSettings.setDescription(option.getValue());
    }

    private void help(Option option) {

        commandLineReader.printHelp();
    }

    private void setClass(Option option) {

        inputSettings.setClassName(option.getValue());
    }

    private void setClasspath(Option option) {

        inputSettings.setClasspath(option.getValue());
    }

    private void contract(Option option) {

        inputSettings.addContractFile(option.getValue());
    }

    private void grammar(Option option) {

        inputSettings.addUserDefinedGrammarFile(option.getValue());
    }

    private void initial(Option option) {

        inputSettings.setPathToInput(option.getValue());
    }

    private void method(Option option) {

        inputSettings.setMethodName(option.getValue());
    }

    private void predefinedGrammar(Option option) {

        String[] values = option.getValues();

        if(values.length == 0) {
            throw new IllegalArgumentException("No predefined grammar name has been provided.");
        }

        String name = values[0];
        Map<String, String> renaming = new HashMap<>();

        for(int i=1; i < values.length; i++) {
            String v = values[i].trim();
            String[] r = v.split("=");
            if(r.length != 2) {
                throw new IllegalArgumentException("Invalid selector renaming of predefined grammar.");
            }
            renaming.put(r[0], r[1]);
        }

        inputSettings.addPredefinedGrammar(new InputSettings.PredefinedGrammar(name, renaming));
    }

    private void admissibleAbstraction(Option option) {

        scene().abstractionOptions().setAdmissibleAbstractionEnabled(true);
    }

    private void admissibleConstants(Option option) {

        scene().abstractionOptions().setAdmissibleConstantsEnabled(true);
    }

    private void admissibleMarkings(Option option) {

        scene().abstractionOptions().setAdmissibleMarkingsEnabled(true);
    }

    private void admissibleFull(Option option) {

        scene().abstractionOptions().setAdmissibleFullEnabled(true);
    }

    private void noChainAbstraction(Option option) {

        scene().abstractionOptions().setNoChainAbstractionEnabled(true);
    }

    private void noRuleCollapsing(Option option) {

        scene().abstractionOptions().setNoRuleCollapsingEnabled(true);
    }

    private void indexed(Option option) {

        scene().abstractionOptions().setIndexedModeEnabled(true);
    }

    private void postProcessing(Option option) {

        scene().abstractionOptions().setPostProcessingEnabled(true);
    }

    private void canonical(Option option) {

        if(commandLine.hasOption("post-processing")) {
            throw new IllegalArgumentException("Option --canonical is incompatible with option --post-processing.");
        }
        scene().abstractionOptions().setCanonicalEnabled(true);
    }

    private void modelChecking(Option option) {

        String formula = option.getValue();
        try {
            LTLFormula ltlFormula = new LTLFormula(formula);
            ltlFormula.toPNF();
            modelCheckingSettings.addFormula(ltlFormula);
        } catch (Exception e) {
            logger.error("The input " + formula + " is not a valid LTL formula. Skipping it.");
        }
    }

    private void noGarbageCollector(Option option) {

        scene().abstractionOptions().setNoGarbageCollectionEnabled(true);
    }

    private void maxStateSpace(Option option) {

        int size = Integer.valueOf(option.getValue());
        scene().abstractionOptions().setMaxStateSpace(size);
    }

    private void maxHeap(Option option) {

        int size = Integer.valueOf(option.getValue());
        scene().abstractionOptions().setMaxHeap(size);
    }

    private void export(Option option) {

        outputSettings.setExportPath(option.getValue());
    }

    private void exportGrammar(Option option) {

        outputSettings.setExportGrammarPath(option.getValue());
    }

    private void exportLargeStates(Option option) {

        outputSettings.setExportLargeStatesPath(option.getValue());
    }

    @Override
    public void logSummary() {
        // nothing to report
    }

    @Override
    public boolean isVerificationPhase() {
        return false;
    }
}
