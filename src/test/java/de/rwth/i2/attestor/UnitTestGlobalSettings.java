package de.rwth.i2.attestor;

import java.io.File;

/**
 * Global configuration of unit tests.
 *
 * @author Christoph
 */
public final class UnitTestGlobalSettings {

    public static String getExportPath(String filename) {

        return "target"
                + File.separator
                + "unit-test-output"
                + File.separator + filename;
    }

}
