
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public abstract class AbstractWildcardType extends TypeDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        getSootClassDecl_computed = false;
        getSootClassDecl_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public AbstractWildcardType clone() throws CloneNotSupportedException {
        AbstractWildcardType node = (AbstractWildcardType)super.clone();
        node.getSootClassDecl_computed = false;
        node.getSootClassDecl_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 22

    public AbstractWildcardType() {
        super();

        setChild(new List(), 1);

    }

    // Declared in Generics.ast at line 11


    // Declared in Generics.ast line 22
    public AbstractWildcardType(Modifiers p0, String p1, List<BodyDecl> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
    }

    // Declared in Generics.ast at line 18


    // Declared in Generics.ast line 22
    public AbstractWildcardType(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
    }

    // Declared in Generics.ast at line 24


  protected int numChildren() {
    return 2;
  }

    // Declared in Generics.ast at line 27

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 38
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
    // Declared in java.ast line 38
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
    // Declared in java.ast line 38
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 1);
    }

    // Declared in java.ast at line 6


    public int getNumBodyDecl() {
        return getBodyDeclList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public BodyDecl getBodyDecl(int i) {
        return (BodyDecl)getBodyDeclList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addBodyDecl(BodyDecl node) {
        List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addBodyDeclNoTransform(BodyDecl node) {
        List<BodyDecl> list = getBodyDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setBodyDecl(BodyDecl node, int i) {
        List<BodyDecl> list = getBodyDeclList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<BodyDecl> getBodyDecls() {
        return getBodyDeclList();
    }

    // Declared in java.ast at line 31

    public List<BodyDecl> getBodyDeclsNoTransform() {
        return getBodyDeclListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclList() {
        List<BodyDecl> list = (List<BodyDecl>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(1);
    }

    // Declared in GenericsSubtype.jrag at line 103
 @SuppressWarnings({"unchecked", "cast"})     public boolean isWildcard() {
        ASTNode$State state = state();
        boolean isWildcard_value = isWildcard_compute();
        return isWildcard_value;
    }

    private boolean isWildcard_compute() {  return true;  }

    // Declared in GenericsCodegen.jrag at line 424
 @SuppressWarnings({"unchecked", "cast"})     public SootClass getSootClassDecl() {
        if(getSootClassDecl_computed) {
            return getSootClassDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getSootClassDecl_value = getSootClassDecl_compute();
        if(isFinal && num == state().boundariesCrossed)
            getSootClassDecl_computed = true;
        return getSootClassDecl_value;
    }

    private SootClass getSootClassDecl_compute() {  return typeObject().getSootClassDecl();  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
