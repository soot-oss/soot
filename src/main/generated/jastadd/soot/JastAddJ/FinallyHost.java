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
  * @ast interface
 * 
 */
public interface FinallyHost {

    //public Block getFinally();
     
    //public Block getFinally();
    public boolean isDUafterFinally(Variable v);

     
    public boolean isDAafterFinally(Variable v);


  public void emitFinallyCode(Body b);

  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:319
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label_finally_block();
}
