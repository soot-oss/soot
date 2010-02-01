
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class CONSTANT_Long_Info extends CONSTANT_Info {
    // Declared in BytecodeCONSTANT.jrag at line 158

    public long value;

    // Declared in BytecodeCONSTANT.jrag at line 160


    public CONSTANT_Long_Info(BytecodeParser parser) {
      super(parser);
      value = p.readLong();
    }

    // Declared in BytecodeCONSTANT.jrag at line 165


    public String toString() {
      return "LongInfo: " + Long.toString(value);
    }

    // Declared in BytecodeCONSTANT.jrag at line 169


    public Expr expr() {
      //return new LongLiteral(Long.toString(value));
      return new LongLiteral("0x" + Long.toHexString(value));
    }


}
