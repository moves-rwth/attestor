package de.rwth.i2.attestor.strategies;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.GeneralConcreteValue;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * A general implementation of program states that comprises most functionality when analyzing Jimple programs.
 *
 * @author Christoph
 */
public abstract class GeneralJimpleProgramState implements JimpleProgramState {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger( "GeneralJimpleProgramState" );

    /**
     * The heap configuration that determines the shape of the heap and the assignment of
     * program variables underlying this program state.
     */
	protected HeapConfiguration heap;

    /**
     * The program location corresponding to this program state.
     */
	protected int programCounter;

    /**
     * The depth of the procedure call scope for this program state.
     */
	protected int scopeDepth;

	/**
	 * Id of this state in a state space
	 */
	private int stateSpaceId = -1;

    /**
     * The atomic propositions assigned to this state.
     */
    protected HashSet<String> atomicPropositions;

    /**
     * Initializes a state with the initial program location and scope depth 0.
     * @param heap The initial heap configuration.
     */
	protected GeneralJimpleProgramState(HeapConfiguration heap) {
		
		this.heap = heap;
		atomicPropositions = new HashSet<>();
	}

    /**
     * Initializes a state with the initial program location 0.
     * @param heap The initial heap configuration.
     * @param scopeDepth The initial call scope depth.
     */
	protected GeneralJimpleProgramState(HeapConfiguration heap, int scopeDepth) {
		
		this.heap = heap;
		this.scopeDepth = scopeDepth;
		atomicPropositions = new HashSet<>();
	}

    /**
     * Creates a shallow copy of a program state.
     * @param state The state that should be copied.
     */
	protected GeneralJimpleProgramState(GeneralJimpleProgramState state) {
		
		this.heap = state.heap;
		this.programCounter = state.programCounter;
		this.scopeDepth = state.scopeDepth;
		atomicPropositions = new HashSet<>(state.getAPs());
	}

    /**
     * @return A deep copy of this program state.
     */
    public abstract GeneralJimpleProgramState clone();

    /**
     * @param name The name of a variable or constant.
     * @return The corresponding name in the current scope of this state.
     */
    private String getScopedName(String name) {

        return String.valueOf(scopeDepth) + "-" + name;
    }

    /**
     * Creates a human readable string representation for debugging purposes.
     * @return A string representation of this program state.
     */
    public String toString() {

        return "ssid: " + String.valueOf(stateSpaceId) + "\npc: " + String.valueOf(programCounter) + "\nscope: " + String.valueOf(scopeDepth) + "\n" + heap;
    }

    /**
     * @return A hash code corresponding to this state.
     */
    public int hashCode() {

        return heap.hashCode();
    }

    /**
     * @return The depth of the call scope of this state.
     */
    public int getScopeDepth(){

        return this.scopeDepth;
    }

    /**
     * @return The size of this program state, i.e. the number of
     *         nodes of the underlying heap configuration.
     */
    public int size() {

        return heap.countNodes();
    }


	@Override
	public int getProgramCounter() {
		
		return programCounter;
	}

	@Override
	public void setProgramCounter(int pc) {
		
		programCounter = pc;
		
	}

	@Override
	public HeapConfiguration getHeap() {
		
		return heap;
	}


