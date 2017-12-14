package de.rwth.i2.attestor.ipa;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.boolExpr.EqualExpr;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;


public class InterproceduralAnalysisManagerTest {

    private SceneObject sceneObject;
    SelectorLabel next; 
    

    @Before
    public void setUp() {

        sceneObject = new MockupSceneObject();
        next = sceneObject.scene().getSelectorLabel("next");
    }

	@Test
	public void testNonRecursive() throws StateSpaceGenerationAbortedException {
		Type type = sceneObject.scene().getType("List");
		
		IpaAbstractMethod mainMethod = new IpaAbstractMethod( sceneObject, "main");
		final String paramName = "@this";
		mainMethod.setControlFlow( getCallNextProgram(type, paramName) );
		
		final int startPos = 0;
		
		HeapConfiguration input = exampleList(type, paramName, startPos);
		ProgramState inputState = new DefaultProgramState(input).prepareHeap();
		
		InterproceduralAnalysisManager manager = sceneObject.scene().recursionManager();
		manager.registerToCompute(mainMethod, inputState);
		final MockupSymbolicExecutionObserver observer = new MockupSymbolicExecutionObserver(sceneObject);
		manager.computeFixpoints( observer );
		
		Set<ProgramState> result = mainMethod.getFinalStates(inputState, inputState, observer);
		final HeapConfiguration expectedHeap = exampleList(type,"@return",startPos+1);
		final ProgramState expectedState = new DefaultProgramState(expectedHeap).prepareHeap();
		assertThat( result, contains(expectedState));
	}
	
	@Test
	public void testRecursive() throws StateSpaceGenerationAbortedException {
		Type type = sceneObject.scene().getType("List");
		
		IpaAbstractMethod traverseMethod = new IpaAbstractMethod( sceneObject, "main");
		final String paramName = "@this";
		traverseMethod.setControlFlow( getRecursiveProgram(type, paramName, traverseMethod) );
		traverseMethod.markAsRecursive();
		
		final int startPos = 0;
		
		HeapConfiguration input = exampleList(type, paramName, startPos);
		ProgramState inputState = new DefaultProgramState(input).prepareHeap();
		
		InterproceduralAnalysisManager manager = sceneObject.scene().recursionManager();
		manager.registerToCompute(traverseMethod, inputState);
		final MockupSymbolicExecutionObserver observer = new MockupSymbolicExecutionObserver(sceneObject);
		manager.computeFixpoints( observer );
		
		Set<ProgramState> result = traverseMethod.getFinalStates(inputState, inputState, observer);
		final HeapConfiguration expectedHeap = exampleList(type,"@return",2);
		final ProgramState expectedState = new DefaultProgramState(expectedHeap).prepareHeap();
		assertThat( result, contains(expectedState));
	}
	
	private List<Semantics> getNextProgram( Type type ){
	
       List<Semantics> program = new ArrayList<>();
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
        		
        	return program;
	}
	

	private List<Semantics> getCallNextProgram( Type type, String paramName ){
   
		List<Semantics> program = new ArrayList<>();
		
		program.add(
        				new IdentityStmt(
        						sceneObject, 1, 
        						new Local(type, "y"),
        						paramName
        						)
        		);
		
	     AbstractMethod nextMethod = new IpaAbstractMethod(sceneObject, "next");
	        nextMethod.setControlFlow( getNextProgram(type) );
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
		
		return program;
	}
	
	private List<Semantics> getRecursiveProgram( Type type, String paramName, AbstractMethod call ){
		   
		List<Semantics> program = new ArrayList<>();
		
		//0
		program.add(
        				new IdentityStmt(
        						sceneObject, 1, 
        						new Local(type, "x"),
        						paramName
        						)
        		);
		
	     AbstractMethod nextMethod = new IpaAbstractMethod(sceneObject, "next");
	        nextMethod.setControlFlow( getNextProgram(type) );
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
		
		
		return program;
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
