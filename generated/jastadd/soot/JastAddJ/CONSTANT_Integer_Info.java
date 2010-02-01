
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class CONSTANT_Integer_Info extends CONSTANT_Info {
    // Declared in BytecodeCONSTANT.jrag at line 123

    public int value;

    // Declared in BytecodeCONSTANT.jrag at line 125


    public CONSTANT_Integer_Info(BytecodeParser parser) {
      super(parser);
      value = p.readInt();
    }

    // Declared in BytecodeCONSTANT.jrag at line 130


    public String toString() {
      return "IntegerInfo: " + Integer.toString(value);
    }

    // Declared in BytecodeCONSTANT.jrag at line 134


    public Expr expr() {
      //return new IntegerLiteral(Integer.toString(value));
      return new IntegerLiteral("0x" + Integer.toHexString(value));
    }

    // Declared in BytecodeCONSTANT.jrag at line 138

    public Expr exprAsBoolean() {
      return new BooleanLiteral(value == 0 ? "false" : "true");
    }


}
