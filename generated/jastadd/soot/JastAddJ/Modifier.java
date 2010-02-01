
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class Modifier extends ASTNode<ASTNode> implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public Modifier clone() throws CloneNotSupportedException {
        Modifier node = (Modifier)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Modifier copy() {
      try {
          Modifier node = (Modifier)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Modifier fullCopy() {
        Modifier res = (Modifier)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 441

     
  public void toString(StringBuffer s) {
    s.append(getID());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 194

    public Modifier() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 194
    public Modifier(String p0) {
        setID(p0);
    }

    // Declared in java.ast at line 15


    // Declared in java.ast line 194
    public Modifier(beaver.Symbol p0) {
        setID(p0);
    }

    // Declared in java.ast at line 19


  protected int numChildren() {
    return 0;
  }

    // Declared in java.ast at line 22

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 194
    protected String tokenString_ID;

    // Declared in java.ast at line 3

    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 6

    public int IDstart;

    // Declared in java.ast at line 7

    public int IDend;

    // Declared in java.ast at line 8

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 15

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in PrettyPrint.jadd at line 814
 @SuppressWarnings({"unchecked", "cast"})     public String dumpString() {
        ASTNode$State state = state();
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return getClass().getName() + " [" + getID() + "]";  }

    // Declared in AnnotationsCodegen.jrag at line 143
 @SuppressWarnings({"unchecked", "cast"})     public boolean isRuntimeVisible() {
        ASTNode$State state = state();
        boolean isRuntimeVisible_value = isRuntimeVisible_compute();
        return isRuntimeVisible_value;
    }

    private boolean isRuntimeVisible_compute() {  return false;  }

    // Declared in AnnotationsCodegen.jrag at line 153
 @SuppressWarnings({"unchecked", "cast"})     public boolean isRuntimeInvisible() {
        ASTNode$State state = state();
        boolean isRuntimeInvisible_value = isRuntimeInvisible_compute();
        return isRuntimeInvisible_value;
    }

    private boolean isRuntimeInvisible_compute() {  return false;  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
