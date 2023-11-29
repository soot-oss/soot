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
public class CONSTANT_Info extends java.lang.Object {

    protected BytecodeParser p;


    public CONSTANT_Info(BytecodeParser parser) {
      p = parser;

    }


    public Expr expr() {
      throw new Error("CONSTANT_info.expr() should not be computed for " + getClass().getName());
    }


    public Expr exprAsBoolean() {
      return expr();
    }


}
