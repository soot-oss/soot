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
 * Type access for a generic class with an empty type parameter list.
 * @production DiamondAccess : {@link Access} ::= <span class="component">TypeAccess:{@link Access}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.ast:4
 */
public class DiamondAccess extends Access implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    type_computed = false;
    type_value = null;
    typeArguments_MethodDecl_values = null;
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
  public DiamondAccess clone() throws CloneNotSupportedException {
    DiamondAccess node = (DiamondAccess)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.typeArguments_MethodDecl_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public DiamondAccess copy() {
    try {
      DiamondAccess node = (DiamondAccess) clone();
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
  public DiamondAccess fullCopy() {
    DiamondAccess tree = (DiamondAccess) copy();
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
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:72
   */
  protected static SimpleSet mostSpecific(
			SimpleSet maxSpecific, MethodDecl decl) {
		if (maxSpecific.isEmpty()) {
			maxSpecific = maxSpecific.add(decl);
		} else {
			if (decl.moreSpecificThan(
						(MethodDecl)maxSpecific.iterator().next()))
				maxSpecific = SimpleSet.emptySet.add(decl);
			else if (!((MethodDecl)maxSpecific.iterator().next()).
					moreSpecificThan(decl))
				maxSpecific = maxSpecific.add(decl);
		}
		return maxSpecific;
	}
  /**
	 * Choose a constructor for the diamond operator using placeholder
	 * methods.
	 * @ast method 
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:91
   */
  protected SimpleSet chooseConstructor() {
		ClassInstanceExpr instanceExpr = getClassInstanceExpr();
		TypeDecl type = getTypeAccess().type();

		assert instanceExpr != null;
		assert type instanceof ParClassDecl;

		GenericClassDecl genericType =
			(GenericClassDecl) ((ParClassDecl)type).genericDecl();

		List<PlaceholderMethodDecl> placeholderMethods =
			genericType.getPlaceholderMethodList();

		SimpleSet maxSpecific = SimpleSet.emptySet;
		Collection<MethodDecl> potentiallyApplicable =
			potentiallyApplicable(placeholderMethods);
		for (MethodDecl candidate : potentiallyApplicable) {
			if (applicableBySubtyping(instanceExpr, candidate) ||
					applicableByMethodInvocationConversion(
						instanceExpr, candidate) ||
					applicableByVariableArity(instanceExpr, candidate))
				maxSpecific = mostSpecific(maxSpecific, candidate);

		}
		return maxSpecific;
	}
  /**
	 * Select potentially applicable method declarations
	 * from a set of candidates.
	 * Type inference is applied to the (potentially) applicable candidates.
	 * @ast method 
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:224
   */
  protected Collection<MethodDecl> potentiallyApplicable(
			List<PlaceholderMethodDecl> candidates) {
		Collection<MethodDecl> potentiallyApplicable =
			new LinkedList<MethodDecl>();
		for (GenericMethodDecl candidate : candidates) {
			if (potentiallyApplicable(candidate)) {
				MethodDecl decl = candidate.lookupParMethodDecl(
						typeArguments(candidate));
				potentiallyApplicable.add(decl);
			}
		}
		return potentiallyApplicable;
	}
  /**
	 * @return false if the candidate method is not applicable.
	 * @ast method 
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:241
   */
  protected boolean potentiallyApplicable(
			GenericMethodDecl candidate) {
		if (candidate.isVariableArity() &&
				!(getClassInstanceExpr().arity() >= candidate.arity()-1))
			return false;
		if (!candidate.isVariableArity() &&
				!(getClassInstanceExpr().arity() == candidate.arity()))
			return false;

		java.util.List<TypeDecl> typeArgs = typeArguments(candidate);
		if (typeArgs.size() != 0) {
			if (candidate.getNumTypeParameter() != typeArgs.size())
				return false;
			for (int i = 0; i < candidate.getNumTypeParameter(); i++)
				if (!typeArgs.get(i).subtype(
							candidate.original().getTypeParameter(i)))
					return false;
		}
		return true;
	}
  /**
	 * Diamond type inference.
	 * @ast method 
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:297
   */
  public Collection<TypeDecl> computeConstraints(
			GenericMethodDecl decl) {
		Constraints c = new Constraints();
		// store type parameters
		for (int i = 0; i < decl.original().getNumTypeParameter(); i++)
			c.addTypeVariable(decl.original().getTypeParameter(i));

		ClassInstanceExpr instanceExpr = getClassInstanceExpr();
		for (int i = 0; i < instanceExpr.getNumArg(); i++) {
			TypeDecl A = instanceExpr.getArg(i).type();
			int index = i >= decl.getNumParameter() ?
				decl.getNumParameter() - 1 : i;
			TypeDecl F = decl.getParameter(index).type();
			if (decl.getParameter(index) instanceof
					VariableArityParameterDeclaration &&
					(instanceExpr.getNumArg() != decl.getNumParameter() ||
					!A.isArrayDecl())) {
				F = F.componentType();
			}
			c.convertibleTo(A, F);
		}
		if (c.rawAccess)
			return new ArrayList();

		c.resolveEqualityConstraints();
		c.resolveSupertypeConstraints();

		if (c.unresolvedTypeArguments()) {
			TypeDecl S = assignConvertedType();
			if (S.isUnboxedPrimitive())
				S = S.boxed();
			TypeDecl R = decl.type();
			if (R.isVoid())
				R = typeObject();

			c.convertibleFrom(S, R);
			c.resolveEqualityConstraints();
			c.resolveSupertypeConstraints();
			c.resolveSubtypeConstraints();
		}

		return c.typeArguments();
	}
  /**
	 * @return true if the method is applicable by subtyping
	 * @ast method 
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:344
   */
  protected boolean applicableBySubtyping(
			ClassInstanceExpr expr, MethodDecl method) {
		if (method.getNumParameter() != expr.getNumArg())
			return false;
		for (int i = 0; i < method.getNumParameter(); i++)
			if(!expr.getArg(i).type().instanceOf(method.getParameter(i).type()))
				return false;
		return true;
	}
  /**
	 * @return true if the method is applicable by method invocation conversion
	 * @ast method 
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:357
   */
  protected boolean applicableByMethodInvocationConversion(
			ClassInstanceExpr expr, MethodDecl method) {
		if (method.getNumParameter() != expr.getNumArg())
			return false;
		for (int i = 0; i < method.getNumParameter(); i++)
			if (!expr.getArg(i).type().methodInvocationConversionTo(
						method.getParameter(i).type()))
				return false;
		return true;
	}
  /**
	 * @return true if the method is applicable by variable arity
	 * @ast method 
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:371
   */
  protected boolean applicableByVariableArity(
			ClassInstanceExpr expr, MethodDecl method) {
		for (int i = 0; i < method.getNumParameter() - 1; i++)
			if(!expr.getArg(i).type().methodInvocationConversionTo(
						method.getParameter(i).type()))
				return false;
		for (int i = method.getNumParameter() - 1; i < expr.getNumArg(); i++)
			if (!expr.getArg(i).type().methodInvocationConversionTo(
						method.lastParameter().type().componentType()))
				return false;
		return true;
	}
  /**
	 * Checks if this diamond access is legal.
	 * The diamond access is not legal if it either is part of an inner class
	 * declaration, if it is used to access a non-generic type, or if it is
	 * part of a call to a generic constructor with explicit type arguments.
	 * @ast method 
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:487
   */
  public void typeCheck() {
		if (isAnonymousDecl())
			error("the diamond operator can not be used with "+
					"anonymous classes");
		if (isExplicitGenericConstructorAccess())
			error("the diamond operator may not be used with generic "+
					"constructors with explicit type parameters");
		if (getClassInstanceExpr() == null)
			error("the diamond operator can only be used in "+
					"class instance expressions");
		if (!(getTypeAccess().type() instanceof ParClassDecl))
			error("the diamond operator can only be used to "+
					"instantiate generic classes");
	}
  /**
	 * Pretty printing of diamond access.
	 * @ast method 
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:505
   */
  public void toString(StringBuffer sb) {
		getTypeAccess().toString(sb);
		sb.append("<>");
	}
  /**
   * @ast method 
   * 
   */
  public DiamondAccess() {
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
    children = new ASTNode[1];
  }
  /**
   * @ast method 
   * 
   */
  public DiamondAccess(Access p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 1;
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
   * Replaces the TypeAccess child.
   * @param node The new node to replace the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeAccess(Access node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the TypeAccess child.
   * @return The current node used as the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Access getTypeAccess() {
    return (Access)getChild(0);
  }
  /**
   * Retrieves the TypeAccess child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the TypeAccess child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(0);
  }
  /**
   * @apilevel internal
   */
  protected boolean type_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl type_value;
  /**
	 * If this DiamondAccess node constitutes a legal use of
	 * the diamond operator, the inferred generic type for the
	 * enclosing class instance expression is returned.
	 * @attribute syn
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:39
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
    if(type_computed) {
      return type_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    type_value = type_compute();
      if(isFinal && num == state().boundariesCrossed) type_computed = true;
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {
		TypeDecl accessType = getTypeAccess().type();

		if (isAnonymousDecl())
			return accessType;

		if (getClassInstanceExpr() == null)
			// it is an error if the DiamondAccess does not occurr
			// within a class instance creation expression, but this
			// error is handled in typeCheck
			return accessType;

		if (!(accessType instanceof ParClassDecl))
			// it is an error if the TypeDecl of a DiamondAccess is not
			// a generic type, but this error is handled in typeCheck
			return accessType;

		SimpleSet maxSpecific = chooseConstructor();

		if (maxSpecific.isEmpty())
			return getTypeAccess().type();

		MethodDecl constructor = (MethodDecl) maxSpecific.iterator().next();
		return constructor.type();
	}
  /**
   * @attribute syn
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:65
   */
  public boolean isDiamond() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  protected java.util.Map typeArguments_MethodDecl_values;
  /**
	 * Type inference for placeholder methods.
	 * @attribute syn
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:267
   */
  @SuppressWarnings({"unchecked", "cast"})
  public java.util.List<TypeDecl> typeArguments(MethodDecl decl) {
    Object _parameters = decl;
    if(typeArguments_MethodDecl_values == null) typeArguments_MethodDecl_values = new java.util.HashMap(4);
    if(typeArguments_MethodDecl_values.containsKey(_parameters)) {
      return (java.util.List<TypeDecl>)typeArguments_MethodDecl_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    java.util.List<TypeDecl> typeArguments_MethodDecl_value = typeArguments_compute(decl);
      if(isFinal && num == state().boundariesCrossed) typeArguments_MethodDecl_values.put(_parameters, typeArguments_MethodDecl_value);
    return typeArguments_MethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private java.util.List<TypeDecl> typeArguments_compute(MethodDecl decl) {
		java.util.List<TypeDecl> typeArguments = new LinkedList<TypeDecl>();
		if (decl instanceof GenericMethodDecl) {
			GenericMethodDecl method = (GenericMethodDecl) decl;
			Collection<TypeDecl> arguments = computeConstraints(method);
			if (arguments.isEmpty())
				return typeArguments;
			int i = 0;
			for (TypeDecl argument : arguments) {
				if (argument == null) {
					TypeVariable v = method.original().getTypeParameter(i);
					if (v.getNumTypeBound() == 0)
						argument = typeObject();
					else if (v.getNumTypeBound() == 1)
						argument = v.getTypeBound(0).type();
					else
						argument = v.lubType();
				}
				typeArguments.add(argument);

				i += 1;
			}
		}
		return typeArguments;
	}
  /**
   * @attribute inh
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:68
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ClassInstanceExpr getClassInstanceExpr() {
    ASTNode$State state = state();
    ClassInstanceExpr getClassInstanceExpr_value = getParent().Define_ClassInstanceExpr_getClassInstanceExpr(this, null);
    return getClassInstanceExpr_value;
  }
  /**
   * @attribute inh
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:262
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeObject() {
    ASTNode$State state = state();
    TypeDecl typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
    return typeObject_value;
  }
  /**
	 * @return true if this access is part of an anonymous class declaration
	 * @attribute inh
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:452
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isAnonymousDecl() {
    ASTNode$State state = state();
    boolean isAnonymousDecl_value = getParent().Define_boolean_isAnonymousDecl(this, null);
    return isAnonymousDecl_value;
  }
  /**
	 * @return true if the Access is part of a generic constructor invocation
	 * with explicit type arguments
	 * @attribute inh
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:468
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isExplicitGenericConstructorAccess() {
    ASTNode$State state = state();
    boolean isExplicitGenericConstructorAccess_value = getParent().Define_boolean_isExplicitGenericConstructorAccess(this, null);
    return isExplicitGenericConstructorAccess_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
