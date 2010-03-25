
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class FieldDeclaration extends MemberDecl implements Cloneable, SimpleSet, Iterator, Variable {
    public void flushCache() {
        super.flushCache();
        accessibleFrom_TypeDecl_values = null;
        exceptions_computed = false;
        exceptions_value = null;
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        constant_computed = false;
        constant_value = null;
        usesTypeVariable_computed = false;
        sourceVariableDecl_computed = false;
        sourceVariableDecl_value = null;
        sootRef_computed = false;
        sootRef_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public FieldDeclaration clone() throws CloneNotSupportedException {
        FieldDeclaration node = (FieldDeclaration)super.clone();
        node.accessibleFrom_TypeDecl_values = null;
        node.exceptions_computed = false;
        node.exceptions_value = null;
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.constant_computed = false;
        node.constant_value = null;
        node.usesTypeVariable_computed = false;
        node.sourceVariableDecl_computed = false;
        node.sourceVariableDecl_value = null;
        node.sootRef_computed = false;
        node.sootRef_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public FieldDeclaration copy() {
      try {
          FieldDeclaration node = (FieldDeclaration)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public FieldDeclaration fullCopy() {
        FieldDeclaration res = (FieldDeclaration)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BoundNames.jrag at line 11

  public Access createQualifiedBoundAccess() {
    if(isStatic())
      return hostType().createQualifiedAccess().qualifiesAccess(new BoundFieldAccess(this));
    else
      return new ThisAccess("this").qualifiesAccess(
        new BoundFieldAccess(this));
  }

    // Declared in BoundNames.jrag at line 86


  public Access createBoundFieldAccess() {
    return createQualifiedBoundAccess();
  }

    // Declared in DataStructures.jrag at line 69

  public SimpleSet add(Object o) {
    return new SimpleSetImpl().add(this).add(o);
  }

    // Declared in DataStructures.jrag at line 75

  private FieldDeclaration iterElem;

    // Declared in DataStructures.jrag at line 76

  public Iterator iterator() { iterElem = this; return this; }

    // Declared in DataStructures.jrag at line 77

  public boolean hasNext() { return iterElem != null; }

    // Declared in DataStructures.jrag at line 78

  public Object next() { Object o = iterElem; iterElem = null; return o; }

    // Declared in DataStructures.jrag at line 79

  public void remove() { throw new UnsupportedOperationException(); }

    // Declared in DefiniteAssignment.jrag at line 179

  
  public void definiteAssignment() {
    super.definiteAssignment();
    if(isBlank() && isFinal() && isClassVariable()) {
      boolean found = false;
      TypeDecl typeDecl = hostType();
      for(int i = 0; i < typeDecl.getNumBodyDecl(); i++) {
        if(typeDecl.getBodyDecl(i) instanceof StaticInitializer) {
          StaticInitializer s = (StaticInitializer)typeDecl.getBodyDecl(i);
          if(s.isDAafter(this))
            found = true;
        }
        
        else if(typeDecl.getBodyDecl(i) instanceof FieldDeclaration) {
          FieldDeclaration f = (FieldDeclaration)typeDecl.getBodyDecl(i);
          if(f.isStatic() && f.isDAafter(this))
            found = true;
        }
        
      }
      if(!found)
        error("blank final class variable " + name() + " in " + hostType().typeName() + " is not definitely assigned in static initializer");

    }
    if(isBlank() && isFinal() && isInstanceVariable()) {
      TypeDecl typeDecl = hostType();
      boolean found = false;
      for(int i = 0; !found && i < typeDecl.getNumBodyDecl(); i++) {
        if(typeDecl.getBodyDecl(i) instanceof FieldDeclaration) {
          FieldDeclaration f = (FieldDeclaration)typeDecl.getBodyDecl(i);
          if(!f.isStatic() && f.isDAafter(this))
            found = true;
        }
        else if(typeDecl.getBodyDecl(i) instanceof InstanceInitializer) {
          InstanceInitializer ii = (InstanceInitializer)typeDecl.getBodyDecl(i);
          if(ii.getBlock().isDAafter(this))
            found = true;
        }
      }
      for(Iterator iter = typeDecl.constructors().iterator(); !found && iter.hasNext(); ) {
        ConstructorDecl c = (ConstructorDecl)iter.next();
        if(!c.isDAafter(this)) {
          error("blank final instance variable " + name() + " in " + hostType().typeName() + " is not definitely assigned after " + c.signature());
          }
      }
    }
    if(isBlank() && hostType().isInterfaceDecl()) {
            error("variable  " + name() + " in " + hostType().typeName() + " which is an interface must have an initializer");
    }

  }

    // Declared in Modifiers.jrag at line 112

 
  public void checkModifiers() {
    super.checkModifiers();
    if(hostType().isInterfaceDecl()) {
      if(isProtected())
        error("an interface field may not be protected");
      if(isPrivate())
        error("an interface field may not be private");
      if(isTransient())
        error("an interface field may not be transient");
      if(isVolatile())
        error("an interface field may not be volatile");
    }
  }

    // Declared in NameCheck.jrag at line 277


  public void nameCheck() {
    super.nameCheck();
    // 8.3
    for(Iterator iter = hostType().memberFields(name()).iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      if(v != this && v.hostType() == hostType())
        error("field named " + name() + " is multiply declared in type " + hostType().typeName());
    }

  }

    // Declared in NodeConstructors.jrag at line 86


  public FieldDeclaration(Modifiers m, Access type, String name) {
    this(m, type, name, new Opt());
  }

    // Declared in NodeConstructors.jrag at line 90

  
  public FieldDeclaration(Modifiers m, Access type, String name, Expr init) {
    this(m, type, name, new Opt(init));
  }

    // Declared in PrettyPrint.jadd at line 151


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

    // Declared in TypeCheck.jrag at line 33


  // 5.2
  public void typeCheck() {
    if(hasInit()) {
      TypeDecl source = getInit().type();
      TypeDecl dest = type();
      if(!source.assignConversionTo(dest, getInit()))
        error("can not assign " + name() + " of type " + dest.typeName() +
              " a value of type " + source.typeName());
    }
  }

    // Declared in Generics.jrag at line 1050

  public BodyDecl p(Parameterization parTypeDecl) {
    FieldDeclaration f = new FieldDeclarationSubstituted(
      (Modifiers)getModifiers().fullCopy(),
      getTypeAccess().type().substituteReturnType(parTypeDecl),
      getID(),
      new Opt(),
      this
    );
    return f;
  }

    // Declared in InnerClasses.jrag at line 278


  public MethodDecl createAccessor(TypeDecl fieldQualifier) {
    MethodDecl m = (MethodDecl)fieldQualifier.getAccessor(this, "field_read");
    if(m != null) return m;
    
    int accessorIndex = fieldQualifier.accessorCounter++;
    Modifiers modifiers = new Modifiers(new List());
    modifiers.addModifier(new Modifier("static"));
    modifiers.addModifier(new Modifier("synthetic"));
    modifiers.addModifier(new Modifier("public"));

    List parameters = new List();
    if(!isStatic())
      parameters.add(new ParameterDeclaration(fieldQualifier.createQualifiedAccess(), "that"));

    m = new MethodDecl(
      modifiers,
      type().createQualifiedAccess(),
      "get$" + name() + "$access$" + accessorIndex,
      parameters,
      new List(),
      new Opt(
        new Block(
          new List().add(
            new ReturnStmt(createAccess())
          )
        )
      )
    );
    m = fieldQualifier.addMemberMethod(m);
    fieldQualifier.addAccessor(this, "field_read", m);
    return m;
  }

    // Declared in InnerClasses.jrag at line 311


  public MethodDecl createAccessorWrite(TypeDecl fieldQualifier) {
    MethodDecl m = (MethodDecl)fieldQualifier.getAccessor(this, "field_write");
    if(m != null) return m;

    int accessorIndex = fieldQualifier.accessorCounter++;
    Modifiers modifiers = new Modifiers(new List());
    modifiers.addModifier(new Modifier("static"));
    modifiers.addModifier(new Modifier("synthetic"));
    modifiers.addModifier(new Modifier("public"));

    List parameters = new List();
    if(!isStatic())
      parameters.add(new ParameterDeclaration(fieldQualifier.createQualifiedAccess(), "that"));
    parameters.add(new ParameterDeclaration(type().createQualifiedAccess(), "value"));

    m = new MethodDecl(
      modifiers,
      type().createQualifiedAccess(),
      "set$" + name() + "$access$" + accessorIndex,
      parameters,
      new List(),
      new Opt(
        new Block(
          new List().add(
            new ExprStmt(
              new AssignSimpleExpr(
                createAccess(),
                new VarAccess("value")
              )
            )
          ).add(
            new ReturnStmt(
              new Opt(
                new VarAccess("value")
              )
            )
          )
        )
      )
    );
    m = fieldQualifier.addMemberMethod(m);
    fieldQualifier.addAccessor(this, "field_write", m);
    return m;
  }

    // Declared in InnerClasses.jrag at line 356


  private Access createAccess() {
    Access fieldAccess = new BoundFieldAccess(this);
    return isStatic() ? fieldAccess : new VarAccess("that").qualifiesAccess(fieldAccess);
  }

    // Declared in EmitJimple.jrag at line 329

  

  public void jimplify1phase2() {
    String name = name();
    soot.Type type = type().getSootType();
    int modifiers = sootTypeModifiers();
    if(!hostType().getSootClassDecl().declaresFieldByName(name)) {
      SootField f = new SootField(name, type, modifiers);
      hostType().getSootClassDecl().addField(f);
      if(isStatic() && isFinal() && isConstant() && (type().isPrimitive() || type().isString())) {
        if(type().isString())
          f.addTag(new soot.tagkit.StringConstantValueTag(constant().stringValue()));
        else if(type().isLong())
          f.addTag(new soot.tagkit.LongConstantValueTag(constant().longValue()));
        else if(type().isDouble())
          f.addTag(new soot.tagkit.DoubleConstantValueTag(constant().doubleValue()));
        else if(type().isFloat())
          f.addTag(new soot.tagkit.FloatConstantValueTag(constant().floatValue()));
        else if(type().isIntegralType())
          f.addTag(new soot.tagkit.IntegerConstantValueTag(constant().intValue()));
      }
      sootField = f;
    } else {
	sootField = hostType().getSootClassDecl().getFieldByName(name);
    }
    addAttributes();
  }

    // Declared in EmitJimple.jrag at line 354

  public SootField sootField;

    // Declared in AnnotationsCodegen.jrag at line 32

  
  public void addAttributes() {
    super.addAttributes();
    ArrayList c = new ArrayList();
    getModifiers().addRuntimeVisibleAnnotationsAttribute(c);
    getModifiers().addRuntimeInvisibleAnnotationsAttribute(c);
    getModifiers().addSourceOnlyAnnotations(c);
    for(Iterator iter = c.iterator(); iter.hasNext(); ) {
      soot.tagkit.Tag tag = (soot.tagkit.Tag)iter.next();
      sootField.addTag(tag);
    }
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 77

    public FieldDeclaration() {
        super();

        setChild(new Opt(), 2);

    }

    // Declared in java.ast at line 11


    // Declared in java.ast line 77
    public FieldDeclaration(Modifiers p0, Access p1, String p2, Opt<Expr> p3) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
        setChild(p3, 2);
    }

    // Declared in java.ast at line 19


    // Declared in java.ast line 77
    public FieldDeclaration(Modifiers p0, Access p1, beaver.Symbol p2, Opt<Expr> p3) {
        setChild(p0, 0);
        setChild(p1, 1);
        setID(p2);
        setChild(p3, 2);
    }

    // Declared in java.ast at line 26


  protected int numChildren() {
    return 3;
  }

    // Declared in java.ast at line 29

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 77
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in java.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 77
    public void setTypeAccess(Access node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Access getTypeAccess() {
        return (Access)getChild(1);
    }

    // Declared in java.ast at line 9


    public Access getTypeAccessNoTransform() {
        return (Access)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 77
    protected String tokenString_ID;

    // Declared in java.ast at line 3

    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 6

    public int IDstart;

    // Declared in java.ast at line 7

    public int IDend;

    // Declared in java.ast at line 8

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 15

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 77
    public void setInitOpt(Opt<Expr> opt) {
        setChild(opt, 2);
    }

    // Declared in java.ast at line 6


    public boolean hasInit() {
        return getInitOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Expr getInit() {
        return (Expr)getInitOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setInit(Expr node) {
        getInitOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getInitOpt() {
        return (Opt<Expr>)getChild(2);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getInitOptNoTransform() {
        return (Opt<Expr>)getChildNoTransform(2);
    }

    protected java.util.Map accessibleFrom_TypeDecl_values;
    // Declared in AccessControl.jrag at line 109
 @SuppressWarnings({"unchecked", "cast"})     public boolean accessibleFrom(TypeDecl type) {
        Object _parameters = type;
if(accessibleFrom_TypeDecl_values == null) accessibleFrom_TypeDecl_values = new java.util.HashMap(4);
        if(accessibleFrom_TypeDecl_values.containsKey(_parameters)) {
            return ((Boolean)accessibleFrom_TypeDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean accessibleFrom_TypeDecl_value = accessibleFrom_compute(type);
        if(isFinal && num == state().boundariesCrossed)
            accessibleFrom_TypeDecl_values.put(_parameters, Boolean.valueOf(accessibleFrom_TypeDecl_value));
        return accessibleFrom_TypeDecl_value;
    }

    private boolean accessibleFrom_compute(TypeDecl type) {
    if(isPublic())
      return true;
    else if(isProtected()) {
      if(hostPackage().equals(type.hostPackage()))
        return true;
      if(type.withinBodyThatSubclasses(hostType()) != null)
        return true;
      return false;
    }
    else if(isPrivate())
      return hostType().topLevelType() == type.topLevelType();
    else
      return hostPackage().equals(type.hostPackage());
  }

    protected boolean exceptions_computed = false;
    protected Collection exceptions_value;
    // Declared in AnonymousClasses.jrag at line 166
 @SuppressWarnings({"unchecked", "cast"})     public Collection exceptions() {
        if(exceptions_computed) {
            return exceptions_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        exceptions_value = exceptions_compute();
        if(isFinal && num == state().boundariesCrossed)
            exceptions_computed = true;
        return exceptions_value;
    }

    private Collection exceptions_compute() {
    HashSet set = new HashSet();
    if(isInstanceVariable() && hasInit()) {
      collectExceptions(set, this);
      for(Iterator iter = set.iterator(); iter.hasNext(); ) {
        TypeDecl typeDecl = (TypeDecl)iter.next();
        if(!getInit().reachedException(typeDecl))
          iter.remove();
      }
    }
    return set;
  }

    // Declared in ConstantExpression.jrag at line 479
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstant() {
        ASTNode$State state = state();
        boolean isConstant_value = isConstant_compute();
        return isConstant_value;
    }

    private boolean isConstant_compute() {  return isFinal() && hasInit() && getInit().isConstant() && (type() instanceof PrimitiveType || type().isString());  }

    // Declared in DataStructures.jrag at line 67
 @SuppressWarnings({"unchecked", "cast"})     public int size() {
        ASTNode$State state = state();
        int size_value = size_compute();
        return size_value;
    }

    private int size_compute() {  return 1;  }

    // Declared in DataStructures.jrag at line 68
 @SuppressWarnings({"unchecked", "cast"})     public boolean isEmpty() {
        ASTNode$State state = state();
        boolean isEmpty_value = isEmpty_compute();
        return isEmpty_value;
    }

    private boolean isEmpty_compute() {  return false;  }

    // Declared in DataStructures.jrag at line 72
 @SuppressWarnings({"unchecked", "cast"})     public boolean contains(Object o) {
        ASTNode$State state = state();
        boolean contains_Object_value = contains_compute(o);
        return contains_Object_value;
    }

    private boolean contains_compute(Object o) {  return this == o;  }

    // Declared in DefiniteAssignment.jrag at line 316
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        Object _parameters = v;
if(isDAafter_Variable_values == null) isDAafter_Variable_values = new java.util.HashMap(4);
        if(isDAafter_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDAafter_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDAafter_Variable_values.put(_parameters, Boolean.valueOf(isDAafter_Variable_value));
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {
    if(v == this)
      return hasInit();
    return hasInit() ? getInit().isDAafter(v) : isDAbefore(v);
  }

    // Declared in DefiniteAssignment.jrag at line 772
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        Object _parameters = v;
if(isDUafter_Variable_values == null) isDUafter_Variable_values = new java.util.HashMap(4);
        if(isDUafter_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDUafter_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDUafter_Variable_values.put(_parameters, Boolean.valueOf(isDUafter_Variable_value));
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {
    if(v == this)
      return !hasInit();
    return hasInit() ? getInit().isDUafter(v) : isDUbefore(v);
  }

    // Declared in Modifiers.jrag at line 214
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSynthetic() {
        ASTNode$State state = state();
        boolean isSynthetic_value = isSynthetic_compute();
        return isSynthetic_value;
    }

    private boolean isSynthetic_compute() {  return getModifiers().isSynthetic();  }

    // Declared in Modifiers.jrag at line 237
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPublic() {
        ASTNode$State state = state();
        boolean isPublic_value = isPublic_compute();
        return isPublic_value;
    }

    private boolean isPublic_compute() {  return getModifiers().isPublic() || hostType().isInterfaceDecl();  }

    // Declared in Modifiers.jrag at line 238
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPrivate() {
        ASTNode$State state = state();
        boolean isPrivate_value = isPrivate_compute();
        return isPrivate_value;
    }

    private boolean isPrivate_compute() {  return getModifiers().isPrivate();  }

    // Declared in Modifiers.jrag at line 239
 @SuppressWarnings({"unchecked", "cast"})     public boolean isProtected() {
        ASTNode$State state = state();
        boolean isProtected_value = isProtected_compute();
        return isProtected_value;
    }

    private boolean isProtected_compute() {  return getModifiers().isProtected();  }

    // Declared in Modifiers.jrag at line 240
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStatic() {
        ASTNode$State state = state();
        boolean isStatic_value = isStatic_compute();
        return isStatic_value;
    }

    private boolean isStatic_compute() {  return getModifiers().isStatic() || hostType().isInterfaceDecl();  }

    // Declared in Modifiers.jrag at line 242
 @SuppressWarnings({"unchecked", "cast"})     public boolean isFinal() {
        ASTNode$State state = state();
        boolean isFinal_value = isFinal_compute();
        return isFinal_value;
    }

    private boolean isFinal_compute() {  return getModifiers().isFinal() || hostType().isInterfaceDecl();  }

    // Declared in Modifiers.jrag at line 243
 @SuppressWarnings({"unchecked", "cast"})     public boolean isTransient() {
        ASTNode$State state = state();
        boolean isTransient_value = isTransient_compute();
        return isTransient_value;
    }

    private boolean isTransient_compute() {  return getModifiers().isTransient();  }

    // Declared in Modifiers.jrag at line 244
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVolatile() {
        ASTNode$State state = state();
        boolean isVolatile_value = isVolatile_compute();
        return isVolatile_value;
    }

    private boolean isVolatile_compute() {  return getModifiers().isVolatile();  }

    // Declared in PrettyPrint.jadd at line 810
 @SuppressWarnings({"unchecked", "cast"})     public String dumpString() {
        ASTNode$State state = state();
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return getClass().getName() + " [" + getID() + "]";  }

    // Declared in TypeAnalysis.jrag at line 251
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        ASTNode$State state = state();
        TypeDecl type_value = type_compute();
        return type_value;
    }

    private TypeDecl type_compute() {  return getTypeAccess().type();  }

    // Declared in TypeAnalysis.jrag at line 273
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVoid() {
        ASTNode$State state = state();
        boolean isVoid_value = isVoid_compute();
        return isVoid_value;
    }

    private boolean isVoid_compute() {  return type().isVoid();  }

    // Declared in VariableDeclaration.jrag at line 55
 @SuppressWarnings({"unchecked", "cast"})     public boolean isClassVariable() {
        ASTNode$State state = state();
        boolean isClassVariable_value = isClassVariable_compute();
        return isClassVariable_value;
    }

    private boolean isClassVariable_compute() {  return isStatic() || hostType().isInterfaceDecl();  }

    // Declared in VariableDeclaration.jrag at line 56
 @SuppressWarnings({"unchecked", "cast"})     public boolean isInstanceVariable() {
        ASTNode$State state = state();
        boolean isInstanceVariable_value = isInstanceVariable_compute();
        return isInstanceVariable_value;
    }

    private boolean isInstanceVariable_compute() {  return (hostType().isClassDecl() || hostType().isAnonymous() )&& !isStatic();  }

    // Declared in VariableDeclaration.jrag at line 57
 @SuppressWarnings({"unchecked", "cast"})     public boolean isMethodParameter() {
        ASTNode$State state = state();
        boolean isMethodParameter_value = isMethodParameter_compute();
        return isMethodParameter_value;
    }

    private boolean isMethodParameter_compute() {  return false;  }

    // Declared in VariableDeclaration.jrag at line 58
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstructorParameter() {
        ASTNode$State state = state();
        boolean isConstructorParameter_value = isConstructorParameter_compute();
        return isConstructorParameter_value;
    }

    private boolean isConstructorParameter_compute() {  return false;  }

    // Declared in VariableDeclaration.jrag at line 59
 @SuppressWarnings({"unchecked", "cast"})     public boolean isExceptionHandlerParameter() {
        ASTNode$State state = state();
        boolean isExceptionHandlerParameter_value = isExceptionHandlerParameter_compute();
        return isExceptionHandlerParameter_value;
    }

    private boolean isExceptionHandlerParameter_compute() {  return false;  }

    // Declared in VariableDeclaration.jrag at line 60
 @SuppressWarnings({"unchecked", "cast"})     public boolean isLocalVariable() {
        ASTNode$State state = state();
        boolean isLocalVariable_value = isLocalVariable_compute();
        return isLocalVariable_value;
    }

    private boolean isLocalVariable_compute() {  return false;  }

    // Declared in VariableDeclaration.jrag at line 62
 @SuppressWarnings({"unchecked", "cast"})     public boolean isBlank() {
        ASTNode$State state = state();
        boolean isBlank_value = isBlank_compute();
        return isBlank_value;
    }

    private boolean isBlank_compute() {  return !hasInit();  }

    // Declared in VariableDeclaration.jrag at line 64
 @SuppressWarnings({"unchecked", "cast"})     public String name() {
        ASTNode$State state = state();
        String name_value = name_compute();
        return name_value;
    }

    private String name_compute() {  return getID();  }

    protected boolean constant_computed = false;
    protected Constant constant_value;
    // Declared in VariableDeclaration.jrag at line 65
 @SuppressWarnings({"unchecked", "cast"})     public Constant constant() {
        if(constant_computed) {
            return constant_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        constant_value = constant_compute();
        if(isFinal && num == state().boundariesCrossed)
            constant_computed = true;
        return constant_value;
    }

    private Constant constant_compute() {  return type().cast(getInit().constant());  }

    // Declared in Annotations.jrag at line 287
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasAnnotationSuppressWarnings(String s) {
        ASTNode$State state = state();
        boolean hasAnnotationSuppressWarnings_String_value = hasAnnotationSuppressWarnings_compute(s);
        return hasAnnotationSuppressWarnings_String_value;
    }

    private boolean hasAnnotationSuppressWarnings_compute(String s) {  return getModifiers().hasAnnotationSuppressWarnings(s);  }

    // Declared in Annotations.jrag at line 325
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDeprecated() {
        ASTNode$State state = state();
        boolean isDeprecated_value = isDeprecated_compute();
        return isDeprecated_value;
    }

    private boolean isDeprecated_compute() {  return getModifiers().hasDeprecatedAnnotation();  }

    protected boolean usesTypeVariable_computed = false;
    protected boolean usesTypeVariable_value;
    // Declared in Generics.jrag at line 906
 @SuppressWarnings({"unchecked", "cast"})     public boolean usesTypeVariable() {
        if(usesTypeVariable_computed) {
            return usesTypeVariable_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        usesTypeVariable_value = usesTypeVariable_compute();
        if(isFinal && num == state().boundariesCrossed)
            usesTypeVariable_computed = true;
        return usesTypeVariable_value;
    }

    private boolean usesTypeVariable_compute() {  return getTypeAccess().usesTypeVariable();  }

    protected boolean sourceVariableDecl_computed = false;
    protected Variable sourceVariableDecl_value;
    // Declared in Generics.jrag at line 1275
 @SuppressWarnings({"unchecked", "cast"})     public Variable sourceVariableDecl() {
        if(sourceVariableDecl_computed) {
            return sourceVariableDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sourceVariableDecl_value = sourceVariableDecl_compute();
        if(isFinal && num == state().boundariesCrossed)
            sourceVariableDecl_computed = true;
        return sourceVariableDecl_value;
    }

    private Variable sourceVariableDecl_compute() {  return this;  }

    // Declared in GenericsParTypeDecl.jrag at line 66
 @SuppressWarnings({"unchecked", "cast"})     public boolean visibleTypeParameters() {
        ASTNode$State state = state();
        boolean visibleTypeParameters_value = visibleTypeParameters_compute();
        return visibleTypeParameters_value;
    }

    private boolean visibleTypeParameters_compute() {  return !isStatic();  }

    // Declared in EmitJimple.jrag at line 127
 @SuppressWarnings({"unchecked", "cast"})     public int sootTypeModifiers() {
        ASTNode$State state = state();
        int sootTypeModifiers_value = sootTypeModifiers_compute();
        return sootTypeModifiers_value;
    }

    private int sootTypeModifiers_compute() {
    int result = 0;
    if(isPublic()) result |= soot.Modifier.PUBLIC;
    if(isProtected()) result |= soot.Modifier.PROTECTED;
    if(isPrivate()) result |= soot.Modifier.PRIVATE;
    if(isFinal()) result |= soot.Modifier.FINAL;
    if(isStatic()) result |= soot.Modifier.STATIC;
    return result;
  }

    protected boolean sootRef_computed = false;
    protected SootFieldRef sootRef_value;
    // Declared in EmitJimple.jrag at line 355
 @SuppressWarnings({"unchecked", "cast"})     public SootFieldRef sootRef() {
        if(sootRef_computed) {
            return sootRef_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sootRef_value = sootRef_compute();
        if(isFinal && num == state().boundariesCrossed)
            sootRef_computed = true;
        return sootRef_value;
    }

    private SootFieldRef sootRef_compute() {  return Scene.v().makeFieldRef(hostType().getSootClassDecl(), name(), type().getSootType(), isStatic());  }

    // Declared in GenericsCodegen.jrag at line 31
 @SuppressWarnings({"unchecked", "cast"})     public FieldDeclaration erasedField() {
        ASTNode$State state = state();
        FieldDeclaration erasedField_value = erasedField_compute();
        return erasedField_value;
    }

    private FieldDeclaration erasedField_compute() {  return this;  }

    // Declared in ExceptionHandling.jrag at line 34
 @SuppressWarnings({"unchecked", "cast"})     public boolean handlesException(TypeDecl exceptionType) {
        ASTNode$State state = state();
        boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
        return handlesException_TypeDecl_value;
    }

    // Declared in DefiniteAssignment.jrag at line 39
    public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
        if(caller == getInitOptNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isSource(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 322
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getInitOptNoTransform()){
    return isDAbefore(v);
  }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in ExceptionHandling.jrag at line 143
    public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
        if(caller == getInitOptNoTransform()){
    if(hostType().isAnonymous())
      return true;
    if(!exceptionType.isUncheckedException())
      return true;
    for(Iterator iter = hostType().constructors().iterator(); iter.hasNext(); ) {
      ConstructorDecl decl = (ConstructorDecl)iter.next();
      if(!decl.throwsException(exceptionType))
        return false;
    }
    return true;
  }
        return getParent().Define_boolean_handlesException(this, caller, exceptionType);
    }

    // Declared in Modifiers.jrag at line 260
    public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBePublic(this, caller);
    }

    // Declared in Modifiers.jrag at line 261
    public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeProtected(this, caller);
    }

    // Declared in Modifiers.jrag at line 262
    public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBePrivate(this, caller);
    }

    // Declared in Modifiers.jrag at line 263
    public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeStatic(this, caller);
    }

    // Declared in Modifiers.jrag at line 264
    public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeFinal(this, caller);
    }

    // Declared in Modifiers.jrag at line 265
    public boolean Define_boolean_mayBeTransient(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeTransient(this, caller);
    }

    // Declared in Modifiers.jrag at line 266
    public boolean Define_boolean_mayBeVolatile(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_mayBeVolatile(this, caller);
    }

    // Declared in SyntacticClassification.jrag at line 78
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getTypeAccessNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

    // Declared in TypeAnalysis.jrag at line 260
    public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
        if(caller == getInitOptNoTransform()) {
            return type();
        }
        return getParent().Define_TypeDecl_declType(this, caller);
    }

    // Declared in TypeHierarchyCheck.jrag at line 141
    public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
        if(caller == getInitOptNoTransform()) {
            return isStatic() || hostType().isInterfaceDecl();
        }
        return getParent().Define_boolean_inStaticContext(this, caller);
    }

    // Declared in Annotations.jrag at line 80
    public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
        if(caller == getModifiersNoTransform()) {
            return name.equals("FIELD");
        }
        return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
    }

    // Declared in GenericMethodsInference.jrag at line 35
    public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
        if(caller == getInitOptNoTransform()) {
            return type();
        }
        return getParent().Define_TypeDecl_assignConvertedType(this, caller);
    }

    // Declared in InnerClasses.jrag at line 64
    public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
        if(caller == getInitOptNoTransform()) {
            return type().componentType();
        }
        return getParent().Define_TypeDecl_expectedType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
