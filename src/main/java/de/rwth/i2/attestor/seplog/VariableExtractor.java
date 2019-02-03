package de.rwth.i2.attestor.seplog;

import java.util.*;

/**
 * Determines the equivalence classes of all variables and constants
 * in a given symbolic heap.
 *
 * @author Christoph
 */
public class VariableExtractor extends SeparationLogicBaseListener implements VariableUnification {

    /**
     * Maps every variable name to the name of the first variable which is
     * equal to it.
     */
    private Map<String,VariableEquivalenceClass> varToClass= new HashMap<>();

    /**
     * The set of all variable names corresponding to program variables.
     */
    private Set<String> programVariableNames = new HashSet<>();

    /**
     * List of all known equivalence classes of variables and constants
     * referring to the same heap location.
     */
    private List<VariableEquivalenceClass> equivalenceClasses = new ArrayList<>();

    /**
     * Flag that is enabled while parsing the head of a symbolic heap.
     */
    private boolean isHeapHead;

    /**
     * The name of the last encountered variable name.
     */
    private String lastVariableName;

    /**
     * The name of the last encountered variable type.
     */
    private String lastVariableType;

    /**
     * Flag that is enabled while parsing a pure formula.
     */
    private boolean isPureFormulaMode;

    /**
     * The stored left-hand side of an equality.
     */
    private String equalityLhs;

    /**
     * The stored right-hand side of an equality.
     */
    private String equalityRhs;

    @Override
    public String getUniqueName(String variableName) {

        return getEquivalenceClass(variableName).getRepresentative();
    }

    private VariableEquivalenceClass getEquivalenceClass(String variableName) {

        if(varToClass.containsKey(variableName)) {
            return varToClass.get(variableName);
        }

        for(VariableEquivalenceClass eqClass : equivalenceClasses) {
            if(eqClass.contains(variableName)) {
                varToClass.put(variableName, eqClass);
                return eqClass;
            }
        }

        throw new IllegalArgumentException("Unknown variable name '" + variableName + "'.");
    }

    @Override
    public String getType(String variableName) {

        return getEquivalenceClass(variableName).getType();
    }

    @Override
    public List<String> getUniqueVariableNames() {

        List<String> result = new ArrayList<>();
        equivalenceClasses.forEach(eqc -> result.add(eqc.getRepresentative()));
        return result;
    }

    @Override
    public Set<String> getProgramVariableNames() {

        return programVariableNames;
    }

    @Override
    public void enterHeapHead(SeparationLogicParser.HeapHeadContext ctx) {

        isHeapHead = true;
    }

    @Override
    public void exitHeapHead(SeparationLogicParser.HeapHeadContext ctx) {

        isHeapHead = false;
    }

    @Override
    public void enterVariable(SeparationLogicParser.VariableContext ctx) {

        lastVariableName = ctx.getText();

        if(isHeapHead) {
            programVariableNames.add(lastVariableName);
        }

        if(isPureFormulaMode) {
            if(equalityLhs == null) {
                equalityLhs = lastVariableName;
            } else {
                equalityRhs = lastVariableName;
            }
        }
    }

    @Override
    public void enterType(SeparationLogicParser.TypeContext ctx) {

        lastVariableType = ctx.getText();
    }

    @Override
    public void exitVariableDeclaration(SeparationLogicParser.VariableDeclarationContext ctx) {

        equivalenceClasses.add(
                new VariableEquivalenceClass(lastVariableName, lastVariableType)
        );

        lastVariableName = null;
        lastVariableType = null;
    }

    @Override
    public void enterPure(SeparationLogicParser.PureContext ctx) {

        isPureFormulaMode = true;
    }

    @Override
    public void enterSelector(SeparationLogicParser.SelectorContext ctx) {

        lastVariableName = null; // reset variable name to consider selector target
    }

    @Override public void exitPointer(SeparationLogicParser.PointerContext ctx) {

        if(lastVariableName == null) { // null is target of some selector
            registerConstant("null");
        }
    }

    @Override
    public void enterConstant(SeparationLogicParser.ConstantContext ctx) {

        String constant = ctx.getText();
        registerConstant(constant);

        if(isPureFormulaMode) {
            if(equalityLhs == null) {
               equalityLhs = constant;
            } else {
                equalityRhs = constant;
            }
        }
    }

    private void registerConstant(String constant) {

        boolean exists = false;
        for(VariableEquivalenceClass eqClass : equivalenceClasses)  {
            if(eqClass.contains(constant)) {
                exists = true;
                break;
            }
        }
        if(!exists) {
            programVariableNames.add(constant);
            equivalenceClasses.add(
                    new VariableEquivalenceClass(constant)
            );
        }
    }

    @Override
    public void exitPure(SeparationLogicParser.PureContext ctx) {

        mergeClasses(equalityLhs, equalityRhs);

        equalityLhs = null;
        equalityRhs = null;
        isPureFormulaMode = false;
    }

    private void mergeClasses(String lhs, String rhs) {

        VariableEquivalenceClass left = null;
        VariableEquivalenceClass right = null;

        Iterator<VariableEquivalenceClass> iter = equivalenceClasses.iterator();
        while(iter.hasNext() && (left == null || right == null)) {
            VariableEquivalenceClass current = iter.next();
            if(left == null && current.contains(lhs)) {
               left = current;
            } else if(right == null && current.contains(rhs)) {
                right = current;
                iter.remove();
            }
        }
        assert left != null && right != null;
        left.merge(right);
    }
}
