package de.rwth.i2.attestor.main.scene;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.procedures.Contract;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.programState.defaultState.RefinedDefaultNonterminal;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.programState.indexedState.IndexedState;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultScene implements Scene {


    private final GeneralType.Factory typeFactory = new GeneralType.Factory();
    private final BasicSelectorLabel.Factory basicSelectorLabelFactory = new BasicSelectorLabel.Factory();
    private final BasicNonterminal.Factory basicNonterminalFactory = new BasicNonterminal.Factory();

    private final Options options = new Options();
    private final Labels labels = new Labels();
    private final Strategies strategies = new Strategies();

    private final Map<String, Method> methods = new HashMap<>();
    private long totalNumberOfStates = 0;

    @Override
    public Type getType(String name) {

        return typeFactory.get(name);
    }

    @Override
    public SelectorLabel getSelectorLabel(String name) {

        if (options.isIndexedMode()) {
            SelectorLabel sel = basicSelectorLabelFactory.get(name);
            return new AnnotatedSelectorLabel(sel, "");
        } else {
            return basicSelectorLabelFactory.get(name);
        }
    }

    @Override
    public Nonterminal getNonterminal(String name) {

        // note that we *never* return IndexedNonterminal here as these are created using a
        // BasicNonterminal which is obtained using this method.
        Nonterminal basicNonterminal = basicNonterminalFactory.get(name);
        if (options.isIndexedMode() && options.isGrammarRefinementEnabled()) {
            throw new IllegalArgumentException("Refinement of indexed grammars is not supported yet.");
        } else if (options.isGrammarRefinementEnabled()) {
            return new RefinedDefaultNonterminal(basicNonterminal, null);
        } else {
            return basicNonterminal;
        }
    }

    @Override
    public Nonterminal createNonterminal(String label, int rank, boolean[] isReductionTentacle) {

        // note that we *never* return IndexedNonterminal here as these are created using a
        // BasicNonterminal which is obtained using this method.
        Nonterminal basicNonterminal = basicNonterminalFactory.create(label, rank, isReductionTentacle);
        if (options.isIndexedMode() && options.isGrammarRefinementEnabled()) {
            throw new IllegalArgumentException("Refinement of indexed grammars is not supported yet.");
        } else if (options.isGrammarRefinementEnabled()) {
            return new RefinedDefaultNonterminal(
                    basicNonterminal, null
            );
        } else {
            return basicNonterminal;
        }
    }

    @Override
    public HeapConfiguration createHeapConfiguration() {

        return new InternalHeapConfiguration();
    }

    @Override
    public ProgramState createProgramState(HeapConfiguration heapConfiguration) {

        ProgramState result;
        if (options.isIndexedMode()) {
            result = new IndexedState(heapConfiguration);
        } else {
            result = new DefaultProgramState(heapConfiguration);
        }
        result.setProgramCounter(0);
        result.prepareHeap();
        return result;
    }

    @Override
    public ProgramState createProgramState() {

        return createProgramState(createHeapConfiguration());
    }

    @Override
    public Contract createContract(HeapConfiguration precondition, Collection<HeapConfiguration> postconditions) {
        return null;
    }

    @Override
    public Method getOrCreateMethod(String signature) {

        if(methods.containsKey(signature)) {
            return methods.get(signature);
        } else {
            Method result = new ConcreteMethod(signature);
            methods.put(signature, result);
            return result;
        }
    }
    
    @Override
    public Method getMethodIfPresent(String signature) throws ElementNotPresentException {

        if(methods.containsKey(signature)) {
            return methods.get(signature);
        } else {
            throw new ElementNotPresentException("there is no method with this signature");
        }
    }

    @Override
    public Collection<Method> getRegisteredMethods() {
        return methods.values();
    }

    @Override
    public void addNumberOfGeneratedStates(int states) {

        totalNumberOfStates += states;
    }

    @Override
    public long getNumberOfGeneratedStates() {

        return totalNumberOfStates;
    }

    @Override
    public Options options() {

        return options;
    }

    @Override
    public Labels labels() {

        return labels;
    }

    @Override
    public Strategies strategies() {

        return strategies;
    }

}
