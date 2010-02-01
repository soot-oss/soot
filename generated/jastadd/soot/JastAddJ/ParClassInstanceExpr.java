
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class ParClassInstanceExpr extends ClassInstanceExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParClassInstanceExpr clone() throws CloneNotSupportedException {
        ParClassInstanceExpr node = (ParClassInstanceExpr)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParClassInstanceExpr copy() {
      try {
          ParClassInstanceExpr node = (ParClassInstanceExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParClassInstanceExpr fullCopy() {
        ParClassInstanceExpr res = (ParClassInstanceExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in GenericMethods.jrag at line 155

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
    // Declared in GenericMethods.ast line 15

    public ParClassInstanceExpr() {
        super();

        setChild(new List(), 1);
        setChild(new Opt(), 2);
        setChild(new List(), 3);

    }

    // Declared in GenericMethods.ast at line 13


    // Declared in GenericMethods.ast line 15
    public ParClassInstanceExpr(Access p0, List<Expr> p1, Opt<TypeDecl> p2, List<Access> p3) {
        setChild(p0, 0);
        setChild(p1, 1);
        setChild(p2, 2);
        setChild(p3, 3);
    }

    // Declared in GenericMethods.ast at line 20


  protected int numChildren() {
    return 4;
  }

    // Declared in GenericMethods.ast at line 23

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 34
    public void setAccess(Access node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Access getAccess() {
        return (Access)getChild(0);
    }

    // Declared in java.ast at line 9


    public Access getAccessNoTransform() {
        return (Access)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 34
    public void setArgList(List<Expr> list) {
        setChild(list, 1);
    }

    // Declared in java.ast at line 6


    public int getNumArg() {
        return getArgList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Expr getArg(int i) {
        return (Expr)getArgList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addArg(Expr node) {
        List<Expr> list = (parent == null || state == null) ? getArgListNoTransform() : getArgList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addArgNoTransform(Expr node) {
        List<Expr> list = getArgListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setArg(Expr node, int i) {
        List<Expr> list = getArgList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Expr> getArgs() {
        return getArgList();
    }

    // Declared in java.ast at line 31

    public List<Expr> getArgsNoTransform() {
        return getArgListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Expr> getArgList() {
        List<Expr> list = (List<Expr>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Expr> getArgListNoTransform() {
        return (List<Expr>)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 34
    public void setTypeDeclOpt(Opt<TypeDecl> opt) {
        setChild(opt, 2);
    }

    // Declared in java.ast at line 6


    public boolean hasTypeDecl() {
        return getTypeDeclOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public TypeDecl getTypeDecl() {
        return (TypeDecl)getTypeDeclOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setTypeDecl(TypeDecl node) {
        getTypeDeclOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<TypeDecl> getTypeDeclOpt() {
        return (Opt<TypeDecl>)getChild(2);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<TypeDecl> getTypeDeclOptNoTransform() {
        return (Opt<TypeDecl>)getChildNoTransform(2);
    }

    // Declared in GenericMethods.ast at line 2
    // Declared in GenericMethods.ast line 15
    public void setTypeArgumentList(List<Access> list) {
        setChild(list, 3);
    }

    // Declared in GenericMethods.ast at line 6


    public int getNumTypeArgument() {
        return getTypeArgumentList().getNumChild();
    }

    // Declared in GenericMethods.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getTypeArgument(int i) {
        return (Access)getTypeArgumentList().getChild(i);
    }

    // Declared in GenericMethods.ast at line 14


    public void addTypeArgument(Access node) {
        List<Access> list = (parent == null || state == null) ? getTypeArgumentListNoTransform() : getTypeArgumentList();
        list.addChild(node);
    }

    // Declared in GenericMethods.ast at line 19


    public void addTypeArgumentNoTransform(Access node) {
        List<Access> list = getTypeArgumentListNoTransform();
        list.addChild(node);
    }

    // Declared in GenericMethods.ast at line 24


    public void setTypeArgument(Access node, int i) {
        List<Access> list = getTypeArgumentList();
        list.setChild(node, i);
    }

    // Declared in GenericMethods.ast at line 28

    public List<Access> getTypeArguments() {
        return getTypeArgumentList();
    }

    // Declared in GenericMethods.ast at line 31

    public List<Access> getTypeArgumentsNoTransform() {
        return getTypeArgumentListNoTransform();
    }

    // Declared in GenericMethods.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeArgumentList() {
        List<Access> list = (List<Access>)getChild(3);
        list.getNumChild();
        return list;
    }

    // Declared in GenericMethods.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeArgumentListNoTransform() {
        return (List<Access>)getChildNoTransform(3);
    }

    // Declared in GenericMethods.jrag at line 121
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeArgumentListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        return super.Define_NameType_nameType(caller, child);
    }

    // Declared in GenericMethods.jrag at line 122
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
