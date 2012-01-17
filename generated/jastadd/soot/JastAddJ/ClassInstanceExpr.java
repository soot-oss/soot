package soot.JastAddJ;

import java.util.HashSet;
import java.util.LinkedHashSet;
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
 * @ast node
 * @declaredat java.ast:34
 */
public class ClassInstanceExpr extends Access implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isDAafterInstance_Variable_values = null;
    computeDAbefore_int_Variable_values = null;
    computeDUbefore_int_Variable_values = null;
    decls_computed = false;
    decls_value = null;
    decl_computed = false;
    decl_value = null;
    localLookupType_String_values = null;
    type_computed = false;
    type_value = null;
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
  public ClassInstanceExpr clone() throws CloneNotSupportedException {
    ClassInstanceExpr node = (ClassInstanceExpr)super.clone();
    node.isDAafterInstance_Variable_values = null;
    node.computeDAbefore_int_Variable_values = null;
    node.computeDUbefore_int_Variable_values = null;
    node.decls_computed = false;
    node.decls_value = null;
    node.decl_computed = false;
    node.decl_value = null;
    node.localLookupType_String_values = null;
    node.type_computed = false;
    node.type_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ClassInstanceExpr copy() {
      try {
        ClassInstanceExpr node = (ClassInstanceExpr)clone();
        if(children != null) node.children = (ASTNode[])children.clone();
        return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
  }
  /**
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ClassInstanceExpr fullCopy() {
    ClassInstanceExpr res = (ClassInstanceExpr)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect AccessControl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:139
   */
  public void accessControl() {
    super.accessControl();
    if(type().isAbstract())
      error("Can not instantiate abstract class " + type().fullName());
    if(!decl().accessibleFrom(hostType()))
      error("constructor " + decl().signature() + " is not accessible");
  }
  /**
   * @ast method 
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:105
   */
  public void exceptionHandling() {
    for (Access exception : decl().getExceptionList()) {
      TypeDecl exceptionType = exception.type();
      if (!handlesException(exceptionType))
        error("" + this + " may throw uncaught exception " +
            exceptionType.fullName() + "; it must be caught or declared as being thrown");
    }
  }
  /**
   * @ast method 
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:267
   */
  protected boolean reachedException(TypeDecl catchType) {
    ConstructorDecl decl = decl();
    for(int i = 0; i < decl.getNumException(); i++) {
      TypeDecl exceptionType = decl.getException(i).type();
      if(catchType.mayCatch(exceptionType))
        return true;
    }
    return super.reachedException(catchType);
  }
  /**
   * @ast method 
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:326
   */
  public SimpleSet keepInnerClasses(SimpleSet c) {
    SimpleSet newSet = SimpleSet.emptySet;
    for(Iterator iter = c.iterator(); iter.hasNext(); ) {
      TypeDecl t = (TypeDecl)iter.next();
      if(t.isInnerType() && t.isClassDecl()) {
        newSet = newSet.add(c);
      }
    }
    return newSet;
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:137
   */
  public void refined_NameCheck_ClassInstanceExpr_nameCheck() {
    super.nameCheck();
    if(decls().isEmpty())
      error("can not instantiate " + type().typeName() + " no matching constructor found in " + type().typeName());
    else if(decls().size() > 1 && validArgs()) {
      error("several most specific constructors found");
      for(Iterator iter = decls().iterator(); iter.hasNext(); ) {
        error("         " + ((ConstructorDecl)iter.next()).signature());
      }
    }
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:82
   */
  public ClassInstanceExpr(Access type, List args) {
    this(type, args, new Opt());
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:327
   */
  public void toString(StringBuffer s) {
    s.append("new ");
    getAccess().toString(s);
    s.append("(");
    if(getNumArg() > 0) {
      getArg(0).toString(s);
      for(int i = 1; i < getNumArg(); i++) {
        s.append(", ");
        getArg(i).toString(s);
      }
    }
    s.append(")");

    if(hasTypeDecl()) {
      TypeDecl decl = getTypeDecl();
      s.append(" {");
      for(int i = 0; i < decl.getNumBodyDecl(); i++) {
        if(!(decl.getBodyDecl(i) instanceof ConstructorDecl))
          decl.getBodyDecl(i).toString(s);
      }
      s.append(typeDeclIndent());
      s.append("}");
    }
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:434
   */
  public void typeCheck() {
    if(isQualified() && qualifier().isTypeAccess() && !qualifier().type().isUnknown())
      error("*** The expression in a qualified class instance expr must not be a type name");
    // 15.9
    if(isQualified() && !type().isInnerClass() && !((ClassDecl)type()).superclass().isInnerClass() && !type().isUnknown()) {
      error("*** Qualified class instance creation can only instantiate inner classes and their anonymous subclasses");
    }
    if(!type().isClassDecl()) {
      error("*** Can only instantiate classes, which " + type().typeName() + " is not"); 
    }
    typeCheckEnclosingInstance();
    typeCheckAnonymousSuperclassEnclosingInstance();
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:448
   */
  public void typeCheckEnclosingInstance() {
    TypeDecl C = type();
    if(!C.isInnerClass())
      return;

    TypeDecl enclosing = null;
    if(C.isAnonymous()) {
      if(noEnclosingInstance()) {
        enclosing = null;
      }
      else {
        enclosing = hostType();
      }
    }
    else if(C.isLocalClass()) {
      if(C.inStaticContext()) {
        enclosing = null;
      }
      else if(noEnclosingInstance()) {
        enclosing = unknownType();
      }
      else {
        TypeDecl nest = hostType();
        while(nest != null && !nest.instanceOf(C.enclosingType()))
          nest = nest.enclosingType();
        enclosing = nest;
      }
    }
    else if(C.isMemberType()) {
      if(!isQualified()) {
        if(noEnclosingInstance()) {
          error("No enclosing instance to initialize " + C.typeName() + " with");
          //System.err.println("ClassInstanceExpr: Non qualified MemberType " + C.typeName() + " is in a static context when instantiated in " + this);
          enclosing = unknownType();
        }
        else {
          TypeDecl nest = hostType();
          while(nest != null && !nest.instanceOf(C.enclosingType()))
            nest = nest.enclosingType();
          enclosing = nest == null ? unknownType() : nest;
        }
      }
      else {
        enclosing = enclosingInstance();
      }
    }
    if(enclosing != null && !enclosing.instanceOf(type().enclosingType())) {
      String msg = enclosing == null ? "None" : enclosing.typeName();
      error("*** Can not instantiate " + type().typeName() + " with the enclosing instance " + msg + " due to incorrect enclosing instance");
    }
    else if(!isQualified() && C.isMemberType() && inExplicitConstructorInvocation() && enclosing == hostType()) {
      error("*** The innermost enclosing instance of type " + enclosing.typeName() + " is this which is not yet initialized here.");
    }
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:521
   */
  public void typeCheckAnonymousSuperclassEnclosingInstance() {
    if(type().isAnonymous() && ((ClassDecl)type()).superclass().isInnerType()) {
      TypeDecl S = ((ClassDecl)type()).superclass();
      if(S.isLocalClass()) {
        if(S.inStaticContext()) {
        }
        else if(noEnclosingInstance()) {
          error("*** No enclosing instance to class " + type().typeName() + " due to static context");
        }
        else if(inExplicitConstructorInvocation())
          error("*** No enclosing instance to superclass " + S.typeName() + " of " + type().typeName() + " since this is not initialized yet");
      }
      else if(S.isMemberType()) {
        if(!isQualified()) {
          // 15.9.2 2nd paragraph
          if(noEnclosingInstance()) {
            error("*** No enclosing instance to class " + type().typeName() + " due to static context");
          }
          else {
            TypeDecl nest = hostType();
            while(nest != null && !nest.instanceOf(S.enclosingType()))
              nest = nest.enclosingType();
            if(nest == null) {
              error("*** No enclosing instance to superclass " + S.typeName() + " of " + type().typeName());
            }
            else if(inExplicitConstructorInvocation()) {
              error("*** No enclosing instance to superclass " + S.typeName() + " of " + type().typeName() + " since this is not initialized yet");
            }
          }
        }
      }
    }
  }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:363
   */
  public void checkModifiers() {
    if(decl().isDeprecated() &&
      !withinDeprecatedAnnotation() &&
      hostType().topLevelType() != decl().hostType().topLevelType() &&
      !withinSuppressWarnings("deprecation"))
        warning(decl().signature() + " in " + decl().hostType().typeName() + " has been deprecated");
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:469
   */
  

  // add val$name as arguments to the constructor
  protected boolean addEnclosingVariables = true;
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:470
   */
  public void addEnclosingVariables() {
    if(!addEnclosingVariables) return;
    addEnclosingVariables = false;
    decl().addEnclosingVariables();
    for(Iterator iter = decl().hostType().enclosingVariables().iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      getArgList().add(new VarAccess(v.name()));
    }
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:127
   */
  public void refined_Transformations_ClassInstanceExpr_transformation() {
    // this$val
    addEnclosingVariables();
    // touch accessorIndex go force creation of private constructorAccessor
    if(decl().isPrivate() && type() != hostType()) {
      decl().createAccessor();
    }
    super.transformation();
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:550
   */
  private soot.Value emitLocalEnclosing(Body b, TypeDecl localClass) {
    if(!localClass.inStaticContext()) {
      return emitThis(b, localClass.enclosingType());
    }
    throw new Error("Not implemented");
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:557
   */
  private soot.Value emitInnerMemberEnclosing(Body b, TypeDecl innerClass) {
    if(hasPrevExpr()) {
      Local base = asLocal(b, prevExpr().eval(b));
      b.setLine(this);
      b.add(b.newInvokeStmt(
        b.newVirtualInvokeExpr(
          base, 
          Scene.v().getMethod("<java.lang.Object: java.lang.Class getClass()>").makeRef(),
          this
        ),
        this
      ));
      return base;
    }
    else {
      TypeDecl enclosing = hostType();
      while(!enclosing.hasType(innerClass.name()))
        enclosing = enclosing.enclosingType();
      return emitThis(b, enclosing);
    }
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:579
   */
  public soot.Value eval(Body b) {
    Local local = asLocal(b, b.newNewExpr(type().sootRef(), this));
    ArrayList list = new ArrayList();

     // 15.9.2 first part
    if(type().isAnonymous()) {
      if(type().isAnonymousInNonStaticContext()) {
        list.add(asImmediate(b, b.emitThis(hostType())));
      }
      // 15.9.2 second part
      ClassDecl C = (ClassDecl)type();
      TypeDecl S = C.superclass();
      if(S.isLocalClass()) {
        if(!type().inStaticContext())
          list.add(asImmediate(b, emitLocalEnclosing(b, S)));
      }
      else if(S.isInnerType()) {
        list.add(asImmediate(b, emitInnerMemberEnclosing(b, S)));
      }
    }
    else if(type().isLocalClass()) {
      if(!type().inStaticContext())
        list.add(asImmediate(b, emitLocalEnclosing(b, type())));
    }
    else if(type().isInnerType()) {
      list.add(asImmediate(b, emitInnerMemberEnclosing(b, type())));
    }

    for(int i = 0; i < getNumArg(); i++)
      list.add(asImmediate(b, getArg(i).type().emitCastTo(b, getArg(i), decl().getParameter(i).type()))); // MethodInvocationConversion
  
    if(decl().isPrivate() && type() != hostType()) {
      list.add(asImmediate(b, soot.jimple.NullConstant.v()));
      b.setLine(this);
      b.add(
        b.newInvokeStmt(
          b.newSpecialInvokeExpr(local, decl().createAccessor().sootRef(), list, this),
          this
        )
      );
      return local;
    }
    else {
      b.setLine(this);
      b.add(
        b.newInvokeStmt(
          b.newSpecialInvokeExpr(local, decl().sootRef(), list, this),
          this
        )
      );
      return local;
    }
  }
  /**
   * @ast method 
   * @aspect EmitJimpleRefinements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:231
   */
  public void collectTypesToSignatures(Collection<Type> set) {
	 super.collectTypesToSignatures(set);
   addDependencyIfNeeded(set, decl().erasedConstructor().hostType());
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public ClassInstanceExpr() {
    super();

    setChild(new List(), 1);
    setChild(new Opt(), 2);

  }
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  public ClassInstanceExpr(Access p0, List<Expr> p1, Opt<TypeDecl> p2) {
    setChild(p0, 0);
    setChild(p1, 1);
    setChild(p2, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:17
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:23
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Access
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setAccess(Access node) {
    setChild(node, 0);
  }
  /**
   * Getter for Access
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Access getAccess() {
    return (Access)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Access getAccessNoTransform() {
    return (Access)getChildNoTransform(0);
  }
  /**
   * Setter for ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setArgList(List<Expr> list) {
    setChild(list, 1);
  }
  /**
   * @return number of children in ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumArg() {
    return getArgList().getNumChild();
  }
  /**
   * Getter for child in list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getArg(int i) {
    return (Expr)getArgList().getChild(i);
  }
  /**
   * Add element to list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addArg(Expr node) {
    List<Expr> list = (parent == null || state == null) ? getArgListNoTransform() : getArgList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addArgNoTransform(Expr node) {
    List<Expr> list = getArgListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setArg(Expr node, int i) {
    List<Expr> list = getArgList();
    list.setChild(node, i);
  }
  /**
   * Getter for Arg list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<Expr> getArgs() {
    return getArgList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<Expr> getArgsNoTransform() {
    return getArgListNoTransform();
  }
  /**
   * Getter for list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Expr> getArgList() {
    List<Expr> list = (List<Expr>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Expr> getArgListNoTransform() {
    return (List<Expr>)getChildNoTransform(1);
  }
  /**
   * Setter for TypeDeclOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setTypeDeclOpt(Opt<TypeDecl> opt) {
    setChild(opt, 2);
  }
  /**
   * Does this node have a TypeDecl child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasTypeDecl() {
    return getTypeDeclOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child TypeDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl getTypeDecl() {
    return (TypeDecl)getTypeDeclOpt().getChild(0);
  }
  /**
   * Setter for optional child TypeDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setTypeDecl(TypeDecl node) {
    getTypeDeclOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<TypeDecl> getTypeDeclOpt() {
    return (Opt<TypeDecl>)getChild(2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<TypeDecl> getTypeDeclOptNoTransform() {
    return (Opt<TypeDecl>)getChildNoTransform(2);
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:19
   */
    public void nameCheck() {
    if(getAccess().type().isEnumDecl() && !enclosingBodyDecl().isEnumConstant())
      error("enum types may not be instantiated explicitly");
    else
      refined_NameCheck_ClassInstanceExpr_nameCheck();
  }
  /**
   * @ast method 
   * @aspect VariableArityParametersCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/VariableArityParametersCodegen.jrag:36
   */
    public void transformation() {
    if(decl().isVariableArity() && !invokesVariableArityAsArray()) {
      // arguments to normal parameters
      List list = new List();
      for(int i = 0; i < decl().getNumParameter() - 1; i++)
        list.add(getArg(i).fullCopy());
      // arguments to variable arity parameters
      List last = new List();
      for(int i = decl().getNumParameter() - 1; i < getNumArg(); i++)
        last.add(getArg(i).fullCopy());
      // build an array holding arguments
      Access typeAccess = decl().lastParameter().type().elementType().createQualifiedAccess();
      for(int i = 0; i < decl().lastParameter().type().dimension(); i++)
        typeAccess = new ArrayTypeAccess(typeAccess);
      list.add(new ArrayCreationExpr(typeAccess, new Opt(new ArrayInit(last))));
      // replace argument list with augemented argument list
      setArgList(list);
    }
    refined_Transformations_ClassInstanceExpr_transformation();
  }
  protected java.util.Map isDAafterInstance_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:420
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafterInstance(Variable v) {
    Object _parameters = v;
    if(isDAafterInstance_Variable_values == null) isDAafterInstance_Variable_values = new java.util.HashMap(4);
    if(isDAafterInstance_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAafterInstance_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAafterInstance_Variable_value = isDAafterInstance_compute(v);
if(isFinal && num == state().boundariesCrossed) isDAafterInstance_Variable_values.put(_parameters, Boolean.valueOf(isDAafterInstance_Variable_value));
    return isDAafterInstance_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafterInstance_compute(Variable v) {
    if(getNumArg() == 0)
      return isDAbefore(v);
    return getArg(getNumArg()-1).isDAafter(v);
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:425
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafter(Variable v) {
      ASTNode$State state = state();
    boolean isDAafter_Variable_value = isDAafter_compute(v);
    return isDAafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafter_compute(Variable v) {  return isDAafterInstance(v);  }
  protected java.util.Map computeDAbefore_int_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:427
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean computeDAbefore(int i, Variable v) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(Integer.valueOf(i));
    _parameters.add(v);
    if(computeDAbefore_int_Variable_values == null) computeDAbefore_int_Variable_values = new java.util.HashMap(4);
    if(computeDAbefore_int_Variable_values.containsKey(_parameters)) {
      return ((Boolean)computeDAbefore_int_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean computeDAbefore_int_Variable_value = computeDAbefore_compute(i, v);
if(isFinal && num == state().boundariesCrossed) computeDAbefore_int_Variable_values.put(_parameters, Boolean.valueOf(computeDAbefore_int_Variable_value));
    return computeDAbefore_int_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean computeDAbefore_compute(int i, Variable v) {  return i == 0 ? isDAbefore(v) : getArg(i-1).isDAafter(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:849
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafterInstance(Variable v) {
      ASTNode$State state = state();
    boolean isDUafterInstance_Variable_value = isDUafterInstance_compute(v);
    return isDUafterInstance_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafterInstance_compute(Variable v) {
    if(getNumArg() == 0)
      return isDUbefore(v);
    return getArg(getNumArg()-1).isDUafter(v);
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:854
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafter(Variable v) {
      ASTNode$State state = state();
    boolean isDUafter_Variable_value = isDUafter_compute(v);
    return isDUafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafter_compute(Variable v) {  return isDUafterInstance(v);  }
  protected java.util.Map computeDUbefore_int_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:856
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean computeDUbefore(int i, Variable v) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(Integer.valueOf(i));
    _parameters.add(v);
    if(computeDUbefore_int_Variable_values == null) computeDUbefore_int_Variable_values = new java.util.HashMap(4);
    if(computeDUbefore_int_Variable_values.containsKey(_parameters)) {
      return ((Boolean)computeDUbefore_int_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean computeDUbefore_int_Variable_value = computeDUbefore_compute(i, v);
if(isFinal && num == state().boundariesCrossed) computeDUbefore_int_Variable_values.put(_parameters, Boolean.valueOf(computeDUbefore_int_Variable_value));
    return computeDUbefore_int_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean computeDUbefore_compute(int i, Variable v) {  return i == 0 ? isDUbefore(v) : getArg(i-1).isDUafter(v);  }
  /**
   * @attribute syn
   * @aspect ConstructScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:53
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean applicableAndAccessible(ConstructorDecl decl) {
      ASTNode$State state = state();
    boolean applicableAndAccessible_ConstructorDecl_value = applicableAndAccessible_compute(decl);
    return applicableAndAccessible_ConstructorDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean applicableAndAccessible_compute(ConstructorDecl decl) {  return decl.applicable(getArgList()) && decl.accessibleFrom(hostType()) && 
    (!decl.isProtected() || hasTypeDecl() || decl.hostPackage().equals(hostPackage()));  }
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
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:70
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
    TypeDecl typeDecl = hasTypeDecl() ? getTypeDecl() : getAccess().type();
    return chooseConstructor(typeDecl.constructors(), getArgList());
  }
  /**
   * @apilevel internal
   */
  protected boolean decl_computed = false;
  /**
   * @apilevel internal
   */
  protected ConstructorDecl decl_value;
  /**
   * @attribute syn
   * @aspect ConstructScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:78
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConstructorDecl decl() {
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
  private ConstructorDecl decl_compute() {
    SimpleSet decls = decls();
    if(decls.size() == 1)
      return (ConstructorDecl)decls.iterator().next();
    return unknownConstructor();
  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:345
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet qualifiedLookupType(String name) {
      ASTNode$State state = state();
    SimpleSet qualifiedLookupType_String_value = qualifiedLookupType_compute(name);
    return qualifiedLookupType_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet qualifiedLookupType_compute(String name) {
    SimpleSet c = keepAccessibleTypes(type().memberTypes(name));
    if(!c.isEmpty())
      return c;
    if(type().name().equals(name))
      return SimpleSet.emptySet.add(type());
    return SimpleSet.emptySet;
  }
  protected java.util.Map localLookupType_String_values;
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:384
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localLookupType(String name) {
    Object _parameters = name;
    if(localLookupType_String_values == null) localLookupType_String_values = new java.util.HashMap(4);
    if(localLookupType_String_values.containsKey(_parameters)) {
      return (SimpleSet)localLookupType_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet localLookupType_String_value = localLookupType_compute(name);
if(isFinal && num == state().boundariesCrossed) localLookupType_String_values.put(_parameters, localLookupType_String_value);
    return localLookupType_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localLookupType_compute(String name) {
    if(hasTypeDecl() && getTypeDecl().name().equals(name))
      return SimpleSet.emptySet.add(getTypeDecl());
    return SimpleSet.emptySet;
  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:130
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean validArgs() {
      ASTNode$State state = state();
    boolean validArgs_value = validArgs_compute();
    return validArgs_value;
  }
  /**
   * @apilevel internal
   */
  private boolean validArgs_compute() {
    for(int i = 0; i < getNumArg(); i++)
      if(getArg(i).type().isUnknown())
        return false;
    return true;
  }
  /**
   * @attribute syn
   * @aspect SyntacticClassification
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:97
   */
  @SuppressWarnings({"unchecked", "cast"})
  public NameType predNameType() {
      ASTNode$State state = state();
    NameType predNameType_value = predNameType_compute();
    return predNameType_value;
  }
  /**
   * @apilevel internal
   */
  private NameType predNameType_compute() {  return NameType.EXPRESSION_NAME;  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:311
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
  private TypeDecl type_compute() {  return hasTypeDecl() ? getTypeDecl() : getAccess().type();  }
  /**
   * @attribute syn
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:519
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean noEnclosingInstance() {
      ASTNode$State state = state();
    boolean noEnclosingInstance_value = noEnclosingInstance_compute();
    return noEnclosingInstance_value;
  }
  /**
   * @apilevel internal
   */
  private boolean noEnclosingInstance_compute() {  return isQualified() ? qualifier().staticContextQualifier() : inStaticContext();  }
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:327
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int arity() {
      ASTNode$State state = state();
    int arity_value = arity_compute();
    return arity_value;
  }
  /**
   * @apilevel internal
   */
  private int arity_compute() {  return getNumArg();  }
  /**
   * @attribute syn
   * @aspect VariableArityParameters
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:54
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean invokesVariableArityAsArray() {
      ASTNode$State state = state();
    boolean invokesVariableArityAsArray_value = invokesVariableArityAsArray_compute();
    return invokesVariableArityAsArray_value;
  }
  /**
   * @apilevel internal
   */
  private boolean invokesVariableArityAsArray_compute() {
    if(!decl().isVariableArity())
      return false;
    if(arity() != decl().arity())
      return false;
    return getArg(getNumArg()-1).type().methodInvocationConversionTo(decl().lastParameter().type());
  }
  /**
   * @attribute inh
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:38
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean handlesException(TypeDecl exceptionType) {
      ASTNode$State state = state();
    boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
    return handlesException_TypeDecl_value;
  }
  /**
   * @attribute inh
   * @aspect ConstructScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:27
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeObject() {
      ASTNode$State state = state();
    TypeDecl typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
    return typeObject_value;
  }
  /**
   * @attribute inh
   * @aspect ConstructScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:84
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConstructorDecl unknownConstructor() {
      ASTNode$State state = state();
    ConstructorDecl unknownConstructor_value = getParent().Define_ConstructorDecl_unknownConstructor(this, null);
    return unknownConstructor_value;
  }
  /**
   * @attribute inh
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:351
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String typeDeclIndent() {
      ASTNode$State state = state();
    String typeDeclIndent_value = getParent().Define_String_typeDeclIndent(this, null);
    return typeDeclIndent_value;
  }
  /**
   * @attribute inh
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:504
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl enclosingInstance() {
      ASTNode$State state = state();
    TypeDecl enclosingInstance_value = getParent().Define_TypeDecl_enclosingInstance(this, null);
    return enclosingInstance_value;
  }
  /**
   * @attribute inh
   * @aspect TypeHierarchyCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:126
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean inExplicitConstructorInvocation() {
      ASTNode$State state = state();
    boolean inExplicitConstructorInvocation_value = getParent().Define_boolean_inExplicitConstructorInvocation(this, null);
    return inExplicitConstructorInvocation_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:15
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_superType(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclOptNoTransform()) {
      return getAccess().type();
    }
    return getParent().Define_TypeDecl_superType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:83
   * @apilevel internal
   */
  public ConstructorDecl Define_ConstructorDecl_constructorDecl(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclOptNoTransform()){
    Collection c = getAccess().type().constructors();
    SimpleSet maxSpecific = chooseConstructor(c, getArgList());
    if(maxSpecific.size() == 1)
      return (ConstructorDecl)maxSpecific.iterator().next();
    return unknownConstructor();
  }
    return getParent().Define_ConstructorDecl_constructorDecl(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:429
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getTypeDeclOptNoTransform()) {
      return isDAafterInstance(v);
    }
    if(caller == getArgListNoTransform()) {
      int i = caller.getIndexOfChild(child);
      return computeDAbefore(i, v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:855
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getArgListNoTransform()) {
      int i = caller.getIndexOfChild(child);
      return computeDUbefore(i, v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:92
   * @apilevel internal
   */
  public boolean Define_boolean_hasPackage(ASTNode caller, ASTNode child, String packageName) {
    if(caller == getArgListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return unqualifiedScope().hasPackage(packageName);
    }
    return getParent().Define_boolean_hasPackage(this, caller, packageName);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:316
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getTypeDeclOptNoTransform()){
    SimpleSet c = localLookupType(name);
    if(!c.isEmpty())
      return c;
    c = lookupType(name);
    if(!c.isEmpty())
      return c;
    return unqualifiedScope().lookupType(name);
  }
    if(caller == getAccessNoTransform()){
    SimpleSet c = lookupType(name);
    if(c.size() == 1) {
      if(isQualified())
        c = keepInnerClasses(c);
    }
    return c;
  }
    if(caller == getArgListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return unqualifiedScope().lookupType(name);
    }
    return getParent().Define_SimpleSet_lookupType(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:137
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getArgListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return unqualifiedScope().lookupVariable(name);
    }
    return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:127
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getArgListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return NameType.EXPRESSION_NAME;
    }
    if(caller == getTypeDeclOptNoTransform()) {
      return NameType.TYPE_NAME;
    }
    if(caller == getAccessNoTransform()) {
      return NameType.TYPE_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:217
   * @apilevel internal
   */
  public boolean Define_boolean_isAnonymous(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclOptNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isAnonymous(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:531
   * @apilevel internal
   */
  public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclOptNoTransform()) {
      return false;
    }
    return getParent().Define_boolean_isMemberType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:573
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclOptNoTransform()) {
      return hostType();
    }
    return getParent().Define_TypeDecl_hostType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:147
   * @apilevel internal
   */
  public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclOptNoTransform()) {
      return isQualified() ?
    qualifier().staticContextQualifier() : inStaticContext();
    }
    return getParent().Define_boolean_inStaticContext(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
