
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class CONSTANT_NameAndType_Info extends CONSTANT_Info {
    // Declared in BytecodeCONSTANT.jrag at line 190

    public int name_index;

    // Declared in BytecodeCONSTANT.jrag at line 191

    public int descriptor_index;

    // Declared in BytecodeCONSTANT.jrag at line 193


    public CONSTANT_NameAndType_Info(BytecodeParser parser) {
      super(parser);
      name_index = p.u2();
      descriptor_index = p.u2();
    }

    // Declared in BytecodeCONSTANT.jrag at line 199


    public String toString() {
      return "NameAndTypeInfo: " + name_index + " " + descriptor_index;
    }


}
