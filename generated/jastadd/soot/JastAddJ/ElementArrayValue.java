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
 * @declaredat Annotations.ast:13
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
        ElementArrayValue node = (ElementArrayValue)clone();
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
  public ElementArrayValue fullCopy() {
    ElementArrayValue res = (ElementArrayValue)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:599
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
   * @declaredat Annotations.ast:1
   */
  public ElementArrayValue() {
    super();

    setChild(new List(), 0);

  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:8
   */
  public ElementArrayValue(List<ElementValue> p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:14
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Annotations.ast:20
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for ElementValueList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:5
   */
  public void setElementValueList(List<ElementValue> list) {
    setChild(list, 0);
  }
  /**
   * @return number of children in ElementValueList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:12
   */
  public int getNumElementValue() {
    return getElementValueList().getNumChild();
  }
  /**
   * Getter for child in list ElementValueList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementValue getElementValue(int i) {
    return (ElementValue)getElementValueList().getChild(i);
  }
  /**
   * Add element to list ElementValueList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:27
   */
  public void addElementValue(ElementValue node) {
    List<ElementValue> list = (parent == null || state == null) ? getElementValueListNoTransform() : getElementValueList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:34
   */
  public void addElementValueNoTransform(ElementValue node) {
    List<ElementValue> list = getElementValueListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ElementValueList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:42
   */
  public void setElementValue(ElementValue node, int i) {
    List<ElementValue> list = getElementValueList();
    list.setChild(node, i);
  }
  /**
   * Getter for ElementValue list.
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:50
   */
  public List<ElementValue> getElementValues() {
    return getElementValueList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:56
   */
  public List<ElementValue> getElementValuesNoTransform() {
    return getElementValueListNoTransform();
  }
  /**
   * Getter for list ElementValueList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ElementValue> getElementValueList() {
    List<ElementValue> list = (List<ElementValue>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ElementValue> getElementValueListNoTransform() {
    return (List<ElementValue>)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean validTarget(Annotation a) {
      ASTNode$State state = state();
    boolean validTarget_Annotation_value = validTarget_compute(a);
    return validTarget_Annotation_value;
  }
  /**
   * @apilevel internal
   */
  private boolean validTarget_compute(Annotation a) {
    for(int i = 0;  i < getNumElementValue(); i++)
      if(getElementValue(i).validTarget(a))
        return true;
    return false;
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:188
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementValue definesElementTypeValue(String name) {
      ASTNode$State state = state();
    ElementValue definesElementTypeValue_String_value = definesElementTypeValue_compute(name);
    return definesElementTypeValue_String_value;
  }
  /**
   * @apilevel internal
   */
  private ElementValue definesElementTypeValue_compute(String name) {
    for(int i = 0; i < getNumElementValue(); i++)
      if(getElementValue(i).definesElementTypeValue(name) != null)
        return getElementValue(i).definesElementTypeValue(name);
    return null;
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:300
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasValue(String s) {
      ASTNode$State state = state();
    boolean hasValue_String_value = hasValue_compute(s);
    return hasValue_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hasValue_compute(String s) {
    for(int i = 0;  i < getNumElementValue(); i++)
      if(getElementValue(i).hasValue(s))
        return true;
    return false;
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:495
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean commensurateWithArrayDecl(ArrayDecl type) {
      ASTNode$State state = state();
    boolean commensurateWithArrayDecl_ArrayDecl_value = commensurateWithArrayDecl_compute(type);
    return commensurateWithArrayDecl_ArrayDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean commensurateWithArrayDecl_compute(ArrayDecl type) {
    for(int i = 0; i < getNumElementValue(); i++)
      if(!type.componentType().commensurateWith(getElementValue(i)))
        return false;
    return true;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:178
   * @apilevel internal
   */
  public ElementValue Define_ElementValue_lookupElementTypeValue(ASTNode caller, ASTNode child, String name) {
    if(caller == getElementValueListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return definesElementTypeValue(name);
    }
    return getParent().Define_ElementValue_lookupElementTypeValue(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
