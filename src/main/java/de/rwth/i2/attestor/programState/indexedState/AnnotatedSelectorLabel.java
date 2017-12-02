package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;

public class AnnotatedSelectorLabel implements SelectorLabel {

	private final BasicSelectorLabel basicSelectorLabel;
	private final String annotation;
	
	public AnnotatedSelectorLabel( SelectorLabel basicSelectorLabel, String annotation ) {

		assert basicSelectorLabel instanceof BasicSelectorLabel;
		this.basicSelectorLabel = (BasicSelectorLabel) basicSelectorLabel;
		this.annotation = annotation;
	}

	public AnnotatedSelectorLabel(SelectorLabel selectorLabel) {

		if(selectorLabel instanceof AnnotatedSelectorLabel) {
			AnnotatedSelectorLabel other = (AnnotatedSelectorLabel) selectorLabel;
			this.basicSelectorLabel = other.basicSelectorLabel;
			this.annotation = other.annotation;
		} else {
			assert selectorLabel instanceof BasicSelectorLabel;
			this.basicSelectorLabel = (BasicSelectorLabel) selectorLabel;
			this.annotation = "";
		}
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
			return other.basicSelectorLabel == null;
		} else return basicSelectorLabel.equals(other.basicSelectorLabel);
	}

	public boolean hasLabel( String label ){
		return basicSelectorLabel.hasLabel(label);
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
