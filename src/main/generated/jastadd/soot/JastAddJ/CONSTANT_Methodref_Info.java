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
public class CONSTANT_Methodref_Info extends CONSTANT_Info {

    public int class_index;


    public int name_and_type_index;



    public CONSTANT_Methodref_Info(BytecodeParser parser) {
      super(parser);
      class_index = p.u2();
      name_and_type_index = p.u2();
    }



    public String toString() {
      return "MethodRefInfo: " + class_index + " " + name_and_type_index;
    }


}
