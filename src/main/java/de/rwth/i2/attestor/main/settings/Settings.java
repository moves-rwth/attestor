package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.main.environment.DefaultScene;
import de.rwth.i2.attestor.main.environment.Scene;

/**
 * A singleton that stores all global settings for Attestor.
 * The settings themselves are grouped into multiple classes within
 * the same package.
 *
 * @author Christoph, Christina
 */
public class Settings {

	// TODO remove
	private Scene scene;

    /**
     * The single instance of this class.
     */
	private static final Settings instance = new Settings(); // TODO

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
     * Settings customizing how state space are exported.
     */
	private OutputSettings outputSettings;

	/**
	 * Settings customizing whether and how model checking is performed.
	 */
	private ModelCheckingSettings mcSettings;

    /**
     * Initializes with default settings.
     */
	private Settings(){
		scene = new DefaultScene();
		resetAllSettings();
	}

	public void setScene(Scene scene) {
		this.scene = scene;
		resetAllSettings();
	}

    /**
     * Resets all settings to their original default value.
     */
	public void resetAllSettings() {
        inputSettings = new InputSettings();
        outputSettings = new OutputSettings();
		mcSettings = new ModelCheckingSettings();
    }

    /**
     * @return All settings regarding input files.
     */
	public InputSettings input(){
		return inputSettings;
	}

    /**
     * @return All settings to customize how state spaces are exported.
     */
	public OutputSettings output() {
		return outputSettings;
	}

	/**
	 * @return The collection of settings specifying the model checking behaviour.
	 */
	public ModelCheckingSettings modelChecking() {return mcSettings; }

	public void setRootPath( String rootPath ) {
		inputSettings.setRootPath( rootPath );
		outputSettings.setRootPath( rootPath );
	}
}
