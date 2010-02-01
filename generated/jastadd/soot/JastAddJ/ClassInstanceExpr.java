
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class ClassInstanceExpr extends Access implements Cloneable {
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
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ClassInstanceExpr clone() throws CloneNotSupportedException {
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
     @SuppressWarnings({"unchecked", "cast"})  public ClassInstanceExpr copy() {
      try {
          ClassInstanceExpr node = (ClassInstanceExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ClassInstanceExpr fullCopy() {
        ClassInstanceExpr res = (ClassInstanceExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in AccessControl.jrag at line 139


  public void accessControl() {
    super.accessControl();
    if(type().isAbstract())
      error("Can not instantiate abstract class " + type().fullName());
    if(!decl().accessibleFrom(hostType()))
      error("constructor " + decl().signature() + " is not accessible");
  }

    // Declared in ExceptionHandling.jrag at line 253


  protected boolean reachedException(TypeDecl catchType) {
    ConstructorDecl decl = decl();
    for(int i = 0; i < decl.getNumException(); i++) {
      TypeDecl exceptionType = decl.getException(i).type();
      if(catchType.mayCatch(exceptionType))
        return true;
    }
    return super.reachedException(catchType);
  }

    // Declared in LookupType.jrag at line 326


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

    // Declared in NameCheck.jrag at line 137


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

    // Declared in NodeConstructors.jrag at line 82


  public ClassInstanceExpr(Access type, List args) {
    this(type, args, new Opt());
  }

    // Declared in PrettyPrint.jadd at line 326


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

    // Declared in TypeCheck.jrag at line 434


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

    // Declared in TypeCheck.jrag at line 448


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

    // Declared in TypeCheck.jrag at line 521


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

    // Declared in Annotations.jrag at line 363


  public void checkModifiers() {
    if(decl().isDeprecated() &&
      !withinDeprecatedAnnotation() &&
      hostType().topLevelType() != decl().hostType().topLevelType() &&
      !withinSuppressWarnings("deprecation"))
        warning(decl().signature() + " in " + decl().hostType().typeName() + " has been deprecated");
  }

    // Declared in InnerClasses.jrag at line 469


  // add val$name as arguments to the constructor
  protected boolean addEnclosingVariables = true;

    // Declared in InnerClasses.jrag at line 470

  public void addEnclosingVariables() {
    if(!addEnclosingVariables) return;
    addEnclosingVariables = false;
    decl().addEnclosingVariables();
    for(Iterator iter = decl().hostType().enclosingVariables().iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      getArgList().add(new VarAccess(v.name()));
    }
  }

    // Declared in Transformations.jrag at line 127



  // remote collection / demand driven creation of accessor
  public void refined_Transformations_ClassInstanceExpr_transformation() {
    // this$val
    addEnclosingVariables();
    // touch accessorIndex go force creation of private constructorAccessor
    if(decl().isPrivate() && type() != hostType()) {
      decl().createAccessor();
    }
    super.transformation();
  }

    // Declared in Expressions.jrag at line 550


  private soot.Value emitLocalEnclosing(Body b, TypeDecl localClass) {
    if(!localClass.inStaticContext()) {
      return emitThis(b, localClass.enclosingType());
    }
    throw new Error("Not implemented");
  }

    // Declared in Expressions.jrag at line 557


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

    // Declared in Expressions.jrag at line 579


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

    // Declared in EmitJimpleRefinements.jrag at line 231

  public void collectTypesToSignatures(Collection<Type> set) {
	 super.collectTypesToSignatures(set);
   addDependencyIfNeeded(set, decl().erasedConstructor().hostType());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 34

    public ClassInstanceExpr() {
        super();

        setChild(new List(), 1);
        setChild(new Opt(), 2);

    }

    // Declared in java.ast at line 12


    // Declared in java.ast line 34
    public ClassInstanceExpr(Access p0, List<Expr> p1, Opt<TypeDecl> p2) {
        setChild(p0, 0);
        setChild(p1, 1);
        setChild(p2, 2);
    }

    // Declared in java.ast at line 18


  protected int numChildren() {
    return 3;
  }

    // Declared in java.ast at line 21

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 34
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
    // Declared in java.ast line 34
    public void setArgList(List<Expr> list) {
        setChild(list, 1);
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
        List<Expr> list = (List<Expr>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Expr> getArgListNoTransform() {
        return (List<Expr>)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 34
    public void setTypeDeclOpt(Opt<TypeDecl> opt) {
        setChild(opt, 2);
    }

    // Declared in java.ast at line 6


    public boolean hasTypeDecl() {
        return getTypeDeclOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public TypeDecl getTypeDecl() {
        return (TypeDecl)getTypeDeclOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setTypeDecl(TypeDecl node) {
        getTypeDeclOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<TypeDecl> getTypeDeclOpt() {
        return (Opt<TypeDecl>)getChild(2);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<TypeDecl> getTypeDeclOptNoTransform() {
        return (Opt<TypeDecl>)getChildNoTransform(2);
    }

    // Declared in Enums.jrag at line 19

  
    public void nameCheck() {
    if(getAccess().type().isEnumDecl() && !enclosingBodyDecl().isEnumConstant())
      error("enum types may not be instantiated explicitly");
    else
      refined_NameCheck_ClassInstanceExpr_nameCheck();
  }

    // Declared in VariableArityParametersCodegen.jrag at line 36

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
    // Declared in DefiniteAssignment.jrag at line 422
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterInstance(Variable v) {
        Object _parameters = v;
if(isDAafterInstance_Variable_values == null) isDAafterInstance_Variable_values = new java.util.HashMap(4);
        if(isDAafterInstance_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDAafterInstance_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDAafterInstance_Variable_value = isDAafterInstance_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDAafterInstance_Variable_values.put(_parameters, Boolean.valueOf(isDAafterInstance_Variable_value));
        return isDAafterInstance_Variable_value;
    }

    private boolean isDAafterInstance_compute(Variable v) {
    if(getNumArg() == 0)
      return isDAbefore(v);
    return getArg(getNumArg()-1).isDAafter(v);
  }

    // Declared in DefiniteAssignment.jrag at line 427
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        ASTNode$State state = state();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {  return isDAafterInstance(v);  }

    protected java.util.Map computeDAbefore_int_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 429
 @SuppressWarnings({"unchecked", "cast"})     public boolean computeDAbefore(int i, Variable v) {
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
        if(isFinal && num == state().boundariesCrossed)
            computeDAbefore_int_Variable_values.put(_parameters, Boolean.valueOf(computeDAbefore_int_Variable_value));
        return computeDAbefore_int_Variable_value;
    }

    private boolean computeDAbefore_compute(int i, Variable v) {  return i == 0 ? isDAbefore(v) : getArg(i-1).isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 854
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterInstance(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterInstance_Variable_value = isDUafterInstance_compute(v);
        return isDUafterInstance_Variable_value;
    }

    private boolean isDUafterInstance_compute(Variable v) {
    if(getNumArg() == 0)
      return isDUbefore(v);
    return getArg(getNumArg()-1).isDUafter(v);
  }

    // Declared in DefiniteAssignment.jrag at line 859
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        ASTNode$State state = state();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {  return isDUafterInstance(v);  }

    protected java.util.Map computeDUbefore_int_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 861
 @SuppressWarnings({"unchecked", "cast"})     public boolean computeDUbefore(int i, Variable v) {
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
        if(isFinal && num == state().boundariesCrossed)
            computeDUbefore_int_Variable_values.put(_parameters, Boolean.valueOf(computeDUbefore_int_Variable_value));
        return computeDUbefore_int_Variable_value;
    }

    private boolean computeDUbefore_compute(int i, Variable v) {  return i == 0 ? isDUbefore(v) : getArg(i-1).isDUafter(v);  }

    // Declared in LookupConstructor.jrag at line 53
 @SuppressWarnings({"unchecked", "cast"})     public boolean applicableAndAccessible(ConstructorDecl decl) {
        ASTNode$State state = state();
        boolean applicableAndAccessible_ConstructorDecl_value = applicableAndAccessible_compute(decl);
        return applicableAndAccessible_ConstructorDecl_value;
    }

    private boolean applicableAndAccessible_compute(ConstructorDecl decl) {  return decl.applicable(getArgList()) && decl.accessibleFrom(hostType()) && 
    (!decl.isProtected() || hasTypeDecl() || decl.hostPackage().equals(hostPackage()));  }

    protected boolean decls_computed = false;
    protected SimpleSet decls_value;
    // Declared in MethodSignature.jrag at line 55
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
    TypeDecl typeDecl = hasTypeDecl() ? getTypeDecl() : getAccess().type();
    return chooseConstructor(typeDecl.constructors(), getArgList());
  }

    protected boolean decl_computed = false;
    protected ConstructorDecl decl_value;
    // Declared in LookupConstructor.jrag at line 78
 @SuppressWarnings({"unchecked", "cast"})     public ConstructorDecl decl() {
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

    private ConstructorDecl decl_compute() {
    SimpleSet decls = decls();
    if(decls.size() == 1)
      return (ConstructorDecl)decls.iterator().next();
    return unknownConstructor();
  }

    // Declared in LookupType.jrag at line 345
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet qualifiedLookupType(String name) {
        ASTNode$State state = state();
        SimpleSet qualifiedLookupType_String_value = qualifiedLookupType_compute(name);
        return qualifiedLookupType_String_value;
    }

    private SimpleSet qualifiedLookupType_compute(String name) {
    SimpleSet c = keepAccessibleTypes(type().memberTypes(name));
    if(!c.isEmpty())
      return c;
    if(type().name().equals(name))
      return SimpleSet.emptySet.add(type());
    return SimpleSet.emptySet;
  }

    protected java.util.Map localLookupType_String_values;
    // Declared in LookupType.jrag at line 384
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet localLookupType(String name) {
        Object _parameters = name;
if(localLookupType_String_values == null) localLookupType_String_values = new java.util.HashMap(4);
        if(localLookupType_String_values.containsKey(_parameters)) {
            return (SimpleSet)localLookupType_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet localLookupType_String_value = localLookupType_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            localLookupType_String_values.put(_parameters, localLookupType_String_value);
        return localLookupType_String_value;
    }

    private SimpleSet localLookupType_compute(String name) {
    if(hasTypeDecl() && getTypeDecl().name().equals(name))
      return SimpleSet.emptySet.add(getTypeDecl());
    return SimpleSet.emptySet;
  }

    // Declared in NameCheck.jrag at line 130
 @SuppressWarnings({"unchecked", "cast"})     public boolean validArgs() {
        ASTNode$State state = state();
        boolean validArgs_value = validArgs_compute();
        return validArgs_value;
    }

    private boolean validArgs_compute() {
    for(int i = 0; i < getNumArg(); i++)
      if(getArg(i).type().isUnknown())
        return false;
    return true;
  }

    // Declared in SyntacticClassification.jrag at line 97
 @SuppressWarnings({"unchecked", "cast"})     public NameType predNameType() {
        ASTNode$State state = state();
        NameType predNameType_value = predNameType_compute();
        return predNameType_value;
    }

    private NameType predNameType_compute() {  return NameType.EXPRESSION_NAME;  }

    // Declared in TypeAnalysis.jrag at line 311
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

    private TypeDecl type_compute() {  return hasTypeDecl() ? getTypeDecl() : getAccess().type();  }

    // Declared in TypeCheck.jrag at line 519
 @SuppressWarnings({"unchecked", "cast"})     public boolean noEnclosingInstance() {
        ASTNode$State state = state();
        boolean noEnclosingInstance_value = noEnclosingInstance_compute();
        return noEnclosingInstance_value;
    }

    private boolean noEnclosingInstance_compute() {  return isQualified() ? qualifier().staticContextQualifier() : inStaticContext();  }

    // Declared in MethodSignature.jrag at line 312
 @SuppressWarnings({"unchecked", "cast"})     public int arity() {
        ASTNode$State state = state();
        int arity_value = arity_compute();
        return arity_value;
    }

    private int arity_compute() {  return getNumArg();  }

    // Declared in VariableArityParameters.jrag at line 54
 @SuppressWarnings({"unchecked", "cast"})     public boolean invokesVariableArityAsArray() {
        ASTNode$State state = state();
        boolean invokesVariableArityAsArray_value = invokesVariableArityAsArray_compute();
        return invokesVariableArityAsArray_value;
    }

    private boolean invokesVariableArityAsArray_compute() {
    if(!decl().isVariableArity())
      return false;
    if(arity() != decl().arity())
      return false;
    return getArg(getNumArg()-1).type().methodInvocationConversionTo(decl().lastParameter().type());
  }

    // Declared in ExceptionHandling.jrag at line 38
 @SuppressWarnings({"unchecked", "cast"})     public boolean handlesException(TypeDecl exceptionType) {
        ASTNode$State state = state();
        boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
        return handlesException_TypeDecl_value;
    }

    // Declared in LookupConstructor.jrag at line 27
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeObject() {
        ASTNode$State state = state();
        TypeDecl typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
        return typeObject_value;
    }

    // Declared in LookupConstructor.jrag at line 84
 @SuppressWarnings({"unchecked", "cast"})     public ConstructorDecl unknownConstructor() {
        ASTNode$State state = state();
        ConstructorDecl unknownConstructor_value = getParent().Define_ConstructorDecl_unknownConstructor(this, null);
        return unknownConstructor_value;
    }

    // Declared in PrettyPrint.jadd at line 350
 @SuppressWarnings({"unchecked", "cast"})     public String typeDeclIndent() {
        ASTNode$State state = state();
        String typeDeclIndent_value = getParent().Define_String_typeDeclIndent(this, null);
        return typeDeclIndent_value;
    }

    // Declared in TypeCheck.jrag at line 504
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl enclosingInstance() {
        ASTNode$State state = state();
        TypeDecl enclosingInstance_value = getParent().Define_TypeDecl_enclosingInstance(this, null);
        return enclosingInstance_value;
    }

    // Declared in TypeHierarchyCheck.jrag at line 126
 @SuppressWarnings({"unchecked", "cast"})     public boolean inExplicitConstructorInvocation() {
        ASTNode$State state = state();
        boolean inExplicitConstructorInvocation_value = getParent().Define_boolean_inExplicitConstructorInvocation(this, null);
        return inExplicitConstructorInvocation_value;
    }

    // Declared in AnonymousClasses.jrag at line 15
    public TypeDecl Define_TypeDecl_superType(ASTNode caller, ASTNode child) {
        if(caller == getTypeDeclOptNoTransform()) {
            return getAccess().type();
        }
        return getParent().Define_TypeDecl_superType(this, caller);
    }

    // Declared in MethodSignature.jrag at line 68
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

    // Declared in DefiniteAssignment.jrag at line 431
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

    // Declared in DefiniteAssignment.jrag at line 860
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getArgListNoTransform()) {
      int i = caller.getIndexOfChild(child);
            return computeDUbefore(i, v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in LookupType.jrag at line 92
    public boolean Define_boolean_hasPackage(ASTNode caller, ASTNode child, String packageName) {
        if(caller == getArgListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return unqualifiedScope().hasPackage(packageName);
        }
        return getParent().Define_boolean_hasPackage(this, caller, packageName);
    }

    // Declared in LookupType.jrag at line 316
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

    // Declared in LookupVariable.jrag at line 135
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        if(caller == getArgListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return unqualifiedScope().lookupVariable(name);
        }
        return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }

    // Declared in SyntacticClassification.jrag at line 127
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

    // Declared in TypeAnalysis.jrag at line 217
    public boolean Define_boolean_isAnonymous(ASTNode caller, ASTNode child) {
        if(caller == getTypeDeclOptNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isAnonymous(this, caller);
    }

    // Declared in TypeAnalysis.jrag at line 531
    public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
        if(caller == getTypeDeclOptNoTransform()) {
            return false;
        }
        return getParent().Define_boolean_isMemberType(this, caller);
    }

    // Declared in TypeAnalysis.jrag at line 573
    public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
        if(caller == getTypeDeclOptNoTransform()) {
            return hostType();
        }
        return getParent().Define_TypeDecl_hostType(this, caller);
    }

    // Declared in TypeHierarchyCheck.jrag at line 147
    public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
        if(caller == getTypeDeclOptNoTransform()) {
            return isQualified() ?
    qualifier().staticContextQualifier() : inStaticContext();
        }
        return getParent().Define_boolean_inStaticContext(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