	@Override
	public ProgramState prepareHeap() {
		
		HeapConfiguration copy = heap.clone();
		
		TIntIterator varIter = heap.variableEdges().iterator();
		while(varIter.hasNext()) {
			int var = varIter.next();
			
			String name = heap.nameOf(var);

			// we do not scope already scoped variables.
			try {
                String[] scope = name.split("-");
                if (scope.length > 1 && Integer.valueOf(scope[0]) >= 0) {
                    continue;
                }
            } catch(NumberFormatException e) {
			    // intended
            }

			if(!Constants.isConstant(name) && !name.startsWith("@")) {
				
				int target = heap.targetOf(var);
				copy.builder()
					.removeVariableEdge(var)
					.addVariableEdge(getScopedName(name), target)
					.build();
			}
		}
		
		Type booleanType =  Settings.getInstance().factory().getType( "int" );
		Type nullType = Settings.getInstance().factory().getType( "NULL" );
		
		int trueNode;

	    if(heap.variableWith(Constants.TRUE) == HeapConfiguration.INVALID_ELEMENT) {
	    	if( heap.variableWith( Constants.ONE) == HeapConfiguration.INVALID_ELEMENT ){
	    		
	    		TIntArrayList nodes = new TIntArrayList(1);
	    		copy.builder()
	    			.addNodes(booleanType, 1, nodes)
	    			.addVariableEdge(Constants.ONE, nodes.get(0))
	    			.build();
	    		trueNode = nodes.get(0);
	    	}else{
	    		trueNode = heap.targetOf(heap.variableWith( Constants.ONE ));
	    	}
			copy.builder().addVariableEdge(Constants.TRUE, trueNode).build();
	    }else{
	    	trueNode = heap.targetOf(heap.variableWith( Constants.TRUE ));
	    	if( heap.variableWith( Constants.ONE ) == HeapConfiguration.INVALID_ELEMENT ){
	    		copy.builder().addVariableEdge(Constants.ONE, trueNode).build();
	    	}else if(heap.targetOf(heap.variableWith( Constants.ONE )) != trueNode ){
	    		logger.warn( "true and 1 do not point to the same node" );
	    	}
	    }
	    
	    if(heap.variableWith(Constants.FALSE) == HeapConfiguration.INVALID_ELEMENT) {
	    	if( heap.variableWith(Constants.ZERO) == HeapConfiguration.INVALID_ELEMENT ){
	    		
	    		TIntArrayList nodes = new TIntArrayList(1);
	    		copy.builder()
	    			.addNodes(booleanType, 1, nodes)
	    			.addVariableEdge(Constants.ZERO, nodes.get(0))
	    			.build();
	    		trueNode = nodes.get(0);
	    	}else{
	    		trueNode = heap.targetOf(heap.variableWith(Constants.ZERO));
	    	}
			copy.builder().addVariableEdge(Constants.FALSE, trueNode).build();
	    }else{
	    	trueNode = heap.targetOf(heap.variableWith(Constants.FALSE ));
	    	if( heap.variableWith(Constants.ZERO) == HeapConfiguration.INVALID_ELEMENT ){
	    		copy.builder().addVariableEdge(Constants.ZERO, trueNode).build();
	    	}else if(heap.targetOf(heap.variableWith( Constants.ZERO )) != trueNode ){
	    		logger.warn( "false and 0 do not point to the same node" );
	    	}
	    }
	  
	    
	    if(heap.variableWith(Constants.NULL) == HeapConfiguration.INVALID_ELEMENT) {
	    	
	    	TIntArrayList nodes = new TIntArrayList(1);
	    	copy.builder()
	    		.addNodes(nullType, 1, nodes)
	    		.addVariableEdge(Constants.NULL, nodes.get(0))
	    		.build();
	    }
		
		this.heap = copy;
		return this;
	}
	

	@Override
	public String getVariableNameInHeap( String originalVariableName){
		return getScopedName(originalVariableName);
	}
	

	@Override
	public void enterScope() {
		
		++this.scopeDepth;
	}

	@Override
	public void leaveScope() {
		
		--this.scopeDepth;
	}

	@Override
	public GeneralConcreteValue getVariableTarget(String variableName) {
		
		variableName = getScopedName(variableName);
		
		try {
			
			int node = heap.targetOf( heap.variableWith(variableName) );
			Type type = heap.nodeTypeOf(node);
			return new GeneralConcreteValue( type, node );
		} catch( NullPointerException | IllegalArgumentException e ) {
			//logger.trace("Variable " + variableName + " could not be found. Returning undefined.");
			return GeneralConcreteValue.getUndefined();
		}
	}

	@Override
	public void removeVariable(String variableName) {
		
		if(Constants.isConstant(variableName)) {
			logger.error("Illegal attempt to remove constant " + variableName);
			return;
		}

		variableName = getScopedName(variableName);
		int varEdge = heap.variableWith(variableName);
		
		try {
			heap.builder().removeVariableEdge(varEdge).build();
		} catch(IllegalArgumentException e) {
			//logger.trace("Variable '" + variableName + "' could not be removed as it does not exist.");
		}
	}

