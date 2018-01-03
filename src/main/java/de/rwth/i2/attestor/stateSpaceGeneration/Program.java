package de.rwth.i2.attestor.stateSpaceGeneration;

public interface Program {

    /**
     * Provides the program statement for a given program counter
     * @param programCounter A program counter.
     * @return The programs statement corresponding to the given program counter.
     */
    SemanticsCommand getStatement(int programCounter);

    /**
     *
     * @param programCounter
     * @return
     */
    int countPredecessors(int programCounter);

}
