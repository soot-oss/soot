
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class CONSTANT_Class_Info extends CONSTANT_Info {
    // Declared in BytecodeCONSTANT.jrag at line 12

    public int name_index;

    // Declared in BytecodeCONSTANT.jrag at line 14


    public CONSTANT_Class_Info(BytecodeParser parser) {
      super(parser);
      name_index = p.u2();
    }

    // Declared in BytecodeCONSTANT.jrag at line 19


    public String toString() {
      return "ClassInfo: " + name();
    }

    // Declared in BytecodeCONSTANT.jrag at line 23


    public String name() {
      String name = ((CONSTANT_Utf8_Info) this.p.constantPool[name_index]).string();
      //name = name.replaceAll("\\/", ".");
      name = name.replace('/', '.');
      return name;
    }

    // Declared in BytecodeCONSTANT.jrag at line 30


    public String simpleName() {
      String name = name();
      //name = name.replace('$', '.');
      int pos = name.lastIndexOf('.');
      return name.substring(pos + 1, name.length());
    }

    // Declared in BytecodeCONSTANT.jrag at line 37


    public String packageDecl() {
      String name = name();
      //name = name.replace('$', '.');
      int pos = name.lastIndexOf('.');
      if(pos == -1)
        return "";
      return name.substring(0, pos);
    }

    // Declared in BytecodeCONSTANT.jrag at line 46


    public Access access() {
      String name = name();
      int pos = name.lastIndexOf('.');
      String typeName = name.substring(pos + 1, name.length());
      String packageName = pos == -1 ? "" : name.substring(0, pos);
      if(typeName.indexOf('$') != -1)
        return new BytecodeTypeAccess(packageName, typeName);
      else
        return new TypeAccess(packageName, typeName);
    }


}
