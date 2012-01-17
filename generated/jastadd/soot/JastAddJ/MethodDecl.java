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
 * @declaredat java.ast:92
 */
public class MethodDecl extends MemberDecl implements Cloneable, SimpleSet, Iterator {
  /**
   * @apilevel low-level
   */
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
  public MethodDecl clone() throws CloneNotSupportedException {
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
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl copy() {
      try {
        MethodDecl node = (MethodDecl)clone();
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
  public MethodDecl fullCopy() {
    MethodDecl res = (MethodDecl)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:77
   */
  public Access createBoundAccess(List args) {
    if(isStatic()) {
      return hostType().createQualifiedAccess().qualifiesAccess(
        new BoundMethodAccess(name(), args, this)
      );
    }
    return new BoundMethodAccess(name(), args, this);
  }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:154
   */
  public SimpleSet add(Object o) {
    return new SimpleSetImpl().add(this).add(o);
  }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:158
   */
  public boolean isSingleton() { return true; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:159
   */
  public boolean isSingleton(Object o) { return contains(o); }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:162
   */
  
  private MethodDecl iterElem;
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:163
   */
  public Iterator iterator() { iterElem = this; return this; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:164
   */
  public boolean hasNext() { return iterElem != null; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:165
   */
  public Object next() { Object o = iterElem; iterElem = null; return o; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:166
   */
  public void remove() { throw new UnsupportedOperationException(); }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:96
   */
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
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:176
   */
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
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:386
   */
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
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1005
   */
  public BodyDecl p(Parameterization parTypeDecl) {
    //System.out.println("Begin substituting " + signature() + " in " + hostType().typeName() + " with " + parTypeDecl.typeSignature());
    MethodDecl m = new MethodDeclSubstituted(
      (Modifiers)getModifiers().fullCopy(),
      getTypeAccess().type().substituteReturnType(parTypeDecl),
      getID(),
      getParameterList().substitute(parTypeDecl),
      getExceptionList().substitute(parTypeDecl),
      substituteBody(parTypeDecl),
      this
    );
    //System.out.println("End substituting " + signature());
    return m;
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1020
   */
  public Opt substituteBody(Parameterization parTypeDecl) {
    return new Opt();
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:196
   */
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
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:235
   */
  private Stmt createAccessorStmt() {
    List argumentList = new List();
    for(int i = 0; i < getNumParameter(); i++)
      argumentList.add(new VarAccess(getParameter(i).name()));
    Access access = new BoundMethodAccess(name(), argumentList, this);
    if(!isStatic())
      access = new ThisAccess("this").qualifiesAccess(access);
    return isVoid() ? (Stmt) new ExprStmt(access) : new ReturnStmt(new Opt(access));
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:245
   */
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
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:210
   */
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
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:268
   */
  

  public SootMethod sootMethod;
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:43
   */
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
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:163
   */
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
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:219
   */
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
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:274
   */
  public void addSourceLevelParameterAnnotationsAttribute(Collection c) {
    boolean foundVisibleAnnotations = false;
    Collection annotations = new ArrayList(getNumParameter());
    for(int i = 0; i < getNumParameter(); i++) {
      getParameter(i).getModifiers().addSourceOnlyAnnotations(c);
    }
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:342
   */
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
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public MethodDecl() {
    super();

    setChild(new List(), 2);
    setChild(new List(), 3);
    setChild(new Opt(), 4);

  }
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  public MethodDecl(Modifiers p0, Access p1, String p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
    setChild(p3, 2);
    setChild(p4, 3);
    setChild(p5, 4);
  }
  /**
   * @ast method 
   * @declaredat java.ast:18
   */
  public MethodDecl(Modifiers p0, Access p1, beaver.Symbol p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
    setChild(p3, 2);
    setChild(p4, 3);
    setChild(p5, 4);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:29
   */
  protected int numChildren() {
    return 5;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:35
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Getter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Setter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setTypeAccess(Access node) {
    setChild(node, 1);
  }
  /**
   * Getter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Access getTypeAccess() {
    return (Access)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(1);
  }
  /**
   * Setter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**   * @apilevel internal   * @ast method 
   * @declaredat java.ast:8
   */
  
  /**   * @apilevel internal   */  protected String tokenString_ID;
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  
  public int IDstart;
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  
  public int IDend;
  /**
   * @ast method 
   * @declaredat java.ast:11
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Getter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:22
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setParameterList(List<ParameterDeclaration> list) {
    setChild(list, 2);
  }
  /**
   * @return number of children in ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumParameter() {
    return getParameterList().getNumChild();
  }
  /**
   * Getter for child in list ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParameterDeclaration getParameter(int i) {
    return (ParameterDeclaration)getParameterList().getChild(i);
  }
  /**
   * Add element to list ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addParameter(ParameterDeclaration node) {
    List<ParameterDeclaration> list = (parent == null || state == null) ? getParameterListNoTransform() : getParameterList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addParameterNoTransform(ParameterDeclaration node) {
    List<ParameterDeclaration> list = getParameterListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setParameter(ParameterDeclaration node, int i) {
    List<ParameterDeclaration> list = getParameterList();
    list.setChild(node, i);
  }
  /**
   * Getter for Parameter list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<ParameterDeclaration> getParameters() {
    return getParameterList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<ParameterDeclaration> getParametersNoTransform() {
    return getParameterListNoTransform();
  }
  /**
   * Getter for list ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ParameterDeclaration> getParameterList() {
    List<ParameterDeclaration> list = (List<ParameterDeclaration>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ParameterDeclaration> getParameterListNoTransform() {
    return (List<ParameterDeclaration>)getChildNoTransform(2);
  }
  /**
   * Setter for ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setExceptionList(List<Access> list) {
    setChild(list, 3);
  }
  /**
   * @return number of children in ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumException() {
    return getExceptionList().getNumChild();
  }
  /**
   * Getter for child in list ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getException(int i) {
    return (Access)getExceptionList().getChild(i);
  }
  /**
   * Add element to list ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addException(Access node) {
    List<Access> list = (parent == null || state == null) ? getExceptionListNoTransform() : getExceptionList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addExceptionNoTransform(Access node) {
    List<Access> list = getExceptionListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setException(Access node, int i) {
    List<Access> list = getExceptionList();
    list.setChild(node, i);
  }
  /**
   * Getter for Exception list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<Access> getExceptions() {
    return getExceptionList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<Access> getExceptionsNoTransform() {
    return getExceptionListNoTransform();
  }
  /**
   * Getter for list ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getExceptionList() {
    List<Access> list = (List<Access>)getChild(3);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getExceptionListNoTransform() {
    return (List<Access>)getChildNoTransform(3);
  }
  /**
   * Setter for BlockOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setBlockOpt(Opt<Block> opt) {
    setChild(opt, 4);
  }
  /**
   * Does this node have a Block child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasBlock() {
    return getBlockOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child Block
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Block getBlock() {
    return (Block)getBlockOpt().getChild(0);
  }
  /**
   * Setter for optional child Block
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setBlock(Block node) {
    getBlockOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getBlockOpt() {
    return (Opt<Block>)getChild(4);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getBlockOptNoTransform() {
    return (Opt<Block>)getChildNoTransform(4);
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:730
   */
   
  public void checkModifiers() {
    super.checkModifiers();
    if(hostType().isClassDecl()) {
      // 8.4.3.1
      if(!hostType().isEnumDecl() && isAbstract() && !hostType().isAbstract())
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
  /**
   * @ast method 
   * @aspect EmitJimpleRefinements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:100
   */
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
  /**
   * @ast method 
   * @aspect MethodDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:148
   */
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
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:107
   */
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
  /**
   * @attribute syn
   * @aspect AccessControl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:77
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean accessibleFrom(TypeDecl type) {
    Object _parameters = type;
    if(accessibleFrom_TypeDecl_values == null) accessibleFrom_TypeDecl_values = new java.util.HashMap(4);
    if(accessibleFrom_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)accessibleFrom_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean accessibleFrom_TypeDecl_value = accessibleFrom_compute(type);
if(isFinal && num == state().boundariesCrossed) accessibleFrom_TypeDecl_values.put(_parameters, Boolean.valueOf(accessibleFrom_TypeDecl_value));
    return accessibleFrom_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @attribute syn
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:152
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int size() {
      ASTNode$State state = state();
    int size_value = size_compute();
    return size_value;
  }
  /**
   * @apilevel internal
   */
  private int size_compute() {  return 1;  }
  /**
   * @attribute syn
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:153
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isEmpty() {
      ASTNode$State state = state();
    boolean isEmpty_value = isEmpty_compute();
    return isEmpty_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isEmpty_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:157
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean contains(Object o) {
      ASTNode$State state = state();
    boolean contains_Object_value = contains_compute(o);
    return contains_Object_value;
  }
  /**
   * @apilevel internal
   */
  private boolean contains_compute(Object o) {  return this == o;  }
  /**
   * @attribute syn
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:31
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int lineNumber() {
      ASTNode$State state = state();
    int lineNumber_value = lineNumber_compute();
    return lineNumber_value;
  }
  /**
   * @apilevel internal
   */
  private int lineNumber_compute() {  return getLine(IDstart);  }
  protected java.util.Map throwsException_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:132
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean throwsException(TypeDecl exceptionType) {
    Object _parameters = exceptionType;
    if(throwsException_TypeDecl_values == null) throwsException_TypeDecl_values = new java.util.HashMap(4);
    if(throwsException_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)throwsException_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean throwsException_TypeDecl_value = throwsException_compute(exceptionType);
if(isFinal && num == state().boundariesCrossed) throwsException_TypeDecl_values.put(_parameters, Boolean.valueOf(throwsException_TypeDecl_value));
    return throwsException_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean throwsException_compute(TypeDecl exceptionType) {
    for(int i = 0; i < getNumException(); i++)
      if(exceptionType.instanceOf(getException(i).type()))
        return true;
    return false;
  }
  /**
   * @attribute syn
   * @aspect MethodDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:131
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String name() {
      ASTNode$State state = state();
    String name_value = name_compute();
    return name_value;
  }
  /**
   * @apilevel internal
   */
  private String name_compute() {  return getID();  }
  /**
   * @apilevel internal
   */
  protected boolean signature_computed = false;
  /**
   * @apilevel internal
   */
  protected String signature_value;
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:347
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String signature() {
    if(signature_computed) {
      return signature_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    signature_value = signature_compute();
if(isFinal && num == state().boundariesCrossed) signature_computed = true;
    return signature_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @attribute syn
   * @aspect MethodDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:146
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean sameSignature(MethodDecl m) {
      ASTNode$State state = state();
    boolean sameSignature_MethodDecl_value = sameSignature_compute(m);
    return sameSignature_MethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean sameSignature_compute(MethodDecl m) {  return signature().equals(m.signature());  }
  protected java.util.Map moreSpecificThan_MethodDecl_values;
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:155
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean moreSpecificThan(MethodDecl m) {
    Object _parameters = m;
    if(moreSpecificThan_MethodDecl_values == null) moreSpecificThan_MethodDecl_values = new java.util.HashMap(4);
    if(moreSpecificThan_MethodDecl_values.containsKey(_parameters)) {
      return ((Boolean)moreSpecificThan_MethodDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean moreSpecificThan_MethodDecl_value = moreSpecificThan_compute(m);
if(isFinal && num == state().boundariesCrossed) moreSpecificThan_MethodDecl_values.put(_parameters, Boolean.valueOf(moreSpecificThan_MethodDecl_value));
    return moreSpecificThan_MethodDecl_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @attribute syn
   * @aspect MethodDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:189
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean overrides(MethodDecl m) {
    Object _parameters = m;
    if(overrides_MethodDecl_values == null) overrides_MethodDecl_values = new java.util.HashMap(4);
    if(overrides_MethodDecl_values.containsKey(_parameters)) {
      return ((Boolean)overrides_MethodDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean overrides_MethodDecl_value = overrides_compute(m);
if(isFinal && num == state().boundariesCrossed) overrides_MethodDecl_values.put(_parameters, Boolean.valueOf(overrides_MethodDecl_value));
    return overrides_MethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean overrides_compute(MethodDecl m) {  return !isStatic() && !m.isPrivate() && m.accessibleFrom(hostType()) && 
     hostType().instanceOf(m.hostType()) && m.signature().equals(signature());  }
  protected java.util.Map hides_MethodDecl_values;
  /**
   * @attribute syn
   * @aspect MethodDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:193
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hides(MethodDecl m) {
    Object _parameters = m;
    if(hides_MethodDecl_values == null) hides_MethodDecl_values = new java.util.HashMap(4);
    if(hides_MethodDecl_values.containsKey(_parameters)) {
      return ((Boolean)hides_MethodDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean hides_MethodDecl_value = hides_compute(m);
if(isFinal && num == state().boundariesCrossed) hides_MethodDecl_values.put(_parameters, Boolean.valueOf(hides_MethodDecl_value));
    return hides_MethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hides_compute(MethodDecl m) {  return isStatic() && !m.isPrivate() && m.accessibleFrom(hostType()) && 
     hostType().instanceOf(m.hostType()) && m.signature().equals(signature());  }
  protected java.util.Map parameterDeclaration_String_values;
  /**
   * @attribute syn
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:99
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet parameterDeclaration(String name) {
    Object _parameters = name;
    if(parameterDeclaration_String_values == null) parameterDeclaration_String_values = new java.util.HashMap(4);
    if(parameterDeclaration_String_values.containsKey(_parameters)) {
      return (SimpleSet)parameterDeclaration_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet parameterDeclaration_String_value = parameterDeclaration_compute(name);
if(isFinal && num == state().boundariesCrossed) parameterDeclaration_String_values.put(_parameters, parameterDeclaration_String_value);
    return parameterDeclaration_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet parameterDeclaration_compute(String name) {
    for(int i = 0; i < getNumParameter(); i++)
      if(getParameter(i).name().equals(name))
        return (ParameterDeclaration)getParameter(i);
    return SimpleSet.emptySet;
  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:215
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSynthetic() {
      ASTNode$State state = state();
    boolean isSynthetic_value = isSynthetic_compute();
    return isSynthetic_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSynthetic_compute() {  return getModifiers().isSynthetic();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:224
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isPublic() {
      ASTNode$State state = state();
    boolean isPublic_value = isPublic_compute();
    return isPublic_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isPublic_compute() {  return getModifiers().isPublic() || hostType().isInterfaceDecl();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:225
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isPrivate() {
      ASTNode$State state = state();
    boolean isPrivate_value = isPrivate_compute();
    return isPrivate_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isPrivate_compute() {  return getModifiers().isPrivate();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:226
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isProtected() {
      ASTNode$State state = state();
    boolean isProtected_value = isProtected_compute();
    return isProtected_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isProtected_compute() {  return getModifiers().isProtected();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:227
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isAbstract() {
      ASTNode$State state = state();
    boolean isAbstract_value = isAbstract_compute();
    return isAbstract_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isAbstract_compute() {  return getModifiers().isAbstract() || hostType().isInterfaceDecl();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:228
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isStatic() {
      ASTNode$State state = state();
    boolean isStatic_value = isStatic_compute();
    return isStatic_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isStatic_compute() {  return getModifiers().isStatic();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:230
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isFinal() {
      ASTNode$State state = state();
    boolean isFinal_value = isFinal_compute();
    return isFinal_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isFinal_compute() {  return getModifiers().isFinal() || hostType().isFinal() || isPrivate();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:231
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSynchronized() {
      ASTNode$State state = state();
    boolean isSynchronized_value = isSynchronized_compute();
    return isSynchronized_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSynchronized_compute() {  return getModifiers().isSynchronized();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:232
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isNative() {
      ASTNode$State state = state();
    boolean isNative_value = isNative_compute();
    return isNative_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isNative_compute() {  return getModifiers().isNative();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:233
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isStrictfp() {
      ASTNode$State state = state();
    boolean isStrictfp_value = isStrictfp_compute();
    return isStrictfp_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isStrictfp_compute() {  return getModifiers().isStrictfp();  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:814
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String dumpString() {
      ASTNode$State state = state();
    String dumpString_value = dumpString_compute();
    return dumpString_value;
  }
  /**
   * @apilevel internal
   */
  private String dumpString_compute() {  return getClass().getName() + " [" + getID() + "]";  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:269
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
  private TypeDecl type_compute() {  return getTypeAccess().type();  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:272
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isVoid() {
      ASTNode$State state = state();
    boolean isVoid_value = isVoid_compute();
    return isVoid_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isVoid_compute() {  return type().isVoid();  }
  /**
   * @attribute syn
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:89
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean mayOverrideReturn(MethodDecl m) {
      ASTNode$State state = state();
    boolean mayOverrideReturn_MethodDecl_value = mayOverrideReturn_compute(m);
    return mayOverrideReturn_MethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean mayOverrideReturn_compute(MethodDecl m) {
    return type().instanceOf(m.type());
  }
  /* It is also a compile-time error if any method declared in an annotation type has a
  signature that is override-equivalent to that of any public or protected method
  declared in class Object or in the interface annotation.Annotation* @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:139
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean annotationMethodOverride() {
      ASTNode$State state = state();
    boolean annotationMethodOverride_value = annotationMethodOverride_compute();
    return annotationMethodOverride_value;
  }
  /**
   * @apilevel internal
   */
  private boolean annotationMethodOverride_compute() {  return !hostType().ancestorMethods(signature()).isEmpty();  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:285
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasAnnotationSuppressWarnings(String s) {
      ASTNode$State state = state();
    boolean hasAnnotationSuppressWarnings_String_value = hasAnnotationSuppressWarnings_compute(s);
    return hasAnnotationSuppressWarnings_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hasAnnotationSuppressWarnings_compute(String s) {  return getModifiers().hasAnnotationSuppressWarnings(s);  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:323
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDeprecated() {
      ASTNode$State state = state();
    boolean isDeprecated_value = isDeprecated_compute();
    return isDeprecated_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDeprecated_compute() {  return getModifiers().hasDeprecatedAnnotation();  }
  /**
   * @apilevel internal
   */
  protected boolean usesTypeVariable_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean usesTypeVariable_value;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:907
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean usesTypeVariable() {
    if(usesTypeVariable_computed) {
      return usesTypeVariable_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    usesTypeVariable_value = usesTypeVariable_compute();
if(isFinal && num == state().boundariesCrossed) usesTypeVariable_computed = true;
    return usesTypeVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean usesTypeVariable_compute() {  return getModifiers().usesTypeVariable() || getTypeAccess().usesTypeVariable() ||
    getParameterList().usesTypeVariable() || getExceptionList().usesTypeVariable();  }
  /**
   * @apilevel internal
   */
  protected boolean sourceMethodDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected MethodDecl sourceMethodDecl_value;
  /**
   * @attribute syn
   * @aspect SourceDeclarations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1274
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl sourceMethodDecl() {
    if(sourceMethodDecl_computed) {
      return sourceMethodDecl_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    sourceMethodDecl_value = sourceMethodDecl_compute();
if(isFinal && num == state().boundariesCrossed) sourceMethodDecl_computed = true;
    return sourceMethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private MethodDecl sourceMethodDecl_compute() {  return this;  }
  /**
   * @attribute syn
   * @aspect GenericsParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:65
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean visibleTypeParameters() {
      ASTNode$State state = state();
    boolean visibleTypeParameters_value = visibleTypeParameters_compute();
    return visibleTypeParameters_value;
  }
  /**
   * @apilevel internal
   */
  private boolean visibleTypeParameters_compute() {  return !isStatic();  }
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:284
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
  private int arity_compute() {  return getNumParameter();  }
  /* The method is then a variable arity method. Otherwise, it is a fixed arity method.* @attribute syn
   * @aspect VariableArityParameters
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:33
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isVariableArity() {
      ASTNode$State state = state();
    boolean isVariableArity_value = isVariableArity_compute();
    return isVariableArity_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isVariableArity_compute() {  return getNumParameter() == 0 ? false : getParameter(getNumParameter()-1).isVariableArity();  }
  /**
   * @attribute syn
   * @aspect VariableArityParameters
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:38
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParameterDeclaration lastParameter() {
      ASTNode$State state = state();
    ParameterDeclaration lastParameter_value = lastParameter_compute();
    return lastParameter_value;
  }
  /**
   * @apilevel internal
   */
  private ParameterDeclaration lastParameter_compute() {  return getParameter(getNumParameter() - 1);  }
  /**
   * @attribute syn
   * @aspect VariableArityParametersCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/VariableArityParametersCodegen.jrag:80
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int sootTypeModifiers() {
      ASTNode$State state = state();
    int sootTypeModifiers_value = sootTypeModifiers_compute();
    return sootTypeModifiers_value;
  }
  /**
   * @apilevel internal
   */
  private int sootTypeModifiers_compute() {
    int res = refined_EmitJimple_MethodDecl_sootTypeModifiers();
    if(isVariableArity())
      res |= Modifiers.ACC_VARARGS;
    return res;
  }
  /**
   * @apilevel internal
   */
  protected boolean sootMethod_computed = false;
  /**
   * @apilevel internal
   */
  protected SootMethod sootMethod_value;
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:269
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SootMethod sootMethod() {
    if(sootMethod_computed) {
      return sootMethod_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    sootMethod_value = sootMethod_compute();
if(isFinal && num == state().boundariesCrossed) sootMethod_computed = true;
    return sootMethod_value;
  }
  /**
   * @apilevel internal
   */
  private SootMethod sootMethod_compute() {
    ArrayList list = new ArrayList();
    for(int i = 0; i < getNumParameter(); i++)
      list.add(getParameter(i).type().getSootType());
    if(hostType().isArrayDecl())
      return typeObject().getSootClassDecl().getMethod(name(), list, type().getSootType());
    return hostType().getSootClassDecl().getMethod(name(), list, type().getSootType());
  }
  /**
   * @apilevel internal
   */
  protected boolean sootRef_computed = false;
  /**
   * @apilevel internal
   */
  protected SootMethodRef sootRef_value;
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:279
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SootMethodRef sootRef() {
    if(sootRef_computed) {
      return sootRef_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    sootRef_value = sootRef_compute();
if(isFinal && num == state().boundariesCrossed) sootRef_computed = true;
    return sootRef_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @apilevel internal
   */
  protected boolean offsetBeforeParameters_computed = false;
  /**
   * @apilevel internal
   */
  protected int offsetBeforeParameters_value;
  /**
   * @attribute syn
   * @aspect LocalNum
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:17
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int offsetBeforeParameters() {
    if(offsetBeforeParameters_computed) {
      return offsetBeforeParameters_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    offsetBeforeParameters_value = offsetBeforeParameters_compute();
if(isFinal && num == state().boundariesCrossed) offsetBeforeParameters_computed = true;
    return offsetBeforeParameters_value;
  }
  /**
   * @apilevel internal
   */
  private int offsetBeforeParameters_compute() {  return 0;  }
  /**
   * @apilevel internal
   */
  protected boolean offsetAfterParameters_computed = false;
  /**
   * @apilevel internal
   */
  protected int offsetAfterParameters_value;
  /**
   * @attribute syn
   * @aspect LocalNum
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int offsetAfterParameters() {
    if(offsetAfterParameters_computed) {
      return offsetAfterParameters_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    offsetAfterParameters_value = offsetAfterParameters_compute();
if(isFinal && num == state().boundariesCrossed) offsetAfterParameters_computed = true;
    return offsetAfterParameters_value;
  }
  /**
   * @apilevel internal
   */
  private int offsetAfterParameters_compute() {
    if(getNumParameter() == 0)
      return offsetBeforeParameters();
    return getParameter(getNumParameter()-1).localNum() + 
           getParameter(getNumParameter()-1).type().variableSize();
  }
  /**
   * @attribute syn
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:34
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl erasedMethod() {
      ASTNode$State state = state();
    MethodDecl erasedMethod_value = erasedMethod_compute();
    return erasedMethod_value;
  }
  /**
   * @apilevel internal
   */
  private MethodDecl erasedMethod_compute() {  return this;  }
  protected java.util.Map handlesException_TypeDecl_values;
  /**
   * @attribute inh
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean handlesException(TypeDecl exceptionType) {
    Object _parameters = exceptionType;
    if(handlesException_TypeDecl_values == null) handlesException_TypeDecl_values = new java.util.HashMap(4);
    if(handlesException_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)handlesException_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
if(isFinal && num == state().boundariesCrossed) handlesException_TypeDecl_values.put(_parameters, Boolean.valueOf(handlesException_TypeDecl_value));
    return handlesException_TypeDecl_value;
  }
  /**
   * @attribute inh
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:14
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl unknownMethod() {
      ASTNode$State state = state();
    MethodDecl unknownMethod_value = getParent().Define_MethodDecl_unknownMethod(this, null);
    return unknownMethod_value;
  }
  /**
   * @attribute inh
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:277
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeObject() {
      ASTNode$State state = state();
    TypeDecl typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
    return typeObject_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:436
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBlockOptNoTransform()) {
      return v.isFinal() && (v.isClassVariable() || v.isInstanceVariable()) ? true : isDAbefore(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:867
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBlockOptNoTransform()) {
      return v.isFinal() && (v.isClassVariable() || v.isInstanceVariable()) ? false : true;
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:129
   * @apilevel internal
   */
  public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    if(caller == getBlockOptNoTransform()) {
      return throwsException(exceptionType) || handlesException(exceptionType);
    }
    return getParent().Define_boolean_handlesException(this, caller, exceptionType);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:46
   * @apilevel internal
   */
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
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:271
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBePublic(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:272
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeProtected(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:273
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBePrivate(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:274
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeAbstract(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:275
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeStatic(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:276
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeFinal(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:277
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeSynchronized(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeSynchronized(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:278
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeNative(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeNative(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:279
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStrictfp(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeStrictfp(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:241
   * @apilevel internal
   */
  public ASTNode Define_ASTNode_enclosingBlock(ASTNode caller, ASTNode child) {
    if(caller == getBlockOptNoTransform()) {
      return this;
    }
    return getParent().Define_ASTNode_enclosingBlock(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:82
   * @apilevel internal
   */
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
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:405
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_returnType(ASTNode caller, ASTNode child) {
    if(caller == getBlockOptNoTransform()) {
      return type();
    }
    return getParent().Define_TypeDecl_returnType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:142
   * @apilevel internal
   */
  public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
    if(caller == getBlockOptNoTransform()) {
      return isStatic();
    }
    return getParent().Define_boolean_inStaticContext(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:33
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getBlockOptNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_reachable(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:61
   * @apilevel internal
   */
  public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return true;
    }
    return getParent().Define_boolean_isMethodParameter(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:62
   * @apilevel internal
   */
  public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_isConstructorParameter(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:63
   * @apilevel internal
   */
  public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:86
   * @apilevel internal
   */
  public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
    if(caller == getModifiersNoTransform()) {
      return name.equals("METHOD");
    }
    return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:22
   * @apilevel internal
   */
  public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
    if(caller == getParameterListNoTransform()) {
      int i = caller.getIndexOfChild(child);
      return i == getNumParameter() - 1;
    }
    return getParent().Define_boolean_variableArityValid(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:26
   * @apilevel internal
   */
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
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:351
   * @apilevel internal
   */
  public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
    if(caller == getBlockOptNoTransform()) {
      return getNumException() != 0;
    }
    return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
