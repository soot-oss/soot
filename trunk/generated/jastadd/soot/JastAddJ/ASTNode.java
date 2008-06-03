
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;

// Generated with JastAdd II (http://jastadd.cs.lth.se) version R20080527

public class ASTNode<T extends ASTNode> extends beaver.Symbol  implements Cloneable, Iterable<T> {
    public void flushCache() {
    }
     @SuppressWarnings({"unchecked", "cast"})  public ASTNode<T> clone() throws CloneNotSupportedException {
        ASTNode node = (ASTNode)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ASTNode<T> copy() {
      try {
          ASTNode node = (ASTNode)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ASTNode<T> fullCopy() {
        ASTNode res = (ASTNode)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in AccessControl.jrag at line 125

    
  public void accessControl() {
  }

    // Declared in AnonymousClasses.jrag at line 136


  protected void collectExceptions(Collection c, ASTNode target) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).collectExceptions(c, target);
  }

    // Declared in BranchTarget.jrag at line 45

  
  public void collectBranches(Collection c) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).collectBranches(c);
  }

    // Declared in BranchTarget.jrag at line 151

  public Stmt branchTarget(Stmt branchStmt) {
    if(getParent() != null)
      return getParent().branchTarget(branchStmt);
    else
      return null;
  }

    // Declared in BranchTarget.jrag at line 191

  public void collectFinally(Stmt branchStmt, ArrayList list) {
    if(getParent() != null)
      getParent().collectFinally(branchStmt, list);
  }

    // Declared in DeclareBeforeUse.jrag at line 13

  public int varChildIndex(Block b) {
    ASTNode node = this;
    while(node.getParent().getParent() != b) {
      node = node.getParent();
    }
    return b.getStmtListNoTransform().getIndexOfChild(node);
  }

    // Declared in DeclareBeforeUse.jrag at line 31


  public int varChildIndex(TypeDecl t) {
    ASTNode node = this;
    while(node != null && node.getParent() != null && node.getParent().getParent() != t) {
      node = node.getParent();
    }
    if(node == null)
      return -1;
    return t.getBodyDeclListNoTransform().getIndexOfChild(node);
  }

    // Declared in DefiniteAssignment.jrag at line 12


  public void definiteAssignment() {
  }

    // Declared in DefiniteAssignment.jrag at line 451


  // 16.2.2 9th, 10th bullet
  protected boolean checkDUeverywhere(Variable v) {
    for(int i = 0; i < getNumChild(); i++)
      if(!getChild(i).checkDUeverywhere(v))
        return false;
    return true;
  }

    // Declared in DefiniteAssignment.jrag at line 561


  protected boolean isDescendantTo(ASTNode node) {
    if(this == node)
      return true;
    if(getParent() == null)
      return false;
    return getParent().isDescendantTo(node);
  }

    // Declared in ErrorCheck.jrag at line 12


  protected String sourceFile() {
    ASTNode node = this;
    while(node != null && !(node instanceof CompilationUnit))
      node = node.getParent();
    if(node == null)
      return "Unknown file";
    CompilationUnit u = (CompilationUnit)node;
    return u.relativeName();
  }

    // Declared in ErrorCheck.jrag at line 34


  // set start and end position to the same as the argument and return self
  public ASTNode setLocation(ASTNode node) {
    setStart(node.getStart());
    setEnd(node.getEnd());
    return this;
  }

    // Declared in ErrorCheck.jrag at line 40


  public ASTNode setStart(int i) {
    start = i;
    return this;
  }

    // Declared in ErrorCheck.jrag at line 44

  public int start() {
    return start;
  }

    // Declared in ErrorCheck.jrag at line 47

  public ASTNode setEnd(int i) {
    end = i;
    return this;
  }

    // Declared in ErrorCheck.jrag at line 51

  public int end() {
    return end;
  }

    // Declared in ErrorCheck.jrag at line 57




  public String location() {
    return "" + lineNumber();
  }

    // Declared in ErrorCheck.jrag at line 60

  public String errorPrefix() {
    return sourceFile() + ":" + location() + ":\n" + "  *** Semantic Error: ";
  }

    // Declared in ErrorCheck.jrag at line 63

  public String warningPrefix() {
    return sourceFile() + ":" + location() + ":\n" + "  *** WARNING: ";
  }

    // Declared in ErrorCheck.jrag at line 173


