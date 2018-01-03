package de.rwth.i2.attestor.ipa;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.ipa.methods.Method;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InstanceInvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.NullConstant;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.boolExpr.EqualExpr;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.ProgramImpl;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;


public class InterproceduralAnalysisManagerTest {

    private SceneObject sceneObject;
    SelectorLabel next; 
    

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        next = sceneObject.scene().getSelectorLabel("next");
    }

	@Test
	@Ignore
	public void testNonRecursive() throws StateSpaceGenerationAbortedException {
		Type type = sceneObject.scene().getType("List");
		
		Method mainMethod = sceneObject.scene().getMethod("main");
		final String paramName = "@this";
		mainMethod.setBody( getCallNextProgram(type, paramName) );
		
		final int startPos = 0;
		
		HeapConfiguration input = exampleList(type, paramName, startPos);
		ProgramState inputState = sceneObject.scene().createProgramState(input);

		InterproceduralAnalysisManager manager = sceneObject.scene().recursionManager();
		manager.registerToCompute(mainMethod, inputState);
		StateSpace stateSpace = manager.computeFixpoint( mainMethod, inputState );
		
		Set<ProgramState> result = stateSpace.getFinalStates();
		final HeapConfiguration expectedHeap = exampleList(type,"@return",startPos+1);
		final ProgramState expectedState = new DefaultProgramState(expectedHeap).prepareHeap();

		assertEquals( 1, result.size() );
		assertEquals(expectedState.getHeap(), result.iterator().next().getHeap());
	}
	
	@Test
	@Ignore
	public void testRecursive() throws StateSpaceGenerationAbortedException {
		Type type = sceneObject.scene().getType("List");
		
		final String paramName = "@this:";
		Method traverseMethod = sceneObject.scene().getMethod("traverse");
		traverseMethod.setRecursive(true);
		traverseMethod.setBody(getRecursiveProgram(type, paramName, traverseMethod) );
		
		
		
		
		final int startPos = 0;
		
		HeapConfiguration input = exampleList(type, paramName, startPos);
		ProgramState inputState = new DefaultProgramState(input).prepareHeap();
		
		InterproceduralAnalysisManager manager = sceneObject.scene().recursionManager();
		StateSpace stateSpace = manager.computeFixpoint( traverseMethod, inputState);
		
		Set<ProgramState> result = stateSpace.getFinalStates();
		final HeapConfiguration expectedHeap = exampleList(type,"@return",2);
		final ProgramState expectedState = new DefaultProgramState(expectedHeap).prepareHeap();
		
		assertEquals( 1, result.size() );
		assertEquals(expectedState.getHeap(), result.iterator().next().getHeap());
	}
	
	private Program getNextProgram(Type type ){
	
       List<SemanticsCommand> program = new ArrayList<>();
        		program.add(
        				new IdentityStmt(
        						sceneObject, 1, 
        						new Local(type, "x"),
        						"@this:"
        						)
        		);
        		program.add(
                        new AssignStmt(
                                sceneObject,
                                new Local(type, "x"),
                                new Field(type, new Local(type, "x"), next),
                                2, Collections.emptySet()
                        )
                );
        		program.add(
                		new ReturnValueStmt(
                				sceneObject, 
                				new Local(type, "x"), 
                				type
                		)
                );
        		
        	return new ProgramImpl(program);
	}
	

	private Program getCallNextProgram( Type type, String paramName ){
   
		List<SemanticsCommand> program = new ArrayList<>();
		
		program.add(
        				new IdentityStmt(
        						sceneObject, 1, 
        						new Local(type, "y"),
        						paramName
        						)
        		);

		 Method nextMethod = sceneObject.scene().getMethod("next");
		 nextMethod.setBody(getNextProgram(type));
		 InvokeHelper invokePrepare = new InstanceInvokeHelper( sceneObject,
																	new Local(type,"y"), 
																	Collections.emptyList()
																);
		program.add(
				new AssignInvoke(
						sceneObject, 
						new Local(type,"y"), 
						nextMethod, 
						invokePrepare, 
						2)
				);
		program.add(
                		new ReturnValueStmt(
                				sceneObject, 
                				new Local(type, "y"), 
                				type
                		)
                );
		
		return new ProgramImpl(program);
	}
	
	private Program getRecursiveProgram(Type type, String paramName, Method call ){
		   
		List<SemanticsCommand> program = new ArrayList<>();
		
		//0
		program.add(
        				new IdentityStmt(
        						sceneObject, 1, 
        						new Local(type, "x"),
        						paramName
        						)
        		);
		
	     Method nextMethod = sceneObject.scene().getMethod("next");
	     nextMethod.setBody( getNextProgram(type) );
		 InvokeHelper invokePrepare = new InstanceInvokeHelper( sceneObject,
																	new Local(type,"x"), 
																	Collections.emptyList()
																);
		//1
		program.add(
				new AssignInvoke(
						sceneObject, 
						new Local(type,"y"), 
						nextMethod, 
						invokePrepare, 
						2)
				);
		Value isNull = new EqualExpr(new Local(type, "y"), new NullConstant());
		//2
		program.add(
				new IfStmt(
						sceneObject, 
						isNull ,
						3, 4, 
						Collections.emptySet()
					)
				);
		//3
		program.add(
                		new ReturnValueStmt(
                				sceneObject, 
                				new Local(type, "x"), 
                				type
                		)
                );
		InvokeHelper invokePrepareForTraverse = new InstanceInvokeHelper( sceneObject, 
				new Local(type,"y"), 
				Collections.emptyList()
			);
		//4
		program.add(
				new AssignInvoke(
						sceneObject, 
						new Local(type, "z"), 
						call, invokePrepareForTraverse,
						5
						)
				);
		//5
		program.add(
        		new ReturnValueStmt(
        				sceneObject, 
        				new Local(type, "z"), 
        				type
        		)
        );
		
		
		return new ProgramImpl(program);
	}
	
	private HeapConfiguration exampleList(Type type, String varName, int varPos){
		Type nullType = sceneObject.scene().getType("NULL");
		HeapConfiguration hc = new InternalHeapConfiguration();
		
		type.addSelectorLabel(next, Constants.NULL);
		
		TIntArrayList nodes = new TIntArrayList();
		return hc.builder()
				.addNodes(type, 3, nodes)
				.addNodes(nullType, 1, nodes)
				.addSelector(nodes.get(0), next, nodes.get(1))
				.addSelector(nodes.get(1), next, nodes.get(2))
				.addSelector(nodes.get(2), next, nodes.get(3))
				.addVariableEdge("null", nodes.get(3))
				.addVariableEdge(varName, nodes.get(varPos))
				.build();
				
	}

}
