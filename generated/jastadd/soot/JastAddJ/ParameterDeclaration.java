
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class ParameterDeclaration extends ASTNode<ASTNode> implements Cloneable, SimpleSet, Iterator, Variable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
        sourceVariableDecl_computed = false;
        sourceVariableDecl_value = null;
        localNum_computed = false;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParameterDeclaration clone() throws CloneNotSupportedException {
        ParameterDeclaration node = (ParameterDeclaration)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.sourceVariableDecl_computed = false;
        node.sourceVariableDecl_value = null;
        node.localNum_computed = false;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParameterDeclaration copy() {
      try {
          ParameterDeclaration node = (ParameterDeclaration)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParameterDeclaration fullCopy() {
        ParameterDeclaration res = (ParameterDeclaration)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in DataStructures.jrag at line 101

  public SimpleSet add(Object o) {
    return new SimpleSetImpl().add(this).add(o);
  }

    // Declared in DataStructures.jrag at line 107

  private ParameterDeclaration iterElem;

    // Declared in DataStructures.jrag at line 108

  public Iterator iterator() { iterElem = this; return this; }

    // Declared in DataStructures.jrag at line 109

  public boolean hasNext() { return iterElem != null; }

    // Declared in DataStructures.jrag at line 110

  public Object next() { Object o = iterElem; iterElem = null; return o; }

    // Declared in DataStructures.jrag at line 111

  public void remove() { throw new UnsupportedOperationException(); }

    // Declared in NameCheck.jrag at line 328

  
  public void nameCheck() {
    SimpleSet decls = outerScope().lookupVariable(name());
    for(Iterator iter = decls.iterator(); iter.hasNext(); ) {
      Variable var = (Variable)iter.next();
      if(var instanceof VariableDeclaration) {
        VariableDeclaration decl = (VariableDeclaration)var;
	      if(decl.enclosingBodyDecl() == enclosingBodyDecl())
  	      error("duplicate declaration of local variable " + name());
      }
      else if(var instanceof ParameterDeclaration) {
        ParameterDeclaration decl = (ParameterDeclaration)var;
	      if(decl.enclosingBodyDecl() == enclosingBodyDecl())
          error("duplicate declaration of local variable " + name());
      }
    }

    // 8.4.1  
    if(!lookupVariable(name()).contains(this)) {
      error("duplicate declaration of parameter " + name());
    }
  }

    // Declared in NodeConstructors.jrag at line 11

  public ParameterDeclaration(Access type, String name) {
    this(new Modifiers(new List()), type, name);
  }

    // Declared in NodeConstructors.jrag at line 14

  public ParameterDeclaration(TypeDecl type, String name) {
    this(new Modifiers(new List()), type.createQualifiedAccess(), name);
  }

    // Declared in PrettyPrint.jadd at line 232


  public void toString(StringBuffer s) {
    getModifiers().toString(s);
    getTypeAccess().toString(s);
    s.append(" " + name());
  }

    // Declared in EmitJimple.jrag at line 397


  public void jimplify2(Body b) {
    b.setLine(this);
    local = b.newLocal(name(), type().getSootType());
    b.add(b.newIdentityStmt(local, b.newParameterRef(type().getSootType(), localNum(), this),this));
  }

    // Declared in EmitJimple.jrag at line 402

  public Local local;

    // Declared in java.ast at line 3
    // Declared in java.ast line 84

    public ParameterDeclaration() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 84
    public ParameterDeclaration(Modifiers p0, Access p1, String p2) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
    }

    // Declared in java.ast at line 17


    // Declared in java.ast line 84
    public ParameterDeclaration(Modifiers p0, Access p1, beaver.Symbol p2) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
    }

    // Declared in java.ast at line 23


  protected int numChildren() {
    return 2;
  }

    // Declared in java.ast at line 26

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 84
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in java.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 84
    public void setTypeAccess(Access node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Access getTypeAccess() {
        return (Access)getChild(1);
    }

    // Declared in java.ast at line 9


    public Access getTypeAccessNoTransform() {
        return (Access)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 84
    protected String tokenString_ID;

    // Declared in java.ast at line 3

    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 6

    public int IDstart;

    // Declared in java.ast at line 7

    public int IDend;

    // Declared in java.ast at line 8

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 15

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in DataStructures.jrag at line 99
 @SuppressWarnings({"unchecked", "cast"})     public int size() {
        ASTNode$State state = state();
        int size_value = size_compute();
        return size_value;
    }

    private int size_compute() {  return 1;  }

    // Declared in DataStructures.jrag at line 100
 @SuppressWarnings({"unchecked", "cast"})     public boolean isEmpty() {
        ASTNode$State state = state();
        boolean isEmpty_value = isEmpty_compute();
        return isEmpty_value;
    }

    private boolean isEmpty_compute() {  return false;  }

    // Declared in DataStructures.jrag at line 104
 @SuppressWarnings({"unchecked", "cast"})     public boolean contains(Object o) {
        ASTNode$State state = state();
        boolean contains_Object_value = contains_compute(o);
        return contains_Object_value;
    }

    private boolean contains_compute(Object o) {  return this == o;  }

    // Declared in Modifiers.jrag at line 218
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSynthetic() {
        ASTNode$State state = state();
        boolean isSynthetic_value = isSynthetic_compute();
        return isSynthetic_value;
    }

    private boolean isSynthetic_compute() {  return getModifiers().isSynthetic();  }

    // Declared in PrettyPrint.jadd at line 812
 @SuppressWarnings({"unchecked", "cast"})     public String dumpString() {
        ASTNode$State state = state();
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return getClass().getName() + " [" + getID() + "]";  }

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in TypeAnalysis.jrag at line 253
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

    // Declared in VariableDeclaration.jrag at line 69
 @SuppressWarnings({"unchecked", "cast"})     public boolean isClassVariable() {
        ASTNode$State state = state();
        boolean isClassVariable_value = isClassVariable_compute();
        return isClassVariable_value;
    }

    private boolean isClassVariable_compute() {  return false;  }

    // Declared in VariableDeclaration.jrag at line 70
 @SuppressWarnings({"unchecked", "cast"})     public boolean isInstanceVariable() {
        ASTNode$State state = state();
        boolean isInstanceVariable_value = isInstanceVariable_compute();
        return isInstanceVariable_value;
    }

    private boolean isInstanceVariable_compute() {  return false;  }

    // Declared in VariableDeclaration.jrag at line 74
 @SuppressWarnings({"unchecked", "cast"})     public boolean isLocalVariable() {
        ASTNode$State state = state();
        boolean isLocalVariable_value = isLocalVariable_compute();
        return isLocalVariable_value;
    }

    private boolean isLocalVariable_compute() {  return false;  }

    // Declared in VariableDeclaration.jrag at line 92
 @SuppressWarnings({"unchecked", "cast"})     public boolean isFinal() {
        ASTNode$State state = state();
        boolean isFinal_value = isFinal_compute();
        return isFinal_value;
    }

    private boolean isFinal_compute() {  return getModifiers().isFinal();  }

    // Declared in VariableDeclaration.jrag at line 93
 @SuppressWarnings({"unchecked", "cast"})     public boolean isBlank() {
        ASTNode$State state = state();
        boolean isBlank_value = isBlank_compute();
        return isBlank_value;
    }

    private boolean isBlank_compute() {  return true;  }

    // Declared in VariableDeclaration.jrag at line 94
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStatic() {
        ASTNode$State state = state();
        boolean isStatic_value = isStatic_compute();
        return isStatic_value;
    }

    private boolean isStatic_compute() {  return false;  }

    // Declared in VariableDeclaration.jrag at line 96
 @SuppressWarnings({"unchecked", "cast"})     public String name() {
        ASTNode$State state = state();
        String name_value = name_compute();
        return name_value;
    }

    private String name_compute() {  return getID();  }

    // Declared in VariableDeclaration.jrag at line 98
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasInit() {
        ASTNode$State state = state();
        boolean hasInit_value = hasInit_compute();
        return hasInit_value;
    }

    private boolean hasInit_compute() {  return false;  }

    // Declared in VariableDeclaration.jrag at line 99
 @SuppressWarnings({"unchecked", "cast"})     public Expr getInit() {
        ASTNode$State state = state();
        Expr getInit_value = getInit_compute();
        return getInit_value;
    }

    private Expr getInit_compute() { throw new UnsupportedOperationException(); }

    // Declared in VariableDeclaration.jrag at line 100
 @SuppressWarnings({"unchecked", "cast"})     public Constant constant() {
        ASTNode$State state = state();
        Constant constant_value = constant_compute();
        return constant_value;
    }

    private Constant constant_compute() { throw new UnsupportedOperationException(); }

    protected boolean sourceVariableDecl_computed = false;
    protected Variable sourceVariableDecl_value;
    // Declared in Generics.jrag at line 1277
 @SuppressWarnings({"unchecked", "cast"})     public Variable sourceVariableDecl() {
        if(sourceVariableDecl_computed) {
            return sourceVariableDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sourceVariableDecl_value = sourceVariableDecl_compute();
        if(isFinal && num == state().boundariesCrossed)
            sourceVariableDecl_computed = true;
        return sourceVariableDecl_value;
    }

    private Variable sourceVariableDecl_compute() {  return this;  }

    // Declared in VariableArityParameters.jrag at line 35
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVariableArity() {
        ASTNode$State state = state();
        boolean isVariableArity_value = isVariableArity_compute();
        return isVariableArity_value;
    }

    private boolean isVariableArity_compute() {  return false;  }

    // Declared in LookupVariable.jrag at line 22
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet lookupVariable(String name) {
        ASTNode$State state = state();
        SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
        return lookupVariable_String_value;
    }

    // Declared in NameCheck.jrag at line 288
 @SuppressWarnings({"unchecked", "cast"})     public VariableScope outerScope() {
        ASTNode$State state = state();
        VariableScope outerScope_value = getParent().Define_VariableScope_outerScope(this, null);
        return outerScope_value;
    }

    // Declared in NameCheck.jrag at line 349
 @SuppressWarnings({"unchecked", "cast"})     public BodyDecl enclosingBodyDecl() {
        ASTNode$State state = state();
        BodyDecl enclosingBodyDecl_value = getParent().Define_BodyDecl_enclosingBodyDecl(this, null);
        return enclosingBodyDecl_value;
    }

    // Declared in TypeAnalysis.jrag at line 586
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl hostType() {
        ASTNode$State state = state();
        TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
        return hostType_value;
    }

    // Declared in VariableDeclaration.jrag at line 71
 @SuppressWarnings({"unchecked", "cast"})     public boolean isMethodParameter() {
        ASTNode$State state = state();
        boolean isMethodParameter_value = getParent().Define_boolean_isMethodParameter(this, null);
        return isMethodParameter_value;
    }

    // Declared in VariableDeclaration.jrag at line 72
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstructorParameter() {
        ASTNode$State state = state();
        boolean isConstructorParameter_value = getParent().Define_boolean_isConstructorParameter(this, null);
        return isConstructorParameter_value;
    }

    // Declared in VariableDeclaration.jrag at line 73
 @SuppressWarnings({"unchecked", "cast"})     public boolean isExceptionHandlerParameter() {
        ASTNode$State state = state();
        boolean isExceptionHandlerParameter_value = getParent().Define_boolean_isExceptionHandlerParameter(this, null);
        return isExceptionHandlerParameter_value;
    }

    protected boolean localNum_computed = false;
    protected int localNum_value;
    // Declared in LocalNum.jrag at line 13
 @SuppressWarnings({"unchecked", "cast"})     public int localNum() {
        if(localNum_computed) {
            return localNum_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        localNum_value = getParent().Define_int_localNum(this, null);
        if(isFinal && num == state().boundariesCrossed)
            localNum_computed = true;
        return localNum_value;
    }

    // Declared in Modifiers.jrag at line 286
    public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeFinal(this, caller);
    }

    // Declared in Annotations.jrag at line 83
    public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
        if(caller == getModifiersNoTransform()) {
            return name.equals("PARAMETER");
        }
        return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
    }

    // Declared in Enums.jrag at line 79
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeAccessNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
