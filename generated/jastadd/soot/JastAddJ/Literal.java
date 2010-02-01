
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public abstract class Literal extends PrimaryExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        constant_computed = false;
        constant_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public Literal clone() throws CloneNotSupportedException {
        Literal node = (Literal)super.clone();
        node.constant_computed = false;
        node.constant_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in ConstantExpression.jrag at line 160

  
  static long parseLong(String s) {
    long x = 0L;
    s = s.toLowerCase();
    boolean neg = false;
    if(s.startsWith("-")) {
      s = s.substring(1);
      neg = true;
    }
    if(s.startsWith("0x")) {
      s = s.substring(2);
      if(s.length() > 16) {
        for(int i = 0; i < s.length()-16; i++)
          if(s.charAt(i) != '0')
            throw new NumberFormatException("");
      }
      for (int i = 0; i < s.length(); i++) {
        int c = s.charAt(i);
        if (c >= 'a' && c <= 'f')
          c = c - 'a' + 10;
        else if(c >= '0' && c <= '9')
          c = c - '0';
        else
          throw new NumberFormatException("");
        x = x * 16 + c;
      }
    }
    else if(s.startsWith("0")) {
      s = s.substring(1);
      // Octals larger than 01777777777777777777777L are not valid
      if(s.length() > 21) {
        for(int i = 0; i < s.length() - 21; i++)
          if(i == s.length() - 21 - 1) {
            if(s.charAt(i) != '0' && s.charAt(i) != '1')
              throw new NumberFormatException("");
          }
          else {
            if(s.charAt(i) != '0')
              throw new NumberFormatException("");
          }
      }
      for (int i = 0; i < s.length(); i++) {
        int c = s.charAt(i);
        if(c >= '0' && c <= '7')
          c = c - '0';
        else
          throw new NumberFormatException("");
        x = x * 8 + c;
      }
    }
    else {
      long oldx = 0;
      for (int i = 0; i < s.length(); i++) {
        int c = s.charAt(i);
        if(c >= '0' && c <= '9')
          c = c - '0';
        else
          throw new NumberFormatException("");
        x = x * 10 + c;
        if(x < oldx) {
          boolean negMinValue = i == (s.length()-1) && neg && x == Long.MIN_VALUE;
          if(!negMinValue)
            throw new NumberFormatException("");
        }
        oldx = x;
      }
      if(x == Long.MIN_VALUE)
        return x;
      if(x < 0)
        throw new NumberFormatException("");
    }
    return neg ? -x : x;
  }

    // Declared in PrettyPrint.jadd at line 262


  // Literals
    
  public void toString(StringBuffer s) {
    s.append(getLITERAL());
  }

    // Declared in PrettyPrint.jadd at line 289


  protected static String escape(String s) {
    StringBuffer result = new StringBuffer();
    for (int i=0; i < s.length(); i++) {
      switch(s.charAt(i)) {
        case '\b' : result.append("\\b"); break;
        case '\t' : result.append("\\t"); break;
        case '\n' : result.append("\\n"); break;
        case '\f' : result.append("\\f"); break;
        case '\r' : result.append("\\r"); break;
        case '\"' : result.append("\\\""); break;
        case '\'' : result.append("\\\'"); break;
        case '\\' : result.append("\\\\"); break;
        default:
          int value = (int)s.charAt(i);
          if(value < 0x20 || (value > 0x7e))
            result.append(asEscape(value));
          else
            result.append(s.charAt(i));
      }
    }
    return result.toString();
  }

    // Declared in PrettyPrint.jadd at line 311

  protected static String asEscape(int value) {
    StringBuffer s = new StringBuffer("\\u");
    String hex = Integer.toHexString(value);
    for(int i = 0; i < 4-hex.length(); i++)
      s.append("0");
    s.append(hex);
    return s.toString();
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 124

    public Literal() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 124
    public Literal(String p0) {
        setLITERAL(p0);
    }

    // Declared in java.ast at line 15


    // Declared in java.ast line 124
    public Literal(beaver.Symbol p0) {
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
    protected String tokenString_LITERAL;

    // Declared in java.ast at line 3

    public void setLITERAL(String value) {
        tokenString_LITERAL = value;
    }

    // Declared in java.ast at line 6

    public int LITERALstart;

    // Declared in java.ast at line 7

    public int LITERALend;

    // Declared in java.ast at line 8

    public void setLITERAL(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setLITERAL is only valid for String lexemes");
        tokenString_LITERAL = (String)symbol.value;
        LITERALstart = symbol.getStart();
        LITERALend = symbol.getEnd();
    }

    // Declared in java.ast at line 15

    public String getLITERAL() {
        return tokenString_LITERAL != null ? tokenString_LITERAL : "";
    }

    protected boolean constant_computed = false;
    protected Constant constant_value;
    // Declared in ConstantExpression.jrag at line 103
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
    throw new UnsupportedOperationException("ConstantExpression operation constant" +
      " not supported for type " + getClass().getName()); 
  }

    // Declared in ConstantExpression.jrag at line 483
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstant() {
        ASTNode$State state = state();
        boolean isConstant_value = isConstant_compute();
        return isConstant_value;
    }

    private boolean isConstant_compute() {  return true;  }

    // Declared in PrettyPrint.jadd at line 815
 @SuppressWarnings({"unchecked", "cast"})     public String dumpString() {
        ASTNode$State state = state();
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return getClass().getName() + " [" + getLITERAL() + "]";  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
