
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;

public class GenericMethodDecl extends MethodDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        getParMethodDeclList_computed = false;
        getParMethodDeclList_value = null;
        rawMethodDecl_computed = false;
        rawMethodDecl_value = null;
        lookupParMethodDecl_ArrayList_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericMethodDecl clone() throws CloneNotSupportedException {
        GenericMethodDecl node = (GenericMethodDecl)super.clone();
        node.getParMethodDeclList_computed = false;
        node.getParMethodDeclList_value = null;
        node.rawMethodDecl_computed = false;
        node.rawMethodDecl_value = null;
        node.lookupParMethodDecl_ArrayList_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericMethodDecl copy() {
      try {
          GenericMethodDecl node = (GenericMethodDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericMethodDecl fullCopy() {
        GenericMethodDecl res = (GenericMethodDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in GenericMethods.jrag at line 55


  public ParMethodDecl p(ArrayList typeArguments) {
    ParMethodDecl methodDecl = typeArguments.isEmpty() ? new RawMethodDecl() : new ParMethodDecl();
    addParMethodDecl(methodDecl);
    List list = new List();
    if(typeArguments.isEmpty()) {
      GenericMethodDecl original = original();
      for(int i = 0; i < original.getNumTypeParameter(); i++)
        list.add(original.getTypeParameter(i).erasure().createBoundAccess());
    }
    else {
      for(Iterator iter = typeArguments.iterator(); iter.hasNext(); )
        list.add(((TypeDecl)iter.next()).createBoundAccess());
    }
    methodDecl.setTypeArgumentList(list);
    methodDecl.setModifiers((Modifiers)getModifiers().fullCopy());
    methodDecl.setTypeAccess(getTypeAccess().type().substituteReturnType(methodDecl));
    methodDecl.setID(getID());
    methodDecl.setParameterList(getParameterList().substitute(methodDecl));
    methodDecl.setExceptionList(getExceptionList().substitute(methodDecl));
    return methodDecl;
  }

    // Declared in GenericMethods.jrag at line 165


  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);
    
    s.append(" <");
    for(int i = 0; i < getNumTypeParameter(); i++) {
      if(i != 0) s.append(", ");
      original().getTypeParameter(i).toString(s);
    }
    s.append("> ");
    
    getTypeAccess().toString(s);
    s.append(" " + getID());
    s.append("(");
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
    if(hasBlock()) {
      s.append(" ");
      getBlock().toString(s);
    }
    else {
      s.append(";\n");
    }
  }

    // Declared in Generics.jrag at line 1016


  public BodyDecl p(Parameterization parTypeDecl) {
    //System.out.println("Begin substituting generic " + signature() + " in " + hostType().typeName() + " with " + parTypeDecl.typeSignature());
    GenericMethodDecl m = new GenericMethodDecl(
      (Modifiers)getModifiers().fullCopy(),
      getTypeAccess().type().substituteReturnType(parTypeDecl),
      getID(),
      getParameterList().substitute(parTypeDecl),
      getExceptionList().substitute(parTypeDecl),
      new Opt(),
      (List)getTypeParameterList().fullCopy()
    );
    m.original = this;
    //System.out.println("End substituting generic " + signature());
    return m;
  }

    // Declared in Generics.jrag at line 1032

  public GenericMethodDecl original;

    // Declared in GenericMethods.ast at line 3
    // Declared in GenericMethods.ast line 1

    public GenericMethodDecl() {
        super();

        setChild(new List(), 2);
        setChild(new List(), 3);
        setChild(new Opt(), 4);
        setChild(new List(), 5);
        setChild(new List(), 6);

    }

    // Declared in GenericMethods.ast at line 15


    // Declared in GenericMethods.ast line 1
    public GenericMethodDecl(Modifiers p0, Access p1, String p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5, List<TypeVariable> p6) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
        setChild(p6, 5);
        setChild(new List(), 6);
    }

    // Declared in GenericMethods.ast at line 27


    // Declared in GenericMethods.ast line 1
    public GenericMethodDecl(Modifiers p0, Access p1, beaver.Symbol p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5, List<TypeVariable> p6) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
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
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 88
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
    // Declared in java.ast line 88
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
    // Declared in java.ast line 88
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
    // Declared in java.ast line 88
    public void setParameterList(List<ParameterDeclaration> list) {
        setChild(list, 2);
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
        List<ParameterDeclaration> list = (List<ParameterDeclaration>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<ParameterDeclaration> getParameterListNoTransform() {
        return (List<ParameterDeclaration>)getChildNoTransform(2);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 88
    public void setExceptionList(List<Access> list) {
        setChild(list, 3);
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
        List<Access> list = (List<Access>)getChild(3);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getExceptionListNoTransform() {
        return (List<Access>)getChildNoTransform(3);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 88
    public void setBlockOpt(Opt<Block> opt) {
        setChild(opt, 4);
    }

    // Declared in java.ast at line 6


    public boolean hasBlock() {
        return getBlockOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Block getBlock() {
        return (Block)getBlockOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setBlock(Block node) {
        getBlockOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Block> getBlockOpt() {
        return (Opt<Block>)getChild(4);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Block> getBlockOptNoTransform() {
        return (Opt<Block>)getChildNoTransform(4);
    }

    // Declared in GenericMethods.ast at line 2
    // Declared in GenericMethods.ast line 1
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
    // Declared in GenericMethods.ast line 1
    public void setParMethodDeclList(List<ParMethodDecl> list) {
        setChild(list, 6);
    }

    // Declared in GenericMethods.ast at line 6


    public int getNumParMethodDecl() {
        return getParMethodDeclList().getNumChild();
    }

    // Declared in GenericMethods.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public ParMethodDecl getParMethodDecl(int i) {
        return (ParMethodDecl)getParMethodDeclList().getChild(i);
    }

    // Declared in GenericMethods.ast at line 14


    public void addParMethodDecl(ParMethodDecl node) {
        List<ParMethodDecl> list = (parent == null || state == null) ? getParMethodDeclListNoTransform() : getParMethodDeclList();
        list.addChild(node);
    }

    // Declared in GenericMethods.ast at line 19


    public void addParMethodDeclNoTransform(ParMethodDecl node) {
        List<ParMethodDecl> list = getParMethodDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in GenericMethods.ast at line 24


    public void setParMethodDecl(ParMethodDecl node, int i) {
        List<ParMethodDecl> list = getParMethodDeclList();
        list.setChild(node, i);
    }

    // Declared in GenericMethods.ast at line 28

    public List<ParMethodDecl> getParMethodDecls() {
        return getParMethodDeclList();
    }

    // Declared in GenericMethods.ast at line 31

    public List<ParMethodDecl> getParMethodDeclsNoTransform() {
        return getParMethodDeclListNoTransform();
    }

    // Declared in GenericMethods.ast at line 35


    public List<ParMethodDecl> getParMethodDeclListNoTransform() {
        return (List<ParMethodDecl>)getChildNoTransform(6);
    }

    // Declared in GenericMethods.ast at line 39


    protected int getParMethodDeclListChildPosition() {
        return 6;
    }

    protected boolean getParMethodDeclList_computed = false;
    protected List getParMethodDeclList_value;
    // Declared in GenericMethods.jrag at line 26
 @SuppressWarnings({"unchecked", "cast"})     public List getParMethodDeclList() {
        if(getParMethodDeclList_computed) {
            return (List)ASTNode.getChild(this, getParMethodDeclListChildPosition());
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getParMethodDeclList_value = getParMethodDeclList_compute();
        setParMethodDeclList(getParMethodDeclList_value);
        if(true)
            getParMethodDeclList_computed = true;
        return (List)ASTNode.getChild(this, getParMethodDeclListChildPosition());
    }

    private List getParMethodDeclList_compute() {  return new List();  }

    protected boolean rawMethodDecl_computed = false;
    protected MethodDecl rawMethodDecl_value;
    // Declared in GenericMethods.jrag at line 28
 @SuppressWarnings({"unchecked", "cast"})     public MethodDecl rawMethodDecl() {
        if(rawMethodDecl_computed) {
            return rawMethodDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        rawMethodDecl_value = rawMethodDecl_compute();
        if(true)
            rawMethodDecl_computed = true;
        return rawMethodDecl_value;
    }

    private MethodDecl rawMethodDecl_compute() {  return lookupParMethodDecl(new ArrayList());  }

    protected java.util.Map lookupParMethodDecl_ArrayList_values;
    // Declared in GenericMethods.jrag at line 38
 @SuppressWarnings({"unchecked", "cast"})     public MethodDecl lookupParMethodDecl(ArrayList typeArguments) {
        Object _parameters = typeArguments;
if(lookupParMethodDecl_ArrayList_values == null) lookupParMethodDecl_ArrayList_values = new java.util.HashMap(4);
        if(lookupParMethodDecl_ArrayList_values.containsKey(_parameters)) {
            return (MethodDecl)lookupParMethodDecl_ArrayList_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        MethodDecl lookupParMethodDecl_ArrayList_value = lookupParMethodDecl_compute(typeArguments);
        if(isFinal && num == state().boundariesCrossed)
            lookupParMethodDecl_ArrayList_values.put(_parameters, lookupParMethodDecl_ArrayList_value);
        return lookupParMethodDecl_ArrayList_value;
    }

    private MethodDecl lookupParMethodDecl_compute(ArrayList typeArguments) {
    l: for(int i = 0; i < getNumParMethodDecl(); i++) {
      ParMethodDecl decl = getParMethodDecl(i);
      if(decl instanceof RawMethodDecl) {
        if(typeArguments.isEmpty())
          return decl;
      }
      else if(decl.getNumTypeArgument() == typeArguments.size()) {
        for(int j = 0; j < decl.getNumTypeArgument(); j++)
          if(decl.getTypeArgument(j).type() != typeArguments.get(j))
            continue l;
        return decl;
      }
    }
    return p(typeArguments);
  }

    // Declared in GenericMethods.jrag at line 96
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

    // Declared in Generics.jrag at line 1031
 @SuppressWarnings({"unchecked", "cast"})     public GenericMethodDecl original() {
        ASTNode$State state = state();
        GenericMethodDecl original_value = original_compute();
        return original_value;
    }

    private GenericMethodDecl original_compute() {  return original != null ? original : this;  }

    // Declared in GenericMethods.jrag at line 95
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet lookupType(String name) {
        ASTNode$State state = state();
        SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
        return lookupType_String_value;
    }

    // Declared in GenericMethods.jrag at line 32
    public GenericMethodDecl Define_GenericMethodDecl_genericMethodDecl(ASTNode caller, ASTNode child) {
        if(caller == getParMethodDeclListNoTransform()) {
      int i = caller.getIndexOfChild(child);
            return this;
        }
        return getParent().Define_GenericMethodDecl_genericMethodDecl(this, caller);
    }

    // Declared in GenericMethods.jrag at line 93
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        return super.Define_NameType_nameType(caller, child);
    }

    // Declared in GenericMethods.jrag at line 103
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
