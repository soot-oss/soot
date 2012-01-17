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
 * @declaredat Generics.ast:28
 */
public class ConstructorDeclSubstituted extends ConstructorDecl implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    sourceConstructorDecl_computed = false;
    sourceConstructorDecl_value = null;
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
  public ConstructorDeclSubstituted clone() throws CloneNotSupportedException {
    ConstructorDeclSubstituted node = (ConstructorDeclSubstituted)super.clone();
    node.sourceConstructorDecl_computed = false;
    node.sourceConstructorDecl_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConstructorDeclSubstituted copy() {
      try {
        ConstructorDeclSubstituted node = (ConstructorDeclSubstituted)clone();
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
  public ConstructorDeclSubstituted fullCopy() {
    ConstructorDeclSubstituted res = (ConstructorDeclSubstituted)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:23
   */
  public ConstructorDecl createAccessor() {
    return erasedConstructor().createAccessor();
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:27
   */
  protected TypeDecl createAnonymousJavaTypeDecl() {
    return erasedConstructor().createAnonymousJavaTypeDecl();
  }
  /**
   * @ast method 
   * @declaredat Generics.ast:1
   */
  public ConstructorDeclSubstituted() {
    super();

    setChild(new List(), 1);
    setChild(new List(), 2);
    setChild(new Opt(), 3);

  }
  /**
   * @ast method 
   * @declaredat Generics.ast:10
   */
  public ConstructorDeclSubstituted(Modifiers p0, String p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5, ConstructorDecl p6) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
    setChild(p4, 3);
    setChild(p5, 4);
    setOriginal(p6);
  }
  /**
   * @ast method 
   * @declaredat Generics.ast:19
   */
  public ConstructorDeclSubstituted(Modifiers p0, beaver.Symbol p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5, ConstructorDecl p6) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
    setChild(p4, 3);
    setChild(p5, 4);
    setOriginal(p6);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:31
   */
  protected int numChildren() {
    return 5;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Generics.ast:37
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
   * Setter for lexeme Original
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:5
   */
  public void setOriginal(ConstructorDecl value) {
    tokenConstructorDecl_Original = value;
  }
  /**   * @apilevel internal   * @ast method 
   * @declaredat Generics.ast:8
   */
  
  /**   * @apilevel internal   */  protected ConstructorDecl tokenConstructorDecl_Original;
  /**
   * Getter for lexeme Original
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:13
   */
  public ConstructorDecl getOriginal() {
    return tokenConstructorDecl_Original;
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1280
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
  private ConstructorDecl sourceConstructorDecl_compute() {  return getOriginal().sourceConstructorDecl();  }
  /**
   * @attribute syn
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:318
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
  private ConstructorDecl erasedConstructor_compute() {  return getOriginal().erasedConstructor();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
