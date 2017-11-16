package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;

public class AnnotatedSelectorLabel implements SelectorLabel {
	//private static final Logger logger = LogManager.getLogger( "AnnotatedSelectorLabel" );

	private final BasicSelectorLabel basicSelectorLabel;
	private final String annotation;
	
	public AnnotatedSelectorLabel( String label, String annotation ) {
		basicSelectorLabel = BasicSelectorLabel.getSelectorLabel(label);
		this.annotation = annotation;
	}

	@Override
	public int compareTo( SelectorLabel o ) {
		if( this.equals(o)){
			return 0;
		}else{
			return -1;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
		result = prime * result + ((basicSelectorLabel == null) ? 0 : basicSelectorLabel.hashCode());
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
		AnnotatedSelectorLabel other = (AnnotatedSelectorLabel) obj;
		if (annotation == null) {
			if (other.annotation != null)
				return false;
		} else if (!annotation.equals(other.annotation))
			return false;
		if (basicSelectorLabel == null) {
			if (other.basicSelectorLabel != null)
				return false;
		} else if (!basicSelectorLabel.equals(other.basicSelectorLabel))
			return false;
		return true;
	}

	public boolean hasLabel( String label ){
		return this.basicSelectorLabel.equals( BasicSelectorLabel.getSelectorLabel(label) );
	}
	
	@Override
	public String getLabel( ){
		return this.basicSelectorLabel.toString();
	}
	
	@Override
	public String toString(){
		return basicSelectorLabel.toString() + "[" + this.annotation + "]";
	}

    public String getAnnotation() {
	    return annotation;
    }
}