	@Override
	public void setVariable(String variableName, ConcreteValue value) {
		
		if(Constants.isConstant(variableName)) {
			logger.error("Illegal attempt to set value of constant " + variableName);
			return;
		}
		
		if(value instanceof GeneralConcreteValue) {
			
			variableName = getScopedName(variableName);
			
			GeneralConcreteValue v = (GeneralConcreteValue) value;
			
			int node = v.getNode();
			if(node == GeneralConcreteValue.UNDEFINED) {
					logger.debug("Aborting setVariable as the new target '"
							+ v.toString()
							+ "' for the following variable could be found: '"
							+ variableName
							+ "'"
					);
				return;
			}
			
			int variable = heap.variableWith(variableName);
			
			if(variable != HeapConfiguration.INVALID_ELEMENT) {
				heap.builder().removeVariableEdge(variable);
			}
			heap.builder()
				.addVariableEdge(variableName, node)
				.build();
			
		} else {
			
			logger.error("Received value of illegal type.");
		}
	}


	@Override
	public GeneralConcreteValue getConstant(String constantName) {

		try {
			
			int n = heap.targetOf(heap.variableWith(constantName));
			Type t = heap.nodeTypeOf(n);
			
			return new GeneralConcreteValue( t, n );
		} catch( NullPointerException | IllegalArgumentException e ) {
			
			logger.warn("Constant '" + constantName + "' not found. Returning undefined.");
			return GeneralConcreteValue.getUndefined();
		}
	}

	/**
	 * Intermediates are internally equal to variables. <br>
	 * Name clashes do not happen since they start with an '@'
	 * while variables have to start with a letter.
	 */
	@Override
	public GeneralConcreteValue removeIntermediate(String name) {
		
		HeapConfigurationBuilder builder = heap.builder();
		
		try {
			
			int var = heap.variableWith(name);
			int res = heap.targetOf(var);
			builder.removeVariableEdge(var);
			
			builder.build();
			
			Type type = heap.nodeTypeOf(res);
			return new GeneralConcreteValue(type, res);
			
		} catch( NullPointerException | IllegalArgumentException e ) {
			
			builder.build();
			return GeneralConcreteValue.getUndefined();
		}
	}

	/**
	 * Intermediates are internally equal to variables. <br>
	 * Name clashes do not happen since they start with an '@'
	 * while variables have to start with a letter.
	 */
	@Override
	public void setIntermediate(String name, ConcreteValue value) {
		
		HeapConfigurationBuilder builder = heap.builder();
		int oldVar = heap.variableWith(name);
		
		if(oldVar != HeapConfiguration.INVALID_ELEMENT) {
			
			builder.removeVariableEdge(oldVar);
		}
		
		int node = ((GeneralConcreteValue) value).getNode();
		builder.addVariableEdge( name, node ).build();
	}

	@Override
	public GeneralConcreteValue insertNewElement(Type type) {
		
		HeapConfigurationBuilder builder = heap.builder();
		
		try {
			
			TIntArrayList nodes = new TIntArrayList(1);
			builder.addNodes(type, 1, nodes);
			
			GeneralConcreteValue res = new GeneralConcreteValue( type,  nodes.get(0) );

			builder.build();
			return res;
		} catch( ClassCastException e ) {
			
			logger.error("GeneralJimpleProgramState expects NodeTypes as types.");
			builder.build();
			return null;
		}
	}

	@Override
	public GeneralConcreteValue getUndefined() {
		
		return GeneralConcreteValue.getUndefined();
	}
	
	@Override
	public ProgramState shallowCopyWithUpdateHeap(HeapConfiguration newHeap) {
		GeneralJimpleProgramState copy = (GeneralJimpleProgramState) shallowCopy();
		copy.heap = newHeap;
		return copy;
	}

	@Override
	public boolean satisfiesAP(String ap) {
		return atomicPropositions.contains(ap);
	}

	@Override
	public void addAP(String ap) {
		atomicPropositions.add(ap);
	}

	@Override
	public Set<String> getAPs() {
		return atomicPropositions;
	}

	@Override
	public int getSize() {
		return heap.countNodes();
	}

	@Override
	public void setStateSpaceId(int id) {

		stateSpaceId = id;
	}

	@Override
	public int getStateSpaceId() {

		return stateSpaceId;
	}
}
