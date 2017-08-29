package de.rwth.i2.attestor.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * A general implementation of selector labels that consists of a single label.
 * There exists exactly one object for every selector label. These should be created and accessed through the static
 * method {@link BasicSelectorLabel#getSelectorLabel(String)}.
 *
 * @author Christoph
 */
public class BasicSelectorLabel implements SelectorLabel {

    /**
     * Stores all created selector label objects.
     */
	private static final Map<String, BasicSelectorLabel> existingSelectors = new HashMap<>();

    /**
     * Provides a selector label with the requested label.
     * If no object with this label exists, a new one will be created.
     *
     * @param label The requested label.
     * @return The selector label object with the requested label.
     */
	public static synchronized BasicSelectorLabel getSelectorLabel(String label ){
		if( !existingSelectors.containsKey( label ) ){
			existingSelectors.put( label, new BasicSelectorLabel( label ) );
		}
		return existingSelectors.get( label );
	}

    /**
     * The label of the selector label.
     */
	private final String label;

    /**
     * Creates a selector label.
     * @param label The name of the label.
     */
	private BasicSelectorLabel(String label) {
		this.label = label;
	}
	
	@Override
	public int compareTo(SelectorLabel other) {
		return this.toString().compareTo( other.toString() );
	}
	
	public String toString(){
		return label;
	}

	@Override
	public boolean hasLabel(String label) {
		
		return this.label.equals(label);
	}

	@Override
	public String getLabel() {
		
		return label;
	}
	
}
