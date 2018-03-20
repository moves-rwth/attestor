package de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.graph.morphism.MorphismOptions;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;

import java.util.List;

import static org.junit.Assert.fail;

public class HeapConfigurationDummy implements HeapConfiguration {

	String id;
	
	public HeapConfigurationDummy( String id ) {
		this.id = id;
	}
	
	public String toString() {
		return id;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		HeapConfigurationDummy other = (HeapConfigurationDummy) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public HeapConfiguration clone() {
		return this;
	}

	@Override
	public HeapConfiguration getEmpty() {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public HeapConfigurationBuilder builder() {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public int countNodes() {
		return 0;
	}

	@Override
	public TIntArrayList nodes() {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public Type nodeTypeOf(int node) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public TIntArrayList attachedVariablesOf(int node) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public TIntArrayList attachedNonterminalEdgesOf(int node) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public TIntArrayList successorNodesOf(int node) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public TIntArrayList predecessorNodesOf(int node) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public List<SelectorLabel> selectorLabelsOf(int node) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public int selectorTargetOf(int node, SelectorLabel sel) {
		fail("Not expected to be called");
		return 0;
	}

	@Override
	public int countExternalNodes() {
		fail("Not expected to be called");
		return 0;
	}

	@Override
	public TIntArrayList externalNodes() {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public int externalNodeAt(int pos) {
		fail("Not expected to be called");
		return 0;
	}

	@Override
	public boolean isExternalNode(int node) {
		fail("Not expected to be called");
		return false;
	}

	@Override
	public int externalIndexOf(int node) {
		fail("Not expected to be called");
		return 0;
	}

	@Override
	public int countNonterminalEdges() {
		fail("Not expected to be called");
		return 0;
	}

	@Override
	public TIntArrayList nonterminalEdges() {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public int rankOf(int ntEdge) {
		fail("Not expected to be called");
		return 0;
	}

	@Override
	public Nonterminal labelOf(int ntEdge) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public TIntArrayList attachedNodesOf(int ntEdge) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public int countVariableEdges() {
		fail("Not expected to be called");
		return 0;
	}

	@Override
	public TIntArrayList variableEdges() {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public int variableWith(String name) {
		return 0;
	}

	@Override
	public String nameOf(int varEdge) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public int targetOf(int varEdge) {
		return 0;
	}

	@Override
	public AbstractMatchingChecker getEmbeddingsOf(HeapConfiguration pattern, MorphismOptions options) {
		fail("Not expected to be called");
		return null;
	}

	@Override
	public int variableTargetOf(String variableName) {
		fail("Not expected to be called");
		return 0;
	}

	@Override
	public TIntIntMap attachedNonterminalEdgesWithNonReductionTentacle(int node) {
		fail("Not expected to be called");
		return null;
	}

}
