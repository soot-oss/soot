
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class ElementValuePair extends ASTNode<ASTNode> implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementValuePair clone() throws CloneNotSupportedException {
        ElementValuePair node = (ElementValuePair)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementValuePair copy() {
      try {
          ElementValuePair node = (ElementValuePair)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ElementValuePair fullCopy() {
        ElementValuePair res = (ElementValuePair)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Annotations.jrag at line 502

  /* It is a compile-time error if the element type is not commensurate with the ElementValue.*/
  public void typeCheck() {
    if(!type().commensurateWith(getElementValue()))
      error(type().typeName() + " is not commensurate with " + getElementValue().type().typeName());
  }

    // Declared in Annotations.jrag at line 589

  public void toString(StringBuffer s) {
    s.append(getName() + " = ");
    getElementValue().toString(s);
  }

    // Declared in Annotations.ast at line 3
    // Declared in Annotations.ast line 8

    public ElementValuePair() {
        super();


    }

    // Declared in Annotations.ast at line 10


    // Declared in Annotations.ast line 8
    public ElementValuePair(String p0, ElementValue p1) {
        setName(p0);
        setChild(p1, 0);
    }

    // Declared in Annotations.ast at line 16


    // Declared in Annotations.ast line 8
    public ElementValuePair(beaver.Symbol p0, ElementValue p1) {
        setName(p0);
        setChild(p1, 0);
    }

    // Declared in Annotations.ast at line 21


  protected int numChildren() {
    return 1;
  }

    // Declared in Annotations.ast at line 24

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in Annotations.ast at line 2
    // Declared in Annotations.ast line 8
    protected String tokenString_Name;

    // Declared in Annotations.ast at line 3

    public void setName(String value) {
        tokenString_Name = value;
    }

    // Declared in Annotations.ast at line 6

    public int Namestart;

    // Declared in Annotations.ast at line 7

    public int Nameend;

    // Declared in Annotations.ast at line 8

    public void setName(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setName is only valid for String lexemes");
        tokenString_Name = (String)symbol.value;
        Namestart = symbol.getStart();
        Nameend = symbol.getEnd();
    }

    // Declared in Annotations.ast at line 15

    public String getName() {
        return tokenString_Name != null ? tokenString_Name : "";
    }

    // Declared in Annotations.ast at line 2
    // Declared in Annotations.ast line 8
    public void setElementValue(ElementValue node) {
        setChild(node, 0);
    }

    // Declared in Annotations.ast at line 5

    public ElementValue getElementValue() {
        return (ElementValue)getChild(0);
    }

    // Declared in Annotations.ast at line 9


    public ElementValue getElementValueNoTransform() {
        return (ElementValue)getChildNoTransform(0);
    }

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in Annotations.jrag at line 448
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        if(type_computed) {
            return type_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        type_value = type_compute();
        if(isFinal && num == state().boundariesCrossed)
            type_computed = true;
        return type_value;
    }

    private TypeDecl type_compute() {
    Iterator iter = enclosingAnnotationDecl().memberMethods(getName()).iterator();
    if(iter.hasNext()) {
      MethodDecl m = (MethodDecl)iter.next();
      return m.type();
    }
    return unknownType();
  }

    // Declared in Annotations.jrag at line 456
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl unknownType() {
        ASTNode$State state = state();
        TypeDecl unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
        return unknownType_value;
    }

    // Declared in Annotations.jrag at line 458
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl enclosingAnnotationDecl() {
        ASTNode$State state = state();
        TypeDecl enclosingAnnotationDecl_value = getParent().Define_TypeDecl_enclosingAnnotationDecl(this, null);
        return enclosingAnnotationDecl_value;
    }

public ASTNode rewriteTo() {
    // Declared in Annotations.jrag at line 523
    if(type().isArrayDecl() && getElementValue() instanceof ElementConstantValue) {
        state().duringAnnotations++;
        ASTNode result = rewriteRule0();
        state().duringAnnotations--;
        return result;
    }

    return super.rewriteTo();
}

    // Declared in Annotations.jrag at line 523
    private ElementValuePair rewriteRule0() {
{
      setElementValue(new ElementArrayValue(new List().add(getElementValue())));
      return this;
    }    }
}
