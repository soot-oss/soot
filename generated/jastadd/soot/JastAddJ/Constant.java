
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class Constant extends java.lang.Object {
    // Declared in ConstantExpression.jrag at line 12

    static class ConstantInt extends Constant {
      private int value;
      public ConstantInt(int i) { this.value = i; }
      int intValue() { return value; }
      long longValue() { return value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Integer(value).toString(); }
      Literal buildLiteral() { return new IntegerLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 22

    static class ConstantLong extends Constant {
      private long value;
      public ConstantLong(long l) { this.value = l; }
      int intValue() { return (int)value; }
      long longValue() { return value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Long(value).toString(); }
      Literal buildLiteral() { return new LongLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 32

    static class ConstantFloat extends Constant {
      private float value;
      public ConstantFloat(float f) { this.value = f; }
      int intValue() { return (int)value; }
      long longValue() { return (long)value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Float(value).toString(); }
      Literal buildLiteral() { return new FloatingPointLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 42

    static class ConstantDouble extends Constant {
      private double value;
      public ConstantDouble(double d) { this.value = d; }
      int intValue() { return (int)value; }
      long longValue() { return (long)value; }
      float floatValue() { return (float)value; }
      double doubleValue() { return value; }
      String stringValue() { return new Double(value).toString(); }
      Literal buildLiteral() { return new DoubleLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 52

    static class ConstantChar extends Constant {
      private char value;
      public ConstantChar(char c) { this.value = c; }
      int intValue() { return value; }
      long longValue() { return value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Character(value).toString(); }
      Literal buildLiteral() { return new CharacterLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 62

    static class ConstantBoolean extends Constant {
      private boolean value;
      public ConstantBoolean(boolean b) { this.value = b; }
      boolean booleanValue() { return value; }
      String stringValue() { return new Boolean(value).toString(); }
      Literal buildLiteral() { return new BooleanLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 69

    static class ConstantString extends Constant {
      private String value;
      public ConstantString(String s) { this.value = s; }
      String stringValue() { return value; }
      Literal buildLiteral() { return new StringLiteral(stringValue()); }
    }

    // Declared in ConstantExpression.jrag at line 76


    int intValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 77

    long longValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 78

    float floatValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 79

    double doubleValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 80

    boolean booleanValue() { throw new UnsupportedOperationException(getClass().getName()); }

    // Declared in ConstantExpression.jrag at line 81

    String stringValue() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 82

    Literal buildLiteral() { throw new UnsupportedOperationException(); }

    // Declared in ConstantExpression.jrag at line 84

      
    protected Constant() {
    }

    // Declared in ConstantExpression.jrag at line 87

    
    public boolean error = false;

    // Declared in ConstantExpression.jrag at line 89


    static Constant create(int i) { return new ConstantInt(i); }

    // Declared in ConstantExpression.jrag at line 90

    static Constant create(long l) { return new ConstantLong(l); }

    // Declared in ConstantExpression.jrag at line 91

    static Constant create(float f) { return new ConstantFloat(f); }

    // Declared in ConstantExpression.jrag at line 92

    static Constant create(double d) { return new ConstantDouble(d); }

    // Declared in ConstantExpression.jrag at line 93

    static Constant create(boolean b) { return new ConstantBoolean(b); }

    // Declared in ConstantExpression.jrag at line 94

    static Constant create(char c) { return new ConstantChar(c); }

    // Declared in ConstantExpression.jrag at line 95

    static Constant create(String s) { return new ConstantString(s); }


}
