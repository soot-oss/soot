package soot.JastAddJ;

import java.util.HashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;

/**
  * @ast class
 * 
 */
public class NameType extends java.lang.Object {

    private NameType() {
      super();
    }


    public static final NameType NO_NAME = new NameType();


    public static final NameType PACKAGE_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new PackageAccess(name, start, end); }
    };


    public static final NameType TYPE_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new TypeAccess(name, start, end); }
    };


    public static final NameType PACKAGE_OR_TYPE_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new PackageOrTypeAccess(name, start, end); }
    };


    public static final NameType AMBIGUOUS_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new AmbiguousAccess(name, start, end); }
    };


    public static final NameType METHOD_NAME = new NameType();


    public static final NameType ARRAY_TYPE_NAME = new NameType();


    public static final NameType ARRAY_READ_NAME = new NameType();


    public static final NameType EXPRESSION_NAME = new NameType() {
      public Access reclassify(String name, int start, int end) { return new VarAccess(name, start, end); }
    };



    public Access reclassify(String name, int start, int end) {
      throw new Error("Can not reclassify ParseName node " + name);
    }


}
