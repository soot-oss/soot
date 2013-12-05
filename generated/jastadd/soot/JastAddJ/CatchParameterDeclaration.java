/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
package soot.JastAddJ;

import java.util.HashSet;
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
 * A catch parameter with disjunct exception type.
 * @production CatchParameterDeclaration : {@link ASTNode} ::= <span class="component">{@link Modifiers}</span> <span class="component">TypeAccess:{@link Access}*</span> <span class="component">&lt;ID:String&gt;</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.ast:19
 */
public class CatchParameterDeclaration extends ASTNode<ASTNode> implements Cloneable, Variable, SimpleSet, Iterator {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    sourceVariableDecl_computed = false;
    sourceVariableDecl_value = null;
    throwTypes_computed = false;
    throwTypes_value = null;
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
  public CatchParameterDeclaration clone() throws CloneNotSupportedException {
    CatchParameterDeclaration node = (CatchParameterDeclaration)super.clone();
    node.sourceVariableDecl_computed = false;
    node.sourceVariableDecl_value = null;
    node.throwTypes_computed = false;
    node.throwTypes_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CatchParameterDeclaration copy() {
    try {
      CatchParameterDeclaration node = (CatchParameterDeclaration) clone();
      node.parent = null;
      if(children != null)
        node.children = (ASTNode[]) children.clone();
      return node;
    } catch (CloneNotSupportedException e) {
      throw new Error("Error: clone not supported for " +
        getClass().getName());
    }
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CatchParameterDeclaration fullCopy() {
    CatchParameterDeclaration tree = (CatchParameterDeclaration) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        ASTNode child = (ASTNode) children[i];
        if(child != null) {
          child = child.fullCopy();
          tree.setChild(child, i);
        }
      }
    }
    return tree;
  }
  /**
   * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:63
   */
  public SimpleSet add(Object o) {
		return new SimpleSetImpl().add(this).add(o);
	}
  /**
   * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:67
   */
  public boolean isSingleton() { return true; }
  /**
   * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:68
   */
  public boolean isSingleton(Object o) { return contains(o); }
  /**
   * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:71
   */
  
