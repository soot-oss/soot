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
import soot.tagkit.SourceFileTag;
/**
  * @ast class
 * 
 */
public class CONSTANT_Float_Info extends CONSTANT_Info {

    public float value;



    public CONSTANT_Float_Info(BytecodeParser parser) {
      super(parser);
      value = p.readFloat();
    }



    public String toString() {
      return "FloatInfo: " + Float.toString(value);
    }



    public Expr expr() {
      return Literal.buildFloatLiteral(value);
    }


}
