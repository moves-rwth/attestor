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

import java.util.Iterator;

public class CommandLinePhase extends AbstractPhase
        implements InputSettingsTransformer, OutputSettingsTransformer,
        MCSettingsTransformer, StateLabelingStrategyBuilderTransformer {

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
                help();
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
            default:
                throw new IllegalArgumentException("Unknown command line option: " + optionName);
        }

    }

    private void description(Option option) {

        inputSettings.setDescription(option.getValue());
    }

    private void help() {

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

        inputSettings.addInitialHeapFile(option.getValue());
    }

    private void method(Option option) {

        inputSettings.setMethodName(option.getValue());
    }

    private void predefinedGrammar(Option option) {

        String grammarName = option.getValue();
        if(grammarName == null) {
            throw new IllegalArgumentException("Unspecified grammar name");
        }
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

        scene().abstractionOptions().setAdmissibleAbstractionEnabled(true);
    }

    private void admissibleConstants() {

        scene().abstractionOptions().setAdmissibleConstantsEnabled(true);
    }

    private void admissibleMarkings() {

        scene().abstractionOptions().setAdmissibleMarkingsEnabled(true);
    }

    private void admissibleFull() {

        scene().abstractionOptions().setAdmissibleFullEnabled(true);
    }

    private void noChainAbstraction() {

        scene().abstractionOptions().setNoChainAbstractionEnabled(true);
    }

    private void noRuleCollapsing() {

        scene().abstractionOptions().setNoRuleCollapsingEnabled(true);
    }

    private void indexed() {

        scene().abstractionOptions().setIndexedModeEnabled(true);
    }

    private void postProcessing() {

        scene().abstractionOptions().setPostProcessingEnabled(true);
    }

    private void canonical() {

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

    private void noGarbageCollector() {

        scene().abstractionOptions().setGarbageCollectionEnabled(false);
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