	private CatchParameterDeclaration iterElem;
  /**
   * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:72
   */
  public Iterator iterator() { iterElem = this; return this; }
  /**
   * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:73
   */
  public boolean hasNext() { return iterElem != null; }
  /**
   * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:74
   */
  public Object next() { Object o = iterElem; iterElem = null; return o; }
  /**
   * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:75
   */
  public void remove() { throw new UnsupportedOperationException(); }
  /**
	 * Type checking.
	 * The types given in a disjunction type may not be
	 * subtypes of each other.
	 * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:109
   */
  public void typeCheck() {
		boolean pass = true;
		for (int i = 0; i < getNumTypeAccess(); ++i) {
			for (int j = 0; j < getNumTypeAccess(); ++j) {
				if (i == j) continue;
				TypeDecl t1 = getTypeAccess(i).type();
				TypeDecl t2 = getTypeAccess(j).type();
				if (t2.instanceOf(t1)) {
					error(t2.fullName() + " is a subclass of " +
							t1.fullName());
					pass = false;
				}
			}
		}
	}
  /**
	 * Pretty printing of catch parameter declaration.
	 * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:156
   */
  public void toString(StringBuffer sb) {
		getModifiers().toString(sb);
		for (int i = 0; i < getNumTypeAccess(); ++i) {
			if (i > 0) sb.append(" | ");
			getTypeAccess(i).toString(sb);
		}
		sb.append(" "+getID());
	}
  /**
	 * Duplicate declaration checking for catch parameters.
	 * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:186
   */
  public void nameCheck() {
		SimpleSet decls = outerScope().lookupVariable(name());
		for(Iterator iter = decls.iterator(); iter.hasNext(); ) {
			Variable var = (Variable)iter.next();
			if (var instanceof VariableDeclaration) {
				VariableDeclaration decl = (VariableDeclaration)var;
				if (decl.enclosingBodyDecl() == enclosingBodyDecl())
					error("duplicate declaration of "+
						"catch parameter "+name());
			} else if (var instanceof ParameterDeclaration) {
				ParameterDeclaration decl = (ParameterDeclaration)var;
				if (decl.enclosingBodyDecl() == enclosingBodyDecl())
					error("duplicate declaration of "+
						"catch parameter "+name());
			} else if (var instanceof CatchParameterDeclaration) {
				CatchParameterDeclaration decl = (CatchParameterDeclaration)var;
				if (decl.enclosingBodyDecl() == enclosingBodyDecl())
					error("duplicate declaration of "+
						"catch parameter "+name());
			}
		}

		// 8.4.1  
		if (!lookupVariable(name()).contains(this))
			error("duplicate declaration of catch parameter " +
					name());
	}
  /**
   * @ast method 
   * 
   */
  public CatchParameterDeclaration() {
    super();


  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @ast method 
   * 
   */
  public void init$Children() {
    children = new ASTNode[2];
    setChild(new List(), 1);
  }
  /**
   * @ast method 
   * 
   */
  public CatchParameterDeclaration(Modifiers p0, List<Access> p1, String p2) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
  }
  /**
   * @ast method 
   * 
   */
  public CatchParameterDeclaration(Modifiers p0, List<Access> p1, beaver.Symbol p2) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Replaces the Modifiers child.
   * @param node The new node to replace the Modifiers child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Modifiers child.
   * @return The current node used as the Modifiers child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * Retrieves the Modifiers child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Modifiers child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Replaces the TypeAccess list.
   * @param list The new list node to be used as the TypeAccess list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeAccessList(List<Access> list) {
    setChild(list, 1);
  }
  /**
   * Retrieves the number of children in the TypeAccess list.
   * @return Number of children in the TypeAccess list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumTypeAccess() {
    return getTypeAccessList().getNumChild();
  }
  /**
   * Retrieves the number of children in the TypeAccess list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the TypeAccess list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumTypeAccessNoTransform() {
    return getTypeAccessListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the TypeAccess list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the TypeAccess list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getTypeAccess(int i) {
    return (Access)getTypeAccessList().getChild(i);
  }
  /**
   * Append an element to the TypeAccess list.
   * @param node The element to append to the TypeAccess list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addTypeAccess(Access node) {
    List<Access> list = (parent == null || state == null) ? getTypeAccessListNoTransform() : getTypeAccessList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addTypeAccessNoTransform(Access node) {
    List<Access> list = getTypeAccessListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the TypeAccess list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeAccess(Access node, int i) {
    List<Access> list = getTypeAccessList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the TypeAccess list.
   * @return The node representing the TypeAccess list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Access> getTypeAccesss() {
    return getTypeAccessList();
  }
  /**
   * Retrieves the TypeAccess list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the TypeAccess list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Access> getTypeAccesssNoTransform() {
    return getTypeAccessListNoTransform();
  }
  /**
   * Retrieves the TypeAccess list.
   * @return The node representing the TypeAccess list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeAccessList() {
    List<Access> list = (List<Access>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the TypeAccess list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the TypeAccess list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeAccessListNoTransform() {
    return (List<Access>)getChildNoTransform(1);
  }
  /**
   * Replaces the lexeme ID.
   * @param value The new value for the lexeme ID.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  protected String tokenString_ID;
  /**
   * @ast method 
   * 
   */
  
  public int IDstart;
  /**
   * @ast method 
   * 
   */
  
