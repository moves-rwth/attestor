package de.rwth.i2.attestor.programState;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.GeneralConcreteValue;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import gnu.trove.list.array.TIntArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A general implementation of program states that comprises most functionality when analyzing Jimple programs.
 *
 * @author Christoph
 */
public abstract class GeneralProgramState implements ProgramState {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger("GeneralProgramState");
    /**
     * The atomic propositions assigned to this state.
     */
    private final Set<String> atomicPropositions;
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
     * Id of this state in a state space
     */
    private int stateSpaceId = -1;

    private StateSpace containingStateSpace = null;
    
    /**
     * A flag to indicate whether this state is just continued (in a partial stateSpace)
     * This is necessary, as the successors of continued states always have to be merged
     * to avoid duplicate applications of the same contract
     */
    boolean isContinueState = false;

    /**
     * Initializes a state with the initial program location 0.
     *
     * @param heap The initial heap configuration.
     */
    protected GeneralProgramState(HeapConfiguration heap) {

        this.heap = heap;
        atomicPropositions = new LinkedHashSet<>();
    }

    /**
     * Creates a shallow copy of a program state.
     *
     * @param state The state that should be copied.
     */
    protected GeneralProgramState(GeneralProgramState state) {

        this.heap = state.heap;
        this.programCounter = state.programCounter;
        atomicPropositions = new LinkedHashSet<>(state.getAPs());
    }

    /**
     * @return A deep copy of this program state.
     */
    public abstract GeneralProgramState clone();

    /**
     * Creates a human readable string representation for debugging purposes.
     *
     * @return A string representation of this program state.
     */
    public String toString() {

        return "ssid: " + String.valueOf(stateSpaceId)
                + "\npc: " + String.valueOf(programCounter)
                + "\n" + heap;
    }

    /**
     * @return A hash code corresponding to this state.
     */
    @Override
    public int hashCode() {

        int hash = programCounter;
        hash = (hash << 1) ^ heap.hashCode();
        return hash;
    }

