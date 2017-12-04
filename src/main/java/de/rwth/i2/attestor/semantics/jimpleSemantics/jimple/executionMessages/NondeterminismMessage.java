package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.executionMessages;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;

public class NondeterminismMessage {

    private Statement statement;

    public NondeterminismMessage(Statement statement) {

        this.statement = statement;
    }

    public Statement getStatement() {

        return statement;
    }
}
