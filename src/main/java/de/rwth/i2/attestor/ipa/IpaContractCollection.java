package de.rwth.i2.attestor.ipa;

import java.util.*;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;

/**
 * This is essentially a hashMap from IpaPrecondtion to List&#60;HeapConfiguration&#62;
 * but it returns the complete Entry instead of only the value.
 * This is necessary to manage the reordering of tentacles.
 * 
 * @author Hannah
 *
 */
public class IpaContractCollection {

	class IpaContract{
		public IpaContract(IpaPrecondition precondition, ArrayList postconditions) {
			this.precondition = precondition;
			this.postconditions = postconditions;
		}
		public IpaPrecondition precondition;
		public List<HeapConfiguration> postconditions;
	}

	Map<Integer,
	List< IpaContract >
	> map = new HashMap<>();

	public List<HeapConfiguration> getPostconditions( HeapConfiguration reachableFragment ){

		final IpaPrecondition toMatch = new IpaPrecondition(reachableFragment);
		int hashCode = toMatch.hashCode();
		if( ! map.containsKey(hashCode) ){
			map.put(hashCode, new ArrayList<>() );
		}
		List<IpaContract> contracts = map.get( hashCode );
		for( IpaContract contract : contracts ){
			if( contract.precondition.equals(toMatch) ){
				return contract.postconditions;
			}
		}

		return null;
	}

	public int [] getReordering( HeapConfiguration reachableFragment ){
		
		for( IpaContract contract : map.get(reachableFragment.hashCode()) ){
			
			final IpaPrecondition ipaPrecondition = new IpaPrecondition( reachableFragment );
			if( contract.precondition.equals(ipaPrecondition) ){
				return contract.precondition.getReordering( reachableFragment );
			}
		}
		
		return null;
	}


	public void addPrecondition( HeapConfiguration precondition ){
		int hashCode = precondition.hashCode();
		if( ! map.containsKey( hashCode ) ){
			map.put( hashCode, new ArrayList<>() );
		}

		final IpaPrecondition ipaPrecondition = new IpaPrecondition(precondition);
		map.get( hashCode ).add( new IpaContract( ipaPrecondition, new ArrayList<>() ) );
	}

	public boolean hasPrecondition( HeapConfiguration precondition ){
		int hashCode = precondition.hashCode();
		if( ! map.containsKey( hashCode ) ){
			return false;
		}

		final IpaPrecondition ipaPrecondition = new IpaPrecondition(precondition);
		for( IpaContract contract : map.get(hashCode) ){
			if( contract.precondition.equals( ipaPrecondition ) ){
				return true;
			}
		}
		return false;
	}
}
