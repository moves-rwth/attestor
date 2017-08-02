package de.rwth.i2.attestor.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Writes the whole content of a given file/stream into a string.
 *
 * @author Christoph, Christina
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

    public static String read(InputStream stream){
        java.util.Scanner s = new Scanner(stream);

        return scannerToString(s);
    }

    private static String scannerToString(Scanner scan){
        StringBuilder str = new StringBuilder("");
        while (scan.hasNext()) {
            str.append(scan.nextLine());
        }
        scan.close();
        return str.toString();
    }
}
