
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class SuperConstructorAccess extends ConstructorAccess implements Cloneable {
    public void flushCache() {
        super.flushCache();
        decls_computed = false;
        decls_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public SuperConstructorAccess clone() throws CloneNotSupportedException {
        SuperConstructorAccess node = (SuperConstructorAccess)super.clone();
        node.decls_computed = false;
        node.decls_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SuperConstructorAccess copy() {
      try {
          SuperConstructorAccess node = (SuperConstructorAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SuperConstructorAccess fullCopy() {
        SuperConstructorAccess res = (SuperConstructorAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in TypeHierarchyCheck.jrag at line 63


  public void nameCheck() {
    super.nameCheck();
    // 8.8.5.1
    TypeDecl c = hostType();
    TypeDecl s = c.isClassDecl() && ((ClassDecl)c).hasSuperclass() ? ((ClassDecl)c).superclass() : unknownType();
    if(isQualified()) {
      if(!s.isInnerType() || s.inStaticContext())
        error("the super type " + s.typeName() + " of " + c.typeName() +
           " is not an inner class");
    
      else if(!qualifier().type().instanceOf(s.enclosingType()))
        error("The type of this primary expression, " +
                qualifier().type().typeName() + " is not enclosing the super type, " + 
                s.typeName() + ", of " + c.typeName());
    }
    if(!isQualified() && s.isInnerType()) {
      if(!c.isInnerType()) {
        error("no enclosing instance for " + s.typeName() + " when accessed in " + this);
      }
    }
    if(s.isInnerType() && hostType().instanceOf(s.enclosingType()))
      error("cannot reference this before supertype constructor has been called");
  }

    // Declared in Transformations.jrag at line 149


  // remote collection / demand driven creation of accessor
  public void transformation() {
    // this$val
    addEnclosingVariables();
    // touch accessorIndex to force creation of private constructorAccessor
    if(decl().isPrivate() && decl().hostType() != hostType()) {
      decl().createAccessor();
    }
    super.transformation();
  }

    // Declared in EmitJimpleRefinements.jrag at line 239

  public void collectTypesToSignatures(Collection<Type> set) {
	 super.collectTypesToSignatures(set);
   addDependencyIfNeeded(set, decl().erasedConstructor().hostType());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 19

    public SuperConstructorAccess() {
        super();

        setChild(new List(), 0);

    }

    // Declared in java.ast at line 11


    // Declared in java.ast line 19
    public SuperConstructorAccess(String p0, List<Expr> p1) {
        setID(p0);
        setChild(p1, 0);
    }

    // Declared in java.ast at line 17


    // Declared in java.ast line 19
    public SuperConstructorAccess(beaver.Symbol p0, List<Expr> p1) {
        setID(p0);
        setChild(p1, 0);
    }

    // Declared in java.ast at line 22


  protected int numChildren() {
    return 1;
  }

    // Declared in java.ast at line 25

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 18
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 18
    public void setArgList(List<Expr> list) {
        setChild(list, 0);
    }

    // Declared in java.ast at line 6


    public int getNumArg() {
        return getArgList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Expr getArg(int i) {
        return (Expr)getArgList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addArg(Expr node) {
        List<Expr> list = (parent == null || state == null) ? getArgListNoTransform() : getArgList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addArgNoTransform(Expr node) {
        List<Expr> list = getArgListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setArg(Expr node, int i) {
        List<Expr> list = getArgList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Expr> getArgs() {
        return getArgList();
    }

    // Declared in java.ast at line 31

    public List<Expr> getArgsNoTransform() {
        return getArgListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Expr> getArgList() {
        List<Expr> list = (List<Expr>)getChild(0);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Expr> getArgListNoTransform() {
        return (List<Expr>)getChildNoTransform(0);
    }

    // Declared in GenericsCodegen.jrag at line 255


    public soot.Value eval(Body b) {
    ConstructorDecl c = decl().erasedConstructor();
    // this
    Local base = b.emitThis(hostType());

    int index = 0;
    ArrayList list = new ArrayList();
    if(c.needsEnclosing()) {
      if(hasPrevExpr() && !prevExpr().isTypeAccess()) {
        list.add(asImmediate(b, prevExpr().eval(b)));
      }
      else {
        if(hostType().needsSuperEnclosing()) {
          soot.Type type = ((ClassDecl)hostType()).superclass().enclosingType().getSootType();
          if(hostType().needsEnclosing())
            list.add(asImmediate(b, b.newParameterRef(type, 1, this)));
          else
            list.add(asImmediate(b, b.newParameterRef(type, 0, this)));
        }
        else {
          list.add(emitThis(b, superConstructorQualifier(c.hostType().enclosingType())));
        }
      }
    }

    // args
    for(int i = 0; i < getNumArg(); i++)
      list.add(asImmediate(b, 
         getArg(i).type().emitCastTo(b, getArg(i), c.getParameter(i).type()))); // MethodInvocationConversion
 
    if(decl().isPrivate() && decl().hostType() != hostType()) {
      list.add(asImmediate(b, soot.jimple.NullConstant.v()));
      b.add(
        b.newInvokeStmt(
          b.newSpecialInvokeExpr(base, decl().erasedConstructor().createAccessor().sootRef(), list, this),
          this
        )
      );
      return base;
    }
    else {
      return b.newSpecialInvokeExpr(base, c.sootRef(), list, this);
    }
  }

    // Declared in DefiniteAssignment.jrag at line 299
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        ASTNode$State state = state();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {  return isDAbefore(v);  }

    // Declared in DefiniteAssignment.jrag at line 755
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        ASTNode$State state = state();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {  return isDUbefore(v);  }

    // Declared in MethodSignature.jrag at line 62
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet decls() {
        if(decls_computed) {
            return decls_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        decls_value = decls_compute();
        if(isFinal && num == state().boundariesCrossed)
            decls_computed = true;
        return decls_value;
    }

    private SimpleSet decls_compute() {
    Collection c = hasPrevExpr() && !prevExpr().isTypeAccess() ?
      hostType().lookupSuperConstructor() : lookupSuperConstructor();
    return chooseConstructor(c, getArgList());
  }

    // Declared in QualifiedNames.jrag at line 20
 @SuppressWarnings({"unchecked", "cast"})     public String name() {
        ASTNode$State state = state();
        String name_value = name_compute();
        return name_value;
    }

    private String name_compute() {  return "super";  }

    // Declared in ResolveAmbiguousNames.jrag at line 51
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSuperConstructorAccess() {
        ASTNode$State state = state();
        boolean isSuperConstructorAccess_value = isSuperConstructorAccess_compute();
        return isSuperConstructorAccess_value;
    }

    private boolean isSuperConstructorAccess_compute() {  return true;  }

    // Declared in SyntacticClassification.jrag at line 96
 @SuppressWarnings({"unchecked", "cast"})     public NameType predNameType() {
        ASTNode$State state = state();
        NameType predNameType_value = predNameType_compute();
        return predNameType_value;
    }

    private NameType predNameType_compute() {  return NameType.EXPRESSION_NAME;  }

    // Declared in LookupConstructor.jrag at line 19
 @SuppressWarnings({"unchecked", "cast"})     public Collection lookupSuperConstructor() {
        ASTNode$State state = state();
        Collection lookupSuperConstructor_value = getParent().Define_Collection_lookupSuperConstructor(this, null);
        return lookupSuperConstructor_value;
    }

    // Declared in TypeCheck.jrag at line 503
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl enclosingInstance() {
        ASTNode$State state = state();
        TypeDecl enclosingInstance_value = getParent().Define_TypeDecl_enclosingInstance(this, null);
        return enclosingInstance_value;
    }

    // Declared in LookupType.jrag at line 89
    public boolean Define_boolean_hasPackage(ASTNode caller, ASTNode child, String packageName) {
        if(caller == getArgListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return unqualifiedScope().hasPackage(packageName);
        }
        return super.Define_boolean_hasPackage(caller, child, packageName);
    }

    // Declared in LookupVariable.jrag at line 132
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        if(caller == getArgListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return unqualifiedScope().lookupVariable(name);
        }
        return super.Define_SimpleSet_lookupVariable(caller, child, name);
    }

    // Declared in TypeHierarchyCheck.jrag at line 131
    public boolean Define_boolean_inExplicitConstructorInvocation(ASTNode caller, ASTNode child) {
        if(caller == getArgListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return true;
        }
        return super.Define_boolean_inExplicitConstructorInvocation(caller, child);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
