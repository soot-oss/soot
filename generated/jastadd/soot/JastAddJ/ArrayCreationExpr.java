
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class ArrayCreationExpr extends PrimaryExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
        numArrays_computed = false;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ArrayCreationExpr clone() throws CloneNotSupportedException {
        ArrayCreationExpr node = (ArrayCreationExpr)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.numArrays_computed = false;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ArrayCreationExpr copy() {
      try {
          ArrayCreationExpr node = (ArrayCreationExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ArrayCreationExpr fullCopy() {
        ArrayCreationExpr res = (ArrayCreationExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 355


  public void toString(StringBuffer s) {
    s.append("new ");
    getTypeAccess().toString(s);
    if(hasArrayInit()) {
      getArrayInit().toString(s);
    }
  }

    // Declared in Expressions.jrag at line 633


  public soot.Value eval(Body b) {
    if(hasArrayInit()) {
      return getArrayInit().eval(b);
    }
    else {
      ArrayList list = new ArrayList();
      getTypeAccess().addArraySize(b, list);
      if(numArrays() == 1) {
        soot.Value size = (soot.Value)list.get(0);
        return b.newNewArrayExpr(
          type().componentType().getSootType(),
          asImmediate(b, size),
          this
        );
      }
      else {
        return b.newNewMultiArrayExpr(
          (soot.ArrayType)type().getSootType(),
          list,
          this
        );
      }
    }
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 136

    public ArrayCreationExpr() {
        super();

        setChild(new Opt(), 1);

    }

    // Declared in java.ast at line 11


    // Declared in java.ast line 136
    public ArrayCreationExpr(Access p0, Opt<ArrayInit> p1) {
        setChild(p0, 0);
        setChild(p1, 1);
    }

    // Declared in java.ast at line 16


  protected int numChildren() {
    return 2;
  }

    // Declared in java.ast at line 19

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 136
    public void setTypeAccess(Access node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Access getTypeAccess() {
        return (Access)getChild(0);
    }

    // Declared in java.ast at line 9


    public Access getTypeAccessNoTransform() {
        return (Access)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 136
    public void setArrayInitOpt(Opt<ArrayInit> opt) {
        setChild(opt, 1);
    }

    // Declared in java.ast at line 6


    public boolean hasArrayInit() {
        return getArrayInitOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public ArrayInit getArrayInit() {
        return (ArrayInit)getArrayInitOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setArrayInit(ArrayInit node) {
        getArrayInitOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<ArrayInit> getArrayInitOpt() {
        return (Opt<ArrayInit>)getChild(1);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<ArrayInit> getArrayInitOptNoTransform() {
        return (Opt<ArrayInit>)getChildNoTransform(1);
    }

    // Declared in DefiniteAssignment.jrag at line 433
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterCreation(Variable v) {
        ASTNode$State state = state();
        boolean isDAafterCreation_Variable_value = isDAafterCreation_compute(v);
        return isDAafterCreation_Variable_value;
    }

    private boolean isDAafterCreation_compute(Variable v) {  return getTypeAccess().isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 434
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        ASTNode$State state = state();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {  return hasArrayInit() ? getArrayInit().isDAafter(v) : isDAafterCreation(v);  }

    // Declared in DefiniteAssignment.jrag at line 864
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterCreation(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterCreation_Variable_value = isDUafterCreation_compute(v);
        return isDUafterCreation_Variable_value;
    }

    private boolean isDUafterCreation_compute(Variable v) {  return getTypeAccess().isDUafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 865
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        ASTNode$State state = state();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {  return hasArrayInit() ? getArrayInit().isDUafter(v) : isDUafterCreation(v);  }

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in TypeAnalysis.jrag at line 312
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        if(type_computed) {
            return type_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        type_value = type_compute();
        if(isFinal && num == state().boundariesCrossed)
            type_computed = true;
        return type_value;
    }

    private TypeDecl type_compute() {  return getTypeAccess().type();  }

    protected boolean numArrays_computed = false;
    protected int numArrays_value;
    // Declared in InnerClasses.jrag at line 69
 @SuppressWarnings({"unchecked", "cast"})     public int numArrays() {
        if(numArrays_computed) {
            return numArrays_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        numArrays_value = numArrays_compute();
        if(isFinal && num == state().boundariesCrossed)
            numArrays_computed = true;
        return numArrays_value;
    }

    private int numArrays_compute() {
    int i = type().dimension();
    Access a = getTypeAccess();
    while(a instanceof ArrayTypeAccess && !(a instanceof ArrayTypeWithSizeAccess)) {
      i--;
      a = ((ArrayTypeAccess)a).getAccess();
    }
    return i;
  }

    // Declared in DefiniteAssignment.jrag at line 435
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getArrayInitOptNoTransform()) {
            return isDAafterCreation(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 867
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getArrayInitOptNoTransform()) {
            return isDUafterCreation(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in SyntacticClassification.jrag at line 87
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeAccessNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

    // Declared in TypeAnalysis.jrag at line 262
    public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
        if(caller == getArrayInitOptNoTransform()) {
            return type();
        }
        return getParent().Define_TypeDecl_declType(this, caller);
    }

    // Declared in InnerClasses.jrag at line 63
    public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
        if(caller == getArrayInitOptNoTransform()) {
            return type().componentType();
        }
        return getParent().Define_TypeDecl_expectedType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
