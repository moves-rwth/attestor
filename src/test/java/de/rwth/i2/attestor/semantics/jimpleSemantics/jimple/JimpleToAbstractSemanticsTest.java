//package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple;
//
//
//  import static org.junit.Assert.*;
//
//  import org.junit.Test;
//
//import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
//import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.JimpleToAbstractSemantics;
//import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
//import soot.RefType;
//  import soot.jimple.internal.JimpleLocal;
//
//	public class JimpleToAbstractSemanticsTest {
//
//		@Test
//		public void testAndExpr() {
//			
//			JimpleToAbstractSemantics translator = new StandardAbstractSemantics();
//			
//			soot.Value op1 = new JimpleLocal( "op1", RefType.v( "java.lang.Boolean" ));
//			soot.Value op2 = new JimpleLocal( "op2", RefType.v( "java.lang.Boolean" ));
//			soot.Value testExpr = soot.jimple.Jimple.v().newAndExpr(op1, op2);
//			Value resExpr = translator.translateValue( testExpr );
//			assertNotNull("resExpr is null", resExpr );
//			//assertTrue("resExpr is of type " + resExpr.getClass().getCanonicalName(), resExpr instanceof AndExpr );
//		}
//}
