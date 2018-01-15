package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import de.rwth.i2.attestor.procedures.Method;

import java.util.*;

public class TarjanAlgorithm {

    Map<Method, Vertex> methodToVertex = new LinkedHashMap<>();
    List<Vertex> vertices = new ArrayList<>();
    Map<Vertex, List<Vertex>> edges = new LinkedHashMap<>();
    int index = 0;
    Deque<Vertex> stack = new ArrayDeque<>();

    public void addMethodAsVertex(Method method) {

        Vertex vertex = new Vertex(method);
        assert (!this.vertices.contains(vertex));
        this.vertices.add(vertex);
        this.methodToVertex.put(method, vertex);
        edges.put(vertex, new ArrayList<>());
    }

    public void addCallEdge(Method caller, Method callee) {

        Vertex v = methodToVertex.get(caller);
        Vertex u = methodToVertex.get(callee);
        if (u == null) {//necessary for untranslated library methodExecution
            addMethodAsVertex(callee);
            u = methodToVertex.get(callee);
        }
        edges.get(v).add(u);
    }

    public void markRecursiveMethods() {

        for (Vertex v : vertices) {
            if (v.index < 0) {
                strongconnect(v);
            }
        }
    }

    private void strongconnect(Vertex v) {

        v.index = index;
        v.lowlink = index;
        index++;
        stack.push(v);
        v.onStack = true;

        for (Vertex w : edges.get(v)) {
            if (w.index < 0) {
                strongconnect(w);
                v.lowlink = Math.min(v.lowlink, w.lowlink);
            } else if (w.onStack) {
                v.lowlink = Math.min(v.lowlink, w.index);
            }
        }

        if (v.lowlink == v.index) {
            List<Vertex> scc = new ArrayList<>();
            Vertex w;
            do {
                w = stack.pop();
                w.onStack = false;
                scc.add(w);
            } while (w != v);

            if (scc.size() > 1) {
                for (Vertex s : scc) {
                    s.method.setRecursive(true);
                }
            } else {
                Vertex s = scc.get(0);
                if (edges.get(s).contains(s)) { //self loop
                    s.method.setRecursive(true);
                }
            }
        }


    }

    class Vertex {

        public Method method;
        public int index = -1;
        public int lowlink = -1;
        public boolean onStack = false;

        public Vertex(Method method) {

            this.method = method;
        }

        @Override
        public int hashCode() {

            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((method == null) ? 0 : method.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Vertex other = (Vertex) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (method == null) {
                return other.method == null;
            } else return method.equals(other.method);
        }

        public String toString() {

            return this.method.toString() + "(" + this.index + "," + this.lowlink + ")";
        }

        private TarjanAlgorithm getOuterType() {

            return TarjanAlgorithm.this;
        }
    }


}
