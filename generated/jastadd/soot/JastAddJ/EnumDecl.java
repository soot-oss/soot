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
 * @production EnumDecl : {@link ClassDecl} ::= <span class="component">{@link Modifiers}</span> <span class="component">&lt;ID:String&gt;</span> <span class="component">[SuperClassAccess:{@link Access}]</span> <span class="component">Implements:{@link Access}*</span> <span class="component">{@link BodyDecl}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.ast:1
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
      EnumDecl node = (EnumDecl) clone();
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
  public EnumDecl fullCopy() {
    EnumDecl tree = (EnumDecl) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
          switch (i) {
          case 4:
            tree.children[i] = new Opt();
            continue;
          }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:527
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:675
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
   * 
   */
  public EnumDecl() {
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
    children = new ASTNode[4];
    setChild(new List(), 1);
    setChild(new List(), 2);
    setChild(new Opt(), 3);
  }
  /**
   * @ast method 
   * 
   */
  public EnumDecl(Modifiers p0, String p1, List<Access> p2, List<BodyDecl> p3) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
  }
  /**
   * @ast method 
   * 
   */
  public EnumDecl(Modifiers p0, beaver.Symbol p1, List<Access> p2, List<BodyDecl> p3) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return true;
  }
  /**
   * Replaces the Modifiers child.
   * @param node The new node to replace the Modifiers child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Modifiers child.
   * @return The current node used as the Modifiers child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * Retrieves the Modifiers child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Modifiers child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
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
   * Replaces the Implements list.
   * @param list The new list node to be used as the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setImplementsList(List<Access> list) {
    setChild(list, 1);
  }
  /**
   * Retrieves the number of children in the Implements list.
   * @return Number of children in the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumImplements() {
    return getImplementsList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Implements list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Implements list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumImplementsNoTransform() {
    return getImplementsListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Implements list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getImplements(int i) {
    return (Access)getImplementsList().getChild(i);
  }
  /**
   * Append an element to the Implements list.
   * @param node The element to append to the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addImplements(Access node) {
    List<Access> list = (parent == null || state == null) ? getImplementsListNoTransform() : getImplementsList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addImplementsNoTransform(Access node) {
    List<Access> list = getImplementsListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Implements list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setImplements(Access node, int i) {
    List<Access> list = getImplementsList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Implements list.
   * @return The node representing the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Access> getImplementss() {
    return getImplementsList();
  }
  /**
   * Retrieves the Implements list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Implements list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Access> getImplementssNoTransform() {
    return getImplementsListNoTransform();
  }
  /**
   * Retrieves the Implements list.
   * @return The node representing the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getImplementsList() {
    List<Access> list = (List<Access>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the Implements list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Implements list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getImplementsListNoTransform() {
    return (List<Access>)getChildNoTransform(1);
  }
  /**
   * Replaces the BodyDecl list.
   * @param list The new list node to be used as the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 2);
  }
  /**
   * Retrieves the number of children in the BodyDecl list.
   * @return Number of children in the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumBodyDecl() {
    return getBodyDeclList().getNumChild();
  }
  /**
   * Retrieves the number of children in the BodyDecl list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the BodyDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumBodyDeclNoTransform() {
    return getBodyDeclListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the BodyDecl list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl getBodyDecl(int i) {
    return (BodyDecl)getBodyDeclList().getChild(i);
  }
  /**
   * Append an element to the BodyDecl list.
   * @param node The element to append to the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addBodyDecl(BodyDecl node) {
    List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addBodyDeclNoTransform(BodyDecl node) {
    List<BodyDecl> list = getBodyDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the BodyDecl list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBodyDecl(BodyDecl node, int i) {
    List<BodyDecl> list = getBodyDeclList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the BodyDecl list.
   * @return The node representing the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<BodyDecl> getBodyDecls() {
    return getBodyDeclList();
  }
  /**
   * Retrieves the BodyDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the BodyDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<BodyDecl> getBodyDeclsNoTransform() {
    return getBodyDeclListNoTransform();
  }
  /**
   * Retrieves the BodyDecl list.
   * @return The node representing the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclList() {
    List<BodyDecl> list = (List<BodyDecl>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the BodyDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the BodyDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclListNoTransform() {
    return (List<BodyDecl>)getChildNoTransform(2);
  }
  /**
   * Replaces the optional node for the SuperClassAccess child. This is the {@code Opt} node containing the child SuperClassAccess, not the actual child!
   * @param opt The new node to be used as the optional node for the SuperClassAccess child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setSuperClassAccessOpt(Opt<Access> opt) {
    setChild(opt, 3);
  }
  /**
   * Check whether the optional SuperClassAccess child exists.
   * @return {@code true} if the optional SuperClassAccess child exists, {@code false} if it does not.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public boolean hasSuperClassAccess() {
    return getSuperClassAccessOpt().getNumChild() != 0;
  }
  /**
   * Retrieves the (optional) SuperClassAccess child.
   * @return The SuperClassAccess child, if it exists. Returns {@code null} otherwise.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getSuperClassAccess() {
    return (Access)getSuperClassAccessOpt().getChild(0);
  }
  /**
   * Replaces the (optional) SuperClassAccess child.
   * @param node The new node to be used as the SuperClassAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setSuperClassAccess(Access node) {
    getSuperClassAccessOpt().setChild(node, 0);
  }
  /**
   * Retrieves the optional node for child SuperClassAccess. This is the {@code Opt} node containing the child SuperClassAccess, not the actual child!
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The optional node for child SuperClassAccess.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Access> getSuperClassAccessOptNoTransform() {
    return (Opt<Access>)getChildNoTransform(3);
  }
  /**
   * Retrieves the child position of the optional child SuperClassAccess.
   * @return The the child position of the optional child SuperClassAccess.
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int getSuperClassAccessOptChildPosition() {
    return 3;
  }
  /* It is a compile-time error if the return type of a method declared in an
  annotation type is any type other than one of the following: one of the
  primitive types, String, Class and any invocation of Class, an enum type
  (\ufffd8.9), an annotation type, or an array (\ufffd10) of one of the preceding types.* @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:121
   */
  public boolean isValidAnnotationMethodReturnType() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /* 
     1) It is a compile-time error to attempt to explicitly instantiate an enum type
     (\ufffd\ufffd\ufffd15.9.1).
  * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:16
   */
  public boolean isEnumDecl() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
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
      return (Opt) getChild(getSuperClassAccessOptChildPosition());
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getSuperClassAccessOpt_value = getSuperClassAccessOpt_compute();
    setSuperClassAccessOpt(getSuperClassAccessOpt_value);
      if(isFinal && num == state().boundariesCrossed) getSuperClassAccessOpt_computed = true;
    return (Opt) getChild(getSuperClassAccessOptChildPosition());
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
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:209
   */
  public boolean isFinal() {
    ASTNode$State state = state();
    try {
    for(Iterator iter = enumConstants().iterator(); iter.hasNext(); ) {
      EnumConstant c = (EnumConstant)iter.next();
      ClassInstanceExpr e = (ClassInstanceExpr)c.getInit();
      if(e.hasTypeDecl())
        return false;
    }
    return true;
  }
    finally {
    }
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
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:204
   */
  public boolean isAbstract() {
    ASTNode$State state = state();
    try {
    for (int i = 0; i < getNumBodyDecl(); i++) {
      if (getBodyDecl(i) instanceof MethodDecl) {
        MethodDecl m = (MethodDecl)getBodyDecl(i);
        if (m.isAbstract())
          return true;
      }
    }
    return false;
  }
    finally {
    }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:623
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
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:90
   */
  public int sootTypeModifiers() {
    ASTNode$State state = state();
    try {  return super.sootTypeModifiers() | Modifiers.ACC_ENUM;  }
    finally {
    }
  }
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
    else {      return super.Define_boolean_mayBeAbstract(caller, child);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:40
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return isNestedType();
    }
    else {      return super.Define_boolean_mayBeStatic(caller, child);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:292
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return false;
    }
    else {      return super.Define_boolean_mayBeFinal(caller, child);
    }
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
