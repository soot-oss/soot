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
 * @declaredat java.ast:72
 */
public class ConstructorDecl extends BodyDecl implements Cloneable {
  /**
   * @apilevel low-level
   */
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
  public ConstructorDecl clone() throws CloneNotSupportedException {
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
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConstructorDecl copy() {
      try {
        ConstructorDecl node = (ConstructorDecl)clone();
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
  public ConstructorDecl fullCopy() {
    ConstructorDecl res = (ConstructorDecl)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect ConstructorDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:144
   */
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
  /**
   * Flag to indicate if this constructor is an auto-generated
   * default constructor. Default constructors are not pretty
   * printed.
   * @ast method 
   * @aspect ImplicitConstructor
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:171
   */
  

  /**
   * Flag to indicate if this constructor is an auto-generated
   * default constructor. Default constructors are not pretty
   * printed.
   */
  private boolean isDefaultConstructor = false;
  /**
   * Set the default constructor flag. Causes this constructor
   * to not be pretty printed.
   * @ast method 
   * @aspect ImplicitConstructor
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:176
   */
  public void setDefaultConstructor() {
    isDefaultConstructor = true;
  }
  /**
   * @ast method 
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:110
   */
  public void checkModifiers() {
    super.checkModifiers();
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:68
   */
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
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:119
   */
  public void toString(StringBuffer s) {
    if(isDefaultConstructor()) return;
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
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:424
   */
  public void typeCheck() {
    // 8.8.4 (8.4.4)
    TypeDecl exceptionType = typeThrowable();
    for(int i = 0; i < getNumException(); i++) {
      TypeDecl typeDecl = getException(i).type();
      if(!typeDecl.instanceOf(exceptionType))
        error(signature() + " throws non throwable type " + typeDecl.fullName());
    }
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:138
   */
  protected void transformEnumConstructors() {
    // make sure constructor is private
    Modifiers newModifiers = new Modifiers(new List());
    for (int i = 0; i < getModifiers().getNumModifier(); ++i) {
      String modifier = getModifiers().getModifier(i).getID();
      if (modifier.equals("public") || modifier.equals("private") ||
        modifier.equals("protected"))
          continue;
      newModifiers.addModifier(new Modifier(modifier));
    }
    newModifiers.addModifier(new Modifier("private"));
    setModifiers(newModifiers);

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
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1046
   */
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
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:445
   */
  

  // add val$name as parameters to the constructor
  protected boolean addEnclosingVariables = true;
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:446
   */
  public void addEnclosingVariables() {
    if(!addEnclosingVariables) return;
    addEnclosingVariables = false;
    hostType().addEnclosingVariables();
    for(Iterator iter = hostType().enclosingVariables().iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      getParameterList().add(new ParameterDeclaration(v.type(), "val$" + v.name()));
    }
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:480
   */
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
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:524
   */
  protected List createAccessorParameters() {
    List parameters = new List();
    for (int i=0; i<getNumParameter(); i++)
      parameters.add(new ParameterDeclaration(getParameter(i).type(), getParameter(i).name()));
    parameters.add(new ParameterDeclaration(createAnonymousJavaTypeDecl().createBoundAccess(), ("p" + getNumParameter())));
    return parameters;
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:532
   */
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
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:119
   */
  public void transformation() {
    // this$val as fields and constructor parameters
    addEnclosingVariables();
    super.transformation();
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:234
   */
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
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:294
   */
  


  public SootMethod sootMethod;
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:57
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:186
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:242
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:281
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
   * @declaredat java.ast:1
   */
  public ConstructorDecl() {
    super();

    setChild(new List(), 1);
    setChild(new List(), 2);
    setChild(new Opt(), 3);

  }
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  public ConstructorDecl(Modifiers p0, String p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
    setChild(p4, 3);
    setChild(p5, 4);
  }
  /**
   * @ast method 
   * @declaredat java.ast:18
   */
  public ConstructorDecl(Modifiers p0, beaver.Symbol p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
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
    return true;
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
    setChild(list, 1);
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
    List<ParameterDeclaration> list = (List<ParameterDeclaration>)getChild(1);
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
    return (List<ParameterDeclaration>)getChildNoTransform(1);
  }
  /**
   * Setter for ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setExceptionList(List<Access> list) {
    setChild(list, 2);
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
    List<Access> list = (List<Access>)getChild(2);
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
    return (List<Access>)getChildNoTransform(2);
  }
  /**
   * Setter for ConstructorInvocationOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setConstructorInvocationOpt(Opt<Stmt> opt) {
    setChild(opt, 3);
  }
  /**
   * Does this node have a ConstructorInvocation child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasConstructorInvocation() {
    return getConstructorInvocationOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child ConstructorInvocation
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Stmt getConstructorInvocation() {
    return (Stmt)getConstructorInvocationOpt().getChild(0);
  }
  /**
   * Setter for optional child ConstructorInvocation
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setConstructorInvocation(Stmt node) {
    getConstructorInvocationOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Stmt> getConstructorInvocationOpt() {
    return (Opt<Stmt>)getChild(3);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Stmt> getConstructorInvocationOptNoTransform() {
    return (Opt<Stmt>)getChildNoTransform(3);
  }
  /**
   * Setter for Block
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setBlock(Block node) {
    setChild(node, 4);
  }
  /**
   * Getter for Block
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Block getBlock() {
    return (Block)getChild(4);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Block getBlockNoTransform() {
    return (Block)getChildNoTransform(4);
  }
  /**
   * @ast method 
   * @aspect EmitJimpleRefinements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:121
   */
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
  /**
   * @ast method 
   * @aspect ConstructorDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:136
   */
  private boolean refined_ConstructorDecl_ConstructorDecl_moreSpecificThan_ConstructorDecl(ConstructorDecl m)
{
    for(int i = 0; i < getNumParameter(); i++) {
      if(!getParameter(i).type().instanceOf(m.getParameter(i).type()))
        return false;
    }
    return true;
  }
  protected java.util.Map accessibleFrom_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect AccessControl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:94
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
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:295
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
  private boolean isDAafter_compute(Variable v) {  return getBlock().isDAafter(v) && getBlock().checkReturnDA(v);  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:751
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafter(Variable v) {
    Object _parameters = v;
    if(isDUafter_Variable_values == null) isDUafter_Variable_values = new java.util.HashMap(4);
    if(isDUafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDUafter_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDUafter_Variable_value = isDUafter_compute(v);
if(isFinal && num == state().boundariesCrossed) isDUafter_Variable_values.put(_parameters, Boolean.valueOf(isDUafter_Variable_value));
    return isDUafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafter_compute(Variable v) {  return getBlock().isDUafter(v) && getBlock().checkReturnDU(v);  }
  protected java.util.Map throwsException_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:145
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
   * @apilevel internal
   */
  protected boolean name_computed = false;
  /**
   * @apilevel internal
   */
  protected String name_value;
  /**
   * @attribute syn
   * @aspect ConstructorDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:110
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String name() {
    if(name_computed) {
      return name_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    name_value = name_compute();
if(isFinal && num == state().boundariesCrossed) name_computed = true;
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
   * @aspect ConstructorDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:112
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
      s.append(getParameter(i));
      if(i != getNumParameter() - 1)
        s.append(", ");
    }
    s.append(")");
    return s.toString();
  }
  protected java.util.Map sameSignature_ConstructorDecl_values;
  /**
   * @attribute syn
   * @aspect ConstructorDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:125
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean sameSignature(ConstructorDecl c) {
    Object _parameters = c;
    if(sameSignature_ConstructorDecl_values == null) sameSignature_ConstructorDecl_values = new java.util.HashMap(4);
    if(sameSignature_ConstructorDecl_values.containsKey(_parameters)) {
      return ((Boolean)sameSignature_ConstructorDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean sameSignature_ConstructorDecl_value = sameSignature_compute(c);
if(isFinal && num == state().boundariesCrossed) sameSignature_ConstructorDecl_values.put(_parameters, Boolean.valueOf(sameSignature_ConstructorDecl_value));
    return sameSignature_ConstructorDecl_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:168
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean moreSpecificThan(ConstructorDecl m) {
    Object _parameters = m;
    if(moreSpecificThan_ConstructorDecl_values == null) moreSpecificThan_ConstructorDecl_values = new java.util.HashMap(4);
    if(moreSpecificThan_ConstructorDecl_values.containsKey(_parameters)) {
      return ((Boolean)moreSpecificThan_ConstructorDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean moreSpecificThan_ConstructorDecl_value = moreSpecificThan_compute(m);
if(isFinal && num == state().boundariesCrossed) moreSpecificThan_ConstructorDecl_values.put(_parameters, Boolean.valueOf(moreSpecificThan_ConstructorDecl_value));
    return moreSpecificThan_ConstructorDecl_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @return true if this is an auto-generated default constructor
   * @attribute syn
   * @aspect ImplicitConstructor
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:182
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDefaultConstructor() {
      ASTNode$State state = state();
    boolean isDefaultConstructor_value = isDefaultConstructor_compute();
    return isDefaultConstructor_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDefaultConstructor_compute() {  return isDefaultConstructor;  }
  protected java.util.Map parameterDeclaration_String_values;
  /**
   * @attribute syn
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:105
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:217
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:235
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
  private boolean isPublic_compute() {  return getModifiers().isPublic();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:236
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:237
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
  protected java.util.Map circularThisInvocation_ConstructorDecl_values;
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:83
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean circularThisInvocation(ConstructorDecl decl) {
    Object _parameters = decl;
    if(circularThisInvocation_ConstructorDecl_values == null) circularThisInvocation_ConstructorDecl_values = new java.util.HashMap(4);
    if(circularThisInvocation_ConstructorDecl_values.containsKey(_parameters)) {
      return ((Boolean)circularThisInvocation_ConstructorDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean circularThisInvocation_ConstructorDecl_value = circularThisInvocation_compute(decl);
if(isFinal && num == state().boundariesCrossed) circularThisInvocation_ConstructorDecl_values.put(_parameters, Boolean.valueOf(circularThisInvocation_ConstructorDecl_value));
    return circularThisInvocation_ConstructorDecl_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:268
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
      ASTNode$State state = state();
    TypeDecl type_value = type_compute();
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {  return unknownType();  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:274
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
  private boolean isVoid_compute() {  return true;  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:286
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:324
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
  protected boolean sourceConstructorDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected ConstructorDecl sourceConstructorDecl_value;
  /**
   * @attribute syn
   * @aspect SourceDeclarations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1278
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConstructorDecl sourceConstructorDecl() {
    if(sourceConstructorDecl_computed) {
      return sourceConstructorDecl_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    sourceConstructorDecl_value = sourceConstructorDecl_compute();
if(isFinal && num == state().boundariesCrossed) sourceConstructorDecl_computed = true;
    return sourceConstructorDecl_value;
  }
  /**
   * @apilevel internal
   */
  private ConstructorDecl sourceConstructorDecl_compute() {  return this;  }
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:190
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean applicableBySubtyping(List argList) {
      ASTNode$State state = state();
    boolean applicableBySubtyping_List_value = applicableBySubtyping_compute(argList);
    return applicableBySubtyping_List_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:210
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean applicableByMethodInvocationConversion(List argList) {
      ASTNode$State state = state();
    boolean applicableByMethodInvocationConversion_List_value = applicableByMethodInvocationConversion_compute(argList);
    return applicableByMethodInvocationConversion_List_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:231
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean applicableVariableArity(List argList) {
      ASTNode$State state = state();
    boolean applicableVariableArity_List_value = applicableVariableArity_compute(argList);
    return applicableVariableArity_List_value;
  }
  /**
   * @apilevel internal
   */
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
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:318
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean potentiallyApplicable(List argList) {
      ASTNode$State state = state();
    boolean potentiallyApplicable_List_value = potentiallyApplicable_compute(argList);
    return potentiallyApplicable_List_value;
  }
  /**
   * @apilevel internal
   */
  private boolean potentiallyApplicable_compute(List argList) {
    if(isVariableArity() && !(argList.getNumChild() >= arity()-1))
      return false;
    if(!isVariableArity() && !(arity() == argList.getNumChild()))
      return false;
    return true;
  }
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:325
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
  /**
   * @attribute syn
   * @aspect VariableArityParameters
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:34
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:63
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
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:421
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean needsEnclosing() {
      ASTNode$State state = state();
    boolean needsEnclosing_value = needsEnclosing_compute();
    return needsEnclosing_value;
  }
  /**
   * @apilevel internal
   */
  private boolean needsEnclosing_compute() {  return hostType().needsEnclosing();  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:422
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean needsSuperEnclosing() {
      ASTNode$State state = state();
    boolean needsSuperEnclosing_value = needsSuperEnclosing_compute();
    return needsSuperEnclosing_value;
  }
  /**
   * @apilevel internal
   */
  private boolean needsSuperEnclosing_compute() {  return hostType().needsSuperEnclosing();  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:424
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl enclosing() {
      ASTNode$State state = state();
    TypeDecl enclosing_value = enclosing_compute();
    return enclosing_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl enclosing_compute() {  return hostType().enclosing();  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:425
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl superEnclosing() {
      ASTNode$State state = state();
    TypeDecl superEnclosing_value = superEnclosing_compute();
    return superEnclosing_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl superEnclosing_compute() {  return hostType().superEnclosing();  }
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:120
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
    int result = 0;
    if(isPublic()) result |= soot.Modifier.PUBLIC;
    if(isProtected()) result |= soot.Modifier.PROTECTED;
    if(isPrivate()) result |= soot.Modifier.PRIVATE;
    return result;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:295
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:310
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
  /**
   * @apilevel internal
   */
  protected boolean localNumOfFirstParameter_computed = false;
  /**
   * @apilevel internal
   */
  protected int localNumOfFirstParameter_value;
  /**
   * @attribute syn
   * @aspect LocalNum
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:32
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int localNumOfFirstParameter() {
    if(localNumOfFirstParameter_computed) {
      return localNumOfFirstParameter_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    localNumOfFirstParameter_value = localNumOfFirstParameter_compute();
if(isFinal && num == state().boundariesCrossed) localNumOfFirstParameter_computed = true;
    return localNumOfFirstParameter_value;
  }
  /**
   * @apilevel internal
   */
  private int localNumOfFirstParameter_compute() {
    int i = 0;
    if(hostType().needsEnclosing())
      i++;
    if(hostType().needsSuperEnclosing())
      i++;
    return i;
  }
  /**
   * @apilevel internal
   */
  protected boolean offsetFirstEnclosingVariable_computed = false;
  /**
   * @apilevel internal
   */
  protected int offsetFirstEnclosingVariable_value;
  /**
   * @attribute syn
   * @aspect LocalNum
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:41
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int offsetFirstEnclosingVariable() {
    if(offsetFirstEnclosingVariable_computed) {
      return offsetFirstEnclosingVariable_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    offsetFirstEnclosingVariable_value = offsetFirstEnclosingVariable_compute();
if(isFinal && num == state().boundariesCrossed) offsetFirstEnclosingVariable_computed = true;
    return offsetFirstEnclosingVariable_value;
  }
  /**
   * @apilevel internal
   */
  private int offsetFirstEnclosingVariable_compute() {  return getNumParameter() == 0 ?
    localNumOfFirstParameter() :
    getParameter(getNumParameter()-1).localNum() + getParameter(getNumParameter()-1).type().variableSize();  }
  /**
   * @attribute syn
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:317
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConstructorDecl erasedConstructor() {
      ASTNode$State state = state();
    ConstructorDecl erasedConstructor_value = erasedConstructor_compute();
    return erasedConstructor_value;
  }
  /**
   * @apilevel internal
   */
  private ConstructorDecl erasedConstructor_compute() {  return this;  }
  protected java.util.Map handlesException_TypeDecl_values;
  /**
   * @attribute inh
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:36
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
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:267
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl unknownType() {
      ASTNode$State state = state();
    TypeDecl unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
    return unknownType_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:298
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBlockNoTransform()) {
      return hasConstructorInvocation() ? getConstructorInvocation().isDAafter(v) : isDAbefore(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:754
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBlockNoTransform()) {
      return hasConstructorInvocation() ? getConstructorInvocation().isDUafter(v) : isDUbefore(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:142
   * @apilevel internal
   */
  public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    if(caller == getConstructorInvocationOptNoTransform()) {
      return throwsException(exceptionType) || handlesException(exceptionType);
    }
    if(caller == getBlockNoTransform()) {
      return throwsException(exceptionType) || handlesException(exceptionType);
    }
    return getParent().Define_boolean_handlesException(this, caller, exceptionType);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:45
   * @apilevel internal
   */
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
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:64
   * @apilevel internal
   */
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
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:282
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBePublic(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:283
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeProtected(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:284
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBePrivate(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:242
   * @apilevel internal
   */
  public ASTNode Define_ASTNode_enclosingBlock(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return this;
    }
    return getParent().Define_ASTNode_enclosingBlock(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:117
   * @apilevel internal
   */
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
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:517
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingInstance(ASTNode caller, ASTNode child) {
    if(caller == getConstructorInvocationOptNoTransform()) {
      return unknownType();
    }
    return getParent().Define_TypeDecl_enclosingInstance(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:132
   * @apilevel internal
   */
  public boolean Define_boolean_inExplicitConstructorInvocation(ASTNode caller, ASTNode child) {
    if(caller == getConstructorInvocationOptNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_inExplicitConstructorInvocation(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:144
   * @apilevel internal
   */
  public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
    if(caller == getConstructorInvocationOptNoTransform()) {
      return false;
    }
    if(caller == getBlockNoTransform()) {
      return false;
    }
    return getParent().Define_boolean_inStaticContext(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:32
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return !hasConstructorInvocation() ? true : getConstructorInvocation().canCompleteNormally();
    }
    if(caller == getConstructorInvocationOptNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_reachable(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:58
   * @apilevel internal
   */
  public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_isMethodParameter(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:59
   * @apilevel internal
   */
  public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return true;
    }
    return getParent().Define_boolean_isConstructorParameter(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:60
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:89
   * @apilevel internal
   */
  public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
    if(caller == getModifiersNoTransform()) {
      return name.equals("CONSTRUCTOR");
    }
    return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:21
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:45
   * @apilevel internal
   */
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
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:352
   * @apilevel internal
   */
  public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return getNumException() != 0;
    }
    return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag at line 218
    if(!hasConstructorInvocation() && !hostType().isObject()) {
      state().duringLookupConstructor++;
      ASTNode result = rewriteRule0();
      state().duringLookupConstructor--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:218
   * @apilevel internal
   */  private ConstructorDecl rewriteRule0() {
{
      setConstructorInvocation(
        new ExprStmt(
          new SuperConstructorAccess("super", new List())
          )
        );
      return this;
    }  }
}