  public int IDend;
  /**
   * JastAdd-internal setter for lexeme ID using the Beaver parser.
   * @apilevel internal
   * @ast method 
   * 
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Retrieves the value for the lexeme ID.
   * @return The value for the lexeme ID.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:17
   */
  public boolean isParameter() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:20
   */
  public boolean isClassVariable() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:21
   */
  public boolean isInstanceVariable() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:25
   */
  public boolean isLocalVariable() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
	 * The catch parameter of a multi-catch clause is implicitly final.
	 * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:34
   */
  public boolean isFinal() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:35
   */
  public boolean isVolatile() {
    ASTNode$State state = state();
    try {  return getModifiers().isVolatile();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:36
   */
  public boolean isBlank() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:37
   */
  public boolean isStatic() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:39
   */
  public String name() {
    ASTNode$State state = state();
    try {  return getID();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:41
   */
  public boolean hasInit() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:42
   */
  public Expr getInit() {
    ASTNode$State state = state();
    try {
		throw new UnsupportedOperationException();
	}
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:45
   */
  public Constant constant() {
    ASTNode$State state = state();
    try {
		throw new UnsupportedOperationException();
	}
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:50
   */
  public boolean isSynthetic() {
    ASTNode$State state = state();
    try {  return getModifiers().isSynthetic();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean sourceVariableDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected Variable sourceVariableDecl_value;
  /**
	 * @see "Generics.jrag"
	 * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:55
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
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:61
   */
  public int size() {
    ASTNode$State state = state();
    try {  return 1;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:62
   */
  public boolean isEmpty() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:66
   */
  public boolean contains(Object o) {
    ASTNode$State state = state();
    try {  return this == o;  }
    finally {
    }
  }
  /**
	 * A catch parameter declared with a disjunction type has the
	 * effective type lub(t1, t2, ...)
	 *
	 * @see "JLSv3 &sect;15.12.2.7"
	 * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:173
   */
  public TypeDecl type() {
    ASTNode$State state = state();
    try {
		ArrayList<TypeDecl> list = new ArrayList<TypeDecl>();
		for (int i = 0; i < getNumTypeAccess(); i++)
			list.add(getTypeAccess(i).type());
		return lookupLUBType(list).lub();
	}
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean throwTypes_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection<TypeDecl> throwTypes_value;
  /**
   * @attribute syn
   * @aspect PreciseRethrow
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:38
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection<TypeDecl> throwTypes() {
    if(throwTypes_computed) {
      return throwTypes_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    throwTypes_value = throwTypes_compute();
      if(isFinal && num == state().boundariesCrossed) throwTypes_computed = true;
    return throwTypes_value;
  }
  /**
   * @apilevel internal
   */
  private Collection<TypeDecl> throwTypes_compute() {  return catchClause().caughtExceptions();  }
  /**
	 * Inherit the lookupVariable attribute.
	 * @attribute inh
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:14
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupVariable(String name) {
    ASTNode$State state = state();
    SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
    return lookupVariable_String_value;
  }
  /**
   * @attribute inh
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:22
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isMethodParameter() {
    ASTNode$State state = state();
    boolean isMethodParameter_value = getParent().Define_boolean_isMethodParameter(this, null);
    return isMethodParameter_value;
  }
  /**
   * @attribute inh
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:23
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isConstructorParameter() {
    ASTNode$State state = state();
    boolean isConstructorParameter_value = getParent().Define_boolean_isConstructorParameter(this, null);
    return isConstructorParameter_value;
  }
  /**
   * @attribute inh
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:24
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isExceptionHandlerParameter() {
    ASTNode$State state = state();
    boolean isExceptionHandlerParameter_value = getParent().Define_boolean_isExceptionHandlerParameter(this, null);
    return isExceptionHandlerParameter_value;
  }
  /**
   * @attribute inh
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:49
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl hostType() {
    ASTNode$State state = state();
    TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
    return hostType_value;
  }
  /**
   * @attribute inh
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:165
   */
  @SuppressWarnings({"unchecked", "cast"})
  public LUBType lookupLUBType(Collection bounds) {
    ASTNode$State state = state();
    LUBType lookupLUBType_Collection_value = getParent().Define_LUBType_lookupLUBType(this, null, bounds);
    return lookupLUBType_Collection_value;
  }
  /**
   * @attribute inh
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:180
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VariableScope outerScope() {
    ASTNode$State state = state();
    VariableScope outerScope_value = getParent().Define_VariableScope_outerScope(this, null);
    return outerScope_value;
  }
  /**
   * @attribute inh
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:181
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl enclosingBodyDecl() {
    ASTNode$State state = state();
    BodyDecl enclosingBodyDecl_value = getParent().Define_BodyDecl_enclosingBodyDecl(this, null);
    return enclosingBodyDecl_value;
  }
  /**
   * @attribute inh
   * @aspect PreciseRethrow
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:128
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CatchClause catchClause() {
    ASTNode$State state = state();
    CatchClause catchClause_value = getParent().Define_CatchClause_catchClause(this, null);
    return catchClause_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:92
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeAccessListNoTransform())  {
    int i = caller.getIndexOfChild(child);
    return NameType.TYPE_NAME;
  }
    else {      return getParent().Define_NameType_nameType(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
