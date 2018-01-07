package de.rwth.i2.attestor.ipa;

public class InterproceduralAnalysisManagerTest {

	/*
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
		StateSpace stateSpace = manager.computeFixpoint( mainMethod, inputState, observer );
		
		Set<ProgramState> result = stateSpace.getFinalStates();
		final HeapConfiguration expectedHeap = exampleList(type,"@return",startPos+1);
		final ProgramState expectedState = new DefaultProgramState(expectedHeap).prepareHeap();

		assertEquals( 1, result.size() );
		assertEquals(expectedState.getHeap(), result.iterator().next().getHeap());
	}
	
	@Test
	public void testRecursive() throws StateSpaceGenerationAbortedException {
		Type type = sceneObject.scene().getType("List");
		
		final String paramName = "@this:";
		IpaAbstractMethod traverseMethod = new IpaAbstractMethod(sceneObject, "traverse"); // TODO sceneObject.scene().getMethod("traverse");
		traverseMethod.markAsRecursive();
		traverseMethod.setControlFlow(getRecursiveProgram(type, paramName, traverseMethod) );
		
		
		
		
		final int startPos = 0;
		
		HeapConfiguration input = exampleList(type, paramName, startPos);
		ProgramState inputState = new DefaultProgramState(input).prepareHeap();
		
		InterproceduralAnalysisManager manager = sceneObject.scene().recursionManager();
		final MockupSymbolicExecutionObserver observer = new MockupSymbolicExecutionObserver(sceneObject);
		StateSpace stateSpace = manager.computeFixpoint( traverseMethod, inputState, observer );
		
		Set<ProgramState> result = stateSpace.getFinalStates();
		final HeapConfiguration expectedHeap = exampleList(type,"@return",2);
		final ProgramState expectedState = new DefaultProgramState(expectedHeap).prepareHeap();
		
		assertEquals( 1, result.size() );
		assertEquals(expectedState.getHeap(), result.iterator().next().getHeap());
	}
	
	private List<SemanticsCommand> getNextProgram(Type type ){
	
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
        		
        	return program;
	}
	

	private List<SemanticsCommand> getCallNextProgram( Type type, String paramName ){
   
		List<SemanticsCommand> program = new ArrayList<>();
		
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
	
	private List<SemanticsCommand> getRecursiveProgram( Type type, String paramName, AbstractMethod call ){
		   
		List<SemanticsCommand> program = new ArrayList<>();
		
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
	*/

}
