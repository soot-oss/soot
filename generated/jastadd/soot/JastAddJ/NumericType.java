
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;


// 4.2 Primitive Types and Values

public abstract class NumericType extends PrimitiveType implements Cloneable {
    public void flushCache() {
        super.flushCache();
        unaryNumericPromotion_computed = false;
        unaryNumericPromotion_value = null;
        binaryNumericPromotion_TypeDecl_values = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public NumericType clone() throws CloneNotSupportedException {
        NumericType node = (NumericType)super.clone();
        node.unaryNumericPromotion_computed = false;
        node.unaryNumericPromotion_value = null;
        node.binaryNumericPromotion_TypeDecl_values = null;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
    // Declared in AutoBoxingCodegen.jrag at line 20

  public soot.Value emitCastTo(Body b, soot.Value v, TypeDecl type) {
    if(type.isUnknown()) throw new Error("Trying to cast to Unknown");
    if(type == this)
      return v;
    if((isLong() || this instanceof FloatingPointType) && type.isIntegralType()) {
      v = soot.jimple.Jimple.v().newCastExpr(
        asImmediate(b, v), typeInt().getSootType());
      return typeInt().emitCastTo(b, v, type);
    }
    else if(type instanceof NumericType) {
      return soot.jimple.Jimple.v().newCastExpr(
        asImmediate(b, v),
        type.getSootType()
      );
    }
    else if(!type.isNumericType())
      return emitCastTo(b, v, boxed());
    else
      return boxed().emitBoxingOperation(b, emitCastTo(b, v, type.unboxed()));
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 50

    public NumericType() {
        super();

        setChild(new Opt(), 1);
        setChild(new List(), 2);

    }

    // Declared in java.ast at line 12


    // Declared in java.ast line 50
    public NumericType(Modifiers p0, String p1, Opt<Access> p2, List<BodyDecl> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in java.ast at line 20


    // Declared in java.ast line 50
    public NumericType(Modifiers p0, beaver.Symbol p1, Opt<Access> p2, List<BodyDecl> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in java.ast at line 27


  protected int numChildren() {
    return 3;
  }

    // Declared in java.ast at line 30

  public boolean mayHaveRewrite() { return false; }

    // Declared in java.ast at line 2
    // Declared in java.ast line 42
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in java.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 42
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public int IDstart;

    // Declared in java.ast at line 6

    public int IDend;

    // Declared in java.ast at line 7

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 14

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 42
    public void setSuperClassAccessOpt(Opt<Access> opt) {
        setChild(opt, 1);
    }

    // Declared in java.ast at line 6


    public boolean hasSuperClassAccess() {
        return getSuperClassAccessOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getSuperClassAccess() {
        return (Access)getSuperClassAccessOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setSuperClassAccess(Access node) {
        getSuperClassAccessOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Access> getSuperClassAccessOpt() {
        return (Opt<Access>)getChild(1);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Access> getSuperClassAccessOptNoTransform() {
        return (Opt<Access>)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 42
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 2);
    }

    // Declared in java.ast at line 6


    private int getNumBodyDecl = 0;

    // Declared in java.ast at line 7

    public int getNumBodyDecl() {
        return getBodyDeclList().getNumChild();
    }

    // Declared in java.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public BodyDecl getBodyDecl(int i) {
        return (BodyDecl)getBodyDeclList().getChild(i);
    }

    // Declared in java.ast at line 15


    public void addBodyDecl(BodyDecl node) {
        List<BodyDecl> list = getBodyDeclList();
        list.addChild(node);
    }

    // Declared in java.ast at line 20


    public void setBodyDecl(BodyDecl node, int i) {
        List<BodyDecl> list = getBodyDeclList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 24

    public List<BodyDecl> getBodyDecls() {
        return getBodyDeclList();
    }

    // Declared in java.ast at line 27

    public List<BodyDecl> getBodyDeclsNoTransform() {
        return getBodyDeclListNoTransform();
    }

    // Declared in java.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclList() {
        return (List<BodyDecl>)getChild(2);
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(2);
    }

    // Declared in TypeAnalysis.jrag at line 155
private TypeDecl refined_TypeAnalysis_binaryNumericPromotion_TypeDecl(TypeDecl type)
{
    if(!type.isNumericType())
      return unknownType();
     return unaryNumericPromotion().instanceOf(type) ? type : unaryNumericPromotion();
  }

    protected boolean unaryNumericPromotion_computed = false;
    protected TypeDecl unaryNumericPromotion_value;
    // Declared in TypeAnalysis.jrag at line 148
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl unaryNumericPromotion() {
        if(unaryNumericPromotion_computed)
            return unaryNumericPromotion_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        unaryNumericPromotion_value = unaryNumericPromotion_compute();
        if(isFinal && num == boundariesCrossed)
            unaryNumericPromotion_computed = true;
        return unaryNumericPromotion_value;
    }

    private TypeDecl unaryNumericPromotion_compute() {  return this;  }

    protected java.util.Map binaryNumericPromotion_TypeDecl_values;
    // Declared in AutoBoxing.jrag at line 174
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl binaryNumericPromotion(TypeDecl type) {
        Object _parameters = type;
if(binaryNumericPromotion_TypeDecl_values == null) binaryNumericPromotion_TypeDecl_values = new java.util.HashMap(4);
        if(binaryNumericPromotion_TypeDecl_values.containsKey(_parameters))
            return (TypeDecl)binaryNumericPromotion_TypeDecl_values.get(_parameters);
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        TypeDecl binaryNumericPromotion_TypeDecl_value = binaryNumericPromotion_compute(type);
        if(isFinal && num == boundariesCrossed)
            binaryNumericPromotion_TypeDecl_values.put(_parameters, binaryNumericPromotion_TypeDecl_value);
        return binaryNumericPromotion_TypeDecl_value;
    }

    private TypeDecl binaryNumericPromotion_compute(TypeDecl type) {
    if(type.isReferenceType())
      type = type.unboxed();
    return refined_TypeAnalysis_binaryNumericPromotion_TypeDecl(type);
  }

    // Declared in TypeAnalysis.jrag at line 174
 @SuppressWarnings({"unchecked", "cast"})     public boolean isNumericType() {
        boolean isNumericType_value = isNumericType_compute();
        return isNumericType_value;
    }

    private boolean isNumericType_compute() {  return true;  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
