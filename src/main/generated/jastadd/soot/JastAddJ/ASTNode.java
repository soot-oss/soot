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
 * @production ASTNode;
 * @ast node
 * 
 */
public class ASTNode<T extends ASTNode> extends beaver.Symbol  implements Cloneable, Iterable<T> {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ASTNode<T> clone() throws CloneNotSupportedException {
    ASTNode node = (ASTNode)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ASTNode<T> copy() {
    try {
      ASTNode node = (ASTNode) clone();
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
  public ASTNode<T> fullCopy() {
    ASTNode tree = (ASTNode) copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:125
   */
  public void accessControl() {
  }
  /**
   * @ast method 
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:202
   */
  protected void collectExceptions(Collection c, ASTNode target) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).collectExceptions(c, target);
  }
  /**
   * @ast method 
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:44
   */
  public void collectBranches(Collection c) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).collectBranches(c);
  }
  /**
   * @ast method 
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:150
   */
  public Stmt branchTarget(Stmt branchStmt) {
    if(getParent() != null)
      return getParent().branchTarget(branchStmt);
    else
      return null;
  }
  /**
   * @ast method 
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:190
   */
  public void collectFinally(Stmt branchStmt, ArrayList list) {
    if(getParent() != null)
      getParent().collectFinally(branchStmt, list);
  }
  /**
   * @ast method 
   * @aspect DeclareBeforeUse
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DeclareBeforeUse.jrag:13
   */
  public int varChildIndex(Block b) {
    ASTNode node = this;
    while(node.getParent().getParent() != b) {
      node = node.getParent();
    }
    return b.getStmtListNoTransform().getIndexOfChild(node);
  }
  /**
   * @ast method 
   * @aspect DeclareBeforeUse
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DeclareBeforeUse.jrag:31
   */
  public int varChildIndex(TypeDecl t) {
    ASTNode node = this;
    while(node != null && node.getParent() != null && node.getParent().getParent() != t) {
      node = node.getParent();
    }
    if(node == null)
      return -1;
    return t.getBodyDeclListNoTransform().getIndexOfChild(node);
  }
  /**
   * @ast method 
   * @aspect DefiniteAssignment
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:12
   */
  public void definiteAssignment() {
  }
  /**
   * @ast method 
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:450
   */
  protected boolean checkDUeverywhere(Variable v) {
    for(int i = 0; i < getNumChild(); i++)
      if(!getChild(i).checkDUeverywhere(v))
        return false;
    return true;
  }
  /**
   * @ast method 
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:560
   */
  protected boolean isDescendantTo(ASTNode node) {
    if(this == node)
      return true;
    if(getParent() == null)
      return false;
    return getParent().isDescendantTo(node);
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:12
   */
  protected String sourceFile() {
    ASTNode node = this;
    while(node != null && !(node instanceof CompilationUnit))
      node = node.getParent();
    if(node == null)
      return "Unknown file";
    CompilationUnit u = (CompilationUnit)node;
    return u.relativeName();
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:34
   */
  public ASTNode setLocation(ASTNode node) {
    setStart(node.getStart());
    setEnd(node.getEnd());
    return this;
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:40
   */
  public ASTNode setStart(int i) {
    start = i;
    return this;
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:44
   */
  public int start() {
    return start;
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:47
   */
  public ASTNode setEnd(int i) {
    end = i;
    return this;
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:51
   */
  public int end() {
    return end;
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:55
   */
  public String location() {
    return "" + lineNumber();
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:58
   */
  public String errorPrefix() {
    return sourceFile() + ":" + location() + ":\n" + "  *** Semantic Error: ";
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:61
   */
  public String warningPrefix() {
    return sourceFile() + ":" + location() + ":\n" + "  *** WARNING: ";
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:171
   */
  public void error(String s) {
    ASTNode node = this;
    while(node != null && !(node instanceof CompilationUnit))
      node = node.getParent();
    CompilationUnit cu = (CompilationUnit)node;
    if(getNumChild() == 0 && getStart() != 0 && getEnd() != 0) {  
      int line = getLine(getStart());
      int column = getColumn(getStart());
      int endLine = getLine(getEnd());
      int endColumn = getColumn(getEnd());
      cu.errors.add(new Problem(sourceFile(), s, line, column, endLine, endColumn, Problem.Severity.ERROR, Problem.Kind.SEMANTIC));
    }
    else
      cu.errors.add(new Problem(sourceFile(), s, lineNumber(), Problem.Severity.ERROR, Problem.Kind.SEMANTIC));
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:187
   */
  public void warning(String s) {
    ASTNode node = this;
    while(node != null && !(node instanceof CompilationUnit))
      node = node.getParent();
    CompilationUnit cu = (CompilationUnit)node;
    cu.warnings.add(new Problem(sourceFile(), "WARNING: " + s, lineNumber(), Problem.Severity.WARNING));
  }
  /**
   * @ast method 
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:54
   */
  public void exceptionHandling() {
  }
  /**
   * @ast method 
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:224
   */
  protected boolean reachedException(TypeDecl type) {
    for(int i = 0; i < getNumChild(); i++)
      if(getChild(i).reachedException(type))
        return true;
    return false;
  }
  /**
   * @ast method 
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:54
   */
  public static Collection removeInstanceMethods(Collection c) {
    c = new LinkedList(c);
    for(Iterator iter = c.iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(!m.isStatic())
        iter.remove();
    }
    return c;
  }
  /**
   * @ast method 
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:359
   */
  protected static void putSimpleSetElement(HashMap map, Object key, Object value) {
    SimpleSet set = (SimpleSet)map.get(key);
    if(set == null) set = SimpleSet.emptySet;
    map.put(key, set.add(value));
  }
  /**
   * @ast method 
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:182
   */
  public SimpleSet removeInstanceVariables(SimpleSet oldSet) {
    SimpleSet newSet = SimpleSet.emptySet;
    for(Iterator iter = oldSet.iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      if(!v.isInstanceVariable())
        newSet = newSet.add(v);
    }
    return newSet;
  }
  /**
   * @ast method 
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:11
   */
  void checkModifiers() {
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:11
   */
  public void nameCheck() {
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:14
   */
  public TypeDecl extractSingleType(SimpleSet c) {
    if(c.size() != 1)
      return null;
    return (TypeDecl)c.iterator().next();
  }
  /**
   * @ast method 
   * @aspect AddOptionsToProgram
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Options.jadd:14
   */
  public Options options() {
    return state().options;
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:13
   */
  public String toString() {
    StringBuffer s = new StringBuffer();
    toString(s);
    return s.toString().trim();
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:19
   */
  public void toString(StringBuffer s) {
    throw new Error("Operation toString(StringBuffer s) not implemented for " + getClass().getName());
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:770
   */
  public String dumpTree() {
    StringBuffer s = new StringBuffer();
    dumpTree(s, 0);
    return s.toString();
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:776
   */
  public void dumpTree(StringBuffer s, int j) {
    for(int i = 0; i < j; i++) {
      s.append("  ");
    }
    s.append(dumpString() + "\n");
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).dumpTree(s, j + 1);
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:785
   */
  public String dumpTreeNoRewrite() {
    StringBuffer s = new StringBuffer();
    dumpTreeNoRewrite(s, 0);
    return s.toString();
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:790
   */
  protected void dumpTreeNoRewrite(StringBuffer s, int indent) {
    for(int i = 0; i < indent; i++)
      s.append("  ");
    s.append(dumpString());
    s.append("\n");
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      getChildNoTransform(i).dumpTreeNoRewrite(s, indent+1);
    }
  }
  /**
   * @ast method 
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:11
   */
  
  protected static final String PRIMITIVE_PACKAGE_NAME = "@primitive";
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:12
   */
  public void typeCheck() {
  }
  /**
   * @ast method 
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:12
   */
  void checkUnreachableStmt() {
  }
  /**
   * @ast method 
   * @aspect VariableDeclarationTransformation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:134
   */
  public void clearLocations() {
    setStart(0);
    setEnd(0);
    for(int i = 0; i < getNumChildNoTransform(); i++)
      getChildNoTransform(i).clearLocations();
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:131
   */
  protected void transformEnumConstructors() {
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode child = getChildNoTransform(i);
      if(child != null)
        child.transformEnumConstructors();
    }
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:444
   */
  protected void checkEnum(EnumDecl enumDecl) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).checkEnum(enumDecl);
  }
  /**
   * @ast method 
   * @aspect FlushCaches
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/FlushCaches.jrag:3
   */
  public void flushCaches() {
	    flushCache();
	    for(int i = 0; i < getNumChild(); i++)
	      getChild(i).flushCaches();
	  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:158
   */
  public void collectEnclosingVariables(HashSet set, TypeDecl typeDecl) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).collectEnclosingVariables(set, typeDecl);
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:12
   */
  public void transformation() {
    for(int i = 0; i < getNumChild(); i++) {
        getChild(i).transformation();
    }
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:209
   */
  protected ASTNode replace(ASTNode node) {
    state().replacePos = node.getParent().getIndexOfChild(node);
    node.getParent().in$Circle(true);
    return node.getParent();
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:214
   */
  protected ASTNode with(ASTNode node) {
   ((ASTNode)this).setChild(node, state().replacePos);
   in$Circle(false);
   return node;
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:60
   */
  public void jimplify1phase1() {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).jimplify1phase1();
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:137
   */
  public void jimplify1phase2() {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).jimplify1phase2();
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:366
   */
  public void jimplify2() {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).jimplify2();
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:371
   */
  public void jimplify2(Body b) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).jimplify2(b);
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:405
   */
  public soot.Immediate asImmediate(Body b, soot.Value v) {
    if(v instanceof soot.Immediate) return (soot.Immediate)v;
    return b.newTemp(v);
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:409
   */
  public soot.Local asLocal(Body b, soot.Value v) {
    if(v instanceof soot.Local) return (soot.Local)v;
    return b.newTemp(v);
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:413
   */
  public soot.Local asLocal(Body b, soot.Value v, Type t) {
    if(v instanceof soot.Local) return (soot.Local)v;
    soot.Local local = b.newTemp(t);
    b.add(b.newAssignStmt(local, v, null));
    b.copyLocation(v, local);
    return local;
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:420
   */
  public soot.Value asRValue(Body b, soot.Value v) {
    if(v instanceof soot.Local) return v;
    if(v instanceof soot.jimple.Constant) return v;
    if(v instanceof soot.jimple.ConcreteRef) return v;
    if(v instanceof soot.jimple.Expr) return v;
    throw new Error("Need to convert " + v.getClass().getName() + " to RValue");
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:879
   */
  protected soot.jimple.Stmt newLabel() {
    return soot.jimple.Jimple.v().newNopStmt();
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:959
   */
  public void addAttributes() {
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:718
   */
  public static soot.Value emitConstant(Constant constant) {
    if(constant instanceof Constant.ConstantInt)
      return IntType.emitConstant(constant.intValue());
    else if(constant instanceof Constant.ConstantLong)
      return soot.jimple.LongConstant.v(constant.longValue());
    else if(constant instanceof Constant.ConstantFloat)
      return soot.jimple.FloatConstant.v(constant.floatValue());
    else if(constant instanceof Constant.ConstantDouble)
      return soot.jimple.DoubleConstant.v(constant.doubleValue());
    else if(constant instanceof Constant.ConstantChar)
      return IntType.emitConstant(constant.intValue());
    else if(constant instanceof Constant.ConstantBoolean)
      return BooleanType.emitConstant(constant.booleanValue());
    else if(constant instanceof Constant.ConstantString)
      return soot.jimple.StringConstant.v(constant.stringValue());
    throw new Error("Unexpected constant");
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:256
   */
  public void endExceptionRange(Body b, ArrayList list) {
    if(list != null) {
      soot.jimple.Stmt label = newLabel();
      b.addLabel(label);
      list.add(label);
      //list.add(b.previousStmt());
    }
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:264
   */
  public void beginExceptionRange(Body b, ArrayList list) {
    if(list != null)
      b.addNextStmt(list);
  }
  /**
	 * Create a deep copy of this subtree.
	 * The copy is dangling, i.e. has no parent.
	 *
	 * @return a dangling copy of the subtree at this node
	 * @ast method 
   * @aspect JastAddExtensions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/JastAddExtensions.jadd:20
   */
  public ASTNode cloneSubtree() {
		try {
			ASTNode tree = (ASTNode) clone();
			tree.setParent(null);// make dangling
			if (children != null) {
				tree.children = new ASTNode[children.length];
				for (int i = 0; i < children.length; ++i) {
					if (children[i] == null) {
						tree.children[i] = null;
					} else {
						tree.children[i] = children[i].cloneSubtree();
						tree.children[i].setParent(tree);
					}
				}
			}
			return tree;
		} catch (CloneNotSupportedException e) {
			throw new Error("Error: clone not supported for " +
					getClass().getName());
		}
	}
  /**
   * @ast method 
   * @aspect UncheckedConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/UncheckedConversion.jrag:40
   */
  public void checkUncheckedConversion(TypeDecl source, TypeDecl dest) {
    if (source.isUncheckedConversionTo(dest))
      warning("unchecked conversion from raw type "+source.typeName()+
        " to generic type "+dest.typeName());
  }
  /**
	 * Checking of the SafeVarargs annotation is only needed for method
	 * declarations.
	 * @ast method 
   * @aspect Warnings
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Warnings.jadd:38
   */
  public void checkWarnings() {
	}
  /**
   * @ast method 
   * @aspect EmitJimpleRefinements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:197
   */
  public void collectTypesToHierarchy(Collection<Type> set) {
	 for(int i = 0; i < getNumChild(); i++)
	  getChild(i).collectTypesToHierarchy(set);
  }
  /**
   * @ast method 
   * @aspect EmitJimpleRefinements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:215
   */
  public void collectTypesToSignatures(Collection<Type> set) {
	 for(int i = 0; i < getNumChild(); i++)
	  getChild(i).collectTypesToSignatures(set);
  }
  /**
   * @ast method 
   * 
   */
  public ASTNode() {
    super();

    init$Children();

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
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  public static final boolean generatedWithCircularEnabled = true;
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  public static final boolean generatedWithCacheCycle = false;
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  public static final boolean generatedWithComponentCheck = false;
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  protected static ASTNode$State state = new ASTNode$State();
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public final ASTNode$State state() { return state; }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  public boolean in$Circle = false;
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean in$Circle() { return in$Circle; }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public void in$Circle(boolean b) { in$Circle = b; }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  public boolean is$Final = false;
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean is$Final() { return is$Final; }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public void is$Final(boolean b) { is$Final = b; }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings("cast") public T getChild(int i) {
    ASTNode node = this.getChildNoTransform(i);
    if(node == null) return null;
    if(node.is$Final()) {
      return (T) node;
    }
    if(!node.mayHaveRewrite()) {
      node.is$Final(this.is$Final());
      return (T) node;
    }
    if(!node.in$Circle()) {
      int rewriteState;
      int num = this.state().boundariesCrossed;
      do {
        this.state().push(ASTNode$State.REWRITE_CHANGE);
        ASTNode oldNode = node;
        oldNode.in$Circle(true);
        node = node.rewriteTo();
        if(node != oldNode) {
          this.setChild(node, i);
        }
        oldNode.in$Circle(false);
        rewriteState = this.state().pop();
      } while(rewriteState == ASTNode$State.REWRITE_CHANGE);
      if(rewriteState == ASTNode$State.REWRITE_NOCHANGE && this.is$Final()) {
        node.is$Final(true);
        this.state().boundariesCrossed = num;
      }
    }
    else if(this.is$Final() != node.is$Final()) this.state().boundariesCrossed++;
    return (T) node;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  private int childIndex;
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getIndexOfChild(ASTNode node) {
    if(node != null && node.childIndex < getNumChildNoTransform() && node == getChildNoTransform(node.childIndex))
      return node.childIndex;
    for(int i = 0; i < getNumChildNoTransform(); i++)
      if(getChildNoTransform(i) == node) {
        node.childIndex = i;
        return i;
      }
    return -1;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addChild(T node) {
    setChild(node, getNumChildNoTransform());
  }
  /**
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings("cast")
  public final T getChildNoTransform(int i) {
    return (T) (children != null ? children[i] : null);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  
  /**
   * @apilevel low-level
   */
  protected int numChildren;
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return numChildren;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumChild() {
    return numChildren();
  }
  /**
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @apilevel low-level
   * @ast method 
   * 
   */
  public final int getNumChildNoTransform() {
    return numChildren();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setChild(ASTNode node, int i) {
    if(children == null) {
      children = new ASTNode[i+1>4?i+1:4];
    } else if (i >= children.length) {
      ASTNode c[] = new ASTNode[i << 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = node;
    if(i >= numChildren) numChildren = i+1;
    if(node != null) { node.setParent(this); node.childIndex = i; }
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void insertChild(ASTNode node, int i) {
    if(children == null) {
      children = new ASTNode[i+1>4?i+1:4];
      children[i] = node;
    } else {
      ASTNode c[] = new ASTNode[children.length + 1];
      System.arraycopy(children, 0, c, 0, i);
      c[i] = node;
      if(i < children.length) {
        System.arraycopy(children, i, c, i+1, children.length-i);
        for(int j = i+1; j < c.length; ++j) {
          if(c[j] != null)
            c[j].childIndex = j;
        }
      }
      children = c;
    }
    numChildren++;
    if(node != null) { node.setParent(this); node.childIndex = i; }
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void removeChild(int i) {
    if(children != null) {
      ASTNode child = (ASTNode)children[i];
      if(child != null) {
        child.parent = null;
        child.childIndex = -1;
      }
      if (this instanceof List || this instanceof Opt) {
        System.arraycopy(children, i+1, children, i, children.length-i-1);
        children[children.length-1] = null;
        numChildren--;
        for(int j = i; j < numChildren; ++j) {
          if(children[j] != null) {
            child = (ASTNode) children[j];
            child.childIndex = j;
          }
        }
      } else {
        children[i] = null;
      }
    }
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public ASTNode getParent() {
    if(parent != null && ((ASTNode)parent).is$Final() != is$Final()) {
      state().boundariesCrossed++;
    }
    return (ASTNode)parent;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setParent(ASTNode node) {
    parent = node;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  
  /**
   * @apilevel low-level
   */
  protected ASTNode parent;
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  
  /**
   * @apilevel low-level
   */
  protected ASTNode[] children;
  /**
   * @ast method 
   * 
   */
  protected boolean duringImplicitConstructor() {
    if(state().duringImplicitConstructor == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringBoundNames() {
    if(state().duringBoundNames == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringNameResolution() {
    if(state().duringNameResolution == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringSyntacticClassification() {
    if(state().duringSyntacticClassification == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringAnonymousClasses() {
    if(state().duringAnonymousClasses == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringVariableDeclarationTransformation() {
    if(state().duringVariableDeclarationTransformation == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringLiterals() {
    if(state().duringLiterals == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringDU() {
    if(state().duringDU == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringAnnotations() {
    if(state().duringAnnotations == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringEnums() {
    if(state().duringEnums == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @ast method 
   * 
   */
  protected boolean duringGenericTypeVariables() {
    if(state().duringGenericTypeVariables == 0) {
      return false;
    }
    else {
      state().pop();
      state().push(ASTNode$State.REWRITE_INTERRUPT);
      return true;
    }
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public java.util.Iterator<T> iterator() {
    return new java.util.Iterator<T>() {
      private int counter = 0;
      public boolean hasNext() {
        return counter < getNumChild();
      }
      @SuppressWarnings("unchecked") public T next() {
        if(hasNext())
          return (T)getChild(counter++);
        else
          return null;
      }
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
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
	 * The collectErrors method is refined so that it calls
	 * the checkWarnings method on each ASTNode to report
	 * unchecked warnings.
	 * @ast method 
   * @aspect Warnings
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Warnings.jadd:20
   */
    public void collectErrors() {
		nameCheck();
		typeCheck();
		accessControl();
		exceptionHandling();
		checkUnreachableStmt();
		definiteAssignment();
		checkModifiers();
		checkWarnings();
		for(int i = 0; i < getNumChild(); i++) {
			getChild(i).collectErrors();
		}
	}
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1196
   */
  public boolean unassignedEverywhere(Variable v, TryStmt stmt) {
    ASTNode$State state = state();
    try {
    for(int i = 0; i < getNumChild(); i++) {
      if(!getChild(i).unassignedEverywhere(v, stmt))
        return false;
    }
    return true;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:22
   */
  public int lineNumber() {
    ASTNode$State state = state();
    try {
    ASTNode n = this;
    while(n.getParent() != null && n.getStart() == 0) {
      n = n.getParent();
    }
    return getLine(n.getStart());
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:744
   */
  public String indent() {
    ASTNode$State state = state();
    try {
    String indent = extractIndent();
    return indent.startsWith("\n") ? indent : ("\n" + indent);
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:749
   */
  public String extractIndent() {
    ASTNode$State state = state();
    try {
    if(getParent() == null)
      return "";
    String indent = getParent().extractIndent();
    if(getParent().addsIndentationLevel())
      indent += "  ";
    return indent;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:758
   */
  public boolean addsIndentationLevel() {
    ASTNode$State state = state();
    try {  return false;  }
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
    try {  return getClass().getName();  }
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
    try {
    for(int i = 0; i < getNumChild(); i++)
      if(getChild(i).usesTypeVariable())
        return true;
    return false;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:88
   */
  public boolean isStringAdd() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:21
   */
  public boolean definesLabel() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
	 * Fetches the immediately enclosing compilation unit.
	 * @attribute inh
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:451
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CompilationUnit compilationUnit() {
    ASTNode$State state = state();
    CompilationUnit compilationUnit_value = getParent().Define_CompilationUnit_compilationUnit(this, null);
    return compilationUnit_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    if(state().peek() == ASTNode$State.REWRITE_CHANGE) {
      state().pop();
      state().push(ASTNode$State.REWRITE_NOCHANGE);
    }
    return this;
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_superType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_superType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ConstructorDecl Define_ConstructorDecl_constructorDecl(ASTNode caller, ASTNode child) {
    return getParent().Define_ConstructorDecl_constructorDecl(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_componentType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_componentType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public LabeledStmt Define_LabeledStmt_lookupLabel(ASTNode caller, ASTNode child, String name) {
    return getParent().Define_LabeledStmt_lookupLabel(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isDest(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isSource(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isIncOrDec(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isIncOrDec(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeException(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeException(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeRuntimeException(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeRuntimeException(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeError(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeError(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeNullPointerException(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeNullPointerException(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeThrowable(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeThrowable(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    return getParent().Define_boolean_handlesException(this, caller, exceptionType);
  }
  /**
   * @apilevel internal
   */
  public Collection Define_Collection_lookupConstructor(ASTNode caller, ASTNode child) {
    return getParent().Define_Collection_lookupConstructor(this, caller);
  }
  /**
   * @apilevel internal
   */
  public Collection Define_Collection_lookupSuperConstructor(ASTNode caller, ASTNode child) {
    return getParent().Define_Collection_lookupSuperConstructor(this, caller);
  }
  /**
   * @apilevel internal
   */
  public Expr Define_Expr_nestedScope(ASTNode caller, ASTNode child) {
    return getParent().Define_Expr_nestedScope(this, caller);
  }
  /**
   * @apilevel internal
   */
  public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
    return getParent().Define_Collection_lookupMethod(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeObject(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeObject(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeCloneable(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeCloneable(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeSerializable(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeSerializable(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeBoolean(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeBoolean(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeByte(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeByte(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeShort(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeShort(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeChar(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeChar(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeInt(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeInt(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeLong(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeLong(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeFloat(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeFloat(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeDouble(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeDouble(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeString(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeString(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeVoid(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeVoid(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeNull(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeNull(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_unknownType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_unknownType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_hasPackage(ASTNode caller, ASTNode child, String packageName) {
    return getParent().Define_boolean_hasPackage(this, caller, packageName);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_lookupType(ASTNode caller, ASTNode child, String packageName, String typeName) {
    return getParent().Define_TypeDecl_lookupType(this, caller, packageName, typeName);
  }
  /**
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    return getParent().Define_SimpleSet_lookupType(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBePublic(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBeProtected(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBePrivate(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBeStatic(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBeFinal(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBeAbstract(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeVolatile(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBeVolatile(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeTransient(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBeTransient(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStrictfp(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBeStrictfp(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeSynchronized(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBeSynchronized(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeNative(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_mayBeNative(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode Define_ASTNode_enclosingBlock(ASTNode caller, ASTNode child) {
    return getParent().Define_ASTNode_enclosingBlock(this, caller);
  }
  /**
   * @apilevel internal
   */
  public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
    return getParent().Define_VariableScope_outerScope(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_insideLoop(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_insideLoop(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_insideSwitch(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_insideSwitch(this, caller);
  }
  /**
   * @apilevel internal
   */
  public Case Define_Case_bind(ASTNode caller, ASTNode child, Case c) {
    return getParent().Define_Case_bind(this, caller, c);
  }
  /**
   * @apilevel internal
   */
  public String Define_String_typeDeclIndent(ASTNode caller, ASTNode child) {
    return getParent().Define_String_typeDeclIndent(this, caller);
  }
  /**
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isAnonymous(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isAnonymous(this, caller);
  }
  /**
   * @apilevel internal
   */
  public Variable Define_Variable_unknownField(ASTNode caller, ASTNode child) {
    return getParent().Define_Variable_unknownField(this, caller);
  }
  /**
   * @apilevel internal
   */
  public MethodDecl Define_MethodDecl_unknownMethod(ASTNode caller, ASTNode child) {
    return getParent().Define_MethodDecl_unknownMethod(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ConstructorDecl Define_ConstructorDecl_unknownConstructor(ASTNode caller, ASTNode child) {
    return getParent().Define_ConstructorDecl_unknownConstructor(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_declType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public BodyDecl Define_BodyDecl_enclosingBodyDecl(ASTNode caller, ASTNode child) {
    return getParent().Define_BodyDecl_enclosingBodyDecl(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isMemberType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_hostType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_switchType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_switchType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_returnType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_returnType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingInstance(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_enclosingInstance(this, caller);
  }
  /**
   * @apilevel internal
   */
  public String Define_String_methodHost(ASTNode caller, ASTNode child) {
    return getParent().Define_String_methodHost(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_inExplicitConstructorInvocation(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_inExplicitConstructorInvocation(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_inStaticContext(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_reportUnreachable(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isMethodParameter(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isConstructorParameter(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
    return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public ElementValue Define_ElementValue_lookupElementTypeValue(ASTNode caller, ASTNode child, String name) {
    return getParent().Define_ElementValue_lookupElementTypeValue(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_withinSuppressWarnings(ASTNode caller, ASTNode child, String s) {
    return getParent().Define_boolean_withinSuppressWarnings(this, caller, s);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_withinDeprecatedAnnotation(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_withinDeprecatedAnnotation(this, caller);
  }
  /**
   * @apilevel internal
   */
  public Annotation Define_Annotation_lookupAnnotation(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
    return getParent().Define_Annotation_lookupAnnotation(this, caller, typeDecl);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingAnnotationDecl(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_enclosingAnnotationDecl(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_assignConvertedType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_inExtendsOrImplements(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_inExtendsOrImplements(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_typeWildcard(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_typeWildcard(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_lookupWildcardExtends(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
    return getParent().Define_TypeDecl_lookupWildcardExtends(this, caller, typeDecl);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_lookupWildcardSuper(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
    return getParent().Define_TypeDecl_lookupWildcardSuper(this, caller, typeDecl);
  }
  /**
   * @apilevel internal
   */
  public LUBType Define_LUBType_lookupLUBType(ASTNode caller, ASTNode child, Collection bounds) {
    return getParent().Define_LUBType_lookupLUBType(this, caller, bounds);
  }
  /**
   * @apilevel internal
   */
  public GLBType Define_GLBType_lookupGLBType(ASTNode caller, ASTNode child, ArrayList bounds) {
    return getParent().Define_GLBType_lookupGLBType(this, caller, bounds);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_genericDecl(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_genericDecl(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_variableArityValid(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_expectedType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
    return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
  }
  /**
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
    return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
  }
  /**
   * @apilevel internal
   */
  public int Define_int_localNum(ASTNode caller, ASTNode child) {
    return getParent().Define_int_localNum(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ArrayList Define_ArrayList_exceptionRanges(ASTNode caller, ASTNode child) {
    return getParent().Define_ArrayList_exceptionRanges(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isCatchParam(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isCatchParam(this, caller);
  }
  /**
   * @apilevel internal
   */
  public CatchClause Define_CatchClause_catchClause(ASTNode caller, ASTNode child) {
    return getParent().Define_CatchClause_catchClause(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_resourcePreviouslyDeclared(ASTNode caller, ASTNode child, String name) {
    return getParent().Define_boolean_resourcePreviouslyDeclared(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public ClassInstanceExpr Define_ClassInstanceExpr_getClassInstanceExpr(ASTNode caller, ASTNode child) {
    return getParent().Define_ClassInstanceExpr_getClassInstanceExpr(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isAnonymousDecl(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isAnonymousDecl(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isExplicitGenericConstructorAccess(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isExplicitGenericConstructorAccess(this, caller);
  }
  /**
   * @apilevel internal
   */
  public CompilationUnit Define_CompilationUnit_compilationUnit(ASTNode caller, ASTNode child) {
    return getParent().Define_CompilationUnit_compilationUnit(this, caller);
  }
  /**
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_allImportedTypes(ASTNode caller, ASTNode child, String name) {
    return getParent().Define_SimpleSet_allImportedTypes(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public String Define_String_packageName(ASTNode caller, ASTNode child) {
    return getParent().Define_String_packageName(this, caller);
  }
  /**
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingType(ASTNode caller, ASTNode child) {
    return getParent().Define_TypeDecl_enclosingType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isNestedType(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isNestedType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_isLocalClass(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_isLocalClass(this, caller);
  }
  /**
   * @apilevel internal
   */
  public String Define_String_hostPackage(ASTNode caller, ASTNode child) {
    return getParent().Define_String_hostPackage(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    return getParent().Define_boolean_reachable(this, caller);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_inhModifiedInScope(ASTNode caller, ASTNode child, Variable var) {
    return getParent().Define_boolean_inhModifiedInScope(this, caller, var);
  }
  /**
   * @apilevel internal
   */
  public boolean Define_boolean_reachableCatchClause(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    return getParent().Define_boolean_reachableCatchClause(this, caller, exceptionType);
  }
  /**
   * @apilevel internal
   */
  public Collection<TypeDecl> Define_Collection_TypeDecl__caughtExceptions(ASTNode caller, ASTNode child) {
    return getParent().Define_Collection_TypeDecl__caughtExceptions(this, caller);
  }
}