  protected void error(String s) {
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

    // Declared in ErrorCheck.jrag at line 189


  protected void warning(String s) {
    ASTNode node = this;
    while(node != null && !(node instanceof CompilationUnit))
      node = node.getParent();
    CompilationUnit cu = (CompilationUnit)node;
    cu.warnings.add(new Problem(sourceFile(), "WARNING: " + s, lineNumber(), Problem.Severity.WARNING));
  }

    // Declared in ErrorCheck.jrag at line 197

  
  public void collectErrors() {
    nameCheck();
    typeCheck();
    accessControl();
    exceptionHandling();
    checkUnreachableStmt();
    definiteAssignment();
    checkModifiers();
    for(int i = 0; i < getNumChild(); i++) {
      getChild(i).collectErrors();
    }
  }

    // Declared in ExceptionHandling.jrag at line 40

  
  public void exceptionHandling() {
  }

    // Declared in ExceptionHandling.jrag at line 196


  protected boolean reachedException(TypeDecl type) {
    for(int i = 0; i < getNumChild(); i++)
      if(getChild(i).reachedException(type))
        return true;
    return false;
  }

    // Declared in LookupMethod.jrag at line 54

  public static Collection removeInstanceMethods(Collection c) {
    c = new LinkedList(c);
    for(Iterator iter = c.iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(!m.isStatic())
        iter.remove();
    }
    return c;
  }

    // Declared in LookupMethod.jrag at line 342

  protected static void putSimpleSetElement(HashMap map, Object key, Object value) {
    SimpleSet set = (SimpleSet)map.get(key);
    if(set == null) set = SimpleSet.emptySet;
    map.put(key, set.add(value));
  }

    // Declared in LookupVariable.jrag at line 177


  public SimpleSet removeInstanceVariables(SimpleSet oldSet) {
    SimpleSet newSet = SimpleSet.emptySet;
    for(Iterator iter = oldSet.iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      if(!v.isInstanceVariable())
        newSet = newSet.add(v);
    }
    return newSet;
  }

    // Declared in Modifiers.jrag at line 11

  void checkModifiers() {
  }

    // Declared in NameCheck.jrag at line 11

  public void nameCheck() {
  }

    // Declared in NameCheck.jrag at line 14


  public TypeDecl extractSingleType(SimpleSet c) {
    if(c.size() != 1)
      return null;
    return (TypeDecl)c.iterator().next();
  }

    // Declared in PrettyPrint.jadd at line 13

  // Helper for indentation
  
  protected static int indent = 0;

    // Declared in PrettyPrint.jadd at line 15

  
  public static String indent() {
    StringBuffer s = new StringBuffer();
    for(int i = 0; i < indent; i++) {
      s.append("  ");
    }
    return s.toString();
  }

    // Declared in PrettyPrint.jadd at line 25


  // Default output
  
  public String toString() {
    StringBuffer s = new StringBuffer();
    toString(s);
    return s.toString().trim();
  }

    // Declared in PrettyPrint.jadd at line 31

  
  public void toString(StringBuffer s) {
    throw new Error("Operation toString(StringBuffer s) not implemented for " + getClass().getName());
  }

    // Declared in PrettyPrint.jadd at line 749


  // dump the AST to standard output

  public String dumpTree() {
    StringBuffer s = new StringBuffer();
    dumpTree(s, 0);
    return s.toString();
  }

    // Declared in PrettyPrint.jadd at line 755


  public void dumpTree(StringBuffer s, int j) {
    for(int i = 0; i < j; i++) {
      s.append("  ");
    }
    s.append(dumpString() + "\n");
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).dumpTree(s, j + 1);
  }

    // Declared in PrettyPrint.jadd at line 764


  public String dumpTreeNoRewrite() {
    StringBuffer s = new StringBuffer();
    dumpTreeNoRewrite(s, 0);
    return s.toString();
  }

    // Declared in PrettyPrint.jadd at line 769

