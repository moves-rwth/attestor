package de.rwth.i2.attestor.graph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple standard implementation of nonterminal symbols.
 *
 * Exactly one object is created for every label. These objects can be accessed through
 * {@link BasicNonterminal#getNonterminal(String)}.
 *
 * New nonterminals should be created through
 * {@link BasicNonterminal#getNonterminal(String, int, boolean[])}.
 *
 * @author Christoph
 */
public final class BasicNonterminal implements Nonterminal {

	/**
	 * The logger used by this class.
	 */
	private static final Logger logger = LogManager.getLogger( "BasicNonterminal" );

	/**
	 * Stores the unique nonterminal object corresponding to each label.
	 */
	private static final Map<String, BasicNonterminal> existingNonterminals = new HashMap<>();

	/**
	 * Method to remove all stored existing nonterminals.
	 */
	public static synchronized void clearExistingNonterminals() {
		existingNonterminals.clear();
	}

	/**
	 * Method to access already existing nonterminal symbols.
	 * @param label The label of a requested already existing nonterminal symbol.
	 * @return The nonterminal symbol with the requested label.
	 */
	public static synchronized BasicNonterminal getNonterminal(String label ){

		if( !existingNonterminals.containsKey( label ) ){
			logger.fatal( "requested nonterminal does not exist" );
			logger.fatal( "requested was: " + label );
			logger.fatal( "have: " + existingNonterminals );
			throw new IllegalArgumentException("Requested nonterminal does not exist.");
		}
		return existingNonterminals.get( label );
	}

	/**
	 * Method to get or create nonterminal symbols.
	 * @param label The label of the requested nonterminal symbol.
	 * @param rank The rank of the requested nonterminal symbol.
	 * @param isReductionTentacle An array of length rank that determines for every tentacle whether it is a
	 *                            reduction tentacle (value true) or not (value false).
	 * @return The requested nonterminal symbol. If this object does not exist, it will be created first.
	 */
	public static synchronized BasicNonterminal getNonterminal(String label, int rank, boolean [] isReductionTentacle ){

		BasicNonterminal res;
		
		if( !existingNonterminals.containsKey( label ) ){
			
			res = new BasicNonterminal( label, rank, isReductionTentacle );
			existingNonterminals.put( label, res );
		}else{
			res = existingNonterminals.get( label );
			/*
			if( res.getRank() != rank ){
				logger.warn( label + ": rank of stored nonterminal does not match. got: " + res.getRank() + " request: " + rank );
			}
			for( int i = 0; i < isReductionTentacle.length; i++){
				if( res.isReductionTentacle[i] != isReductionTentacle[i] ){
					logger.warn( label +  ": " + i + "th  reduction tentacle of stored nonterminal does not match. got: " + res.isReductionTentacle[i] + " request: " + isReductionTentacle[i] );
				}
			}
			*/
		}
		
		return res;
	
	}

	/**
	 * The label of the nonterminal symbol.
	 */
	private final String label;

	/**
	 * Determines for every tentacle of the nonterminal whether it is a reduction tentacle (value true)
	 * or not (value false).
	 */
	private final boolean [] isReductionTentacle;

	/**
	 * Initializes a new nonterminal symbol object.
	 * @param label The label of the requested nonterminal symbol.
	 * @param rank The rank of the requested nonterminal symbol.
	 * @param isReductionTentacle An array of length rank that determines for every tentacle whether it is a
	 *                            reduction tentacle (value true) or not (value false).
	 */
	private BasicNonterminal(String label, int rank, boolean[] isReductionTentacle){
		
		this.label = label;
		this.isReductionTentacle = Arrays.copyOf(isReductionTentacle, rank);
	}

	@Override
	public int getRank(){
		return isReductionTentacle.length;
	}

	@Override
	public String getLabel() {

		return label;
	}

	@Override
	public boolean isReductionTentacle( int tentacle ){
		
		return isReductionTentacle[tentacle];
	}
	
	@Override
	public void setReductionTentacle( int tentacle) {
		
		isReductionTentacle[tentacle] = true;
	}
	
	@Override
	public void unsetReductionTentacle( int tentacle ){
		isReductionTentacle[tentacle] = false;
	}

	@Override
	public String toString(){
		return this.label;
	}

	@Override
	public int hashCode() {

		return label.hashCode();
	}
}
