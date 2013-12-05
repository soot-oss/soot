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
public class CONSTANT_Integer_Info extends CONSTANT_Info {

    public int value;



    public CONSTANT_Integer_Info(BytecodeParser parser) {
      super(parser);
      value = p.readInt();
    }



    public String toString() {
      return "IntegerInfo: " + Integer.toString(value);
    }



    public Expr expr() {
      return Literal.buildIntegerLiteral(value);
    }


    public Expr exprAsBoolean() {
      return Literal.buildBooleanLiteral(value == 0);
    }


}
