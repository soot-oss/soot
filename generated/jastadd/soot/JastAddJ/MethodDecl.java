
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class MethodDecl extends MemberDecl implements Cloneable, SimpleSet, Iterator {
    public void flushCache() {
        super.flushCache();
        accessibleFrom_TypeDecl_values = null;
        throwsException_TypeDecl_values = null;
        signature_computed = false;
        signature_value = null;
        moreSpecificThan_MethodDecl_values = null;
        overrides_MethodDecl_values = null;
        hides_MethodDecl_values = null;
        parameterDeclaration_String_values = null;
        type_computed = false;
        type_value = null;
        usesTypeVariable_computed = false;
        sourceMethodDecl_computed = false;
        sourceMethodDecl_value = null;
        sootMethod_computed = false;
        sootMethod_value = null;
        sootRef_computed = false;
        sootRef_value = null;
        offsetBeforeParameters_computed = false;
        offsetAfterParameters_computed = false;
        handlesException_TypeDecl_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public MethodDecl clone() throws CloneNotSupportedException {
        MethodDecl node = (MethodDecl)super.clone();
        node.accessibleFrom_TypeDecl_values = null;
        node.throwsException_TypeDecl_values = null;
        node.signature_computed = false;
        node.signature_value = null;
        node.moreSpecificThan_MethodDecl_values = null;
        node.overrides_MethodDecl_values = null;
        node.hides_MethodDecl_values = null;
        node.parameterDeclaration_String_values = null;
        node.type_computed = false;
        node.type_value = null;
        node.usesTypeVariable_computed = false;
        node.sourceMethodDecl_computed = false;
        node.sourceMethodDecl_value = null;
        node.sootMethod_computed = false;
        node.sootMethod_value = null;
        node.sootRef_computed = false;
        node.sootRef_value = null;
        node.offsetBeforeParameters_computed = false;
        node.offsetAfterParameters_computed = false;
        node.handlesException_TypeDecl_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public MethodDecl copy() {
      try {
          MethodDecl node = (MethodDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public MethodDecl fullCopy() {
        MethodDecl res = (MethodDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BoundNames.jrag at line 77


  public Access createBoundAccess(List args) {
    if(isStatic()) {
      return hostType().createQualifiedAccess().qualifiesAccess(
        new BoundMethodAccess(name(), args, this)
      );
    }
    return new BoundMethodAccess(name(), args, this);
  }

    // Declared in DataStructures.jrag at line 134

  public SimpleSet add(Object o) {
    return new SimpleSetImpl().add(this).add(o);
  }

    // Declared in DataStructures.jrag at line 140

  private MethodDecl iterElem;

    // Declared in DataStructures.jrag at line 141

  public Iterator iterator() { iterElem = this; return this; }

    // Declared in DataStructures.jrag at line 142

  public boolean hasNext() { return iterElem != null; }

    // Declared in DataStructures.jrag at line 143

  public Object next() { Object o = iterElem; iterElem = null; return o; }

    // Declared in DataStructures.jrag at line 144

  public void remove() { throw new UnsupportedOperationException(); }

    // Declared in Modifiers.jrag at line 127

  
  // 8.4.3
  public void checkModifiers() {
    super.checkModifiers();
    if(hostType().isClassDecl()) {
      // 8.4.3.1
      if(isAbstract() && !hostType().isAbstract())
        error("class must be abstract to include abstract methods");
      // 8.4.3.1
      if(isAbstract() && isPrivate())
        error("method may not be abstract and private");
      // 8.4.3.1
      // 8.4.3.2
      if(isAbstract() && isStatic())
        error("method may not be abstract and static");
      if(isAbstract() && isSynchronized())
        error("method may not be abstract and synchronized");
      // 8.4.3.4
      if(isAbstract() && isNative())
        error("method may not be abstract and native");
      if(isAbstract() && isStrictfp())
        error("method may not be abstract and strictfp");
      if(isNative() && isStrictfp())
        error("method may not be native and strictfp");
    }
    if(hostType().isInterfaceDecl()) {
      // 9.4
      if(isStatic())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be static");
      if(isStrictfp())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be strictfp");
      if(isNative())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be native");
      if(isSynchronized())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be synchronized");
      if(isProtected())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be protected");
      if(isPrivate())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be private");
      else if(isFinal())
        error("interface method " + signature() + " in " +
            hostType().typeName() +  " may not be final");
    }
  }

    // Declared in NameCheck.jrag at line 96


  public void nameCheck() {
    // 8.4
    // 8.4.2
    if(!hostType().methodsSignature(signature()).contains(this))
      error("method with signature " + signature() + " is multiply declared in type " + hostType().typeName());
    // 8.4.3.4
    if(isNative() && hasBlock())
      error("native methods must have an empty semicolon body");
    // 8.4.5
    if(isAbstract() && hasBlock())
      error("abstract methods must have an empty semicolon body");
    // 8.4.5
    if(!hasBlock() && !(isNative() || isAbstract()))
      error("only abstract and native methods may have an empty semicolon body");
  }

    // Declared in PrettyPrint.jadd at line 175


  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);
    getTypeAccess().toString(s);
    s.append(" " + name() + "(");
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
    if(hasBlock()) {
      s.append(" ");
      getBlock().toString(s);
    }
    else {
      s.append(";");
    }
  }

    // Declared in TypeCheck.jrag at line 386


  public void typeCheck() {
    // Thrown vs super class method see MethodDecl.nameCheck
    // 8.4.4
    TypeDecl exceptionType = typeThrowable();
    for(int i = 0; i < getNumException(); i++) {
      TypeDecl typeDecl = getException(i).type();
      if(!typeDecl.instanceOf(exceptionType))
        error(signature() + " throws non throwable type " + typeDecl.fullName());
    }

    // check returns
    if(!isVoid() && hasBlock() && getBlock().canCompleteNormally())
      error("the body of a non void method may not complete normally");

  }

    // Declared in Generics.jrag at line 1001


  public BodyDecl p(Parameterization parTypeDecl) {
    //System.out.println("Begin substituting " + signature() + " in " + hostType().typeName() + " with " + parTypeDecl.typeSignature());
    MethodDecl m = new MethodDeclSubstituted(
      (Modifiers)getModifiers().fullCopy(),
      getTypeAccess().type().substituteReturnType(parTypeDecl),
      getID(),
      getParameterList().substitute(parTypeDecl),
      getExceptionList().substitute(parTypeDecl),
      new Opt(),
      this
    );
    //System.out.println("End substituting " + signature());
    return m;
  }

    // Declared in InnerClasses.jrag at line 196


  public MethodDecl createAccessor(TypeDecl methodQualifier) {
    MethodDecl m = (MethodDecl)methodQualifier.getAccessor(this, "method");
    if(m != null) return m;

    int accessorIndex = methodQualifier.accessorCounter++;
    
    List parameterList = new List();
    for(int i = 0; i < getNumParameter(); i++)
      parameterList.add(new ParameterDeclaration(getParameter(i).type(), getParameter(i).name()));
    List exceptionList = new List();
    for(int i = 0; i < getNumException(); i++)
      exceptionList.add(getException(i).type().createQualifiedAccess());

    // add synthetic flag to modifiers
    Modifiers modifiers = new Modifiers(new List());
    if(getModifiers().isStatic())
      modifiers.addModifier(new Modifier("static"));
    modifiers.addModifier(new Modifier("synthetic"));
    modifiers.addModifier(new Modifier("public"));
    // build accessor declaration
    m = new MethodDecl(
      modifiers,
      type().createQualifiedAccess(),
      name() + "$access$" + accessorIndex,
      parameterList,
      exceptionList,
      new Opt(
        new Block(
          new List().add(
            createAccessorStmt()
          )
        )
      )
    );
    m = methodQualifier.addMemberMethod(m);
    methodQualifier.addAccessor(this, "method", m);
    return m;
  }

    // Declared in InnerClasses.jrag at line 235

  
  private Stmt createAccessorStmt() {
    List argumentList = new List();
    for(int i = 0; i < getNumParameter(); i++)
      argumentList.add(new VarAccess(getParameter(i).name()));
    Access access = new BoundMethodAccess(name(), argumentList, this);
    if(!isStatic())
      access = new ThisAccess("this").qualifiesAccess(access);
    return isVoid() ? (Stmt) new ExprStmt(access) : new ReturnStmt(new Opt(access));
  }

    // Declared in InnerClasses.jrag at line 245


  public MethodDecl createSuperAccessor(TypeDecl methodQualifier) {
    MethodDecl m = (MethodDecl)methodQualifier.getAccessor(this, "method_super");
    if(m != null) return m;

    int accessorIndex = methodQualifier.accessorCounter++;
    List parameters = new List();
    List args = new List();
    for(int i = 0; i < getNumParameter(); i++) {
      parameters.add(new ParameterDeclaration(getParameter(i).type(), getParameter(i).name()));
      args.add(new VarAccess(getParameter(i).name()));
    }
    Stmt stmt;
    if(type().isVoid())
      stmt = new ExprStmt(new SuperAccess("super").qualifiesAccess(new MethodAccess(name(), args)));
    else 
      stmt = new ReturnStmt(new Opt(new SuperAccess("super").qualifiesAccess(new MethodAccess(name(), args))));
    m = new MethodDecl(
      new Modifiers(new List().add(new Modifier("synthetic"))),
      type().createQualifiedAccess(),
      name() + "$access$" + accessorIndex,
      parameters,
      new List(),
      new Opt(
        new Block(
          new List().add(stmt)
        )
      )
    );
    m = methodQualifier.addMemberMethod(m);
    methodQualifier.addAccessor(this, "method_super", m);
    return m;
  }

    // Declared in EmitJimple.jrag at line 210


  public void jimplify1phase2() {
    String name = name();
    ArrayList parameters = new ArrayList();
    ArrayList paramnames = new ArrayList();
    for(int i = 0; i < getNumParameter(); i++) {
      parameters.add(getParameter(i).type().getSootType());
      paramnames.add(getParameter(i).name());
    }
    soot.Type returnType = type().getSootType();
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

    // Declared in EmitJimple.jrag at line 268


  public SootMethod sootMethod;

    // Declared in AnnotationsCodegen.jrag at line 43

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

    // Declared in AnnotationsCodegen.jrag at line 163


  // 4.8.17
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

    // Declared in AnnotationsCodegen.jrag at line 219


  // 4.8.18
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

    // Declared in AnnotationsCodegen.jrag at line 274


  public void addSourceLevelParameterAnnotationsAttribute(Collection c) {
    boolean foundVisibleAnnotations = false;
    Collection annotations = new ArrayList(getNumParameter());
    for(int i = 0; i < getNumParameter(); i++) {
      getParameter(i).getModifiers().addSourceOnlyAnnotations(c);
    }
  }

    // Declared in GenericsCodegen.jrag at line 342

  
  public void transformation() {
    super.transformation();
    HashSet processed = new HashSet();
    for(Iterator iter = hostType().bridgeCandidates(signature()).iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(this.overrides(m)) {
        MethodDecl erased = m.erasedMethod();
        if(!erased.signature().equals(signature()) || erased.type().erasure() != type().erasure()) {
          StringBuffer keyBuffer = new StringBuffer();
          for(int i = 0; i < getNumParameter(); i++) {
            keyBuffer.append(erased.getParameter(i).type().erasure().fullName());
          }
          keyBuffer.append(erased.type().erasure().fullName());
          String key = keyBuffer.toString();
          if(!processed.contains(key)) {
            processed.add(key);

            List args = new List();
            List parameters = new List();
            for(int i = 0; i < getNumParameter(); i++) {
              args.add(new CastExpr(getParameter(i).type().erasure().createBoundAccess(), new VarAccess("p" + i)));
              parameters.add(new ParameterDeclaration(erased.getParameter(i).type().erasure(), "p" + i));
            }
            Stmt stmt;
            if(type().isVoid()) {
              stmt = new ExprStmt(
                  createBoundAccess(
                    args
                    )
                  );
            }
            else {
              stmt = new ReturnStmt(
                  createBoundAccess(
                    args
                    )
                  );
            }
            List modifiersList = new List();
            if(isPublic())
              modifiersList.add(new Modifier("public"));
            else if(isProtected())
              modifiersList.add(new Modifier("protected"));
            else if(isPrivate())
              modifiersList.add(new Modifier("private"));
            MethodDecl bridge = new BridgeMethodDecl(
                new Modifiers(modifiersList),
                erased.type().erasure().createBoundAccess(),
                erased.name(),
                parameters,
                (List)getExceptionList().fullCopy(),
                new Opt(
                  new Block(
                    new List().add(stmt)
                    )
                  )
                );
            hostType().addBodyDecl(bridge);
          }
        }
      }
    }
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 88

    public MethodDecl() {
        super();

        setChild(new List(), 2);
        setChild(new List(), 3);
        setChild(new Opt(), 4);

    }

    // Declared in java.ast at line 13


    // Declared in java.ast line 88
    public MethodDecl(Modifiers p0, Access p1, String p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
    }

    // Declared in java.ast at line 23


    // Declared in java.ast line 88
    public MethodDecl(Modifiers p0, Access p1, beaver.Symbol p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
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
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 88
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
    // Declared in java.ast line 88
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
    // Declared in java.ast line 88
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
    // Declared in java.ast line 88
    public void setParameterList(List<ParameterDeclaration> list) {
        setChild(list, 2);
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
        List<ParameterDeclaration> list = (List<ParameterDeclaration>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<ParameterDeclaration> getParameterListNoTransform() {
        return (List<ParameterDeclaration>)getChildNoTransform(2);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 88
    public void setExceptionList(List<Access> list) {
        setChild(list, 3);
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
        List<Access> list = (List<Access>)getChild(3);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getExceptionListNoTransform() {
        return (List<Access>)getChildNoTransform(3);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 88
    public void setBlockOpt(Opt<Block> opt) {
        setChild(opt, 4);
    }

    // Declared in java.ast at line 6


    public boolean hasBlock() {
        return getBlockOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Block getBlock() {
        return (Block)getBlockOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setBlock(Block node) {
        getBlockOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Block> getBlockOpt() {
        return (Opt<Block>)getChild(4);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Block> getBlockOptNoTransform() {
        return (Opt<Block>)getChildNoTransform(4);
    }

    // Declared in EmitJimpleRefinements.jrag at line 100




    public void jimplify2() {
    if(!generate() || sootMethod().hasActiveBody()  ||
      (sootMethod().getSource() != null && (sootMethod().getSource() instanceof soot.coffi.CoffiMethodSource)) ) return;
    try {
      if(hasBlock() && !(hostType().isInterfaceDecl())) {
        JimpleBody body = Jimple.v().newBody(sootMethod());
        sootMethod().setActiveBody(body);
        Body b = new Body(hostType(), body, this);
        b.setLine(this);
        for(int i = 0; i < getNumParameter(); i++)
          getParameter(i).jimplify2(b);
        getBlock().jimplify2(b);
        if(type() instanceof VoidType)
          b.add(Jimple.v().newReturnVoidStmt());
      }
    } catch (RuntimeException e) {
      System.err.println("Error generating " + hostType().typeName() + ": " + this); 
      throw e;
    }
  }

    // Declared in LookupMethod.jrag at line 142
private boolean refined_MethodDecl_MethodDecl_moreSpecificThan_MethodDecl(MethodDecl m)
{
    if(getNumParameter() == 0)
      return false;
    for(int i = 0; i < getNumParameter(); i++) {
      if(!getParameter(i).type().instanceOf(m.getParameter(i).type()))
        return false;
    }
    return true;
  }

    // Declared in EmitJimple.jrag at line 107
private int refined_EmitJimple_MethodDecl_sootTypeModifiers()
{
    int result = 0;
    if(isPublic()) result |= soot.Modifier.PUBLIC;
    if(isProtected()) result |= soot.Modifier.PROTECTED;
    if(isPrivate()) result |= soot.Modifier.PRIVATE;
    if(isFinal()) result |= soot.Modifier.FINAL;
    if(isStatic()) result |= soot.Modifier.STATIC;
    if(isAbstract()) result |= soot.Modifier.ABSTRACT;
    if(isSynchronized()) result |= soot.Modifier.SYNCHRONIZED;
    if(isStrictfp()) result |= soot.Modifier.STRICTFP;
    if(isNative()) result |= soot.Modifier.NATIVE;
    return result;
  }

    protected java.util.Map accessibleFrom_TypeDecl_values;
    // Declared in AccessControl.jrag at line 77
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
    if(isPublic()) {
      return true;
    }
    else if(isProtected()) {
      if(hostPackage().equals(type.hostPackage()))
        return true;
      if(type.withinBodyThatSubclasses(hostType()) != null)
        return true;
      return false;
    }
    else if(isPrivate())
      return hostType().topLevelType() == type.topLevelType();
    else
      return hostPackage().equals(type.hostPackage());
  }

    // Declared in DataStructures.jrag at line 132
 @SuppressWarnings({"unchecked", "cast"})     public int size() {
        ASTNode$State state = state();
        int size_value = size_compute();
        return size_value;
    }

    private int size_compute() {  return 1;  }

    // Declared in DataStructures.jrag at line 133
 @SuppressWarnings({"unchecked", "cast"})     public boolean isEmpty() {
        ASTNode$State state = state();
        boolean isEmpty_value = isEmpty_compute();
        return isEmpty_value;
    }

    private boolean isEmpty_compute() {  return false;  }

    // Declared in DataStructures.jrag at line 137
 @SuppressWarnings({"unchecked", "cast"})     public boolean contains(Object o) {
        ASTNode$State state = state();
        boolean contains_Object_value = contains_compute(o);
        return contains_Object_value;
    }

    private boolean contains_compute(Object o) {  return this == o;  }

    // Declared in ErrorCheck.jrag at line 31
 @SuppressWarnings({"unchecked", "cast"})     public int lineNumber() {
        ASTNode$State state = state();
        int lineNumber_value = lineNumber_compute();
        return lineNumber_value;
    }

    private int lineNumber_compute() {  return getLine(IDstart);  }

    protected java.util.Map throwsException_TypeDecl_values;
    // Declared in ExceptionHandling.jrag at line 123
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

    // Declared in LookupMethod.jrag at line 125
 @SuppressWarnings({"unchecked", "cast"})     public String name() {
        ASTNode$State state = state();
        String name_value = name_compute();
        return name_value;
    }

    private String name_compute() {  return getID();  }

    protected boolean signature_computed = false;
    protected String signature_value;
    // Declared in MethodSignature.jrag at line 332
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
      if(i != 0) s.append(", ");
      s.append(getParameter(i).type().erasure().typeName());
    }
    s.append(")");
    return s.toString();

  }

    // Declared in LookupMethod.jrag at line 140
 @SuppressWarnings({"unchecked", "cast"})     public boolean sameSignature(MethodDecl m) {
        ASTNode$State state = state();
        boolean sameSignature_MethodDecl_value = sameSignature_compute(m);
        return sameSignature_MethodDecl_value;
    }

    private boolean sameSignature_compute(MethodDecl m) {  return signature().equals(m.signature());  }

    protected java.util.Map moreSpecificThan_MethodDecl_values;
    // Declared in MethodSignature.jrag at line 140
 @SuppressWarnings({"unchecked", "cast"})     public boolean moreSpecificThan(MethodDecl m) {
        Object _parameters = m;
if(moreSpecificThan_MethodDecl_values == null) moreSpecificThan_MethodDecl_values = new java.util.HashMap(4);
        if(moreSpecificThan_MethodDecl_values.containsKey(_parameters)) {
            return ((Boolean)moreSpecificThan_MethodDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean moreSpecificThan_MethodDecl_value = moreSpecificThan_compute(m);
        if(isFinal && num == state().boundariesCrossed)
            moreSpecificThan_MethodDecl_values.put(_parameters, Boolean.valueOf(moreSpecificThan_MethodDecl_value));
        return moreSpecificThan_MethodDecl_value;
    }

    private boolean moreSpecificThan_compute(MethodDecl m) {
    if(!isVariableArity() && !m.isVariableArity())
      return refined_MethodDecl_MethodDecl_moreSpecificThan_MethodDecl(m);
    int num = Math.max(getNumParameter(), m.getNumParameter());
    for(int i = 0; i < num; i++) {
      TypeDecl t1 = i < getNumParameter() - 1 ? getParameter(i).type() : getParameter(getNumParameter()-1).type().componentType();
      TypeDecl t2 = i < m.getNumParameter() - 1 ? m.getParameter(i).type() : m.getParameter(m.getNumParameter()-1).type().componentType();
      if(!t1.instanceOf(t2))
        return false;
    }
    return true;
  }

    protected java.util.Map overrides_MethodDecl_values;
    // Declared in LookupMethod.jrag at line 183
 @SuppressWarnings({"unchecked", "cast"})     public boolean overrides(MethodDecl m) {
        Object _parameters = m;
if(overrides_MethodDecl_values == null) overrides_MethodDecl_values = new java.util.HashMap(4);
        if(overrides_MethodDecl_values.containsKey(_parameters)) {
            return ((Boolean)overrides_MethodDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean overrides_MethodDecl_value = overrides_compute(m);
        if(isFinal && num == state().boundariesCrossed)
            overrides_MethodDecl_values.put(_parameters, Boolean.valueOf(overrides_MethodDecl_value));
        return overrides_MethodDecl_value;
    }

    private boolean overrides_compute(MethodDecl m) {  return !isStatic() && !m.isPrivate() && m.accessibleFrom(hostType()) && 
     hostType().instanceOf(m.hostType()) && m.signature().equals(signature());  }

    protected java.util.Map hides_MethodDecl_values;
    // Declared in LookupMethod.jrag at line 187
 @SuppressWarnings({"unchecked", "cast"})     public boolean hides(MethodDecl m) {
        Object _parameters = m;
if(hides_MethodDecl_values == null) hides_MethodDecl_values = new java.util.HashMap(4);
        if(hides_MethodDecl_values.containsKey(_parameters)) {
            return ((Boolean)hides_MethodDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean hides_MethodDecl_value = hides_compute(m);
        if(isFinal && num == state().boundariesCrossed)
            hides_MethodDecl_values.put(_parameters, Boolean.valueOf(hides_MethodDecl_value));
        return hides_MethodDecl_value;
    }

    private boolean hides_compute(MethodDecl m) {  return isStatic() && !m.isPrivate() && m.accessibleFrom(hostType()) && 
     hostType().instanceOf(m.hostType()) && m.signature().equals(signature());  }

    protected java.util.Map parameterDeclaration_String_values;
    // Declared in LookupVariable.jrag at line 99
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

    // Declared in Modifiers.jrag at line 213
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSynthetic() {
        ASTNode$State state = state();
        boolean isSynthetic_value = isSynthetic_compute();
        return isSynthetic_value;
    }

    private boolean isSynthetic_compute() {  return getModifiers().isSynthetic();  }

    // Declared in Modifiers.jrag at line 222
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPublic() {
        ASTNode$State state = state();
        boolean isPublic_value = isPublic_compute();
        return isPublic_value;
    }

    private boolean isPublic_compute() {  return getModifiers().isPublic() || hostType().isInterfaceDecl();  }

    // Declared in Modifiers.jrag at line 223
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPrivate() {
        ASTNode$State state = state();
        boolean isPrivate_value = isPrivate_compute();
        return isPrivate_value;
    }

    private boolean isPrivate_compute() {  return getModifiers().isPrivate();  }

    // Declared in Modifiers.jrag at line 224
 @SuppressWarnings({"unchecked", "cast"})     public boolean isProtected() {
        ASTNode$State state = state();
        boolean isProtected_value = isProtected_compute();
        return isProtected_value;
    }

    private boolean isProtected_compute() {  return getModifiers().isProtected();  }

    // Declared in Modifiers.jrag at line 225
 @SuppressWarnings({"unchecked", "cast"})     public boolean isAbstract() {
        ASTNode$State state = state();
        boolean isAbstract_value = isAbstract_compute();
        return isAbstract_value;
    }

    private boolean isAbstract_compute() {  return getModifiers().isAbstract() || hostType().isInterfaceDecl();  }

    // Declared in Modifiers.jrag at line 226
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStatic() {
        ASTNode$State state = state();
        boolean isStatic_value = isStatic_compute();
        return isStatic_value;
    }

    private boolean isStatic_compute() {  return getModifiers().isStatic();  }

    // Declared in Modifiers.jrag at line 228
 @SuppressWarnings({"unchecked", "cast"})     public boolean isFinal() {
        ASTNode$State state = state();
        boolean isFinal_value = isFinal_compute();
        return isFinal_value;
    }

    private boolean isFinal_compute() {  return getModifiers().isFinal() || hostType().isFinal() || isPrivate();  }

    // Declared in Modifiers.jrag at line 229
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSynchronized() {
        ASTNode$State state = state();
        boolean isSynchronized_value = isSynchronized_compute();
        return isSynchronized_value;
    }

    private boolean isSynchronized_compute() {  return getModifiers().isSynchronized();  }

    // Declared in Modifiers.jrag at line 230
 @SuppressWarnings({"unchecked", "cast"})     public boolean isNative() {
        ASTNode$State state = state();
        boolean isNative_value = isNative_compute();
        return isNative_value;
    }

    private boolean isNative_compute() {  return getModifiers().isNative();  }

    // Declared in Modifiers.jrag at line 231
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStrictfp() {
        ASTNode$State state = state();
        boolean isStrictfp_value = isStrictfp_compute();
        return isStrictfp_value;
    }

    private boolean isStrictfp_compute() {  return getModifiers().isStrictfp();  }

    // Declared in PrettyPrint.jadd at line 813
 @SuppressWarnings({"unchecked", "cast"})     public String dumpString() {
        ASTNode$State state = state();
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return getClass().getName() + " [" + getID() + "]";  }

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in TypeAnalysis.jrag at line 269
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

    // Declared in TypeAnalysis.jrag at line 272
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVoid() {
        ASTNode$State state = state();
        boolean isVoid_value = isVoid_compute();
        return isVoid_value;
    }

    private boolean isVoid_compute() {  return type().isVoid();  }

    // Declared in GenericMethods.jrag at line 84
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayOverrideReturn(MethodDecl m) {
        ASTNode$State state = state();
        boolean mayOverrideReturn_MethodDecl_value = mayOverrideReturn_compute(m);
        return mayOverrideReturn_MethodDecl_value;
    }

    private boolean mayOverrideReturn_compute(MethodDecl m) {
    return type().instanceOf(m.type());
  }

    // Declared in Annotations.jrag at line 139
 @SuppressWarnings({"unchecked", "cast"})     public boolean annotationMethodOverride() {
        ASTNode$State state = state();
        boolean annotationMethodOverride_value = annotationMethodOverride_compute();
        return annotationMethodOverride_value;
    }

    private boolean annotationMethodOverride_compute() {  return !hostType().ancestorMethods(signature()).isEmpty();  }

    // Declared in Annotations.jrag at line 285
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasAnnotationSuppressWarnings(String s) {
        ASTNode$State state = state();
        boolean hasAnnotationSuppressWarnings_String_value = hasAnnotationSuppressWarnings_compute(s);
        return hasAnnotationSuppressWarnings_String_value;
    }

    private boolean hasAnnotationSuppressWarnings_compute(String s) {  return getModifiers().hasAnnotationSuppressWarnings(s);  }

    // Declared in Annotations.jrag at line 323
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDeprecated() {
        ASTNode$State state = state();
        boolean isDeprecated_value = isDeprecated_compute();
        return isDeprecated_value;
    }

    private boolean isDeprecated_compute() {  return getModifiers().hasDeprecatedAnnotation();  }

    protected boolean usesTypeVariable_computed = false;
    protected boolean usesTypeVariable_value;
    // Declared in Generics.jrag at line 903
 @SuppressWarnings({"unchecked", "cast"})     public boolean usesTypeVariable() {
        if(usesTypeVariable_computed) {
            return usesTypeVariable_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        usesTypeVariable_value = usesTypeVariable_compute();
        if(isFinal && num == state().boundariesCrossed)
            usesTypeVariable_computed = true;
        return usesTypeVariable_value;
    }

    private boolean usesTypeVariable_compute() {  return getModifiers().usesTypeVariable() || getTypeAccess().usesTypeVariable() ||
    getParameterList().usesTypeVariable() || getExceptionList().usesTypeVariable();  }

    protected boolean sourceMethodDecl_computed = false;
    protected MethodDecl sourceMethodDecl_value;
    // Declared in Generics.jrag at line 1265
 @SuppressWarnings({"unchecked", "cast"})     public MethodDecl sourceMethodDecl() {
        if(sourceMethodDecl_computed) {
            return sourceMethodDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sourceMethodDecl_value = sourceMethodDecl_compute();
        if(isFinal && num == state().boundariesCrossed)
            sourceMethodDecl_computed = true;
        return sourceMethodDecl_value;
    }

    private MethodDecl sourceMethodDecl_compute() {  return this;  }

    // Declared in GenericsParTypeDecl.jrag at line 65
 @SuppressWarnings({"unchecked", "cast"})     public boolean visibleTypeParameters() {
        ASTNode$State state = state();
        boolean visibleTypeParameters_value = visibleTypeParameters_compute();
        return visibleTypeParameters_value;
    }

    private boolean visibleTypeParameters_compute() {  return !isStatic();  }

    // Declared in MethodSignature.jrag at line 269
 @SuppressWarnings({"unchecked", "cast"})     public int arity() {
        ASTNode$State state = state();
        int arity_value = arity_compute();
        return arity_value;
    }

    private int arity_compute() {  return getNumParameter();  }

    // Declared in VariableArityParameters.jrag at line 33
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVariableArity() {
        ASTNode$State state = state();
        boolean isVariableArity_value = isVariableArity_compute();
        return isVariableArity_value;
    }

    private boolean isVariableArity_compute() {  return getNumParameter() == 0 ? false : getParameter(getNumParameter()-1).isVariableArity();  }

    // Declared in VariableArityParameters.jrag at line 38
 @SuppressWarnings({"unchecked", "cast"})     public ParameterDeclaration lastParameter() {
        ASTNode$State state = state();
        ParameterDeclaration lastParameter_value = lastParameter_compute();
        return lastParameter_value;
    }

    private ParameterDeclaration lastParameter_compute() {  return getParameter(getNumParameter() - 1);  }

    // Declared in VariableArityParametersCodegen.jrag at line 80
 @SuppressWarnings({"unchecked", "cast"})     public int sootTypeModifiers() {
        ASTNode$State state = state();
        int sootTypeModifiers_value = sootTypeModifiers_compute();
        return sootTypeModifiers_value;
    }

    private int sootTypeModifiers_compute() {
    int res = refined_EmitJimple_MethodDecl_sootTypeModifiers();
    if(isVariableArity())
      res |= Modifiers.ACC_VARARGS;
    return res;
  }

    protected boolean sootMethod_computed = false;
    protected SootMethod sootMethod_value;
    // Declared in EmitJimple.jrag at line 269
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
    for(int i = 0; i < getNumParameter(); i++)
      list.add(getParameter(i).type().getSootType());
    if(hostType().isArrayDecl())
      return typeObject().getSootClassDecl().getMethod(name(), list, type().getSootType());
    return hostType().getSootClassDecl().getMethod(name(), list, type().getSootType());
  }

    protected boolean sootRef_computed = false;
    protected SootMethodRef sootRef_value;
    // Declared in EmitJimple.jrag at line 279
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
    for(int i = 0; i < getNumParameter(); i++)
      parameters.add(getParameter(i).type().getSootType());
    SootMethodRef ref = Scene.v().makeMethodRef(
      hostType().getSootClassDecl(),
      name(),
      parameters,
      type().getSootType(),
      isStatic()
    );
    return ref;
  }

    protected boolean offsetBeforeParameters_computed = false;
    protected int offsetBeforeParameters_value;
    // Declared in LocalNum.jrag at line 17
 @SuppressWarnings({"unchecked", "cast"})     public int offsetBeforeParameters() {
        if(offsetBeforeParameters_computed) {
            return offsetBeforeParameters_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        offsetBeforeParameters_value = offsetBeforeParameters_compute();
        if(isFinal && num == state().boundariesCrossed)
            offsetBeforeParameters_computed = true;
        return offsetBeforeParameters_value;
    }

    private int offsetBeforeParameters_compute() {  return 0;  }

    protected boolean offsetAfterParameters_computed = false;
    protected int offsetAfterParameters_value;
    // Declared in LocalNum.jrag at line 19
 @SuppressWarnings({"unchecked", "cast"})     public int offsetAfterParameters() {
        if(offsetAfterParameters_computed) {
            return offsetAfterParameters_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        offsetAfterParameters_value = offsetAfterParameters_compute();
        if(isFinal && num == state().boundariesCrossed)
            offsetAfterParameters_computed = true;
        return offsetAfterParameters_value;
    }

    private int offsetAfterParameters_compute() {
    if(getNumParameter() == 0)
      return offsetBeforeParameters();
    return getParameter(getNumParameter()-1).localNum() + 
           getParameter(getNumParameter()-1).type().variableSize();
  }

    // Declared in GenericsCodegen.jrag at line 34
 @SuppressWarnings({"unchecked", "cast"})     public MethodDecl erasedMethod() {
        ASTNode$State state = state();
        MethodDecl erasedMethod_value = erasedMethod_compute();
        return erasedMethod_value;
    }

    private MethodDecl erasedMethod_compute() {  return this;  }

    protected java.util.Map handlesException_TypeDecl_values;
    // Declared in ExceptionHandling.jrag at line 37
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

    // Declared in LookupMethod.jrag at line 14
 @SuppressWarnings({"unchecked", "cast"})     public MethodDecl unknownMethod() {
        ASTNode$State state = state();
        MethodDecl unknownMethod_value = getParent().Define_MethodDecl_unknownMethod(this, null);
        return unknownMethod_value;
    }

    // Declared in EmitJimple.jrag at line 277
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeObject() {
        ASTNode$State state = state();
        TypeDecl typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
        return typeObject_value;
    }

    // Declared in DefiniteAssignment.jrag at line 438
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockOptNoTransform()) {
            return v.isFinal() && (v.isClassVariable() || v.isInstanceVariable()) ? true : isDAbefore(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 872
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockOptNoTransform()) {
            return v.isFinal() && (v.isClassVariable() || v.isInstanceVariable()) ? false : true;
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in ExceptionHandling.jrag at line 120
    public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
        if(caller == getBlockOptNoTransform()) {
            return throwsException(exceptionType) || handlesException(exceptionType);
        }
        return getParent().Define_boolean_handlesException(this, caller, exceptionType);
    }

    // Declared in LookupVariable.jrag at line 46
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return parameterDeclaration(name);
        }
        if(caller == getBlockOptNoTransform()){
    SimpleSet set = parameterDeclaration(name);
    // A declaration of a method parameter name shadows any other variable declarations
    if(!set.isEmpty()) return set;
    // Delegate to other declarations in scope
    return lookupVariable(name);
  }
        return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }

    // Declared in Modifiers.jrag at line 269
    public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBePublic(this, caller);
    }

    // Declared in Modifiers.jrag at line 270
    public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeProtected(this, caller);
    }

    // Declared in Modifiers.jrag at line 271
    public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBePrivate(this, caller);
    }

    // Declared in Modifiers.jrag at line 272
    public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeAbstract(this, caller);
    }

    // Declared in Modifiers.jrag at line 273
    public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeStatic(this, caller);
    }

    // Declared in Modifiers.jrag at line 274
    public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeFinal(this, caller);
    }

    // Declared in Modifiers.jrag at line 275
    public boolean Define_boolean_mayBeSynchronized(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeSynchronized(this, caller);
    }

    // Declared in Modifiers.jrag at line 276
    public boolean Define_boolean_mayBeNative(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeNative(this, caller);
    }

    // Declared in Modifiers.jrag at line 277
    public boolean Define_boolean_mayBeStrictfp(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeStrictfp(this, caller);
    }

    // Declared in NameCheck.jrag at line 241
    public ASTNode Define_ASTNode_enclosingBlock(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return this;
        }
        return getParent().Define_ASTNode_enclosingBlock(this, caller);
    }

    // Declared in SyntacticClassification.jrag at line 82
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getExceptionListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        if(caller == getTypeAccessNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

    // Declared in TypeCheck.jrag at line 405
    public TypeDecl Define_TypeDecl_returnType(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return type();
        }
        return getParent().Define_TypeDecl_returnType(this, caller);
    }

    // Declared in TypeHierarchyCheck.jrag at line 142
    public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return isStatic();
        }
        return getParent().Define_boolean_inStaticContext(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 33
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 80
    public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return true;
        }
        return getParent().Define_boolean_isMethodParameter(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 81
    public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return false;
        }
        return getParent().Define_boolean_isConstructorParameter(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 82
    public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return false;
        }
        return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
    }

    // Declared in Annotations.jrag at line 86
    public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
        if(caller == getModifiersNoTransform()) {
            return name.equals("METHOD");
        }
        return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
    }

    // Declared in VariableArityParameters.jrag at line 22
    public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) {
      int i = caller.getIndexOfChild(child);
            return i == getNumParameter() - 1;
        }
        return getParent().Define_boolean_variableArityValid(this, caller);
    }

    // Declared in LocalNum.jrag at line 26
    public int Define_int_localNum(ASTNode caller, ASTNode child) {
        if(caller == getParameterListNoTransform()) { 
   int index = caller.getIndexOfChild(child);
{
    if(index == 0)
      return offsetBeforeParameters();
    return getParameter(index-1).localNum() + getParameter(index-1).type().variableSize();
  }
}
        return getParent().Define_int_localNum(this, caller);
    }

    // Declared in Statements.jrag at line 350
    public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
        if(caller == getBlockOptNoTransform()) {
            return getNumException() != 0;
        }
        return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
