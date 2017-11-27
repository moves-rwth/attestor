package de.rwth.i2.attestor;

import java.io.File;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.ipa.IpaAbstractMethod;
import de.rwth.i2.attestor.main.settings.Settings;

/**
 * Global configuration of unit tests.
 *
 * @author Christoph
 */
public final class UnitTestGlobalSettings {

    public static void reset() {

        BasicNonterminal.clearExistingNonterminals();
        IpaAbstractMethod.clear();
        Settings.getInstance().resetAllSettings();
    }

    public static String getExportPath(String filename) {

        return "target"
                + File.separator
                + "unit-test-output"
                + File.separator + filename;
    }

}
