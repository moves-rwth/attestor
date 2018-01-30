package de.rwth.i2.attestor.phases.symbolicExecution.recursive;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.ProgramImpl;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InstanceInvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullConstant;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.boolExpr.EqualExpr;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.types.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExampleRecursiveProgram extends SceneObject {

    private Type type;
    private String paramName;
    private SelectorLabel next;

    public ExampleRecursiveProgram(SceneObject sceneObject, Type type, String paramName,
                                   SelectorLabel next) {

        super(sceneObject);
        this.type = type;
        this.paramName = paramName;
        this.next = next;
    }

    public Program getRecursiveProgram(Method call){

        List<SemanticsCommand> program = new ArrayList<>();

        //0
        program.add(
                new IdentityStmt(
                        this, 1,
                        new Local(type, "x"),
                        paramName
                )
        );

        Method nextMethod = scene().getOrCreateMethod("next");
        nextMethod.setBody( getNextProgram() );

        InvokeHelper invokePrepare = new InstanceInvokeHelper( this,
                new Local(type,"x"),
                Collections.emptyList()
        );
        //1
        program.add(
                new AssignInvoke(
                        this,
                        new Local(type,"y"),
                        nextMethod,
                        invokePrepare,
                        2)
        );
        Value isNull = new EqualExpr(new Local(type, "y"), new NullConstant());
        //2
        program.add(
                new IfStmt(
                        this,
                        isNull ,
                        3, 4,
                        Collections.emptySet()
                )
        );
        //3
        program.add(
                new ReturnValueStmt(
                        this,
                        new Local(type, "x"),
                        type
                )
        );
        InvokeHelper invokePrepareForTraverse = new InstanceInvokeHelper( this,
                new Local(type,"y"),
                Collections.emptyList()
        );
        //4
        program.add(
                new AssignInvoke(
                        this,
                        new Local(type, "z"),
                        call, invokePrepareForTraverse,
                        5
                )
        );
        //5
        program.add(
                new ReturnValueStmt(
                        this,
                        new Local(type, "z"),
                        type
                )
        );

        return new ProgramImpl(program);
    }

    public Program getNextProgram(){

        List<SemanticsCommand> program = new ArrayList<>();
        program.add(
                new IdentityStmt(
                        this, 1,
                        new Local(type, "x"),
                        "@this:"
                )
        );
        program.add(
                new AssignStmt(
                        this,
                        new Local(type, "x"),
                        new Field(type, new Local(type, "x"), next),
                        2, Collections.emptySet()
                )
        );
        program.add(
                new ReturnValueStmt(
                        this,
                        new Local(type, "x"),
                        type
                )
        );

        return new ProgramImpl(program);
    }

    public Program getCallNextProgram(){

        List<SemanticsCommand> program = new ArrayList<>();

        program.add(
                new IdentityStmt(
                        this, 1,
                        new Local(type, "y"),
                        paramName
                )
        );

        Method nextMethod = scene().getOrCreateMethod("next");
        nextMethod.setBody( getNextProgram() );
        InvokeHelper invokePrepare = new InstanceInvokeHelper( this,
                new Local(type,"y"),
                Collections.emptyList()
        );
        program.add(
                new AssignInvoke(
                        this,
                        new Local(type,"y"),
                        nextMethod,
                        invokePrepare,
                        2)
        );
        program.add(
                new ReturnValueStmt(
                        this,
                        new Local(type, "y"),
                        type
                )
        );

        return new ProgramImpl(program);
    }


}
