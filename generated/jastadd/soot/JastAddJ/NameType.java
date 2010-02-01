
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;
// NameType is basically an Enum for the different kinds of names
  // The factory method reclassify builds name nodes of a particular kind
public class NameType extends java.lang.Object {
    // Declared in SyntacticClassification.jrag at line 26

    private NameType() {
      super();
    }

    // Declared in SyntacticClassification.jrag at line 29

    public static final NameType NO_NAME = new NameType();

    // Declared in SyntacticClassification.jrag at line 30

    public static final NameType PACKAGE_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new PackageAccess(name, start, end); }
    };

    // Declared in SyntacticClassification.jrag at line 33

    public static final NameType TYPE_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new TypeAccess(name, start, end); }
    };

    // Declared in SyntacticClassification.jrag at line 36

    public static final NameType PACKAGE_OR_TYPE_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new PackageOrTypeAccess(name, start, end); }
    };

    // Declared in SyntacticClassification.jrag at line 39

    public static final NameType AMBIGUOUS_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new AmbiguousAccess(name, start, end); }
    };

    // Declared in SyntacticClassification.jrag at line 42

    public static final NameType METHOD_NAME = new NameType();

    // Declared in SyntacticClassification.jrag at line 43

    public static final NameType ARRAY_TYPE_NAME = new NameType();

    // Declared in SyntacticClassification.jrag at line 44

    public static final NameType ARRAY_READ_NAME = new NameType();

    // Declared in SyntacticClassification.jrag at line 45

    public static final NameType EXPRESSION_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new VarAccess(name, start, end); }
    };

    // Declared in SyntacticClassification.jrag at line 49


    public Access reclassify(String name, int start, int end) {
      throw new Error("Can not reclassify ParseName node " + name);
    }


}
