
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;

public class CONSTANT_Float_Info extends CONSTANT_Info {
    // Declared in BytecodeCONSTANT.jrag at line 103

    public float value;

    // Declared in BytecodeCONSTANT.jrag at line 105


    public CONSTANT_Float_Info(BytecodeParser parser) {
      super(parser);
      value = p.readFloat();
    }

    // Declared in BytecodeCONSTANT.jrag at line 110


    public String toString() {
      return "FloatInfo: " + Float.toString(value);
    }

    // Declared in BytecodeCONSTANT.jrag at line 114


    public Expr expr() {
      return new FloatingPointLiteral(Float.toString(value));
    }


}
