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
 * @declaredat java.ast:67
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
        AnonymousDecl node = (AnonymousDecl)clone();
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
  public AnonymousDecl fullCopy() {
    AnonymousDecl res = (AnonymousDecl)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public AnonymousDecl() {
    super();

    setChild(new List(), 1);
    setChild(new Opt(), 2);
    setChild(new List(), 3);

  }
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  public AnonymousDecl(Modifiers p0, String p1, List<BodyDecl> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(new Opt(), 2);
    setChild(new List(), 3);
  }
  /**
   * @ast method 
   * @declaredat java.ast:17
   */
  public AnonymousDecl(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(new Opt(), 2);
    setChild(new List(), 3);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:27
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:33
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
  /**
   * @ast method 
   * @declaredat java.ast:8
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
   * @declaredat java.ast:19
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 1);
  }
  /**
   * @return number of children in BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumBodyDecl() {
    return getBodyDeclList().getNumChild();
  }
  /**
   * Getter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl getBodyDecl(int i) {
    return (BodyDecl)getBodyDeclList().getChild(i);
  }
  /**
   * Add element to list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addBodyDecl(BodyDecl node) {
    List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addBodyDeclNoTransform(BodyDecl node) {
    List<BodyDecl> list = getBodyDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setBodyDecl(BodyDecl node, int i) {
    List<BodyDecl> list = getBodyDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<BodyDecl> getBodyDecls() {
    return getBodyDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<BodyDecl> getBodyDeclsNoTransform() {
    return getBodyDeclListNoTransform();
  }
  /**
   * Getter for list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclList() {
    List<BodyDecl> list = (List<BodyDecl>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclListNoTransform() {
    return (List<BodyDecl>)getChildNoTransform(1);
  }
  /**
   * Setter for SuperClassAccessOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setSuperClassAccessOpt(Opt<Access> opt) {
    setChild(opt, 2);
  }
  /**
   * Does this node have a SuperClassAccess child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasSuperClassAccess() {
    return getSuperClassAccessOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child SuperClassAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getSuperClassAccess() {
    return (Access)getSuperClassAccessOpt().getChild(0);
  }
  /**
   * Setter for optional child SuperClassAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setSuperClassAccess(Access node) {
    getSuperClassAccessOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:33
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Access> getSuperClassAccessOptNoTransform() {
    return (Opt<Access>)getChildNoTransform(2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:40
   */
  protected int getSuperClassAccessOptChildPosition() {
    return 2;
  }
  /**
   * Setter for ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setImplementsList(List<Access> list) {
    setChild(list, 3);
  }
  /**
   * @return number of children in ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumImplements() {
    return getImplementsList().getNumChild();
  }
  /**
   * Getter for child in list ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getImplements(int i) {
    return (Access)getImplementsList().getChild(i);
  }
  /**
   * Add element to list ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addImplements(Access node) {
    List<Access> list = (parent == null || state == null) ? getImplementsListNoTransform() : getImplementsList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addImplementsNoTransform(Access node) {
    List<Access> list = getImplementsListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ImplementsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setImplements(Access node, int i) {
    List<Access> list = getImplementsList();
    list.setChild(node, i);
  }
  /**
   * Getter for Implements list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<Access> getImplementss() {
    return getImplementsList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<Access> getImplementssNoTransform() {
    return getImplementsListNoTransform();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:62
   */
  public List<Access> getImplementsListNoTransform() {
    return (List<Access>)getChildNoTransform(3);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:68
   */
  protected int getImplementsListChildPosition() {
    return 3;
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
      return (List)ASTNode.getChild(this, getImplementsListChildPosition());
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getImplementsList_value = getImplementsList_compute();
    setImplementsList(getImplementsList_value);
if(isFinal && num == state().boundariesCrossed) getImplementsList_computed = true;
    return (List)ASTNode.getChild(this, getImplementsListChildPosition());
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:165
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
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag at line 52
    if(noConstructor()) {
      state().duringAnonymousClasses++;
      ASTNode result = rewriteRule0();
      state().duringAnonymousClasses--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:52
   * @apilevel internal
   */  private AnonymousDecl rewriteRule0() {
{
            setModifiers(new Modifiers(new List().add(new Modifier("final"))));
      
      ConstructorDecl decl = constructorDecl();
      Modifiers modifiers = (Modifiers)decl.getModifiers().fullCopy();
      String name = "Anonymous" + nextAnonymousIndex();

      List parameterList = new List();
      for(int i = 0; i < decl.getNumParameter(); i++) {
        parameterList.add(
          new ParameterDeclaration(
            decl.getParameter(i).type().createBoundAccess(),
            decl.getParameter(i).name()
          )
        );
      }
      
      ConstructorDecl constructor = new ConstructorDecl(modifiers, name,
		      parameterList, new List(), new Opt(), new Block());  
      constructor.setDefaultConstructor();
      addBodyDecl(constructor);

      setID(name);
      
      List argList = new List();
      for(int i = 0; i < constructor.getNumParameter(); i++)
        argList.add(new VarAccess(constructor.getParameter(i).name()));
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
