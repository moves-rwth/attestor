package de.rwth.i2.attestor.indexedGrammars;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;

public class AnnotatedSelectorLabel implements SelectorLabel {
	//private static final Logger logger = LogManager.getLogger( "AnnotatedSelectorLabel" );

	private final GeneralSelectorLabel generalSelectorLabel;
	private final String annotation;
	
	public AnnotatedSelectorLabel( String label, String annotation ) {
		generalSelectorLabel = GeneralSelectorLabel.getSelectorLabel(label);
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
		result = prime * result + ((generalSelectorLabel == null) ? 0 : generalSelectorLabel.hashCode());
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
		if (generalSelectorLabel == null) {
			if (other.generalSelectorLabel != null)
				return false;
		} else if (!generalSelectorLabel.equals(other.generalSelectorLabel))
			return false;
		return true;
	}

	public boolean hasLabel( String label ){
		return this.generalSelectorLabel.equals( GeneralSelectorLabel.getSelectorLabel(label) );
	}
	
	@Override
	public String getLabel( ){
		return this.generalSelectorLabel.toString();
	}
	
	@Override
	public String toString(){
		return generalSelectorLabel.toString() + "[" + this.annotation + "]";
	}

    public String getAnnotation() {
	    return annotation;
    }
}
