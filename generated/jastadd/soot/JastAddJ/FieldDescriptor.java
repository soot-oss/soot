
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class FieldDescriptor extends java.lang.Object {
    // Declared in BytecodeDescriptor.jrag at line 13

    private BytecodeParser p;

    // Declared in BytecodeDescriptor.jrag at line 14

    String typeDescriptor;

    // Declared in BytecodeDescriptor.jrag at line 16


    public FieldDescriptor(BytecodeParser parser, String name) {
      p = parser;
      int descriptor_index = p.u2();
      typeDescriptor = ((CONSTANT_Utf8_Info) p.constantPool[descriptor_index]).string();
      if(BytecodeParser.VERBOSE)
        p.println("  Field: " + name + ", " + typeDescriptor);
    }

    // Declared in BytecodeDescriptor.jrag at line 24


    public Access type() {
      return new TypeDescriptor(p, typeDescriptor).type();
    }

    // Declared in BytecodeDescriptor.jrag at line 28


    public boolean isBoolean() {
      return new TypeDescriptor(p, typeDescriptor).isBoolean();
    }


}
