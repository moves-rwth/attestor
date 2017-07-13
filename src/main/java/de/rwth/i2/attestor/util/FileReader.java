package de.rwth.i2.attestor.util;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Writes the whole content of a given file into a string.
 *
 * @author Christoph
 */
public class FileReader {

    public static String read(String filename) throws FileNotFoundException {
        Scanner scan = new Scanner(new java.io.FileReader( filename ) );
        StringBuilder str = new StringBuilder("");
        while (scan.hasNext()) {
            str.append(scan.nextLine());
        }
        scan.close();
        return str.toString();
    }
}
