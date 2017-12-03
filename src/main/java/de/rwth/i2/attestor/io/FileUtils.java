package de.rwth.i2.attestor.io;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static void createDirectories(String path) throws IOException {

        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            boolean success = (new File(path)).mkdirs();
            if (!success) {
                throw new IOException("Unable to generate directory: " + path);
            }
        }
    }
}
