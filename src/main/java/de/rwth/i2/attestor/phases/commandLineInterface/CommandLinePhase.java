package de.rwth.i2.attestor.phases.commandLineInterface;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.phases.communication.OutputSettings;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.MCSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.OutputSettingsTransformer;
import de.rwth.i2.attestor.phases.transformers.StateLabelingStrategyBuilderTransformer;
import de.rwth.i2.attestor.refinement.AutomatonStateLabelingStrategyBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Arrays;
import java.util.Iterator;

public class CommandLinePhase extends AbstractPhase
        implements InputSettingsTransformer, OutputSettingsTransformer,
        MCSettingsTransformer, StateLabelingStrategyBuilderTransformer {

    private final String[] originalCommandLineArguments;

    private final InputSettings inputSettings = new InputSettings();
    private final OutputSettings outputSettings = new OutputSettings();
    private final ModelCheckingSettings modelCheckingSettings = new ModelCheckingSettings();

    private CommandLineReader commandLineReader;
    private CommandLine commandLine;

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

        try {
            commandLineReader = new CommandLineReader();
            commandLine = commandLineReader.read(originalCommandLineArguments);
        } catch(Exception e) {
            commandLineReader.printHelp();
            throw new IllegalArgumentException(e.getMessage());
        }


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
            logger.info("root path: " + rootPath);
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
            case "rename":
                rename(option);
                break;
            case "admissible-abstraction":
                admissibleAbstraction();
                break;
            case "admissible-constants":
                admissibleConstants();
                break;
            case "admissible-markings":
                admissibleMarkings();
                break;
            case "admissible-full":
                admissibleFull();
                break;
            case "no-chain-abstraction":
                noChainAbstraction();
                break;
            case "no-rule-collapsing":
                noRuleCollapsing();
                break;
            case "indexed":
                indexed();
                break;
            case "post-processing":
                postProcessing();
                break;
            case "canonical":
                canonical();
                break;
            case "model-checking":
                modelChecking(option);
                break;
            case "no-garbage-collector":
                noGarbageCollector();
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
            case "save-contracts":
                saveContracts(option);
                break;
            case "export-contracts":
                exportContracts(option);
                break;
            case "quiet":
                quiet();
                break;
            case "verbose":
                verbose();
                break;
            case "debug":
                debug();
                break;
            case "christoph":
                christoph();
                break;
            default:
                throw new IllegalArgumentException("Unknown command line option: " + optionName);
        }

    }


    private void description(Option option) {

        String description = option.getValue();
        logger.info("description: " + description);
        inputSettings.setDescription(description);
    }

    private void setClass(Option option) {

        String classname = option.getValue();
        logger.info("classname: " + classname);
        inputSettings.setClassName(classname);
    }

    private void setClasspath(Option option) {

        String classpath = option.getValue();
        logger.info("classpath: " + classpath);
        inputSettings.setClasspath(classpath);
    }

    private void contract(Option option) {

        String contract = option.getValue();
        logger.info("contract: " + contract);
        inputSettings.addContractFile(contract);
    }

    private void grammar(Option option) {

        String grammar = option.getValue();
        logger.info("grammar: " + grammar);
        inputSettings.addUserDefinedGrammarFile(grammar);
    }

    private void initial(Option option) {

        String initial = option.getValue();
        logger.info("initial heap: " + initial);
        inputSettings.addInitialHeapFile(initial);
    }

    private void method(Option option) {

        String method = option.getValue();
        logger.info("method: " + method);
        inputSettings.setMethodName(method);
    }

    private void predefinedGrammar(Option option) {

        String grammarName = option.getValue();
        if(grammarName == null) {
            throw new IllegalArgumentException("Unspecified grammar name");
        }
        logger.info("predefined grammar: " + grammarName);
        inputSettings.addPredefinedGrammarName(grammarName);
    }

    private void rename(Option option) {

        String[] values = option.getValues();
        if(values.length == 0) {
            throw new IllegalArgumentException("No class to rename has been provided.");
        }

        String type = values[0];
        String[] t = type.split("=");
        if(t.length != 2) {
            throw new IllegalArgumentException("The syntax for type renaming is 'oldType=newType'.");
        }
        inputSettings.addTypeRenaming(t[0], t[1]);
        logger.info("using renaming " + Arrays.toString(values));

        for(int i=1; i < values.length; i++) {
            String selector = values[i].trim();
            String[] s = selector.split("=");
            if(s.length != 2) {
                throw new IllegalArgumentException("The syntax for selector renaming is 'oldSelector=newSelector'.");
            }
            inputSettings.addSelectorRenaming(t[1], s[0], s[1]);
        }
    }


    private void admissibleAbstraction() {

        logger.info("enabled admissibility check during abstraction");
        scene().options().setAdmissibleAbstractionEnabled(true);
    }

    private void admissibleConstants() {

        logger.info("enabled admissibility check for constants");
        scene().options().setAdmissibleConstantsEnabled(true);
    }

    private void admissibleMarkings() {

        logger.info("enabled admissibility check for markings");
        scene().options().setAdmissibleMarkingsEnabled(true);
    }

    private void admissibleFull() {

        logger.info("enabled full admissibility using materialization");
        scene().options().setAdmissibleFullEnabled(true);
    }

    private void noChainAbstraction() {

        logger.info("disabled chain abstraction");
        scene().options().setChainAbstractionEnabled(false);
    }

    private void noRuleCollapsing() {

        logger.info("disabled rule collapsing");
        scene().options().setRuleCollapsingEnabled(false);
    }

    private void indexed() {

        logger.info("enabled use of indexed grammars");
        scene().options().setIndexedModeEnabled(true);
    }

    private void postProcessing() {

        logger.info("enabled state space post processing");
        scene().options().setPostProcessingEnabled(true);
    }

    private void canonical() {

        if(commandLine.hasOption("post-processing")) {
            throw new IllegalArgumentException("Option --canonical is incompatible with option --post-processing.");
        }
        logger.info("enabled computation of canonical states");
        scene().options().setCanonicalEnabled(true);
    }

    private void modelChecking(Option option) {

        String formula = option.getValue();
        try {
            LTLFormula ltlFormula = new LTLFormula(formula);
            ltlFormula.toPNF();
            modelCheckingSettings.addFormula(ltlFormula);
            logger.info("model-checking: " + formula);
        } catch (Exception e) {
            logger.error("The input " + formula + " is not a valid LTL formula. Skipping it.");
        }
    }

    private void noGarbageCollector() {

        logger.info("disabled garbage collector");
        scene().options().setGarbageCollectionEnabled(false);
    }

    private void maxStateSpace(Option option) {

        int size = Integer.valueOf(option.getValue());
        logger.info("maximal state space size: " + size);
        scene().options().setMaxStateSpace(size);
    }

    private void maxHeap(Option option) {

        int size = Integer.valueOf(option.getValue());
        logger.info("maximal heap size: " + size);
        scene().options().setMaxHeap(size);
    }

    private void export(Option option) {

        String exportPath = option.getValue();
        logger.info("state space will be exported to " + exportPath);
        outputSettings.setExportPath(exportPath);
    }

    private void exportGrammar(Option option) {

        String exportPath = option.getValue();
        logger.info("grammar will be exported to " + exportPath);
        outputSettings.setExportGrammarPath(exportPath);
    }

    private void exportLargeStates(Option option) {

        String exportPath = option.getValue();
        logger.info("large states will be exported to " + exportPath);
        outputSettings.setExportLargeStatesPath(exportPath);
    }

    private void saveContracts(Option option) {
        String path = option.getValue();
        logger.info("contracts will be saved in " + path);
        outputSettings.setSaveContractsPath(path);
    }

    private void exportContracts(Option option) {

        String path = option.getValue();
        logger.info("contracts will be exported to " + path);
        outputSettings.setExportContractsPath(path);
    }

    private void quiet() {
        Configurator.setRootLevel(Level.OFF);
    }

    private void verbose() {
        Configurator.setRootLevel(Level.INFO);
    }

    private void debug() {
        Configurator.setRootLevel(Level.DEBUG);
    }

    private void christoph() {
        logHighlight("Welcome Christoph :-)");
        Configurator.setRootLevel(Level.TRACE);
    }

    @Override
    public void logSummary() {
        logSum("Analyzed method: " + inputSettings.getClassName() + "." + inputSettings.getMethodName());
        String description = inputSettings.getDescription();
        if(description != null) {
            logSum("Scenario: " + description);
        }
    }

    @Override
    public boolean isVerificationPhase() {
        return false;
    }

    @Override
    public InputSettings getInputSettings() {
        return inputSettings;
    }

    @Override
    public ModelCheckingSettings getMcSettings() {
        return modelCheckingSettings;
    }

    @Override
    public OutputSettings getOutputSettings() {
        return outputSettings;
    }

    @Override
    public AutomatonStateLabelingStrategyBuilder getStrategy() {
        return null;
    }
}
