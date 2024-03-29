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
public class CONSTANT_Double_Info extends CONSTANT_Info {

    public double value;



    public CONSTANT_Double_Info(BytecodeParser parser) {
      super(parser);
      value = this.p.readDouble();
    }



    public String toString() {
      return "DoubleInfo: " + Double.toString(value);
    }



    public Expr expr() {
      return Literal.buildDoubleLiteral(value);
    }


}
