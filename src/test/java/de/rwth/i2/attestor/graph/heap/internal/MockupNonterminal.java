package de.rwth.i2.attestor.graph.heap.internal;

import static org.junit.Assert.fail;

import java.util.Collection;

import de.rwth.i2.attestor.graph.Nonterminal;

public class MockupNonterminal implements Nonterminal {

    private final String label;
    private final int rank;

    public MockupNonterminal(String label, int rank) {

        this.label = label;
        this.rank = rank;
    }

    @Override
    public int getRank() {

        return rank;
    }

    @Override
    public boolean isReductionTentacle(int tentacle) {

        return false;
    }

    public String toString() {

        return label;
    }

    @Override
    public void setReductionTentacle(int tentacle) {

    }

    @Override
    public void unsetReductionTentacle(int tentacle) {

    }

    @Override
    public String getLabel() {

        return label;
    }

	@Override
	public Collection<Integer> reachableTentaclesFrom(int tentacle) {
		fail("call not expected");
		return null;
	}

}
