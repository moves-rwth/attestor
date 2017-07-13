package de.rwth.i2.attestor;

import java.io.File;

/**
 * Global configuration of integration tests.
 *
 * @author Christoph
 */
public final class IntegrationTestGlobalSettings {

    public static String getExportPath(String filename) {

        return "target"
                + File.separator
                + "integration-test-output"
                + File.separator + filename;
    }

}
