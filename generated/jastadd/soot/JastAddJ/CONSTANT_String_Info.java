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
public class CONSTANT_String_Info extends CONSTANT_Info {

    public int string_index;



    public CONSTANT_String_Info(BytecodeParser parser) {
      super(parser);
      string_index = p.u2();
    }



    public Expr expr() {
      CONSTANT_Utf8_Info i = (CONSTANT_Utf8_Info)p.constantPool[string_index];
      return Literal.buildStringLiteral(i.string);
    }



    public String toString() {
      return "StringInfo: " + p.constantPool[string_index];
    }


}
