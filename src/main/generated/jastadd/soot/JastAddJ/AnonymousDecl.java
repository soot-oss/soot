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
 * @production AnonymousDecl : {@link ClassDecl} ::= <span class="component">{@link Modifiers}</span> <span class="component">&lt;ID:String&gt;</span> <span class="component">[SuperClassAccess:{@link Access}]</span> <span class="component">Implements:{@link Access}*</span> <span class="component">{@link BodyDecl}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:70
 */
public class AnonymousDecl extends ClassDecl implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isCircular_visited = -1;
    isCircular_computed = false;
    isCircular_initialized = false;
    getSuperClassAccessOpt_computed = false;
    getSuperClassAccessOpt_value = null;
    getImplementsList_computed = false;
    getImplementsList_value = null;
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
  public AnonymousDecl clone() throws CloneNotSupportedException {
    AnonymousDecl node = (AnonymousDecl)super.clone();
    node.isCircular_visited = -1;
    node.isCircular_computed = false;
    node.isCircular_initialized = false;
    node.getSuperClassAccessOpt_computed = false;
    node.getSuperClassAccessOpt_value = null;
    node.getImplementsList_computed = false;
    node.getImplementsList_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public AnonymousDecl copy() {
    try {
      AnonymousDecl node = (AnonymousDecl) clone();
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
  public AnonymousDecl fullCopy() {
    AnonymousDecl tree = (AnonymousDecl) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
          switch (i) {
          case 3:
            tree.children[i] = new Opt();
            continue;
          case 4:
            tree.children[i] = new List();
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
   * 
   */
  public AnonymousDecl() {
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
    setChild(new Opt(), 2);
    setChild(new List(), 3);
  }
  /**
   * @ast method 
   * 
   */
  public AnonymousDecl(Modifiers p0, String p1, List<BodyDecl> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * 
   */
  public AnonymousDecl(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 2;
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
   * Replaces the BodyDecl list.
   * @param list The new list node to be used as the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 1);
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
    List<BodyDecl> list = (List<BodyDecl>)getChild(1);
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
    return (List<BodyDecl>)getChildNoTransform(1);
  }
  /**
   * Replaces the optional node for the SuperClassAccess child. This is the {@code Opt} node containing the child SuperClassAccess, not the actual child!
   * @param opt The new node to be used as the optional node for the SuperClassAccess child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setSuperClassAccessOpt(Opt<Access> opt) {
    setChild(opt, 2);
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
    return (Opt<Access>)getChildNoTransform(2);
  }
  /**
   * Retrieves the child position of the optional child SuperClassAccess.
   * @return The the child position of the optional child SuperClassAccess.
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int getSuperClassAccessOptChildPosition() {
    return 2;
  }
  /**
   * Replaces the Implements list.
   * @param list The new list node to be used as the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setImplementsList(List<Access> list) {
    setChild(list, 3);
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
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Implements list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Access> getImplementsListNoTransform() {
    return (List<Access>)getChildNoTransform(3);
  }
  /**
   * Retrieves the child position of the Implements list.
   * @return The the child position of the Implements list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int getImplementsListChildPosition() {
    return 3;
  }
  /**
   * @ast method 
   * @aspect VariableArityParameters
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:107
   */
   
  protected List constructorParameterList(ConstructorDecl decl) {
    List parameterList = new List();
    for(int i = 0; i < decl.getNumParameter(); i++) {
      ParameterDeclaration param = decl.getParameter(i);
      if (param instanceof VariableArityParameterDeclaration) {
        parameterList.add(
            new VariableArityParameterDeclaration(
              new Modifiers(new List()),
              ((ArrayDecl) param.type()).componentType().createBoundAccess(),
              param.name()
              ));
      } else {
        parameterList.add(
            new ParameterDeclaration(
              param.type().createBoundAccess(),
              param.name()
              ));
      }
    }

    return parameterList;
  }
  /**
   * @apilevel internal
   */
  protected int isCircular_visited = -1;
  /**
   * @apilevel internal
   */
  protected boolean isCircular_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isCircular_initialized = false;
  /**
   * @apilevel internal
   */
  protected boolean isCircular_value;
  /**
   * @attribute syn
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:30
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isCircular() {
    if(isCircular_computed) {
      return isCircular_value;
    }
    ASTNode$State state = state();
    if (!isCircular_initialized) {
      isCircular_initialized = true;
      isCircular_value = true;
    }
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
    int num = state.boundariesCrossed;
    boolean isFinal = this.is$Final();
      do {
        isCircular_visited = state.CIRCLE_INDEX;
        state.CHANGE = false;
        boolean new_isCircular_value = isCircular_compute();
        if (new_isCircular_value!=isCircular_value)
          state.CHANGE = true;
        isCircular_value = new_isCircular_value; 
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
        if(isFinal && num == state().boundariesCrossed) {
      isCircular_computed = true;
      }
      else {
      state.RESET_CYCLE = true;
      isCircular_compute();
      state.RESET_CYCLE = false;
        isCircular_computed = false;
        isCircular_initialized = false;
      }
      state.IN_CIRCLE = false; 
      return isCircular_value;
    }
    if(isCircular_visited != state.CIRCLE_INDEX) {
      isCircular_visited = state.CIRCLE_INDEX;
      if (state.RESET_CYCLE) {
        isCircular_computed = false;
        isCircular_initialized = false;
        isCircular_visited = -1;
        return isCircular_value;
      }
      boolean new_isCircular_value = isCircular_compute();
      if (new_isCircular_value!=isCircular_value)
        state.CHANGE = true;
      isCircular_value = new_isCircular_value; 
      return isCircular_value;
    }
    return isCircular_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isCircular_compute() {  return false;  }
  /**
   * @apilevel internal
   */
  protected boolean getSuperClassAccessOpt_computed = false;
  /**
   * @apilevel internal
   */
  protected Opt getSuperClassAccessOpt_value;
  /**
   * @attribute syn nta
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:32
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
    if(superType().isInterfaceDecl())
      return new Opt(typeObject().createQualifiedAccess());
    else
      return new Opt(superType().createBoundAccess());
  }
  /**
   * @apilevel internal
   */
  protected boolean getImplementsList_computed = false;
  /**
   * @apilevel internal
   */
  protected List getImplementsList_value;
  /**
   * @attribute syn nta
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:38
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List getImplementsList() {
    if(getImplementsList_computed) {
      return (List) getChild(getImplementsListChildPosition());
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getImplementsList_value = getImplementsList_compute();
    setImplementsList(getImplementsList_value);
      if(isFinal && num == state().boundariesCrossed) getImplementsList_computed = true;
    return (List) getChild(getImplementsListChildPosition());
  }
  /**
   * @apilevel internal
   */
  private List getImplementsList_compute() {
    if(superType().isInterfaceDecl())
      return new List().add(superType().createBoundAccess());
    else
      return new List();
  }
  /**
   * @attribute inh
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:14
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl superType() {
    ASTNode$State state = state();
    TypeDecl superType_value = getParent().Define_TypeDecl_superType(this, null);
    return superType_value;
  }
  /**
   * @attribute inh
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:18
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConstructorDecl constructorDecl() {
    ASTNode$State state = state();
    ConstructorDecl constructorDecl_value = getParent().Define_ConstructorDecl_constructorDecl(this, null);
    return constructorDecl_value;
  }
  /**
   * @attribute inh
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:175
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeNullPointerException() {
    ASTNode$State state = state();
    TypeDecl typeNullPointerException_value = getParent().Define_TypeDecl_typeNullPointerException(this, null);
    return typeNullPointerException_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag at line 70
    if(noConstructor()) {
      state().duringAnonymousClasses++;
      ASTNode result = rewriteRule0();
      state().duringAnonymousClasses--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:70
   * @apilevel internal
   */  private AnonymousDecl rewriteRule0() {
{
      setModifiers(new Modifiers(new List().add(new Modifier("final"))));

      ConstructorDecl decl = constructorDecl();
      Modifiers modifiers = (Modifiers)decl.getModifiers().fullCopy();
      String anonName = "Anonymous" + nextAnonymousIndex();

      ConstructorDecl constructor = new ConstructorDecl(modifiers, anonName,
          constructorParameterList(decl), new List(), new Opt(), new Block());
      constructor.setDefaultConstructor();
      addBodyDecl(constructor);

      setID(anonName);

      List argList = new List();
      for(int i = 0; i < constructor.getNumParameter(); i++) {
        argList.add(new VarAccess(constructor.getParameter(i).name()));
      }

      constructor.setConstructorInvocation(
        new ExprStmt(
          new SuperConstructorAccess("super", argList)
        )
      );

      HashSet set = new HashSet();
      for(int i = 0; i < getNumBodyDecl(); i++) {
        if(getBodyDecl(i) instanceof InstanceInitializer) {
          InstanceInitializer init = (InstanceInitializer)getBodyDecl(i);
          set.addAll(init.exceptions());
        }
        else if(getBodyDecl(i) instanceof FieldDeclaration) {
          FieldDeclaration f = (FieldDeclaration)getBodyDecl(i);
          if(f.isInstanceVariable()) {
            set.addAll(f.exceptions());
          }
        }
      }
      List exceptionList = new List();
      for(Iterator iter = set.iterator(); iter.hasNext(); ) {
        TypeDecl exceptionType = (TypeDecl)iter.next();
        if(exceptionType.isNull())
          exceptionType = typeNullPointerException();
        exceptionList.add(exceptionType.createQualifiedAccess());
      }
      constructor.setExceptionList(exceptionList);
      return this;
      /*
      setModifiers(new Modifiers(new List().add(new Modifier("final"))));
      
      ConstructorDecl constructor = new ConstructorDecl();
      addBodyDecl(constructor);

      constructor.setModifiers((Modifiers)constructorDecl().getModifiers().fullCopy());
      String name = "Anonymous" + nextAnonymousIndex();
      setID(name);
      constructor.setID(name);

      List parameterList = new List();
      for(int i = 0; i < constructorDecl().getNumParameter(); i++) {
        parameterList.add(
          new ParameterDeclaration(
            constructorDecl().getParameter(i).type().createBoundAccess(),
            constructorDecl().getParameter(i).name()
          )
        );
      }
      constructor.setParameterList(parameterList);
      
      List argList = new List();
      for(int i = 0; i < constructor.getNumParameter(); i++)
        argList.add(new VarAccess(constructor.getParameter(i).name()));
      constructor.setConstructorInvocation(
        new ExprStmt(
          new SuperConstructorAccess("super", argList)
        )
      );
      constructor.setBlock(new Block());

      HashSet set = new HashSet();
      for(int i = 0; i < getNumBodyDecl(); i++) {
        if(getBodyDecl(i) instanceof InstanceInitializer) {
          InstanceInitializer init = (InstanceInitializer)getBodyDecl(i);
          set.addAll(init.exceptions());
        }
        else if(getBodyDecl(i) instanceof FieldDeclaration) {
          FieldDeclaration f = (FieldDeclaration)getBodyDecl(i);
          if(f.isInstanceVariable()) {
            set.addAll(f.exceptions());
          }
        }
      }
      List exceptionList = new List();
      for(Iterator iter = set.iterator(); iter.hasNext(); ) {
        TypeDecl exceptionType = (TypeDecl)iter.next();
        if(exceptionType.isNull())
          exceptionType = typeNullPointerException();
        exceptionList.add(exceptionType.createQualifiedAccess());
      }
      constructor.setExceptionList(exceptionList);
      return this;
      */
    }  }
}
