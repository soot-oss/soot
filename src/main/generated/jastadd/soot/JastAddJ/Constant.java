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
  * @ast class
 * 
 */
public class Constant extends java.lang.Object {

    static class ConstantInt extends Constant {
      private int value;
      public ConstantInt(int i) { this.value = i; }
      int intValue() { return value; }
      long longValue() { return value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Integer(value).toString(); }
    }


    static class ConstantLong extends Constant {
      private long value;
      public ConstantLong(long l) { this.value = l; }
      int intValue() { return (int)value; }
      long longValue() { return value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Long(value).toString(); }
    }


    static class ConstantFloat extends Constant {
      private float value;
      public ConstantFloat(float f) { this.value = f; }
      int intValue() { return (int)value; }
      long longValue() { return (long)value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Float(value).toString(); }
    }


    static class ConstantDouble extends Constant {
      private double value;
      public ConstantDouble(double d) { this.value = d; }
      int intValue() { return (int)value; }
      long longValue() { return (long)value; }
      float floatValue() { return (float)value; }
      double doubleValue() { return value; }
      String stringValue() { return new Double(value).toString(); }
    }


    static class ConstantChar extends Constant {
      private char value;
      public ConstantChar(char c) { this.value = c; }
      int intValue() { return value; }
      long longValue() { return value; }
      float floatValue() { return value; }
      double doubleValue() { return value; }
      String stringValue() { return new Character(value).toString(); }
    }


    static class ConstantBoolean extends Constant {
      private boolean value;
      public ConstantBoolean(boolean b) { this.value = b; }
      boolean booleanValue() { return value; }
      String stringValue() { return new Boolean(value).toString(); }
    }


    static class ConstantString extends Constant {
      private String value;
      public ConstantString(String s) { this.value = s; }
      String stringValue() { return value; }
    }



    int intValue() { throw new UnsupportedOperationException(); }


    long longValue() { throw new UnsupportedOperationException(); }


    float floatValue() { throw new UnsupportedOperationException(); }


    double doubleValue() { throw new UnsupportedOperationException(); }


    boolean booleanValue() { throw new UnsupportedOperationException(getClass().getName()); }


    String stringValue() { throw new UnsupportedOperationException(); }


      
    protected Constant() {
    }


    
    public boolean error = false;



    static Constant create(int i) { return new ConstantInt(i); }


    static Constant create(long l) { return new ConstantLong(l); }


    static Constant create(float f) { return new ConstantFloat(f); }


    static Constant create(double d) { return new ConstantDouble(d); }


    static Constant create(boolean b) { return new ConstantBoolean(b); }


    static Constant create(char c) { return new ConstantChar(c); }


    static Constant create(String s) { return new ConstantString(s); }


}
