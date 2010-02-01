
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class FieldDecl extends MemberDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public FieldDecl clone() throws CloneNotSupportedException {
        FieldDecl node = (FieldDecl)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public FieldDecl copy() {
      try {
          FieldDecl node = (FieldDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public FieldDecl fullCopy() {
        FieldDecl res = (FieldDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in java.ast at line 3
    // Declared in java.ast line 76

    public FieldDecl() {
        super();

        setChild(new List(), 2);

    }

    // Declared in java.ast at line 11


    // Declared in java.ast line 76
    public FieldDecl(Modifiers p0, Access p1, List<VariableDecl> p2) {
        setChild(p0, 0);
        setChild(p1, 1);
        setChild(p2, 2);
    }

    // Declared in java.ast at line 17


  protected int numChildren() {
    return 3;
  }

    // Declared in java.ast at line 20

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 76
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
    // Declared in java.ast line 76
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
    // Declared in java.ast line 76
    public void setVariableDeclList(List<VariableDecl> list) {
        setChild(list, 2);
    }

    // Declared in java.ast at line 6


    public int getNumVariableDecl() {
        return getVariableDeclList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public VariableDecl getVariableDecl(int i) {
        return (VariableDecl)getVariableDeclList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addVariableDecl(VariableDecl node) {
        List<VariableDecl> list = (parent == null || state == null) ? getVariableDeclListNoTransform() : getVariableDeclList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addVariableDeclNoTransform(VariableDecl node) {
        List<VariableDecl> list = getVariableDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setVariableDecl(VariableDecl node, int i) {
        List<VariableDecl> list = getVariableDeclList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<VariableDecl> getVariableDecls() {
        return getVariableDeclList();
    }

    // Declared in java.ast at line 31

    public List<VariableDecl> getVariableDeclsNoTransform() {
        return getVariableDeclListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<VariableDecl> getVariableDeclList() {
        List<VariableDecl> list = (List<VariableDecl>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<VariableDecl> getVariableDeclListNoTransform() {
        return (List<VariableDecl>)getChildNoTransform(2);
    }

    // Declared in Modifiers.jrag at line 241
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStatic() {
        ASTNode$State state = state();
        boolean isStatic_value = isStatic_compute();
        return isStatic_value;
    }

    private boolean isStatic_compute() {  return getModifiers().isStatic();  }

    // Declared in SyntacticClassification.jrag at line 77
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeAccessNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

    // Declared in TypeAnalysis.jrag at line 257
    public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
        if(caller == getVariableDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return null;
        }
        return getParent().Define_TypeDecl_declType(this, caller);
    }

public ASTNode rewriteTo() {
    // Declared in VariableDeclaration.jrag at line 109
    if(getNumVariableDecl() == 1) {
        state().duringVariableDeclaration++;
        ASTNode result = rewriteRule0();
        state().duringVariableDeclaration--;
        return result;
    }

    // Declared in VariableDeclaration.jrag at line 120
    if(getParent().getParent() instanceof TypeDecl && 
        ((TypeDecl)getParent().getParent()).getBodyDeclListNoTransform() == getParent() && getNumVariableDecl() > 1) {
        state().duringVariableDeclaration++;
      List list = (List)getParent();
      int i = list.getIndexOfChild(this);
      List newList = rewriteTypeDecl_getBodyDecl();
      for(int j = 1; j < newList.getNumChildNoTransform(); j++)
        list.insertChild(newList.getChildNoTransform(j), ++i);
        state().duringVariableDeclaration--;
      return newList.getChildNoTransform(0);
    }
    return super.rewriteTo();
}

    // Declared in VariableDeclaration.jrag at line 109
    private FieldDeclaration rewriteRule0() {
{
      FieldDeclaration decl = getVariableDecl(0).createFieldDeclarationFrom(getModifiers(), getTypeAccess());
      decl.setStart(start); // copy location information
      decl.setEnd(end); // copy location information
      return decl;
    }    }
    // Declared in VariableDeclaration.jrag at line 120
    private List rewriteTypeDecl_getBodyDecl() {
{
      List varList = new List();
      for(int j = 0; j < getNumVariableDecl(); j++) {
        FieldDeclaration f = 
          getVariableDecl(j).createFieldDeclarationFrom(
            (Modifiers)getModifiers().fullCopy(),
            (Access)getTypeAccess().fullCopy()
          );
        if(j == 0)
          f.setStart(start);
        else {
          f.getModifiersNoTransform().clearLocations();
          f.getTypeAccessNoTransform().clearLocations();
        }
        varList.add(f);
      }
      return varList;
    }    }
}
