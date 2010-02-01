
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class ElementArrayValue extends ElementValue implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementArrayValue clone() throws CloneNotSupportedException {
        ElementArrayValue node = (ElementArrayValue)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementArrayValue copy() {
      try {
          ElementArrayValue node = (ElementArrayValue)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementArrayValue fullCopy() {
        ElementArrayValue res = (ElementArrayValue)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Annotations.jrag at line 599

  public void toString(StringBuffer s) {
    s.append("{");
    for(int i = 0; i < getNumElementValue(); i++) {
      getElementValue(i).toString(s);
      s.append(", ");
    }
    s.append("}");
  }

    // Declared in AnnotationsCodegen.jrag at line 357

  public void appendAsAttributeTo(Collection list, String name) {
    ArrayList elemVals = new ArrayList();
    for(int i = 0; i < getNumElementValue(); i++)
      getElementValue(i).appendAsAttributeTo(elemVals, "default");
    list.add(new soot.tagkit.AnnotationArrayElem(elemVals, '[', name));
  }

    // Declared in Annotations.ast at line 3
    // Declared in Annotations.ast line 13

    public ElementArrayValue() {
        super();

        setChild(new List(), 0);

    }

    // Declared in Annotations.ast at line 11


    // Declared in Annotations.ast line 13
    public ElementArrayValue(List<ElementValue> p0) {
        setChild(p0, 0);
    }

    // Declared in Annotations.ast at line 15


  protected int numChildren() {
    return 1;
  }

    // Declared in Annotations.ast at line 18

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in Annotations.ast at line 2
    // Declared in Annotations.ast line 13
    public void setElementValueList(List<ElementValue> list) {
        setChild(list, 0);
    }

    // Declared in Annotations.ast at line 6


    public int getNumElementValue() {
        return getElementValueList().getNumChild();
    }

    // Declared in Annotations.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public ElementValue getElementValue(int i) {
        return (ElementValue)getElementValueList().getChild(i);
    }

    // Declared in Annotations.ast at line 14


    public void addElementValue(ElementValue node) {
        List<ElementValue> list = (parent == null || state == null) ? getElementValueListNoTransform() : getElementValueList();
        list.addChild(node);
    }

    // Declared in Annotations.ast at line 19


    public void addElementValueNoTransform(ElementValue node) {
        List<ElementValue> list = getElementValueListNoTransform();
        list.addChild(node);
    }

    // Declared in Annotations.ast at line 24


    public void setElementValue(ElementValue node, int i) {
        List<ElementValue> list = getElementValueList();
        list.setChild(node, i);
    }

    // Declared in Annotations.ast at line 28

    public List<ElementValue> getElementValues() {
        return getElementValueList();
    }

    // Declared in Annotations.ast at line 31

    public List<ElementValue> getElementValuesNoTransform() {
        return getElementValueListNoTransform();
    }

    // Declared in Annotations.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<ElementValue> getElementValueList() {
        List<ElementValue> list = (List<ElementValue>)getChild(0);
        list.getNumChild();
        return list;
    }

    // Declared in Annotations.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<ElementValue> getElementValueListNoTransform() {
        return (List<ElementValue>)getChildNoTransform(0);
    }

    // Declared in Annotations.jrag at line 63
 @SuppressWarnings({"unchecked", "cast"})     public boolean validTarget(Annotation a) {
        ASTNode$State state = state();
        boolean validTarget_Annotation_value = validTarget_compute(a);
        return validTarget_Annotation_value;
    }

    private boolean validTarget_compute(Annotation a) {
    for(int i = 0;  i < getNumElementValue(); i++)
      if(getElementValue(i).validTarget(a))
        return true;
    return false;
  }

    // Declared in Annotations.jrag at line 188
 @SuppressWarnings({"unchecked", "cast"})     public ElementValue definesElementTypeValue(String name) {
        ASTNode$State state = state();
        ElementValue definesElementTypeValue_String_value = definesElementTypeValue_compute(name);
        return definesElementTypeValue_String_value;
    }

    private ElementValue definesElementTypeValue_compute(String name) {
    for(int i = 0; i < getNumElementValue(); i++)
      if(getElementValue(i).definesElementTypeValue(name) != null)
        return getElementValue(i).definesElementTypeValue(name);
    return null;
  }

    // Declared in Annotations.jrag at line 300
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasValue(String s) {
        ASTNode$State state = state();
        boolean hasValue_String_value = hasValue_compute(s);
        return hasValue_String_value;
    }

    private boolean hasValue_compute(String s) {
    for(int i = 0;  i < getNumElementValue(); i++)
      if(getElementValue(i).hasValue(s))
        return true;
    return false;
  }

    // Declared in Annotations.jrag at line 495
 @SuppressWarnings({"unchecked", "cast"})     public boolean commensurateWithArrayDecl(ArrayDecl type) {
        ASTNode$State state = state();
        boolean commensurateWithArrayDecl_ArrayDecl_value = commensurateWithArrayDecl_compute(type);
        return commensurateWithArrayDecl_ArrayDecl_value;
    }

    private boolean commensurateWithArrayDecl_compute(ArrayDecl type) {
    for(int i = 0; i < getNumElementValue(); i++)
      if(!type.componentType().commensurateWith(getElementValue(i)))
        return false;
    return true;
  }

    // Declared in Annotations.jrag at line 178
    public ElementValue Define_ElementValue_lookupElementTypeValue(ASTNode caller, ASTNode child, String name) {
        if(caller == getElementValueListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return definesElementTypeValue(name);
        }
        return getParent().Define_ElementValue_lookupElementTypeValue(this, caller, name);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
