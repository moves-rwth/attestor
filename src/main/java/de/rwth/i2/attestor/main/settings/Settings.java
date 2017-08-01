package de.rwth.i2.attestor.main.settings;

/**
 * A singleton that stores all global settings for Attestor.
 * The settings themselves are grouped into multiple classes within
 * the same package.
 *
 * @author Christoph
 */
public class Settings {

    /**
     * The single instance of this class.
     */
	private static final Settings instance = new Settings();

    /**
     * @return The unique instance of this class.
     */
	public static Settings getInstance() {
		return instance;
	}
	

    /**
     * Settings regarding input files.
     */
	private InputSettings inputSettings;

    /**
     * Settings customizing the analysis that is executed.
     */
	private OptionSettings optionSettings;

    /**
     * Settings customizing how state space are exported.
     */
	private OutputSettings outputSettings;

    /**
     * Settings regarding the creation of objects.
     */
	private FactorySettings factorySettings;

    /**
     * Settings regarding the graph grammars underlying the analysis.
     */
	private GrammarSettings grammarSettings;

    /**
     * Initializes with default settings.
     */
	private Settings(){
	    resetAllSettings();
	}

    /**
     * Resets all settings to their original default value.
     */
	public void resetAllSettings() {
        inputSettings = new InputSettings();
        optionSettings = new OptionSettings();
        outputSettings = new OutputSettings();
        factorySettings = new FactorySettings();
        grammarSettings = new GrammarSettings();
    }

    /**
     * @return All settings regarding input files.
     */
	public InputSettings input(){
		return inputSettings;
	}

    /**
     * @return All settings to customize the execution of the analysis.
     */
	public OptionSettings options(){
		return optionSettings;
	}

    /**
     * @return All settings to customize how state spaces are exported.
     */
	public OutputSettings output() {
		return outputSettings;
	}

    /**
     * @return A factory to create (customized) objects.
     */
	public FactorySettings factory() {
		return factorySettings;
	}

    /**
     * @return All settings regarding the graph grammar underlying the analysis.
     */
	public GrammarSettings grammar() {
	    return grammarSettings;
	}

	public void setRootPath(String rootPath ) {
		inputSettings.setRootPath( rootPath );
		outputSettings.setRootPath( rootPath );
	}
}
