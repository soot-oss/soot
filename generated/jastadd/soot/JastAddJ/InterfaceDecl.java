
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class InterfaceDecl extends ReferenceType implements Cloneable {
    public void flushCache() {
        super.flushCache();
        methodsSignatureMap_computed = false;
        methodsSignatureMap_value = null;
        ancestorMethods_String_values = null;
        memberTypes_String_values = null;
        memberFieldsMap_computed = false;
        memberFieldsMap_value = null;
        memberFields_String_values = null;
        isStatic_computed = false;
        castingConversionTo_TypeDecl_values = null;
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
     @SuppressWarnings({"unchecked", "cast"})  public InterfaceDecl clone() throws CloneNotSupportedException {
        InterfaceDecl node = (InterfaceDecl)super.clone();
        node.methodsSignatureMap_computed = false;
        node.methodsSignatureMap_value = null;
        node.ancestorMethods_String_values = null;
        node.memberTypes_String_values = null;
        node.memberFieldsMap_computed = false;
        node.memberFieldsMap_value = null;
        node.memberFields_String_values = null;
        node.isStatic_computed = false;
        node.castingConversionTo_TypeDecl_values = null;
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
     @SuppressWarnings({"unchecked", "cast"})  public InterfaceDecl copy() {
      try {
          InterfaceDecl node = (InterfaceDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public InterfaceDecl fullCopy() {
        InterfaceDecl res = (InterfaceDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in AccessControl.jrag at line 167


  public void accessControl() {
    super.accessControl();
    
    if(!isCircular()) {
      // 9.1.2
      HashSet set = new HashSet();
      for(int i = 0; i < getNumSuperInterfaceId(); i++) {
        TypeDecl decl = getSuperInterfaceId(i).type();

        if(!decl.isInterfaceDecl() && !decl.isUnknown())
          error("interface " + fullName() + " tries to extend non interface type " + decl.fullName());
        if(!decl.isCircular() && !decl.accessibleFrom(this))
          error("interface " + fullName() + " can not extend non accessible type " + decl.fullName());

        if(set.contains(decl))
          error("extended interface " + decl.fullName() + " mentionened multiple times in extends clause");
        set.add(decl);
      }
    }
  }

    // Declared in Modifiers.jrag at line 104

  
  public void checkModifiers() {
    super.checkModifiers();
  }

    // Declared in PrettyPrint.jadd at line 85

  
  public void toString(StringBuffer s) {
    s.append(indent());
    getModifiers().toString(s);
    s.append("interface " + name());
    if(getNumSuperInterfaceId() > 0) {
      s.append(" extends ");
      getSuperInterfaceId(0).toString(s);
      for(int i = 1; i < getNumSuperInterfaceId(); i++) {
        s.append(", ");
        getSuperInterfaceId(i).toString(s);
      }
    }
    s.append(" {");
    for(int i=0; i < getNumBodyDecl(); i++) {
      getBodyDecl(i).toString(s);
    }
    s.append(indent() + "}");
  }

    // Declared in TypeAnalysis.jrag at line 641

  
  public Iterator superinterfacesIterator() {
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
        if(isCircular()) return;
        while(index < getNumSuperInterfaceId()) {
          TypeDecl typeDecl = getSuperInterfaceId(index++).type();
          if(!typeDecl.isCircular() && typeDecl.isInterfaceDecl()) {
            current = typeDecl;
            return;
          }
        }
      }
    };
  }

    // Declared in TypeHierarchyCheck.jrag at line 312


  public void nameCheck() {
    super.nameCheck();
    if(isCircular())
      error("circular inheritance dependency in " + typeName()); 
    else {
      for(int i = 0; i < getNumSuperInterfaceId(); i++) {
        TypeDecl typeDecl = getSuperInterfaceId(i).type();
        if(typeDecl.isCircular())
          error("circular inheritance dependency in " + typeName()); 
      }
    }
    for(Iterator iter = methodsSignatureMap().values().iterator(); iter.hasNext(); ) {
      SimpleSet set = (SimpleSet)iter.next();
      if(set.size() > 1) {
        Iterator i2 = set.iterator();
        MethodDecl m = (MethodDecl)i2.next();
        while(i2.hasNext()) {
          MethodDecl n = (MethodDecl)i2.next();
          if(!n.mayOverrideReturn(m) && !m.mayOverrideReturn(n))
            error("multiply inherited methods with the same signature must have the same return type");
        }
      }
    }
  }

    // Declared in Generics.jrag at line 189


  public TypeDecl makeGeneric(Signatures.ClassSignature s) {
    if(s.hasFormalTypeParameters()) {
      ASTNode node = getParent();
      int index = node.getIndexOfChild(this);
      node.setChild(
          new GenericInterfaceDecl(
            getModifiersNoTransform(),
            getID(),
            s.hasSuperinterfaceSignature() ? s.superinterfaceSignature() : getSuperInterfaceIdListNoTransform(),
            getBodyDeclListNoTransform(),
            s.typeParameters()
          ),
          index
      );
      return (TypeDecl)node.getChildNoTransform(index);
    }
    else {
      if(s.hasSuperinterfaceSignature())
        setSuperInterfaceIdList(s.superinterfaceSignature());
      return this;
    }
  }

    // Declared in Generics.jrag at line 1091

  public InterfaceDecl p(Parameterization parTypeDecl) {
    InterfaceDecl c = new InterfaceDeclSubstituted(
      (Modifiers)getModifiers().fullCopy(),
      getID(),
      getSuperInterfaceIdList().substitute(parTypeDecl),
      new List(),
      this
    );
    return c;
  }

    // Declared in Java2Rewrites.jrag at line 100


  public FieldDeclaration createStaticClassField(String name) {
    return methodHolder().createStaticClassField(name);
  }

    // Declared in Java2Rewrites.jrag at line 103

  public MethodDecl createStaticClassMethod() {
    return methodHolder().createStaticClassMethod();
  }

    // Declared in Java2Rewrites.jrag at line 107

  // create anonymous class to delegate to
  private TypeDecl methodHolder = null;

    // Declared in Java2Rewrites.jrag at line 108

  public TypeDecl methodHolder() {
    if(methodHolder != null)
      return methodHolder;
    String name = "$" + nextAnonymousIndex();
    ClassDecl c = addMemberClass(new ClassDecl(
      new Modifiers(new List()),
      name,
      new Opt(),
      new List(),
      new List()
    ));
    methodHolder = c;
    return c;
  }

    // Declared in EmitJimple.jrag at line 186


  public void jimplify1phase2() {
    SootClass sc = getSootClassDecl();
    sc.setResolvingLevel(SootClass.DANGLING);
    sc.setModifiers(sootTypeModifiers());
    sc.setApplicationClass();
    SourceFileTag st = new soot.tagkit.SourceFileTag(sourceNameWithoutPath());
    st.setAbsolutePath(new File(sourceFile()).getAbsolutePath());
    sc.addTag(st);
    sc.setSuperclass(typeObject().getSootClassDecl());
    for(Iterator iter = superinterfacesIterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      if(typeDecl != typeObject() && !sc.implementsInterface(typeDecl.getSootClassDecl().getName()))
        sc.addInterface(typeDecl.getSootClassDecl());
    }
    if(isNestedType())
      sc.setOuterClass(enclosingType().getSootClassDecl());
    sc.setResolvingLevel(SootClass.HIERARCHY);
    super.jimplify1phase2();
    sc.setResolvingLevel(SootClass.SIGNATURES);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 64

    public InterfaceDecl() {
        super();

        setChild(new List(), 1);
        setChild(new List(), 2);

    }

    // Declared in java.ast at line 12


    // Declared in java.ast line 64
    public InterfaceDecl(Modifiers p0, String p1, List<Access> p2, List<BodyDecl> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in java.ast at line 20


    // Declared in java.ast line 64
    public InterfaceDecl(Modifiers p0, beaver.Symbol p1, List<Access> p2, List<BodyDecl> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in java.ast at line 27


  protected int numChildren() {
    return 3;
  }

    // Declared in java.ast at line 30

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 64
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
    // Declared in java.ast line 64
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
    // Declared in java.ast line 64
    public void setSuperInterfaceIdList(List<Access> list) {
        setChild(list, 1);
    }

    // Declared in java.ast at line 6


    public int getNumSuperInterfaceId() {
        return getSuperInterfaceIdList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getSuperInterfaceId(int i) {
        return (Access)getSuperInterfaceIdList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addSuperInterfaceId(Access node) {
        List<Access> list = (parent == null || state == null) ? getSuperInterfaceIdListNoTransform() : getSuperInterfaceIdList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addSuperInterfaceIdNoTransform(Access node) {
        List<Access> list = getSuperInterfaceIdListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setSuperInterfaceId(Access node, int i) {
        List<Access> list = getSuperInterfaceIdList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Access> getSuperInterfaceIds() {
        return getSuperInterfaceIdList();
    }

    // Declared in java.ast at line 31

    public List<Access> getSuperInterfaceIdsNoTransform() {
        return getSuperInterfaceIdListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getSuperInterfaceIdList() {
        List<Access> list = (List<Access>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getSuperInterfaceIdListNoTransform() {
        return (List<Access>)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 64
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 2);
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
        List<BodyDecl> list = (List<BodyDecl>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(2);
    }

    // Declared in Generics.jrag at line 53
private boolean refined_Generics_InterfaceDecl_castingConversionTo_TypeDecl(TypeDecl type)
{
    TypeDecl S = this;
    TypeDecl T = type;
    if(T.isArrayDecl())
      return T.instanceOf(S);
    else if(T.isReferenceType() && !T.isFinal()) {
      return true;
    }
    else {
      return T.instanceOf(S);
    }
  }

    // Declared in LookupConstructor.jrag at line 23
 @SuppressWarnings({"unchecked", "cast"})     public Collection lookupSuperConstructor() {
        ASTNode$State state = state();
        Collection lookupSuperConstructor_value = lookupSuperConstructor_compute();
        return lookupSuperConstructor_value;
    }

    private Collection lookupSuperConstructor_compute() {  return typeObject().constructors();  }

    // Declared in MethodSignature.jrag at line 372
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
    for(Iterator outerIter = superinterfacesIterator(); outerIter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)outerIter.next();
      for(Iterator iter = typeDecl.methodsIterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        if(!m.isPrivate() && m.accessibleFrom(this) && !localMethodsSignatureMap().containsKey(m.signature()))
          if(!(m instanceof MethodDeclSubstituted) || !localMethodsSignatureMap().containsKey(m.sourceMethodDecl().signature()))
            putSimpleSetElement(map, m.signature(), m);
      }
    }
    for(Iterator iter = typeObject().methodsIterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(m.isPublic() && !map.containsKey(m.signature()))
        putSimpleSetElement(map, m.signature(), m);
    }
    return map;
  }

    // Declared in LookupMethod.jrag at line 397
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
    for(Iterator outerIter = superinterfacesIterator(); outerIter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)outerIter.next();
      for(Iterator iter = typeDecl.methodsSignature(signature).iterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        set = set.add(m);
      }
    }
    if(!superinterfacesIterator().hasNext()) {
      for(Iterator iter = typeObject().methodsSignature(signature).iterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        if(m.isPublic())
          set = set.add(m);
      }
    }
    return set;
  }

    // Declared in LookupType.jrag at line 432
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
    for(Iterator outerIter = superinterfacesIterator(); outerIter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)outerIter.next();
      for(Iterator iter = typeDecl.memberTypes(name).iterator(); iter.hasNext(); ) {
        TypeDecl decl = (TypeDecl)iter.next();
        if(!decl.isPrivate())
          set = set.add(decl);
      }
    }
    return set;
  }

    // Declared in LookupVariable.jrag at line 291
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
    for(Iterator outerIter = superinterfacesIterator(); outerIter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)outerIter.next();
      for(Iterator iter = typeDecl.fieldsIterator(); iter.hasNext(); ) {
        FieldDeclaration f = (FieldDeclaration)iter.next();
        if(f.accessibleFrom(this) && !f.isPrivate() && !localFieldsMap().containsKey(f.name())) {
          putSimpleSetElement(map, f.name(), f);
        }
      }
    }
    return map;
  }

    // Declared in LookupVariable.jrag at line 345
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
      return fields;
    for(Iterator outerIter = superinterfacesIterator(); outerIter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)outerIter.next();
      for(Iterator iter = typeDecl.memberFields(name).iterator(); iter.hasNext(); ) {
        FieldDeclaration f = (FieldDeclaration)iter.next();
        if(f.accessibleFrom(this) && !f.isPrivate()) {
          fields = fields.add(f);
        }
      }
    }
    return fields;
  }

    // Declared in Modifiers.jrag at line 203
 @SuppressWarnings({"unchecked", "cast"})     public boolean isAbstract() {
        ASTNode$State state = state();
        boolean isAbstract_value = isAbstract_compute();
        return isAbstract_value;
    }

    private boolean isAbstract_compute() {  return true;  }

    // Declared in Modifiers.jrag at line 206
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStatic() {
        if(isStatic_computed) {
            return isStatic_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isStatic_value = isStatic_compute();
        if(isFinal && num == state().boundariesCrossed)
            isStatic_computed = true;
        return isStatic_value;
    }

    private boolean isStatic_compute() {  return getModifiers().isStatic() || isMemberType();  }

    // Declared in AutoBoxing.jrag at line 148
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
    if(refined_Generics_InterfaceDecl_castingConversionTo_TypeDecl(type))
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

    // Declared in TypeAnalysis.jrag at line 212
 @SuppressWarnings({"unchecked", "cast"})     public boolean isInterfaceDecl() {
        ASTNode$State state = state();
        boolean isInterfaceDecl_value = isInterfaceDecl_compute();
        return isInterfaceDecl_value;
    }

    private boolean isInterfaceDecl_compute() {  return true;  }

    // Declared in GenericsSubtype.jrag at line 388
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

    // Declared in TypeAnalysis.jrag at line 429
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSupertypeOfClassDecl(ClassDecl type) {
        ASTNode$State state = state();
        boolean isSupertypeOfClassDecl_ClassDecl_value = isSupertypeOfClassDecl_compute(type);
        return isSupertypeOfClassDecl_ClassDecl_value;
    }

    private boolean isSupertypeOfClassDecl_compute(ClassDecl type) {
    if(super.isSupertypeOfClassDecl(type))
      return true;
    for(Iterator iter = type.interfacesIterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      if(typeDecl.instanceOf(this))
        return true;
    }
    return type.hasSuperclass() && type.superclass() != null && type.superclass().instanceOf(this);
  }

    // Declared in TypeAnalysis.jrag at line 442
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSupertypeOfInterfaceDecl(InterfaceDecl type) {
        ASTNode$State state = state();
        boolean isSupertypeOfInterfaceDecl_InterfaceDecl_value = isSupertypeOfInterfaceDecl_compute(type);
        return isSupertypeOfInterfaceDecl_InterfaceDecl_value;
    }

    private boolean isSupertypeOfInterfaceDecl_compute(InterfaceDecl type) {
    if(super.isSupertypeOfInterfaceDecl(type))
      return true;
    for(Iterator iter = type.superinterfacesIterator(); iter.hasNext(); ) {
      TypeDecl superinterface = (TypeDecl)iter.next();
      if(superinterface.instanceOf(this))
        return true;
    }
    return false;
  }

    // Declared in TypeAnalysis.jrag at line 459
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSupertypeOfArrayDecl(ArrayDecl type) {
        ASTNode$State state = state();
        boolean isSupertypeOfArrayDecl_ArrayDecl_value = isSupertypeOfArrayDecl_compute(type);
        return isSupertypeOfArrayDecl_ArrayDecl_value;
    }

    private boolean isSupertypeOfArrayDecl_compute(ArrayDecl type) {
    if(super.isSupertypeOfArrayDecl(type))
      return true;
    for(Iterator iter = type.interfacesIterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      if(typeDecl.instanceOf(this))
        return true;
    }
    return false;
  }

    // Declared in TypeAnalysis.jrag at line 693
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
    for(int i = 0; i < getNumSuperInterfaceId(); i++) {
      Access a = getSuperInterfaceId(i).lastAccess();
      while(a != null) {
        if(a.type().isCircular())
          return true;
        a = (a.isQualified() && a.qualifier().isTypeAccess()) ? (Access)a.qualifier() : null;
      }
    }
    return false;
  }

    // Declared in Generics.jrag at line 378
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
    HashSet set= new HashSet();
    set.addAll(typeObject().implementedInterfaces());
    for(Iterator iter = superinterfacesIterator(); iter.hasNext(); ) {
      InterfaceDecl decl = (InterfaceDecl)iter.next();
      set.add(decl);
      set.addAll(decl.implementedInterfaces());
    }
    return set;
  }

    // Declared in GenericsSubtype.jrag at line 408
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

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeInterfaceDecl(this);  }

    // Declared in GenericsSubtype.jrag at line 426
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeClassDecl(ClassDecl type) {
        ASTNode$State state = state();
        boolean supertypeClassDecl_ClassDecl_value = supertypeClassDecl_compute(type);
        return supertypeClassDecl_ClassDecl_value;
    }

    private boolean supertypeClassDecl_compute(ClassDecl type) {
    if(super.supertypeClassDecl(type))
      return true;
    for(Iterator iter = type.interfacesIterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      if(typeDecl.subtype(this))
        return true;
    }
    return type.hasSuperclass() && type.superclass() != null && type.superclass().subtype(this);
  }

    // Declared in GenericsSubtype.jrag at line 439
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeInterfaceDecl(InterfaceDecl type) {
        ASTNode$State state = state();
        boolean supertypeInterfaceDecl_InterfaceDecl_value = supertypeInterfaceDecl_compute(type);
        return supertypeInterfaceDecl_InterfaceDecl_value;
    }

    private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {
    if(super.supertypeInterfaceDecl(type))
      return true;
    for(Iterator iter = type.superinterfacesIterator(); iter.hasNext(); ) {
      TypeDecl superinterface = (TypeDecl)iter.next();
      if(superinterface.subtype(this))
        return true;
    }
    return false;
  }

    // Declared in GenericsSubtype.jrag at line 456
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeArrayDecl(ArrayDecl type) {
        ASTNode$State state = state();
        boolean supertypeArrayDecl_ArrayDecl_value = supertypeArrayDecl_compute(type);
        return supertypeArrayDecl_ArrayDecl_value;
    }

    private boolean supertypeArrayDecl_compute(ArrayDecl type) {
    if(super.supertypeArrayDecl(type))
      return true;
    for(Iterator iter = type.interfacesIterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      if(typeDecl.subtype(this))
        return true;
    }
    return false;
  }

    // Declared in EmitJimpleRefinements.jrag at line 55
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
    if(options().verbose())
      System.out.println("Creating from source " + jvmName());
    SootClass sc = SootResolver.v().makeClassRef(jvmName());
    sc.setModifiers(sootTypeModifiers()); // turn it into an interface
    return sc;
  }

    // Declared in EmitJimple.jrag at line 105
 @SuppressWarnings({"unchecked", "cast"})     public int sootTypeModifiers() {
        ASTNode$State state = state();
        int sootTypeModifiers_value = sootTypeModifiers_compute();
        return sootTypeModifiers_value;
    }

    private int sootTypeModifiers_compute() {  return super.sootTypeModifiers() | soot.Modifier.INTERFACE;  }

    // Declared in AnnotationsCodegen.jrag at line 324
 @SuppressWarnings({"unchecked", "cast"})     public String typeDescriptor() {
        ASTNode$State state = state();
        String typeDescriptor_value = typeDescriptor_compute();
        return typeDescriptor_value;
    }

    private String typeDescriptor_compute() {  return "L" + jvmName().replace('.', '/') + ";";  }

    // Declared in GenericsCodegen.jrag at line 334
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet bridgeCandidates(String signature) {
        ASTNode$State state = state();
        SimpleSet bridgeCandidates_String_value = bridgeCandidates_compute(signature);
        return bridgeCandidates_String_value;
    }

    private SimpleSet bridgeCandidates_compute(String signature) {  return ancestorMethods(signature);  }

    // Declared in TypeAnalysis.jrag at line 97
 @SuppressWarnings({"unchecked", "cast"})     public MethodDecl unknownMethod() {
        ASTNode$State state = state();
        MethodDecl unknownMethod_value = getParent().Define_MethodDecl_unknownMethod(this, null);
        return unknownMethod_value;
    }

    // Declared in SyntacticClassification.jrag at line 75
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getSuperInterfaceIdListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return NameType.TYPE_NAME;
        }
        return super.Define_NameType_nameType(caller, child);
    }

    // Declared in TypeAnalysis.jrag at line 577
    public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
        if(caller == getSuperInterfaceIdListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return hostType();
        }
        return super.Define_TypeDecl_hostType(caller, child);
    }

    // Declared in Annotations.jrag at line 278
    public boolean Define_boolean_withinSuppressWarnings(ASTNode caller, ASTNode child, String s) {
        if(caller == getSuperInterfaceIdListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return hasAnnotationSuppressWarnings(s) || withinSuppressWarnings(s);
        }
        return super.Define_boolean_withinSuppressWarnings(caller, child, s);
    }

    // Declared in Annotations.jrag at line 378
    public boolean Define_boolean_withinDeprecatedAnnotation(ASTNode caller, ASTNode child) {
        if(caller == getSuperInterfaceIdListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return isDeprecated() || withinDeprecatedAnnotation();
        }
        return super.Define_boolean_withinDeprecatedAnnotation(caller, child);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
