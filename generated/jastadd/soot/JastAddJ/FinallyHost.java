
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public interface FinallyHost {
    // Declared in DefiniteAssignment.jrag at line 911

    //public Block getFinally();
    public boolean isDUafterFinally(Variable v);

    // Declared in DefiniteAssignment.jrag at line 912

    public boolean isDAafterFinally(Variable v);

    // Declared in Statements.jrag at line 320


  public void emitFinallyCode(Body b);


    // Declared in Statements.jrag at line 318
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_finally_block();
}
