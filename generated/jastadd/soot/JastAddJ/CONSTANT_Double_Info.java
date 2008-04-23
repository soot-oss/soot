
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;

public class CONSTANT_Double_Info extends CONSTANT_Info {
    // Declared in BytecodeCONSTANT.jrag at line 70

    public double value;

    // Declared in BytecodeCONSTANT.jrag at line 72


    public CONSTANT_Double_Info(BytecodeParser parser) {
      super(parser);
      value = this.p.readDouble();
    }

    // Declared in BytecodeCONSTANT.jrag at line 77


    public String toString() {
      return "DoubleInfo: " + Double.toString(value);
    }

    // Declared in BytecodeCONSTANT.jrag at line 81


    public Expr expr() {
      return new DoubleLiteral(Double.toString(value));
    }


}
