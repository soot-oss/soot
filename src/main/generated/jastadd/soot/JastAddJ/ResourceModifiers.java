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
 * Subclass of Modifiers for resource declarations.
 * This subclass is added as a convenient method of making resource
 * declarations implicitly final.
 * @production ResourceModifiers : {@link Modifiers};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.ast:16
 */
public class ResourceModifiers extends Modifiers implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isFinal_computed = false;
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
  public ResourceModifiers clone() throws CloneNotSupportedException {
    ResourceModifiers node = (ResourceModifiers)super.clone();
    node.isFinal_computed = false;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ResourceModifiers copy() {
    try {
      ResourceModifiers node = (ResourceModifiers) clone();
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
  public ResourceModifiers fullCopy() {
    ResourceModifiers tree = (ResourceModifiers) copy();
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
   * 
   */
  public ResourceModifiers() {
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
    children = new ASTNode[1];
    setChild(new List(), 0);
  }
  /**
   * @ast method 
   * 
   */
  public ResourceModifiers(List<Modifier> p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 1;
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
   * Replaces the Modifier list.
   * @param list The new list node to be used as the Modifier list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setModifierList(List<Modifier> list) {
    setChild(list, 0);
  }
  /**
   * Retrieves the number of children in the Modifier list.
   * @return Number of children in the Modifier list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumModifier() {
    return getModifierList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Modifier list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Modifier list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumModifierNoTransform() {
    return getModifierListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Modifier list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Modifier list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Modifier getModifier(int i) {
    return (Modifier)getModifierList().getChild(i);
  }
  /**
   * Append an element to the Modifier list.
   * @param node The element to append to the Modifier list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addModifier(Modifier node) {
    List<Modifier> list = (parent == null || state == null) ? getModifierListNoTransform() : getModifierList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addModifierNoTransform(Modifier node) {
    List<Modifier> list = getModifierListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Modifier list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setModifier(Modifier node, int i) {
    List<Modifier> list = getModifierList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Modifier list.
   * @return The node representing the Modifier list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Modifier> getModifiers() {
    return getModifierList();
  }
  /**
   * Retrieves the Modifier list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Modifier list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Modifier> getModifiersNoTransform() {
    return getModifierListNoTransform();
  }
  /**
   * Retrieves the Modifier list.
   * @return The node representing the Modifier list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Modifier> getModifierList() {
    List<Modifier> list = (List<Modifier>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the Modifier list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Modifier list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Modifier> getModifierListNoTransform() {
    return (List<Modifier>)getChildNoTransform(0);
  }
  /**
   * @apilevel internal
   */
  protected boolean isFinal_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isFinal_value;
  /**
   * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:237
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isFinal() {
    if(isFinal_computed) {
      return isFinal_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isFinal_value = isFinal_compute();
      if(isFinal && num == state().boundariesCrossed) isFinal_computed = true;
    return isFinal_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isFinal_compute() {  return true;  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
