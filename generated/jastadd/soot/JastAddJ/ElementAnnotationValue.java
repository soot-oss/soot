
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class ElementAnnotationValue extends ElementValue implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementAnnotationValue clone() throws CloneNotSupportedException {
        ElementAnnotationValue node = (ElementAnnotationValue)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementAnnotationValue copy() {
      try {
          ElementAnnotationValue node = (ElementAnnotationValue)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementAnnotationValue fullCopy() {
        ElementAnnotationValue res = (ElementAnnotationValue)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Annotations.jrag at line 596

  public void toString(StringBuffer s) {
    getAnnotation().toString(s);
  }

    // Declared in AnnotationsCodegen.jrag at line 352

  public void appendAsAttributeTo(Collection list, String name) {
    ArrayList elemVals = new ArrayList();
    getAnnotation().appendAsAttributeTo(elemVals);
    list.add(new soot.tagkit.AnnotationAnnotationElem((soot.tagkit.AnnotationTag)elemVals.get(0), '@', name));
  }

    // Declared in Annotations.ast at line 3
    // Declared in Annotations.ast line 12

    public ElementAnnotationValue() {
        super();


    }

    // Declared in Annotations.ast at line 10


    // Declared in Annotations.ast line 12
    public ElementAnnotationValue(Annotation p0) {
        setChild(p0, 0);
    }

    // Declared in Annotations.ast at line 14


  protected int numChildren() {
    return 1;
  }

    // Declared in Annotations.ast at line 17

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in Annotations.ast at line 2
    // Declared in Annotations.ast line 12
    public void setAnnotation(Annotation node) {
        setChild(node, 0);
    }

    // Declared in Annotations.ast at line 5

    public Annotation getAnnotation() {
        return (Annotation)getChild(0);
    }

    // Declared in Annotations.ast at line 9


    public Annotation getAnnotationNoTransform() {
        return (Annotation)getChildNoTransform(0);
    }

    // Declared in Annotations.jrag at line 488
 @SuppressWarnings({"unchecked", "cast"})     public boolean commensurateWithTypeDecl(TypeDecl type) {
        ASTNode$State state = state();
        boolean commensurateWithTypeDecl_TypeDecl_value = commensurateWithTypeDecl_compute(type);
        return commensurateWithTypeDecl_TypeDecl_value;
    }

    private boolean commensurateWithTypeDecl_compute(TypeDecl type) {
    return type() == type;
  }

    // Declared in Annotations.jrag at line 508
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        ASTNode$State state = state();
        TypeDecl type_value = type_compute();
        return type_value;
    }

    private TypeDecl type_compute() {  return getAnnotation().type();  }

    // Declared in Annotations.jrag at line 423
 @SuppressWarnings({"unchecked", "cast"})     public Annotation lookupAnnotation(TypeDecl typeDecl) {
        ASTNode$State state = state();
        Annotation lookupAnnotation_TypeDecl_value = getParent().Define_Annotation_lookupAnnotation(this, null, typeDecl);
        return lookupAnnotation_TypeDecl_value;
    }

    // Declared in Annotations.jrag at line 95
    public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
        if(caller == getAnnotationNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
    }

    // Declared in Annotations.jrag at line 427
    public Annotation Define_Annotation_lookupAnnotation(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
        if(caller == getAnnotationNoTransform()) {
            return getAnnotation().type() == typeDecl ? getAnnotation() : lookupAnnotation(typeDecl);
        }
        return getParent().Define_Annotation_lookupAnnotation(this, caller, typeDecl);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