  protected void dumpTreeNoRewrite(StringBuffer s, int indent) {
    for(int i = 0; i < indent; i++)
      s.append("  ");
    s.append(dumpString());
    s.append("\n");
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      getChildNoTransform(i).dumpTreeNoRewrite(s, indent+1);
    }
  }

    // Declared in PrimitiveTypes.jrag at line 11

  protected static final String PRIMITIVE_PACKAGE_NAME = "@primitive";

    // Declared in TypeCheck.jrag at line 12

  public void typeCheck() {
  }

    // Declared in UnreachableStatements.jrag at line 12


  void checkUnreachableStmt() {
  }

    // Declared in Enums.jrag at line 128


  protected void transformEnumConstructors() {
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode child = getChildNoTransform(i);
      if(child != null)
        child.transformEnumConstructors();
    }
  }

    // Declared in Enums.jrag at line 411

  
  /*
    14) It is a compile-time error to reference a static field of an enum type that
    is not a compile-time constant (\ufffd15.28) from constructors, instance
    initializer blocks, or instance variable initializer expressions of that
    type.
  */

  protected void checkEnum(EnumDecl enumDecl) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).checkEnum(enumDecl);
  }

    // Declared in InnerClasses.jrag at line 155


  public void collectEnclosingVariables(HashSet set, TypeDecl typeDecl) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).collectEnclosingVariables(set, typeDecl);
  }

    // Declared in Java2Rewrites.jrag at line 63

  
  public void flushCaches() {
    flushCache();
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).flushCaches();
  }

    // Declared in Transformations.jrag at line 12

  // generic traversal of the tree
  public void transformation() {
    for(int i = 0; i < getNumChild(); i++) {
        getChild(i).transformation();
    }
  }

    // Declared in Transformations.jrag at line 209

  
  // imperative transformation of the AST
  // syntax ASTNode.replace(sourcenode).with(destnode)
  // this syntax is used to allow for building the destnode using the sourcenode
  protected static ASTNode replace(ASTNode node) {
    replacePos = node.getParent().getIndexOfChild(node);
    node.getParent().in$Circle(true);
    return node.getParent();
  }

    // Declared in Transformations.jrag at line 214

  protected ASTNode with(ASTNode node) {
   ((ASTNode)this).setChild(node, replacePos);
   in$Circle(false);
   return node;
  }

    // Declared in Transformations.jrag at line 219

  private static int replacePos = 0;

    // Declared in EmitJimple.jrag at line 59


  public void jimplify1phase1() {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).jimplify1phase1();
  }

    // Declared in EmitJimple.jrag at line 138

  
  public void jimplify1phase2() {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).jimplify1phase2();
  }

    // Declared in EmitJimple.jrag at line 357

  public void jimplify2() {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).jimplify2();
  }

    // Declared in EmitJimple.jrag at line 362


  public void jimplify2(Body b) {
    for(int i = 0; i < getNumChild(); i++)
      getChild(i).jimplify2(b);
  }

    // Declared in EmitJimple.jrag at line 395



  public soot.Immediate asImmediate(Body b, soot.Value v) {
    if(v instanceof soot.Immediate) return (soot.Immediate)v;
    return b.newTemp(v);
  }

    // Declared in EmitJimple.jrag at line 399

  public soot.Local asLocal(Body b, soot.Value v) {
    if(v instanceof soot.Local) return (soot.Local)v;
    return b.newTemp(v);
  }

    // Declared in EmitJimple.jrag at line 403

  public soot.Local asLocal(Body b, soot.Value v, Type t) {
    if(v instanceof soot.Local) return (soot.Local)v;
    soot.Local local = b.newTemp(t);
    b.add(Jimple.v().newAssignStmt(local, v));
    return local;
  }

    // Declared in EmitJimple.jrag at line 409

  public soot.Value asRValue(Body b, soot.Value v) {
    if(v instanceof soot.Local) return v;
    if(v instanceof soot.jimple.Constant) return v;
    if(v instanceof soot.jimple.ConcreteRef) return v;
    if(v instanceof soot.jimple.Expr) return v;
    throw new Error("Need to convert " + v.getClass().getName() + " to RValue");
  }

    // Declared in EmitJimple.jrag at line 561


  protected soot.jimple.Stmt newLabel() {
    return soot.jimple.Jimple.v().newNopStmt();
  }

    // Declared in EmitJimple.jrag at line 640


  public void addAttributes() {
  }

    // Declared in Expressions.jrag at line 698


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

    // Declared in Statements.jrag at line 255


  public void endExceptionRange(Body b, ArrayList list) {
    if(list != null) {
      soot.jimple.Stmt label = newLabel();
      b.addLabel(label);
      list.add(label);
      //list.add(b.previousStmt());
    }
  }

    // Declared in Statements.jrag at line 263

  public void beginExceptionRange(Body b, ArrayList list) {
    if(list != null)
      b.addNextStmt(list);
  }

    // Declared in EmitJimpleRefinements.jrag at line 196


  public void collectTypesToHierarchy(Collection<Type> set) {
	 for(int i = 0; i < getNumChild(); i++)
	  getChild(i).collectTypesToHierarchy(set);
  }

    // Declared in EmitJimpleRefinements.jrag at line 214

	
  public void collectTypesToSignatures(Collection<Type> set) {
	 for(int i = 0; i < getNumChild(); i++)
	  getChild(i).collectTypesToSignatures(set);
  }

    // Declared in ASTNode.ast at line 3
    // Declared in ASTNode.ast line 0

    public ASTNode() {
        super();


    }

    // Declared in ASTNode.ast at line 9


   static public boolean generatedWithCircularEnabled = true;

    // Declared in ASTNode.ast at line 10

   static public boolean generatedWithCacheCycle = false;

    // Declared in ASTNode.ast at line 11

   static public boolean generatedWithComponentCheck = false;

    // Declared in ASTNode.ast at line 12

  static public boolean IN_CIRCLE = false;

    // Declared in ASTNode.ast at line 13

  static public int CIRCLE_INDEX;

    // Declared in ASTNode.ast at line 14

  static public boolean CHANGE = false;

    // Declared in ASTNode.ast at line 15

  static public boolean RESET_CYCLE = false;

    // Declared in ASTNode.ast at line 16

  public static int boundariesCrossed = 0;

    // Declared in ASTNode.ast at line 43

  protected static ASTNode$State state = new ASTNode$State();

    // Declared in ASTNode.ast at line 44

  public boolean in$Circle = false;

    // Declared in ASTNode.ast at line 45

  public boolean in$Circle() { return in$Circle; }

    // Declared in ASTNode.ast at line 46

  public void in$Circle(boolean b) { in$Circle = b; }

    // Declared in ASTNode.ast at line 47

  public boolean is$Final = false;

    // Declared in ASTNode.ast at line 48

  public boolean is$Final() { return is$Final; }

    // Declared in ASTNode.ast at line 49

  public void is$Final(boolean b) { is$Final = b; }

    // Declared in ASTNode.ast at line 50

  protected static final int REWRITE_CHANGE = 1;

    // Declared in ASTNode.ast at line 51

  protected static final int REWRITE_NOCHANGE = 2;

    // Declared in ASTNode.ast at line 52

  protected static final int REWRITE_INTERRUPT = 3;

    // Declared in ASTNode.ast at line 53

  @SuppressWarnings("cast") public T getChild(int i) {
    return (T)ASTNode.getChild(this, i);
  }

    // Declared in ASTNode.ast at line 56

  public static ASTNode getChild(ASTNode that, int i) {
    ASTNode node = that.getChildNoTransform(i);
    if(node.is$Final()) return node;
    if(!node.mayHaveRewrite()) {
      node.is$Final(that.is$Final());
      return node;
    }
    if(!node.in$Circle()) {
      int rewriteState;
      int num = ASTNode.boundariesCrossed;
      do {
        ASTNode.state.push(ASTNode.REWRITE_CHANGE);
        ASTNode oldNode = node;
        oldNode.in$Circle(true);
        node = node.rewriteTo();
        if(node != oldNode)
          that.setChild(node, i);
        oldNode.in$Circle(false);
        rewriteState = state.pop();
      } while(rewriteState == ASTNode.REWRITE_CHANGE);
      if(rewriteState == ASTNode.REWRITE_NOCHANGE && that.is$Final()) {
        node.is$Final(true);
        ASTNode.boundariesCrossed = num;
      }
    }
    else if(that.is$Final() != node.is$Final()) boundariesCrossed++;
    return node;
  }

    // Declared in ASTNode.ast at line 84

  private int childIndex;

    // Declared in ASTNode.ast at line 85

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

    // Declared in ASTNode.ast at line 96


  public void addChild(T node) {
    setChild(node, getNumChildNoTransform());
  }

    // Declared in ASTNode.ast at line 99

  @SuppressWarnings("cast") public final T getChildNoTransform(int i) {
    return (T)children[i];
  }

    // Declared in ASTNode.ast at line 102

  protected ASTNode parent;

    // Declared in ASTNode.ast at line 103

  protected ASTNode[] children;

    // Declared in ASTNode.ast at line 104

  protected int numChildren;

    // Declared in ASTNode.ast at line 105

  protected int numChildren() {
    return numChildren;
  }

    // Declared in ASTNode.ast at line 108

  public int getNumChild() {
    return numChildren();
  }

    // Declared in ASTNode.ast at line 111

  public final int getNumChildNoTransform() {
    return numChildren();
  }

    // Declared in ASTNode.ast at line 114

  public void setChild(T node, int i) {
    if(children == null) {
      children = new ASTNode[i + 1];
    } else if (i >= children.length) {
      ASTNode c[] = new ASTNode[i << 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = node;
    if(i >= numChildren) numChildren = i+1;
    if(node != null) { node.setParent(this); node.childIndex = i; }
  }

    // Declared in ASTNode.ast at line 126

  public void insertChild(T node, int i) {
    if(children == null) {
      children = new ASTNode[i + 1];
      children[i] = node;
    } else {
      ASTNode c[] = new ASTNode[children.length + 1];
      System.arraycopy(children, 0, c, 0, i);
      c[i] = node;
      if(i < children.length)
        System.arraycopy(children, i, c, i+1, children.length-i);
      children = c;
    }
    numChildren++;
    if(node != null) { node.setParent(this); node.childIndex = i; }
  }

    // Declared in ASTNode.ast at line 141

  public void removeChild(int i) {
    if(children != null) {
      ASTNode child = children[i];
      if(child != null) {
        child.setParent(null);
        child.childIndex = -1;
      }
      System.arraycopy(children, i+1, children, i, children.length-i-1);
      numChildren--;
    }
  }

    // Declared in ASTNode.ast at line 152

  public ASTNode getParent() {
    if(parent != null && parent.is$Final() != is$Final()) {
      boundariesCrossed++;
    }
    return parent;
  }

    // Declared in ASTNode.ast at line 158

  public void setParent(ASTNode node) {
    parent = node;
  }

    // Declared in ASTNode.ast at line 161

    protected static int duringDefiniteAssignment = 0;

    // Declared in ASTNode.ast at line 162

    protected static boolean duringDefiniteAssignment() {
        if(duringDefiniteAssignment == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 172

    protected static int duringGenericTypeVariables = 0;

    // Declared in ASTNode.ast at line 173

    protected static boolean duringGenericTypeVariables() {
        if(duringGenericTypeVariables == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 183

    protected static int duringVariableDeclaration = 0;

    // Declared in ASTNode.ast at line 184

    protected static boolean duringVariableDeclaration() {
        if(duringVariableDeclaration == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 194

    protected static int duringBoundNames = 0;

    // Declared in ASTNode.ast at line 195

    protected static boolean duringBoundNames() {
        if(duringBoundNames == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 205

    protected static int duringEnums = 0;

    // Declared in ASTNode.ast at line 206

    protected static boolean duringEnums() {
        if(duringEnums == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 216

    protected static int duringLookupConstructor = 0;

    // Declared in ASTNode.ast at line 217

    protected static boolean duringLookupConstructor() {
        if(duringLookupConstructor == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 227

    protected static int duringConstantExpression = 0;

    // Declared in ASTNode.ast at line 228

    protected static boolean duringConstantExpression() {
        if(duringConstantExpression == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 238

    protected static int duringAnonymousClasses = 0;

    // Declared in ASTNode.ast at line 239

    protected static boolean duringAnonymousClasses() {
        if(duringAnonymousClasses == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 249

    protected static int duringAnnotations = 0;

    // Declared in ASTNode.ast at line 250

    protected static boolean duringAnnotations() {
        if(duringAnnotations == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 260

    protected static int duringSyntacticClassification = 0;

    // Declared in ASTNode.ast at line 261

    protected static boolean duringSyntacticClassification() {
        if(duringSyntacticClassification == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 271

    protected static int duringResolveAmbiguousNames = 0;

    // Declared in ASTNode.ast at line 272

    protected static boolean duringResolveAmbiguousNames() {
        if(duringResolveAmbiguousNames == 0) {
            return false;
        }
        else {
            state.pop();
            state.push(ASTNode.REWRITE_INTERRUPT);
            return true;
        }
    }

    // Declared in ASTNode.ast at line 282

    public static void reset() {
        IN_CIRCLE = false;
        CIRCLE_INDEX = 0;
        CHANGE = false;
        boundariesCrossed = 0;
        state = new ASTNode$State();
        if(duringDefiniteAssignment != 0) {
            System.out.println("Warning: resetting duringDefiniteAssignment");
            duringDefiniteAssignment = 0;
        }
        if(duringGenericTypeVariables != 0) {
            System.out.println("Warning: resetting duringGenericTypeVariables");
            duringGenericTypeVariables = 0;
        }
        if(duringVariableDeclaration != 0) {
            System.out.println("Warning: resetting duringVariableDeclaration");
            duringVariableDeclaration = 0;
        }
        if(duringBoundNames != 0) {
            System.out.println("Warning: resetting duringBoundNames");
            duringBoundNames = 0;
        }
        if(duringEnums != 0) {
            System.out.println("Warning: resetting duringEnums");
            duringEnums = 0;
        }
        if(duringLookupConstructor != 0) {
            System.out.println("Warning: resetting duringLookupConstructor");
            duringLookupConstructor = 0;
        }
        if(duringConstantExpression != 0) {
            System.out.println("Warning: resetting duringConstantExpression");
            duringConstantExpression = 0;
        }
        if(duringAnonymousClasses != 0) {
            System.out.println("Warning: resetting duringAnonymousClasses");
            duringAnonymousClasses = 0;
        }
        if(duringAnnotations != 0) {
            System.out.println("Warning: resetting duringAnnotations");
            duringAnnotations = 0;
        }
        if(duringSyntacticClassification != 0) {
            System.out.println("Warning: resetting duringSyntacticClassification");
            duringSyntacticClassification = 0;
        }
        if(duringResolveAmbiguousNames != 0) {
            System.out.println("Warning: resetting duringResolveAmbiguousNames");
            duringResolveAmbiguousNames = 0;
        }
    }

    // Declared in ASTNode.ast at line 333

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

    // Declared in ASTNode.ast at line 350

  public boolean mayHaveRewrite() { return false; }

    // Declared in DefiniteAssignment.jrag at line 1200
 @SuppressWarnings({"unchecked", "cast"})     public boolean unassignedEverywhere(Variable v, TryStmt stmt) {
        boolean unassignedEverywhere_Variable_TryStmt_value = unassignedEverywhere_compute(v, stmt);
        return unassignedEverywhere_Variable_TryStmt_value;
    }

    private boolean unassignedEverywhere_compute(Variable v, TryStmt stmt) {
    for(int i = 0; i < getNumChild(); i++) {
      if(!getChild(i).unassignedEverywhere(v, stmt))
        return false;
    }
    return true;
  }

    // Declared in ErrorCheck.jrag at line 22
 @SuppressWarnings({"unchecked", "cast"})     public int lineNumber() {
        int lineNumber_value = lineNumber_compute();
        return lineNumber_value;
    }

    private int lineNumber_compute() {
    ASTNode n = this;
    while(n.getParent() != null && n.getStart() == 0) {
      n = n.getParent();
    }
    return getLine(n.getStart());
  }

    // Declared in PrettyPrint.jadd at line 779
 @SuppressWarnings({"unchecked", "cast"})     public String dumpString() {
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return getClass().getName();  }

    // Declared in Generics.jrag at line 897
 @SuppressWarnings({"unchecked", "cast"})     public boolean usesTypeVariable() {
        boolean usesTypeVariable_value = usesTypeVariable_compute();
        return usesTypeVariable_value;
    }

    private boolean usesTypeVariable_compute() {
    for(int i = 0; i < getNumChild(); i++)
      if(getChild(i).usesTypeVariable())
        return true;
    return false;
  }

    // Declared in InnerClasses.jrag at line 85
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStringAdd() {
        boolean isStringAdd_value = isStringAdd_compute();
        return isStringAdd_value;
    }

    private boolean isStringAdd_compute() {  return false;  }

    // Declared in BooleanExpressions.jrag at line 21
 @SuppressWarnings({"unchecked", "cast"})     public boolean definesLabel() {
        boolean definesLabel_value = definesLabel_compute();
        return definesLabel_value;
    }

    private boolean definesLabel_compute() {  return false;  }

public ASTNode rewriteTo() {
    if(state.peek() == ASTNode.REWRITE_CHANGE) {
        state.pop();
        state.push(ASTNode.REWRITE_NOCHANGE);
    }
    return this;
}

    public LUBType Define_LUBType_lookupLUBType(ASTNode caller, ASTNode child, Collection bounds) {
        return getParent().Define_LUBType_lookupLUBType(this, caller, bounds);
    }
    public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
        return getParent().Define_boolean_handlesException(this, caller, exceptionType);
    }
    public GenericMethodDecl Define_GenericMethodDecl_genericMethodDecl(ASTNode caller, ASTNode child) {
        return getParent().Define_GenericMethodDecl_genericMethodDecl(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeNullPointerException(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeNullPointerException(this, caller);
    }
    public Collection Define_Collection_lookupConstructor(ASTNode caller, ASTNode child) {
        return getParent().Define_Collection_lookupConstructor(this, caller);
    }
    public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBeFinal(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeThrowable(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeThrowable(this, caller);
    }
    public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_expectedType(this, caller);
    }
    public boolean Define_boolean_insideLoop(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_insideLoop(this, caller);
    }
    public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isMethodParameter(this, caller);
    }
    public LabeledStmt Define_LabeledStmt_lookupLabel(ASTNode caller, ASTNode child, String name) {
        return getParent().Define_LabeledStmt_lookupLabel(this, caller, name);
    }
    public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBePublic(this, caller);
    }
    public Collection Define_Collection_lookupSuperConstructor(ASTNode caller, ASTNode child) {
        return getParent().Define_Collection_lookupSuperConstructor(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeRuntimeException(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeRuntimeException(this, caller);
    }
    public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBeProtected(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeWildcard(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeWildcard(this, caller);
    }
    public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_variableArityValid(this, caller);
    }
    public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isMemberType(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeBoolean(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeBoolean(this, caller);
    }
    public TypeDecl Define_TypeDecl_componentType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_componentType(this, caller);
    }
    public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
    }
    public GLBType Define_GLBType_lookupGLBType(ASTNode caller, ASTNode child, ArrayList bounds) {
        return getParent().Define_GLBType_lookupGLBType(this, caller, bounds);
    }
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }
    public boolean Define_boolean_mayBeStrictfp(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBeStrictfp(this, caller);
    }
    public TypeDecl Define_TypeDecl_switchType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_switchType(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeObject(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeObject(this, caller);
    }
    public boolean Define_boolean_inExplicitConstructorInvocation(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_inExplicitConstructorInvocation(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeShort(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeShort(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeCloneable(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeCloneable(this, caller);
    }
    public boolean Define_boolean_isLocalClass(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isLocalClass(this, caller);
    }
    public boolean Define_boolean_isNestedType(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isNestedType(this, caller);
    }
    public String Define_String_methodHost(ASTNode caller, ASTNode child) {
        return getParent().Define_String_methodHost(this, caller);
    }
    public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isDest(this, caller);
    }
    public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isSource(this, caller);
    }
    public ArrayList Define_ArrayList_exceptionRanges(ASTNode caller, ASTNode child) {
        return getParent().Define_ArrayList_exceptionRanges(this, caller);
    }
    public ConstructorDecl Define_ConstructorDecl_unknownConstructor(ASTNode caller, ASTNode child) {
        return getParent().Define_ConstructorDecl_unknownConstructor(this, caller);
    }
    public ConstructorDecl Define_ConstructorDecl_constructorDecl(ASTNode caller, ASTNode child) {
        return getParent().Define_ConstructorDecl_constructorDecl(this, caller);
    }
    public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_declType(this, caller);
    }
    public GenericConstructorDecl Define_GenericConstructorDecl_genericConstructorDecl(ASTNode caller, ASTNode child) {
        return getParent().Define_GenericConstructorDecl_genericConstructorDecl(this, caller);
    }
    public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isConstructorParameter(this, caller);
    }
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
        return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeChar(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeChar(this, caller);
    }
    public TypeDecl Define_TypeDecl_lookupType(ASTNode caller, ASTNode child, String packageName, String typeName) {
        return getParent().Define_TypeDecl_lookupType(this, caller, packageName, typeName);
    }
    public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBeAbstract(this, caller);
    }
    public TypeDecl Define_TypeDecl_lookupWildcardExtends(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
        return getParent().Define_TypeDecl_lookupWildcardExtends(this, caller, typeDecl);
    }
    public boolean Define_boolean_isAnonymous(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isAnonymous(this, caller);
    }
    public SimpleSet Define_SimpleSet_allImportedTypes(ASTNode caller, ASTNode child, String name) {
        return getParent().Define_SimpleSet_allImportedTypes(this, caller, name);
    }
    public TypeDecl Define_TypeDecl_lookupWildcardSuper(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
        return getParent().Define_TypeDecl_lookupWildcardSuper(this, caller, typeDecl);
    }
    public ASTNode Define_ASTNode_enclosingBlock(ASTNode caller, ASTNode child) {
        return getParent().Define_ASTNode_enclosingBlock(this, caller);
    }
    public boolean Define_boolean_mayBeSynchronized(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBeSynchronized(this, caller);
    }
    public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_reportUnreachable(this, caller);
    }
    public TypeDecl Define_TypeDecl_enclosingInstance(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_enclosingInstance(this, caller);
    }
    public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
        return getParent().Define_VariableScope_outerScope(this, caller);
    }
    public CompilationUnit Define_CompilationUnit_compilationUnit(ASTNode caller, ASTNode child) {
        return getParent().Define_CompilationUnit_compilationUnit(this, caller);
    }
    public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBePrivate(this, caller);
    }
    public TypeDecl Define_TypeDecl_unknownType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_unknownType(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeSerializable(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeSerializable(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeLong(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeLong(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeError(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeError(this, caller);
    }
    public Annotation Define_Annotation_lookupAnnotation(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
        return getParent().Define_Annotation_lookupAnnotation(this, caller, typeDecl);
    }
    public boolean Define_boolean_mayBeTransient(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBeTransient(this, caller);
    }
    public String Define_String_packageName(ASTNode caller, ASTNode child) {
        return getParent().Define_String_packageName(this, caller);
    }
    public boolean Define_boolean_mayBeNative(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBeNative(this, caller);
    }
    public MethodDecl Define_MethodDecl_unknownMethod(ASTNode caller, ASTNode child) {
        return getParent().Define_MethodDecl_unknownMethod(this, caller);
    }
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        return getParent().Define_NameType_nameType(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeNull(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeNull(this, caller);
    }
    public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_inStaticContext(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeString(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeString(this, caller);
    }
    public boolean Define_boolean_withinDeprecatedAnnotation(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_withinDeprecatedAnnotation(this, caller);
    }
    public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_assignConvertedType(this, caller);
    }
    public Variable Define_Variable_unknownField(ASTNode caller, ASTNode child) {
        return getParent().Define_Variable_unknownField(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeInt(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeInt(this, caller);
    }
    public String Define_String_hostPackage(ASTNode caller, ASTNode child) {
        return getParent().Define_String_hostPackage(this, caller);
    }
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
        return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
    }
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_reachable(this, caller);
    }
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }
    public boolean Define_boolean_insideSwitch(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_insideSwitch(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeByte(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeByte(this, caller);
    }
    public TypeDecl Define_TypeDecl_genericDecl(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_genericDecl(this, caller);
    }
    public TypeDecl Define_TypeDecl_returnType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_returnType(this, caller);
    }
    public TypeDecl Define_TypeDecl_superType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_superType(this, caller);
    }
    public TypeDecl Define_TypeDecl_enclosingAnnotationDecl(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_enclosingAnnotationDecl(this, caller);
    }
    public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
        return getParent().Define_SimpleSet_lookupType(this, caller, name);
    }
    public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBeStatic(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeFloat(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeFloat(this, caller);
    }
    public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
        return getParent().Define_Collection_lookupMethod(this, caller, name);
    }
    public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_hostType(this, caller);
    }
    public boolean Define_boolean_reachableCatchClause(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_reachableCatchClause(this, caller);
    }
    public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
        return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
    }
    public Expr Define_Expr_nestedScope(ASTNode caller, ASTNode child) {
        return getParent().Define_Expr_nestedScope(this, caller);
    }
    public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
    }
    public boolean Define_boolean_hasPackage(ASTNode caller, ASTNode child, String packageName) {
        return getParent().Define_boolean_hasPackage(this, caller, packageName);
    }
    public boolean Define_boolean_withinSuppressWarnings(ASTNode caller, ASTNode child, String s) {
        return getParent().Define_boolean_withinSuppressWarnings(this, caller, s);
    }
    public TypeDecl Define_TypeDecl_typeVoid(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeVoid(this, caller);
    }
    public TypeDecl Define_TypeDecl_enclosingType(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_enclosingType(this, caller);
    }
    public Case Define_Case_bind(ASTNode caller, ASTNode child, Case c) {
        return getParent().Define_Case_bind(this, caller, c);
    }
    public TypeDecl Define_TypeDecl_typeException(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeException(this, caller);
    }
    public int Define_int_localNum(ASTNode caller, ASTNode child) {
        return getParent().Define_int_localNum(this, caller);
    }
    public boolean Define_boolean_mayBeVolatile(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_mayBeVolatile(this, caller);
    }
    public ElementValue Define_ElementValue_lookupElementTypeValue(ASTNode caller, ASTNode child, String name) {
        return getParent().Define_ElementValue_lookupElementTypeValue(this, caller, name);
    }
    public BodyDecl Define_BodyDecl_enclosingBodyDecl(ASTNode caller, ASTNode child) {
        return getParent().Define_BodyDecl_enclosingBodyDecl(this, caller);
    }
    public boolean Define_boolean_isIncOrDec(ASTNode caller, ASTNode child) {
        return getParent().Define_boolean_isIncOrDec(this, caller);
    }
    public TypeDecl Define_TypeDecl_typeDouble(ASTNode caller, ASTNode child) {
        return getParent().Define_TypeDecl_typeDouble(this, caller);
    }
}
