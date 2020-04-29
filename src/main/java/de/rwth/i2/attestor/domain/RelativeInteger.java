package de.rwth.i2.attestor.domain;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Set;


public abstract class RelativeInteger implements Lattice<RelativeInteger>, RelativeIndex<RelativeInteger> {
    abstract Concrete solve();

    public RelativeInteger add(RelativeInteger e1, RelativeInteger e2) {
        Sum s = new Sum();
        s.addTerm(e1, true);
        s.addTerm(e2, true);
        return s;
    }

    public RelativeInteger subtract(RelativeInteger e1, RelativeInteger e2) {
        Sum s = new Sum();
        s.addTerm(e1, true);
        s.addTerm(e2, false);
        return s;
    }

    // Lattice operations
    @Override
    public RelativeInteger leastElement() {
        return Concrete.ZERO;
    }

    @Override
    public RelativeInteger getLeastUpperBound(Set<RelativeInteger> elements) {
        return new Sum();
    }

    @Override
    public boolean isLessOrEqual(RelativeInteger e1, RelativeInteger e2) {
        Concrete c1 = e1.solve();
        Concrete c2 = e2.solve();
        return c1.constant < c2.constant && c2.vars.containsAll(c1.vars);
    }

    // RelativeIndex operations
    @Override
    public RelativeInteger getVariable() {
        return Variable.generate();
    }

    public static class Concrete extends RelativeInteger {
        private int constant;
        private final TIntSet vars;

        private Concrete(int constant, TIntSet vars) {
            this.constant = constant;
            this.vars = vars;
        }

        @Override
        Concrete solve() {
            return this;
        }

        // Factory
        public static final Concrete ZERO = get(0, new TIntHashSet());

        public static Concrete get(int constant) {
            return get(constant, new TIntHashSet());
        }

        public static Concrete get(TIntSet vars) {
            return get(0, vars);
        }

        public static Concrete get(int constant, TIntSet vars) {
            return new Concrete(constant, vars);
        }
    }

    public static class Variable extends RelativeInteger {
        private final int id;

        private Variable(int id) {
            this.id = id;
        }

        @Override
        Concrete solve() {
            throw new IllegalStateException("variable relative-integers cannot be solved");
        }

        // Factory
        private static final TIntObjectMap<Variable> reserved = new TIntObjectHashMap<>();

        public static Variable generate() {
            return get(reserved.size());
        }

        public static Variable get(int id) {
            if (reserved.containsKey(id)) {
                return reserved.get(id);
            } else {
                Variable variable = new Variable(id);
                reserved.put(id, variable);
                return variable;
            }
        }
    }

    public static class Sum extends RelativeInteger {
        private final Concrete concrete = Concrete.ZERO;
        private final TIntSet positiveVariables = new TIntHashSet();
        private final TIntSet negativeVariables = new TIntHashSet();

        private Sum() {
        }

        private void addTerm(RelativeInteger term, boolean positive) {
            if (term instanceof Sum) {
                Sum t = (Sum) term;

                if (positive) {
                    concrete.constant += t.concrete.constant;
                    concrete.vars.addAll(t.concrete.vars);
                    positiveVariables.addAll(t.positiveVariables);
                } else {
                    concrete.constant -= t.concrete.constant;
                    concrete.vars.removeAll(t.concrete.vars);
                    negativeVariables.addAll(t.negativeVariables);
                }
            } else if (term instanceof Concrete) {
                Concrete t = (Concrete) term;

                if (positive) {
                    concrete.constant += t.constant;
                    concrete.vars.addAll(t.vars);
                } else {
                    concrete.constant -= t.constant;
                    concrete.vars.removeAll(t.vars);
                }

            } else if (term instanceof Variable) {
                Variable t = (Variable) term;

                if (positive) {
                    positiveVariables.add(t.id);
                } else {
                    negativeVariables.add(t.id);
                }
            } else {
                throw new IllegalArgumentException("unsupported relative-integer subtype");
            }
        }

        @Override
        Concrete solve() {
            TIntSet variables = new TIntHashSet(positiveVariables);
            variables.removeAll(negativeVariables);

            if (variables.isEmpty()) {
                return concrete;
            } else {
                throw new IllegalStateException("cannot solve sum due to unresolved variables");
            }
        }
    }
}
