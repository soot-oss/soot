
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public interface Flags {
    // Declared in BytecodeReader.jrag at line 851

    public int ACC_PUBLIC = 0x0001;

    // Declared in BytecodeReader.jrag at line 852

    public int ACC_PRIVATE = 0x0002;

    // Declared in BytecodeReader.jrag at line 853

    public int ACC_PROTECTED = 0x0004;

    // Declared in BytecodeReader.jrag at line 854

    public int ACC_STATIC = 0x0008;

    // Declared in BytecodeReader.jrag at line 855

    public int ACC_FINAL = 0x0010;

    // Declared in BytecodeReader.jrag at line 856

    public int ACC_SUPER = 0x0020;

    // Declared in BytecodeReader.jrag at line 857

    public int ACC_SYNCHRONIZED = 0x0020;

    // Declared in BytecodeReader.jrag at line 858

    public int ACC_VOLATILE = 0x0040;

    // Declared in BytecodeReader.jrag at line 859

    public int ACC_BRIDGE = 0x0040;

    // Declared in BytecodeReader.jrag at line 860

    public int ACC_TRANSIENT = 0x0080;

    // Declared in BytecodeReader.jrag at line 861

    public int ACC_VARARGS = 0x0080;

    // Declared in BytecodeReader.jrag at line 862

    public int ACC_NATIVE = 0x0100;

    // Declared in BytecodeReader.jrag at line 863

    public int ACC_INTERFACE = 0x0200;

    // Declared in BytecodeReader.jrag at line 864

    public int ACC_ABSTRACT = 0x0400;

    // Declared in BytecodeReader.jrag at line 865

    public int ACC_STRICT = 0x0800;

    // Declared in BytecodeReader.jrag at line 866

    public int ACC_SYNTHETIC = 0x1000;

    // Declared in BytecodeReader.jrag at line 867

    public int ACC_ANNOTATION = 0x2000;

    // Declared in BytecodeReader.jrag at line 868

    public int ACC_ENUM = 0x4000;

}
