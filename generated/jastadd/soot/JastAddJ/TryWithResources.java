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
 * The JSR 334 try with resources statement.
 * @production TryWithResources : {@link TryStmt} ::= <span class="component">Resource:{@link ResourceDeclaration}*</span> <span class="component">{@link Block}</span> <span class="component">{@link CatchClause}*</span> <span class="component">[Finally:{@link Block}]</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.ast:4
 */
public class TryWithResources extends TryStmt implements Cloneable, VariableScope {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    localLookup_String_values = null;
    localVariableDeclaration_String_values = null;
    isDAafter_Variable_values = null;
    catchableException_TypeDecl_values = null;
    handlesException_TypeDecl_values = null;
    typeError_computed = false;
    typeError_value = null;
    typeRuntimeException_computed = false;
    typeRuntimeException_value = null;
    lookupVariable_String_values = null;
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
  public TryWithResources clone() throws CloneNotSupportedException {
    TryWithResources node = (TryWithResources)super.clone();
    node.localLookup_String_values = null;
    node.localVariableDeclaration_String_values = null;
    node.isDAafter_Variable_values = null;
    node.catchableException_TypeDecl_values = null;
    node.handlesException_TypeDecl_values = null;
    node.typeError_computed = false;
    node.typeError_value = null;
    node.typeRuntimeException_computed = false;
    node.typeRuntimeException_value = null;
    node.lookupVariable_String_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TryWithResources copy() {
    try {
      TryWithResources node = (TryWithResources) clone();
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
  public TryWithResources fullCopy() {
    TryWithResources tree = (TryWithResources) copy();
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
	 * Exception error checks.
	 * @ast method 
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:40
   */
  public void exceptionHandling() {

		// Check exception handling of exceptions on auto closing of resource
		for (ResourceDeclaration resource : getResourceList()) {
			MethodDecl close = lookupClose(resource);
			if (close == null) continue;
			for (Access exception : close.getExceptionList()) {
				TypeDecl exceptionType = exception.type();
				if (!twrHandlesException(exceptionType))
					error("automatic closing of resource "+resource.name()+
							" may raise the uncaught exception "+exceptionType.fullName()+"; "+
							"it must be caught or declared as being thrown");
			}
		}
	}
  /**
	 * Returns true if the try-with-resources statement can throw
	 * an exception of type (or a subtype of) catchType.
	 * @ast method 
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:181
   */
  protected boolean reachedException(TypeDecl catchType) {
		boolean found = false;
		// found is true if the exception type is caught by a catch clause
		for(int i = 0; i < getNumCatchClause() && !found; i++)
			if(getCatchClause(i).handles(catchType))
				found = true;
		// if an exception is thrown in the block and the exception is not caught and
		// either there is no finally block or the finally block can complete normally
		if(!found && (!hasFinally() || getFinally().canCompleteNormally()) )
			if(catchableException(catchType))
				return true;
		// even if the exception is caught by the catch clauses they may 
		// throw new exceptions
		for(int i = 0; i < getNumCatchClause(); i++)
			if(getCatchClause(i).reachedException(catchType))
				return true;
		return hasFinally() && getFinally().reachedException(catchType);
	}
  /**
 	 * Pretty printing of try-with-resources
 	 * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:244
   */
  public void toString(StringBuffer sb) {
		sb.append(indent() + "try (");
		for (ResourceDeclaration resource : getResourceList()) {
			sb.append(resource.toString());
		}
		sb.append(") ");
		getBlock().toString(sb);
		for (CatchClause cc : getCatchClauseList()) {
			sb.append(" ");
			cc.toString(sb);
		}
		if (hasFinally()) {
			sb.append(" finally ");
			getFinally().toString(sb);
		}
	}
  /**
   * @ast method 
   * 
   */
  public TryWithResources() {
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
    children = new ASTNode[4];
    setChild(new List(), 0);
    setChild(new List(), 2);
    setChild(new Opt(), 3);
  }
  /**
   * @ast method 
   * 
   */
  public TryWithResources(List<ResourceDeclaration> p0, Block p1, List<CatchClause> p2, Opt<Block> p3) {
    setChild(p0, 0);
    setChild(p1, 1);
    setChild(p2, 2);
    setChild(p3, 3);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 4;
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
   * Replaces the Resource list.
   * @param list The new list node to be used as the Resource list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setResourceList(List<ResourceDeclaration> list) {
    setChild(list, 0);
  }
  /**
   * Retrieves the number of children in the Resource list.
   * @return Number of children in the Resource list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumResource() {
    return getResourceList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Resource list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Resource list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumResourceNoTransform() {
    return getResourceListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Resource list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Resource list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ResourceDeclaration getResource(int i) {
    return (ResourceDeclaration)getResourceList().getChild(i);
  }
  /**
   * Append an element to the Resource list.
   * @param node The element to append to the Resource list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addResource(ResourceDeclaration node) {
    List<ResourceDeclaration> list = (parent == null || state == null) ? getResourceListNoTransform() : getResourceList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addResourceNoTransform(ResourceDeclaration node) {
    List<ResourceDeclaration> list = getResourceListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Resource list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setResource(ResourceDeclaration node, int i) {
    List<ResourceDeclaration> list = getResourceList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Resource list.
   * @return The node representing the Resource list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<ResourceDeclaration> getResources() {
    return getResourceList();
  }
  /**
   * Retrieves the Resource list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Resource list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<ResourceDeclaration> getResourcesNoTransform() {
    return getResourceListNoTransform();
  }
  /**
   * Retrieves the Resource list.
   * @return The node representing the Resource list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ResourceDeclaration> getResourceList() {
    List<ResourceDeclaration> list = (List<ResourceDeclaration>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the Resource list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Resource list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ResourceDeclaration> getResourceListNoTransform() {
    return (List<ResourceDeclaration>)getChildNoTransform(0);
  }
  /**
   * Replaces the Block child.
   * @param node The new node to replace the Block child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBlock(Block node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the Block child.
   * @return The current node used as the Block child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Block getBlock() {
    return (Block)getChild(1);
  }
  /**
   * Retrieves the Block child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Block child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Block getBlockNoTransform() {
    return (Block)getChildNoTransform(1);
  }
  /**
   * Replaces the CatchClause list.
   * @param list The new list node to be used as the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setCatchClauseList(List<CatchClause> list) {
    setChild(list, 2);
  }
  /**
   * Retrieves the number of children in the CatchClause list.
   * @return Number of children in the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumCatchClause() {
    return getCatchClauseList().getNumChild();
  }
  /**
   * Retrieves the number of children in the CatchClause list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the CatchClause list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumCatchClauseNoTransform() {
    return getCatchClauseListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the CatchClause list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CatchClause getCatchClause(int i) {
    return (CatchClause)getCatchClauseList().getChild(i);
  }
  /**
   * Append an element to the CatchClause list.
   * @param node The element to append to the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addCatchClause(CatchClause node) {
    List<CatchClause> list = (parent == null || state == null) ? getCatchClauseListNoTransform() : getCatchClauseList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addCatchClauseNoTransform(CatchClause node) {
    List<CatchClause> list = getCatchClauseListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the CatchClause list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setCatchClause(CatchClause node, int i) {
    List<CatchClause> list = getCatchClauseList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the CatchClause list.
   * @return The node representing the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<CatchClause> getCatchClauses() {
    return getCatchClauseList();
  }
  /**
   * Retrieves the CatchClause list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the CatchClause list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<CatchClause> getCatchClausesNoTransform() {
    return getCatchClauseListNoTransform();
  }
  /**
   * Retrieves the CatchClause list.
   * @return The node representing the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<CatchClause> getCatchClauseList() {
    List<CatchClause> list = (List<CatchClause>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the CatchClause list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the CatchClause list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<CatchClause> getCatchClauseListNoTransform() {
    return (List<CatchClause>)getChildNoTransform(2);
  }
  /**
   * Replaces the optional node for the Finally child. This is the {@code Opt} node containing the child Finally, not the actual child!
   * @param opt The new node to be used as the optional node for the Finally child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setFinallyOpt(Opt<Block> opt) {
    setChild(opt, 3);
  }
  /**
   * Check whether the optional Finally child exists.
   * @return {@code true} if the optional Finally child exists, {@code false} if it does not.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public boolean hasFinally() {
    return getFinallyOpt().getNumChild() != 0;
  }
  /**
   * Retrieves the (optional) Finally child.
   * @return The Finally child, if it exists. Returns {@code null} otherwise.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Block getFinally() {
    return (Block)getFinallyOpt().getChild(0);
  }
  /**
   * Replaces the (optional) Finally child.
   * @param node The new node to be used as the Finally child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setFinally(Block node) {
    getFinallyOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getFinallyOpt() {
    return (Opt<Block>)getChild(3);
  }
  /**
   * Retrieves the optional node for child Finally. This is the {@code Opt} node containing the child Finally, not the actual child!
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The optional node for child Finally.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getFinallyOptNoTransform() {
    return (Opt<Block>)getChildNoTransform(3);
  }
  /**
	 * This attribute computes whether or not the TWR statement
	 * has a catch clause which handles the exception.
	 * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:60
   */
  public boolean catchHandlesException(TypeDecl exceptionType) {
    ASTNode$State state = state();
    try {
		for (int i = 0; i < getNumCatchClause(); i++)
			if (getCatchClause(i).handles(exceptionType))
				return true;
		return false;
	}
    finally {
    }
  }
  /**
	 * Returns true if exceptions of type exceptionType are handled
	 * in the try-with-resources statement or any containing statement
	 * within the directly enclosing method or initializer block.
	 * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:72
   */
  public boolean twrHandlesException(TypeDecl exceptionType) {
    ASTNode$State state = state();
    try {
		if (catchHandlesException(exceptionType))
			return true;
		if (hasFinally() && !getFinally().canCompleteNormally())
			return true;
		return handlesException(exceptionType);
	}
    finally {
    }
  }
  /**
	 * Lookup the close method declaration for the resource which is being used.
	 * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:95
   */
  public MethodDecl lookupClose(ResourceDeclaration resource) {
    ASTNode$State state = state();
    try {
		TypeDecl resourceType = resource.getTypeAccess().type();
		for (MethodDecl method : (Collection<MethodDecl>) resourceType.memberMethods("close")) {
			if (method.getNumParameter() == 0) {
				return method;
			}
		}
		return null;
		/* We can't throw a runtime exception here. If there is no close method it
		 * likely means that the resource type is not a subtype of java.lang.AutoCloseable
		 * and type checking will report this error.
		 */
		//throw new RuntimeException("close() not found for resource type "+resourceType.fullName());
	}
    finally {
    }
  }
  protected java.util.Map localLookup_String_values;
  /**
   * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:128
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localLookup(String name) {
    Object _parameters = name;
    if(localLookup_String_values == null) localLookup_String_values = new java.util.HashMap(4);
    if(localLookup_String_values.containsKey(_parameters)) {
      return (SimpleSet)localLookup_String_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet localLookup_String_value = localLookup_compute(name);
      if(isFinal && num == state().boundariesCrossed) localLookup_String_values.put(_parameters, localLookup_String_value);
    return localLookup_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localLookup_compute(String name) {
		VariableDeclaration v = localVariableDeclaration(name);
		if (v != null) return v;
		return lookupVariable(name);
	}
  protected java.util.Map localVariableDeclaration_String_values;
  /**
   * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:133
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VariableDeclaration localVariableDeclaration(String name) {
    Object _parameters = name;
    if(localVariableDeclaration_String_values == null) localVariableDeclaration_String_values = new java.util.HashMap(4);
    if(localVariableDeclaration_String_values.containsKey(_parameters)) {
      return (VariableDeclaration)localVariableDeclaration_String_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    VariableDeclaration localVariableDeclaration_String_value = localVariableDeclaration_compute(name);
      if(isFinal && num == state().boundariesCrossed) localVariableDeclaration_String_values.put(_parameters, localVariableDeclaration_String_value);
    return localVariableDeclaration_String_value;
  }
  /**
   * @apilevel internal
   */
  private VariableDeclaration localVariableDeclaration_compute(String name) {
		for (ResourceDeclaration resource : getResourceList())
			if (resource.declaresVariable(name))
				return resource;
		return null;
	}
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:167
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
  private boolean isDAafter_compute(Variable v) {  return getBlock().isDAafter(v);  }
  /**
	 * True if the automatic closing of resources in this try-with-resources statement
	 * may throw an exception of type catchType.
	 * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:204
   */
  public boolean resourceClosingException(TypeDecl catchType) {
    ASTNode$State state = state();
    try {
		for (ResourceDeclaration resource : getResourceList()) {
			MethodDecl close = lookupClose(resource);
			if (close == null) continue;
			for (Access exception : close.getExceptionList()) {
				TypeDecl exceptionType = exception.type();
				if (catchType.mayCatch(exception.type()))
					return true;
			}
		}
		return false;
	}
    finally {
    }
  }
  /**
	 * True if the resource initialization of this try-with-resources statement
	 * may throw an exception of type catchType.
	 * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:221
   */
  public boolean resourceInitializationException(TypeDecl catchType) {
    ASTNode$State state = state();
    try {
		for (ResourceDeclaration resource : getResourceList()) {
			if (resource.reachedException(catchType))
				return true;
		}
		return false;
	}
    finally {
    }
  }
  protected java.util.Map catchableException_TypeDecl_values;
  /**
 	 * @see AST.TryStmt#catchableException(TypeDecl) TryStmt.catchableException(TypeDecl)
 	 * @attribute syn
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:232
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean catchableException(TypeDecl type) {
    Object _parameters = type;
    if(catchableException_TypeDecl_values == null) catchableException_TypeDecl_values = new java.util.HashMap(4);
    if(catchableException_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)catchableException_TypeDecl_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean catchableException_TypeDecl_value = catchableException_compute(type);
      if(isFinal && num == state().boundariesCrossed) catchableException_TypeDecl_values.put(_parameters, Boolean.valueOf(catchableException_TypeDecl_value));
    return catchableException_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean catchableException_compute(TypeDecl type) {  return getBlock().reachedException(type) ||
			resourceClosingException(type) ||
			resourceInitializationException(type);  }
  protected java.util.Map handlesException_TypeDecl_values;
  /**
	 * Inherit the handlesException attribute from methoddecl.
	 * @attribute inh
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:83
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean handlesException(TypeDecl exceptionType) {
    Object _parameters = exceptionType;
    if(handlesException_TypeDecl_values == null) handlesException_TypeDecl_values = new java.util.HashMap(4);
    if(handlesException_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)handlesException_TypeDecl_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
      if(isFinal && num == state().boundariesCrossed) handlesException_TypeDecl_values.put(_parameters, Boolean.valueOf(handlesException_TypeDecl_value));
    return handlesException_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeError_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeError_value;
  /**
   * @attribute inh
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:110
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeError() {
    if(typeError_computed) {
      return typeError_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeError_value = getParent().Define_TypeDecl_typeError(this, null);
      if(isFinal && num == state().boundariesCrossed) typeError_computed = true;
    return typeError_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeRuntimeException_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeRuntimeException_value;
  /**
   * @attribute inh
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:111
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeRuntimeException() {
    if(typeRuntimeException_computed) {
      return typeRuntimeException_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeRuntimeException_value = getParent().Define_TypeDecl_typeRuntimeException(this, null);
      if(isFinal && num == state().boundariesCrossed) typeRuntimeException_computed = true;
    return typeRuntimeException_value;
  }
  protected java.util.Map lookupVariable_String_values;
  /**
   * @attribute inh
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:141
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupVariable(String name) {
    Object _parameters = name;
    if(lookupVariable_String_values == null) lookupVariable_String_values = new java.util.HashMap(4);
    if(lookupVariable_String_values.containsKey(_parameters)) {
      return (SimpleSet)lookupVariable_String_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
      if(isFinal && num == state().boundariesCrossed) lookupVariable_String_values.put(_parameters, lookupVariable_String_value);
    return lookupVariable_String_value;
  }
  /**
   * @attribute inh
   * @aspect TryWithResources
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:145
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean resourcePreviouslyDeclared(String name) {
    ASTNode$State state = state();
    boolean resourcePreviouslyDeclared_String_value = getParent().Define_boolean_resourcePreviouslyDeclared(this, null, name);
    return resourcePreviouslyDeclared_String_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:88
   * @apilevel internal
   */
  public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    if(caller == getBlockNoTransform()) {
      return twrHandlesException(exceptionType);
    }
    else if(caller == getResourceListNoTransform())  {
    int i = caller.getIndexOfChild(child);
    return twrHandlesException(exceptionType);
  }
    else {      return super.Define_boolean_handlesException(caller, child, exceptionType);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:113
   * @apilevel internal
   */
  public boolean Define_boolean_reachableCatchClause(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    if(caller == getCatchClauseListNoTransform())  { 
    int childIndex = caller.getIndexOfChild(child);
    {
		for (int i = 0; i < childIndex; i++)
			if (getCatchClause(i).handles(exceptionType))
				return false;
		if (catchableException(exceptionType))
			return true;
		if (exceptionType.mayCatch(typeError()) || exceptionType.mayCatch(typeRuntimeException()))
			return true;
		return false;
	}
  }
    else {      return super.Define_boolean_reachableCatchClause(caller, child, exceptionType);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:127
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getBlockNoTransform()) {
      return localLookup(name);
    }
    else {      return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:142
   * @apilevel internal
   */
  public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
    if(caller == getResourceListNoTransform())  {
    int i = caller.getIndexOfChild(child);
    return this;
  }
    else {      return getParent().Define_VariableScope_outerScope(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:146
   * @apilevel internal
   */
  public boolean Define_boolean_resourcePreviouslyDeclared(ASTNode caller, ASTNode child, String name) {
    if(caller == getResourceListNoTransform())  { 
    int index = caller.getIndexOfChild(child);
    {
		for (int i = 0; i < index; ++i) {
			if (getResource(i).name().equals(name))
				return true;
		}
		return false;
	}
  }
    else {      return getParent().Define_boolean_resourcePreviouslyDeclared(this, caller, name);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TryWithResources.jrag:173
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBlockNoTransform()) {
      return getNumResource() == 0 ? isDAbefore(v) :
		getResource(getNumResource() - 1).isDAafter(v);
    }
    else if(caller == getResourceListNoTransform())  {
    int index = caller.getIndexOfChild(child);
    return index == 0 ? isDAbefore(v) : getResource(index - 1).isDAafter(v);
  }
    else {      return super.Define_boolean_isDAbefore(caller, child, v);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
