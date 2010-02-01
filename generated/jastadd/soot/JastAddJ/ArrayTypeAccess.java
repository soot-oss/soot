
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class ArrayTypeAccess extends TypeAccess implements Cloneable {
    public void flushCache() {
        super.flushCache();
        getPackage_computed = false;
        getPackage_value = null;
        getID_computed = false;
        getID_value = null;
        decl_computed = false;
        decl_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ArrayTypeAccess clone() throws CloneNotSupportedException {
        ArrayTypeAccess node = (ArrayTypeAccess)super.clone();
        node.getPackage_computed = false;
        node.getPackage_value = null;
        node.getID_computed = false;
        node.getID_value = null;
        node.decl_computed = false;
        node.decl_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ArrayTypeAccess copy() {
      try {
          ArrayTypeAccess node = (ArrayTypeAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ArrayTypeAccess fullCopy() {
        ArrayTypeAccess res = (ArrayTypeAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in NameCheck.jrag at line 150

    
  
  public void nameCheck() {
    if(decl().elementType().isUnknown())
      error("no type named " + decl().elementType().typeName());
  }

    // Declared in PrettyPrint.jadd at line 489

  
  public void toString(StringBuffer s) {
    getAccess().toString(s);
    s.append("[]");
  }

    // Declared in Expressions.jrag at line 660

  public void addArraySize(Body b, ArrayList list) {
    getAccess().addArraySize(b, list);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 22

    public ArrayTypeAccess() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 22
    public ArrayTypeAccess(Access p0) {
        setChild(p0, 0);
    }

    // Declared in java.ast at line 14


  protected int numChildren() {
    return 1;
  }

    // Declared in java.ast at line 17

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 22
    public void setAccess(Access node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Access getAccess() {
        return (Access)getChild(0);
    }

    // Declared in java.ast at line 9


    public Access getAccessNoTransform() {
        return (Access)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 22
    protected String tokenString_Package;

    // Declared in java.ast at line 3

    public void setPackage(String value) {
        tokenString_Package = value;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 22
    protected String tokenString_ID;

    // Declared in java.ast at line 3

    public void setID(String value) {
        tokenString_ID = value;
    }

    protected boolean getPackage_computed = false;
    protected String getPackage_value;
    // Declared in Arrays.jrag at line 56
 @SuppressWarnings({"unchecked", "cast"})     public String getPackage() {
        if(getPackage_computed) {
            return getPackage_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getPackage_value = getPackage_compute();
            setPackage(getPackage_value);
        if(isFinal && num == state().boundariesCrossed)
            getPackage_computed = true;
        return getPackage_value;
    }

    private String getPackage_compute() {  return getAccess().type().packageName();  }

    protected boolean getID_computed = false;
    protected String getID_value;
    // Declared in Arrays.jrag at line 57
 @SuppressWarnings({"unchecked", "cast"})     public String getID() {
        if(getID_computed) {
            return getID_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getID_value = getID_compute();
            setID(getID_value);
        if(isFinal && num == state().boundariesCrossed)
            getID_computed = true;
        return getID_value;
    }

    private String getID_compute() {  return getAccess().type().name();  }

    // Declared in DefiniteAssignment.jrag at line 360
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        ASTNode$State state = state();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {  return getAccess().isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 841
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        ASTNode$State state = state();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {  return getAccess().isDUafter(v);  }

    // Declared in LookupType.jrag at line 158
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl decl() {
        if(decl_computed) {
            return decl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        decl_value = decl_compute();
        if(isFinal && num == state().boundariesCrossed)
            decl_computed = true;
        return decl_value;
    }

    private TypeDecl decl_compute() {  return getAccess().type().arrayType();  }

    // Declared in PrettyPrint.jadd at line 804
 @SuppressWarnings({"unchecked", "cast"})     public String dumpString() {
        ASTNode$State state = state();
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return getClass().getName();  }

    // Declared in SyntacticClassification.jrag at line 130
 @SuppressWarnings({"unchecked", "cast"})     public NameType predNameType() {
        ASTNode$State state = state();
        NameType predNameType_value = predNameType_compute();
        return predNameType_value;
    }

    private NameType predNameType_compute() {  return NameType.AMBIGUOUS_NAME;  }

    // Declared in TypeHierarchyCheck.jrag at line 155
 @SuppressWarnings({"unchecked", "cast"})     public boolean staticContextQualifier() {
        ASTNode$State state = state();
        boolean staticContextQualifier_value = staticContextQualifier_compute();
        return staticContextQualifier_value;
    }

    private boolean staticContextQualifier_compute() {  return true;  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
