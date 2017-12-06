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
 * @production ElementArrayValue : {@link ElementValue} ::= <span class="component">{@link ElementValue}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.ast:13
 */
public class ElementArrayValue extends ElementValue implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
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
  public ElementArrayValue clone() throws CloneNotSupportedException {
    ElementArrayValue node = (ElementArrayValue)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementArrayValue copy() {
    try {
      ElementArrayValue node = (ElementArrayValue) clone();
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
  public ElementArrayValue fullCopy() {
    ElementArrayValue tree = (ElementArrayValue) copy();
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
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:603
   */
  public void toString(StringBuffer s) {
    s.append("{");
    for(int i = 0; i < getNumElementValue(); i++) {
      getElementValue(i).toString(s);
      s.append(", ");
    }
    s.append("}");
  }
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:359
   */
  public void appendAsAttributeTo(Collection list, String name) {
    ArrayList elemVals = new ArrayList();
    for(int i = 0; i < getNumElementValue(); i++)
      getElementValue(i).appendAsAttributeTo(elemVals, "default");
    list.add(new soot.tagkit.AnnotationArrayElem(elemVals, '[', name));
  }
  /**
   * @ast method 
   * 
   */
  public ElementArrayValue() {
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
  public ElementArrayValue(List<ElementValue> p0) {
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
   * Replaces the ElementValue list.
   * @param list The new list node to be used as the ElementValue list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setElementValueList(List<ElementValue> list) {
    setChild(list, 0);
  }
  /**
   * Retrieves the number of children in the ElementValue list.
   * @return Number of children in the ElementValue list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumElementValue() {
    return getElementValueList().getNumChild();
  }
  /**
   * Retrieves the number of children in the ElementValue list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the ElementValue list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumElementValueNoTransform() {
    return getElementValueListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the ElementValue list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the ElementValue list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementValue getElementValue(int i) {
    return (ElementValue)getElementValueList().getChild(i);
  }
  /**
   * Append an element to the ElementValue list.
   * @param node The element to append to the ElementValue list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addElementValue(ElementValue node) {
    List<ElementValue> list = (parent == null || state == null) ? getElementValueListNoTransform() : getElementValueList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addElementValueNoTransform(ElementValue node) {
    List<ElementValue> list = getElementValueListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the ElementValue list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setElementValue(ElementValue node, int i) {
    List<ElementValue> list = getElementValueList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the ElementValue list.
   * @return The node representing the ElementValue list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<ElementValue> getElementValues() {
    return getElementValueList();
  }
  /**
   * Retrieves the ElementValue list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the ElementValue list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<ElementValue> getElementValuesNoTransform() {
    return getElementValueListNoTransform();
  }
  /**
   * Retrieves the ElementValue list.
   * @return The node representing the ElementValue list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ElementValue> getElementValueList() {
    List<ElementValue> list = (List<ElementValue>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the ElementValue list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the ElementValue list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ElementValue> getElementValueListNoTransform() {
    return (List<ElementValue>)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:57
   */
  public boolean validTarget(Annotation a) {
    ASTNode$State state = state();
    try {
    for(int i = 0;  i < getNumElementValue(); i++)
      if(getElementValue(i).validTarget(a))
        return true;
    return false;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:181
   */
  public ElementValue definesElementTypeValue(String name) {
    ASTNode$State state = state();
    try {
    for(int i = 0; i < getNumElementValue(); i++)
      if(getElementValue(i).definesElementTypeValue(name) != null)
        return getElementValue(i).definesElementTypeValue(name);
    return null;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:295
   */
  public boolean hasValue(String s) {
    ASTNode$State state = state();
    try {
    for(int i = 0;  i < getNumElementValue(); i++)
      if(getElementValue(i).hasValue(s))
        return true;
    return false;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:495
   */
  public boolean commensurateWithArrayDecl(ArrayDecl type) {
    ASTNode$State state = state();
    try {
    for(int i = 0; i < getNumElementValue(); i++)
      if(!type.componentType().commensurateWith(getElementValue(i)))
        return false;
    return true;
  }
    finally {
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:178
   * @apilevel internal
   */
  public ElementValue Define_ElementValue_lookupElementTypeValue(ASTNode caller, ASTNode child, String name) {
    if(caller == getElementValueListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return definesElementTypeValue(name);
  }
    else {      return getParent().Define_ElementValue_lookupElementTypeValue(this, caller, name);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
