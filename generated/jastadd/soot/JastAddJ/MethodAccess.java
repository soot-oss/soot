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
 * @production MethodAccess : {@link Access} ::= <span class="component">&lt;ID:String&gt;</span> <span class="component">Arg:{@link Expr}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:20
 */
public class MethodAccess extends Access implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    computeDAbefore_int_Variable_values = null;
    exceptionCollection_computed = false;
    exceptionCollection_value = null;
    decls_computed = false;
    decls_value = null;
    decl_computed = false;
    decl_value = null;
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
  public MethodAccess clone() throws CloneNotSupportedException {
    MethodAccess node = (MethodAccess)super.clone();
    node.computeDAbefore_int_Variable_values = null;
    node.exceptionCollection_computed = false;
    node.exceptionCollection_value = null;
    node.decls_computed = false;
    node.decls_value = null;
    node.decl_computed = false;
    node.decl_value = null;
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
  public MethodAccess copy() {
    try {
      MethodAccess node = (MethodAccess) clone();
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
  public MethodAccess fullCopy() {
    MethodAccess tree = (MethodAccess) copy();
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
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:215
   */
  protected void collectExceptions(Collection c, ASTNode target) {
    super.collectExceptions(c, target);
    for(int i = 0; i < decl().getNumException(); i++)
      c.add(decl().getException(i).type());
  }
  /**
   * @ast method 
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:57
   */
  public void exceptionHandling() {
    for(Iterator iter = exceptionCollection().iterator(); iter.hasNext(); ) {
      TypeDecl exceptionType = (TypeDecl)iter.next();
      if(!handlesException(exceptionType))
        error("" + decl().hostType().fullName() + "." + this + " invoked in " + hostType().fullName() + " may throw uncaught exception " + exceptionType.fullName());
    }
  }
  /**
   * @ast method 
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:253
   */
  protected boolean reachedException(TypeDecl catchType) {
    for(Iterator iter = exceptionCollection().iterator(); iter.hasNext(); ) {
      TypeDecl exceptionType = (TypeDecl)iter.next();
      if(catchType.mayCatch(exceptionType))
        return true;
    }
    return super.reachedException(catchType);
  }
  /**
   * @ast method 
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:119
   */
  private static SimpleSet removeInstanceMethods(SimpleSet c) {
    SimpleSet set = SimpleSet.emptySet;
    for(Iterator iter = c.iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(m.isStatic())
        set = set.add(m);
    }
    return set;
  }
  /**
   * @ast method 
   * @aspect MethodDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:158
   */
  public boolean applicable(MethodDecl decl) {
    if(getNumArg() != decl.getNumParameter())
      return false;
    if(!name().equals(decl.name()))
      return false;
    for(int i = 0; i < getNumArg(); i++) {
      if(!getArg(i).type().instanceOf(decl.getParameter(i).type()))
        return false;
    }
    return true;
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:56
   */
  public MethodAccess(String name, List args, int start, int end) {
    this(name, args);
    setStart(start);
    setEnd(end);
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:457
   */
  public void toString(StringBuffer s) {
    s.append(name());
    s.append("(");
    if(getNumArg() > 0) {
      getArg(0).toString(s);
      for(int i = 1; i < getNumArg(); i++) {
        s.append(", ");
        getArg(i).toString(s);
      }
    }
    s.append(")");
  }
  /**
   * @ast method 
   * @aspect TypeHierarchyCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:23
   */
  public void nameCheck() {
    if(isQualified() && qualifier().isPackageAccess() && !qualifier().isUnknown())
      error("The method " + decl().signature() + 
          " can not be qualified by a package name.");
    if(isQualified() && decl().isAbstract() && qualifier().isSuperAccess())
      error("may not access abstract methods in superclass");
    if(decls().isEmpty() && (!isQualified() || !qualifier().isUnknown())) {
      StringBuffer s = new StringBuffer();
      s.append("no method named " + name());
      s.append("(");
      for(int i = 0; i < getNumArg(); i++) {
        if(i != 0)
          s.append(", ");
        s.append(getArg(i).type().typeName());
      }
      s.append(")" + " in " + methodHost() + " matches.");
      if(singleCandidateDecl() != null)
        s.append(" However, there is a method " + singleCandidateDecl().signature());
      error(s.toString());
    }
    if(decls().size() > 1) {
      boolean allAbstract = true;
      for(Iterator iter = decls().iterator(); iter.hasNext() && allAbstract; ) {
         MethodDecl m = (MethodDecl)iter.next();
        if(!m.isAbstract() && !m.hostType().isObject())
          allAbstract = false;
      }
      if(!allAbstract && validArgs()) {
        StringBuffer s = new StringBuffer();
        s.append("several most specific methods for " + this + "\n");
        for(Iterator iter = decls().iterator(); iter.hasNext(); ) {
          MethodDecl m = (MethodDecl)iter.next();
          s.append("    " + m.signature() + " in " + m.hostType().typeName() + "\n");
        }
        error(s.toString());
      }
       
    }
  }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:336
   */
  public void checkModifiers() {
    if(decl().isDeprecated() &&
      !withinDeprecatedAnnotation() &&
      hostType().topLevelType() != decl().hostType().topLevelType() &&
      !withinSuppressWarnings("deprecation"))
        warning(decl().signature() + " in " + decl().hostType().typeName() + " has been deprecated");
  }
  /**
   * @ast method 
   * @aspect GenericMethodsInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:46
   */
  public Collection computeConstraints(GenericMethodDecl decl) {
    Constraints c = new Constraints();
    // store type parameters
    for(int i = 0; i < decl.original().getNumTypeParameter(); i++)
      c.addTypeVariable(decl.original().getTypeParameter(i));
    
    // add initial constraints
    for(int i = 0; i < getNumArg(); i++) {
      TypeDecl A = getArg(i).type();
      int index = i >= decl.getNumParameter() ? decl.getNumParameter() - 1 : i;
      TypeDecl F = decl.getParameter(index).type();
      if(decl.getParameter(index) instanceof VariableArityParameterDeclaration 
         && (getNumArg() != decl.getNumParameter() || !A.isArrayDecl())) {
        F = F.componentType();
      }
      c.convertibleTo(A, F);
    }
    if(c.rawAccess)
      return new ArrayList();
    
    //c.printConstraints();
    //System.err.println("Resolving equality constraints");
    c.resolveEqualityConstraints();
    //c.printConstraints();

    //System.err.println("Resolving supertype constraints");
    c.resolveSupertypeConstraints();
    //c.printConstraints();

    //System.err.println("Resolving unresolved type arguments");
    //c.resolveBounds();
    //c.printConstraints();

    if(c.unresolvedTypeArguments()) {
      TypeDecl S = assignConvertedType();
      if(S.isUnboxedPrimitive())
        S = S.boxed();
      TypeDecl R = decl.type();
      // TODO: replace all uses of type variables in R with their inferred types
      TypeDecl Rprime = R;
      if(R.isVoid())
        R = typeObject();
      c.convertibleFrom(S, R);
      // TODO: additional constraints

      c.resolveEqualityConstraints();
      c.resolveSupertypeConstraints();
      //c.resolveBounds();

      c.resolveSubtypeConstraints();
    }

    return c.typeArguments();
  }
  /**
   * @ast method 
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:23
   */
  protected SimpleSet potentiallyApplicable(Collection candidates) {
    SimpleSet potentiallyApplicable = SimpleSet.emptySet;
    // select potentially applicable methods
    for(Iterator iter = candidates.iterator(); iter.hasNext(); ) {
      MethodDecl decl = (MethodDecl)iter.next();
      if(potentiallyApplicable(decl) && accessible(decl)) {
        if(decl instanceof GenericMethodDecl) {
          decl = ((GenericMethodDecl)decl).lookupParMethodDecl(typeArguments(decl));
        }
        potentiallyApplicable = potentiallyApplicable.add(decl);
      }
    }
    return potentiallyApplicable;
  }
  /**
   * @ast method 
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:38
   */
  protected SimpleSet applicableBySubtyping(SimpleSet potentiallyApplicable) {
    SimpleSet maxSpecific = SimpleSet.emptySet;
    for(Iterator iter = potentiallyApplicable.iterator(); iter.hasNext(); ) {
      MethodDecl decl = (MethodDecl)iter.next();
      if(applicableBySubtyping(decl))
        maxSpecific = mostSpecific(maxSpecific, decl);
    }
    return maxSpecific;
  }
  /**
   * @ast method 
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:48
   */
  protected SimpleSet applicableByMethodInvocationConversion(SimpleSet potentiallyApplicable, SimpleSet maxSpecific) {
    if(maxSpecific.isEmpty()) {
      for(Iterator iter = potentiallyApplicable.iterator(); iter.hasNext(); ) {
        MethodDecl decl = (MethodDecl)iter.next();
        if(applicableByMethodInvocationConversion(decl))
          maxSpecific = mostSpecific(maxSpecific, decl);
      }
    }
    return maxSpecific;
  }
  /**
   * @ast method 
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:59
   */
  protected SimpleSet applicableVariableArity(SimpleSet potentiallyApplicable, SimpleSet maxSpecific) {
    if(maxSpecific.isEmpty()) {
      for(Iterator iter = potentiallyApplicable.iterator(); iter.hasNext(); ) {
        MethodDecl decl = (MethodDecl)iter.next();
        if(decl.isVariableArity() && applicableVariableArity(decl))
          maxSpecific = mostSpecific(maxSpecific, decl);
      }
    }
    return maxSpecific;
  }
  /**
   * @ast method 
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:140
   */
  private static SimpleSet mostSpecific(SimpleSet maxSpecific, MethodDecl decl) {
    if(maxSpecific.isEmpty())
      maxSpecific = maxSpecific.add(decl);
    else {
      if(decl.moreSpecificThan((MethodDecl)maxSpecific.iterator().next()))
        maxSpecific = SimpleSet.emptySet.add(decl);
      else if(!((MethodDecl)maxSpecific.iterator().next()).moreSpecificThan(decl))
        maxSpecific = maxSpecific.add(decl);
    }
    return maxSpecific;
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:48
   */
  private TypeDecl refined_InnerClasses_MethodAccess_methodQualifierType() {
    if(hasPrevExpr())
      return prevExpr().type();
    TypeDecl typeDecl = hostType();
    while(typeDecl != null && !typeDecl.hasMethod(name()))
      typeDecl = typeDecl.enclosingType();
    if(typeDecl != null)
      return typeDecl;
    return decl().hostType();
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:113
   */
  public TypeDecl superAccessorTarget() {
    TypeDecl targetDecl = prevExpr().type();
    TypeDecl enclosing = hostType();
    do {
      enclosing = enclosing.enclosingType();
    } while (!enclosing.instanceOf(targetDecl));
    return enclosing;
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:69
   */
  public void refined_Transformations_MethodAccess_transformation() {
    MethodDecl m = decl();


    /*if(!isQualified() && !m.isStatic()) {
      TypeDecl typeDecl = hostType();
      while(typeDecl != null && !typeDecl.hasMethod(name()))
        typeDecl = typeDecl.enclosingType();
      ASTNode result = replace(this).with(typeDecl.createQualifiedAccess().qualifiesAccess(new ThisAccess("this")).qualifiesAccess(new MethodAccess(name(), getArgList())));
      result.transformation();
      return;
    }*/
    
    if(requiresAccessor()) {
      /* Access to private methods in enclosing types:
      The original MethodAccess is replaced with an access to an accessor method
      built by createAccessor(). This method is built lazily and differs from
      normal MethodDeclarations in the following ways:
      1) The method in the class file should always be static and the signature
         is thus changed to include a possible this reference as the first argument. 
      2) The method is always invoked using INVOKESTATIC
      3) The flags must indicate that the method is static and package private
      */
      super.transformation();
      replace(this).with(decl().createAccessor(methodQualifierType()).createBoundAccess(getArgList()));
      return;
    }
    else if(!m.isStatic() && isQualified() && prevExpr().isSuperAccess() && !hostType().instanceOf(prevExpr().type())) {
      decl().createSuperAccessor(superAccessorTarget());
    }
    super.transformation();
  }
  /**
   * @ast method 
   * @aspect SafeVarargs
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SafeVarargs.jrag:73
   */
  public void checkWarnings() {

		MethodDecl decl = decl();
		if (decl.getNumParameter() == 0) return;
		if (decl.getNumParameter() > getNumArg()) return;

		ParameterDeclaration param = decl.getParameter(
				decl.getNumParameter()-1);
		if (!withinSuppressWarnings("unchecked") &&
				!decl.hasAnnotationSafeVarargs() &&
				param.isVariableArity() &&
				!param.type().isReifiable())
			warning("unchecked array creation for variable " +
				"arity parameter of " + decl().name());
	}
  /**
   * @ast method 
   * @aspect EmitJimpleRefinements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:227
   */
  public void collectTypesToSignatures(Collection<Type> set) {
	 super.collectTypesToSignatures(set);
   addDependencyIfNeeded(set, methodQualifierType());
  }
  /**
   * @ast method 
   * 
   */
  public MethodAccess() {
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
    setChild(new List(), 0);
  }
  /**
   * @ast method 
   * 
   */
  public MethodAccess(String p0, List<Expr> p1) {
    setID(p0);
    setChild(p1, 0);
  }
  /**
   * @ast method 
   * 
   */
  public MethodAccess(beaver.Symbol p0, List<Expr> p1) {
    setID(p0);
    setChild(p1, 0);
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
   * Replaces the Arg list.
   * @param list The new list node to be used as the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setArgList(List<Expr> list) {
    setChild(list, 0);
  }
  /**
   * Retrieves the number of children in the Arg list.
   * @return Number of children in the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumArg() {
    return getArgList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Arg list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Arg list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumArgNoTransform() {
    return getArgListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Arg list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getArg(int i) {
    return (Expr)getArgList().getChild(i);
  }
  /**
   * Append an element to the Arg list.
   * @param node The element to append to the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addArg(Expr node) {
    List<Expr> list = (parent == null || state == null) ? getArgListNoTransform() : getArgList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addArgNoTransform(Expr node) {
    List<Expr> list = getArgListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Arg list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setArg(Expr node, int i) {
    List<Expr> list = getArgList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Arg list.
   * @return The node representing the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Expr> getArgs() {
    return getArgList();
  }
  /**
   * Retrieves the Arg list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Arg list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Expr> getArgsNoTransform() {
    return getArgListNoTransform();
  }
  /**
   * Retrieves the Arg list.
   * @return The node representing the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Expr> getArgList() {
    List<Expr> list = (List<Expr>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the Arg list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Arg list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Expr> getArgListNoTransform() {
    return (List<Expr>)getChildNoTransform(0);
  }
  /**
   * @ast method 
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:11
   */
    protected SimpleSet maxSpecific(Collection candidates) {
    SimpleSet potentiallyApplicable = potentiallyApplicable(candidates);
    // first phase
    SimpleSet maxSpecific = applicableBySubtyping(potentiallyApplicable);
    // second phase
    maxSpecific = applicableByMethodInvocationConversion(potentiallyApplicable,
        maxSpecific);
    // third phase
    maxSpecific = applicableVariableArity(potentiallyApplicable, maxSpecific);
    return maxSpecific;
  }
  /**
   * @ast method 
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:331
   */
    public void typeCheck() {
    if(isQualified() && decl().isAbstract() && qualifier().isSuperAccess())
      error("may not access abstract methods in superclass");
    if(!decl().isVariableArity() || invokesVariableArityAsArray()) {
      for(int i = 0; i < decl().getNumParameter(); i++) {
        TypeDecl exprType = getArg(i).type();
        TypeDecl parmType = decl().getParameter(i).type();
        if(!exprType.methodInvocationConversionTo(parmType) && !exprType.isUnknown() && !parmType.isUnknown()) {
          error("#The type " + exprType.typeName() + " of expr " +
            getArg(i) + " is not compatible with the method parameter " +
            decl().getParameter(i));
        }
      }
    }
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:300
   */
    protected TypeDecl refined_GenericsCodegen_MethodAccess_methodQualifierType() {
    TypeDecl typeDecl = refined_InnerClasses_MethodAccess_methodQualifierType();
    if(typeDecl == null)
      return null;
    typeDecl = typeDecl.erasure();
    MethodDecl m = decl().sourceMethodDecl();
    Collection methods = typeDecl.memberMethods(m.name());
    if(!methods.contains(decl()) && !methods.contains(m))
      return m.hostType();
    return typeDecl.erasure();
  }
  /**
   * @ast method 
   * @aspect VariableArityParametersCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/VariableArityParametersCodegen.jrag:16
   */
    public void transformation() {
    if(decl().isVariableArity() && !invokesVariableArityAsArray()) {
      // arguments to normal parameters
      List list = new List();
      for(int i = 0; i < decl().getNumParameter() - 1; i++)
        list.add(getArg(i).fullCopy());
      // arguments to variable arity parameters
      List last = new List();
      for(int i = decl().getNumParameter() - 1; i < getNumArg(); i++)
        last.add(getArg(i).fullCopy());
      // build an array holding arguments
      Access typeAccess = decl().lastParameter().type().elementType().createQualifiedAccess();
      for(int i = 0; i < decl().lastParameter().type().dimension(); i++)
        typeAccess = new ArrayTypeAccess(typeAccess);
      list.add(new ArrayCreationExpr(typeAccess, new Opt(new ArrayInit(last))));
      // replace argument list with augemented argument list
      setArgList(list);
    }
    refined_Transformations_MethodAccess_transformation();
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:127
   */
    private ArrayList buildArgList(Body b) {
    ArrayList list = new ArrayList();
    for(int i = 0; i < getNumArg(); i++)
      list.add(
        asImmediate(b,
          getArg(i).type().emitCastTo(b, // MethodInvocationConversion
            getArg(i),
            decl().getParameter(i).type()
          )
        )
      );
    return list;
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:141
   */
    public soot.Value eval(Body b) {
    MethodDecl decl = decl().erasedMethod();
    if(!decl().isStatic() && isQualified() && prevExpr().isSuperAccess()) {
      Local left = asLocal(b, createLoadQualifier(b));
      ArrayList list = buildArgList(b);
      soot.Value result;
      if(!hostType().instanceOf(prevExpr().type())) {
        MethodDecl m = decl.createSuperAccessor(superAccessorTarget());
        if(methodQualifierType().isInterfaceDecl())
          result = b.newInterfaceInvokeExpr(left, m.sootRef(), list, this);
        else
          result = b.newVirtualInvokeExpr(left, m.sootRef(), list, this);
      }
      else
        result = b.newSpecialInvokeExpr(left, sootRef(), list, this);
      if(decl.type() != decl().type())
        result = decl.type().emitCastTo(b, result, decl().type(), this);
      return type().isVoid() ? result : asLocal(b, result);
    }
    else {
      soot.Value result;
      if(!decl().isStatic()) {
        Local left = asLocal(b, createLoadQualifier(b));
        ArrayList list = buildArgList(b);
        if(methodQualifierType().isInterfaceDecl())
          result = b.newInterfaceInvokeExpr(left, sootRef(), list, this);
        else
          result = b.newVirtualInvokeExpr(left, sootRef(), list, this);
      }
      else {
        if(isQualified() && !qualifier().isTypeAccess())
          b.newTemp(qualifier().eval(b));
        ArrayList list = buildArgList(b);
        result = b.newStaticInvokeExpr(sootRef(), list, this);
      }
      if(decl.type() != decl().type())
        result = decl.type().emitCastTo(b, result, decl().type(), this);
      return type().isVoid() ? result : asLocal(b, result);
    }
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:182
   */
    private SootMethodRef sootRef() {
    MethodDecl decl = decl().erasedMethod();
    ArrayList parameters = new ArrayList();
    for(int i = 0; i < decl.getNumParameter(); i++)
      parameters.add(decl.getParameter(i).type().getSootType());
    SootMethodRef ref = Scene.v().makeMethodRef(
      methodQualifierType().getSootClassDecl(),
      decl.name(),
      parameters,
      decl.type().getSootType(),
      decl.isStatic()
    );
    return ref;
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:197
   */
    private soot.Value createLoadQualifier(Body b) {
    MethodDecl m = decl().erasedMethod();
    if(hasPrevExpr()) {
      // load explicit qualifier
      soot.Value v = prevExpr().eval(b);
      if(v ==  null)
        throw new Error("Problems evaluating " + prevExpr().getClass().getName());
      Local qualifier = asLocal(b, v /*prevExpr().eval(b)*/);
      // pop qualifier stack element for class variables
      // this qualifier must be computed to ensure side effects
      return qualifier;
    }
    else if(!m.isStatic()) {
      // load implicit this qualifier
      return emitThis(b, methodQualifierType());
    }
    throw new Error("createLoadQualifier not supported for " + m.getClass().getName());
  }
  /**
   * @ast method 
   * @aspect StaticImportsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/StaticImportsCodegen.jrag:18
   */
    protected TypeDecl methodQualifierType() {
    TypeDecl typeDecl = refined_GenericsCodegen_MethodAccess_methodQualifierType();
    if(typeDecl != null)
      return typeDecl;
    return decl().hostType();
  }
  /**
   * @ast method 
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:284
   */
  private TypeDecl refined_TypeAnalysis_MethodAccess_type()
{ return decl().type(); }
  protected java.util.Map computeDAbefore_int_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:410
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean computeDAbefore(int i, Variable v) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(Integer.valueOf(i));
    _parameters.add(v);
    if(computeDAbefore_int_Variable_values == null) computeDAbefore_int_Variable_values = new java.util.HashMap(4);
    if(computeDAbefore_int_Variable_values.containsKey(_parameters)) {
      return ((Boolean)computeDAbefore_int_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean computeDAbefore_int_Variable_value = computeDAbefore_compute(i, v);
      if(isFinal && num == state().boundariesCrossed) computeDAbefore_int_Variable_values.put(_parameters, Boolean.valueOf(computeDAbefore_int_Variable_value));
    return computeDAbefore_int_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean computeDAbefore_compute(int i, Variable v) {  return i == 0 ? isDAbefore(v) : getArg(i-1).isDAafter(v);  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:235
   */
  public boolean isDAafter(Variable v) {
    ASTNode$State state = state();
    try {  return getNumArg() == 0 ? isDAbefore(v) : getArg(getNumArg()-1).isDAafter(v);  }
    finally {
    }
  }
  /*eq Stmt.isDAafter(Variable v) {
    //System.out.println("### isDAafter reached in " + getClass().getName());
    //throw new NullPointerException();
    throw new Error("Can not compute isDAafter for " + getClass().getName() + " at " + errorPrefix());
  }* @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:332
   */
  public boolean isDAafterTrue(Variable v) {
    ASTNode$State state = state();
    try {  return (getNumArg() == 0 ? isDAbefore(v) : getArg(getNumArg()-1).isDAafter(v)) || isFalse();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:333
   */
  public boolean isDAafterFalse(Variable v) {
    ASTNode$State state = state();
    try {  return (getNumArg() == 0 ? isDAbefore(v) : getArg(getNumArg()-1).isDAafter(v)) || isTrue();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean exceptionCollection_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection exceptionCollection_value;
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:65
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection exceptionCollection() {
    if(exceptionCollection_computed) {
      return exceptionCollection_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    exceptionCollection_value = exceptionCollection_compute();
      if(isFinal && num == state().boundariesCrossed) exceptionCollection_computed = true;
    return exceptionCollection_value;
  }
  /**
   * @apilevel internal
   */
  private Collection exceptionCollection_compute() {
    //System.out.println("Computing exceptionCollection for " + name());
    HashSet set = new HashSet();
    Iterator iter = decls().iterator();
    if(!iter.hasNext())
      return set;

    MethodDecl m = (MethodDecl)iter.next();
    //System.out.println("Processing first found method " + m.signature() + " in " + m.hostType().fullName());

    for(int i = 0; i < m.getNumException(); i++) {
      TypeDecl exceptionType = m.getException(i).type();
      set.add(exceptionType);
    }
    while(iter.hasNext()) {
      HashSet first = new HashSet();
      first.addAll(set);
      HashSet second = new HashSet();
      m = (MethodDecl)iter.next();
      //System.out.println("Processing the next method " + m.signature() + " in " + m.hostType().fullName());
      for(int i = 0; i < m.getNumException(); i++) {
        TypeDecl exceptionType = m.getException(i).type();
        second.add(exceptionType);
      }
      set = new HashSet();
      for(Iterator i1 = first.iterator(); i1.hasNext(); ) {
        TypeDecl firstType = (TypeDecl)i1.next(); 
        for(Iterator i2 = second.iterator(); i2.hasNext(); ) {
          TypeDecl secondType = (TypeDecl)i2.next();
          if(firstType.instanceOf(secondType)) {
            set.add(firstType);
          }
          else if(secondType.instanceOf(firstType)) {
            set.add(secondType);
          }
        }
      }
    }
    return set;
  }
  /**
   * @attribute syn
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:66
   */
  public MethodDecl singleCandidateDecl() {
    ASTNode$State state = state();
    try {
    MethodDecl result = null;
    for(Iterator iter = lookupMethod(name()).iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(result == null)
        result = m;
      else if(m.getNumParameter() == getNumArg() && result.getNumParameter() != getNumArg())
        result = m;
    }
    return result;
  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean decls_computed = false;
  /**
   * @apilevel internal
   */
  protected SimpleSet decls_value;
  /**
   * @attribute syn
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:96
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet decls() {
    if(decls_computed) {
      return decls_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    decls_value = decls_compute();
      if(isFinal && num == state().boundariesCrossed) decls_computed = true;
    return decls_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet decls_compute() {
    SimpleSet maxSpecific = maxSpecific(lookupMethod(name()));
    if(isQualified() ? qualifier().staticContextQualifier() : inStaticContext())
      maxSpecific = removeInstanceMethods(maxSpecific);
    return maxSpecific;
  }
  /**
   * @apilevel internal
   */
  protected boolean decl_computed = false;
  /**
   * @apilevel internal
   */
  protected MethodDecl decl_value;
  /**
   * @attribute syn
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:103
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl decl() {
    if(decl_computed) {
      return decl_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    decl_value = decl_compute();
      if(isFinal && num == state().boundariesCrossed) decl_computed = true;
    return decl_value;
  }
  /**
   * @apilevel internal
   */
  private MethodDecl decl_compute() {
    SimpleSet decls = decls();
    if(decls.size() == 1)
      return (MethodDecl)decls.iterator().next();

    // 8.4.6.4 - only return the first method in case of multply inherited abstract methods
    boolean allAbstract = true;
    for(Iterator iter = decls.iterator(); iter.hasNext() && allAbstract; ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(!m.isAbstract() && !m.hostType().isObject())
        allAbstract = false;
    }
    if(decls.size() > 1 && allAbstract)
      return (MethodDecl)decls.iterator().next();
    return unknownMethod();
  }
  /**
   * @attribute syn
   * @aspect MethodDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:170
   */
  public boolean accessible(MethodDecl m) {
    ASTNode$State state = state();
    try {
    if(!isQualified())
      return true;
    if(!m.accessibleFrom(hostType()))
      return false;
    // the method is not accessible if the type is not accessible
    if(!qualifier().type().accessibleFrom(hostType()))
      return false;
    // 6.6.2.1 -  include qualifier type for protected access
    if(m.isProtected() && !m.hostPackage().equals(hostPackage())
        && !m.isStatic() && !qualifier().isSuperAccess()) {
      return hostType().mayAccess(this, m);
    }
    return true;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:65
   */
  public boolean validArgs() {
    ASTNode$State state = state();
    try {
    for(int i = 0; i < getNumArg(); i++)
      if(getArg(i).type().isUnknown())
        return false;
    return true;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:800
   */
  public String dumpString() {
    ASTNode$State state = state();
    try {  return getClass().getName() + " [" + getID() + "]";  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Names
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:18
   */
  public String name() {
    ASTNode$State state = state();
    try {  return getID();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect AccessTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:17
   */
  public boolean isMethodAccess() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect SyntacticClassification
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:56
   */
  public NameType predNameType() {
    ASTNode$State state = state();
    try {  return NameType.AMBIGUOUS_NAME;  }
    finally {
    }
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
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:32
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
    if(getNumArg() == 0 && name().equals("getClass") && decl().hostType().isObject()) {
      TypeDecl bound = isQualified() ? qualifier().type() : hostType();
      ArrayList args = new ArrayList();
      args.add(bound.erasure().asWildcardExtends());
      return ((GenericClassDecl)lookupType("java.lang", "Class")).lookupParTypeDecl(args);
    }
    else
      return refined_TypeAnalysis_MethodAccess_type();
  }
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:181
   */
  public boolean applicableBySubtyping(MethodDecl m) {
    ASTNode$State state = state();
    try {
    if(m.getNumParameter() != getNumArg())
      return false;
    for(int i = 0; i < m.getNumParameter(); i++)
      if(!getArg(i).type().instanceOf(m.getParameter(i).type()))
        return false;
    return true;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:201
   */
  public boolean applicableByMethodInvocationConversion(MethodDecl m) {
    ASTNode$State state = state();
    try {
    if(m.getNumParameter() != getNumArg())
      return false;
    for(int i = 0; i < m.getNumParameter(); i++)
      if(!getArg(i).type().methodInvocationConversionTo(m.getParameter(i).type()))
        return false;
    return true;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:221
   */
  public boolean applicableVariableArity(MethodDecl m) {
    ASTNode$State state = state();
    try {
    for(int i = 0; i < m.getNumParameter() - 1; i++)
      if(!getArg(i).type().methodInvocationConversionTo(m.getParameter(i).type()))
        return false;
    for(int i = m.getNumParameter() - 1; i < getNumArg(); i++)
      if(!getArg(i).type().methodInvocationConversionTo(m.lastParameter().type().componentType()))
        return false;
    return true;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:262
   */
  public boolean potentiallyApplicable(MethodDecl m) {
    ASTNode$State state = state();
    try {
    if(!m.name().equals(name()))
      return false;
    if(!m.accessibleFrom(hostType()))
      return false;
    if(m.isVariableArity() && !(arity() >= m.arity()-1))
      return false;
    if(!m.isVariableArity() && !(m.arity() == arity()))
      return false;
    if(m instanceof GenericMethodDecl) {
      GenericMethodDecl gm = (GenericMethodDecl)m;
      ArrayList list = typeArguments(m);
      if(list.size() != 0) {
        if(gm.getNumTypeParameter() != list.size())
          return false;
        for(int i = 0; i < gm.getNumTypeParameter(); i++)
          if(!((TypeDecl)list.get(i)).subtype(gm.original().getTypeParameter(i)))
            return false;
      }
    }
    return true;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:285
   */
  public int arity() {
    ASTNode$State state = state();
    try {  return getNumArg();  }
    finally {
    }
  }
  protected java.util.Map typeArguments_MethodDecl_values;
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:287
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayList typeArguments(MethodDecl m) {
    Object _parameters = m;
    if(typeArguments_MethodDecl_values == null) typeArguments_MethodDecl_values = new java.util.HashMap(4);
    if(typeArguments_MethodDecl_values.containsKey(_parameters)) {
      return (ArrayList)typeArguments_MethodDecl_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    ArrayList typeArguments_MethodDecl_value = typeArguments_compute(m);
      if(isFinal && num == state().boundariesCrossed) typeArguments_MethodDecl_values.put(_parameters, typeArguments_MethodDecl_value);
    return typeArguments_MethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private ArrayList typeArguments_compute(MethodDecl m) {
    ArrayList typeArguments = new ArrayList();
    if(m instanceof GenericMethodDecl) {
      GenericMethodDecl g = (GenericMethodDecl)m;
      Collection arguments = computeConstraints(g);
      if(arguments.isEmpty())
        return typeArguments;
      int i = 0;
      for(Iterator iter = arguments.iterator(); iter.hasNext(); i++) {
        TypeDecl typeDecl = (TypeDecl)iter.next();
        if(typeDecl == null) {
          TypeVariable v = g.original().getTypeParameter(i);
          if(v.getNumTypeBound() == 0)
            typeDecl = typeObject();
          else if(v.getNumTypeBound() == 1)
            typeDecl = v.getTypeBound(0).type();
          else
            typeDecl = v.lubType();
        }
        typeArguments.add(typeDecl);
      }
    }
    return typeArguments;
  }
  /**
   * @attribute syn
   * @aspect VariableArityParameters
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:40
   */
  public boolean invokesVariableArityAsArray() {
    ASTNode$State state = state();
    try {
    if(!decl().isVariableArity())
      return false;
    if(arity() != decl().arity())
      return false;
    return getArg(getNumArg()-1).type().methodInvocationConversionTo(decl().lastParameter().type());
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:385
   */
  public boolean requiresAccessor() {
    ASTNode$State state = state();
    try {
    MethodDecl m = decl();
    if(m.isPrivate() && m.hostType() != hostType())
      return true;
    if(m.isProtected() && !m.hostPackage().equals(hostPackage()) && !hostType().hasMethod(m.name()))
      return true;
    return false;
  }
    finally {
    }
  }
  /**
   * @attribute inh
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:43
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean handlesException(TypeDecl exceptionType) {
    ASTNode$State state = state();
    boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
    return handlesException_TypeDecl_value;
  }
  /**
   * @attribute inh
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:15
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl unknownMethod() {
    ASTNode$State state = state();
    MethodDecl unknownMethod_value = getParent().Define_MethodDecl_unknownMethod(this, null);
    return unknownMethod_value;
  }
  /**
   * @attribute inh
   * @aspect TypeHierarchyCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:123
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean inExplicitConstructorInvocation() {
    ASTNode$State state = state();
    boolean inExplicitConstructorInvocation_value = getParent().Define_boolean_inExplicitConstructorInvocation(this, null);
    return inExplicitConstructorInvocation_value;
  }
  /**
   * @attribute inh
   * @aspect GenericMethodsInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:43
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeObject() {
    ASTNode$State state = state();
    TypeDecl typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
    return typeObject_value;
  }
  /**
   * @attribute inh
   * @aspect SuppressWarnings
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SuppressWarnings.jrag:18
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean withinSuppressWarnings(String s) {
    ASTNode$State state = state();
    boolean withinSuppressWarnings_String_value = getParent().Define_boolean_withinSuppressWarnings(this, null, s);
    return withinSuppressWarnings_String_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:409
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getArgListNoTransform())  {
    int i = caller.getIndexOfChild(child);
    return computeDAbefore(i, v);
  }
    else {      return getParent().Define_boolean_isDAbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:28
   * @apilevel internal
   */
  public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
    if(caller == getArgListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return unqualifiedScope().lookupMethod(name);
  }
    else {      return getParent().Define_Collection_lookupMethod(this, caller, name);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:87
   * @apilevel internal
   */
  public boolean Define_boolean_hasPackage(ASTNode caller, ASTNode child, String packageName) {
    if(caller == getArgListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return unqualifiedScope().hasPackage(packageName);
  }
    else {      return getParent().Define_boolean_hasPackage(this, caller, packageName);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:253
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getArgListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return unqualifiedScope().lookupType(name);
  }
    else {      return getParent().Define_SimpleSet_lookupType(this, caller, name);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:132
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getArgListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return unqualifiedScope().lookupVariable(name);
  }
    else {      return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:120
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getArgListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return NameType.EXPRESSION_NAME;
  }
    else {      return getParent().Define_NameType_nameType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:17
   * @apilevel internal
   */
  public String Define_String_methodHost(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return unqualifiedScope().methodHost();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:41
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
    if(caller == getArgListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return typeObject();
  }
    else {      return getParent().Define_TypeDecl_assignConvertedType(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
