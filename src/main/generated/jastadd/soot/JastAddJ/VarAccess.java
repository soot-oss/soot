/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
package soot.JastAddJ;

import java.util.HashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;
/**
 * @production VarAccess : {@link Access} ::= <span class="component">&lt;ID:String&gt;</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:19
 */
public class VarAccess extends Access implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isConstant_visited = -1;
    isConstant_computed = false;
    isConstant_initialized = false;
    isDAafter_Variable_values = null;
    decls_computed = false;
    decls_value = null;
    decl_computed = false;
    decl_value = null;
    isFieldAccess_computed = false;
    type_computed = false;
    type_value = null;
    base_Body_values = null;
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VarAccess clone() throws CloneNotSupportedException {
    VarAccess node = (VarAccess)super.clone();
    node.isConstant_visited = -1;
    node.isConstant_computed = false;
    node.isConstant_initialized = false;
    node.isDAafter_Variable_values = null;
    node.decls_computed = false;
    node.decls_value = null;
    node.decl_computed = false;
    node.decl_value = null;
    node.isFieldAccess_computed = false;
    node.type_computed = false;
    node.type_value = null;
    node.base_Body_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VarAccess copy() {
    try {
      VarAccess node = (VarAccess) clone();
      node.parent = null;
      if(children != null)
        node.children = (ASTNode[]) children.clone();
      return node;
    } catch (CloneNotSupportedException e) {
      throw new Error("Error: clone not supported for " +
        getClass().getName());
    }
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VarAccess fullCopy() {
    VarAccess tree = (VarAccess) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        ASTNode child = (ASTNode) children[i];
        if(child != null) {
          child = child.fullCopy();
          tree.setChild(child, i);
        }
      }
    }
    return tree;
  }
  /**
   * @ast method 
   * @aspect DefiniteAssignment
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:94
   */
  public void definiteAssignment() {
    if(isSource()) {
      if(decl() instanceof VariableDeclaration) {
        VariableDeclaration v = (VariableDeclaration)decl();
        //System.err.println("Is " + v + " final? " + v.isFinal() + ", DAbefore: " + isDAbefore(v));
        if(v.isValue()) {
        }
        else if(v.isBlankFinal()) {
          //if(!isDAbefore(v) && !v.hasInit() && !v.getInit().isConstant())
          if(!isDAbefore(v))
            error("Final variable " + v.name() + " is not assigned before used");
        }
        else {
          //if(!v.hasInit() && !isDAbefore(v)) {
          if(!isDAbefore(v))
            error("Local variable " + v.name() + " is not assigned before used");
        }
      }
      
      else if(decl() instanceof FieldDeclaration && !isQualified()) {
        FieldDeclaration f = (FieldDeclaration)decl();
        //if(f.isFinal() && f.isInstanceVariable() && !isDAbefore(f)) {
        //if(f.isFinal() && !isDAbefore(f) && (!f.hasInit() || !f.getInit().isConstant())) {
        //if(f.isFinal() && (!f.hasInit() || !f.getInit().isConstant()) && !isDAbefore(f)) {
        if(f.isFinal() && !f.hasInit() && !isDAbefore(f)) {
          error("Final field " + f + " is not assigned before used");
        }
      }
      
    }
    if(isDest()) {
      Variable v = decl();
      // Blank final field
      if(v.isFinal() && v.isBlank() && !hostType().instanceOf(v.hostType()))
        error("The final variable is not a blank final in this context, so it may not be assigned.");
      else if(v.isFinal() && isQualified() && (!qualifier().isThisAccess() || ((Access)qualifier()).isQualified()))
        error("the blank final field " + v.name() + " may only be assigned by simple name");
      
      // local variable or parameter
      else if(v instanceof VariableDeclaration) {
        VariableDeclaration var = (VariableDeclaration)v;
        //System.out.println("### is variable");
        if(!var.isValue() && var.getParent().getParent().getParent() instanceof SwitchStmt && var.isFinal()) {
          if(!isDUbefore(var))
            error("Final variable " + var.name() + " may only be assigned once");
        }
        else if(var.isValue()) {
          if(var.hasInit() || !isDUbefore(var))
            error("Final variable " + var.name() + " may only be assigned once");
        }
        else if(var.isBlankFinal()) {
          if(var.hasInit() || !isDUbefore(var))
            error("Final variable " + var.name() + " may only be assigned once");
        }
        if(var.isFinal() && (var.hasInit() || !isDUbefore(var))) {
        //if(var.isFinal() && ((var.hasInit() && var.getInit().isConstant()) || !isDUbefore(var))) {
        }
      }
      // field
      else if(v instanceof FieldDeclaration) {
        FieldDeclaration f = (FieldDeclaration)v;
        if(f.isFinal()) {
          if(f.hasInit())
            error("initialized field " + f.name() + " can not be assigned");
          else {
            BodyDecl bodyDecl = enclosingBodyDecl();
            if(!(bodyDecl instanceof ConstructorDecl) && !(bodyDecl instanceof InstanceInitializer) && !(bodyDecl instanceof StaticInitializer) && !(bodyDecl instanceof FieldDeclaration))
              error("final field " + f.name() + " may only be assigned in constructors and initializers");
            else if(!isDUbefore(f))
              error("Final field " + f.name() + " may only be assigned once");
          }
        }
      }
      else if(v.isParameter()) {
        // 8.4.1
        if(v.isFinal()) {
          error("Final parameter " + v.name() + " may not be assigned");
        }
      }
      
    }
  }
  /**
   * @ast method 
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:457
   */
  protected boolean checkDUeverywhere(Variable v) {
    if(isDest() && decl() == v)
      return false;
    return super.checkDUeverywhere(v);
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:182
   */
  public void nameCheck() {
    if(decls().isEmpty() && (!isQualified() || !qualifier().type().isUnknown() || qualifier().isPackageAccess()))
      error("no field named " + name() + " is accessible");
    if(decls().size() > 1) {
      StringBuffer s = new StringBuffer();
      s.append("several fields named " + name());
      for(Iterator iter = decls().iterator(); iter.hasNext(); ) {
        Variable v = (Variable)iter.next();
        s.append("\n    " + v.type().typeName() + "." + v.name() + " declared in " + v.hostType().typeName());
      }
      error(s.toString());
    }
      
    // 8.8.5.1
    if(inExplicitConstructorInvocation() && !isQualified() && decl().isInstanceVariable() && hostType() == decl().hostType())
      error("instance variable " + name() + " may not be accessed in an explicit constructor invocation");

    Variable v = decl();
    if(!v.isFinal() && !v.isClassVariable() && !v.isInstanceVariable() && v.hostType() != hostType())
      error("A parameter/variable used but not declared in an inner class must be declared final");

    // 8.3.2.3
    if((decl().isInstanceVariable() || decl().isClassVariable()) && !isQualified()) {
      if(hostType() != null && !hostType().declaredBeforeUse(decl(), this)) {
        if(inSameInitializer() && !simpleAssignment() && inDeclaringClass()) {
          BodyDecl b = closestBodyDecl(hostType());
          error("variable " + decl().name() + " is used in " + b + " before it is declared");
        }
      }
    }

  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:216
   */
  public BodyDecl closestBodyDecl(TypeDecl t) {
    ASTNode node = this;
    while(!(node.getParent().getParent() instanceof Program) && node.getParent().getParent() != t) {
      node = node.getParent();
    }
    if(node instanceof BodyDecl)
      return (BodyDecl)node;
    return null;
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:38
   */
  public VarAccess(String name, int start, int end) {
    this(name);
    this.start = this.IDstart = start;
    this.end = this.IDend = end;
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:453
   */
  public void toString(StringBuffer s) {
    s.append(name());
  }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:344
   */
  public void checkModifiers() {
    if(decl() instanceof FieldDeclaration) {
      FieldDeclaration f = (FieldDeclaration)decl();
      if(f.isDeprecated() &&
        !withinDeprecatedAnnotation() &&
        hostType().topLevelType() != f.hostType().topLevelType() &&
        !withinSuppressWarnings("deprecation"))
          warning(f.name() + " in " + f.hostType().typeName() + " has been deprecated");
    }
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:461
   */
  protected void checkEnum(EnumDecl enumDecl) {
    super.checkEnum(enumDecl);
    if(decl().isStatic() && decl().hostType() == enumDecl && !isConstant())
      error("may not reference a static field of an enum type from here");
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:25
   */
  private TypeDecl refined_InnerClasses_VarAccess_fieldQualifierType() {
    if(hasPrevExpr())
      return prevExpr().type();
    TypeDecl typeDecl = hostType();
    while(typeDecl != null && !typeDecl.hasField(name()))
      typeDecl = typeDecl.enclosingType();
    if(typeDecl != null)
      return typeDecl;
    return decl().hostType();
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:162
   */
  public void collectEnclosingVariables(HashSet set, TypeDecl typeDecl) {
    Variable v = decl();
    if(!v.isInstanceVariable() && !v.isClassVariable() && v.hostType() == typeDecl)
      set.add(v);
    super.collectEnclosingVariables(set, typeDecl);
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:103
   */
  public void transformation() {
    Variable v = decl();
    if(v instanceof FieldDeclaration) {
      FieldDeclaration f = (FieldDeclaration)v;
      if(requiresAccessor()) {
        TypeDecl typeDecl = fieldQualifierType();
        if(isSource())
          f.createAccessor(typeDecl);
        if(isDest())
          f.createAccessorWrite(typeDecl);
      }
    }
    super.transformation();
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:194
   */
  public soot.Value refined_Expressions_VarAccess_eval(Body b) {
    Variable v = decl();
    if(v instanceof VariableDeclaration) {
      VariableDeclaration decl = (VariableDeclaration)v;
      if(decl.hostType() == hostType())
        return decl.local;
      else
        return emitLoadLocalInNestedClass(b, decl);
    }
    else if(v instanceof ParameterDeclaration) {
      ParameterDeclaration decl = (ParameterDeclaration)v;
      if(decl.hostType() == hostType())
        return decl.local;
      else
        return emitLoadLocalInNestedClass(b, decl);
    }
    else if(v instanceof FieldDeclaration) {
      FieldDeclaration f = (FieldDeclaration)v;
      if(f.hostType().isArrayDecl() && f.name().equals("length")) {
        return b.newLengthExpr(asImmediate(b, createLoadQualifier(b)), this);
      }
      if(f.isStatic()) {
        if(isQualified() && !qualifier().isTypeAccess())
          b.newTemp(qualifier().eval(b));
        if(requiresAccessor()) {
          ArrayList list = new ArrayList();
          return b.newStaticInvokeExpr(f.createAccessor(fieldQualifierType()).sootRef(), list, this);
        }
        else
          return b.newStaticFieldRef(sootRef(), this);
      }
      else {
        if(requiresAccessor()) {
          soot.Local base = base(b);
          ArrayList list = new ArrayList();
          list.add(base);
          return b.newStaticInvokeExpr(f.createAccessor(fieldQualifierType()).sootRef(), list, this);
        }
        else {
          soot.Local base = createLoadQualifier(b);
          return b.newInstanceFieldRef(base, sootRef(), this);
        }
      }
    }
    else
      return super.eval(b);
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:270
   */
  public soot.Value refined_Expressions_VarAccess_emitStore(Body b, soot.Value lvalue, soot.Value rvalue, ASTNode location) {
    Variable v = decl();
    if(v instanceof FieldDeclaration) {
      FieldDeclaration f = (FieldDeclaration)v;
      if(requiresAccessor()) {
        if(f.isStatic()) {
          ArrayList list = new ArrayList();
          list.add(rvalue);
          return asLocal(b, b.newStaticInvokeExpr(f.createAccessorWrite(fieldQualifierType()).sootRef(), list, location));
        }
        else {
          soot.Local base = base(b);
          ArrayList list = new ArrayList();
          list.add(base);
          list.add(asLocal(b, rvalue, lvalue.getType()));
          return asLocal(b, b.newStaticInvokeExpr(f.createAccessorWrite(fieldQualifierType()).sootRef(), list, location));
        }
      }
    }
    return super.emitStore(b, lvalue, rvalue, location);
  }
  /**
   * @ast method 
   * @aspect EmitJimpleRefinements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:220
   */
  public void collectTypesToSignatures(Collection<Type> set) {
	 super.collectTypesToSignatures(set);
   // if we access a field declaration we load the qualifying type 
   // the element type is used to cater for reading the field length in an array type
	 if(decl() instanceof FieldDeclaration)
     addDependencyIfNeeded(set, fieldQualifierType());
  }
  /**
   * @ast method 
   * 
   */
  public VarAccess() {
    super();


  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @ast method 
   * 
   */
  public void init$Children() {
  }
  /**
   * @ast method 
   * 
   */
  public VarAccess(String p0) {
    setID(p0);
  }
  /**
   * @ast method 
   * 
   */
  public VarAccess(beaver.Symbol p0) {
    setID(p0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 0;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Replaces the lexeme ID.
   * @param value The new value for the lexeme ID.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  protected String tokenString_ID;
  /**
   * @ast method 
   * 
   */
  
  public int IDstart;
  /**
   * @ast method 
   * 
   */
  
  public int IDend;
  /**
   * JastAdd-internal setter for lexeme ID using the Beaver parser.
   * @apilevel internal
   * @ast method 
   * 
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Retrieves the value for the lexeme ID.
   * @return The value for the lexeme ID.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:312
   */
    protected TypeDecl refined_GenericsCodegen_VarAccess_fieldQualifierType() {
    TypeDecl typeDecl = refined_InnerClasses_VarAccess_fieldQualifierType();
    return typeDecl == null ? null : typeDecl.erasure();
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:38
   */
    public soot.Value eval(Body b) {
    Variable v = decl();
    soot.Value result;
    if(v instanceof FieldDeclaration) {
      FieldDeclaration f = ((FieldDeclaration)v).erasedField();
      if(f.hostType().isArrayDecl() && f.name().equals("length")) {
        return b.newLengthExpr(asImmediate(b, createLoadQualifier(b)), this);
      }
      if(f.isStatic()) {
        if(isQualified() && !qualifier().isTypeAccess())
          b.newTemp(qualifier().eval(b));
        if(requiresAccessor()) {
          ArrayList list = new ArrayList();
          result =  b.newStaticInvokeExpr(f.createAccessor(fieldQualifierType().erasure()).sootRef(), list, this);
        }
        else
          result = b.newStaticFieldRef(sootRef(), this);
      }
      else {
        if(requiresAccessor()) {
          soot.Local base = base(b);
          ArrayList list = new ArrayList();
          list.add(base);
          result = b.newStaticInvokeExpr(f.createAccessor(fieldQualifierType().erasure()).sootRef(), list, this);
        }
        else {
          soot.Local base = createLoadQualifier(b);
          result = b.newInstanceFieldRef(base, sootRef(), this);
        }
      }
      if(f.type() != v.type())
        result = f.type().emitCastTo(b, result, v.type(), this);
      return result;
    }
    else
      return refined_Expressions_VarAccess_eval(b);
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:75
   */
    private SootFieldRef sootRef() {
    FieldDeclaration decl = ((FieldDeclaration)decl()).erasedField();
    SootFieldRef ref = Scene.v().makeFieldRef(
      fieldQualifierType().getSootClassDecl(),
      decl.name(),
      decl.type().getSootType(),
      decl.isStatic()
    );
    return ref;
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:86
   */
    public soot.Value emitStore(Body b, soot.Value lvalue, soot.Value rvalue, ASTNode location) {
    Variable v = decl();
    if(v instanceof FieldDeclaration) {
      FieldDeclaration f = ((FieldDeclaration)v).erasedField();
      if(requiresAccessor()) {
        if(f.isStatic()) {
          ArrayList list = new ArrayList();
          list.add(rvalue);
          return asLocal(b, b.newStaticInvokeExpr(f.createAccessorWrite(fieldQualifierType().erasure()).sootRef(), list, this));
        }
        else {
          soot.Local base = base(b);
          ArrayList list = new ArrayList();
          list.add(base);
          list.add(asLocal(b, rvalue, lvalue.getType()));
          return asLocal(b, b.newStaticInvokeExpr(f.createAccessorWrite(fieldQualifierType().erasure()).sootRef(), list, this));
        }
      }
    }
    return refined_Expressions_VarAccess_emitStore(b, lvalue, rvalue, location);
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:108
   */
    public soot.Local createLoadQualifier(Body b) {
    Variable v = decl();
    if(v instanceof FieldDeclaration) {
      FieldDeclaration f = ((FieldDeclaration)v).erasedField();
      if(hasPrevExpr()) {
        // load explicit qualifier
        Local qualifier = asLocal(b, prevExpr().eval(b));
        // pop qualifier stack element for class variables
        // this qualifier must be computed to ensure side effects
        return qualifier;
      }
      else if(f.isInstanceVariable()) {
        return emitThis(b, fieldQualifierType().erasure());
      }
    }
    throw new Error("createLoadQualifier not supported for " + v.getClass().getName());
  }
  /**
   * @ast method 
   * @aspect StaticImportsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/StaticImportsCodegen.jrag:11
   */
    protected TypeDecl fieldQualifierType() {
    TypeDecl typeDecl = refined_GenericsCodegen_VarAccess_fieldQualifierType();
    if(typeDecl != null)
      return typeDecl;
    return decl().hostType();
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:91
   */
  public Constant constant() {
    ASTNode$State state = state();
    try {  return type().cast(decl().getInit().constant());  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected int isConstant_visited = -1;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_initialized = false;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_value;
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:354
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isConstant() {
    if(isConstant_computed) {
      return isConstant_value;
    }
    ASTNode$State state = state();
    if (!isConstant_initialized) {
      isConstant_initialized = true;
      isConstant_value = false;
    }
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
    int num = state.boundariesCrossed;
    boolean isFinal = this.is$Final();
      do {
        isConstant_visited = state.CIRCLE_INDEX;
        state.CHANGE = false;
        boolean new_isConstant_value = isConstant_compute();
        if (new_isConstant_value!=isConstant_value)
          state.CHANGE = true;
        isConstant_value = new_isConstant_value; 
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
        if(isFinal && num == state().boundariesCrossed) {
      isConstant_computed = true;
      }
      else {
      state.RESET_CYCLE = true;
      isConstant_compute();
      state.RESET_CYCLE = false;
        isConstant_computed = false;
        isConstant_initialized = false;
      }
      state.IN_CIRCLE = false; 
      return isConstant_value;
    }
    if(isConstant_visited != state.CIRCLE_INDEX) {
      isConstant_visited = state.CIRCLE_INDEX;
      if (state.RESET_CYCLE) {
        isConstant_computed = false;
        isConstant_initialized = false;
        isConstant_visited = -1;
        return isConstant_value;
      }
      boolean new_isConstant_value = isConstant_compute();
      if (new_isConstant_value!=isConstant_value)
        state.CHANGE = true;
      isConstant_value = new_isConstant_value; 
      return isConstant_value;
    }
    return isConstant_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isConstant_compute() {
    Variable v = decl();
    if(v instanceof FieldDeclaration) {
      FieldDeclaration f = (FieldDeclaration)v;
      return f.isConstant() && (!isQualified() || (isQualified() && qualifier().isTypeAccess()));
    }
    boolean result = v.isFinal() && v.hasInit() && v.getInit().isConstant() && (v.type().isPrimitive() || v.type().isString());
    return result && (!isQualified() || (isQualified() && qualifier().isTypeAccess()));
  }
  /**
   * @attribute syn
   * @aspect DefiniteAssignment
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:58
   */
  public Variable varDecl() {
    ASTNode$State state = state();
    try {  return decl();  }
    finally {
    }
  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:351
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafter(Variable v) {
    Object _parameters = v;
    if(isDAafter_Variable_values == null) isDAafter_Variable_values = new java.util.HashMap(4);
    if(isDAafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAafter_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAafter_Variable_value = isDAafter_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDAafter_Variable_values.put(_parameters, Boolean.valueOf(isDAafter_Variable_value));
    return isDAafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafter_compute(Variable v) {  return isDAbefore(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:832
   */
  public boolean isDUafter(Variable v) {
    ASTNode$State state = state();
    try {  return isDUbefore(v);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1196
   */
  public boolean unassignedEverywhere(Variable v, TryStmt stmt) {
    ASTNode$State state = state();
    try {
    if(isDest() && decl() == v && enclosingStmt().reachable()) {
      return false;
    }
    return super.unassignedEverywhere(v, stmt);
  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean decls_computed = false;
  /**
   * @apilevel internal
   */
  protected SimpleSet decls_value;
  /**
   * @attribute syn
   * @aspect VariableScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:241
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet decls() {
    if(decls_computed) {
      return decls_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    decls_value = decls_compute();
      if(isFinal && num == state().boundariesCrossed) decls_computed = true;
    return decls_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet decls_compute() {
    SimpleSet set = lookupVariable(name());
    if(set.size() == 1) {
      Variable v = (Variable)set.iterator().next();
      if(!isQualified() && inStaticContext()) {
        if(v.isInstanceVariable() && !hostType().memberFields(v.name()).isEmpty())
          return SimpleSet.emptySet;
      }
      else if(isQualified() && qualifier().staticContextQualifier()) {
        if(v.isInstanceVariable())
          return SimpleSet.emptySet;
      }
    }
    return set;
  }
  /**
   * @apilevel internal
   */
  protected boolean decl_computed = false;
  /**
   * @apilevel internal
   */
  protected Variable decl_value;
  /**
   * @attribute syn
   * @aspect VariableScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:256
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Variable decl() {
    if(decl_computed) {
      return decl_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    decl_value = decl_compute();
      if(isFinal && num == state().boundariesCrossed) decl_computed = true;
    return decl_value;
  }
  /**
   * @apilevel internal
   */
  private Variable decl_compute() {
    SimpleSet decls = decls();
    if(decls.size() == 1)
      return (Variable)decls.iterator().next();
    return unknownField();
  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:226
   */
  public boolean inSameInitializer() {
    ASTNode$State state = state();
    try {
    BodyDecl b = closestBodyDecl(decl().hostType());
    if(b == null) return false;
    if(b instanceof FieldDeclaration && ((FieldDeclaration)b).isStatic() == decl().isStatic())
      return true;
    if(b instanceof InstanceInitializer && !decl().isStatic())
      return true;
    if(b instanceof StaticInitializer && decl().isStatic())
      return true;
    return false;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:238
   */
  public boolean simpleAssignment() {
    ASTNode$State state = state();
    try {  return isDest() && getParent() instanceof AssignSimpleExpr;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:240
   */
  public boolean inDeclaringClass() {
    ASTNode$State state = state();
    try {  return hostType() == decl().hostType();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:800
   */
  public String dumpString() {
    ASTNode$State state = state();
    try {  return getClass().getName() + " [" + getID() + "]";  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Names
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:17
   */
  public String name() {
    ASTNode$State state = state();
    try {  return getID();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean isFieldAccess_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isFieldAccess_value;
  /**
   * @attribute syn
   * @aspect AccessTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:23
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isFieldAccess() {
    if(isFieldAccess_computed) {
      return isFieldAccess_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isFieldAccess_value = isFieldAccess_compute();
      if(isFinal && num == state().boundariesCrossed) isFieldAccess_computed = true;
    return isFieldAccess_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isFieldAccess_compute() {  return decl().isClassVariable() || decl().isInstanceVariable();  }
  /**
   * @attribute syn
   * @aspect SyntacticClassification
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:56
   */
  public NameType predNameType() {
    ASTNode$State state = state();
    try {  return NameType.AMBIGUOUS_NAME;  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean type_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl type_value;
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:283
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
    if(type_computed) {
      return type_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    type_value = type_compute();
      if(isFinal && num == state().boundariesCrossed) type_computed = true;
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {  return decl().type();  }
  /**
   * @attribute syn
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:15
   */
  public boolean isVariable() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:500
   */
  public boolean isEnumConstant() {
    ASTNode$State state = state();
    try {  return varDecl() instanceof EnumConstant;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:373
   */
  public boolean requiresAccessor() {
    ASTNode$State state = state();
    try {
    Variable v = decl();
    if(!(v instanceof FieldDeclaration))
      return false;
    FieldDeclaration f = (FieldDeclaration)v;
    if(f.isPrivate() && !hostType().hasField(v.name()))
      return true;
    if(f.isProtected() && !f.hostPackage().equals(hostPackage()) && !hostType().hasField(v.name()))
      return true;
    return false;
  }
    finally {
    }
  }
  protected java.util.Map base_Body_values;
  /**
   * @attribute syn
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:254
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.Local base(Body b) {
    Object _parameters = b;
    if(base_Body_values == null) base_Body_values = new java.util.HashMap(4);
    if(base_Body_values.containsKey(_parameters)) {
      return (soot.Local)base_Body_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    soot.Local base_Body_value = base_compute(b);
      if(isFinal && num == state().boundariesCrossed) base_Body_values.put(_parameters, base_Body_value);
    return base_Body_value;
  }
  /**
   * @apilevel internal
   */
  private soot.Local base_compute(Body b) {  return asLocal(b, createLoadQualifier(b));  }
  /**
   * @attribute syn
   * @aspect PreciseRethrow
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:11
   */
  public Collection<TypeDecl> throwTypes() {
    ASTNode$State state = state();
    try {  return decl().throwTypes();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PreciseRethrow
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:115
   */
  public boolean isVariable(Variable var) {
    ASTNode$State state = state();
    try {  return decl() == var;  }
    finally {
    }
  }
  /**
   * @attribute inh
   * @aspect TypeHierarchyCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:122
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean inExplicitConstructorInvocation() {
    ASTNode$State state = state();
    boolean inExplicitConstructorInvocation_value = getParent().Define_boolean_inExplicitConstructorInvocation(this, null);
    return inExplicitConstructorInvocation_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
