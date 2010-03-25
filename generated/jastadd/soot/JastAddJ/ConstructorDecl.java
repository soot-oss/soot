
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class ConstructorDecl extends BodyDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        accessibleFrom_TypeDecl_values = null;
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        throwsException_TypeDecl_values = null;
        name_computed = false;
        name_value = null;
        signature_computed = false;
        signature_value = null;
        sameSignature_ConstructorDecl_values = null;
        moreSpecificThan_ConstructorDecl_values = null;
        parameterDeclaration_String_values = null;
        circularThisInvocation_ConstructorDecl_values = null;
        sourceConstructorDecl_computed = false;
        sourceConstructorDecl_value = null;
        sootMethod_computed = false;
        sootMethod_value = null;
        sootRef_computed = false;
        sootRef_value = null;
        localNumOfFirstParameter_computed = false;
        offsetFirstEnclosingVariable_computed = false;
        handlesException_TypeDecl_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ConstructorDecl clone() throws CloneNotSupportedException {
        ConstructorDecl node = (ConstructorDecl)super.clone();
        node.accessibleFrom_TypeDecl_values = null;
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.throwsException_TypeDecl_values = null;
        node.name_computed = false;
        node.name_value = null;
        node.signature_computed = false;
        node.signature_value = null;
        node.sameSignature_ConstructorDecl_values = null;
        node.moreSpecificThan_ConstructorDecl_values = null;
        node.parameterDeclaration_String_values = null;
        node.circularThisInvocation_ConstructorDecl_values = null;
        node.sourceConstructorDecl_computed = false;
        node.sourceConstructorDecl_value = null;
        node.sootMethod_computed = false;
        node.sootMethod_value = null;
        node.sootRef_computed = false;
        node.sootRef_value = null;
        node.localNumOfFirstParameter_computed = false;
        node.offsetFirstEnclosingVariable_computed = false;
        node.handlesException_TypeDecl_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ConstructorDecl copy() {
      try {
          ConstructorDecl node = (ConstructorDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ConstructorDecl fullCopy() {
        ConstructorDecl res = (ConstructorDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in LookupConstructor.jrag at line 164


  public boolean applicable(List argList) {
    if(getNumParameter() != argList.getNumChild())
      return false;
    for(int i = 0; i < getNumParameter(); i++) {
      TypeDecl arg = ((Expr)argList.getChild(i)).type();
      TypeDecl parameter = getParameter(i).type();
      if(!arg.instanceOf(parameter)) {
        return false;
      }  
    }
    return true;
  }

    // Declared in Modifiers.jrag at line 108

 
  public void checkModifiers() {
    super.checkModifiers();
  }

    // Declared in NameCheck.jrag at line 68



  public void nameCheck() {
    super.nameCheck();
    // 8.8
    if(!hostType().name().equals(name()))
      error("constructor " + name() +" does not have the same name as the simple name of the host class " + hostType().name());
    
    // 8.8.2
    if(hostType().lookupConstructor(this) != this)
      error("constructor with signature " + signature() + " is multiply declared in type " + hostType().typeName());

    if(circularThisInvocation(this))
      error("The constructor " + signature() + " may not directly or indirectly invoke itself");
  }

    // Declared in PrettyPrint.jadd at line 119

  
  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);
    s.append(name() + "(");
    if(getNumParameter() > 0) {
      getParameter(0).toString(s);
      for(int i = 1; i < getNumParameter(); i++) {
        s.append(", ");
        getParameter(i).toString(s);
      }
    }
    s.append(")");
    if(getNumException() > 0) {
      s.append(" throws ");
      getException(0).toString(s);
      for(int i = 1; i < getNumException(); i++) {
        s.append(", ");
        getException(i).toString(s);
      }
    }
    
    s.append(" {");
    if(hasConstructorInvocation()) {
      getConstructorInvocation().toString(s);
    }
    for(int i = 0; i < getBlock().getNumStmt(); i++) {
      getBlock().getStmt(i).toString(s);
    }
    s.append(indent());
    s.append("}");
  }

    // Declared in TypeCheck.jrag at line 424


  public void typeCheck() {
    // 8.8.4 (8.4.4)
    TypeDecl exceptionType = typeThrowable();
    for(int i = 0; i < getNumException(); i++) {
      TypeDecl typeDecl = getException(i).type();
      if(!typeDecl.instanceOf(exceptionType))
        error(signature() + " throws non throwable type " + typeDecl.fullName());
    }
  }

    // Declared in Enums.jrag at line 135

  protected void transformEnumConstructors() {
    // add implicit super constructor access since we are traversing
    // without doing rewrites
    if(!hasConstructorInvocation()) {
      setConstructorInvocation(
        new ExprStmt(
          new SuperConstructorAccess("super", new List())
        )
      );
    }
    super.transformEnumConstructors();
    getParameterList().insertChild(
      new ParameterDeclaration(new TypeAccess("java.lang", "String"), "@p0"),
      0
    );
    getParameterList().insertChild(
      new ParameterDeclaration(new TypeAccess("int"), "@p1"),
      1
    );
  }

    // Declared in Generics.jrag at line 1038

  

  public BodyDecl p(Parameterization parTypeDecl) {
    ConstructorDecl c = new ConstructorDeclSubstituted(
      (Modifiers)getModifiers().fullCopy(),
      getID(),
      getParameterList().substitute(parTypeDecl),
      getExceptionList().substitute(parTypeDecl),
      new Opt(),
      new Block(),
      this
    );
    return c;
  }

    // Declared in InnerClasses.jrag at line 445


  // add val$name as parameters to the constructor
  protected boolean addEnclosingVariables = true;

    // Declared in InnerClasses.jrag at line 446

  public void addEnclosingVariables() {
    if(!addEnclosingVariables) return;
    addEnclosingVariables = false;
    hostType().addEnclosingVariables();
    for(Iterator iter = hostType().enclosingVariables().iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      getParameterList().add(new ParameterDeclaration(v.type(), "val$" + v.name()));
    }
  }

    // Declared in InnerClasses.jrag at line 480


  public ConstructorDecl createAccessor() {
    ConstructorDecl c = (ConstructorDecl)hostType().getAccessor(this, "constructor");
    if(c != null) return c;

    // make sure enclosing varibles are added as parameters prior to building accessor
    addEnclosingVariables();

    Modifiers modifiers = new Modifiers(new List());
    modifiers.addModifier(new Modifier("synthetic"));
    modifiers.addModifier(new Modifier("public"));

    List parameters = createAccessorParameters();

    List exceptionList = new List(); 
    for(int i = 0; i < getNumException(); i++)
      exceptionList.add(getException(i).type().createQualifiedAccess());
    
    // add all parameters as arguments except for the dummy parameter
    List args = new List();
    for(int i = 0; i < parameters.getNumChildNoTransform() - 1; i++)
      args.add(new VarAccess(((ParameterDeclaration)parameters.getChildNoTransform(i)).name()));
    ConstructorAccess access = new ConstructorAccess("this", args);
    access.addEnclosingVariables = false;

    c = new ConstructorDecl(
      modifiers,
      name(),
      parameters,
      exceptionList,
      new Opt(
        new ExprStmt(
          access
        )
      ),
      new Block(
        new List().add(new ReturnStmt(new Opt()))
      )
    );
    c = hostType().addConstructor(c);
    c.addEnclosingVariables = false;
    hostType().addAccessor(this, "constructor", c);
    return c;
  }

    // Declared in InnerClasses.jrag at line 524


  protected List createAccessorParameters() {
    List parameters = new List();
    for (int i=0; i<getNumParameter(); i++)
      parameters.add(new ParameterDeclaration(getParameter(i).type(), getParameter(i).name()));
    parameters.add(new ParameterDeclaration(createAnonymousJavaTypeDecl().createBoundAccess(), ("p" + getNumParameter())));
    return parameters;
  }

    // Declared in InnerClasses.jrag at line 532


  protected TypeDecl createAnonymousJavaTypeDecl() {
    ClassDecl classDecl =
      new ClassDecl(
          new Modifiers(new List().add(new Modifier("synthetic"))),
          "" + hostType().nextAnonymousIndex(),
          new Opt(),
          new List(),
          new List()
      );
    classDecl = hostType().addMemberClass(classDecl);
    hostType().addNestedType(classDecl);
    return classDecl;
  }

    // Declared in Transformations.jrag at line 119



  public void transformation() {
    // this$val as fields and constructor parameters
    addEnclosingVariables();
    super.transformation();
  }

    // Declared in EmitJimple.jrag at line 234

  public void jimplify1phase2() {
    String name = "<init>";
    ArrayList parameters = new ArrayList();
    ArrayList paramnames = new ArrayList();
    // this$0
    TypeDecl typeDecl = hostType();
    if(typeDecl.needsEnclosing())
      parameters.add(typeDecl.enclosingType().getSootType());
    if(typeDecl.needsSuperEnclosing()) {
      TypeDecl superClass = ((ClassDecl)typeDecl).superclass();
      parameters.add(superClass.enclosingType().getSootType());
    }
    // args
    for(int i = 0; i < getNumParameter(); i++) {
      parameters.add(getParameter(i).type().getSootType());
      paramnames.add(getParameter(i).name());
    }
    soot.Type returnType = soot.VoidType.v();
    int modifiers = sootTypeModifiers();
    ArrayList throwtypes = new ArrayList();
    for(int i = 0; i < getNumException(); i++)
      throwtypes.add(getException(i).type().getSootClassDecl());
    String signature = SootMethod.getSubSignature(name, parameters, returnType);
    if(!hostType().getSootClassDecl().declaresMethod(signature)) {
      SootMethod m = new SootMethod(name, parameters, returnType, modifiers, throwtypes);
      hostType().getSootClassDecl().addMethod(m);
      m.addTag(new soot.tagkit.ParamNamesTag(paramnames));
      sootMethod = m;
    } else {
    	sootMethod = hostType().getSootClassDecl().getMethod(signature);
    }
    addAttributes();
  }

    // Declared in EmitJimple.jrag at line 294



  public SootMethod sootMethod;

    // Declared in AnnotationsCodegen.jrag at line 57

  public void addAttributes() {
    super.addAttributes();
    ArrayList c = new ArrayList();
    getModifiers().addRuntimeVisibleAnnotationsAttribute(c);
    getModifiers().addRuntimeInvisibleAnnotationsAttribute(c);
    addRuntimeVisibleParameterAnnotationsAttribute(c);
    addRuntimeInvisibleParameterAnnotationsAttribute(c);
    addSourceLevelParameterAnnotationsAttribute(c);
    getModifiers().addSourceOnlyAnnotations(c);
    for(Iterator iter = c.iterator(); iter.hasNext(); ) {
      soot.tagkit.Tag tag = (soot.tagkit.Tag)iter.next();
      sootMethod.addTag(tag);
    }
  }

    // Declared in AnnotationsCodegen.jrag at line 186

  public void addRuntimeVisibleParameterAnnotationsAttribute(Collection c) {
    boolean foundVisibleAnnotations = false;
    Collection annotations = new ArrayList(getNumParameter());
    for(int i = 0; i < getNumParameter(); i++) {
      Collection a = getParameter(i).getModifiers().runtimeVisibleAnnotations();
      if(!a.isEmpty()) foundVisibleAnnotations = true;
      soot.tagkit.VisibilityAnnotationTag tag = new soot.tagkit.VisibilityAnnotationTag(soot.tagkit.AnnotationConstants.RUNTIME_VISIBLE);
      for(Iterator iter = a.iterator(); iter.hasNext(); ) {
        Annotation annotation = (Annotation)iter.next();
        ArrayList elements = new ArrayList(1);
        annotation.appendAsAttributeTo(elements);
        tag.addAnnotation((soot.tagkit.AnnotationTag)elements.get(0));
      }
      annotations.add(tag);
    }
    if(foundVisibleAnnotations) {
      soot.tagkit.VisibilityParameterAnnotationTag tag = new soot.tagkit.VisibilityParameterAnnotationTag(annotations.size(), soot.tagkit.AnnotationConstants.RUNTIME_VISIBLE);
      for(Iterator iter = annotations.iterator(); iter.hasNext(); ) {
        tag.addVisibilityAnnotation((soot.tagkit.VisibilityAnnotationTag)iter.next());
      }
      c.add(tag);
    }
  }

    // Declared in AnnotationsCodegen.jrag at line 242

  public void addRuntimeInvisibleParameterAnnotationsAttribute(Collection c) {
    boolean foundVisibleAnnotations = false;
    Collection annotations = new ArrayList(getNumParameter());
    for(int i = 0; i < getNumParameter(); i++) {
      Collection a = getParameter(i).getModifiers().runtimeInvisibleAnnotations();
      if(!a.isEmpty()) foundVisibleAnnotations = true;
      soot.tagkit.VisibilityAnnotationTag tag = new soot.tagkit.VisibilityAnnotationTag(soot.tagkit.AnnotationConstants.RUNTIME_INVISIBLE);
      for(Iterator iter = a.iterator(); iter.hasNext(); ) {
        Annotation annotation = (Annotation)iter.next();
        ArrayList elements = new ArrayList(1);
        annotation.appendAsAttributeTo(elements);
        tag.addAnnotation((soot.tagkit.AnnotationTag)elements.get(0));
      }
      annotations.add(tag);
    }
    if(foundVisibleAnnotations) {
      soot.tagkit.VisibilityParameterAnnotationTag tag = new soot.tagkit.VisibilityParameterAnnotationTag(annotations.size(), soot.tagkit.AnnotationConstants.RUNTIME_INVISIBLE);
      for(Iterator iter = annotations.iterator(); iter.hasNext(); ) {
        tag.addVisibilityAnnotation((soot.tagkit.VisibilityAnnotationTag)iter.next());
      }
      c.add(tag);
    }
  }

    // Declared in AnnotationsCodegen.jrag at line 281

  public void addSourceLevelParameterAnnotationsAttribute(Collection c) {
    boolean foundVisibleAnnotations = false;
    Collection annotations = new ArrayList(getNumParameter());
    for(int i = 0; i < getNumParameter(); i++) {
      getParameter(i).getModifiers().addSourceOnlyAnnotations(c);
    }
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 72

    public ConstructorDecl() {
        super();

        setChild(new List(), 1);
        setChild(new List(), 2);
        setChild(new Opt(), 3);

    }

    // Declared in java.ast at line 13


    // Declared in java.ast line 72
    public ConstructorDecl(Modifiers p0, String p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
    }

    // Declared in java.ast at line 23


    // Declared in java.ast line 72
    public ConstructorDecl(Modifiers p0, beaver.Symbol p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
    }

    // Declared in java.ast at line 32


  protected int numChildren() {
    return 5;
  }

    // Declared in java.ast at line 35

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 72
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
    // Declared in java.ast line 72
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

    // Declared in java.ast at line 2
    // Declared in java.ast line 72
    public void setParameterList(List<ParameterDeclaration> list) {
        setChild(list, 1);
    }

    // Declared in java.ast at line 6


    public int getNumParameter() {
        return getParameterList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public ParameterDeclaration getParameter(int i) {
        return (ParameterDeclaration)getParameterList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addParameter(ParameterDeclaration node) {
        List<ParameterDeclaration> list = (parent == null || state == null) ? getParameterListNoTransform() : getParameterList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addParameterNoTransform(ParameterDeclaration node) {
        List<ParameterDeclaration> list = getParameterListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setParameter(ParameterDeclaration node, int i) {
        List<ParameterDeclaration> list = getParameterList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<ParameterDeclaration> getParameters() {
        return getParameterList();
    }

    // Declared in java.ast at line 31

    public List<ParameterDeclaration> getParametersNoTransform() {
        return getParameterListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<ParameterDeclaration> getParameterList() {
        List<ParameterDeclaration> list = (List<ParameterDeclaration>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<ParameterDeclaration> getParameterListNoTransform() {
        return (List<ParameterDeclaration>)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 72
    public void setExceptionList(List<Access> list) {
        setChild(list, 2);
    }

    // Declared in java.ast at line 6


    public int getNumException() {
        return getExceptionList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getException(int i) {
        return (Access)getExceptionList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addException(Access node) {
        List<Access> list = (parent == null || state == null) ? getExceptionListNoTransform() : getExceptionList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addExceptionNoTransform(Access node) {
        List<Access> list = getExceptionListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setException(Access node, int i) {
        List<Access> list = getExceptionList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Access> getExceptions() {
        return getExceptionList();
    }

    // Declared in java.ast at line 31

    public List<Access> getExceptionsNoTransform() {
        return getExceptionListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getExceptionList() {
        List<Access> list = (List<Access>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getExceptionListNoTransform() {
        return (List<Access>)getChildNoTransform(2);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 72
    public void setConstructorInvocationOpt(Opt<Stmt> opt) {
        setChild(opt, 3);
    }

    // Declared in java.ast at line 6


    public boolean hasConstructorInvocation() {
        return getConstructorInvocationOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Stmt getConstructorInvocation() {
        return (Stmt)getConstructorInvocationOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setConstructorInvocation(Stmt node) {
        getConstructorInvocationOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Stmt> getConstructorInvocationOpt() {
        return (Opt<Stmt>)getChild(3);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Stmt> getConstructorInvocationOptNoTransform() {
        return (Opt<Stmt>)getChildNoTransform(3);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 72
    public void setBlock(Block node) {
        setChild(node, 4);
    }

    // Declared in java.ast at line 5

    public Block getBlock() {
        return (Block)getChild(4);
    }

    // Declared in java.ast at line 9


    public Block getBlockNoTransform() {
        return (Block)getChildNoTransform(4);
    }

    // Declared in EmitJimpleRefinements.jrag at line 121

  
    public void jimplify2() {
    if(!generate() || sootMethod().hasActiveBody()  ||
      (sootMethod().getSource() != null && (sootMethod().getSource() instanceof soot.coffi.CoffiMethodSource)) ) return;  
     JimpleBody body = Jimple.v().newBody(sootMethod());
    sootMethod().setActiveBody(body);
    Body b = new Body(hostType(), body, this);
    b.setLine(this);
    for(int i = 0; i < getNumParameter(); i++)
      getParameter(i).jimplify2(b);

    boolean needsInit = true;

    if(hasConstructorInvocation()) {
      getConstructorInvocation().jimplify2(b);
      Stmt stmt = getConstructorInvocation();
      if(stmt instanceof ExprStmt) {
        ExprStmt exprStmt = (ExprStmt)stmt;
        Expr expr = exprStmt.getExpr();
        if(!expr.isSuperConstructorAccess())
          needsInit = false;

      }
    }

    if(hostType().needsEnclosing()) {
      TypeDecl type = hostType().enclosingType();
      b.add(Jimple.v().newAssignStmt(
        Jimple.v().newInstanceFieldRef(
          b.emitThis(hostType()),
          hostType().getSootField("this$0", type).makeRef()
        ),
        asLocal(b, Jimple.v().newParameterRef(type.getSootType(), 0))
      ));
    }
    
    for(Iterator iter = hostType().enclosingVariables().iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      ParameterDeclaration p = (ParameterDeclaration)parameterDeclaration("val$" + v.name()).iterator().next();
      b.add(Jimple.v().newAssignStmt(
        Jimple.v().newInstanceFieldRef(
          b.emitThis(hostType()),
          Scene.v().makeFieldRef(hostType().getSootClassDecl(), "val$" + v.name(), v.type().getSootType(), false)
          //hostType().getSootClassDecl().getField("val$" + v.name(), v.type().getSootType()).makeRef()
        ),
        p.local
      ));
    }

    if(needsInit) {
      TypeDecl typeDecl = hostType();
      for(int i = 0; i < typeDecl.getNumBodyDecl(); i++) {
        BodyDecl bodyDecl = typeDecl.getBodyDecl(i);
        if(bodyDecl instanceof FieldDeclaration && bodyDecl.generate()) {
          FieldDeclaration f = (FieldDeclaration)bodyDecl;
          if(!f.isStatic() && f.hasInit()) {
            soot.Local base = b.emitThis(hostType());
            Local l = asLocal(b,
              f.getInit().type().emitCastTo(b, f.getInit(), f.type()), // AssignConversion
              f.type().getSootType()
            );
            b.setLine(f);
            b.add(Jimple.v().newAssignStmt(
              Jimple.v().newInstanceFieldRef(base, f.sootRef()),
              l
            ));
          }
        }
        else if(bodyDecl instanceof InstanceInitializer && bodyDecl.generate()) {
          bodyDecl.jimplify2(b);
        }
      }
    }
    getBlock().jimplify2(b);
    b.add(Jimple.v().newReturnVoidStmt());
  }

    // Declared in LookupConstructor.jrag at line 156
private boolean refined_ConstructorDecl_ConstructorDecl_moreSpecificThan_ConstructorDecl(ConstructorDecl m)
{
    for(int i = 0; i < getNumParameter(); i++) {
      if(!getParameter(i).type().instanceOf(m.getParameter(i).type()))
        return false;
    }
    return true;
  }

    protected java.util.Map accessibleFrom_TypeDecl_values;
    // Declared in AccessControl.jrag at line 94
 @SuppressWarnings({"unchecked", "cast"})     public boolean accessibleFrom(TypeDecl type) {
        Object _parameters = type;
if(accessibleFrom_TypeDecl_values == null) accessibleFrom_TypeDecl_values = new java.util.HashMap(4);
        if(accessibleFrom_TypeDecl_values.containsKey(_parameters)) {
            return ((Boolean)accessibleFrom_TypeDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean accessibleFrom_TypeDecl_value = accessibleFrom_compute(type);
        if(isFinal && num == state().boundariesCrossed)
            accessibleFrom_TypeDecl_values.put(_parameters, Boolean.valueOf(accessibleFrom_TypeDecl_value));
        return accessibleFrom_TypeDecl_value;
    }

    private boolean accessibleFrom_compute(TypeDecl type) {
    if(!hostType().accessibleFrom(type))
      return false;
    else if(isPublic())
      return true;
    else if(isProtected()) {
      return true;
    }
    else if(isPrivate()) {
      return hostType().topLevelType() == type.topLevelType();
    }
    else
      return hostPackage().equals(type.hostPackage());
  }

    // Declared in DefiniteAssignment.jrag at line 297
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        Object _parameters = v;
if(isDAafter_Variable_values == null) isDAafter_Variable_values = new java.util.HashMap(4);
        if(isDAafter_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDAafter_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDAafter_Variable_values.put(_parameters, Boolean.valueOf(isDAafter_Variable_value));
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {  return getBlock().isDAafter(v) && getBlock().checkReturnDA(v);  }

    // Declared in DefiniteAssignment.jrag at line 753
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        Object _parameters = v;
if(isDUafter_Variable_values == null) isDUafter_Variable_values = new java.util.HashMap(4);
        if(isDUafter_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDUafter_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDUafter_Variable_values.put(_parameters, Boolean.valueOf(isDUafter_Variable_value));
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {  return getBlock().isDUafter(v) && getBlock().checkReturnDU(v);  }

    protected java.util.Map throwsException_TypeDecl_values;
    // Declared in ExceptionHandling.jrag at line 136
 @SuppressWarnings({"unchecked", "cast"})     public boolean throwsException(TypeDecl exceptionType) {
        Object _parameters = exceptionType;
if(throwsException_TypeDecl_values == null) throwsException_TypeDecl_values = new java.util.HashMap(4);
        if(throwsException_TypeDecl_values.containsKey(_parameters)) {
            return ((Boolean)throwsException_TypeDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean throwsException_TypeDecl_value = throwsException_compute(exceptionType);
        if(isFinal && num == state().boundariesCrossed)
            throwsException_TypeDecl_values.put(_parameters, Boolean.valueOf(throwsException_TypeDecl_value));
        return throwsException_TypeDecl_value;
    }

    private boolean throwsException_compute(TypeDecl exceptionType) {
    for(int i = 0; i < getNumException(); i++)
      if(exceptionType.instanceOf(getException(i).type()))
        return true;
    return false;
  }

    protected boolean name_computed = false;
    protected String name_value;
    // Declared in LookupConstructor.jrag at line 130
 @SuppressWarnings({"unchecked", "cast"})     public String name() {
        if(name_computed) {
            return name_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        name_value = name_compute();
        if(isFinal && num == state().boundariesCrossed)
            name_computed = true;
        return name_value;
    }

    private String name_compute() {  return getID();  }

    protected boolean signature_computed = false;
    protected String signature_value;
    // Declared in LookupConstructor.jrag at line 132
 @SuppressWarnings({"unchecked", "cast"})     public String signature() {
        if(signature_computed) {
            return signature_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        signature_value = signature_compute();
        if(isFinal && num == state().boundariesCrossed)
            signature_computed = true;
        return signature_value;
    }

    private String signature_compute() {
    StringBuffer s = new StringBuffer();
    s.append(name() + "(");
    for(int i = 0; i < getNumParameter(); i++) {
      s.append(getParameter(i));
      if(i != getNumParameter() - 1)
        s.append(", ");
    }
    s.append(")");
    return s.toString();
  }

    protected java.util.Map sameSignature_ConstructorDecl_values;
    // Declared in LookupConstructor.jrag at line 145
 @SuppressWarnings({"unchecked", "cast"})     public boolean sameSignature(ConstructorDecl c) {
        Object _parameters = c;
if(sameSignature_ConstructorDecl_values == null) sameSignature_ConstructorDecl_values = new java.util.HashMap(4);
        if(sameSignature_ConstructorDecl_values.containsKey(_parameters)) {
            return ((Boolean)sameSignature_ConstructorDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean sameSignature_ConstructorDecl_value = sameSignature_compute(c);
        if(isFinal && num == state().boundariesCrossed)
            sameSignature_ConstructorDecl_values.put(_parameters, Boolean.valueOf(sameSignature_ConstructorDecl_value));
        return sameSignature_ConstructorDecl_value;
    }

    private boolean sameSignature_compute(ConstructorDecl c) {
    if(!name().equals(c.name()))
      return false;
    if(c.getNumParameter() != getNumParameter())
      return false;
    for(int i = 0; i < getNumParameter(); i++)
      if(!c.getParameter(i).type().equals(getParameter(i).type()))
        return false;
    return true;
  }

    protected java.util.Map moreSpecificThan_ConstructorDecl_values;
    // Declared in MethodSignature.jrag at line 153
 @SuppressWarnings({"unchecked", "cast"})     public boolean moreSpecificThan(ConstructorDecl m) {
        Object _parameters = m;
if(moreSpecificThan_ConstructorDecl_values == null) moreSpecificThan_ConstructorDecl_values = new java.util.HashMap(4);
        if(moreSpecificThan_ConstructorDecl_values.containsKey(_parameters)) {
            return ((Boolean)moreSpecificThan_ConstructorDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean moreSpecificThan_ConstructorDecl_value = moreSpecificThan_compute(m);
        if(isFinal && num == state().boundariesCrossed)
            moreSpecificThan_ConstructorDecl_values.put(_parameters, Boolean.valueOf(moreSpecificThan_ConstructorDecl_value));
        return moreSpecificThan_ConstructorDecl_value;
    }

    private boolean moreSpecificThan_compute(ConstructorDecl m) {
    if(!isVariableArity() && !m.isVariableArity())
      return refined_ConstructorDecl_ConstructorDecl_moreSpecificThan_ConstructorDecl(m);
    int num = Math.max(getNumParameter(), m.getNumParameter());
    for(int i = 0; i < num; i++) {
      TypeDecl t1 = i < getNumParameter() - 1 ? getParameter(i).type() : getParameter(getNumParameter()-1).type().componentType();
      TypeDecl t2 = i < m.getNumParameter() - 1 ? m.getParameter(i).type() : m.getParameter(m.getNumParameter()-1).type().componentType();
      if(!t1.instanceOf(t2))
        return false;
    }
    return true;
  }

    protected java.util.Map parameterDeclaration_String_values;
    // Declared in LookupVariable.jrag at line 105
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet parameterDeclaration(String name) {
        Object _parameters = name;
if(parameterDeclaration_String_values == null) parameterDeclaration_String_values = new java.util.HashMap(4);
        if(parameterDeclaration_String_values.containsKey(_parameters)) {
            return (SimpleSet)parameterDeclaration_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet parameterDeclaration_String_value = parameterDeclaration_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            parameterDeclaration_String_values.put(_parameters, parameterDeclaration_String_value);
        return parameterDeclaration_String_value;
    }

    private SimpleSet parameterDeclaration_compute(String name) {
    for(int i = 0; i < getNumParameter(); i++)
      if(getParameter(i).name().equals(name))
        return (ParameterDeclaration)getParameter(i);
    return SimpleSet.emptySet;
  }

    // Declared in Modifiers.jrag at line 215
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSynthetic() {
        ASTNode$State state = state();
        boolean isSynthetic_value = isSynthetic_compute();
        return isSynthetic_value;
    }

    private boolean isSynthetic_compute() {  return getModifiers().isSynthetic();  }

    // Declared in Modifiers.jrag at line 233
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPublic() {
        ASTNode$State state = state();
        boolean isPublic_value = isPublic_compute();
        return isPublic_value;
    }

    private boolean isPublic_compute() {  return getModifiers().isPublic();  }

    // Declared in Modifiers.jrag at line 234
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPrivate() {
        ASTNode$State state = state();
        boolean isPrivate_value = isPrivate_compute();
        return isPrivate_value;
    }

    private boolean isPrivate_compute() {  return getModifiers().isPrivate();  }

    // Declared in Modifiers.jrag at line 235
 @SuppressWarnings({"unchecked", "cast"})     public boolean isProtected() {
        ASTNode$State state = state();
        boolean isProtected_value = isProtected_compute();
        return isProtected_value;
    }

    private boolean isProtected_compute() {  return getModifiers().isProtected();  }

    protected java.util.Map circularThisInvocation_ConstructorDecl_values;
    // Declared in NameCheck.jrag at line 83
 @SuppressWarnings({"unchecked", "cast"})     public boolean circularThisInvocation(ConstructorDecl decl) {
        Object _parameters = decl;
if(circularThisInvocation_ConstructorDecl_values == null) circularThisInvocation_ConstructorDecl_values = new java.util.HashMap(4);
        if(circularThisInvocation_ConstructorDecl_values.containsKey(_parameters)) {
            return ((Boolean)circularThisInvocation_ConstructorDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean circularThisInvocation_ConstructorDecl_value = circularThisInvocation_compute(decl);
        if(isFinal && num == state().boundariesCrossed)
            circularThisInvocation_ConstructorDecl_values.put(_parameters, Boolean.valueOf(circularThisInvocation_ConstructorDecl_value));
        return circularThisInvocation_ConstructorDecl_value;
    }

    private boolean circularThisInvocation_compute(ConstructorDecl decl) {
    if(hasConstructorInvocation()) {
      Expr e = ((ExprStmt)getConstructorInvocation()).getExpr();
      if(e instanceof ConstructorAccess) {
        ConstructorDecl constructorDecl = ((ConstructorAccess)e).decl();
        if(constructorDecl == decl)
          return true;
        return constructorDecl.circularThisInvocation(decl);
      }
    }
    return false;
  }

    // Declared in TypeAnalysis.jrag at line 268
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        ASTNode$State state = state();
        TypeDecl type_value = type_compute();
        return type_value;
    }

    private TypeDecl type_compute() {  return unknownType();  }

    // Declared in TypeAnalysis.jrag at line 274
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVoid() {
        ASTNode$State state = state();
        boolean isVoid_value = isVoid_compute();
        return isVoid_value;
    }

    private boolean isVoid_compute() {  return true;  }

    // Declared in Annotations.jrag at line 286
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasAnnotationSuppressWarnings(String s) {
        ASTNode$State state = state();
        boolean hasAnnotationSuppressWarnings_String_value = hasAnnotationSuppressWarnings_compute(s);
        return hasAnnotationSuppressWarnings_String_value;
    }

    private boolean hasAnnotationSuppressWarnings_compute(String s) {  return getModifiers().hasAnnotationSuppressWarnings(s);  }

    // Declared in Annotations.jrag at line 324
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDeprecated() {
        ASTNode$State state = state();
        boolean isDeprecated_value = isDeprecated_compute();
        return isDeprecated_value;
    }

    private boolean isDeprecated_compute() {  return getModifiers().hasDeprecatedAnnotation();  }

    protected boolean sourceConstructorDecl_computed = false;
    protected ConstructorDecl sourceConstructorDecl_value;
    // Declared in Generics.jrag at line 1269
 @SuppressWarnings({"unchecked", "cast"})     public ConstructorDecl sourceConstructorDecl() {
        if(sourceConstructorDecl_computed) {
            return sourceConstructorDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sourceConstructorDecl_value = sourceConstructorDecl_compute();
        if(isFinal && num == state().boundariesCrossed)
            sourceConstructorDecl_computed = true;
        return sourceConstructorDecl_value;
    }

    private ConstructorDecl sourceConstructorDecl_compute() {  return this;  }

    // Declared in MethodSignature.jrag at line 175
 @SuppressWarnings({"unchecked", "cast"})     public boolean applicableBySubtyping(List argList) {
        ASTNode$State state = state();
        boolean applicableBySubtyping_List_value = applicableBySubtyping_compute(argList);
        return applicableBySubtyping_List_value;
    }

    private boolean applicableBySubtyping_compute(List argList) {
    if(getNumParameter() != argList.getNumChild())
      return false;
    for(int i = 0; i < getNumParameter(); i++) {
      TypeDecl arg = ((Expr)argList.getChild(i)).type();
      if(!arg.instanceOf(getParameter(i).type()))
        return false;
    }
    return true;
  }

    // Declared in MethodSignature.jrag at line 195
 @SuppressWarnings({"unchecked", "cast"})     public boolean applicableByMethodInvocationConversion(List argList) {
        ASTNode$State state = state();
        boolean applicableByMethodInvocationConversion_List_value = applicableByMethodInvocationConversion_compute(argList);
        return applicableByMethodInvocationConversion_List_value;
    }

    private boolean applicableByMethodInvocationConversion_compute(List argList) {
    if(getNumParameter() != argList.getNumChild())
      return false;
    for(int i = 0; i < getNumParameter(); i++) {
      TypeDecl arg = ((Expr)argList.getChild(i)).type();
      if(!arg.methodInvocationConversionTo(getParameter(i).type()))
        return false;
    }
    return true;
  }

    // Declared in MethodSignature.jrag at line 216
 @SuppressWarnings({"unchecked", "cast"})     public boolean applicableVariableArity(List argList) {
        ASTNode$State state = state();
        boolean applicableVariableArity_List_value = applicableVariableArity_compute(argList);
        return applicableVariableArity_List_value;
    }

    private boolean applicableVariableArity_compute(List argList) {
    for(int i = 0; i < getNumParameter() - 1; i++) {
      TypeDecl arg = ((Expr)argList.getChild(i)).type();
      if(!arg.methodInvocationConversionTo(getParameter(i).type()))
        return false;
    }
    for(int i = getNumParameter() - 1; i < argList.getNumChild(); i++) {
      TypeDecl arg = ((Expr)argList.getChild(i)).type();
      if(!arg.methodInvocationConversionTo(lastParameter().type().componentType()))
        return false;
    }
    return true;
  }

    // Declared in MethodSignature.jrag at line 303
 @SuppressWarnings({"unchecked", "cast"})     public boolean potentiallyApplicable(List argList) {
        ASTNode$State state = state();
        boolean potentiallyApplicable_List_value = potentiallyApplicable_compute(argList);
        return potentiallyApplicable_List_value;
    }

    private boolean potentiallyApplicable_compute(List argList) {
    if(isVariableArity() && !(argList.getNumChild() >= arity()-1))
      return false;
    if(!isVariableArity() && !(arity() == argList.getNumChild()))
      return false;
    return true;
  }

    // Declared in MethodSignature.jrag at line 310
 @SuppressWarnings({"unchecked", "cast"})     public int arity() {
        ASTNode$State state = state();
        int arity_value = arity_compute();
        return arity_value;
    }

    private int arity_compute() {  return getNumParameter();  }

    // Declared in VariableArityParameters.jrag at line 34
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVariableArity() {
        ASTNode$State state = state();
        boolean isVariableArity_value = isVariableArity_compute();
        return isVariableArity_value;
    }

    private boolean isVariableArity_compute() {  return getNumParameter() == 0 ? false : getParameter(getNumParameter()-1).isVariableArity();  }

    // Declared in VariableArityParameters.jrag at line 63
 @SuppressWarnings({"unchecked", "cast"})     public ParameterDeclaration lastParameter() {
        ASTNode$State state = state();
        ParameterDeclaration lastParameter_value = lastParameter_compute();
        return lastParameter_value;
    }

    private ParameterDeclaration lastParameter_compute() {  return getParameter(getNumParameter() - 1);  }

    // Declared in InnerClasses.jrag at line 421
 @SuppressWarnings({"unchecked", "cast"})     public boolean needsEnclosing() {
        ASTNode$State state = state();
        boolean needsEnclosing_value = needsEnclosing_compute();
        return needsEnclosing_value;
    }

    private boolean needsEnclosing_compute() {  return hostType().needsEnclosing();  }

    // Declared in InnerClasses.jrag at line 422
 @SuppressWarnings({"unchecked", "cast"})     public boolean needsSuperEnclosing() {
        ASTNode$State state = state();
        boolean needsSuperEnclosing_value = needsSuperEnclosing_compute();
        return needsSuperEnclosing_value;
    }

    private boolean needsSuperEnclosing_compute() {  return hostType().needsSuperEnclosing();  }

    // Declared in InnerClasses.jrag at line 424
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl enclosing() {
        ASTNode$State state = state();
        TypeDecl enclosing_value = enclosing_compute();
        return enclosing_value;
    }

    private TypeDecl enclosing_compute() {  return hostType().enclosing();  }

    // Declared in InnerClasses.jrag at line 425
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl superEnclosing() {
        ASTNode$State state = state();
        TypeDecl superEnclosing_value = superEnclosing_compute();
        return superEnclosing_value;
    }

    private TypeDecl superEnclosing_compute() {  return hostType().superEnclosing();  }

    // Declared in EmitJimple.jrag at line 120
 @SuppressWarnings({"unchecked", "cast"})     public int sootTypeModifiers() {
        ASTNode$State state = state();
        int sootTypeModifiers_value = sootTypeModifiers_compute();
        return sootTypeModifiers_value;
    }

    private int sootTypeModifiers_compute() {
    int result = 0;
    if(isPublic()) result |= soot.Modifier.PUBLIC;
    if(isProtected()) result |= soot.Modifier.PROTECTED;
    if(isPrivate()) result |= soot.Modifier.PRIVATE;
    return result;
  }

    protected boolean sootMethod_computed = false;
    protected SootMethod sootMethod_value;
    // Declared in EmitJimple.jrag at line 295
 @SuppressWarnings({"unchecked", "cast"})     public SootMethod sootMethod() {
        if(sootMethod_computed) {
            return sootMethod_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sootMethod_value = sootMethod_compute();
        if(isFinal && num == state().boundariesCrossed)
            sootMethod_computed = true;
        return sootMethod_value;
    }

    private SootMethod sootMethod_compute() {
    ArrayList list = new ArrayList();
    // this$0
    TypeDecl typeDecl = hostType();
    if(typeDecl.needsEnclosing())
      list.add(typeDecl.enclosingType().getSootType());
    if(typeDecl.needsSuperEnclosing()) {
      TypeDecl superClass = ((ClassDecl)typeDecl).superclass();
      list.add(superClass.enclosingType().getSootType());
    }
    // args
    for(int i = 0; i < getNumParameter(); i++)
      list.add(getParameter(i).type().getSootType());
    return hostType().getSootClassDecl().getMethod("<init>", list, soot.VoidType.v());
  }

    protected boolean sootRef_computed = false;
    protected SootMethodRef sootRef_value;
    // Declared in EmitJimple.jrag at line 310
 @SuppressWarnings({"unchecked", "cast"})     public SootMethodRef sootRef() {
        if(sootRef_computed) {
            return sootRef_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sootRef_value = sootRef_compute();
        if(isFinal && num == state().boundariesCrossed)
            sootRef_computed = true;
        return sootRef_value;
    }

    private SootMethodRef sootRef_compute() {
    ArrayList parameters = new ArrayList();
    TypeDecl typeDecl = hostType();
    if(typeDecl.needsEnclosing())
      parameters.add(typeDecl.enclosingType().getSootType());
    if(typeDecl.needsSuperEnclosing()) {
      TypeDecl superClass = ((ClassDecl)typeDecl).superclass();
      parameters.add(superClass.enclosingType().getSootType());
    }
    for(int i = 0; i < getNumParameter(); i++)
      parameters.add(getParameter(i).type().getSootType());
    SootMethodRef ref = Scene.v().makeConstructorRef(
      hostType().getSootClassDecl(),
      parameters
    );
    return ref;
  }

    protected boolean localNumOfFirstParameter_computed = false;
    protected int localNumOfFirstParameter_value;
    // Declared in LocalNum.jrag at line 32
 @SuppressWarnings({"unchecked", "cast"})     public int localNumOfFirstParameter() {
        if(localNumOfFirstParameter_computed) {
            return localNumOfFirstParameter_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        localNumOfFirstParameter_value = localNumOfFirstParameter_compute();
        if(isFinal && num == state().boundariesCrossed)
            localNumOfFirstParameter_computed = true;
        return localNumOfFirstParameter_value;
    }

    private int localNumOfFirstParameter_compute() {
    int i = 0;
    if(hostType().needsEnclosing())
      i++;
    if(hostType().needsSuperEnclosing())
      i++;
    return i;
  }

    protected boolean offsetFirstEnclosingVariable_computed = false;
    protected int offsetFirstEnclosingVariable_value;
    // Declared in LocalNum.jrag at line 41
 @SuppressWarnings({"unchecked", "cast"})     public int offsetFirstEnclosingVariable() {
        if(offsetFirstEnclosingVariable_computed) {
            return offsetFirstEnclosingVariable_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        offsetFirstEnclosingVariable_value = offsetFirstEnclosingVariable_compute();
        if(isFinal && num == state().boundariesCrossed)
            offsetFirstEnclosingVariable_computed = true;
        return offsetFirstEnclosingVariable_value;
    }

    private int offsetFirstEnclosingVariable_compute() {  return getNumParameter() == 0 ?
    localNumOfFirstParameter() :
    getParameter(getNumParameter()-1).localNum() + getParameter(getNumParameter()-1).type().variableSize();  }

    // Declared in GenericsCodegen.jrag at line 317
 @SuppressWarnings({"unchecked", "cast"})     public ConstructorDecl erasedConstructor() {
        ASTNode$State state = state();
        ConstructorDecl erasedConstructor_value = erasedConstructor_compute();
        return erasedConstructor_value;
    }

    private ConstructorDecl erasedConstructor_compute() {  return this;  }

    protected java.util.Map handlesException_TypeDecl_values;
    // Declared in ExceptionHandling.jrag at line 36
 @SuppressWarnings({"unchecked", "cast"})     public boolean handlesException(TypeDecl exceptionType) {
        Object _parameters = exceptionType;
if(handlesException_TypeDecl_values == null) handlesException_TypeDecl_values = new java.util.HashMap(4);
        if(handlesException_TypeDecl_values.containsKey(_parameters)) {
            return ((Boolean)handlesException_TypeDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
        if(isFinal && num == state().boundariesCrossed)
            handlesException_TypeDecl_values.put(_parameters, Boolean.valueOf(handlesException_TypeDecl_value));
        return handlesException_TypeDecl_value;
    }

    // Declared in TypeAnalysis.jrag at line 267
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl unknownType() {
        ASTNode$State state = state();
        TypeDecl unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
        return unknownType_value;
    }

    // Declared in DefiniteAssignment.jrag at line 300
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockNoTransform()) {
            return hasConstructorInvocation() ? getConstructorInvocation().isDAafter(v) : isDAbefore(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 756
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockNoTransform()) {
            return hasConstructorInvocation() ? getConstructorInvocation().isDUafter(v) : isDUbefore(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in ExceptionHandling.jrag at line 133
    public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
        if(caller == getConstructorInvocationOptNoTransform()) {
            return throwsException(exceptionType) || handlesException(exceptionType);
        }
        if(caller == getBlockNoTransform()) {
            return throwsException(exceptionType) || handlesException(exceptionType);
        }
        return getParent().Define_boolean_handlesException(this, caller, exceptionType);
    }

    // Declared in LookupMethod.jrag at line 45
    public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
        if(caller == getConstructorInvocationOptNoTransform()){
    Collection c = new ArrayList();
    for(Iterator iter = lookupMethod(name).iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(!hostType().memberMethods(name).contains(m) || m.isStatic())
        c.add(m);
    }
    return c;
  }
        return getParent().Define_Collection_lookupMethod(this, caller, name);
    }

    // Declared in LookupVariable.jrag at line 64
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return parameterDeclaration(name);
        }
        if(caller == getConstructorInvocationOptNoTransform()){
    SimpleSet set = parameterDeclaration(name);
    if(!set.isEmpty()) return set;
    for(Iterator iter = lookupVariable(name).iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      if(!hostType().memberFields(name).contains(v) || v.isStatic())
        set = set.add(v);
    }
    return set;
  }
        if(caller == getBlockNoTransform()){
    SimpleSet set = parameterDeclaration(name);
    if(!set.isEmpty()) return set;
    return lookupVariable(name);
  }
        return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }

    // Declared in Modifiers.jrag at line 280
    public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBePublic(this, caller);
    }

    // Declared in Modifiers.jrag at line 281
    public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeProtected(this, caller);
    }

    // Declared in Modifiers.jrag at line 282
    public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBePrivate(this, caller);
    }

    // Declared in NameCheck.jrag at line 242
    public ASTNode Define_ASTNode_enclosingBlock(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return this;
        }
        return getParent().Define_ASTNode_enclosingBlock(this, caller);
    }

    // Declared in SyntacticClassification.jrag at line 117
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getConstructorInvocationOptNoTransform()) {
            return NameType.EXPRESSION_NAME;
        }
        if(caller == getExceptionListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

    // Declared in TypeCheck.jrag at line 517
    public TypeDecl Define_TypeDecl_enclosingInstance(ASTNode caller, ASTNode child) {
        if(caller == getConstructorInvocationOptNoTransform()) {
            return unknownType();
        }
        return getParent().Define_TypeDecl_enclosingInstance(this, caller);
    }

    // Declared in TypeHierarchyCheck.jrag at line 132
    public boolean Define_boolean_inExplicitConstructorInvocation(ASTNode caller, ASTNode child) {
        if(caller == getConstructorInvocationOptNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_inExplicitConstructorInvocation(this, caller);
    }

    // Declared in TypeHierarchyCheck.jrag at line 144
    public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
        if(caller == getConstructorInvocationOptNoTransform()) {
            return false;
        }
        if(caller == getBlockNoTransform()) {
            return false;
        }
        return getParent().Define_boolean_inStaticContext(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 32
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return !hasConstructorInvocation() ? true : getConstructorInvocation().canCompleteNormally();
        }
        if(caller == getConstructorInvocationOptNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 77
    public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return false;
        }
        return getParent().Define_boolean_isMethodParameter(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 78
    public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return true;
        }
        return getParent().Define_boolean_isConstructorParameter(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 79
    public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return false;
        }
        return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
    }

    // Declared in Annotations.jrag at line 89
    public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
        if(caller == getModifiersNoTransform()) {
            return name.equals("CONSTRUCTOR");
        }
        return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
    }

    // Declared in VariableArityParameters.jrag at line 21
    public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int i = caller.getIndexOfChild(child);
            return i == getNumParameter() - 1;
        }
        return getParent().Define_boolean_variableArityValid(this, caller);
    }

    // Declared in LocalNum.jrag at line 45
    public int Define_int_localNum(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) { 
   int index = caller.getIndexOfChild(child);
{
    if(index == 0) {
      return localNumOfFirstParameter();
    }
    return getParameter(index-1).localNum() + getParameter(index-1).type().variableSize();
  }
}
        return getParent().Define_int_localNum(this, caller);
    }

    // Declared in Statements.jrag at line 351
    public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return getNumException() != 0;
        }
        return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
    }

public ASTNode rewriteTo() {
    // Declared in LookupConstructor.jrag at line 217
    if(!hasConstructorInvocation() && !hostType().isObject()) {
        state().duringLookupConstructor++;
        ASTNode result = rewriteRule0();
        state().duringLookupConstructor--;
        return result;
    }

    return super.rewriteTo();
}

    // Declared in LookupConstructor.jrag at line 217
    private ConstructorDecl rewriteRule0() {
{
      setConstructorInvocation(
        new ExprStmt(
          new SuperConstructorAccess("super", new List())
          )
        );
      return this;
    }    }
}
