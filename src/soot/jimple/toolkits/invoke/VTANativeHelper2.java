/**
 * Implementation of pointer.util.NativeHelper.
 *
 * @author Feng Qian
 */
package soot.jimple.toolkits.invoke;

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

import java.util.*;

public class VTANativeHelper2 
  extends NativeHelper implements TypeConstants{

  /* class hierarchy set by the caller */
  Hierarchy h;

  /* VTA type graph */
  VTATypeGraph2 vtg;

  /************* implementation of NativeHelper ***************/
  protected 
    void assignImpl(ReferenceVariable lhs, ReferenceVariable rhs) {
    vtg.addEdge((TypeGraphNode2)rhs, (TypeGraphNode2)lhs);
  }
  
  protected 
    void assignObjectToImpl(ReferenceVariable lhs, AbstractObject obj) {
    TypeSet2 ts = (TypeSet2)vtg.getReachingTypesOf(lhs);
    SootClass type = obj.getType();
    ts.add(TypeElement2.v(type));
  }

  /* Also cloning returns the original variable, it is conservative
   * for points-to analyses.
   */
  protected 
    ReferenceVariable cloneObjectImpl(ReferenceVariable source) {
    return source;
  }

  /* It implements the newInstance0 method of java.lang.Class. 
   * The 'cls' must have the type 'java.lang.Class'.
   * It may not be possible to know what 'cls' is. Give dummy
   * variable 'java.lang.Object' type is conservative.
   */ 
  protected 
    ReferenceVariable newInstanceOfImpl(ReferenceVariable cls) {
    /* all types are possible */
    TypeGraphNode2 tempNode = TypeGraphNode2.makeTempNode();
    vtg.addNode(tempNode, RefType.v(OBJECTCLASS));
    includeSubtypesOf(tempNode.getTypeSet2(), 
		      OBJECTCLASS);
    return tempNode;
  }

  /* This is a implementation specific choice, for VTA, we do not
   * distinguish the base of array reference. So the unique least
   * object as base is conservative.
   */
  protected
    ReferenceVariable arrayElementOfImpl(ReferenceVariable base) {
    return base;
  }

  /* We can get more accurate declaring type info from field signature.
   * The static field may imply a call of <clinit>, but we do not have to
   * worry about this in native method because the method get called 
   * before.
   */
  protected 
    ReferenceVariable staticFieldImpl(String clsname, String fieldsig) {
    TypeGraphNode2 tempNode = TypeGraphNode2.v(clsname+fieldsig);
    vtg.addNode(tempNode, RefType.v(OBJECTCLASS));
    return tempNode;
  }

  /* Makes a dummy variable for a temporary field 
   */
  protected 
    ReferenceVariable tempFieldImpl(String fieldsig){
    TypeGraphNode2 tempNode = TypeGraphNode2.v(fieldsig);
    vtg.addNode(tempNode, RefType.v(OBJECTCLASS));
    return tempNode;
  } 

  /* Makes a dummy variable for a temporary variable.
   */
  protected
    ReferenceVariable tempVariableImpl(){
    TypeGraphNode2 tempNode = TypeGraphNode2.makeTempNode();
    vtg.addNode(tempNode, RefType.v(OBJECTCLASS));  
    return tempNode;
  }

  /* Include subtypes of ...
   */
  private void includeSubtypesOf(TypeSet2 typeset, SootClass cls)  {
    Iterator clsIt = 
	  h.getSubclassesOfIncluding(cls).iterator();
    while (clsIt.hasNext()) {
      typeset.add(TypeElement2.v((SootClass)clsIt.next()));
    }
  }
}
