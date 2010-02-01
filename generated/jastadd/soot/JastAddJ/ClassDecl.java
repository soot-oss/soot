
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


// 4.3 Reference Types and Values

public class ClassDecl extends ReferenceType implements Cloneable {
    public void flushCache() {
        super.flushCache();
        interfacesMethodsSignatureMap_computed = false;
        interfacesMethodsSignatureMap_value = null;
        methodsSignatureMap_computed = false;
        methodsSignatureMap_value = null;
        ancestorMethods_String_values = null;
        memberTypes_String_values = null;
        memberFieldsMap_computed = false;
        memberFieldsMap_value = null;
        memberFields_String_values = null;
        unimplementedMethods_computed = false;
        unimplementedMethods_value = null;
        hasAbstract_computed = false;
        castingConversionTo_TypeDecl_values = null;
        isString_computed = false;
        isObject_computed = false;
        instanceOf_TypeDecl_values = null;
        isCircular_visited = -1;
        isCircular_computed = false;
        isCircular_initialized = false;
        implementedInterfaces_computed = false;
        implementedInterfaces_value = null;
        subtype_TypeDecl_values = null;
        sootClass_computed = false;
        sootClass_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ClassDecl clone() throws CloneNotSupportedException {
        ClassDecl node = (ClassDecl)super.clone();
        node.interfacesMethodsSignatureMap_computed = false;
        node.interfacesMethodsSignatureMap_value = null;
        node.methodsSignatureMap_computed = false;
        node.methodsSignatureMap_value = null;
        node.ancestorMethods_String_values = null;
        node.memberTypes_String_values = null;
        node.memberFieldsMap_computed = false;
        node.memberFieldsMap_value = null;
        node.memberFields_String_values = null;
        node.unimplementedMethods_computed = false;
        node.unimplementedMethods_value = null;
        node.hasAbstract_computed = false;
        node.castingConversionTo_TypeDecl_values = null;
        node.isString_computed = false;
        node.isObject_computed = false;
        node.instanceOf_TypeDecl_values = null;
        node.isCircular_visited = -1;
        node.isCircular_computed = false;
        node.isCircular_initialized = false;
        node.implementedInterfaces_computed = false;
        node.implementedInterfaces_value = null;
        node.subtype_TypeDecl_values = null;
        node.sootClass_computed = false;
        node.sootClass_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ClassDecl copy() {
      try {
          ClassDecl node = (ClassDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ClassDecl fullCopy() {
        ClassDecl res = (ClassDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in AccessControl.jrag at line 147

  
  public void accessControl() {
    super.accessControl();
    
    // 8.1.1.2 final Classes
    TypeDecl typeDecl = hasSuperclass() ? superclass() : null;
    if(typeDecl != null && !typeDecl.accessibleFromExtend(this))
    //if(typeDecl != null && !isCircular() && !typeDecl.accessibleFrom(this))
      error("class " + fullName() + " may not extend non accessible type " + typeDecl.fullName());

    if(hasSuperclass() && !superclass().accessibleFrom(this))
      error("a superclass must be accessible which " + superclass().name() + " is not");

    // 8.1.4
    for(int i = 0; i < getNumImplements(); i++) {
      TypeDecl decl = getImplements(i).type();
      if(!decl.isCircular() && !decl.accessibleFrom(this))
        error("class " + fullName() + " can not implement non accessible type " + decl.fullName());
    }
  }

    // Declared in ExceptionHandling.jrag at line 92


  public void exceptionHandling() {
    constructors();
    super.exceptionHandling();
  }

    // Declared in LookupMethod.jrag at line 248


  // iterator over all methods in implemented interfaces
  public Iterator interfacesMethodsIterator() {
    return new Iterator() {
      private Iterator outer = interfacesMethodsSignatureMap().values().iterator();
      private Iterator inner = null;
      public boolean hasNext() {
        if((inner == null || !inner.hasNext()) && outer.hasNext())
          inner = ((SimpleSet)outer.next()).iterator();
        return inner == null ? false : inner.hasNext();
      }
      public Object next() {
        return inner.next();
      }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }

    // Declared in Modifiers.jrag at line 94

  
 public void checkModifiers() {
    super.checkModifiers();
    // 8.1.1.2 final Classes
    TypeDecl typeDecl = hasSuperclass() ? superclass() : null;
    if(typeDecl != null && typeDecl.isFinal()) {
      error("class " + fullName() + " may not extend final class " + typeDecl.fullName());
    }

  }

    // Declared in PrettyPrint.jadd at line 62

    
  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);
    s.append("class " + name());
    if(hasSuperClassAccess()) {
      s.append(" extends ");
      getSuperClassAccess().toString(s);
    }
    if(getNumImplements() > 0) {
      s.append(" implements ");
      getImplements(0).toString(s);
      for(int i = 1; i < getNumImplements(); i++) {
        s.append(", ");
        getImplements(i).toString(s);
      }
    }
    s.append(" {");
    for(int i=0; i < getNumBodyDecl(); i++) {
      getBodyDecl(i).toString(s);
    }
    s.append(indent() + "}");
  }

    // Declared in TypeAnalysis.jrag at line 593


  public boolean hasSuperclass() {
    return !isObject();
  }

    // Declared in TypeAnalysis.jrag at line 597


  public ClassDecl superclass() {
    if(isObject())
      return null;
    if(hasSuperClassAccess() && !isCircular() && getSuperClassAccess().type().isClassDecl())
      return (ClassDecl)getSuperClassAccess().type();
    return (ClassDecl)typeObject();
  }

    // Declared in TypeAnalysis.jrag at line 612

  

  public Iterator interfacesIterator() {
    return new Iterator() {
      public boolean hasNext() {
        computeNextCurrent();
        return current != null;
      }
      public Object next() {
        return current;
      }
      public void remove() {
        throw new UnsupportedOperationException();
      }
      private int index = 0;
      private TypeDecl current = null;
      private void computeNextCurrent() {
        current = null;
        if(isObject() || isCircular())
          return;
        while(index < getNumImplements()) {
          TypeDecl typeDecl = getImplements(index++).type();
          if(!typeDecl.isCircular() && typeDecl.isInterfaceDecl()) {
            current = typeDecl;
            return;
          }
        }
      }
    };
  }

    // Declared in TypeHierarchyCheck.jrag at line 239


  public void nameCheck() {
    super.nameCheck();
    if(hasSuperClassAccess() && !getSuperClassAccess().type().isClassDecl())
      error("class may only inherit a class and not " + getSuperClassAccess().type().typeName());
    if(isObject() && hasSuperClassAccess())
      error("class Object may not have superclass");
    if(isObject() && getNumImplements() != 0)
      error("class Object may not implement interfaces");
    
    // 8.1.3
    if(isCircular())
      error("circular inheritance dependency in " + typeName()); 
      
    // 8.1.4
    HashSet set = new HashSet();
    for(int i = 0; i < getNumImplements(); i++) {
      TypeDecl decl = getImplements(i).type();
      if(!decl.isInterfaceDecl() && !decl.isUnknown())
        error("type " + fullName() + " tries to implement non interface type " + decl.fullName());
      if(set.contains(decl))
        error("type " + decl.fullName() + " mentionened multiple times in implements clause");
      set.add(decl);
    }

    for(Iterator iter = interfacesMethodsIterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(localMethodsSignature(m.signature()).isEmpty()) {
        SimpleSet s = superclass().methodsSignature(m.signature());
        for(Iterator i2 = s.iterator(); i2.hasNext(); ) {
          MethodDecl n = (MethodDecl)i2.next();
          if(n.accessibleFrom(this)) {
            interfaceMethodCompatibleWithInherited(m, n);
          }
        }
        if(s.isEmpty()) {
          for(Iterator i2 = interfacesMethodsSignature(m.signature()).iterator(); i2.hasNext(); ) {
            MethodDecl n = (MethodDecl)i2.next();
            if(!n.mayOverrideReturn(m) && !m.mayOverrideReturn(n))
              error("Xthe return type of method " + m.signature() + " in " + m.hostType().typeName() + 
                  " does not match the return type of method " + n.signature() + " in " + 
                  n.hostType().typeName() + " and may thus not be overriden");
          }
        }
      }
    }
  }

    // Declared in TypeHierarchyCheck.jrag at line 286


  private void interfaceMethodCompatibleWithInherited(MethodDecl m, MethodDecl n) {
    if(n.isStatic())
      error("Xa static method may not hide an instance method");
    if(!n.isAbstract() && !n.isPublic())
      error("Xoverriding access modifier error for " + m.signature() + " in " + m.hostType().typeName() + " and " + n.hostType().typeName());
    if(!n.mayOverrideReturn(m) && !m.mayOverrideReturn(m))
      error("Xthe return type of method " + m.signature() + " in " + m.hostType().typeName() + 
            " does not match the return type of method " + n.signature() + " in " + 
            n.hostType().typeName() + " and may thus not be overriden");
    if(!n.isAbstract()) {
      // n implements and overrides method m in the interface
      // may not throw more checked exceptions
      for(int i = 0; i < n.getNumException(); i++) {
        Access e = n.getException(i);
        boolean found = false;
        for(int j = 0; !found && j < m.getNumException(); j++) {
          if(e.type().instanceOf(m.getException(j).type()))
            found = true;
        }
        if(!found && e.type().isUncheckedException())
          error("X" + n.signature() + " in " + n.hostType().typeName() + " may not throw more checked exceptions than overridden method " +
           m.signature() + " in " + m.hostType().typeName());
      }
    }
  }

    // Declared in Generics.jrag at line 163

  public TypeDecl makeGeneric(Signatures.ClassSignature s) {
    if(s.hasFormalTypeParameters()) {
      ASTNode node = getParent();
      int index = node.getIndexOfChild(this);
      node.setChild(
          new GenericClassDecl(
            getModifiersNoTransform(),
            getID(),
            s.hasSuperclassSignature() ? new Opt(s.superclassSignature()) : getSuperClassAccessOptNoTransform(),
            s.hasSuperinterfaceSignature() ? s.superinterfaceSignature() : getImplementsListNoTransform(),
            getBodyDeclListNoTransform(),
            s.typeParameters()
          ),
          index
      );
      return (TypeDecl)node.getChildNoTransform(index);
    }
    else {
      if(s.hasSuperclassSignature())
        setSuperClassAccessOpt(new Opt(s.superclassSignature()));
      if(s.hasSuperinterfaceSignature())
        setImplementsList(s.superinterfaceSignature());
      return this;
    }
  }

    // Declared in Generics.jrag at line 1068


  public ClassDecl p(Parameterization parTypeDecl) {
    ClassDecl c = new ClassDeclSubstituted(
      (Modifiers)getModifiers().fullCopy(),
      getID(),
      hasSuperClassAccess() ? new Opt(getSuperClassAccess().type().substitute(parTypeDecl)) : new Opt(),
      getImplementsList().substitute(parTypeDecl),
      new List(), 
      this
    );
    return c;
  }

    // Declared in EmitJimple.jrag at line 163


  public void jimplify1phase2() {
    SootClass sc = getSootClassDecl();
    sc.setResolvingLevel(SootClass.DANGLING);
    sc.setModifiers(sootTypeModifiers());
    sc.setApplicationClass();
    SourceFileTag st = new soot.tagkit.SourceFileTag(sourceNameWithoutPath());
    st.setAbsolutePath(new File(sourceFile()).getAbsolutePath());
    sc.addTag(st);
    if(hasSuperclass()) {
      sc.setSuperclass(superclass().getSootClassDecl());
    }
    for(Iterator iter = interfacesIterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      if(!sc.implementsInterface(typeDecl.getSootClassDecl().getName()))
        sc.addInterface(typeDecl.getSootClassDecl());
    }
    if(isNestedType())
      sc.setOuterClass(enclosingType().getSootClassDecl());
    sc.setResolvingLevel(SootClass.HIERARCHY);
    super.jimplify1phase2();
    sc.setResolvingLevel(SootClass.SIGNATURES);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 63

    public ClassDecl() {
        super();

        setChild(new Opt(), 1);
        setChild(new List(), 2);
        setChild(new List(), 3);

    }

    // Declared in java.ast at line 13


    // Declared in java.ast line 63
    public ClassDecl(Modifiers p0, String p1, Opt<Access> p2, List<Access> p3, List<BodyDecl> p4) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
    }

    // Declared in java.ast at line 22


    // Declared in java.ast line 63
    public ClassDecl(Modifiers p0, beaver.Symbol p1, Opt<Access> p2, List<Access> p3, List<BodyDecl> p4) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
    }

    // Declared in java.ast at line 30


  protected int numChildren() {
    return 4;
  }

    // Declared in java.ast at line 33

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 63
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
    // Declared in java.ast line 63
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 63
    public void setSuperClassAccessOpt(Opt<Access> opt) {
        setChild(opt, 1);
    }

    // Declared in java.ast at line 6


    public boolean hasSuperClassAccess() {
        return getSuperClassAccessOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getSuperClassAccess() {
        return (Access)getSuperClassAccessOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setSuperClassAccess(Access node) {
        getSuperClassAccessOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Access> getSuperClassAccessOpt() {
        return (Opt<Access>)getChild(1);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Access> getSuperClassAccessOptNoTransform() {
        return (Opt<Access>)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 63
    public void setImplementsList(List<Access> list) {
        setChild(list, 2);
    }

    // Declared in java.ast at line 6


    public int getNumImplements() {
        return getImplementsList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getImplements(int i) {
        return (Access)getImplementsList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addImplements(Access node) {
        List<Access> list = (parent == null || state == null) ? getImplementsListNoTransform() : getImplementsList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addImplementsNoTransform(Access node) {
        List<Access> list = getImplementsListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setImplements(Access node, int i) {
        List<Access> list = getImplementsList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Access> getImplementss() {
        return getImplementsList();
    }

    // Declared in java.ast at line 31

    public List<Access> getImplementssNoTransform() {
        return getImplementsListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getImplementsList() {
        List<Access> list = (List<Access>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getImplementsListNoTransform() {
        return (List<Access>)getChildNoTransform(2);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 63
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 3);
    }

    // Declared in java.ast at line 6


    public int getNumBodyDecl() {
        return getBodyDeclList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public BodyDecl getBodyDecl(int i) {
        return (BodyDecl)getBodyDeclList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addBodyDecl(BodyDecl node) {
        List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addBodyDeclNoTransform(BodyDecl node) {
        List<BodyDecl> list = getBodyDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setBodyDecl(BodyDecl node, int i) {
        List<BodyDecl> list = getBodyDeclList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<BodyDecl> getBodyDecls() {
        return getBodyDeclList();
    }

    // Declared in java.ast at line 31

    public List<BodyDecl> getBodyDeclsNoTransform() {
        return getBodyDeclListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclList() {
        List<BodyDecl> list = (List<BodyDecl>)getChild(3);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(3);
    }

    // Declared in TypeAnalysis.jrag at line 84
private boolean refined_TypeConversion_ClassDecl_castingConversionTo_TypeDecl(TypeDecl type)
{
    if(type.isArrayDecl()) {
      return isObject();
    }
    else if(type.isClassDecl()) {
      return this == type || instanceOf(type) || type.instanceOf(this);
    }
    else if(type.isInterfaceDecl()) {
      return !isFinal() || instanceOf(type);
    }
    else return super.castingConversionTo(type);
  }

    // Declared in Generics.jrag at line 37
private boolean refined_Generics_ClassDecl_castingConversionTo_TypeDecl(TypeDecl type)
{
    TypeDecl S = this;
    TypeDecl T = type;
    if(T instanceof TypeVariable) {
      TypeVariable t = (TypeVariable)T;
      if(t.getNumTypeBound() == 0) return true;
      for(int i = 0; i < t.getNumTypeBound(); i++)
        if(castingConversionTo(t.getTypeBound(i).type()))
          return true;
      return false;
    }
    if(T.isClassDecl() && (S.erasure() != S || T.erasure() != T))
        return S.erasure().castingConversionTo(T.erasure());
    return refined_TypeConversion_ClassDecl_castingConversionTo_TypeDecl(type);
  }

    // Declared in EmitJimpleRefinements.jrag at line 24
private SootClass refined_EmitJimpleRefinements_ClassDecl_sootClass()
{
    boolean needAddclass = false;
    SootClass sc = null;
    if(Scene.v().containsClass(jvmName())) {
      SootClass cl = Scene.v().getSootClass(jvmName());
      //fix for test case 653: if there's a class java.lang.Object etc. on the command line
      //prefer that class over the Coffi class that may already have been loaded from bytecode
      try {
        MethodSource source = cl.getMethodByName("<clinit>").getSource();
        if(source instanceof CoffiMethodSource) {
          Scene.v().removeClass(cl);
          needAddclass = true;
        }
      } catch(RuntimeException e) {
        //method not found
      }    	
      sc = cl;       
    }
    else {
      needAddclass = true;
    }
    if(needAddclass) {
      if(options().verbose())
        System.out.println("Creating from source " + jvmName());        
      sc = new SootClass(jvmName());
      sc.setResolvingLevel(SootClass.DANGLING);
      Scene.v().addClass(sc);
    } 
    return sc;
  }

    // Declared in ConstantExpression.jrag at line 318
 @SuppressWarnings({"unchecked", "cast"})     public Constant cast(Constant c) {
        ASTNode$State state = state();
        Constant cast_Constant_value = cast_compute(c);
        return cast_Constant_value;
    }

    private Constant cast_compute(Constant c) {  return Constant.create(c.stringValue());  }

    // Declared in ConstantExpression.jrag at line 380
 @SuppressWarnings({"unchecked", "cast"})     public Constant add(Constant c1, Constant c2) {
        ASTNode$State state = state();
        Constant add_Constant_Constant_value = add_compute(c1, c2);
        return add_Constant_Constant_value;
    }

    private Constant add_compute(Constant c1, Constant c2) {  return Constant.create(c1.stringValue() + c2.stringValue());  }

    // Declared in ConstantExpression.jrag at line 445
 @SuppressWarnings({"unchecked", "cast"})     public Constant questionColon(Constant cond, Constant c1, Constant c2) {
        ASTNode$State state = state();
        Constant questionColon_Constant_Constant_Constant_value = questionColon_compute(cond, c1, c2);
        return questionColon_Constant_Constant_Constant_value;
    }

    private Constant questionColon_compute(Constant cond, Constant c1, Constant c2) {  return Constant.create(cond.booleanValue() ? c1.stringValue() : c2.stringValue());  }

    // Declared in ConstantExpression.jrag at line 549
 @SuppressWarnings({"unchecked", "cast"})     public boolean eqIsTrue(Expr left, Expr right) {
        ASTNode$State state = state();
        boolean eqIsTrue_Expr_Expr_value = eqIsTrue_compute(left, right);
        return eqIsTrue_Expr_Expr_value;
    }

    private boolean eqIsTrue_compute(Expr left, Expr right) {  return isString() && left.constant().stringValue().equals(right.constant().stringValue());  }

    // Declared in ErrorCheck.jrag at line 30
 @SuppressWarnings({"unchecked", "cast"})     public int lineNumber() {
        ASTNode$State state = state();
        int lineNumber_value = lineNumber_compute();
        return lineNumber_value;
    }

    private int lineNumber_compute() {  return getLine(IDstart);  }

    // Declared in LookupConstructor.jrag at line 22
 @SuppressWarnings({"unchecked", "cast"})     public Collection lookupSuperConstructor() {
        ASTNode$State state = state();
        Collection lookupSuperConstructor_value = lookupSuperConstructor_compute();
        return lookupSuperConstructor_value;
    }

    private Collection lookupSuperConstructor_compute() {  return hasSuperclass() ? superclass().constructors() : Collections.EMPTY_LIST;  }

    // Declared in LookupConstructor.jrag at line 208
 @SuppressWarnings({"unchecked", "cast"})     public boolean noConstructor() {
        ASTNode$State state = state();
        boolean noConstructor_value = noConstructor_compute();
        return noConstructor_value;
    }

    private boolean noConstructor_compute() {
    for(int i = 0; i < getNumBodyDecl(); i++)
      if(getBodyDecl(i) instanceof ConstructorDecl)
        return false;
    return true;
  }

    // Declared in LookupMethod.jrag at line 263
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet interfacesMethodsSignature(String signature) {
        ASTNode$State state = state();
        SimpleSet interfacesMethodsSignature_String_value = interfacesMethodsSignature_compute(signature);
        return interfacesMethodsSignature_String_value;
    }

    private SimpleSet interfacesMethodsSignature_compute(String signature) {
    SimpleSet set = (SimpleSet)interfacesMethodsSignatureMap().get(signature);
    if(set != null) return set;
    return SimpleSet.emptySet;
  }

    protected boolean interfacesMethodsSignatureMap_computed = false;
    protected HashMap interfacesMethodsSignatureMap_value;
    // Declared in LookupMethod.jrag at line 269
 @SuppressWarnings({"unchecked", "cast"})     public HashMap interfacesMethodsSignatureMap() {
        if(interfacesMethodsSignatureMap_computed) {
            return interfacesMethodsSignatureMap_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        interfacesMethodsSignatureMap_value = interfacesMethodsSignatureMap_compute();
        if(isFinal && num == state().boundariesCrossed)
            interfacesMethodsSignatureMap_computed = true;
        return interfacesMethodsSignatureMap_value;
    }

    private HashMap interfacesMethodsSignatureMap_compute() {
    HashMap map = new HashMap();
    for(Iterator iter = interfacesIterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (InterfaceDecl)iter.next();
      for(Iterator i2 = typeDecl.methodsIterator(); i2.hasNext(); ) {
        MethodDecl m = (MethodDecl)i2.next();
        putSimpleSetElement(map, m.signature(), m);
      }
    }
    return map;
  }

    // Declared in MethodSignature.jrag at line 344
 @SuppressWarnings({"unchecked", "cast"})     public HashMap methodsSignatureMap() {
        if(methodsSignatureMap_computed) {
            return methodsSignatureMap_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        methodsSignatureMap_value = methodsSignatureMap_compute();
        if(isFinal && num == state().boundariesCrossed)
            methodsSignatureMap_computed = true;
        return methodsSignatureMap_value;
    }

    private HashMap methodsSignatureMap_compute() {
    HashMap map = new HashMap(localMethodsSignatureMap());
    if(hasSuperclass()) {
      for(Iterator iter = superclass().methodsIterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        if(!m.isPrivate() && m.accessibleFrom(this) && !localMethodsSignatureMap().containsKey(m.signature())) {
          if(!(m instanceof MethodDeclSubstituted) || !localMethodsSignatureMap().containsKey(m.sourceMethodDecl().signature()))
            putSimpleSetElement(map, m.signature(), m);
        }
      }
    }
    for(Iterator outerIter = interfacesIterator(); outerIter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)outerIter.next();
      for(Iterator iter = typeDecl.methodsIterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        if(!m.isPrivate() && m.accessibleFrom(this) && !localMethodsSignatureMap().containsKey(m.signature())) {
          if(!(m instanceof MethodDeclSubstituted) || !localMethodsSignatureMap().containsKey(m.sourceMethodDecl().signature())) {
            if(allMethodsAbstract((SimpleSet)map.get(m.signature())) &&
              (!(m instanceof MethodDeclSubstituted) ||
               allMethodsAbstract((SimpleSet)map.get(m.sourceMethodDecl().signature()))              )
            )
              putSimpleSetElement(map, m.signature(), m);
          }
        }
      }
    }
    return map;
  }

    // Declared in LookupMethod.jrag at line 363
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet ancestorMethods(String signature) {
        Object _parameters = signature;
if(ancestorMethods_String_values == null) ancestorMethods_String_values = new java.util.HashMap(4);
        if(ancestorMethods_String_values.containsKey(_parameters)) {
            return (SimpleSet)ancestorMethods_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet ancestorMethods_String_value = ancestorMethods_compute(signature);
        if(isFinal && num == state().boundariesCrossed)
            ancestorMethods_String_values.put(_parameters, ancestorMethods_String_value);
        return ancestorMethods_String_value;
    }

    private SimpleSet ancestorMethods_compute(String signature) {
    SimpleSet set = SimpleSet.emptySet;
    if(hasSuperclass()) {
      for(Iterator iter = superclass().localMethodsSignature(signature).iterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        if(!m.isPrivate())
          set = set.add(m);
      }
    }
    if(set.size() != 1 || ((MethodDecl)set.iterator().next()).isAbstract()) { 
      for(Iterator iter = interfacesMethodsSignature(signature).iterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        set = set.add(m);
      }
    }
    if(!hasSuperclass()) return set;
    if(set.size() == 1) {
      MethodDecl m = (MethodDecl)set.iterator().next();
      if(!m.isAbstract()) {
        boolean done = true;
        for(Iterator iter = superclass().ancestorMethods(signature).iterator(); iter.hasNext(); ) {
          MethodDecl n = (MethodDecl)iter.next();
          if(n.isPrivate() || !n.accessibleFrom(m.hostType()))
            done = false;
        }
        if(done) return set;
      }
    }
    for(Iterator iter = superclass().ancestorMethods(signature).iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      set = set.add(m);
    }
    return set;
  }

    // Declared in LookupType.jrag at line 410
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet memberTypes(String name) {
        Object _parameters = name;
if(memberTypes_String_values == null) memberTypes_String_values = new java.util.HashMap(4);
        if(memberTypes_String_values.containsKey(_parameters)) {
            return (SimpleSet)memberTypes_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet memberTypes_String_value = memberTypes_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            memberTypes_String_values.put(_parameters, memberTypes_String_value);
        return memberTypes_String_value;
    }

    private SimpleSet memberTypes_compute(String name) {
    SimpleSet set = localTypeDecls(name);
    if(!set.isEmpty()) return set;
    for(Iterator outerIter = interfacesIterator(); outerIter.hasNext(); ) {
      TypeDecl type = (TypeDecl)outerIter.next();
      for(Iterator iter = type.memberTypes(name).iterator(); iter.hasNext(); ) {
        TypeDecl decl = (TypeDecl)iter.next();
        if(!decl.isPrivate() && decl.accessibleFrom(this))
          set = set.add(decl);
      }
    }
    if(hasSuperclass()) {
      for(Iterator iter = superclass().memberTypes(name).iterator(); iter.hasNext(); ) {
        TypeDecl decl = (TypeDecl)iter.next();
        if(!decl.isPrivate() && decl.accessibleFrom(this)) {
          set = set.add(decl);
        }
      }
    }
    return set;
  }

    // Declared in LookupVariable.jrag at line 272
 @SuppressWarnings({"unchecked", "cast"})     public HashMap memberFieldsMap() {
        if(memberFieldsMap_computed) {
            return memberFieldsMap_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        memberFieldsMap_value = memberFieldsMap_compute();
        if(isFinal && num == state().boundariesCrossed)
            memberFieldsMap_computed = true;
        return memberFieldsMap_value;
    }

    private HashMap memberFieldsMap_compute() {
    HashMap map = new HashMap(localFieldsMap());
    if(hasSuperclass()) {
      for(Iterator iter = superclass().fieldsIterator(); iter.hasNext(); ) {
        FieldDeclaration decl = (FieldDeclaration)iter.next();
        if(!decl.isPrivate() && decl.accessibleFrom(this) && !localFieldsMap().containsKey(decl.name()))
          putSimpleSetElement(map, decl.name(), decl);
      }
    }
    for(Iterator outerIter = interfacesIterator(); outerIter.hasNext(); ) {
      TypeDecl type = (TypeDecl)outerIter.next();
      for(Iterator iter = type.fieldsIterator(); iter.hasNext(); ) {
        FieldDeclaration decl = (FieldDeclaration)iter.next();
        if(!decl.isPrivate() && decl.accessibleFrom(this) && !localFieldsMap().containsKey(decl.name()))
          putSimpleSetElement(map, decl.name(), decl);
      }
    }
    return map;
  }

    // Declared in LookupVariable.jrag at line 323
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet memberFields(String name) {
        Object _parameters = name;
if(memberFields_String_values == null) memberFields_String_values = new java.util.HashMap(4);
        if(memberFields_String_values.containsKey(_parameters)) {
            return (SimpleSet)memberFields_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet memberFields_String_value = memberFields_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            memberFields_String_values.put(_parameters, memberFields_String_value);
        return memberFields_String_value;
    }

    private SimpleSet memberFields_compute(String name) {
    SimpleSet fields = localFields(name);
    if(!fields.isEmpty())
      return fields; // this causes hiding of fields in superclass and interfaces
    if(hasSuperclass()) {
      for(Iterator iter = superclass().memberFields(name).iterator(); iter.hasNext(); ) {
        FieldDeclaration decl = (FieldDeclaration)iter.next();
        if(!decl.isPrivate() && decl.accessibleFrom(this))
          fields = fields.add(decl);
      }
    }
    for(Iterator outerIter = interfacesIterator(); outerIter.hasNext(); ) {
      TypeDecl type = (TypeDecl)outerIter.next();
      for(Iterator iter = type.memberFields(name).iterator(); iter.hasNext(); ) {
        FieldDeclaration decl = (FieldDeclaration)iter.next();
        if(!decl.isPrivate() && decl.accessibleFrom(this))
          fields = fields.add(decl);
      }
    }
    return fields;
  }

    // Declared in Modifiers.jrag at line 17
 @SuppressWarnings({"unchecked", "cast"})     public Collection unimplementedMethods() {
        if(unimplementedMethods_computed) {
            return unimplementedMethods_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        unimplementedMethods_value = unimplementedMethods_compute();
        if(isFinal && num == state().boundariesCrossed)
            unimplementedMethods_computed = true;
        return unimplementedMethods_value;
    }

    private Collection unimplementedMethods_compute() {
    Collection c = new ArrayList();
    for(Iterator iter = interfacesMethodsIterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      boolean implemented = false;
      SimpleSet set = (SimpleSet)localMethodsSignature(m.signature());
      if(set.size() == 1) {
        MethodDecl n = (MethodDecl)set.iterator().next();
        if(!n.isAbstract())
          implemented = true;
      }
      if(!implemented) {
        set = (SimpleSet)ancestorMethods(m.signature());
        for(Iterator i2 = set.iterator(); i2.hasNext(); ) {
          MethodDecl n = (MethodDecl)i2.next();
          if(!n.isAbstract())
            implemented = true;
        }
      }
      if(!implemented) {
        c.add(m);
      }
    }

    if(hasSuperclass()) {
      for(Iterator iter = superclass().unimplementedMethods().iterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        SimpleSet set = (SimpleSet)localMethodsSignature(m.signature());
        if(set.size() == 1) {
          MethodDecl n = (MethodDecl)set.iterator().next();
          if(n.isAbstract() || !n.overrides(m))
            c.add(m);
        }
        else
          c.add(m);
      }
    }

    for(Iterator iter = localMethodsIterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(m.isAbstract()) {
        c.add(m);
      }
    }
    return c;
  }

    // Declared in Modifiers.jrag at line 64
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasAbstract() {
        if(hasAbstract_computed) {
            return hasAbstract_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        hasAbstract_value = hasAbstract_compute();
        if(isFinal && num == state().boundariesCrossed)
            hasAbstract_computed = true;
        return hasAbstract_value;
    }

    private boolean hasAbstract_compute() {  return !unimplementedMethods().isEmpty();  }

    // Declared in AutoBoxing.jrag at line 134
 @SuppressWarnings({"unchecked", "cast"})     public boolean castingConversionTo(TypeDecl type) {
        Object _parameters = type;
if(castingConversionTo_TypeDecl_values == null) castingConversionTo_TypeDecl_values = new java.util.HashMap(4);
        if(castingConversionTo_TypeDecl_values.containsKey(_parameters)) {
            return ((Boolean)castingConversionTo_TypeDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean castingConversionTo_TypeDecl_value = castingConversionTo_compute(type);
        if(isFinal && num == state().boundariesCrossed)
            castingConversionTo_TypeDecl_values.put(_parameters, Boolean.valueOf(castingConversionTo_TypeDecl_value));
        return castingConversionTo_TypeDecl_value;
    }

    private boolean castingConversionTo_compute(TypeDecl type) {
    if(refined_Generics_ClassDecl_castingConversionTo_TypeDecl(type))
      return true;
    boolean canUnboxThis = !unboxed().isUnknown();
    boolean canUnboxType = !type.unboxed().isUnknown();
    if(canUnboxThis && !canUnboxType)
      return unboxed().wideningConversionTo(type);
    return false;
    /*
    else if(unboxingConversionTo(type))
      return true;
    return false;
    */
  }

    // Declared in TypeAnalysis.jrag at line 210
 @SuppressWarnings({"unchecked", "cast"})     public boolean isClassDecl() {
        ASTNode$State state = state();
        boolean isClassDecl_value = isClassDecl_compute();
        return isClassDecl_value;
    }

    private boolean isClassDecl_compute() {  return true;  }

    // Declared in TypeAnalysis.jrag at line 225
 @SuppressWarnings({"unchecked", "cast"})     public boolean isString() {
        if(isString_computed) {
            return isString_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isString_value = isString_compute();
        if(isFinal && num == state().boundariesCrossed)
            isString_computed = true;
        return isString_value;
    }

    private boolean isString_compute() {  return fullName().equals("java.lang.String");  }

    // Declared in TypeAnalysis.jrag at line 228
 @SuppressWarnings({"unchecked", "cast"})     public boolean isObject() {
        if(isObject_computed) {
            return isObject_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isObject_value = isObject_compute();
        if(isFinal && num == state().boundariesCrossed)
            isObject_computed = true;
        return isObject_value;
    }

    private boolean isObject_compute() {  return name().equals("Object") && packageName().equals("java.lang");  }

    // Declared in GenericsSubtype.jrag at line 387
 @SuppressWarnings({"unchecked", "cast"})     public boolean instanceOf(TypeDecl type) {
        Object _parameters = type;
if(instanceOf_TypeDecl_values == null) instanceOf_TypeDecl_values = new java.util.HashMap(4);
        if(instanceOf_TypeDecl_values.containsKey(_parameters)) {
            return ((Boolean)instanceOf_TypeDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean instanceOf_TypeDecl_value = instanceOf_compute(type);
        if(isFinal && num == state().boundariesCrossed)
            instanceOf_TypeDecl_values.put(_parameters, Boolean.valueOf(instanceOf_TypeDecl_value));
        return instanceOf_TypeDecl_value;
    }

    private boolean instanceOf_compute(TypeDecl type) { return subtype(type); }

    // Declared in TypeAnalysis.jrag at line 424
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSupertypeOfClassDecl(ClassDecl type) {
        ASTNode$State state = state();
        boolean isSupertypeOfClassDecl_ClassDecl_value = isSupertypeOfClassDecl_compute(type);
        return isSupertypeOfClassDecl_ClassDecl_value;
    }

    private boolean isSupertypeOfClassDecl_compute(ClassDecl type) {
    if(super.isSupertypeOfClassDecl(type))
      return true;
    return type.hasSuperclass() && type.superclass() != null && type.superclass().instanceOf(this);
  }

    // Declared in TypeAnalysis.jrag at line 441
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSupertypeOfInterfaceDecl(InterfaceDecl type) {
        ASTNode$State state = state();
        boolean isSupertypeOfInterfaceDecl_InterfaceDecl_value = isSupertypeOfInterfaceDecl_compute(type);
        return isSupertypeOfInterfaceDecl_InterfaceDecl_value;
    }

    private boolean isSupertypeOfInterfaceDecl_compute(InterfaceDecl type) {  return isObject();  }

    // Declared in TypeAnalysis.jrag at line 454
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSupertypeOfArrayDecl(ArrayDecl type) {
        ASTNode$State state = state();
        boolean isSupertypeOfArrayDecl_ArrayDecl_value = isSupertypeOfArrayDecl_compute(type);
        return isSupertypeOfArrayDecl_ArrayDecl_value;
    }

    private boolean isSupertypeOfArrayDecl_compute(ArrayDecl type) {
    if(super.isSupertypeOfArrayDecl(type))
      return true;
    return type.hasSuperclass() && type.superclass() != null && type.superclass().instanceOf(this);
  }

    // Declared in TypeAnalysis.jrag at line 536
 @SuppressWarnings({"unchecked", "cast"})     public boolean isInnerClass() {
        ASTNode$State state = state();
        boolean isInnerClass_value = isInnerClass_compute();
        return isInnerClass_value;
    }

    private boolean isInnerClass_compute() {  return isNestedType() && !isStatic() && enclosingType().isClassDecl();  }

    // Declared in TypeAnalysis.jrag at line 674
 @SuppressWarnings({"unchecked", "cast"})     public boolean isCircular() {
        if(isCircular_computed) {
            return isCircular_value;
        }
        ASTNode$State state = state();
        if (!isCircular_initialized) {
            isCircular_initialized = true;
            isCircular_value = true;
        }
        if (!state.IN_CIRCLE) {
            state.IN_CIRCLE = true;
            int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
            do {
                isCircular_visited = state.CIRCLE_INDEX;
                state.CHANGE = false;
                boolean new_isCircular_value = isCircular_compute();
                if (new_isCircular_value!=isCircular_value)
                    state.CHANGE = true;
                isCircular_value = new_isCircular_value; 
                state.CIRCLE_INDEX++;
            } while (state.CHANGE);
            if(isFinal && num == state().boundariesCrossed)
{
            isCircular_computed = true;
            }
            else {
            state.RESET_CYCLE = true;
            isCircular_compute();
            state.RESET_CYCLE = false;
              isCircular_computed = false;
              isCircular_initialized = false;
            }
            state.IN_CIRCLE = false; 
            return isCircular_value;
        }
        if(isCircular_visited != state.CIRCLE_INDEX) {
            isCircular_visited = state.CIRCLE_INDEX;
            if (state.RESET_CYCLE) {
                isCircular_computed = false;
                isCircular_initialized = false;
                isCircular_visited = -1;
                return isCircular_value;
            }
            boolean new_isCircular_value = isCircular_compute();
            if (new_isCircular_value!=isCircular_value)
                state.CHANGE = true;
            isCircular_value = new_isCircular_value; 
            return isCircular_value;
        }
        return isCircular_value;
    }

    private boolean isCircular_compute() {
    if(hasSuperClassAccess()) {
      Access a = getSuperClassAccess().lastAccess();
      while(a != null) {
        if(a.type().isCircular())
          return true;
        a = (a.isQualified() && a.qualifier().isTypeAccess()) ? (Access)a.qualifier() : null;
      }
    }
    for(int i = 0; i < getNumImplements(); i++) {
      Access a = getImplements(i).lastAccess();
      while(a != null) {
        if(a.type().isCircular())
          return true;
        a = (a.isQualified() && a.qualifier().isTypeAccess()) ? (Access)a.qualifier() : null;
      }
    }
    return false;
  }

    // Declared in Annotations.jrag at line 228
 @SuppressWarnings({"unchecked", "cast"})     public Annotation annotation(TypeDecl typeDecl) {
        ASTNode$State state = state();
        Annotation annotation_TypeDecl_value = annotation_compute(typeDecl);
        return annotation_TypeDecl_value;
    }

    private Annotation annotation_compute(TypeDecl typeDecl) {
    Annotation a = super.annotation(typeDecl);
    if(a != null) return a;
    if(hasSuperclass()) {
      // If the queried annotation is itself annotation with @Inherited then
      // delegate the query to the superclass
      if(typeDecl.annotation(lookupType("java.lang.annotation", "Inherited")) != null)
        return superclass().annotation(typeDecl);
    }
    return null;
  }

    // Declared in Generics.jrag at line 367
 @SuppressWarnings({"unchecked", "cast"})     public HashSet implementedInterfaces() {
        if(implementedInterfaces_computed) {
            return implementedInterfaces_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        implementedInterfaces_value = implementedInterfaces_compute();
        if(isFinal && num == state().boundariesCrossed)
            implementedInterfaces_computed = true;
        return implementedInterfaces_value;
    }

    private HashSet implementedInterfaces_compute() {
    HashSet set = new HashSet();
    if(hasSuperclass())
      set.addAll(superclass().implementedInterfaces());
    for(Iterator iter = interfacesIterator(); iter.hasNext(); ) {
      InterfaceDecl decl = (InterfaceDecl)iter.next();
      set.add(decl);
      set.addAll(decl.implementedInterfaces());
    }
    return set;
  }

    // Declared in GenericsSubtype.jrag at line 407
 @SuppressWarnings({"unchecked", "cast"})     public boolean subtype(TypeDecl type) {
        Object _parameters = type;
if(subtype_TypeDecl_values == null) subtype_TypeDecl_values = new java.util.HashMap(4);
        ASTNode$State.CircularValue _value;
        if(subtype_TypeDecl_values.containsKey(_parameters)) {
            Object _o = subtype_TypeDecl_values.get(_parameters);
            if(!(_o instanceof ASTNode$State.CircularValue)) {
                return ((Boolean)_o).booleanValue();
            }
            else
                _value = (ASTNode$State.CircularValue)_o;
        }
        else {
            _value = new ASTNode$State.CircularValue();
            subtype_TypeDecl_values.put(_parameters, _value);
            _value.value = Boolean.valueOf(true);
        }
        ASTNode$State state = state();
        if (!state.IN_CIRCLE) {
            state.IN_CIRCLE = true;
            int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
            boolean new_subtype_TypeDecl_value;
            do {
                _value.visited = new Integer(state.CIRCLE_INDEX);
                state.CHANGE = false;
                new_subtype_TypeDecl_value = subtype_compute(type);
                if (new_subtype_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
                    state.CHANGE = true;
                    _value.value = Boolean.valueOf(new_subtype_TypeDecl_value);
                }
                state.CIRCLE_INDEX++;
            } while (state.CHANGE);
            if(isFinal && num == state().boundariesCrossed)
{
                subtype_TypeDecl_values.put(_parameters, new_subtype_TypeDecl_value);
            }
            else {
                subtype_TypeDecl_values.remove(_parameters);
            state.RESET_CYCLE = true;
            subtype_compute(type);
            state.RESET_CYCLE = false;
            }
            state.IN_CIRCLE = false; 
            return new_subtype_TypeDecl_value;
        }
        if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
            _value.visited = new Integer(state.CIRCLE_INDEX);
            boolean new_subtype_TypeDecl_value = subtype_compute(type);
            if (state.RESET_CYCLE) {
                subtype_TypeDecl_values.remove(_parameters);
            }
            else if (new_subtype_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
                state.CHANGE = true;
                _value.value = new_subtype_TypeDecl_value;
            }
            return new_subtype_TypeDecl_value;
        }
        return ((Boolean)_value.value).booleanValue();
    }

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeClassDecl(this);  }

    // Declared in GenericsSubtype.jrag at line 422
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeClassDecl(ClassDecl type) {
        ASTNode$State state = state();
        boolean supertypeClassDecl_ClassDecl_value = supertypeClassDecl_compute(type);
        return supertypeClassDecl_ClassDecl_value;
    }

    private boolean supertypeClassDecl_compute(ClassDecl type) {  return super.supertypeClassDecl(type) || 
    type.hasSuperclass() && type.superclass() != null && type.superclass().subtype(this);  }

    // Declared in GenericsSubtype.jrag at line 438
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeInterfaceDecl(InterfaceDecl type) {
        ASTNode$State state = state();
        boolean supertypeInterfaceDecl_InterfaceDecl_value = supertypeInterfaceDecl_compute(type);
        return supertypeInterfaceDecl_InterfaceDecl_value;
    }

    private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {  return isObject();  }

    // Declared in GenericsSubtype.jrag at line 451
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeArrayDecl(ArrayDecl type) {
        ASTNode$State state = state();
        boolean supertypeArrayDecl_ArrayDecl_value = supertypeArrayDecl_compute(type);
        return supertypeArrayDecl_ArrayDecl_value;
    }

    private boolean supertypeArrayDecl_compute(ArrayDecl type) {
    if(super.supertypeArrayDecl(type))
      return true;
    return type.hasSuperclass() && type.superclass() != null && type.superclass().subtype(this);
  }

    // Declared in GenericsCodegen.jrag at line 19
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl superEnclosing() {
        ASTNode$State state = state();
        TypeDecl superEnclosing_value = superEnclosing_compute();
        return superEnclosing_value;
    }

    private TypeDecl superEnclosing_compute() {
    return superclass().erasure().enclosing();
  }

    // Declared in IncrementalJimple.jrag at line 35
 @SuppressWarnings({"unchecked", "cast"})     public SootClass sootClass() {
        if(sootClass_computed) {
            return sootClass_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sootClass_value = sootClass_compute();
        if(isFinal && num == state().boundariesCrossed)
            sootClass_computed = true;
        return sootClass_value;
    }

    private SootClass sootClass_compute() {
		if(!Scene.v().isIncrementalBuild()) {
			return refined_EmitJimpleRefinements_ClassDecl_sootClass();
		}
			
	    if(Scene.v().containsClass(jvmName())) {
			Scene.v().removeClass(Scene.v().getSootClass(jvmName()));
		}
	
	    SootClass sc = null;
	    if(options().verbose())
	    	System.out.println("Creating from source " + jvmName());        
	    sc = new SootClass(jvmName());
	    sc.setResolvingLevel(SootClass.DANGLING);
		Scene.v().addClass(sc);
	    return sc;
	}

    // Declared in AnnotationsCodegen.jrag at line 323
 @SuppressWarnings({"unchecked", "cast"})     public String typeDescriptor() {
        ASTNode$State state = state();
        String typeDescriptor_value = typeDescriptor_compute();
        return typeDescriptor_value;
    }

    private String typeDescriptor_compute() {  return "L" + jvmName().replace('.', '/') + ";";  }

    // Declared in GenericsCodegen.jrag at line 335
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet bridgeCandidates(String signature) {
        ASTNode$State state = state();
        SimpleSet bridgeCandidates_String_value = bridgeCandidates_compute(signature);
        return bridgeCandidates_String_value;
    }

    private SimpleSet bridgeCandidates_compute(String signature) {
    SimpleSet set = ancestorMethods(signature);
    for(Iterator iter = interfacesMethodsSignature(signature).iterator(); iter.hasNext(); )
      set = set.add(iter.next());
    return set;
  }

    // Declared in Modifiers.jrag at line 257
    public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return true;
        }
        return super.Define_boolean_mayBeFinal(caller, child);
    }

    // Declared in SyntacticClassification.jrag at line 74
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getImplementsListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        if(caller == getSuperClassAccessOptNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return super.Define_NameType_nameType(caller, child);
    }

    // Declared in TypeAnalysis.jrag at line 576
    public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
        if(caller == getImplementsListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return hostType();
        }
        if(caller == getSuperClassAccessOptNoTransform()) {
            return hostType();
        }
        return super.Define_TypeDecl_hostType(caller, child);
    }

    // Declared in Annotations.jrag at line 276
    public boolean Define_boolean_withinSuppressWarnings(ASTNode caller, ASTNode child, String s) {
        if(caller == getImplementsListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return hasAnnotationSuppressWarnings(s) || withinSuppressWarnings(s);
        }
        if(caller == getSuperClassAccessOptNoTransform()) {
            return hasAnnotationSuppressWarnings(s) || withinSuppressWarnings(s);
        }
        return super.Define_boolean_withinSuppressWarnings(caller, child, s);
    }

    // Declared in Annotations.jrag at line 377
    public boolean Define_boolean_withinDeprecatedAnnotation(ASTNode caller, ASTNode child) {
        if(caller == getImplementsListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return isDeprecated() || withinDeprecatedAnnotation();
        }
        if(caller == getSuperClassAccessOptNoTransform()) {
            return isDeprecated() || withinDeprecatedAnnotation();
        }
        return super.Define_boolean_withinDeprecatedAnnotation(caller, child);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
