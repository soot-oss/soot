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
 * @production ParMethodDecl : {@link MethodDecl} ::= <span class="component">TypeArgument:{@link Access}*</span> <span class="component">&lt;GenericMethodDecl:GenericMethodDecl&gt;</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.ast:7
 */
public class ParMethodDecl extends MethodDecl implements Cloneable, Parameterization {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    genericMethodDecl_computed = false;
    genericMethodDecl_value = null;
    sourceMethodDecl_computed = false;
    sourceMethodDecl_value = null;
    moreSpecificThan_MethodDecl_values = null;
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
  public ParMethodDecl clone() throws CloneNotSupportedException {
    ParMethodDecl node = (ParMethodDecl)super.clone();
    node.genericMethodDecl_computed = false;
    node.genericMethodDecl_value = null;
    node.sourceMethodDecl_computed = false;
    node.sourceMethodDecl_value = null;
    node.moreSpecificThan_MethodDecl_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParMethodDecl copy() {
    try {
      ParMethodDecl node = (ParMethodDecl) clone();
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
  public ParMethodDecl fullCopy() {
    ParMethodDecl tree = (ParMethodDecl) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
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
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:132
   */
  public void collectErrors() {
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:846
   */
  public TypeDecl substitute(TypeVariable typeVariable) {
    for(int i = 0; i < numTypeParameter(); i++)
      if(typeParameter(i) == typeVariable)
        return getTypeArgument(i).type();
    return genericMethodDecl().hostType().substitute(typeVariable);
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:860
   */
  public boolean isRawType() {
    return false; 
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:866
   */
  public int numTypeParameter() {
    return genericMethodDecl().original().getNumTypeParameter();
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:869
   */
  public TypeVariable typeParameter(int index) {
    return genericMethodDecl().original().getTypeParameter(index);
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:322
   */
  public void transformation() { }
  /**
   * @ast method 
   * 
   */
  public ParMethodDecl() {
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
    children = new ASTNode[6];
    setChild(new List(), 2);
    setChild(new List(), 3);
    setChild(new Opt(), 4);
    setChild(new List(), 5);
  }
  /**
   * @ast method 
   * 
   */
  public ParMethodDecl(Modifiers p0, Access p1, String p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5, List<Access> p6, GenericMethodDecl p7) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
    setChild(p3, 2);
    setChild(p4, 3);
    setChild(p5, 4);
    setChild(p6, 5);
    setGenericMethodDecl(p7);
  }
  /**
   * @ast method 
   * 
   */
  public ParMethodDecl(Modifiers p0, Access p1, beaver.Symbol p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5, List<Access> p6, GenericMethodDecl p7) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
    setChild(p3, 2);
    setChild(p4, 3);
    setChild(p5, 4);
    setChild(p6, 5);
    setGenericMethodDecl(p7);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 6;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return false;
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
   * Replaces the TypeAccess child.
   * @param node The new node to replace the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeAccess(Access node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the TypeAccess child.
   * @return The current node used as the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Access getTypeAccess() {
    return (Access)getChild(1);
  }
  /**
   * Retrieves the TypeAccess child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the TypeAccess child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(1);
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
   * Replaces the Parameter list.
   * @param list The new list node to be used as the Parameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setParameterList(List<ParameterDeclaration> list) {
    setChild(list, 2);
  }
  /**
   * Retrieves the number of children in the Parameter list.
   * @return Number of children in the Parameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumParameter() {
    return getParameterList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Parameter list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Parameter list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumParameterNoTransform() {
    return getParameterListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Parameter list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Parameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParameterDeclaration getParameter(int i) {
    return (ParameterDeclaration)getParameterList().getChild(i);
  }
  /**
   * Append an element to the Parameter list.
   * @param node The element to append to the Parameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addParameter(ParameterDeclaration node) {
    List<ParameterDeclaration> list = (parent == null || state == null) ? getParameterListNoTransform() : getParameterList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addParameterNoTransform(ParameterDeclaration node) {
    List<ParameterDeclaration> list = getParameterListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Parameter list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setParameter(ParameterDeclaration node, int i) {
    List<ParameterDeclaration> list = getParameterList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Parameter list.
   * @return The node representing the Parameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<ParameterDeclaration> getParameters() {
    return getParameterList();
  }
  /**
   * Retrieves the Parameter list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Parameter list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<ParameterDeclaration> getParametersNoTransform() {
    return getParameterListNoTransform();
  }
  /**
   * Retrieves the Parameter list.
   * @return The node representing the Parameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ParameterDeclaration> getParameterList() {
    List<ParameterDeclaration> list = (List<ParameterDeclaration>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the Parameter list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Parameter list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ParameterDeclaration> getParameterListNoTransform() {
    return (List<ParameterDeclaration>)getChildNoTransform(2);
  }
  /**
   * Replaces the Exception list.
   * @param list The new list node to be used as the Exception list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setExceptionList(List<Access> list) {
    setChild(list, 3);
  }
  /**
   * Retrieves the number of children in the Exception list.
   * @return Number of children in the Exception list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumException() {
    return getExceptionList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Exception list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Exception list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumExceptionNoTransform() {
    return getExceptionListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Exception list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Exception list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getException(int i) {
    return (Access)getExceptionList().getChild(i);
  }
  /**
   * Append an element to the Exception list.
   * @param node The element to append to the Exception list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addException(Access node) {
    List<Access> list = (parent == null || state == null) ? getExceptionListNoTransform() : getExceptionList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addExceptionNoTransform(Access node) {
    List<Access> list = getExceptionListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Exception list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setException(Access node, int i) {
    List<Access> list = getExceptionList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Exception list.
   * @return The node representing the Exception list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Access> getExceptions() {
    return getExceptionList();
  }
  /**
   * Retrieves the Exception list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Exception list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Access> getExceptionsNoTransform() {
    return getExceptionListNoTransform();
  }
  /**
   * Retrieves the Exception list.
   * @return The node representing the Exception list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getExceptionList() {
    List<Access> list = (List<Access>)getChild(3);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the Exception list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Exception list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getExceptionListNoTransform() {
    return (List<Access>)getChildNoTransform(3);
  }
  /**
   * Replaces the optional node for the Block child. This is the {@code Opt} node containing the child Block, not the actual child!
   * @param opt The new node to be used as the optional node for the Block child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setBlockOpt(Opt<Block> opt) {
    setChild(opt, 4);
  }
  /**
   * Check whether the optional Block child exists.
   * @return {@code true} if the optional Block child exists, {@code false} if it does not.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public boolean hasBlock() {
    return getBlockOpt().getNumChild() != 0;
  }
  /**
   * Retrieves the (optional) Block child.
   * @return The Block child, if it exists. Returns {@code null} otherwise.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Block getBlock() {
    return (Block)getBlockOpt().getChild(0);
  }
  /**
   * Replaces the (optional) Block child.
   * @param node The new node to be used as the Block child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBlock(Block node) {
    getBlockOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getBlockOpt() {
    return (Opt<Block>)getChild(4);
  }
  /**
   * Retrieves the optional node for child Block. This is the {@code Opt} node containing the child Block, not the actual child!
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The optional node for child Block.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getBlockOptNoTransform() {
    return (Opt<Block>)getChildNoTransform(4);
  }
  /**
   * Replaces the TypeArgument list.
   * @param list The new list node to be used as the TypeArgument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeArgumentList(List<Access> list) {
    setChild(list, 5);
  }
  /**
   * Retrieves the number of children in the TypeArgument list.
   * @return Number of children in the TypeArgument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumTypeArgument() {
    return getTypeArgumentList().getNumChild();
  }
  /**
   * Retrieves the number of children in the TypeArgument list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the TypeArgument list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumTypeArgumentNoTransform() {
    return getTypeArgumentListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the TypeArgument list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the TypeArgument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getTypeArgument(int i) {
    return (Access)getTypeArgumentList().getChild(i);
  }
  /**
   * Append an element to the TypeArgument list.
   * @param node The element to append to the TypeArgument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addTypeArgument(Access node) {
    List<Access> list = (parent == null || state == null) ? getTypeArgumentListNoTransform() : getTypeArgumentList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addTypeArgumentNoTransform(Access node) {
    List<Access> list = getTypeArgumentListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the TypeArgument list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeArgument(Access node, int i) {
    List<Access> list = getTypeArgumentList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the TypeArgument list.
   * @return The node representing the TypeArgument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Access> getTypeArguments() {
    return getTypeArgumentList();
  }
  /**
   * Retrieves the TypeArgument list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the TypeArgument list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Access> getTypeArgumentsNoTransform() {
    return getTypeArgumentListNoTransform();
  }
  /**
   * Retrieves the TypeArgument list.
   * @return The node representing the TypeArgument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeArgumentList() {
    List<Access> list = (List<Access>)getChild(5);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the TypeArgument list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the TypeArgument list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeArgumentListNoTransform() {
    return (List<Access>)getChildNoTransform(5);
  }
  /**
   * Replaces the lexeme GenericMethodDecl.
   * @param value The new value for the lexeme GenericMethodDecl.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setGenericMethodDecl(GenericMethodDecl value) {
    tokenGenericMethodDecl_GenericMethodDecl = value;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  protected GenericMethodDecl tokenGenericMethodDecl_GenericMethodDecl;
  /**
   * Retrieves the value for the lexeme GenericMethodDecl.
   * @return The value for the lexeme GenericMethodDecl.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public GenericMethodDecl getGenericMethodDecl() {
    return tokenGenericMethodDecl_GenericMethodDecl;
  }
  /**
   * @apilevel internal
   */
  protected boolean genericMethodDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected GenericMethodDecl genericMethodDecl_value;
  /**
   * @attribute syn
   * @aspect GenericMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:45
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GenericMethodDecl genericMethodDecl() {
    if(genericMethodDecl_computed) {
      return genericMethodDecl_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    genericMethodDecl_value = genericMethodDecl_compute();
      if(isFinal && num == state().boundariesCrossed) genericMethodDecl_computed = true;
    return genericMethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private GenericMethodDecl genericMethodDecl_compute() {
	  return getGenericMethodDecl();
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1512
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
  private MethodDecl sourceMethodDecl_compute() {  return genericMethodDecl().original().sourceMethodDecl();  }
  protected java.util.Map moreSpecificThan_MethodDecl_values;
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:152
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
  private boolean moreSpecificThan_compute(MethodDecl m) {  return genericMethodDecl().moreSpecificThan(m instanceof ParMethodDecl ? ((ParMethodDecl)m).genericMethodDecl() : m );  }
  /**
   * @attribute syn
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:34
   */
  public MethodDecl erasedMethod() {
    ASTNode$State state = state();
    try {  return genericMethodDecl().erasedMethod();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
