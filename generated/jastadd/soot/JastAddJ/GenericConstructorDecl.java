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
 * @declaredat GenericMethods.ast:2
 */
public class GenericConstructorDecl extends ConstructorDecl implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    getParConstructorDeclList_computed = false;
    getParConstructorDeclList_value = null;
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
  public GenericConstructorDecl clone() throws CloneNotSupportedException {
    GenericConstructorDecl node = (GenericConstructorDecl)super.clone();
    node.getParConstructorDeclList_computed = false;
    node.getParConstructorDeclList_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GenericConstructorDecl copy() {
      try {
        GenericConstructorDecl node = (GenericConstructorDecl)clone();
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
  public GenericConstructorDecl fullCopy() {
    GenericConstructorDecl res = (GenericConstructorDecl)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect GenericMethodsPrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:212
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);

    s.append(" <");
    for(int i = 0; i < getNumTypeParameter(); i++) {
      if(i != 0) s.append(", ");
      original().getTypeParameter(i).toString(s);
    }
    s.append("> ");

    s.append(getID() + "(");
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
      s.append(indent());
      getConstructorInvocation().toString(s);
    }
    for(int i = 0; i < getBlock().getNumStmt(); i++) {
      s.append(indent());
      getBlock().getStmt(i).toString(s);
    }
    s.append(indent());
    s.append("}");
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1043
   */
  
  public GenericConstructorDecl original;
  /**
   * @ast method 
   * @declaredat GenericMethods.ast:1
   */
  public GenericConstructorDecl() {
    super();

    setChild(new List(), 1);
    setChild(new List(), 2);
    setChild(new Opt(), 3);
    setChild(new List(), 5);
    setChild(new List(), 6);

  }
  /**
   * @ast method 
   * @declaredat GenericMethods.ast:12
   */
  public GenericConstructorDecl(Modifiers p0, String p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5, List<TypeVariable> p6) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
    setChild(p4, 3);
    setChild(p5, 4);
    setChild(p6, 5);
    setChild(new List(), 6);
  }
  /**
   * @ast method 
   * @declaredat GenericMethods.ast:22
   */
  public GenericConstructorDecl(Modifiers p0, beaver.Symbol p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5, List<TypeVariable> p6) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
    setChild(p4, 3);
    setChild(p5, 4);
    setChild(p6, 5);
    setChild(new List(), 6);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:35
   */
  protected int numChildren() {
    return 6;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat GenericMethods.ast:41
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
   * Setter for TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:5
   */
  public void setTypeParameterList(List<TypeVariable> list) {
    setChild(list, 5);
  }
  /**
   * @return number of children in TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:12
   */
  public int getNumTypeParameter() {
    return getTypeParameterList().getNumChild();
  }
  /**
   * Getter for child in list TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeVariable getTypeParameter(int i) {
    return (TypeVariable)getTypeParameterList().getChild(i);
  }
  /**
   * Add element to list TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:27
   */
  public void addTypeParameter(TypeVariable node) {
    List<TypeVariable> list = (parent == null || state == null) ? getTypeParameterListNoTransform() : getTypeParameterList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:34
   */
  public void addTypeParameterNoTransform(TypeVariable node) {
    List<TypeVariable> list = getTypeParameterListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:42
   */
  public void setTypeParameter(TypeVariable node, int i) {
    List<TypeVariable> list = getTypeParameterList();
    list.setChild(node, i);
  }
  /**
   * Getter for TypeParameter list.
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:50
   */
  public List<TypeVariable> getTypeParameters() {
    return getTypeParameterList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:56
   */
  public List<TypeVariable> getTypeParametersNoTransform() {
    return getTypeParameterListNoTransform();
  }
  /**
   * Getter for list TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<TypeVariable> getTypeParameterList() {
    List<TypeVariable> list = (List<TypeVariable>)getChild(5);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<TypeVariable> getTypeParameterListNoTransform() {
    return (List<TypeVariable>)getChildNoTransform(5);
  }
  /**
   * Setter for ParConstructorDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:5
   */
  public void setParConstructorDeclList(List<ParConstructorDecl> list) {
    setChild(list, 6);
  }
  /**
   * @return number of children in ParConstructorDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:12
   */
  public int getNumParConstructorDecl() {
    return getParConstructorDeclList().getNumChild();
  }
  /**
   * Getter for child in list ParConstructorDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParConstructorDecl getParConstructorDecl(int i) {
    return (ParConstructorDecl)getParConstructorDeclList().getChild(i);
  }
  /**
   * Add element to list ParConstructorDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:27
   */
  public void addParConstructorDecl(ParConstructorDecl node) {
    List<ParConstructorDecl> list = (parent == null || state == null) ? getParConstructorDeclListNoTransform() : getParConstructorDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:34
   */
  public void addParConstructorDeclNoTransform(ParConstructorDecl node) {
    List<ParConstructorDecl> list = getParConstructorDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ParConstructorDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:42
   */
  public void setParConstructorDecl(ParConstructorDecl node, int i) {
    List<ParConstructorDecl> list = getParConstructorDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for ParConstructorDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:50
   */
  public List<ParConstructorDecl> getParConstructorDecls() {
    return getParConstructorDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:56
   */
  public List<ParConstructorDecl> getParConstructorDeclsNoTransform() {
    return getParConstructorDeclListNoTransform();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:62
   */
  public List<ParConstructorDecl> getParConstructorDeclListNoTransform() {
    return (List<ParConstructorDecl>)getChildNoTransform(6);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:68
   */
  protected int getParConstructorDeclListChildPosition() {
    return 6;
  }
  /**
   * @apilevel internal
   */
  protected boolean getParConstructorDeclList_computed = false;
  /**
   * @apilevel internal
   */
  protected List getParConstructorDeclList_value;
  /**
   * @attribute syn nta
   * @aspect GenericMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:27
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List getParConstructorDeclList() {
    if(getParConstructorDeclList_computed) {
      return (List)ASTNode.getChild(this, getParConstructorDeclListChildPosition());
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getParConstructorDeclList_value = getParConstructorDeclList_compute();
    setParConstructorDeclList(getParConstructorDeclList_value);
if(true) getParConstructorDeclList_computed = true;
    return (List)ASTNode.getChild(this, getParConstructorDeclListChildPosition());
  }
  /**
   * @apilevel internal
   */
  private List getParConstructorDeclList_compute() {  return new List();  }
  /**
   * @attribute syn
   * @aspect GenericMethodsNameAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:117
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localLookupType(String name) {
      ASTNode$State state = state();
    SimpleSet localLookupType_String_value = localLookupType_compute(name);
    return localLookupType_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localLookupType_compute(String name) {
    for(int i = 0; i < getNumTypeParameter(); i++) {
      if(original().getTypeParameter(i).name().equals(name))
        return SimpleSet.emptySet.add(original().getTypeParameter(i));
    }
    return SimpleSet.emptySet;
  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1042
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GenericConstructorDecl original() {
      ASTNode$State state = state();
    GenericConstructorDecl original_value = original_compute();
    return original_value;
  }
  /**
   * @apilevel internal
   */
  private GenericConstructorDecl original_compute() {  return original != null ? original : this;  }
  /**
   * @attribute inh
   * @aspect GenericMethodsNameAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:116
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupType(String name) {
      ASTNode$State state = state();
    SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
    return lookupType_String_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:114
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return NameType.TYPE_NAME;
    }
    return super.Define_NameType_nameType(caller, child);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:124
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return localLookupType(name).isEmpty() ? lookupType(name) : localLookupType(name);
    }
    return getParent().Define_SimpleSet_lookupType(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
