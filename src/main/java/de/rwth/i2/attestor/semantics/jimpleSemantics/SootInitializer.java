package de.rwth.i2.attestor.semantics.jimpleSemantics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soot.Scene;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Takes care of the necessary soot initialisations for parsing.
 * This includes setting the soot classpath and the main method of the analysed program.
 *
 * @author Christina
 */
public class SootInitializer {

    private static final Logger logger = LogManager.getLogger("Soot");
    private final String javaClassPath;


    public SootInitializer() {

        javaClassPath = defaultJavaClassPath();
    }

    /**
     * This method determines the path to the Java runtime scene.
     * <p>
     * Note that this is a workaround since Soot determines a wrong Java classpath on certain versions of MacOS.
     *
     * @return the path to the runtime scene
     */
    private static String defaultJavaClassPath() {

        StringBuilder sb = new StringBuilder();

        File rtJar = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar");
        if (rtJar.exists() && rtJar.isFile()) {
            // G.v().out.println("Using JRE runtime: " + rtJar.getAbsolutePath());
            sb.append(rtJar.getAbsolutePath());
        } else {
            // in case we're not in JRE scene, try JDK
            rtJar = new File(System.getProperty("java.home") + File.separator + "jre" + File.separator + "lib" + File.separator + "rt.jar");
            if (rtJar.exists() && rtJar.isFile()) {
                // G.v().out.println("Using JDK runtime: " + rtJar.getAbsolutePath());
                sb.append(rtJar.getAbsolutePath());
            } else if (System.getProperty("os.name").equals("Mac OS X")) {
                //in older Mac OS X versions, rt.jar was split into classes.jar and ui.jar
                sb.append(System.getProperty("java.home"));
                sb.append(File.separator);
                sb.append("..");
                sb.append(File.separator);
                sb.append("Classes");
                sb.append(File.separator);
                sb.append("classes.jar");

                sb.append(File.pathSeparator);
                sb.append(System.getProperty("java.home"));
                sb.append(File.separator);
                sb.append("..");
                sb.append(File.separator);
                sb.append("Classes");
                sb.append(File.separator);
                sb.append("ui.jar");
                sb.append(File.pathSeparator);
            } else {
                // not in JDK either
                throw new RuntimeException("Error: cannot find rt.jar.");
            }
        }

        return sb.toString();
    }

    /**
     * This method initializes Soot with the program to be analysed.
     *
     * @param classpath the classpath to the class containing the program to be analysed
     */
    public void initialize(String classpath) {

        soot.G.reset();

        // ensure windows compatibility
        try {
            classpath = URLDecoder.decode(classpath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }

        //String sootClassPath = Scene.v().defaultClassPath() + File.pathSeparator + classpath;
        // Workaround: fixes MacOS Soot problem where the java classpath is not detected correctly
        String sootClassPath = javaClassPath + File.pathSeparator + classpath;

        logger.debug("Setting classpath for soot: " + sootClassPath);

        Scene.v().setSootClassPath(sootClassPath);

    }

}
