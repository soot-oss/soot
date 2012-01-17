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
 * @declaredat java.ast:80
 */
public class VariableDeclaration extends Stmt implements Cloneable, SimpleSet, Iterator, Variable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isDAafter_Variable_values = null;
    isDUafter_Variable_values = null;
    constant_computed = false;
    constant_value = null;
    sourceVariableDecl_computed = false;
    sourceVariableDecl_value = null;
    localNum_computed = false;
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
  public VariableDeclaration clone() throws CloneNotSupportedException {
    VariableDeclaration node = (VariableDeclaration)super.clone();
    node.isDAafter_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.constant_computed = false;
    node.constant_value = null;
    node.sourceVariableDecl_computed = false;
    node.sourceVariableDecl_value = null;
    node.localNum_computed = false;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VariableDeclaration copy() {
      try {
        VariableDeclaration node = (VariableDeclaration)clone();
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
  public VariableDeclaration fullCopy() {
    VariableDeclaration res = (VariableDeclaration)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:99
   */
  public SimpleSet add(Object o) {
    return new SimpleSetImpl().add(this).add(o);
  }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:103
   */
  public boolean isSingleton() { return true; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:104
   */
  public boolean isSingleton(Object o) { return contains(o); }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:107
   */
  
  private VariableDeclaration iterElem;
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:108
   */
  public Iterator iterator() { iterElem = this; return this; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:109
   */
  public boolean hasNext() { return iterElem != null; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:110
   */
  public Object next() { Object o = iterElem; iterElem = null; return o; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:111
   */
  public void remove() { throw new UnsupportedOperationException(); }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:299
   */
  public void nameCheck() {
    SimpleSet decls = outerScope().lookupVariable(name());
    for(Iterator iter = decls.iterator(); iter.hasNext(); ) {
      Variable var = (Variable)iter.next();
      if(var instanceof VariableDeclaration) {
        VariableDeclaration decl = (VariableDeclaration)var;
        if(decl != this && decl.enclosingBodyDecl() == enclosingBodyDecl())
  	      error("duplicate declaration of local variable " + name() + " in enclosing scope");
      }
      // 8.4.1
      else if(var instanceof ParameterDeclaration) {
        ParameterDeclaration decl = (ParameterDeclaration)var;
	      if(decl.enclosingBodyDecl() == enclosingBodyDecl())
  	      error("duplicate declaration of local variable and parameter " + name());
      }
    }
    if(getParent().getParent() instanceof Block) {
      Block block = (Block)getParent().getParent();
      for(int i = 0; i < block.getNumStmt(); i++) {
        if(block.getStmt(i) instanceof Variable) {
          Variable v = (Variable)block.getStmt(i);
          if(v.name().equals(name()) && v != this) {
     	    error("duplicate declaration of local variable " + name());
          }
	}
      }
    }
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:74
   */
  public VariableDeclaration(Access type, String name, Expr init) {
    this(new Modifiers(new List()), type, name, new Opt(init));
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:78
   */
  public VariableDeclaration(Access type, String name) {
    this(new Modifiers(new List()), type, name, new Opt());
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:164
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);
    getTypeAccess().toString(s);
    s.append(" " + name());
    if(hasInit()) {
      s.append(" = ");
      getInit().toString(s);
    }
    s.append(";");
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:22
   */
  public void typeCheck() {
    if(hasInit()) {
      TypeDecl source = getInit().type();
      TypeDecl dest = type();
      if(!source.assignConversionTo(dest, getInit()))
        error("can not assign " + name() + " of type " + dest.typeName() +
              " a value of type " + source.typeName());
    }
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:377
   */
  public void jimplify2(Body b) {
    b.setLine(this);
    local = b.newLocal(name(), type().getSootType());
    if(hasInit()) {
      b.add(
        b.newAssignStmt(
          local,
          asRValue(b,
            getInit().type().emitCastTo(b, // Assign conversion
              getInit(),
              type()
            )
          ),
          this
        )
      );
    }
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:395
   */
  
  public Local local;
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public VariableDeclaration() {
    super();

    setChild(new Opt(), 2);

  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public VariableDeclaration(Modifiers p0, Access p1, String p2, Opt<Expr> p3) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
    setChild(p3, 2);
  }
  /**
   * @ast method 
   * @declaredat java.ast:14
   */
  public VariableDeclaration(Modifiers p0, Access p1, beaver.Symbol p2, Opt<Expr> p3) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
    setChild(p3, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:23
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:29
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
  /**   * @apilevel internal   * @ast method 
   * @declaredat java.ast:8
   */
  
  /**   * @apilevel internal   */  protected String tokenString_ID;
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  
  public int IDstart;
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  
  public int IDend;
  /**
   * @ast method 
   * @declaredat java.ast:11
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
   * @declaredat java.ast:22
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for InitOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setInitOpt(Opt<Expr> opt) {
    setChild(opt, 2);
  }
  /**
   * Does this node have a Init child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasInit() {
    return getInitOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child Init
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getInit() {
    return (Expr)getInitOpt().getChild(0);
  }
  /**
   * Setter for optional child Init
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setInit(Expr node) {
    getInitOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Expr> getInitOpt() {
    return (Opt<Expr>)getChild(2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Expr> getInitOptNoTransform() {
    return (Opt<Expr>)getChildNoTransform(2);
  }
  /**
   * @attribute syn
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:97
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int size() {
      ASTNode$State state = state();
    int size_value = size_compute();
    return size_value;
  }
  /**
   * @apilevel internal
   */
  private int size_compute() {  return 1;  }
  /**
   * @attribute syn
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:98
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isEmpty() {
      ASTNode$State state = state();
    boolean isEmpty_value = isEmpty_compute();
    return isEmpty_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isEmpty_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:102
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean contains(Object o) {
      ASTNode$State state = state();
    boolean contains_Object_value = contains_compute(o);
    return contains_Object_value;
  }
  /**
   * @apilevel internal
   */
  private boolean contains_compute(Object o) {  return this == o;  }
  /**
   * @attribute syn
   * @aspect DefiniteAssignment
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:91
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isBlankFinal() {
      ASTNode$State state = state();
    boolean isBlankFinal_value = isBlankFinal_compute();
    return isBlankFinal_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isBlankFinal_compute() {  return isFinal() && (!hasInit() || !getInit().isConstant());  }
  /**
   * @attribute syn
   * @aspect DefiniteAssignment
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:92
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isValue() {
      ASTNode$State state = state();
    boolean isValue_value = isValue_compute();
    return isValue_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isValue_compute() {  return isFinal() && hasInit() && getInit().isConstant();  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:491
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafter(Variable v) {
    Object _parameters = v;
    if(isDAafter_Variable_values == null) isDAafter_Variable_values = new java.util.HashMap(4);
    if(isDAafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAafter_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAafter_Variable_value = isDAafter_compute(v);
if(isFinal && num == state().boundariesCrossed) isDAafter_Variable_values.put(_parameters, Boolean.valueOf(isDAafter_Variable_value));
    return isDAafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafter_compute(Variable v) {
    if(v == this)
      return hasInit();
    return hasInit() ? getInit().isDAafter(v) : isDAbefore(v);
  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:874
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafter(Variable v) {
    Object _parameters = v;
    if(isDUafter_Variable_values == null) isDUafter_Variable_values = new java.util.HashMap(4);
    if(isDUafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDUafter_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDUafter_Variable_value = isDUafter_compute(v);
if(isFinal && num == state().boundariesCrossed) isDUafter_Variable_values.put(_parameters, Boolean.valueOf(isDUafter_Variable_value));
    return isDUafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafter_compute(Variable v) {
    if(v == this)
      return !hasInit();
    return hasInit() ? getInit().isDUafter(v) : isDUbefore(v);
  }
  /**
   * @attribute syn
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:130
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean declaresVariable(String name) {
      ASTNode$State state = state();
    boolean declaresVariable_String_value = declaresVariable_compute(name);
    return declaresVariable_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean declaresVariable_compute(String name) {  return name().equals(name);  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:219
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSynthetic() {
      ASTNode$State state = state();
    boolean isSynthetic_value = isSynthetic_compute();
    return isSynthetic_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSynthetic_compute() {  return getModifiers().isSynthetic();  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:812
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String dumpString() {
      ASTNode$State state = state();
    String dumpString_value = dumpString_compute();
    return dumpString_value;
  }
  /**
   * @apilevel internal
   */
  private String dumpString_compute() {  return getClass().getName() + " [" + getID() + "]";  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:252
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
      ASTNode$State state = state();
    TypeDecl type_value = type_compute();
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {  return getTypeAccess().type();  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:13
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isParameter() {
      ASTNode$State state = state();
    boolean isParameter_value = isParameter_compute();
    return isParameter_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isParameter_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:15
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isClassVariable() {
      ASTNode$State state = state();
    boolean isClassVariable_value = isClassVariable_compute();
    return isClassVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isClassVariable_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:16
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isInstanceVariable() {
      ASTNode$State state = state();
    boolean isInstanceVariable_value = isInstanceVariable_compute();
    return isInstanceVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isInstanceVariable_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:17
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isMethodParameter() {
      ASTNode$State state = state();
    boolean isMethodParameter_value = isMethodParameter_compute();
    return isMethodParameter_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isMethodParameter_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:18
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isConstructorParameter() {
      ASTNode$State state = state();
    boolean isConstructorParameter_value = isConstructorParameter_compute();
    return isConstructorParameter_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isConstructorParameter_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isExceptionHandlerParameter() {
      ASTNode$State state = state();
    boolean isExceptionHandlerParameter_value = isExceptionHandlerParameter_compute();
    return isExceptionHandlerParameter_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isExceptionHandlerParameter_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:20
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isLocalVariable() {
      ASTNode$State state = state();
    boolean isLocalVariable_value = isLocalVariable_compute();
    return isLocalVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isLocalVariable_compute() {  return true;  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:22
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isFinal() {
      ASTNode$State state = state();
    boolean isFinal_value = isFinal_compute();
    return isFinal_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isFinal_compute() {  return getModifiers().isFinal();  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:23
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isVolatile() {
      ASTNode$State state = state();
    boolean isVolatile_value = isVolatile_compute();
    return isVolatile_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isVolatile_compute() {  return getModifiers().isVolatile();  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:24
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isBlank() {
      ASTNode$State state = state();
    boolean isBlank_value = isBlank_compute();
    return isBlank_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isBlank_compute() {  return !hasInit();  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:25
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isStatic() {
      ASTNode$State state = state();
    boolean isStatic_value = isStatic_compute();
    return isStatic_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isStatic_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:27
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String name() {
      ASTNode$State state = state();
    String name_value = name_compute();
    return name_value;
  }
  /**
   * @apilevel internal
   */
  private String name_compute() {  return getID();  }
  /**
   * @apilevel internal
   */
  protected boolean constant_computed = false;
  /**
   * @apilevel internal
   */
  protected Constant constant_value;
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:29
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant constant() {
    if(constant_computed) {
      return constant_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    constant_value = constant_compute();
if(isFinal && num == state().boundariesCrossed) constant_computed = true;
    return constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant constant_compute() {  return type().cast(getInit().constant());  }
  /**
   * @apilevel internal
   */
  protected boolean sourceVariableDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected Variable sourceVariableDecl_value;
  /**
   * @attribute syn
   * @aspect SourceDeclarations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1283
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Variable sourceVariableDecl() {
    if(sourceVariableDecl_computed) {
      return sourceVariableDecl_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    sourceVariableDecl_value = sourceVariableDecl_compute();
if(isFinal && num == state().boundariesCrossed) sourceVariableDecl_computed = true;
    return sourceVariableDecl_value;
  }
  /**
   * @apilevel internal
   */
  private Variable sourceVariableDecl_compute() {  return this;  }
  /**
   * @attribute inh
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:21
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupVariable(String name) {
      ASTNode$State state = state();
    SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
    return lookupVariable_String_value;
  }
  /**
   * @attribute inh
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:289
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VariableScope outerScope() {
      ASTNode$State state = state();
    VariableScope outerScope_value = getParent().Define_VariableScope_outerScope(this, null);
    return outerScope_value;
  }
  /**
   * @attribute inh
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:585
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl hostType() {
      ASTNode$State state = state();
    TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
    return hostType_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean localNum_computed = false;
  /**
   * @apilevel internal
   */
  protected int localNum_value;
  /**
   * @attribute inh
   * @aspect LocalNum
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:11
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int localNum() {
    if(localNum_computed) {
      return localNum_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    localNum_value = getParent().Define_int_localNum(this, null);
if(isFinal && num == state().boundariesCrossed) localNum_computed = true;
    return localNum_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:40
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    if(caller == getInitOptNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isSource(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:496
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getInitOptNoTransform()) {
      return isDAbefore(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:879
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getInitOptNoTransform()) {
      return isDUbefore(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:286
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeFinal(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:85
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeAccessNoTransform()) {
      return NameType.TYPE_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:261
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
    if(caller == getInitOptNoTransform()) {
      return type();
    }
    return getParent().Define_TypeDecl_declType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:92
   * @apilevel internal
   */
  public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
    if(caller == getModifiersNoTransform()) {
      return name.equals("LOCAL_VARIABLE");
    }
    return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:34
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
    if(caller == getInitOptNoTransform()) {
      return type();
    }
    return getParent().Define_TypeDecl_assignConvertedType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:65
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
    if(caller == getInitOptNoTransform()) {
      return type().componentType();
    }
    return getParent().Define_TypeDecl_expectedType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
