
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class SuperAccess extends Access implements Cloneable {
    public void flushCache() {
        super.flushCache();
        decl_computed = false;
        decl_value = null;
        type_computed = false;
        type_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public SuperAccess clone() throws CloneNotSupportedException {
        SuperAccess node = (SuperAccess)super.clone();
        node.decl_computed = false;
        node.decl_value = null;
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SuperAccess copy() {
      try {
          SuperAccess node = (SuperAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SuperAccess fullCopy() {
        SuperAccess res = (SuperAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 505

  
  public void toString(StringBuffer s) {
    s.append("super");
  }

    // Declared in TypeHierarchyCheck.jrag at line 87


  public void nameCheck() {
    if(isQualified()) {
      if(!hostType().isInnerTypeOf(decl()) && hostType() != decl())
        error("qualified super must name an enclosing type");
      if(inStaticContext()) {
        error("*** Qualified super may not occur in static context");
      }
    }
    // 8.8.5.1
    if(inExplicitConstructorInvocation() && hostType().instanceOf(decl().hostType()) )
      error("super may not be accessed in an explicit constructor invocation");
    // 8.4.3.2
    if(inStaticContext())
      error("super may not be accessed in a static context");
  }

    // Declared in Expressions.jrag at line 424

  
  public soot.Value eval(Body b) {
    return emitThis(b, decl());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 25

    public SuperAccess() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 25
    public SuperAccess(String p0) {
        setID(p0);
    }

    // Declared in java.ast at line 15


    // Declared in java.ast line 25
    public SuperAccess(beaver.Symbol p0) {
        setID(p0);
    }

    // Declared in java.ast at line 19


  protected int numChildren() {
    return 0;
  }

    // Declared in java.ast at line 22

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 25
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

    // Declared in LookupType.jrag at line 163
private TypeDecl refined_TypeScopePropagation_SuperAccess_decl()
{ return isQualified() ? qualifier().type() : hostType(); }

    // Declared in LookupType.jrag at line 161
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet decls() {
        ASTNode$State state = state();
        SimpleSet decls_value = decls_compute();
        return decls_value;
    }

    private SimpleSet decls_compute() {  return SimpleSet.emptySet;  }

    protected boolean decl_computed = false;
    protected TypeDecl decl_value;
    // Declared in Generics.jrag at line 293
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

    private TypeDecl decl_compute() {
    TypeDecl typeDecl = refined_TypeScopePropagation_SuperAccess_decl();
    if(typeDecl instanceof ParTypeDecl)
      typeDecl = ((ParTypeDecl)typeDecl).genericDecl();
    return typeDecl;
  }

    // Declared in ResolveAmbiguousNames.jrag at line 27
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSuperAccess() {
        ASTNode$State state = state();
        boolean isSuperAccess_value = isSuperAccess_compute();
        return isSuperAccess_value;
    }

    private boolean isSuperAccess_compute() {  return true;  }

    // Declared in SyntacticClassification.jrag at line 93
 @SuppressWarnings({"unchecked", "cast"})     public NameType predNameType() {
        ASTNode$State state = state();
        NameType predNameType_value = predNameType_compute();
        return predNameType_value;
    }

    private NameType predNameType_compute() {  return NameType.TYPE_NAME;  }

    // Declared in TypeAnalysis.jrag at line 288
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

    private TypeDecl type_compute() {
    TypeDecl typeDecl = decl();
    if(!typeDecl.isClassDecl())
      return unknownType();
    ClassDecl classDecl = (ClassDecl)typeDecl;
    if(!classDecl.hasSuperclass())
      return unknownType();
    return classDecl.superclass();
  }

    // Declared in TypeHierarchyCheck.jrag at line 124
 @SuppressWarnings({"unchecked", "cast"})     public boolean inExplicitConstructorInvocation() {
        ASTNode$State state = state();
        boolean inExplicitConstructorInvocation_value = getParent().Define_boolean_inExplicitConstructorInvocation(this, null);
        return inExplicitConstructorInvocation_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
