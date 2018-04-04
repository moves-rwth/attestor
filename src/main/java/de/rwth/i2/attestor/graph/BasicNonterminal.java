package de.rwth.i2.attestor.graph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple standard implementation of nonterminal symbols.
 * <p>
 * Exactly one object is created for every label.
 *
 * @author Christoph
 */
public final class BasicNonterminal implements Nonterminal {

    /**
     * The logger used by this class.
     */
    private static final Logger logger = LogManager.getLogger("BasicNonterminal");
    /**
     * The label of the nonterminal symbol.
     */
    private final String label;
    /**
     * Determines for every tentacle of the nonterminal whether it is a reduction tentacle (value true)
     * or not (value false).
     */
    private final boolean[] isReductionTentacle;
    
    /**
     * stores which tentacle is reachable from a given tentacle
     * has to be initialized when building the grammar.
     */
    Map<Integer,Collection<Integer>> reachabilityMap = null;

    /**
     * Initializes a new nonterminal symbol object.
     *
     * @param label               The label of the requested nonterminal symbol.
     * @param rank                The rank of the requested nonterminal symbol.
     * @param isReductionTentacle An array of length rank that determines for every tentacle whether it is a
     *                            reduction tentacle (value true) or not (value false).
     */
    private BasicNonterminal(String label, int rank, boolean[] isReductionTentacle) {

        this.label = label;
        this.isReductionTentacle = Arrays.copyOf(isReductionTentacle, rank);
    }

    @Override
    public int getRank() {

        return isReductionTentacle.length;
    }

    @Override
    public String getLabel() {

        return label;
    }

    @Override
    public boolean isReductionTentacle(int tentacle) {

        return isReductionTentacle[tentacle];
    }

    @Override
    public void setReductionTentacle(int tentacle) {

        isReductionTentacle[tentacle] = true;
    }

    @Override
    public void unsetReductionTentacle(int tentacle) {

        isReductionTentacle[tentacle] = false;
    }

    @Override
    public String toString() {

        return this.label;
    }

    @Override
    public int hashCode() {

        return label.hashCode();
    }

    public static final class Factory {

        private final Map<String, BasicNonterminal>
                knownNonterminals = new LinkedHashMap<>();

        public BasicNonterminal get(String name) {

            if (!knownNonterminals.containsKey(name)) {
                throw new IllegalArgumentException("Requested nonterminal does not exist. Requested was "
                    + name + ". Known nonterminals are: " + knownNonterminals);
            }
            return knownNonterminals.get(name);
        }

        /**
         * Method to create nonterminal symbols. If the nonterminal already exists, a reference to the existing one
         * will be returned.
         *
         * @param label               The label of the requested nonterminal symbol.
         * @param rank                The rank of the requested nonterminal symbol.
         * @param isReductionTentacle An array of length rank that determines for every tentacle whether it is a
         *                            reduction tentacle (value true) or not (value false).
         * @return The requested nonterminal symbol. If this object does not exist, it will be created first.
         */
        public BasicNonterminal create(String label, int rank, boolean[] isReductionTentacle) {

            BasicNonterminal res;
            if (!knownNonterminals.containsKey(label)) {
                res = new BasicNonterminal(label, rank, isReductionTentacle);
                knownNonterminals.put(label, res);
            } else {
                res = knownNonterminals.get(label);
                if (res.getRank() != rank) {
                    logger.warn(label + ": rank of stored nonterminal does not match. got: " + res.getRank() + " request: " + rank);
                }
                for (int i = 0; i < isReductionTentacle.length; i++) {
                    if (res.isReductionTentacle[i] != isReductionTentacle[i]) {
                        logger.warn(label + ": " + i + "th  reduction tentacle of stored nonterminal does not match. got: " + res.isReductionTentacle[i] + " request: " + isReductionTentacle[i]);
                    }
                }
            }
            return res;
        }
    }
    

	@Override
	public Collection<Integer> reachableTentaclesFrom(int tentacle) {
		if( reachabilityMap == null ) {
			throw new IllegalStateException();
		}else {
			return reachabilityMap.get( tentacle );
		}
	}

	@Override
	public void setReachableTentacles(Map<Integer, Collection<Integer>> reachabilityMap) {
		if( this.reachabilityMap == null ) {
			this.reachabilityMap = reachabilityMap;
		}else {
			for( int tentacle : reachabilityMap.keySet() ) {
				Collection<Integer> oldReachableTentacles = this.reachabilityMap.get(tentacle);
				Collection<Integer> newReachableTentacles = reachabilityMap.get(tentacle);
				newReachableTentacles.removeAll(oldReachableTentacles);
				oldReachableTentacles.addAll(newReachableTentacles);
			}
		}
		
	}
}
