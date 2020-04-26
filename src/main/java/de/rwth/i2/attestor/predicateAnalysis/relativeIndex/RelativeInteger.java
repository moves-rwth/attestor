package de.rwth.i2.attestor.predicateAnalysis.relativeIndex;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class RelativeInteger implements RelativeIndex {
    abstract Concrete solve();

    public static boolean compare(RelativeInteger ir1, RelativeInteger ir2) {
        Concrete c1 = ir1.solve();
        Concrete c2 = ir2.solve();

        return c1.constant < c2.constant && c2.vars.containsAll(c1.vars);
    }

    public static class Concrete extends RelativeInteger {
        private int constant;
        private final TIntSet vars;

        private Concrete(int constant, TIntSet vars) {
            this.constant = constant;
            this.vars = vars;
        }

        public static Concrete get(int constant) {
            return get(constant, new TIntHashSet());
        }

        public static Concrete get(TIntSet vars) {
            return get(0, vars);
        }

        public static Concrete get(int constant, TIntSet vars) {
            return new Concrete(constant, vars);
        }

        public int getConstant() {
            return constant;
        }

        public TIntSet getVars() {
            return TCollections.unmodifiableSet(vars);
        }

        @Override
        Concrete solve() {
            return this;
        }
    }

    public static class Variable extends RelativeInteger {
        private static final TIntObjectMap<Variable> reservedVariables = new TIntObjectHashMap<>();

        private final int id;

        private Variable(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Variable get(int id) {
            if (reservedVariables.containsKey(id)) {
                return reservedVariables.get(id);
            } else {
                Variable variable = new Variable(id);
                reservedVariables.put(id, variable);
                return variable;
            }
        }

        public static Variable generate() {
            return get(reservedVariables.size());
        }

        @Override
        Concrete solve() {
            throw new IllegalStateException("variable relative-integer cannot be solved");
        }
    }

    public static class Sum extends RelativeInteger {
        private final Concrete concrete = Concrete.get(0, new TIntHashSet());
        private final Set<Variable> positiveVariables = new HashSet<>();
        private final Set<Variable> negativeVariables = new HashSet<>();

        public Sum addPositiveTerm(RelativeInteger term) {
            if (term instanceof Sum) {
                Sum sumTerm = (Sum) term;
                concrete.constant += sumTerm.concrete.constant;
                concrete.vars.removeAll(sumTerm.concrete.vars);
                positiveVariables.addAll(sumTerm.getPositiveVariables());
            } else if (term instanceof Concrete) {
                Concrete concreteTerm = (Concrete) term;
                concrete.constant += concreteTerm.constant;
                concrete.vars.addAll(concreteTerm.vars);
            } else if (term instanceof Variable) {
                Variable variableTerm = (Variable) term;
                positiveVariables.add(variableTerm);
            } else {
                throw new IllegalArgumentException("unknown relative-integer subtype");
            }

            return this;
        }

        public Sum addNegativeTerm(RelativeInteger term) {
            if (term instanceof Sum) {
                Sum sumTerm = (Sum) term;
                concrete.constant -= sumTerm.concrete.constant;
                concrete.vars.removeAll(sumTerm.concrete.vars);
                negativeVariables.addAll(sumTerm.getNegativeVariables());
            } else if (term instanceof Concrete) {
                Concrete concreteTerm = (Concrete) term;
                concrete.constant -= concreteTerm.constant;
                concrete.vars.removeAll(concreteTerm.vars);
            } else if (term instanceof Variable) {
                Variable variableTerm = (Variable) term;
                negativeVariables.add(variableTerm);
            } else {
                throw new IllegalArgumentException("unknown relative-integer subtype");
            }

            return this;
        }

        public Set<Variable> getPositiveVariables() {
            return Collections.unmodifiableSet(positiveVariables);
        }

        public Set<Variable> getNegativeVariables() {
            return Collections.unmodifiableSet(negativeVariables);
        }

        @Override
        Concrete solve() {
            Set<RelativeInteger> variables = new HashSet<>(positiveVariables);
            variables.removeAll(negativeVariables);

            if (variables.isEmpty()) {
                return concrete;
            } else {
                throw new IllegalStateException("cannot solve sum due to unresolved variables");
            }
        }
    }
}
