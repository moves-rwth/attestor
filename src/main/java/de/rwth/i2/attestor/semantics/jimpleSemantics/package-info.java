 /**
 *  This package contains all classes which deal with the translation of Jimple elements, such as statements, values
  *  and types to semantics objects used within attestor.
 *  <br>
 *  Classes that deal with the translation process are in sub-package
 *  {@link de.rwth.i2.attestor.semantics.jimpleSemantics.translation translation}
 *  <br>
 *  Classes that deal with the semantics are in the sub-package
 *  {@link de.rwth.i2.attestor.semantics.jimpleSemantics.jimple jimple}
 *  <br>
 *  {@link de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState JimpleProgramState}
 *  specifies the functionality a heap has to have such that the our semantics is applicable to it.
 *  <br>
 *  The {@link de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleParser JimpleParser} 
 *  starts the translation of JavaByteCode via Jimple to our semantics.
 *  
 *  
 *  @see <a href="https://en.wikipedia.org/wiki/Soot_(software)#Jimple"> wikipedia </a> 
 *  @see <a href="https://www.google.de/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&ved=0ahUKEwixnYzbutPRAhWpJsAKHR1MA3IQFggjMAA&url=http%3A%2F%2Fwww.sable.mcgill.ca%2Fpublications%2Fpapers%2F1999-1%2Fsable-paper-1999-1.ps.gz&usg=AFQjCNEVNSd6Fi3dNH0_k1DKK5I7xS7U9Q&cad=rja">
 *  original thesis p.24-25 </a> 
 *
 * @author Hannah Arndt
 *
 */
package de.rwth.i2.attestor.semantics.jimpleSemantics;