
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;
 // Simplified VarDeclStmt


public class VariableDecl extends ASTNode<ASTNode> implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public VariableDecl clone() throws CloneNotSupportedException {
        VariableDecl node = (VariableDecl)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public VariableDecl copy() {
      try {
          VariableDecl node = (VariableDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public VariableDecl fullCopy() {
        VariableDecl res = (VariableDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in VariableDeclaration.jrag at line 189


  public VariableDeclaration createVariableDeclarationFrom(Modifiers modifiers, Access type) {
    VariableDeclaration decl = new VariableDeclaration(
      modifiers,
      type.addArrayDims(getDimsList()),
      getID(),
      getInitOpt()
    );
    decl.setStart(start); // copy location information
    decl.setEnd(end); // copy location information
    decl.IDstart = IDstart;
    decl.IDend = IDend;
    return decl;
  }

    // Declared in VariableDeclaration.jrag at line 203


  public FieldDeclaration createFieldDeclarationFrom(Modifiers modifiers, Access type) {
    FieldDeclaration decl = new FieldDeclaration(
      modifiers,
      type.addArrayDims(getDimsList()),
      getID(),
      getInitOpt()
    );
    decl.setStart(start); // copy location information
    decl.setEnd(end); // copy location information
    decl.IDstart = IDstart;
    decl.IDend = IDend;
    return decl;
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 82

    public VariableDecl() {
        super();

        setChild(new List(), 0);
        setChild(new Opt(), 1);

    }

    // Declared in java.ast at line 12


    // Declared in java.ast line 82
    public VariableDecl(String p0, List<Dims> p1, Opt<Expr> p2) {
        setID(p0);
        setChild(p1, 0);
        setChild(p2, 1);
    }

    // Declared in java.ast at line 19


    // Declared in java.ast line 82
    public VariableDecl(beaver.Symbol p0, List<Dims> p1, Opt<Expr> p2) {
        setID(p0);
        setChild(p1, 0);
        setChild(p2, 1);
    }

    // Declared in java.ast at line 25


  protected int numChildren() {
    return 2;
  }

    // Declared in java.ast at line 28

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 82
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

    // Declared in java.ast at line 2
    // Declared in java.ast line 82
    public void setDimsList(List<Dims> list) {
        setChild(list, 0);
    }

    // Declared in java.ast at line 6


    public int getNumDims() {
        return getDimsList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Dims getDims(int i) {
        return (Dims)getDimsList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addDims(Dims node) {
        List<Dims> list = (parent == null || state == null) ? getDimsListNoTransform() : getDimsList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addDimsNoTransform(Dims node) {
        List<Dims> list = getDimsListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setDims(Dims node, int i) {
        List<Dims> list = getDimsList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Dims> getDimss() {
        return getDimsList();
    }

    // Declared in java.ast at line 31

    public List<Dims> getDimssNoTransform() {
        return getDimsListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Dims> getDimsList() {
        List<Dims> list = (List<Dims>)getChild(0);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Dims> getDimsListNoTransform() {
        return (List<Dims>)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 82
    public void setInitOpt(Opt<Expr> opt) {
        setChild(opt, 1);
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
        return (Opt<Expr>)getChild(1);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getInitOptNoTransform() {
        return (Opt<Expr>)getChildNoTransform(1);
    }

    // Declared in VariableDeclaration.jrag at line 102
 @SuppressWarnings({"unchecked", "cast"})     public String name() {
        ASTNode$State state = state();
        String name_value = name_compute();
        return name_value;
    }

    private String name_compute() {  return getID();  }

    // Declared in DefiniteAssignment.jrag at line 41
    public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
        if(caller == getInitOptNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isSource(this, caller);
    }

    // Declared in InnerClasses.jrag at line 66
    public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
        if(caller == getInitOptNoTransform()) {
            return null;
        }
        return getParent().Define_TypeDecl_expectedType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
