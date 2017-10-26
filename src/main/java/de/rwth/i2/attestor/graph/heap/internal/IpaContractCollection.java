package de.rwth.i2.attestor.graph.heap.internal;

import java.util.*;
import java.util.Map.Entry;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.IpaPrecondition;

/**
 * This is essentially a hashMap from IpaPrecondtion to List&#60;HeapConfiguration&#62;
 * but it returns the complete Entry instead of only the value.
 * This is necessary to manage the reordering of tentacles.
 * 
 * @author Hannah
 *
 */
public class IpaContractCollection {

	Map<Integer,Map<IpaPrecondition,List<HeapConfiguration>>> map;
	
	public Entry<IpaPrecondition,List<HeapConfiguration>> getContract( HeapConfiguration remainingFragment ){
		
		
		final IpaPrecondition toMatch = new IpaPrecondition(remainingFragment);
		int hashCode = toMatch.hashCode();
		Map<IpaPrecondition,List<HeapConfiguration>> contracts = map.get( hashCode );
		for( Entry<IpaPrecondition, List<HeapConfiguration>> contract : contracts.entrySet() ){
			if( contract.getKey().equals(toMatch) ){
				return contract;
			}
		}
		
		return null;
	}
	
	public List<HeapConfiguration> getPostConditions( IpaPrecondition precondition ){
		int hashCode = precondition.hashCode();
		return map.get(hashCode).get(precondition);
	}
	
	public void addPrecondition( IpaPrecondition precondition ){
		int hashCode = precondition.hashCode();
		if( ! map.containsKey( hashCode ) ){
			map.put( hashCode, new HashMap<>() );
		}
		
		map.get( hashCode ).put( precondition, new ArrayList<>() );
	}
}
