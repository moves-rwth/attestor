package de.rwth.i2.attestor.semantics;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramImpl;

/**
 * ProgramParsers specify how a source code file is translated into a
 * {@link ProgramImpl} that can be analyzed.
 *
 * @author Hannah Arndt, Christoph
 */
public interface ProgramParser {

    /**
     * Transforms a source code file into a ProgramImpl that can be analyzed.
     *
     * @param classpath The path to a source code file
     * @param classname The name of the class to transform into a ProgramImpl.
     * @return The program generated from the source code file.
     */
    ProgramImpl parse(String classpath, String classname);

    /**
     * Transforms a source code file into a ProgramImpl that can be analyzed.
     *
     * @param classpath  The path to a source code file
     * @param classname  The name of the class to transform into a ProgramImpl.
     * @param entryPoint The initial method that is invoked on running the parsed program.
     * @return The program generated from the source code file.
     */
    ProgramImpl parse(String classpath, String classname, String entryPoint);

}
