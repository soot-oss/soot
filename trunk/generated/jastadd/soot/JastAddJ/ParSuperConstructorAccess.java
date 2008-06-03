
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;


public class ParSuperConstructorAccess extends SuperConstructorAccess implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParSuperConstructorAccess clone() throws CloneNotSupportedException {
        ParSuperConstructorAccess node = (ParSuperConstructorAccess)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParSuperConstructorAccess copy() {
      try {
          ParSuperConstructorAccess node = (ParSuperConstructorAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParSuperConstructorAccess fullCopy() {
        ParSuperConstructorAccess res = (ParSuperConstructorAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in GenericMethods.jrag at line 144

  public void toString(StringBuffer s) {
    s.append("<");
    for(int i = 0; i < getNumTypeArgument(); i++) {
      if(i != 0) s.append(", ");
      getTypeArgument(i).toString(s);
    }
    s.append(">");
    super.toString(s);
  }

    // Declared in GenericMethods.ast at line 3
    // Declared in GenericMethods.ast line 14

    public ParSuperConstructorAccess() {
        super();

        setChild(new List(), 0);
        setChild(new List(), 1);

    }

    // Declared in GenericMethods.ast at line 12


    // Declared in GenericMethods.ast line 14
    public ParSuperConstructorAccess(String p0, List<Expr> p1, List<Access> p2) {
        setID(p0);
        setChild(p1, 0);
        setChild(p2, 1);
    }

    // Declared in GenericMethods.ast at line 19


    // Declared in GenericMethods.ast line 14
    public ParSuperConstructorAccess(beaver.Symbol p0, List<Expr> p1, List<Access> p2) {
        setID(p0);
        setChild(p1, 0);
        setChild(p2, 1);
    }

    // Declared in GenericMethods.ast at line 25


  protected int numChildren() {
    return 2;
  }

    // Declared in GenericMethods.ast at line 28

  public boolean mayHaveRewrite() { return false; }

    // Declared in java.ast at line 2
    // Declared in java.ast line 18
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public int IDstart;

    // Declared in java.ast at line 6

    public int IDend;

    // Declared in java.ast at line 7

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 14

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 18
    public void setArgList(List<Expr> list) {
        setChild(list, 0);
    }

    // Declared in java.ast at line 6


    private int getNumArg = 0;

    // Declared in java.ast at line 7

    public int getNumArg() {
        return getArgList().getNumChild();
    }

    // Declared in java.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public Expr getArg(int i) {
        return (Expr)getArgList().getChild(i);
    }

    // Declared in java.ast at line 15


    public void addArg(Expr node) {
        List<Expr> list = getArgList();
        list.addChild(node);
    }

    // Declared in java.ast at line 20


    public void setArg(Expr node, int i) {
        List<Expr> list = getArgList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 24

    public List<Expr> getArgs() {
        return getArgList();
    }

    // Declared in java.ast at line 27

    public List<Expr> getArgsNoTransform() {
        return getArgListNoTransform();
    }

    // Declared in java.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<Expr> getArgList() {
        return (List<Expr>)getChild(0);
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Expr> getArgListNoTransform() {
        return (List<Expr>)getChildNoTransform(0);
    }

    // Declared in GenericMethods.ast at line 2
    // Declared in GenericMethods.ast line 14
    public void setTypeArgumentList(List<Access> list) {
        setChild(list, 1);
    }

    // Declared in GenericMethods.ast at line 6


    private int getNumTypeArgument = 0;

    // Declared in GenericMethods.ast at line 7

    public int getNumTypeArgument() {
        return getTypeArgumentList().getNumChild();
    }

    // Declared in GenericMethods.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public Access getTypeArgument(int i) {
        return (Access)getTypeArgumentList().getChild(i);
    }

    // Declared in GenericMethods.ast at line 15


    public void addTypeArgument(Access node) {
        List<Access> list = getTypeArgumentList();
        list.addChild(node);
    }

    // Declared in GenericMethods.ast at line 20


    public void setTypeArgument(Access node, int i) {
        List<Access> list = getTypeArgumentList();
        list.setChild(node, i);
    }

    // Declared in GenericMethods.ast at line 24

    public List<Access> getTypeArguments() {
        return getTypeArgumentList();
    }

    // Declared in GenericMethods.ast at line 27

    public List<Access> getTypeArgumentsNoTransform() {
        return getTypeArgumentListNoTransform();
    }

    // Declared in GenericMethods.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeArgumentList() {
        return (List<Access>)getChild(1);
    }

    // Declared in GenericMethods.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeArgumentListNoTransform() {
        return (List<Access>)getChildNoTransform(1);
    }

    // Declared in GenericMethods.jrag at line 105
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeArgumentListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        return super.Define_NameType_nameType(caller, child);
    }

    // Declared in GenericMethods.jrag at line 106
    public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
        if(caller == getTypeArgumentListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return unqualifiedScope().lookupType(name);
        }
        return super.Define_SimpleSet_lookupType(caller, child, name);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
