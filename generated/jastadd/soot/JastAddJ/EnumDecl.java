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
 * @declaredat Enums.ast:1
 */
public class EnumDecl extends ClassDecl implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isStatic_computed = false;
    getSuperClassAccessOpt_computed = false;
    getSuperClassAccessOpt_value = null;
    enumConstants_computed = false;
    enumConstants_value = null;
    unimplementedMethods_computed = false;
    unimplementedMethods_value = null;
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
  public EnumDecl clone() throws CloneNotSupportedException {
    EnumDecl node = (EnumDecl)super.clone();
    node.isStatic_computed = false;
    node.getSuperClassAccessOpt_computed = false;
    node.getSuperClassAccessOpt_value = null;
    node.enumConstants_computed = false;
    node.enumConstants_value = null;
    node.unimplementedMethods_computed = false;
    node.unimplementedMethods_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public EnumDecl copy() {
      try {
        EnumDecl node = (EnumDecl)clone();
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
  public EnumDecl fullCopy() {
    EnumDecl res = (EnumDecl)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:46
   */
  public void typeCheck() {
    super.typeCheck();
    for(Iterator iter = memberMethods("finalize").iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(m.getNumParameter() == 0 && m.hostType() == this)
        error("an enum may not declare a finalizer");
    }
    checkEnum(this);
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:81
   */
  

  private boolean done = false;
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:82
   */
  private boolean done() {
    if(done) return true;
    done = true;
    return false;
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:309
   */
  private void addValues() {
    int numConstants = enumConstants().size();
    List initValues = new List();
    for(Iterator iter = enumConstants().iterator(); iter.hasNext(); ) {
      EnumConstant c = (EnumConstant)iter.next();
      initValues.add(c.createBoundFieldAccess());
    }
    FieldDeclaration values = new FieldDeclaration(
      new Modifiers(new List().add(
        new Modifier("private")).add(
        new Modifier("static")).add(
        new Modifier("final")).add(
        new Modifier("synthetic"))
      ),
      arrayType().createQualifiedAccess(),
      "$VALUES",
      new Opt(
          new ArrayCreationExpr(
            new ArrayTypeWithSizeAccess(
              createQualifiedAccess(),
              Literal.buildIntegerLiteral(enumConstants().size())
            ),
            new Opt(
              new ArrayInit(
                initValues
              )
            )
          )
      )
    );
    addBodyDecl(values);
    // public static final Test[] values() { return (Test[])$VALUES.clone(); }
    addBodyDecl(
      new MethodDecl(
        new Modifiers(new List().add(
          new Modifier("public")).add(
          new Modifier("static")).add(
          new Modifier("final")).add(
          new Modifier("synthetic"))
        ),
        arrayType().createQualifiedAccess(),
        "values",
        new List(),
        new List(),
        new Opt(
          new Block(
            new List().add(
              new ReturnStmt(
                new Opt(
                  new CastExpr(
                    arrayType().createQualifiedAccess(),
                    values.createBoundFieldAccess().qualifiesAccess(
                      new MethodAccess(
                        "clone",
                        new List()
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    );
    // public static Test valueOf(String s) { return (Test)java.lang.Enum.valueOf(Test.class, s); }
    addBodyDecl(
      new MethodDecl(
        new Modifiers(new List().add(
          new Modifier("public")).add(
          new Modifier("static")).add(
          new Modifier("synthetic"))
        ),
        createQualifiedAccess(),
        "valueOf",
        new List().add(
          new ParameterDeclaration(
            new Modifiers(new List()),
            typeString().createQualifiedAccess(),
            "s"
          )
        ),
        new List(),
        new Opt(
          new Block(
            new List().add(
              new ReturnStmt(
                new Opt(
                  new CastExpr(
                    createQualifiedAccess(),
                    lookupType("java.lang", "Enum").createQualifiedAccess().qualifiesAccess(
                      new MethodAccess(
                        "valueOf",
                        new List().add(
                          createQualifiedAccess().qualifiesAccess(new ClassAccess())
                        ).add(
                          new VarAccess(
                            "s"
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    );
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:448
   */
  protected void checkEnum(EnumDecl enumDecl) {
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof ConstructorDecl)
        getBodyDecl(i).checkEnum(enumDecl);
      else if(getBodyDecl(i) instanceof InstanceInitializer)
        getBodyDecl(i).checkEnum(enumDecl);
      else if(getBodyDecl(i) instanceof FieldDeclaration) {
        FieldDeclaration f = (FieldDeclaration)getBodyDecl(i);
        if(!f.isStatic() && f.hasInit())
          f.checkEnum(enumDecl);
      }
    }
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:540
   */
  public void toString(StringBuffer s) {
    getModifiers().toString(s);
    s.append("enum " + name());
    if(getNumImplements() > 0) {
      s.append(" implements ");
      getImplements(0).toString(s);
      for(int i = 1; i < getNumImplements(); i++) {
        s.append(", ");
        getImplements(i).toString(s);
      }
    }
    s.append(" {");
    for(int i=0; i < getNumBodyDecl(); i++) {
      BodyDecl d = getBodyDecl(i);
      if(d instanceof EnumConstant) {
        d.toString(s);
        if(i + 1 < getNumBodyDecl() && !(getBodyDecl(i + 1) instanceof EnumConstant))
          s.append(indent() + ";");
      }
      else if(d instanceof ConstructorDecl) {
        ConstructorDecl c = (ConstructorDecl)d;
        if(!c.isSynthetic()) {
          s.append(indent());
          c.getModifiers().toString(s);
          s.append(c.name() + "(");
          if(c.getNumParameter() > 2) {
            c.getParameter(2).toString(s);
            for(int j = 3; j < c.getNumParameter(); j++) {
              s.append(", ");
              c.getParameter(j).toString(s);
            }
          }
          s.append(")");
          if(c.getNumException() > 0) {
            s.append(" throws ");
            c.getException(0).toString(s);
            for(int j = 1; j < c.getNumException(); j++) {
              s.append(", ");
              c.getException(j).toString(s);
            }
          }
          s.append(" {");
          for(int j = 0; j < c.getBlock().getNumStmt(); j++) {
            c.getBlock().getStmt(j).toString(s);
          }
          s.append(indent());
          s.append("}");
        }
      }
      else if(d instanceof MethodDecl) {
        MethodDecl m = (MethodDecl)d;
        if(!m.isSynthetic())
          m.toString(s);
      }
      else if(d instanceof FieldDeclaration) {
        FieldDeclaration f = (FieldDeclaration)d;
        if(!f.isSynthetic())
          f.toString(s);
      }
      else
        d.toString(s);
    }
    s.append(indent() + "}");
  }
  /**
   * Check that the enum does not contain unimplemented abstract methods.
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:688
   */
  public void checkModifiers() {
    super.checkModifiers();
    if (!unimplementedMethods().isEmpty()) {
      StringBuffer s = new StringBuffer();
      s.append("" + name() + " lacks implementations in one or more " +
    "enum constants for the following methods:\n");
      for (Iterator iter = unimplementedMethods().iterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        s.append("  " + m.signature() + " in " + m.hostType().typeName() + "\n");
      }
      error(s.toString());
    }
  }
  /**
   * @ast method 
   * @declaredat Enums.ast:1
   */
  public EnumDecl() {
    super();

    setChild(new List(), 1);
    setChild(new List(), 2);
    setChild(new Opt(), 3);

  }
  /**
   * @ast method 
   * @declaredat Enums.ast:10
   */
  public EnumDecl(Modifiers p0, String p1, List<Access> p2, List<BodyDecl> p3) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
    setChild(new Opt(), 3);
  }
  /**
   * @ast method 
   * @declaredat Enums.ast:17
   */
  public EnumDecl(Modifiers p0, beaver.Symbol p1, List<Access> p2, List<BodyDecl> p3) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
    setChild(new Opt(), 3);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:27
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Enums.ast:33
   */
  public boolean mayHaveRewrite() {
    return true;
  }
  /**
   * Setter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:5
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Getter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:12
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:18
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Setter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:5
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * @ast method 
   * @declaredat Enums.ast:8
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
   * @declaredat Enums.ast:19
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:5
   */
  public void setImplementsList(List<Access> list) {
    setChild(list, 1);
  }
  /**
   * @return number of children in ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:12
   */
  public int getNumImplements() {
    return getImplementsList().getNumChild();
  }
  /**
   * Getter for child in list ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getImplements(int i) {
    return (Access)getImplementsList().getChild(i);
  }
  /**
   * Add element to list ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:27
   */
  public void addImplements(Access node) {
    List<Access> list = (parent == null || state == null) ? getImplementsListNoTransform() : getImplementsList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:34
   */
  public void addImplementsNoTransform(Access node) {
    List<Access> list = getImplementsListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:42
   */
  public void setImplements(Access node, int i) {
    List<Access> list = getImplementsList();
    list.setChild(node, i);
  }
  /**
   * Getter for Implements list.
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:50
   */
  public List<Access> getImplementss() {
    return getImplementsList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:56
   */
  public List<Access> getImplementssNoTransform() {
    return getImplementsListNoTransform();
  }
  /**
   * Getter for list ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getImplementsList() {
    List<Access> list = (List<Access>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getImplementsListNoTransform() {
    return (List<Access>)getChildNoTransform(1);
  }
  /**
   * Setter for BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:5
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 2);
  }
  /**
   * @return number of children in BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:12
   */
  public int getNumBodyDecl() {
    return getBodyDeclList().getNumChild();
  }
  /**
   * Getter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl getBodyDecl(int i) {
    return (BodyDecl)getBodyDeclList().getChild(i);
  }
  /**
   * Add element to list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:27
   */
  public void addBodyDecl(BodyDecl node) {
    List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:34
   */
  public void addBodyDeclNoTransform(BodyDecl node) {
    List<BodyDecl> list = getBodyDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:42
   */
  public void setBodyDecl(BodyDecl node, int i) {
    List<BodyDecl> list = getBodyDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:50
   */
  public List<BodyDecl> getBodyDecls() {
    return getBodyDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:56
   */
  public List<BodyDecl> getBodyDeclsNoTransform() {
    return getBodyDeclListNoTransform();
  }
  /**
   * Getter for list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclList() {
    List<BodyDecl> list = (List<BodyDecl>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclListNoTransform() {
    return (List<BodyDecl>)getChildNoTransform(2);
  }
  /**
   * Setter for SuperClassAccessOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:5
   */
  public void setSuperClassAccessOpt(Opt<Access> opt) {
    setChild(opt, 3);
  }
  /**
   * Does this node have a SuperClassAccess child?
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:12
   */
  public boolean hasSuperClassAccess() {
    return getSuperClassAccessOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child SuperClassAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getSuperClassAccess() {
    return (Access)getSuperClassAccessOpt().getChild(0);
  }
  /**
   * Setter for optional child SuperClassAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:27
   */
  public void setSuperClassAccess(Access node) {
    getSuperClassAccessOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:33
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Access> getSuperClassAccessOptNoTransform() {
    return (Opt<Access>)getChildNoTransform(3);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:40
   */
  protected int getSuperClassAccessOptChildPosition() {
    return 3;
  }
  /* It is a compile-time error if the return type of a method declared in an
  annotation type is any type other than one of the following: one of the
  primitive types, String, Class and any invocation of Class, an enum type
  (\u00df8.9), an annotation type, or an array (\u00df10) of one of the preceding types.* @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:132
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isValidAnnotationMethodReturnType() {
      ASTNode$State state = state();
    boolean isValidAnnotationMethodReturnType_value = isValidAnnotationMethodReturnType_compute();
    return isValidAnnotationMethodReturnType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isValidAnnotationMethodReturnType_compute() {  return true;  }
  /* 
     1) It is a compile-time error to attempt to explicitly instantiate an enum type
     (\u00d4\u00f8\u03a915.9.1).
  * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:17
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isEnumDecl() {
      ASTNode$State state = state();
    boolean isEnumDecl_value = isEnumDecl_compute();
    return isEnumDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isEnumDecl_compute() {  return true;  }
  /**
   * @apilevel internal
   */
  protected boolean isStatic_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isStatic_value;
  /*
    9) Nested enum types are implicitly static. It is permissable to explicitly
    declare a nested enum type to be static.
  * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:39
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isStatic() {
    if(isStatic_computed) {
      return isStatic_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isStatic_value = isStatic_compute();
if(isFinal && num == state().boundariesCrossed) isStatic_computed = true;
    return isStatic_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isStatic_compute() {  return isNestedType();  }
  /**
   * @apilevel internal
   */
  protected boolean getSuperClassAccessOpt_computed = false;
  /**
   * @apilevel internal
   */
  protected Opt getSuperClassAccessOpt_value;
  /*
    10) The direct superclass of an enum type named E is Enum<E>. 
  * @attribute syn nta
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:60
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt getSuperClassAccessOpt() {
    if(getSuperClassAccessOpt_computed) {
      return (Opt)ASTNode.getChild(this, getSuperClassAccessOptChildPosition());
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getSuperClassAccessOpt_value = getSuperClassAccessOpt_compute();
    setSuperClassAccessOpt(getSuperClassAccessOpt_value);
if(isFinal && num == state().boundariesCrossed) getSuperClassAccessOpt_computed = true;
    return (Opt)ASTNode.getChild(this, getSuperClassAccessOptChildPosition());
  }
  /**
   * @apilevel internal
   */
  private Opt getSuperClassAccessOpt_compute() {
    return new Opt(
      new ParTypeAccess(
        new TypeAccess(
          "java.lang",
          "Enum"
        ),
        new List().add(createQualifiedAccess())
      )
    );
  }
  /*
    7) It is a compile-time error for the class body of an enum constant to declare
    an abstract method.

    TODO: work on error messages
  * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:283
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
  private boolean isFinal_compute() {
    for(Iterator iter = enumConstants().iterator(); iter.hasNext(); ) {
      EnumConstant c = (EnumConstant)iter.next();
      ClassInstanceExpr e = (ClassInstanceExpr)c.getInit();
      if(e.hasTypeDecl())
        return false;
    }
    return true;
  }
  /**
   * @apilevel internal
   */
  protected boolean enumConstants_computed = false;
  /**
   * @apilevel internal
   */
  protected ArrayList enumConstants_value;
  /**
   * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:294
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayList enumConstants() {
    if(enumConstants_computed) {
      return enumConstants_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    enumConstants_value = enumConstants_compute();
if(isFinal && num == state().boundariesCrossed) enumConstants_computed = true;
    return enumConstants_value;
  }
  /**
   * @apilevel internal
   */
  private ArrayList enumConstants_compute() {
    ArrayList list = new ArrayList();
    for(int i = 0; i < getNumBodyDecl(); i++)
      if(getBodyDecl(i).isEnumConstant())
        list.add(getBodyDecl(i));
    return list;
  }
  /*
    14) It is a compile-time error to reference a static field of an enum type that
    is not a compile-time constant (\u00d4\u00f8\u03a915.28) from constructors, instance
    initializer blocks, or instance variable initializer expressions of that
    type.
  * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:433
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
  private boolean isAbstract_compute() {
    for (int i = 0; i < getNumBodyDecl(); i++) {
      if (getBodyDecl(i) instanceof MethodDecl) {
        MethodDecl m = (MethodDecl)getBodyDecl(i);
        if (m.isAbstract())
          return true;
      }
    }
    return false;
  }
  /**
   * @apilevel internal
   */
  protected boolean unimplementedMethods_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection unimplementedMethods_value;
  /**
   * From the Java Language Specification, third edition, section 8.9 Enums:
   *
   * It is a compile-time error for an enum type E to have an abstract method
   * m as a member unless E has one or more enum constants, and all of E's enum
   * constants have class bodies that provide concrete implementations of m.
   * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:636
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection unimplementedMethods() {
    if(unimplementedMethods_computed) {
      return unimplementedMethods_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    unimplementedMethods_value = unimplementedMethods_compute();
if(isFinal && num == state().boundariesCrossed) unimplementedMethods_computed = true;
    return unimplementedMethods_value;
  }
  /**
   * @apilevel internal
   */
  private Collection unimplementedMethods_compute() {
    Collection<MethodDecl> methods = new LinkedList<MethodDecl>();
    for (Iterator iter = interfacesMethodsIterator(); iter.hasNext(); ) {
      MethodDecl method = (MethodDecl)iter.next();
      SimpleSet set = (SimpleSet)localMethodsSignature(method.signature());
      if (set.size() == 1) {
        MethodDecl n = (MethodDecl)set.iterator().next();
        if (!n.isAbstract()) 
    continue;
      }
      boolean implemented = false;
      set = (SimpleSet)ancestorMethods(method.signature());
      for (Iterator i2 = set.iterator(); i2.hasNext(); ) {
        MethodDecl n = (MethodDecl)i2.next();
        if (!n.isAbstract()) {
          implemented = true;
    break;
  }
      }
      if (!implemented)
  methods.add(method);
    }

    for (Iterator iter = localMethodsIterator(); iter.hasNext(); ) {
      MethodDecl method = (MethodDecl)iter.next();
      if (method.isAbstract())
        methods.add(method);
    }

    Collection unimplemented = new ArrayList();
    for (MethodDecl method : methods) {
      if (enumConstants().isEmpty()) {
  unimplemented.add(method);
  continue;
      }
      boolean missing = false;
      for (Iterator iter = enumConstants().iterator(); iter.hasNext(); ) {
  if (!((EnumConstant) iter.next()).implementsMethod(method)) {
    missing = true;
    break;
        }
      }
      if (missing)
  unimplemented.add(method);
    }

    return unimplemented;
  }
  /**
   * @attribute syn
   * @aspect EnumsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:13
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
  private int sootTypeModifiers_compute() {  return super.sootTypeModifiers() | Modifiers.ACC_ENUM;  }
  /**
   * @attribute inh
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:421
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeString() {
      ASTNode$State state = state();
    TypeDecl typeString_value = getParent().Define_TypeDecl_typeString(this, null);
    return typeString_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:33
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return false;
    }
    return super.Define_boolean_mayBeAbstract(caller, child);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:40
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return isNestedType();
    }
    return super.Define_boolean_mayBeStatic(caller, child);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:292
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return false;
    }
    return super.Define_boolean_mayBeFinal(caller, child);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag at line 88
    if(!done()) {
      state().duringEnums++;
      ASTNode result = rewriteRule0();
      state().duringEnums--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:88
   * @apilevel internal
   */  private EnumDecl rewriteRule0() {
{
      if(noConstructor()) {
        List parameterList = new List();
        parameterList.add(
          new ParameterDeclaration(new TypeAccess("java.lang", "String"), "p0")
        );
        parameterList.add(
          new ParameterDeclaration(new TypeAccess("int"), "p1")
        );
        addBodyDecl(
          new ConstructorDecl(
            new Modifiers(new List().add(
              new Modifier("private")).add(
        new Modifier("synthetic"))
            ),
            name(),
            parameterList,
            new List(),
            new Opt(
              new ExprStmt(
                new SuperConstructorAccess(
                  "super",
                  new List().add(
                    new VarAccess("p0")
                  ).add(
                    new VarAccess("p1")
                  )
                )
              )
            ),
            new Block(new List())
          )
        );
      }
      else {
        transformEnumConstructors();
      }
      addValues(); // Add the values() and getValue(String s) methods
      return this;
    }  }
}
