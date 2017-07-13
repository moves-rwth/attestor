package de.rwth.i2.attestor.graph.heap.internal;

public class TestHeapConfigImplementation extends InternalHeapConfiguration {
		
		public TestHeapConfigurationBuilder getBuilderForTest(){
			assert( this.builder == null  );
			this.builder = new TestHeapConfigurationBuilder( this );
			
			return (TestHeapConfigurationBuilder) super.builder;
		}

}
