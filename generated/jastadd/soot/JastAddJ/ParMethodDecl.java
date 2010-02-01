
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class ParMethodDecl extends MethodDecl implements Cloneable, Parameterization {
    public void flushCache() {
        super.flushCache();
        sourceMethodDecl_computed = false;
        sourceMethodDecl_value = null;
        moreSpecificThan_MethodDecl_values = null;
        genericMethodDecl_computed = false;
        genericMethodDecl_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParMethodDecl clone() throws CloneNotSupportedException {
        ParMethodDecl node = (ParMethodDecl)super.clone();
        node.sourceMethodDecl_computed = false;
        node.sourceMethodDecl_value = null;
        node.moreSpecificThan_MethodDecl_values = null;
        node.genericMethodDecl_computed = false;
        node.genericMethodDecl_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParMethodDecl copy() {
      try {
          ParMethodDecl node = (ParMethodDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ParMethodDecl fullCopy() {
        ParMethodDecl res = (ParMethodDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in GenericMethods.jrag at line 81

  // Disable error checking in instantiated generic methods
  public void collectErrors() {
  }

    // Declared in Generics.jrag at line 699

  public TypeDecl substitute(TypeVariable typeVariable) {
    for(int i = 0; i < numTypeParameter(); i++)
      if(typeParameter(i) == typeVariable)
        return getTypeArgument(i).type();
    return hostType().substitute(typeVariable);
  }

    // Declared in Generics.jrag at line 713

  public boolean isRawType() {
    return false; 
  }

    // Declared in Generics.jrag at line 719

  public int numTypeParameter() {
    return genericMethodDecl().original().getNumTypeParameter();
  }

    // Declared in Generics.jrag at line 722

  public TypeVariable typeParameter(int index) {
    return genericMethodDecl().original().getTypeParameter(index);
  }

    // Declared in GenericsCodegen.jrag at line 322

  public void transformation() { }

    // Declared in GenericMethods.ast at line 3
    // Declared in GenericMethods.ast line 4

    public ParMethodDecl() {
        super();

        setChild(new List(), 2);
        setChild(new List(), 3);
        setChild(new Opt(), 4);
        setChild(new List(), 5);

    }

    // Declared in GenericMethods.ast at line 14


    // Declared in GenericMethods.ast line 4
    public ParMethodDecl(Modifiers p0, Access p1, String p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5, List<Access> p6) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
        setChild(p6, 5);
    }

    // Declared in GenericMethods.ast at line 25


    // Declared in GenericMethods.ast line 4
    public ParMethodDecl(Modifiers p0, Access p1, beaver.Symbol p2, List<ParameterDeclaration> p3, List<Access> p4, Opt<Block> p5, List<Access> p6) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
        setChild(p6, 5);
    }

    // Declared in GenericMethods.ast at line 35


  protected int numChildren() {
    return 6;
  }

    // Declared in GenericMethods.ast at line 38

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
    // Declared in GenericMethods.ast line 4
    public void setTypeArgumentList(List<Access> list) {
        setChild(list, 5);
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
        List<Access> list = (List<Access>)getChild(5);
        list.getNumChild();
        return list;
    }

    // Declared in GenericMethods.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeArgumentListNoTransform() {
        return (List<Access>)getChildNoTransform(5);
    }

    // Declared in Generics.jrag at line 1266
 @SuppressWarnings({"unchecked", "cast"})     public MethodDecl sourceMethodDecl() {
        if(sourceMethodDecl_computed) {
            return sourceMethodDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sourceMethodDecl_value = sourceMethodDecl_compute();
        if(isFinal && num == state().boundariesCrossed)
            sourceMethodDecl_computed = true;
        return sourceMethodDecl_value;
    }

    private MethodDecl sourceMethodDecl_compute() {  return genericMethodDecl().original().sourceMethodDecl();  }

    // Declared in MethodSignature.jrag at line 137
 @SuppressWarnings({"unchecked", "cast"})     public boolean moreSpecificThan(MethodDecl m) {
        Object _parameters = m;
if(moreSpecificThan_MethodDecl_values == null) moreSpecificThan_MethodDecl_values = new java.util.HashMap(4);
        if(moreSpecificThan_MethodDecl_values.containsKey(_parameters)) {
            return ((Boolean)moreSpecificThan_MethodDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean moreSpecificThan_MethodDecl_value = moreSpecificThan_compute(m);
        if(isFinal && num == state().boundariesCrossed)
            moreSpecificThan_MethodDecl_values.put(_parameters, Boolean.valueOf(moreSpecificThan_MethodDecl_value));
        return moreSpecificThan_MethodDecl_value;
    }

    private boolean moreSpecificThan_compute(MethodDecl m) {  return genericMethodDecl().moreSpecificThan(m instanceof ParMethodDecl ? ((ParMethodDecl)m).genericMethodDecl() : m );  }

    // Declared in GenericsCodegen.jrag at line 36
 @SuppressWarnings({"unchecked", "cast"})     public MethodDecl erasedMethod() {
        ASTNode$State state = state();
        MethodDecl erasedMethod_value = erasedMethod_compute();
        return erasedMethod_value;
    }

    private MethodDecl erasedMethod_compute() {  return genericMethodDecl().erasedMethod();  }

    protected boolean genericMethodDecl_computed = false;
    protected GenericMethodDecl genericMethodDecl_value;
    // Declared in GenericMethods.jrag at line 31
 @SuppressWarnings({"unchecked", "cast"})     public GenericMethodDecl genericMethodDecl() {
        if(genericMethodDecl_computed) {
            return genericMethodDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        genericMethodDecl_value = getParent().Define_GenericMethodDecl_genericMethodDecl(this, null);
        if(isFinal && num == state().boundariesCrossed)
            genericMethodDecl_computed = true;
        return genericMethodDecl_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
