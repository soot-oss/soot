
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class DoubleLiteral extends Literal implements Cloneable {
    public void flushCache() {
        super.flushCache();
        isZero_computed = false;
        constant_computed = false;
        constant_value = null;
        type_computed = false;
        type_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public DoubleLiteral clone() throws CloneNotSupportedException {
        DoubleLiteral node = (DoubleLiteral)super.clone();
        node.isZero_computed = false;
        node.constant_computed = false;
        node.constant_value = null;
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public DoubleLiteral copy() {
      try {
          DoubleLiteral node = (DoubleLiteral)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public DoubleLiteral fullCopy() {
        DoubleLiteral res = (DoubleLiteral)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 284


  public void toString(StringBuffer s) {
    s.append(getLITERAL());
    s.append("D");
  }

    // Declared in TypeCheck.jrag at line 588

 public void typeCheck() {
   if(!isZero() && constant().doubleValue() == 0.0f)
     error("It is an error for nonzero floating-point " + getLITERAL() + " to round to zero");
   if(constant().doubleValue() == Double.NEGATIVE_INFINITY || constant().doubleValue() == Double.POSITIVE_INFINITY)
     error("It is an error for floating-point " + getLITERAL() + " to round to an infinity");
 }

    // Declared in Expressions.jrag at line 29

  public soot.Value eval(Body b) {
    return soot.jimple.DoubleConstant.v(constant().doubleValue());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 128

    public DoubleLiteral() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 128
    public DoubleLiteral(String p0) {
        setLITERAL(p0);
    }

    // Declared in java.ast at line 15


    // Declared in java.ast line 128
    public DoubleLiteral(beaver.Symbol p0) {
        setLITERAL(p0);
    }

    // Declared in java.ast at line 19


  protected int numChildren() {
    return 0;
  }

    // Declared in java.ast at line 22

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 124
    public void setLITERAL(String value) {
        tokenString_LITERAL = value;
    }

    // Declared in java.ast at line 5

    public void setLITERAL(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setLITERAL is only valid for String lexemes");
        tokenString_LITERAL = (String)symbol.value;
        LITERALstart = symbol.getStart();
        LITERALend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getLITERAL() {
        return tokenString_LITERAL != null ? tokenString_LITERAL : "";
    }

    protected boolean isZero_computed = false;
    protected boolean isZero_value;
    // Declared in ConstantExpression.jrag at line 147
 @SuppressWarnings({"unchecked", "cast"})     public boolean isZero() {
        if(isZero_computed) {
            return isZero_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isZero_value = isZero_compute();
        if(isFinal && num == state().boundariesCrossed)
            isZero_computed = true;
        return isZero_value;
    }

    private boolean isZero_compute() {
    String s = getLITERAL();
    for(int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if(c == 'E'  || c == 'e')
        break;
      if(Character.isDigit(c) && c != '0') {
        return false;
      }
    }
    return true;
  }

    // Declared in ConstantExpression.jrag at line 292
 @SuppressWarnings({"unchecked", "cast"})     public Constant constant() {
        if(constant_computed) {
            return constant_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        constant_value = constant_compute();
        if(isFinal && num == state().boundariesCrossed)
            constant_computed = true;
        return constant_value;
    }

    private Constant constant_compute() {
    try {
      return Constant.create(Double.parseDouble(getLITERAL()));
    }
    catch (NumberFormatException e) {
      Constant c = Constant.create(0.0d);
      c.error = true;
      return c;
    }
  }

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in TypeAnalysis.jrag at line 303
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

    private TypeDecl type_compute() {  return typeDouble();  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
