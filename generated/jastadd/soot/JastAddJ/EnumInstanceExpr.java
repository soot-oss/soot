
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class EnumInstanceExpr extends ClassInstanceExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        getAccess_computed = false;
        getAccess_value = null;
        getArgList_computed = false;
        getArgList_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public EnumInstanceExpr clone() throws CloneNotSupportedException {
        EnumInstanceExpr node = (EnumInstanceExpr)super.clone();
        node.getAccess_computed = false;
        node.getAccess_value = null;
        node.getArgList_computed = false;
        node.getArgList_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public EnumInstanceExpr copy() {
      try {
          EnumInstanceExpr node = (EnumInstanceExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public EnumInstanceExpr fullCopy() {
        EnumInstanceExpr res = (EnumInstanceExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Enums.ast at line 3
    // Declared in Enums.ast line 5

    public EnumInstanceExpr() {
        super();

        setChild(new Opt(), 0);
        setChild(new List(), 2);

    }

    // Declared in Enums.ast at line 12


    // Declared in Enums.ast line 5
    public EnumInstanceExpr(Opt<TypeDecl> p0) {
        setChild(p0, 0);
        setChild(null, 1);
        setChild(new List(), 2);
    }

    // Declared in Enums.ast at line 18


  protected int numChildren() {
    return 1;
  }

    // Declared in Enums.ast at line 21

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 5
    public void setTypeDeclOpt(Opt<TypeDecl> opt) {
        setChild(opt, 0);
    }

    // Declared in Enums.ast at line 6


    public boolean hasTypeDecl() {
        return getTypeDeclOpt().getNumChild() != 0;
    }

    // Declared in Enums.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public TypeDecl getTypeDecl() {
        return (TypeDecl)getTypeDeclOpt().getChild(0);
    }

    // Declared in Enums.ast at line 14


    public void setTypeDecl(TypeDecl node) {
        getTypeDeclOpt().setChild(node, 0);
    }

    // Declared in Enums.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<TypeDecl> getTypeDeclOpt() {
        return (Opt<TypeDecl>)getChild(0);
    }

    // Declared in Enums.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<TypeDecl> getTypeDeclOptNoTransform() {
        return (Opt<TypeDecl>)getChildNoTransform(0);
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 5
    public void setAccess(Access node) {
        setChild(node, 1);
    }

    // Declared in Enums.ast at line 5

    public Access getAccessNoTransform() {
        return (Access)getChildNoTransform(1);
    }

    // Declared in Enums.ast at line 9


    protected int getAccessChildPosition() {
        return 1;
    }

    // Declared in Enums.ast at line 2
    // Declared in Enums.ast line 5
    public void setArgList(List<Expr> list) {
        setChild(list, 2);
    }

    // Declared in Enums.ast at line 6


    public int getNumArg() {
        return getArgList().getNumChild();
    }

    // Declared in Enums.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Expr getArg(int i) {
        return (Expr)getArgList().getChild(i);
    }

    // Declared in Enums.ast at line 14


    public void addArg(Expr node) {
        List<Expr> list = (parent == null || state == null) ? getArgListNoTransform() : getArgList();
        list.addChild(node);
    }

    // Declared in Enums.ast at line 19


    public void addArgNoTransform(Expr node) {
        List<Expr> list = getArgListNoTransform();
        list.addChild(node);
    }

    // Declared in Enums.ast at line 24


    public void setArg(Expr node, int i) {
        List<Expr> list = getArgList();
        list.setChild(node, i);
    }

    // Declared in Enums.ast at line 28

    public List<Expr> getArgs() {
        return getArgList();
    }

    // Declared in Enums.ast at line 31

    public List<Expr> getArgsNoTransform() {
        return getArgListNoTransform();
    }

    // Declared in Enums.ast at line 35


    public List<Expr> getArgListNoTransform() {
        return (List<Expr>)getChildNoTransform(2);
    }

    // Declared in Enums.ast at line 39


    protected int getArgListChildPosition() {
        return 2;
    }

    protected boolean getAccess_computed = false;
    protected Access getAccess_value;
    // Declared in Enums.jrag at line 196
 @SuppressWarnings({"unchecked", "cast"})     public Access getAccess() {
        if(getAccess_computed) {
            return (Access)ASTNode.getChild(this, getAccessChildPosition());
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getAccess_value = getAccess_compute();
            setAccess(getAccess_value);
        if(isFinal && num == state().boundariesCrossed)
            getAccess_computed = true;
        return (Access)ASTNode.getChild(this, getAccessChildPosition());
    }

    private Access getAccess_compute() {
	  return hostType().createQualifiedAccess();
  }

    protected boolean getArgList_computed = false;
    protected List<Expr> getArgList_value;
    // Declared in Enums.jrag at line 200
 @SuppressWarnings({"unchecked", "cast"})     public List<Expr> getArgList() {
        if(getArgList_computed) {
            return (List<Expr>)ASTNode.getChild(this, getArgListChildPosition());
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getArgList_value = getArgList_compute();
        setArgList(getArgList_value);
        if(isFinal && num == state().boundariesCrossed)
            getArgList_computed = true;
        return (List<Expr>)ASTNode.getChild(this, getArgListChildPosition());
    }

    private List<Expr> getArgList_compute() {
	  EnumConstant ec = (EnumConstant)getParent().getParent();
	  List<EnumConstant> ecs = (List<EnumConstant>)ec.getParent();
	  int idx = ecs.getIndexOfChild(ec);
	  if(idx == -1)
		  throw new Error("internal: cannot determine numeric value of enum constant");
	  List<Expr> argList = new List<Expr>();
	  argList.add(new StringLiteral(ec.name()));
	  argList.add(new IntegerLiteral(idx));
	  for(Expr arg : ec.getArgs())
    	argList.add((Expr)arg.fullCopy());
	  return argList;
  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
