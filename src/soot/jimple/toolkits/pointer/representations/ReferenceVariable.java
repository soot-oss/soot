/* ReferenceVariable provides interface for simulating assignments
 * in a native method.
 *
 * e.g.,  a = b, can be written by a.isAssigned(b);
 *
 * A reference variable is an abstract of a variable rather than a
 * representation of an abstract object, the later is represented by
 * AbstractObject.
 *
 * The simulation formulates the assignment or field assignment of such
 * high level variable. It is the resposibility of analyses to get the
 * constraints out of such abstract simulation. 
 *
 * Analyses may use very different representations of points-to, e.g.,
 * set constraints, type rules, or points-to graphs.  All the analyses
 * have to do is to let the variables in the program implement this
 * interface.  
 */

package soot.jimple.toolkits.pointer.representations;

import soot.*;

public interface ReferenceVariable {
}
