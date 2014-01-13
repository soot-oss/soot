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
 * @production TypeAccess : {@link Access} ::= <span class="component">&lt;Package:String&gt;</span> <span class="component">&lt;ID:String&gt;</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:23
 */
public class TypeAccess extends Access implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    decls_computed = false;
    decls_value = null;
    decl_computed = false;
    decl_value = null;
    type_computed = false;
    type_value = null;
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
  public TypeAccess clone() throws CloneNotSupportedException {
    TypeAccess node = (TypeAccess)super.clone();
    node.decls_computed = false;
    node.decls_value = null;
    node.decl_computed = false;
    node.decl_value = null;
    node.type_computed = false;
    node.type_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeAccess copy() {
    try {
      TypeAccess node = (TypeAccess) clone();
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
  public TypeAccess fullCopy() {
    TypeAccess tree = (TypeAccess) copy();
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
   * @aspect AccessControl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:128
   */
  public void accessControl() {
    super.accessControl();
    TypeDecl hostType = hostType();
    if(hostType != null && !hostType.isUnknown() && !type().accessibleFrom(hostType)) {
      error("" + this + " in " + hostType().fullName() + " can not access type " + type().fullName());
    }
    else if((hostType == null || hostType.isUnknown())  && !type().accessibleFromPackage(hostPackage())) {
      error("" + this + " can not access type " + type().fullName());
    }
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:160
   */
  public void nameCheck() {
    if(isQualified() && !qualifier().isTypeAccess() && !qualifier().isPackageAccess())
      error("can not access the type named " + decl().typeName() + " in this context");
    if(decls().isEmpty())
      error("no visible type named " + typeName());
    if(decls().size() > 1) {
      StringBuffer s = new StringBuffer();
      s.append("several types named " + name() + ":");
      for(Iterator iter = decls().iterator(); iter.hasNext(); ) {
        TypeDecl t = (TypeDecl)iter.next();
        s.append(" " + t.typeName());
      }
      error(s.toString());
    }
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:23
   */
  public TypeAccess(String name, int start, int end) {
    this(name);
    this.start = this.IDstart = start;
    this.end = this.IDend = end;
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:44
   */
  public TypeAccess(String typeName) {
    this("", typeName);
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:483
   */
  public void toString(StringBuffer s) {
    if(decl().isReferenceType())
      s.append(nameWithPackage());
    else
      s.append(decl().name());
  }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:328
   */
  public void checkModifiers() {
    if(decl().isDeprecated() &&
       !withinDeprecatedAnnotation() &&
       (hostType() == null || hostType().topLevelType() != decl().topLevelType()) &&
       !withinSuppressWarnings("deprecation"))
      warning(decl().typeName() + " has been deprecated");
  }
  /**
   * @ast method 
   * @aspect GenericsTypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:298
   */
  public boolean isRaw() {
    /*
    if(hasNextAccess())
      return false;
    */
    ASTNode parent = getParent();
    while(parent instanceof AbstractDot)
      parent = parent.getParent();
    if(parent instanceof ParTypeAccess)
      return false;
    if(parent instanceof ImportDecl)
      return false;
    /*
    Access a = this;
    while(a.isTypeAccess() && hasNextAccess())
      a = a.nextAccess();
    if(a.isThisAccess() || a.isSuperAccess())
      return false;
    */
    return true;
  }
  /**
   * @ast method 
   * @aspect GenericsTypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:438
   */
  public void typeCheck() {
    TypeDecl type = type();
    if(type.isRawType() && type.isNestedType() && type.enclosingType().isParameterizedType() && !type.enclosingType().isRawType())
      error("Can not access a member type of a paramterized type as a raw type");
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:44
   */
  public void transformation() {
    super.transformation();
    if(type().elementType().isNestedType() && hostType() != null)
      hostType().addUsedNestedType(type().elementType());
  }
  /**
   * @ast method 
   * 
   */
  public TypeAccess() {
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
  }
  /**
   * @ast method 
   * 
   */
  public TypeAccess(String p0, String p1) {
    setPackage(p0);
    setID(p1);
  }
  /**
   * @ast method 
   * 
   */
  public TypeAccess(beaver.Symbol p0, beaver.Symbol p1) {
    setPackage(p0);
    setID(p1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 0;
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
   * Replaces the lexeme Package.
   * @param value The new value for the lexeme Package.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setPackage(String value) {
    tokenString_Package = value;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  protected String tokenString_Package;
  /**
   * @ast method 
   * 
   */
  
  public int Packagestart;
  /**
   * @ast method 
   * 
   */
  
  public int Packageend;
  /**
   * JastAdd-internal setter for lexeme Package using the Beaver parser.
   * @apilevel internal
   * @ast method 
   * 
   */
  public void setPackage(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setPackage is only valid for String lexemes");
    tokenString_Package = (String)symbol.value;
    Packagestart = symbol.getStart();
    Packageend = symbol.getEnd();
  }
  /**
   * Retrieves the value for the lexeme Package.
   * @return The value for the lexeme Package.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public String getPackage() {
    return tokenString_Package != null ? tokenString_Package : "";
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
   * @ast method 
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:238
   */
  private TypeDecl refined_TypeScopePropagation_TypeAccess_decl()
{
    SimpleSet decls = decls();
    if(decls.size() == 1) {
      return (TypeDecl)decls.iterator().next();
    }
    return unknownType();
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
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:223
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
    if(packageName().equals(""))
      return lookupType(name());
    else {
      TypeDecl typeDecl = lookupType(packageName(), name());
      if(typeDecl != null)
        return SimpleSet.emptySet.add(typeDecl);
      return SimpleSet.emptySet;
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean decl_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl decl_value;
  /**
   * @attribute syn
   * @aspect GenericsTypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:290
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl decl() {
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
  private TypeDecl decl_compute() {
    TypeDecl decl = refined_TypeScopePropagation_TypeAccess_decl();
    if(decl instanceof GenericTypeDecl && isRaw())
      return ((GenericTypeDecl)decl).lookupParTypeDecl(new ArrayList());
    return decl;
  }
  /**
   * @attribute syn
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:148
   */
  public SimpleSet qualifiedLookupVariable(String name) {
    ASTNode$State state = state();
    try {
    if(type().accessibleFrom(hostType())) {
      SimpleSet c = type().memberFields(name);
      c = keepAccessibleFields(c);
      if(type().isClassDecl() && c.size() == 1)
        c = removeInstanceVariables(c);
      return c;
    }
    return SimpleSet.emptySet;
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
    try {  return getClass().getName() + " [" + getPackage() + ", " + getID() + "]";  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Names
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:21
   */
  public String name() {
    ASTNode$State state = state();
    try {  return getID();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Names
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:25
   */
  public String packageName() {
    ASTNode$State state = state();
    try {  return getPackage();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Names
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:49
   */
  public String nameWithPackage() {
    ASTNode$State state = state();
    try {  return getPackage().equals("") ? name() : (getPackage() + "." + name());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Names
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:62
   */
  public String typeName() {
    ASTNode$State state = state();
    try {  return isQualified() ? (qualifier().typeName() + "." + name()) : nameWithPackage();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect AccessTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:13
   */
  public boolean isTypeAccess() {
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
    try {  return NameType.PACKAGE_OR_TYPE_NAME;  }
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
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:279
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
  private TypeDecl type_compute() {  return decl();  }
  /**
   * @attribute syn
   * @aspect TypeHierarchyCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:150
   */
  public boolean staticContextQualifier() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1056
   */
  public boolean usesTypeVariable() {
    ASTNode$State state = state();
    try {  return decl().usesTypeVariable() || super.usesTypeVariable();  }
    finally {
    }
  }
  /**
	 * Builds a copy of this Access node where all occurrences
	 * of type variables in the original type parameter list have been replaced
	 * by the substitution type parameters.
	 *
	 * @return the substituted Access node
	 * @attribute syn
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:406
   */
  public Access substituted(Collection<TypeVariable> original, List<TypeVariable> substitution) {
    ASTNode$State state = state();
    try {
		TypeDecl decl = decl();
		int i = 0;
		for (TypeVariable typeVar : original) {
			if (typeVar == decl)
				return new TypeAccess(substitution.getChild(i).getID());
			i += 1;
		}
		return super.substituted(original, substitution);
	}
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