    /**
     * @return The size of this program state, i.e. the number of
     * nodes of the underlying heap configuration.
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

        int trueNode;

        if (heap.variableWith(Constants.TRUE) == HeapConfiguration.INVALID_ELEMENT) {
            if (heap.variableWith(Constants.ONE) == HeapConfiguration.INVALID_ELEMENT) {

                TIntArrayList nodes = new TIntArrayList(1);
                copy.builder()
                        .addNodes(Types.INT, 1, nodes)
                        .addVariableEdge(Constants.ONE, nodes.get(0))
                        .build();
                trueNode = nodes.get(0);
            } else {
                trueNode = heap.targetOf(heap.variableWith(Constants.ONE));
            }
            copy.builder().addVariableEdge(Constants.TRUE, trueNode).build();
        } else {
            trueNode = heap.targetOf(heap.variableWith(Constants.TRUE));
            if (heap.variableWith(Constants.ONE) == HeapConfiguration.INVALID_ELEMENT) {
                copy.builder().addVariableEdge(Constants.ONE, trueNode).build();
            } else if (heap.targetOf(heap.variableWith(Constants.ONE)) != trueNode) {
                logger.warn("true and 1 do not point to the same node");
            }
        }

        if (heap.variableWith(Constants.FALSE) == HeapConfiguration.INVALID_ELEMENT) {
            if (heap.variableWith(Constants.ZERO) == HeapConfiguration.INVALID_ELEMENT) {

                TIntArrayList nodes = new TIntArrayList(1);
                copy.builder()
                        .addNodes(Types.INT, 1, nodes)
                        .addVariableEdge(Constants.ZERO, nodes.get(0))
                        .build();
                trueNode = nodes.get(0);
            } else {
                trueNode = heap.targetOf(heap.variableWith(Constants.ZERO));
            }
            copy.builder().addVariableEdge(Constants.FALSE, trueNode).build();
        } else {
            trueNode = heap.targetOf(heap.variableWith(Constants.FALSE));
            if (heap.variableWith(Constants.ZERO) == HeapConfiguration.INVALID_ELEMENT) {
                copy.builder().addVariableEdge(Constants.ZERO, trueNode).build();
            } else if (heap.targetOf(heap.variableWith(Constants.ZERO)) != trueNode) {
                logger.warn("false and 0 do not point to the same node");
            }
        }


        if (heap.variableWith(Constants.NULL) == HeapConfiguration.INVALID_ELEMENT) {

            TIntArrayList nodes = new TIntArrayList(1);
            copy.builder()
                    .addNodes(Types.NULL, 1, nodes)
                    .addVariableEdge(Constants.NULL, nodes.get(0))
                    .build();
        }

        this.heap = copy;
        return this;
    }

    @Override
    public GeneralConcreteValue getVariableTarget(String variableName) {

        try {

            int node = heap.targetOf(heap.variableWith(variableName));
            Type type = heap.nodeTypeOf(node);
            return new GeneralConcreteValue(type, node);
        } catch (NullPointerException | IllegalArgumentException e) {
            //logger.trace("Variable " + variableName + " could not be found. Returning undefined.");
            return GeneralConcreteValue.getUndefined();
        }
    }

    @Override
    public void removeVariable(String variableName) {

        if (Constants.isConstant(variableName)) {
            logger.error("Illegal attempt to remove constant " + variableName);
            return;
        }

        int varEdge = heap.variableWith(variableName);

        try {
            heap.builder().removeVariableEdge(varEdge).build();
        } catch (IllegalArgumentException e) {
            //logger.trace("Variable '" + variableName + "' could not be removed as it does not exist.");
        }
    }

    @Override
    public void setVariable(String variableName, ConcreteValue value) {

        if (Constants.isConstant(variableName)) {
            logger.error("Illegal attempt to set value of constant " + variableName);
            return;
        }

        if (value instanceof GeneralConcreteValue) {

            GeneralConcreteValue v = (GeneralConcreteValue) value;

            int node = v.getNode();
            if (node == GeneralConcreteValue.UNDEFINED) {
                logger.trace("Aborting setVariable as the new target '"
                        + v.toString()
                        + "' for the following variable could be found: '"
                        + variableName
                        + "'"
                );
                return;
            }

            int variable = heap.variableWith(variableName);

            if (variable != HeapConfiguration.INVALID_ELEMENT) {
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

            return new GeneralConcreteValue(t, n);
        } catch (NullPointerException | IllegalArgumentException e) {

            logger.debug("Constant '" + constantName + "' not found. Will be replaced by undefined value.");
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

        } catch (NullPointerException | IllegalArgumentException e) {

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

        if (oldVar != HeapConfiguration.INVALID_ELEMENT) {

            builder.removeVariableEdge(oldVar);
        }

        int node = ((GeneralConcreteValue) value).getNode();
        builder.addVariableEdge(name, node).build();
    }

    @Override
    public GeneralConcreteValue insertNewElement(Type type) {

        HeapConfigurationBuilder builder = heap.builder();

        try {

            TIntArrayList nodes = new TIntArrayList(1);
            builder.addNodes(type, 1, nodes);
            GeneralConcreteValue res = new GeneralConcreteValue(type, nodes.get(0));

            Map<SelectorLabel, String> selectorToDefaults = type.getSelectorLabels();
            for (Map.Entry<SelectorLabel, String> selectorDefault : selectorToDefaults.entrySet()) {

                SelectorLabel selectorLabel = selectorDefault.getKey();
                if(type.isOptional(selectorLabel)) {
                    continue;
                }
                int target = heap.variableTargetOf(selectorDefault.getValue());
                if (target == HeapConfiguration.INVALID_ELEMENT) {
                    throw new IllegalStateException("default target '" + selectorDefault.getValue() + "' of selector '" + selectorDefault.getKey() + "' not found.");
                } else {
                    builder.addSelector(nodes.get(0), selectorLabel, target);
                }
            }

            builder.build();

            return res;
        } catch (ClassCastException e) {

            logger.error("GeneralProgramState expects NodeTypes as types.");
            builder.build();
            return null;
        }
    }

    @Override
    public GeneralConcreteValue getSelectorTarget(ConcreteValue from, SelectorLabel selectorLabel) {

        if (from != null && from.getClass() == GeneralConcreteValue.class) {

            GeneralConcreteValue dFrom = (GeneralConcreteValue) from;

            if (dFrom.isUndefined()) {
                logger.debug("getSelectorTarget: origin is undefined");
                return dFrom;
            }

            int baseNode = dFrom.getNode();
            Type baseNodeType = dFrom.type();
            if (!baseNodeType.hasSelectorLabel(selectorLabel)) {
                throw new IllegalStateException("Invalid selector '" + selectorLabel + "' for node of type '"
                        + baseNodeType + "'");
            }

            int node = getSelectorTargetOf(baseNode, selectorLabel);

            if (node == HeapConfiguration.INVALID_ELEMENT) {

                if (baseNodeType.isOptional(selectorLabel)) {
                    return GeneralConcreteValue.getUndefined();
                } else {
                    throw new IllegalStateException("Required selector label " + selectorLabel + " of node "
                            + baseNode + " could not be found." +
                            " This is probably caused by a flawed initial heap or an error in the provided grammar.");
                }
            }

            Type type = heap.nodeTypeOf(node);
            return new GeneralConcreteValue(type, node);
        } else {
            throw new IllegalStateException("getSelectorTarget did not get a GeneralConcreteValue.");
        }
    }

    protected abstract int getSelectorTargetOf(int sourceNode, SelectorLabel selectorLabel);

    @Override
    public void setSelector(ConcreteValue from, SelectorLabel selectorLabel, ConcreteValue to) {

        if (from.isUndefined() || to.isUndefined()) {
            logger.debug("Specified edge has undefined source or target.");
            return;
        }

        if (from.getClass() == GeneralConcreteValue.class && to.getClass() == GeneralConcreteValue.class) {
            GeneralConcreteValue dFrom = (GeneralConcreteValue) from;
            GeneralConcreteValue dTo = (GeneralConcreteValue) to;
            int fromNode = dFrom.getNode();
            Type fromType = heap.nodeTypeOf(fromNode);
            if(!fromType.hasSelectorLabel(selectorLabel)) {
                throw new IllegalStateException("Illegal request to set selector '" + selectorLabel
                        + "' for node of type '" + fromType + "'.");
            }
            try {
                removeSelector(fromNode, selectorLabel);
                heap
                        .builder()
                        .addSelector(fromNode, getNewSelector(selectorLabel), dTo.getNode())
                        .build();
            } catch (IllegalArgumentException e) {
                getHeap().builder().build();
                logger.debug("Specified edge has invalid source or target.");
            }
        }
    }

    protected abstract void removeSelector(int sourceNode, SelectorLabel selectorLabel);
    protected abstract SelectorLabel getNewSelector(SelectorLabel oldSelectorLabel);

    @Override
    public GeneralConcreteValue getUndefined() {

        return GeneralConcreteValue.getUndefined();
    }

    @Override
    public ProgramState shallowCopyWithUpdateHeap(HeapConfiguration newHeap) {

        GeneralProgramState copy = (GeneralProgramState) shallowCopy();
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
    public int getStateSpaceId() {

        return stateSpaceId;
    }

    @Override
    public void setStateSpaceId(int id) {

        stateSpaceId = id;
    }

    @Override
    public boolean isFromTopLevelStateSpace() {

        return this.heap.externalNodes().isEmpty();
    }

    @Override
    public StateSpace getContainingStateSpace() {
    	return this.containingStateSpace;
    }

    @Override
	public void setContainingStateSpace(StateSpace containingStateSpace) {
		this.containingStateSpace = containingStateSpace;
	}
   
    
    public boolean isContinueState() {
    	return this.isContinueState;
    }
	public void flagAsContinueState() {
		this.isContinueState = true;
	}
	public void unflagContinueState() {
		this.isContinueState = false;
	}
}
