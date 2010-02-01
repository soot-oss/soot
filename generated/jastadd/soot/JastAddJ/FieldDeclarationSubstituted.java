
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class FieldDeclarationSubstituted extends FieldDeclaration implements Cloneable {
    public void flushCache() {
        super.flushCache();
        sourceVariableDecl_computed = false;
        sourceVariableDecl_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public FieldDeclarationSubstituted clone() throws CloneNotSupportedException {
        FieldDeclarationSubstituted node = (FieldDeclarationSubstituted)super.clone();
        node.sourceVariableDecl_computed = false;
        node.sourceVariableDecl_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public FieldDeclarationSubstituted copy() {
      try {
          FieldDeclarationSubstituted node = (FieldDeclarationSubstituted)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public FieldDeclarationSubstituted fullCopy() {
        FieldDeclarationSubstituted res = (FieldDeclarationSubstituted)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 29

    public FieldDeclarationSubstituted() {
        super();

        setChild(new Opt(), 2);

    }

    // Declared in Generics.ast at line 11


    // Declared in Generics.ast line 29
    public FieldDeclarationSubstituted(Modifiers p0, Access p1, String p2, Opt<Expr> p3, FieldDeclaration p4) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
        setChild(p3, 2);
        setOriginal(p4);
    }

    // Declared in Generics.ast at line 20


    // Declared in Generics.ast line 29
    public FieldDeclarationSubstituted(Modifiers p0, Access p1, beaver.Symbol p2, Opt<Expr> p3, FieldDeclaration p4) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
        setChild(p3, 2);
        setOriginal(p4);
    }

    // Declared in Generics.ast at line 28


  protected int numChildren() {
    return 3;
  }

    // Declared in Generics.ast at line 31

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 77
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in java.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 77
    public void setTypeAccess(Access node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Access getTypeAccess() {
        return (Access)getChild(1);
    }

    // Declared in java.ast at line 9


    public Access getTypeAccessNoTransform() {
        return (Access)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 77
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 77
    public void setInitOpt(Opt<Expr> opt) {
        setChild(opt, 2);
    }

    // Declared in java.ast at line 6


    public boolean hasInit() {
        return getInitOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Expr getInit() {
        return (Expr)getInitOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setInit(Expr node) {
        getInitOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getInitOpt() {
        return (Opt<Expr>)getChild(2);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getInitOptNoTransform() {
        return (Opt<Expr>)getChildNoTransform(2);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 29
    protected FieldDeclaration tokenFieldDeclaration_Original;

    // Declared in Generics.ast at line 3

    public void setOriginal(FieldDeclaration value) {
        tokenFieldDeclaration_Original = value;
    }

    // Declared in Generics.ast at line 6

    public FieldDeclaration getOriginal() {
        return tokenFieldDeclaration_Original;
    }

    // Declared in Generics.jrag at line 1276
 @SuppressWarnings({"unchecked", "cast"})     public Variable sourceVariableDecl() {
        if(sourceVariableDecl_computed) {
            return sourceVariableDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sourceVariableDecl_value = sourceVariableDecl_compute();
        if(isFinal && num == state().boundariesCrossed)
            sourceVariableDecl_computed = true;
        return sourceVariableDecl_value;
    }

    private Variable sourceVariableDecl_compute() {  return getOriginal().sourceVariableDecl();  }

    // Declared in GenericsCodegen.jrag at line 32
 @SuppressWarnings({"unchecked", "cast"})     public FieldDeclaration erasedField() {
        ASTNode$State state = state();
        FieldDeclaration erasedField_value = erasedField_compute();
        return erasedField_value;
    }

    private FieldDeclaration erasedField_compute() {  return getOriginal().erasedField();  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
