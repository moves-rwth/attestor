package de.rwth.i2.attestor.abstraction.programs;

public class LongList {

	private final LongList next;

	public LongList( LongList next ) {
		this.next = next;
	}
	
	public LongList getNext() {
		return next;
	}

	public static void main( String[] args ) {

		LongList curr = new LongList( null );

		for( int i = 0; i < 10; i++ ) {
			curr = new LongList( curr );
		}

	}
}
