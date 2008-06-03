
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;



public abstract class ElementValue extends ASTNode<ASTNode> implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementValue clone() throws CloneNotSupportedException {
        ElementValue node = (ElementValue)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
    // Declared in AnnotationsCodegen.jrag at line 317


  // 4.8.15.1
  public void appendAsAttributeTo(Collection list, String name) {
    throw new Error(getClass().getName() + " does not support appendAsAttributeTo(Attribute buf)");
  }

    // Declared in Annotations.ast at line 3
    // Declared in Annotations.ast line 10

    public ElementValue() {
        super();


    }

    // Declared in Annotations.ast at line 9


  protected int numChildren() {
    return 0;
  }

    // Declared in Annotations.ast at line 12

  public boolean mayHaveRewrite() { return false; }

    // Declared in Annotations.jrag at line 57
 @SuppressWarnings({"unchecked", "cast"})     public boolean validTarget(Annotation a) {
        boolean validTarget_Annotation_value = validTarget_compute(a);
        return validTarget_Annotation_value;
    }

    private boolean validTarget_compute(Annotation a) {  return false;  }

    // Declared in Annotations.jrag at line 181
 @SuppressWarnings({"unchecked", "cast"})     public ElementValue definesElementTypeValue(String name) {
        ElementValue definesElementTypeValue_String_value = definesElementTypeValue_compute(name);
        return definesElementTypeValue_String_value;
    }

    private ElementValue definesElementTypeValue_compute(String name) {  return null;  }

    // Declared in Annotations.jrag at line 295
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasValue(String s) {
        boolean hasValue_String_value = hasValue_compute(s);
        return hasValue_String_value;
    }

    private boolean hasValue_compute(String s) {  return false;  }

    // Declared in Annotations.jrag at line 473
 @SuppressWarnings({"unchecked", "cast"})     public boolean commensurateWithTypeDecl(TypeDecl type) {
        boolean commensurateWithTypeDecl_TypeDecl_value = commensurateWithTypeDecl_compute(type);
        return commensurateWithTypeDecl_TypeDecl_value;
    }

    private boolean commensurateWithTypeDecl_compute(TypeDecl type) {  return false;  }

    // Declared in Annotations.jrag at line 493
 @SuppressWarnings({"unchecked", "cast"})     public boolean commensurateWithArrayDecl(ArrayDecl type) {
        boolean commensurateWithArrayDecl_ArrayDecl_value = commensurateWithArrayDecl_compute(type);
        return commensurateWithArrayDecl_ArrayDecl_value;
    }

    private boolean commensurateWithArrayDecl_compute(ArrayDecl type) {  return type.componentType().commensurateWith(this);  }

    // Declared in Annotations.jrag at line 506
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        TypeDecl type_value = type_compute();
        return type_value;
    }

    private TypeDecl type_compute() {  return unknownType();  }

    // Declared in Annotations.jrag at line 459
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl enclosingAnnotationDecl() {
        TypeDecl enclosingAnnotationDecl_value = getParent().Define_TypeDecl_enclosingAnnotationDecl(this, null);
        return enclosingAnnotationDecl_value;
    }

    // Declared in Annotations.jrag at line 511
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl unknownType() {
        TypeDecl unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
        return unknownType_value;
    }

    // Declared in AnnotationsCodegen.jrag at line 363
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl hostType() {
        TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
        return hostType_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
