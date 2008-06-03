
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;
 // Simplified FieldDecl


public class VarDeclStmt extends Stmt implements Cloneable {
    public void flushCache() {
        super.flushCache();
        canCompleteNormally_computed = false;
    }
     @SuppressWarnings({"unchecked", "cast"})  public VarDeclStmt clone() throws CloneNotSupportedException {
        VarDeclStmt node = (VarDeclStmt)super.clone();
        node.canCompleteNormally_computed = false;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public VarDeclStmt copy() {
      try {
          VarDeclStmt node = (VarDeclStmt)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public VarDeclStmt fullCopy() {
        VarDeclStmt res = (VarDeclStmt)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in VariableDeclaration.jrag at line 158


  private List createVariableDeclarationList() {
    List varList = new List();
    for(int j = 0; j < getNumVariableDecl(); j++) {
      VariableDeclaration v =
        getVariableDecl(j).createVariableDeclarationFrom(
          (Modifiers)getModifiers().fullCopy(),
          (Access)getTypeAccess().fullCopy()
        );
      if(j == 0)
        v.setStart(start);
      varList.add(v);
    }
    return varList;
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 79

    public VarDeclStmt() {
        super();

        setChild(new List(), 2);

    }

    // Declared in java.ast at line 11


    // Declared in java.ast line 79
    public VarDeclStmt(Modifiers p0, Access p1, List<VariableDecl> p2) {
        setChild(p0, 0);
        setChild(p1, 1);
        setChild(p2, 2);
    }

    // Declared in java.ast at line 17


  protected int numChildren() {
    return 3;
  }

    // Declared in java.ast at line 20

  public boolean mayHaveRewrite() { return true; }

    // Declared in java.ast at line 2
    // Declared in java.ast line 79
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
    // Declared in java.ast line 79
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
    // Declared in java.ast line 79
    public void setVariableDeclList(List<VariableDecl> list) {
        setChild(list, 2);
    }

    // Declared in java.ast at line 6


    private int getNumVariableDecl = 0;

    // Declared in java.ast at line 7

    public int getNumVariableDecl() {
        return getVariableDeclList().getNumChild();
    }

    // Declared in java.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public VariableDecl getVariableDecl(int i) {
        return (VariableDecl)getVariableDeclList().getChild(i);
    }

    // Declared in java.ast at line 15


    public void addVariableDecl(VariableDecl node) {
        List<VariableDecl> list = getVariableDeclList();
        list.addChild(node);
    }

    // Declared in java.ast at line 20


    public void setVariableDecl(VariableDecl node, int i) {
        List<VariableDecl> list = getVariableDeclList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 24

    public List<VariableDecl> getVariableDecls() {
        return getVariableDeclList();
    }

    // Declared in java.ast at line 27

    public List<VariableDecl> getVariableDeclsNoTransform() {
        return getVariableDeclListNoTransform();
    }

    // Declared in java.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<VariableDecl> getVariableDeclList() {
        return (List<VariableDecl>)getChild(2);
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<VariableDecl> getVariableDeclListNoTransform() {
        return (List<VariableDecl>)getChildNoTransform(2);
    }

    // Declared in UnreachableStatements.jrag at line 42
 @SuppressWarnings({"unchecked", "cast"})     public boolean canCompleteNormally() {
        if(canCompleteNormally_computed)
            return canCompleteNormally_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        canCompleteNormally_value = canCompleteNormally_compute();
        if(isFinal && num == boundariesCrossed)
            canCompleteNormally_computed = true;
        return canCompleteNormally_value;
    }

    private boolean canCompleteNormally_compute() {  return reachable();  }

    // Declared in TypeAnalysis.jrag at line 258
    public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
        if(caller == getVariableDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return null;
        }
        return getParent().Define_TypeDecl_declType(this, caller);
    }

    // Declared in SyntacticClassification.jrag at line 84
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeAccessNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

public ASTNode rewriteTo() {
    // Declared in VariableDeclaration.jrag at line 139
    if(getNumVariableDecl() == 1) {
        duringVariableDeclaration++;
        ASTNode result = rewriteRule0();
        duringVariableDeclaration--;
        return result;
    }

    // Declared in VariableDeclaration.jrag at line 150
    if(getParent().getParent() instanceof Block && 
        ((Block)getParent().getParent()).getStmtListNoTransform() == getParent() && getNumVariableDecl() > 1) {
        duringVariableDeclaration++;
      List newList = rewriteBlock_getStmt();
      List list = (List)getParent();
      int i = list.getIndexOfChild(this);
      for(int j = 1; j < newList.getNumChild(); j++)
        list.insertChild(newList.getChildNoTransform(j), ++i);
        duringVariableDeclaration--;
      return newList.getChildNoTransform(0);
    }
    // Declared in VariableDeclaration.jrag at line 154
    if(getParent().getParent() instanceof ForStmt && 
        ((ForStmt)getParent().getParent()).getInitStmtListNoTransform() == getParent() && getNumVariableDecl() > 1) {
        duringVariableDeclaration++;
      List newList = rewriteForStmt_getInitStmt();
      List list = (List)getParent();
      int i = list.getIndexOfChild(this);
      for(int j = 1; j < newList.getNumChild(); j++)
        list.insertChild(newList.getChildNoTransform(j), ++i);
        duringVariableDeclaration--;
      return newList.getChildNoTransform(0);
    }
    return super.rewriteTo();
}

    // Declared in VariableDeclaration.jrag at line 139
    private VariableDeclaration rewriteRule0() {
{
      VariableDeclaration decl = getVariableDecl(0).createVariableDeclarationFrom(getModifiers(), getTypeAccess());
      decl.setStart(start); // copy location information
      decl.setEnd(end); // copy location information
      return decl;
    }    }
    // Declared in VariableDeclaration.jrag at line 150
    private List rewriteBlock_getStmt() {
        return createVariableDeclarationList();
    }
    // Declared in VariableDeclaration.jrag at line 154
    private List rewriteForStmt_getInitStmt() {
        return createVariableDeclarationList();
    }
}
