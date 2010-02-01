
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class CONSTANT_Info extends java.lang.Object {
    // Declared in BytecodeCONSTANT.jrag at line 109

    protected BytecodeParser p;

    // Declared in BytecodeCONSTANT.jrag at line 110

    public CONSTANT_Info(BytecodeParser parser) {
      p = parser;

    }

    // Declared in BytecodeCONSTANT.jrag at line 114

    public Expr expr() {
      throw new Error("CONSTANT_info.expr() should not be computed for " + getClass().getName());
    }

    // Declared in BytecodeCONSTANT.jrag at line 117

    public Expr exprAsBoolean() {
      return expr();
    }


}
