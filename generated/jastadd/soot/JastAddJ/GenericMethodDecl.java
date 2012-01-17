package soot.JastAddJ;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;

/**
 * @ast node
 * @declaredat GenericMethods.ast:1
 */
public class GenericMethodDecl extends MethodDecl implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    getParMethodDeclList_computed = false;
    getParMethodDeclList_value = null;
    rawMethodDecl_computed = false;
    rawMethodDecl_value = null;
    lookupParMethodDecl_java_util_List_values = null;
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GenericMethodDecl clone() throws CloneNotSupportedException {
    GenericMethodDecl node = (GenericMethodDecl)super.clone();
    node.getParMethodDeclList_computed = false;
    node.getParMethodDeclList_value = null;
    node.rawMethodDecl_computed = false;
    node.rawMethodDecl_value = null;
    node.lookupParMethodDecl_java_util_List_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GenericMethodDecl copy() {
      try {
        GenericMethodDecl node = (GenericMethodDecl)clone();
        if(children != null) node.children = (ASTNode[])children.clone();
        return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
  }
  /**
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GenericMethodDecl fullCopy() {
    GenericMethodDecl res = (GenericMethodDecl)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect GenericMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:60
   */
  public ParMethodDecl p(java.util.List typeArguments) {
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
  /**
   * @ast method 
   * @aspect GenericMethodsPrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:170
   */
  private void ppTypeParameters(StringBuffer s) {
    s.append(" <");
    for(int i = 0; i < getNumTypeParameter(); i++) {
      if(i != 0) s.append(", ");
      original().getTypeParameter(i).toString(s);
    }
    s.append("> ");
  }
  /**
   * @ast method 
   * @aspect GenericMethodsPrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:179
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);
    
    ppTypeParameters(s);
    
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
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1024
   */
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
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1040
   */
  
  public GenericMethodDecl original;
  /**
   * @ast method 
   * @declaredat GenericMethods.ast:1
   */
  public GenericMethodDecl() {
    super();

    setChild(new List(), 2);
    setChild(new List(), 3);
    setChild(new Opt(), 4);
    setChild(new List(), 5);
    setChild(new List(), 6);

  }
  /**
   * @ast method 
   * @declaredat GenericMethods.ast:12
   */
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
  /**
   * @ast method 
   * @declaredat GenericMethods.ast:22
   */
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
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:35
   */
  protected int numChildren() {
    return 6;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat GenericMethods.ast:41
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Getter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Setter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setTypeAccess(Access node) {
    setChild(node, 1);
  }
  /**
   * Getter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Access getTypeAccess() {
    return (Access)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(1);
  }
  /**
   * Setter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Getter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setParameterList(List<ParameterDeclaration> list) {
    setChild(list, 2);
  }
  /**
   * @return number of children in ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumParameter() {
    return getParameterList().getNumChild();
  }
  /**
   * Getter for child in list ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParameterDeclaration getParameter(int i) {
    return (ParameterDeclaration)getParameterList().getChild(i);
  }
  /**
   * Add element to list ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addParameter(ParameterDeclaration node) {
    List<ParameterDeclaration> list = (parent == null || state == null) ? getParameterListNoTransform() : getParameterList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addParameterNoTransform(ParameterDeclaration node) {
    List<ParameterDeclaration> list = getParameterListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setParameter(ParameterDeclaration node, int i) {
    List<ParameterDeclaration> list = getParameterList();
    list.setChild(node, i);
  }
  /**
   * Getter for Parameter list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<ParameterDeclaration> getParameters() {
    return getParameterList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<ParameterDeclaration> getParametersNoTransform() {
    return getParameterListNoTransform();
  }
  /**
   * Getter for list ParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ParameterDeclaration> getParameterList() {
    List<ParameterDeclaration> list = (List<ParameterDeclaration>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ParameterDeclaration> getParameterListNoTransform() {
    return (List<ParameterDeclaration>)getChildNoTransform(2);
  }
  /**
   * Setter for ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setExceptionList(List<Access> list) {
    setChild(list, 3);
  }
  /**
   * @return number of children in ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumException() {
    return getExceptionList().getNumChild();
  }
  /**
   * Getter for child in list ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getException(int i) {
    return (Access)getExceptionList().getChild(i);
  }
  /**
   * Add element to list ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addException(Access node) {
    List<Access> list = (parent == null || state == null) ? getExceptionListNoTransform() : getExceptionList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addExceptionNoTransform(Access node) {
    List<Access> list = getExceptionListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setException(Access node, int i) {
    List<Access> list = getExceptionList();
    list.setChild(node, i);
  }
  /**
   * Getter for Exception list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<Access> getExceptions() {
    return getExceptionList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<Access> getExceptionsNoTransform() {
    return getExceptionListNoTransform();
  }
  /**
   * Getter for list ExceptionList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getExceptionList() {
    List<Access> list = (List<Access>)getChild(3);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getExceptionListNoTransform() {
    return (List<Access>)getChildNoTransform(3);
  }
  /**
   * Setter for BlockOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setBlockOpt(Opt<Block> opt) {
    setChild(opt, 4);
  }
  /**
   * Does this node have a Block child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasBlock() {
    return getBlockOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child Block
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Block getBlock() {
    return (Block)getBlockOpt().getChild(0);
  }
  /**
   * Setter for optional child Block
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setBlock(Block node) {
    getBlockOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getBlockOpt() {
    return (Opt<Block>)getChild(4);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getBlockOptNoTransform() {
    return (Opt<Block>)getChildNoTransform(4);
  }
  /**
   * Setter for TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:5
   */
  public void setTypeParameterList(List<TypeVariable> list) {
    setChild(list, 5);
  }
  /**
   * @return number of children in TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:12
   */
  public int getNumTypeParameter() {
    return getTypeParameterList().getNumChild();
  }
  /**
   * Getter for child in list TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeVariable getTypeParameter(int i) {
    return (TypeVariable)getTypeParameterList().getChild(i);
  }
  /**
   * Add element to list TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:27
   */
  public void addTypeParameter(TypeVariable node) {
    List<TypeVariable> list = (parent == null || state == null) ? getTypeParameterListNoTransform() : getTypeParameterList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:34
   */
  public void addTypeParameterNoTransform(TypeVariable node) {
    List<TypeVariable> list = getTypeParameterListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:42
   */
  public void setTypeParameter(TypeVariable node, int i) {
    List<TypeVariable> list = getTypeParameterList();
    list.setChild(node, i);
  }
  /**
   * Getter for TypeParameter list.
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:50
   */
  public List<TypeVariable> getTypeParameters() {
    return getTypeParameterList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:56
   */
  public List<TypeVariable> getTypeParametersNoTransform() {
    return getTypeParameterListNoTransform();
  }
  /**
   * Getter for list TypeParameterList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<TypeVariable> getTypeParameterList() {
    List<TypeVariable> list = (List<TypeVariable>)getChild(5);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<TypeVariable> getTypeParameterListNoTransform() {
    return (List<TypeVariable>)getChildNoTransform(5);
  }
  /**
   * Setter for ParMethodDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:5
   */
  public void setParMethodDeclList(List<ParMethodDecl> list) {
    setChild(list, 6);
  }
  /**
   * @return number of children in ParMethodDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:12
   */
  public int getNumParMethodDecl() {
    return getParMethodDeclList().getNumChild();
  }
  /**
   * Getter for child in list ParMethodDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParMethodDecl getParMethodDecl(int i) {
    return (ParMethodDecl)getParMethodDeclList().getChild(i);
  }
  /**
   * Add element to list ParMethodDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:27
   */
  public void addParMethodDecl(ParMethodDecl node) {
    List<ParMethodDecl> list = (parent == null || state == null) ? getParMethodDeclListNoTransform() : getParMethodDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:34
   */
  public void addParMethodDeclNoTransform(ParMethodDecl node) {
    List<ParMethodDecl> list = getParMethodDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ParMethodDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:42
   */
  public void setParMethodDecl(ParMethodDecl node, int i) {
    List<ParMethodDecl> list = getParMethodDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for ParMethodDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:50
   */
  public List<ParMethodDecl> getParMethodDecls() {
    return getParMethodDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:56
   */
  public List<ParMethodDecl> getParMethodDeclsNoTransform() {
    return getParMethodDeclListNoTransform();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:62
   */
  public List<ParMethodDecl> getParMethodDeclListNoTransform() {
    return (List<ParMethodDecl>)getChildNoTransform(6);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:68
   */
  protected int getParMethodDeclListChildPosition() {
    return 6;
  }
  /**
   * @apilevel internal
   */
  protected boolean getParMethodDeclList_computed = false;
  /**
   * @apilevel internal
   */
  protected List getParMethodDeclList_value;
  /**
   * @attribute syn nta
   * @aspect GenericMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:26
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List getParMethodDeclList() {
    if(getParMethodDeclList_computed) {
      return (List)ASTNode.getChild(this, getParMethodDeclListChildPosition());
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getParMethodDeclList_value = getParMethodDeclList_compute();
    setParMethodDeclList(getParMethodDeclList_value);
if(true) getParMethodDeclList_computed = true;
    return (List)ASTNode.getChild(this, getParMethodDeclListChildPosition());
  }
  /**
   * @apilevel internal
   */
  private List getParMethodDeclList_compute() {  return new List();  }
  /**
   * @apilevel internal
   */
  protected boolean rawMethodDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected MethodDecl rawMethodDecl_value;
  /**
   * @attribute syn
   * @aspect GenericMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:28
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl rawMethodDecl() {
    if(rawMethodDecl_computed) {
      return rawMethodDecl_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    rawMethodDecl_value = rawMethodDecl_compute();
if(true) rawMethodDecl_computed = true;
    return rawMethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private MethodDecl rawMethodDecl_compute() {  return lookupParMethodDecl(new ArrayList());  }
  protected java.util.Map lookupParMethodDecl_java_util_List_values;
  /**
   * @attribute syn
   * @aspect GenericMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:43
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl lookupParMethodDecl(java.util.List typeArguments) {
    Object _parameters = typeArguments;
    if(lookupParMethodDecl_java_util_List_values == null) lookupParMethodDecl_java_util_List_values = new java.util.HashMap(4);
    if(lookupParMethodDecl_java_util_List_values.containsKey(_parameters)) {
      return (MethodDecl)lookupParMethodDecl_java_util_List_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    MethodDecl lookupParMethodDecl_java_util_List_value = lookupParMethodDecl_compute(typeArguments);
if(isFinal && num == state().boundariesCrossed) lookupParMethodDecl_java_util_List_values.put(_parameters, lookupParMethodDecl_java_util_List_value);
    return lookupParMethodDecl_java_util_List_value;
  }
  /**
   * @apilevel internal
   */
  private MethodDecl lookupParMethodDecl_compute(java.util.List typeArguments) {
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
  /**
   * @attribute syn
   * @aspect GenericMethodsNameAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:101
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localLookupType(String name) {
      ASTNode$State state = state();
    SimpleSet localLookupType_String_value = localLookupType_compute(name);
    return localLookupType_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localLookupType_compute(String name) {
    for(int i = 0; i < getNumTypeParameter(); i++) {
      if(original().getTypeParameter(i).name().equals(name))
        return SimpleSet.emptySet.add(original().getTypeParameter(i));
    }
    return SimpleSet.emptySet;
  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1039
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GenericMethodDecl original() {
      ASTNode$State state = state();
    GenericMethodDecl original_value = original_compute();
    return original_value;
  }
  /**
   * @apilevel internal
   */
  private GenericMethodDecl original_compute() {  return original != null ? original : this;  }
  /**
   * @attribute inh
   * @aspect GenericMethodsNameAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:100
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupType(String name) {
      ASTNode$State state = state();
    SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
    return lookupType_String_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:98
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return NameType.TYPE_NAME;
    }
    return super.Define_NameType_nameType(caller, child);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:108
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return localLookupType(name).isEmpty() ? lookupType(name) : localLookupType(name);
    }
    return getParent().Define_SimpleSet_lookupType(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
