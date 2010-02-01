
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class GenericConstructorDecl extends ConstructorDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        getParConstructorDeclList_computed = false;
        getParConstructorDeclList_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericConstructorDecl clone() throws CloneNotSupportedException {
        GenericConstructorDecl node = (GenericConstructorDecl)super.clone();
        node.getParConstructorDeclList_computed = false;
        node.getParConstructorDeclList_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericConstructorDecl copy() {
      try {
          GenericConstructorDecl node = (GenericConstructorDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericConstructorDecl fullCopy() {
        GenericConstructorDecl res = (GenericConstructorDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in GenericMethods.jrag at line 203

  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);

    s.append(" <");
    for(int i = 0; i < getNumTypeParameter(); i++) {
      if(i != 0) s.append(", ");
      original().getTypeParameter(i).toString(s);
    }
    s.append("> ");

    s.append(getID() + "(");
    if(getNumParameter() > 0) {
      getParameter(0).toString(s);
      for(int i = 1; i < getNumParameter(); i++) {
        s.append(", ");
        getParameter(i).toString(s);
      }
    }
    s.append(")");
    if(getNumException() > 0) {
      s.append(" throws ");
      getException(0).toString(s);
      for(int i = 1; i < getNumException(); i++) {
        s.append(", ");
        getException(i).toString(s);
      }
    }

    s.append(" {");
    if(hasConstructorInvocation()) {
      s.append(indent());
      getConstructorInvocation().toString(s);
    }
    for(int i = 0; i < getBlock().getNumStmt(); i++) {
      s.append(indent());
      getBlock().getStmt(i).toString(s);
    }
    s.append(indent());
    s.append("}");
  }

    // Declared in Generics.jrag at line 1035

  public GenericConstructorDecl original;

    // Declared in GenericMethods.ast at line 3
    // Declared in GenericMethods.ast line 2

    public GenericConstructorDecl() {
        super();

        setChild(new List(), 1);
        setChild(new List(), 2);
        setChild(new Opt(), 3);
        setChild(new List(), 5);
        setChild(new List(), 6);

    }

    // Declared in GenericMethods.ast at line 15


    // Declared in GenericMethods.ast line 2
    public GenericConstructorDecl(Modifiers p0, String p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5, List<TypeVariable> p6) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
        setChild(p6, 5);
        setChild(new List(), 6);
    }

    // Declared in GenericMethods.ast at line 27


    // Declared in GenericMethods.ast line 2
    public GenericConstructorDecl(Modifiers p0, beaver.Symbol p1, List<ParameterDeclaration> p2, List<Access> p3, Opt<Stmt> p4, Block p5, List<TypeVariable> p6) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
        setChild(p6, 5);
        setChild(new List(), 6);
    }

    // Declared in GenericMethods.ast at line 38


  protected int numChildren() {
    return 6;
  }

    // Declared in GenericMethods.ast at line 41

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 72
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
    // Declared in java.ast line 72
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
    // Declared in java.ast line 72
    public void setParameterList(List<ParameterDeclaration> list) {
        setChild(list, 1);
    }

    // Declared in java.ast at line 6


    public int getNumParameter() {
        return getParameterList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public ParameterDeclaration getParameter(int i) {
        return (ParameterDeclaration)getParameterList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addParameter(ParameterDeclaration node) {
        List<ParameterDeclaration> list = (parent == null || state == null) ? getParameterListNoTransform() : getParameterList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addParameterNoTransform(ParameterDeclaration node) {
        List<ParameterDeclaration> list = getParameterListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setParameter(ParameterDeclaration node, int i) {
        List<ParameterDeclaration> list = getParameterList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<ParameterDeclaration> getParameters() {
        return getParameterList();
    }

    // Declared in java.ast at line 31

    public List<ParameterDeclaration> getParametersNoTransform() {
        return getParameterListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<ParameterDeclaration> getParameterList() {
        List<ParameterDeclaration> list = (List<ParameterDeclaration>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<ParameterDeclaration> getParameterListNoTransform() {
        return (List<ParameterDeclaration>)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 72
    public void setExceptionList(List<Access> list) {
        setChild(list, 2);
    }

    // Declared in java.ast at line 6


    public int getNumException() {
        return getExceptionList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getException(int i) {
        return (Access)getExceptionList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addException(Access node) {
        List<Access> list = (parent == null || state == null) ? getExceptionListNoTransform() : getExceptionList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addExceptionNoTransform(Access node) {
        List<Access> list = getExceptionListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setException(Access node, int i) {
        List<Access> list = getExceptionList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Access> getExceptions() {
        return getExceptionList();
    }

    // Declared in java.ast at line 31

    public List<Access> getExceptionsNoTransform() {
        return getExceptionListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getExceptionList() {
        List<Access> list = (List<Access>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getExceptionListNoTransform() {
        return (List<Access>)getChildNoTransform(2);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 72
    public void setConstructorInvocationOpt(Opt<Stmt> opt) {
        setChild(opt, 3);
    }

    // Declared in java.ast at line 6


    public boolean hasConstructorInvocation() {
        return getConstructorInvocationOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Stmt getConstructorInvocation() {
        return (Stmt)getConstructorInvocationOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setConstructorInvocation(Stmt node) {
        getConstructorInvocationOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Stmt> getConstructorInvocationOpt() {
        return (Opt<Stmt>)getChild(3);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Stmt> getConstructorInvocationOptNoTransform() {
        return (Opt<Stmt>)getChildNoTransform(3);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 72
    public void setBlock(Block node) {
        setChild(node, 4);
    }

    // Declared in java.ast at line 5

    public Block getBlock() {
        return (Block)getChild(4);
    }

    // Declared in java.ast at line 9


    public Block getBlockNoTransform() {
        return (Block)getChildNoTransform(4);
    }

    // Declared in GenericMethods.ast at line 2
    // Declared in GenericMethods.ast line 2
    public void setTypeParameterList(List<TypeVariable> list) {
        setChild(list, 5);
    }

    // Declared in GenericMethods.ast at line 6


    public int getNumTypeParameter() {
        return getTypeParameterList().getNumChild();
    }

    // Declared in GenericMethods.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public TypeVariable getTypeParameter(int i) {
        return (TypeVariable)getTypeParameterList().getChild(i);
    }

    // Declared in GenericMethods.ast at line 14


    public void addTypeParameter(TypeVariable node) {
        List<TypeVariable> list = (parent == null || state == null) ? getTypeParameterListNoTransform() : getTypeParameterList();
        list.addChild(node);
    }

    // Declared in GenericMethods.ast at line 19


    public void addTypeParameterNoTransform(TypeVariable node) {
        List<TypeVariable> list = getTypeParameterListNoTransform();
        list.addChild(node);
    }

    // Declared in GenericMethods.ast at line 24


    public void setTypeParameter(TypeVariable node, int i) {
        List<TypeVariable> list = getTypeParameterList();
        list.setChild(node, i);
    }

    // Declared in GenericMethods.ast at line 28

    public List<TypeVariable> getTypeParameters() {
        return getTypeParameterList();
    }

    // Declared in GenericMethods.ast at line 31

    public List<TypeVariable> getTypeParametersNoTransform() {
        return getTypeParameterListNoTransform();
    }

    // Declared in GenericMethods.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<TypeVariable> getTypeParameterList() {
        List<TypeVariable> list = (List<TypeVariable>)getChild(5);
        list.getNumChild();
        return list;
    }

    // Declared in GenericMethods.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<TypeVariable> getTypeParameterListNoTransform() {
        return (List<TypeVariable>)getChildNoTransform(5);
    }

    // Declared in GenericMethods.ast at line 2
    // Declared in GenericMethods.ast line 2
    public void setParConstructorDeclList(List<ParConstructorDecl> list) {
        setChild(list, 6);
    }

    // Declared in GenericMethods.ast at line 6


    public int getNumParConstructorDecl() {
        return getParConstructorDeclList().getNumChild();
    }

    // Declared in GenericMethods.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public ParConstructorDecl getParConstructorDecl(int i) {
        return (ParConstructorDecl)getParConstructorDeclList().getChild(i);
    }

    // Declared in GenericMethods.ast at line 14


    public void addParConstructorDecl(ParConstructorDecl node) {
        List<ParConstructorDecl> list = (parent == null || state == null) ? getParConstructorDeclListNoTransform() : getParConstructorDeclList();
        list.addChild(node);
    }

    // Declared in GenericMethods.ast at line 19


    public void addParConstructorDeclNoTransform(ParConstructorDecl node) {
        List<ParConstructorDecl> list = getParConstructorDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in GenericMethods.ast at line 24


    public void setParConstructorDecl(ParConstructorDecl node, int i) {
        List<ParConstructorDecl> list = getParConstructorDeclList();
        list.setChild(node, i);
    }

    // Declared in GenericMethods.ast at line 28

    public List<ParConstructorDecl> getParConstructorDecls() {
        return getParConstructorDeclList();
    }

    // Declared in GenericMethods.ast at line 31

    public List<ParConstructorDecl> getParConstructorDeclsNoTransform() {
        return getParConstructorDeclListNoTransform();
    }

    // Declared in GenericMethods.ast at line 35


    public List<ParConstructorDecl> getParConstructorDeclListNoTransform() {
        return (List<ParConstructorDecl>)getChildNoTransform(6);
    }

    // Declared in GenericMethods.ast at line 39


    protected int getParConstructorDeclListChildPosition() {
        return 6;
    }

    protected boolean getParConstructorDeclList_computed = false;
    protected List getParConstructorDeclList_value;
    // Declared in GenericMethods.jrag at line 27
 @SuppressWarnings({"unchecked", "cast"})     public List getParConstructorDeclList() {
        if(getParConstructorDeclList_computed) {
            return (List)ASTNode.getChild(this, getParConstructorDeclListChildPosition());
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getParConstructorDeclList_value = getParConstructorDeclList_compute();
        setParConstructorDeclList(getParConstructorDeclList_value);
        if(true)
            getParConstructorDeclList_computed = true;
        return (List)ASTNode.getChild(this, getParConstructorDeclListChildPosition());
    }

    private List getParConstructorDeclList_compute() {  return new List();  }

    // Declared in GenericMethods.jrag at line 112
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet localLookupType(String name) {
        ASTNode$State state = state();
        SimpleSet localLookupType_String_value = localLookupType_compute(name);
        return localLookupType_String_value;
    }

    private SimpleSet localLookupType_compute(String name) {
    for(int i = 0; i < getNumTypeParameter(); i++) {
      if(original().getTypeParameter(i).name().equals(name))
        return SimpleSet.emptySet.add(original().getTypeParameter(i));
    }
    return SimpleSet.emptySet;
  }

    // Declared in Generics.jrag at line 1034
 @SuppressWarnings({"unchecked", "cast"})     public GenericConstructorDecl original() {
        ASTNode$State state = state();
        GenericConstructorDecl original_value = original_compute();
        return original_value;
    }

    private GenericConstructorDecl original_compute() {  return original != null ? original : this;  }

    // Declared in GenericMethods.jrag at line 111
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet lookupType(String name) {
        ASTNode$State state = state();
        SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
        return lookupType_String_value;
    }

    // Declared in GenericMethods.jrag at line 35
    public GenericConstructorDecl Define_GenericConstructorDecl_genericConstructorDecl(ASTNode caller, ASTNode child) {
        if(caller == getParConstructorDeclListNoTransform()) {
      int i = caller.getIndexOfChild(child);
            return this;
        }
        return getParent().Define_GenericConstructorDecl_genericConstructorDecl(this, caller);
    }

    // Declared in GenericMethods.jrag at line 109
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        return super.Define_NameType_nameType(caller, child);
    }

    // Declared in GenericMethods.jrag at line 119
    public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
        if(true) {
      int childIndex = this.getIndexOfChild(caller);
            return localLookupType(name).isEmpty() ? lookupType(name) : localLookupType(name);
        }
        return getParent().Define_SimpleSet_lookupType(this, caller, name);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
