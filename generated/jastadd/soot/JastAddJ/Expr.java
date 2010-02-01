
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public abstract class Expr extends ASTNode<ASTNode> implements Cloneable {
    public void flushCache() {
        super.flushCache();
        false_label_computed = false;
        false_label_value = null;
        true_label_computed = false;
        true_label_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public Expr clone() throws CloneNotSupportedException {
        Expr node = (Expr)super.clone();
        node.false_label_computed = false;
        node.false_label_value = null;
        node.true_label_computed = false;
        node.true_label_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in LookupType.jrag at line 373

    
  public SimpleSet keepAccessibleTypes(SimpleSet oldSet) {
    SimpleSet newSet = SimpleSet.emptySet;
    TypeDecl hostType = hostType();
    for(Iterator iter = oldSet.iterator(); iter.hasNext(); ) {
      TypeDecl t = (TypeDecl)iter.next();
      if((hostType != null && t.accessibleFrom(hostType)) || (hostType == null && t.accessibleFromPackage(hostPackage())))
        newSet = newSet.add(t);
    }
    return newSet;
  }

    // Declared in LookupVariable.jrag at line 164


  // remove fields that are not accessible when using this Expr as qualifier
  public SimpleSet keepAccessibleFields(SimpleSet oldSet) {
    SimpleSet newSet = SimpleSet.emptySet;
    for(Iterator iter = oldSet.iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      if(v instanceof FieldDeclaration) {
        FieldDeclaration f = (FieldDeclaration)v;
        if(mayAccess(f))
          newSet = newSet.add(f);
      }
    }
    return newSet;
  }

    // Declared in LookupVariable.jrag at line 187


  public boolean mayAccess(FieldDeclaration f) {
    if(f.isPublic()) 
      return true;
    else if(f.isProtected()) {
      if(f.hostPackage().equals(hostPackage()))
        return true;
      TypeDecl C = f.hostType();
      TypeDecl S = hostType().subclassWithinBody(C);
      TypeDecl Q = type();
      if(S == null)
        return false;
      if(f.isInstanceVariable() && !isSuperAccess())
        return Q.instanceOf(S);
      return true;
    }
    else if(f.isPrivate())
      return f.hostType().topLevelType() == hostType().topLevelType();
    else
      return f.hostPackage().equals(hostType().hostPackage());
  }

    // Declared in ResolveAmbiguousNames.jrag at line 106


  public Dot qualifiesAccess(Access access) {
    Dot dot = new Dot(this, access);
    return dot;
  }

    // Declared in MethodSignature.jrag at line 76


  protected SimpleSet chooseConstructor(Collection constructors, List argList) {
    SimpleSet potentiallyApplicable = SimpleSet.emptySet;
    // select potentially applicable constructors
    for(Iterator iter = constructors.iterator(); iter.hasNext(); ) {
      ConstructorDecl decl = (ConstructorDecl)iter.next();
      if(decl.potentiallyApplicable(argList) && decl.accessibleFrom(hostType()))
        potentiallyApplicable = potentiallyApplicable.add(decl);
    }
    // first phase
    SimpleSet maxSpecific = SimpleSet.emptySet;
    for(Iterator iter = potentiallyApplicable.iterator(); iter.hasNext(); ) {
      ConstructorDecl decl = (ConstructorDecl)iter.next();
      if(decl.applicableBySubtyping(argList))
        maxSpecific = mostSpecific(maxSpecific, decl);
    }

    // second phase
    if(maxSpecific.isEmpty()) {
      for(Iterator iter = potentiallyApplicable.iterator(); iter.hasNext(); ) {
        ConstructorDecl decl = (ConstructorDecl)iter.next();
        if(decl.applicableByMethodInvocationConversion(argList))
          maxSpecific = mostSpecific(maxSpecific, decl);
      }
    }

    // third phase
    if(maxSpecific.isEmpty()) {
      for(Iterator iter = potentiallyApplicable.iterator(); iter.hasNext(); ) {
        ConstructorDecl decl = (ConstructorDecl)iter.next();
        if(decl.isVariableArity() && decl.applicableVariableArity(argList))
          maxSpecific = mostSpecific(maxSpecific, decl);
      }
    }
    return maxSpecific;
  }

    // Declared in MethodSignature.jrag at line 113



  protected static SimpleSet mostSpecific(SimpleSet maxSpecific, ConstructorDecl decl) {
    if(maxSpecific.isEmpty())
      maxSpecific = maxSpecific.add(decl);
    else {
      if(decl.moreSpecificThan((ConstructorDecl)maxSpecific.iterator().next()))
        maxSpecific = SimpleSet.emptySet.add(decl);
      else if(!((ConstructorDecl)maxSpecific.iterator().next()).moreSpecificThan(decl))
        maxSpecific = maxSpecific.add(decl);
    }
    return maxSpecific;
  }

    // Declared in BooleanExpressions.jrag at line 105

  protected soot.Value emitBooleanCondition(Body b) {
    b.setLine(this);
    emitEvalBranch(b);
    soot.jimple.Stmt end_label = newLabel();
    b.addLabel(false_label());
    Local result = b.newTemp(soot.BooleanType.v());
    b.add(b.newAssignStmt(result, BooleanType.emitConstant(false), this));
    b.add(b.newGotoStmt(end_label, this));
    b.addLabel(true_label());
    b.add(b.newAssignStmt(result, BooleanType.emitConstant(true), this));
    b.addLabel(end_label);
    return result;
  }

    // Declared in BooleanExpressions.jrag at line 158

  
  public void refined_BooleanExpressions_Expr_emitEvalBranch(Body b) {
    b.setLine(this);
    if(isTrue())
      b.add(b.newGotoStmt(true_label(), this));
    else if(isFalse())
      b.add(b.newGotoStmt(false_label(), this));
    else {
      b.add(
        b.newIfStmt(
          b.newEqExpr(
            asImmediate(b, eval(b)),
            BooleanType.emitConstant(false),
            this
          ),
          false_label(),
          this
        )
      );
      b.add(b.newGotoStmt(true_label(), this));
    }
  }

    // Declared in Expressions.jrag at line 11

  public soot.Value eval(Body b) {
    throw new Error("Operation eval not supported for " + getClass().getName());
  }

    // Declared in Expressions.jrag at line 259

  public soot.Value emitStore(Body b, soot.Value lvalue, soot.Value rvalue, ASTNode location) {
    b.setLine(this);
    b.add(
      b.newAssignStmt(
        lvalue,
        asLocal(b, rvalue, lvalue.getType()),
        location
      )
    );
    return rvalue;
  }

    // Declared in EmitJimpleRefinements.jrag at line 202

  
  public void collectTypesToHierarchy(Collection<Type> set) {
	  super.collectTypesToHierarchy(set);
	  // collect all expr types that are reference types
	  // select the element type in case it is an array type
    addDependencyIfNeeded(set, type());
  }

    // Declared in EmitJimpleRefinements.jrag at line 209


  protected void addDependencyIfNeeded(Collection<Type> set, TypeDecl type) {
    type = type.elementType().erasure();
    if(type.isReferenceType() && !type.isUnknown())
      set.add(type.getSootType());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 97

    public Expr() {
        super();


    }

    // Declared in java.ast at line 9


  protected int numChildren() {
    return 0;
  }

    // Declared in java.ast at line 12

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in AutoBoxingCodegen.jrag at line 100


  // Generate unboxing code for conditions
  // 14.9 If, 14.12 While, 14.13 Do, 14.14 For
  // 
  // emitEvalBranch is used to emit the condition from these constructs
  // refine behavior to include unboxing of the value when needed
    public void emitEvalBranch(Body b) {
    if(type().isReferenceType()) {
      b.setLine(this);
      b.add(
        b.newIfStmt(
          b.newEqExpr(
            asImmediate(b, type().emitUnboxingOperation(b, eval(b), this)),
            BooleanType.emitConstant(false),
            this
          ),
          false_label(),
          this
        )
      );
      b.add(b.newGotoStmt(true_label(), this));

    }
    else
      refined_BooleanExpressions_Expr_emitEvalBranch(b);
  }

    // Declared in TypeAnalysis.jrag at line 276
 @SuppressWarnings({"unchecked", "cast"})     public abstract TypeDecl type();
    // Declared in ConstantExpression.jrag at line 98
 @SuppressWarnings({"unchecked", "cast"})     public Constant constant() {
        ASTNode$State state = state();
        Constant constant_value = constant_compute();
        return constant_value;
    }

    private Constant constant_compute() {
    throw new UnsupportedOperationException("ConstantExpression operation constant" +
      " not supported for type " + getClass().getName()); 
  }

    // Declared in ConstantExpression.jrag at line 241
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPositive() {
        ASTNode$State state = state();
        boolean isPositive_value = isPositive_compute();
        return isPositive_value;
    }

    private boolean isPositive_compute() {  return false;  }

    // Declared in ConstantExpression.jrag at line 454
 @SuppressWarnings({"unchecked", "cast"})     public boolean representableIn(TypeDecl t) {
        ASTNode$State state = state();
        boolean representableIn_TypeDecl_value = representableIn_compute(t);
        return representableIn_TypeDecl_value;
    }

    private boolean representableIn_compute(TypeDecl t) {	
  	if (!type().isByte() && !type().isChar() && !type().isShort() && !type().isInt()) {
  		return false;
  	}
  	if (t.isByte())
  		return constant().intValue() >= Byte.MIN_VALUE && constant().intValue() <= Byte.MAX_VALUE;
  	if (t.isChar())
  		return constant().intValue() >= Character.MIN_VALUE && constant().intValue() <= Character.MAX_VALUE;
  	if (t.isShort())
  		return constant().intValue() >= Short.MIN_VALUE && constant().intValue() <= Short.MAX_VALUE;
    if(t.isInt()) 
      return constant().intValue() >= Integer.MIN_VALUE && constant().intValue() <= Integer.MAX_VALUE;
	  return false;
  }

    // Declared in ConstantExpression.jrag at line 482
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstant() {
        ASTNode$State state = state();
        boolean isConstant_value = isConstant_compute();
        return isConstant_value;
    }

    private boolean isConstant_compute() {  return false;  }

    // Declared in ConstantExpression.jrag at line 511
 @SuppressWarnings({"unchecked", "cast"})     public boolean isTrue() {
        ASTNode$State state = state();
        boolean isTrue_value = isTrue_compute();
        return isTrue_value;
    }

    private boolean isTrue_compute() {  return isConstant() && type() instanceof BooleanType && constant().booleanValue();  }

    // Declared in ConstantExpression.jrag at line 512
 @SuppressWarnings({"unchecked", "cast"})     public boolean isFalse() {
        ASTNode$State state = state();
        boolean isFalse_value = isFalse_compute();
        return isFalse_value;
    }

    private boolean isFalse_compute() {  return isConstant() && type() instanceof BooleanType && !constant().booleanValue();  }

    // Declared in DefiniteAssignment.jrag at line 58
 @SuppressWarnings({"unchecked", "cast"})     public Variable varDecl() {
        ASTNode$State state = state();
        Variable varDecl_value = varDecl_compute();
        return varDecl_value;
    }

    private Variable varDecl_compute() {  return null;  }

    // Declared in DefiniteAssignment.jrag at line 340
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterFalse(Variable v) {
        ASTNode$State state = state();
        boolean isDAafterFalse_Variable_value = isDAafterFalse_compute(v);
        return isDAafterFalse_Variable_value;
    }

    private boolean isDAafterFalse_compute(Variable v) {  return isTrue() || isDAbefore(v);  }

    // Declared in DefiniteAssignment.jrag at line 342
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterTrue(Variable v) {
        ASTNode$State state = state();
        boolean isDAafterTrue_Variable_value = isDAafterTrue_compute(v);
        return isDAafterTrue_Variable_value;
    }

    private boolean isDAafterTrue_compute(Variable v) {  return isFalse() || isDAbefore(v);  }

    // Declared in DefiniteAssignment.jrag at line 345
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        ASTNode$State state = state();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {  return (isDAafterFalse(v) && isDAafterTrue(v)) || isDAbefore(v);  }

    // Declared in DefiniteAssignment.jrag at line 782
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterFalse(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterFalse_Variable_value = isDUafterFalse_compute(v);
        return isDUafterFalse_Variable_value;
    }

    private boolean isDUafterFalse_compute(Variable v) {
    if(isTrue())
      return true;
    return isDUbefore(v);
  }

    // Declared in DefiniteAssignment.jrag at line 788
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterTrue(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterTrue_Variable_value = isDUafterTrue_compute(v);
        return isDUafterTrue_Variable_value;
    }

    private boolean isDUafterTrue_compute(Variable v) {
    if(isFalse())
      return true;
    return isDUbefore(v);
  }

    // Declared in DefiniteAssignment.jrag at line 798
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        ASTNode$State state = state();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {  return (isDUafterFalse(v) && isDUafterTrue(v)) || isDUbefore(v);  }

    // Declared in LookupConstructor.jrag at line 32
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet mostSpecificConstructor(Collection constructors) {
        ASTNode$State state = state();
        SimpleSet mostSpecificConstructor_Collection_value = mostSpecificConstructor_compute(constructors);
        return mostSpecificConstructor_Collection_value;
    }

    private SimpleSet mostSpecificConstructor_compute(Collection constructors) {
    SimpleSet maxSpecific = SimpleSet.emptySet;
    for(Iterator iter = constructors.iterator(); iter.hasNext(); ) {
      ConstructorDecl decl = (ConstructorDecl)iter.next();
      if(applicableAndAccessible(decl)) {
        if(maxSpecific.isEmpty())
          maxSpecific = maxSpecific.add(decl);
        else {
          if(decl.moreSpecificThan((ConstructorDecl)maxSpecific.iterator().next()))
            maxSpecific = SimpleSet.emptySet.add(decl);
          else if(!((ConstructorDecl)maxSpecific.iterator().next()).moreSpecificThan(decl))
            maxSpecific = maxSpecific.add(decl);
        }
      }
    }
    return maxSpecific;
  }

    // Declared in LookupConstructor.jrag at line 50
 @SuppressWarnings({"unchecked", "cast"})     public boolean applicableAndAccessible(ConstructorDecl decl) {
        ASTNode$State state = state();
        boolean applicableAndAccessible_ConstructorDecl_value = applicableAndAccessible_compute(decl);
        return applicableAndAccessible_ConstructorDecl_value;
    }

    private boolean applicableAndAccessible_compute(ConstructorDecl decl) {  return false;  }

    // Declared in LookupType.jrag at line 83
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasQualifiedPackage(String packageName) {
        ASTNode$State state = state();
        boolean hasQualifiedPackage_String_value = hasQualifiedPackage_compute(packageName);
        return hasQualifiedPackage_String_value;
    }

    private boolean hasQualifiedPackage_compute(String packageName) {  return false;  }

    // Declared in LookupType.jrag at line 342
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet qualifiedLookupType(String name) {
        ASTNode$State state = state();
        SimpleSet qualifiedLookupType_String_value = qualifiedLookupType_compute(name);
        return qualifiedLookupType_String_value;
    }

    private SimpleSet qualifiedLookupType_compute(String name) {  return keepAccessibleTypes(type().memberTypes(name));  }

    // Declared in LookupVariable.jrag at line 146
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet qualifiedLookupVariable(String name) {
        ASTNode$State state = state();
        SimpleSet qualifiedLookupVariable_String_value = qualifiedLookupVariable_compute(name);
        return qualifiedLookupVariable_String_value;
    }

    private SimpleSet qualifiedLookupVariable_compute(String name) {
    if(type().accessibleFrom(hostType()))
      return keepAccessibleFields(type().memberFields(name));
    return SimpleSet.emptySet;
  }

    // Declared in QualifiedNames.jrag at line 25
 @SuppressWarnings({"unchecked", "cast"})     public String packageName() {
        ASTNode$State state = state();
        String packageName_value = packageName_compute();
        return packageName_value;
    }

    private String packageName_compute() {  return "";  }

    // Declared in QualifiedNames.jrag at line 62
 @SuppressWarnings({"unchecked", "cast"})     public String typeName() {
        ASTNode$State state = state();
        String typeName_value = typeName_compute();
        return typeName_value;
    }

    private String typeName_compute() {  return "";  }

    // Declared in ResolveAmbiguousNames.jrag at line 13
 @SuppressWarnings({"unchecked", "cast"})     public boolean isTypeAccess() {
        ASTNode$State state = state();
        boolean isTypeAccess_value = isTypeAccess_compute();
        return isTypeAccess_value;
    }

    private boolean isTypeAccess_compute() {  return false;  }

    // Declared in ResolveAmbiguousNames.jrag at line 17
 @SuppressWarnings({"unchecked", "cast"})     public boolean isMethodAccess() {
        ASTNode$State state = state();
        boolean isMethodAccess_value = isMethodAccess_compute();
        return isMethodAccess_value;
    }

    private boolean isMethodAccess_compute() {  return false;  }

    // Declared in ResolveAmbiguousNames.jrag at line 21
 @SuppressWarnings({"unchecked", "cast"})     public boolean isFieldAccess() {
        ASTNode$State state = state();
        boolean isFieldAccess_value = isFieldAccess_compute();
        return isFieldAccess_value;
    }

    private boolean isFieldAccess_compute() {  return false;  }

    // Declared in ResolveAmbiguousNames.jrag at line 25
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSuperAccess() {
        ASTNode$State state = state();
        boolean isSuperAccess_value = isSuperAccess_compute();
        return isSuperAccess_value;
    }

    private boolean isSuperAccess_compute() {  return false;  }

    // Declared in ResolveAmbiguousNames.jrag at line 31
 @SuppressWarnings({"unchecked", "cast"})     public boolean isThisAccess() {
        ASTNode$State state = state();
        boolean isThisAccess_value = isThisAccess_compute();
        return isThisAccess_value;
    }

    private boolean isThisAccess_compute() {  return false;  }

    // Declared in ResolveAmbiguousNames.jrag at line 37
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPackageAccess() {
        ASTNode$State state = state();
        boolean isPackageAccess_value = isPackageAccess_compute();
        return isPackageAccess_value;
    }

    private boolean isPackageAccess_compute() {  return false;  }

    // Declared in ResolveAmbiguousNames.jrag at line 41
 @SuppressWarnings({"unchecked", "cast"})     public boolean isArrayAccess() {
        ASTNode$State state = state();
        boolean isArrayAccess_value = isArrayAccess_compute();
        return isArrayAccess_value;
    }

    private boolean isArrayAccess_compute() {  return false;  }

    // Declared in ResolveAmbiguousNames.jrag at line 45
 @SuppressWarnings({"unchecked", "cast"})     public boolean isClassAccess() {
        ASTNode$State state = state();
        boolean isClassAccess_value = isClassAccess_compute();
        return isClassAccess_value;
    }

    private boolean isClassAccess_compute() {  return false;  }

    // Declared in ResolveAmbiguousNames.jrag at line 49
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSuperConstructorAccess() {
        ASTNode$State state = state();
        boolean isSuperConstructorAccess_value = isSuperConstructorAccess_compute();
        return isSuperConstructorAccess_value;
    }

    private boolean isSuperConstructorAccess_compute() {  return false;  }

    // Declared in ResolveAmbiguousNames.jrag at line 55
 @SuppressWarnings({"unchecked", "cast"})     public boolean isLeftChildOfDot() {
        ASTNode$State state = state();
        boolean isLeftChildOfDot_value = isLeftChildOfDot_compute();
        return isLeftChildOfDot_value;
    }

    private boolean isLeftChildOfDot_compute() {  return hasParentDot() && parentDot().getLeft() == this;  }

    // Declared in ResolveAmbiguousNames.jrag at line 56
 @SuppressWarnings({"unchecked", "cast"})     public boolean isRightChildOfDot() {
        ASTNode$State state = state();
        boolean isRightChildOfDot_value = isRightChildOfDot_compute();
        return isRightChildOfDot_value;
    }

    private boolean isRightChildOfDot_compute() {  return hasParentDot() && parentDot().getRight() == this;  }

    // Declared in ResolveAmbiguousNames.jrag at line 69
 @SuppressWarnings({"unchecked", "cast"})     public AbstractDot parentDot() {
        ASTNode$State state = state();
        AbstractDot parentDot_value = parentDot_compute();
        return parentDot_value;
    }

    private AbstractDot parentDot_compute() {  return getParent() instanceof AbstractDot ? (AbstractDot)getParent() : null;  }

    // Declared in ResolveAmbiguousNames.jrag at line 70
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasParentDot() {
        ASTNode$State state = state();
        boolean hasParentDot_value = hasParentDot_compute();
        return hasParentDot_value;
    }

    private boolean hasParentDot_compute() {  return parentDot() != null;  }

    // Declared in ResolveAmbiguousNames.jrag at line 72
 @SuppressWarnings({"unchecked", "cast"})     public Access nextAccess() {
        ASTNode$State state = state();
        Access nextAccess_value = nextAccess_compute();
        return nextAccess_value;
    }

    private Access nextAccess_compute() {  return parentDot().nextAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 73
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasNextAccess() {
        ASTNode$State state = state();
        boolean hasNextAccess_value = hasNextAccess_compute();
        return hasNextAccess_value;
    }

    private boolean hasNextAccess_compute() {  return isLeftChildOfDot();  }

    // Declared in TypeAnalysis.jrag at line 504
 @SuppressWarnings({"unchecked", "cast"})     public Stmt enclosingStmt() {
        ASTNode$State state = state();
        Stmt enclosingStmt_value = enclosingStmt_compute();
        return enclosingStmt_value;
    }

    private Stmt enclosingStmt_compute() {
    ASTNode node = this;
    while(node != null && !(node instanceof Stmt))
      node = node.getParent();
    return (Stmt)node;
  }

    // Declared in TypeCheck.jrag at line 15
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVariable() {
        ASTNode$State state = state();
        boolean isVariable_value = isVariable_compute();
        return isVariable_value;
    }

    private boolean isVariable_compute() {  return false;  }

    // Declared in TypeHierarchyCheck.jrag at line 20
 @SuppressWarnings({"unchecked", "cast"})     public boolean isUnknown() {
        ASTNode$State state = state();
        boolean isUnknown_value = isUnknown_compute();
        return isUnknown_value;
    }

    private boolean isUnknown_compute() {  return type().isUnknown();  }

    // Declared in TypeHierarchyCheck.jrag at line 150
 @SuppressWarnings({"unchecked", "cast"})     public boolean staticContextQualifier() {
        ASTNode$State state = state();
        boolean staticContextQualifier_value = staticContextQualifier_compute();
        return staticContextQualifier_value;
    }

    private boolean staticContextQualifier_compute() {  return false;  }

    protected boolean false_label_computed = false;
    protected soot.jimple.Stmt false_label_value;
    // Declared in BooleanExpressions.jrag at line 16
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt false_label() {
        if(false_label_computed) {
            return false_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        false_label_value = false_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            false_label_computed = true;
        return false_label_value;
    }

    private soot.jimple.Stmt false_label_compute() {  return getParent().definesLabel() ? condition_false_label() : newLabel();  }

    protected boolean true_label_computed = false;
    protected soot.jimple.Stmt true_label_value;
    // Declared in BooleanExpressions.jrag at line 18
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt true_label() {
        if(true_label_computed) {
            return true_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        true_label_value = true_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            true_label_computed = true;
        return true_label_value;
    }

    private soot.jimple.Stmt true_label_compute() {  return getParent().definesLabel() ? condition_true_label() : newLabel();  }

    // Declared in BooleanExpressions.jrag at line 82
 @SuppressWarnings({"unchecked", "cast"})     public boolean canBeTrue() {
        ASTNode$State state = state();
        boolean canBeTrue_value = canBeTrue_compute();
        return canBeTrue_value;
    }

    private boolean canBeTrue_compute() {  return !isFalse();  }

    // Declared in BooleanExpressions.jrag at line 92
 @SuppressWarnings({"unchecked", "cast"})     public boolean canBeFalse() {
        ASTNode$State state = state();
        boolean canBeFalse_value = canBeFalse_compute();
        return canBeFalse_value;
    }

    private boolean canBeFalse_compute() {  return !isTrue();  }

    // Declared in DefiniteAssignment.jrag at line 15
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDest() {
        ASTNode$State state = state();
        boolean isDest_value = getParent().Define_boolean_isDest(this, null);
        return isDest_value;
    }

    // Declared in DefiniteAssignment.jrag at line 25
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSource() {
        ASTNode$State state = state();
        boolean isSource_value = getParent().Define_boolean_isSource(this, null);
        return isSource_value;
    }

    // Declared in DefiniteAssignment.jrag at line 49
 @SuppressWarnings({"unchecked", "cast"})     public boolean isIncOrDec() {
        ASTNode$State state = state();
        boolean isIncOrDec_value = getParent().Define_boolean_isIncOrDec(this, null);
        return isIncOrDec_value;
    }

    // Declared in DefiniteAssignment.jrag at line 236
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAbefore(Variable v) {
        ASTNode$State state = state();
        boolean isDAbefore_Variable_value = getParent().Define_boolean_isDAbefore(this, null, v);
        return isDAbefore_Variable_value;
    }

    // Declared in DefiniteAssignment.jrag at line 694
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUbefore(Variable v) {
        ASTNode$State state = state();
        boolean isDUbefore_Variable_value = getParent().Define_boolean_isDUbefore(this, null, v);
        return isDUbefore_Variable_value;
    }

    // Declared in LookupMethod.jrag at line 23
 @SuppressWarnings({"unchecked", "cast"})     public Collection lookupMethod(String name) {
        ASTNode$State state = state();
        Collection lookupMethod_String_value = getParent().Define_Collection_lookupMethod(this, null, name);
        return lookupMethod_String_value;
    }

    // Declared in LookupType.jrag at line 49
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeBoolean() {
        ASTNode$State state = state();
        TypeDecl typeBoolean_value = getParent().Define_TypeDecl_typeBoolean(this, null);
        return typeBoolean_value;
    }

    // Declared in LookupType.jrag at line 50
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeByte() {
        ASTNode$State state = state();
        TypeDecl typeByte_value = getParent().Define_TypeDecl_typeByte(this, null);
        return typeByte_value;
    }

    // Declared in LookupType.jrag at line 51
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeShort() {
        ASTNode$State state = state();
        TypeDecl typeShort_value = getParent().Define_TypeDecl_typeShort(this, null);
        return typeShort_value;
    }

    // Declared in LookupType.jrag at line 52
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeChar() {
        ASTNode$State state = state();
        TypeDecl typeChar_value = getParent().Define_TypeDecl_typeChar(this, null);
        return typeChar_value;
    }

    // Declared in LookupType.jrag at line 53
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeInt() {
        ASTNode$State state = state();
        TypeDecl typeInt_value = getParent().Define_TypeDecl_typeInt(this, null);
        return typeInt_value;
    }

    // Declared in LookupType.jrag at line 54
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeLong() {
        ASTNode$State state = state();
        TypeDecl typeLong_value = getParent().Define_TypeDecl_typeLong(this, null);
        return typeLong_value;
    }

    // Declared in LookupType.jrag at line 55
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeFloat() {
        ASTNode$State state = state();
        TypeDecl typeFloat_value = getParent().Define_TypeDecl_typeFloat(this, null);
        return typeFloat_value;
    }

    // Declared in LookupType.jrag at line 56
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeDouble() {
        ASTNode$State state = state();
        TypeDecl typeDouble_value = getParent().Define_TypeDecl_typeDouble(this, null);
        return typeDouble_value;
    }

    // Declared in LookupType.jrag at line 57
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeString() {
        ASTNode$State state = state();
        TypeDecl typeString_value = getParent().Define_TypeDecl_typeString(this, null);
        return typeString_value;
    }

    // Declared in LookupType.jrag at line 58
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeVoid() {
        ASTNode$State state = state();
        TypeDecl typeVoid_value = getParent().Define_TypeDecl_typeVoid(this, null);
        return typeVoid_value;
    }

    // Declared in LookupType.jrag at line 59
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeNull() {
        ASTNode$State state = state();
        TypeDecl typeNull_value = getParent().Define_TypeDecl_typeNull(this, null);
        return typeNull_value;
    }

    // Declared in LookupType.jrag at line 72
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl unknownType() {
        ASTNode$State state = state();
        TypeDecl unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
        return unknownType_value;
    }

    // Declared in LookupType.jrag at line 86
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasPackage(String packageName) {
        ASTNode$State state = state();
        boolean hasPackage_String_value = getParent().Define_boolean_hasPackage(this, null, packageName);
        return hasPackage_String_value;
    }

    // Declared in LookupType.jrag at line 95
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lookupType(String packageName, String typeName) {
        ASTNode$State state = state();
        TypeDecl lookupType_String_String_value = getParent().Define_TypeDecl_lookupType(this, null, packageName, typeName);
        return lookupType_String_String_value;
    }

    // Declared in LookupType.jrag at line 176
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet lookupType(String name) {
        ASTNode$State state = state();
        SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
        return lookupType_String_value;
    }

    // Declared in LookupVariable.jrag at line 19
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet lookupVariable(String name) {
        ASTNode$State state = state();
        SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
        return lookupVariable_String_value;
    }

    // Declared in SyntacticClassification.jrag at line 20
 @SuppressWarnings({"unchecked", "cast"})     public NameType nameType() {
        ASTNode$State state = state();
        NameType nameType_value = getParent().Define_NameType_nameType(this, null);
        return nameType_value;
    }

    // Declared in TypeAnalysis.jrag at line 511
 @SuppressWarnings({"unchecked", "cast"})     public BodyDecl enclosingBodyDecl() {
        ASTNode$State state = state();
        BodyDecl enclosingBodyDecl_value = getParent().Define_BodyDecl_enclosingBodyDecl(this, null);
        return enclosingBodyDecl_value;
    }

    // Declared in TypeAnalysis.jrag at line 568
 @SuppressWarnings({"unchecked", "cast"})     public String hostPackage() {
        ASTNode$State state = state();
        String hostPackage_value = getParent().Define_String_hostPackage(this, null);
        return hostPackage_value;
    }

    // Declared in TypeAnalysis.jrag at line 583
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl hostType() {
        ASTNode$State state = state();
        TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
        return hostType_value;
    }

    // Declared in TypeHierarchyCheck.jrag at line 11
 @SuppressWarnings({"unchecked", "cast"})     public String methodHost() {
        ASTNode$State state = state();
        String methodHost_value = getParent().Define_String_methodHost(this, null);
        return methodHost_value;
    }

    // Declared in TypeHierarchyCheck.jrag at line 134
 @SuppressWarnings({"unchecked", "cast"})     public boolean inStaticContext() {
        ASTNode$State state = state();
        boolean inStaticContext_value = getParent().Define_boolean_inStaticContext(this, null);
        return inStaticContext_value;
    }

    // Declared in GenericMethodsInference.jrag at line 33
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl assignConvertedType() {
        ASTNode$State state = state();
        TypeDecl assignConvertedType_value = getParent().Define_TypeDecl_assignConvertedType(this, null);
        return assignConvertedType_value;
    }

    // Declared in BooleanExpressions.jrag at line 48
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt condition_false_label() {
        ASTNode$State state = state();
        soot.jimple.Stmt condition_false_label_value = getParent().Define_soot_jimple_Stmt_condition_false_label(this, null);
        return condition_false_label_value;
    }

    // Declared in BooleanExpressions.jrag at line 52
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt condition_true_label() {
        ASTNode$State state = state();
        soot.jimple.Stmt condition_true_label_value = getParent().Define_soot_jimple_Stmt_condition_true_label(this, null);
        return condition_true_label_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
