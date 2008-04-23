
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;

public class CONSTANT_NameAndType_Info extends CONSTANT_Info {
    // Declared in BytecodeCONSTANT.jrag at line 201

    public int name_index;

    // Declared in BytecodeCONSTANT.jrag at line 202

    public int descriptor_index;

    // Declared in BytecodeCONSTANT.jrag at line 204


    public CONSTANT_NameAndType_Info(BytecodeParser parser) {
      super(parser);
      name_index = p.u2();
      descriptor_index = p.u2();
    }

    // Declared in BytecodeCONSTANT.jrag at line 210


    public String toString() {
      return "NameAndTypeInfo: " + name_index + " " + descriptor_index;
    }


}
