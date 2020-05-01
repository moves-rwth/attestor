package de.rwth.i2.attestor.domain;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Set;

public abstract class RelativeInteger {
    abstract Concrete solve();

    // ensures that the inner classes are the only subclasses
    private RelativeInteger() {
    }

    private static class Concrete extends RelativeInteger {
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

        public Concrete get(int constant) {
            return get(constant, new TIntHashSet());
        }

        public static Concrete get(TIntSet vars) {
            return get(0, vars);
        }

        public static Concrete get(int constant, TIntSet vars) {
            return new Concrete(constant, vars);
        }
    }

    private static class Variable extends RelativeInteger {
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

    private static class Sum extends RelativeInteger {
        private final Concrete concrete = Concrete.ZERO;
        private final TIntSet positiveVariables = new TIntHashSet();
        private final TIntSet negativeVariables = new TIntHashSet();

        Sum() {
        }

        void addTerm(RelativeInteger term, boolean positive) {
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

    public static class RelativeIntegerSet implements Lattice<RelativeInteger>, RelativeIndexSet<RelativeInteger> {

        public RelativeInteger add(RelativeInteger i1, RelativeInteger i2) {
            RelativeInteger.Sum s = new RelativeInteger.Sum();
            s.addTerm(i1, true);
            s.addTerm(i2, true);
            return s;
        }

        public RelativeInteger subtract(RelativeInteger i1, RelativeInteger i2) {
            RelativeInteger.Sum s = new RelativeInteger.Sum();
            s.addTerm(i1, true);
            s.addTerm(i2, false);
            return s;
        }

        // Lattice operations
        @Override
        public RelativeInteger getLeastElement() {
            return RelativeInteger.Concrete.ZERO;
        }

        @Override
        public RelativeInteger getLeastUpperBound(Set<RelativeInteger> elements) {
            return new RelativeInteger.Sum();
        }

        @Override
        public boolean isLessOrEqual(RelativeInteger i1, RelativeInteger i2) {
            RelativeInteger.Concrete c1 = i1.solve();
            RelativeInteger.Concrete c2 = i2.solve();
            return c1.constant < c2.constant && c2.vars.containsAll(c1.vars);
        }

        // RelativeIndexSet operations
        @Override
        public RelativeInteger getVariable() {
            return RelativeInteger.Variable.generate();
        }
    }
}
