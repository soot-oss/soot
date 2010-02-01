
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class ArrayDecl extends ClassDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        accessibleFrom_TypeDecl_values = null;
        dimension_computed = false;
        elementType_computed = false;
        elementType_value = null;
        fullName_computed = false;
        fullName_value = null;
        typeName_computed = false;
        typeName_value = null;
        castingConversionTo_TypeDecl_values = null;
        instanceOf_TypeDecl_values = null;
        involvesTypeParameters_visited = -1;
        involvesTypeParameters_computed = false;
        involvesTypeParameters_initialized = false;
        erasure_computed = false;
        erasure_value = null;
        usesTypeVariable_visited = -1;
        usesTypeVariable_computed = false;
        usesTypeVariable_initialized = false;
        subtype_TypeDecl_values = null;
        jvmName_computed = false;
        jvmName_value = null;
        getSootClassDecl_computed = false;
        getSootClassDecl_value = null;
        getSootType_computed = false;
        getSootType_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ArrayDecl clone() throws CloneNotSupportedException {
        ArrayDecl node = (ArrayDecl)super.clone();
        node.accessibleFrom_TypeDecl_values = null;
        node.dimension_computed = false;
        node.elementType_computed = false;
        node.elementType_value = null;
        node.fullName_computed = false;
        node.fullName_value = null;
        node.typeName_computed = false;
        node.typeName_value = null;
        node.castingConversionTo_TypeDecl_values = null;
        node.instanceOf_TypeDecl_values = null;
        node.involvesTypeParameters_visited = -1;
        node.involvesTypeParameters_computed = false;
        node.involvesTypeParameters_initialized = false;
        node.erasure_computed = false;
        node.erasure_value = null;
        node.usesTypeVariable_visited = -1;
        node.usesTypeVariable_computed = false;
        node.usesTypeVariable_initialized = false;
        node.subtype_TypeDecl_values = null;
        node.jvmName_computed = false;
        node.jvmName_value = null;
        node.getSootClassDecl_computed = false;
        node.getSootClassDecl_value = null;
        node.getSootType_computed = false;
        node.getSootType_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ArrayDecl copy() {
      try {
          ArrayDecl node = (ArrayDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ArrayDecl fullCopy() {
        ArrayDecl res = (ArrayDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Arrays.jrag at line 59


  public Access createQualifiedAccess() {
    return new ArrayTypeAccess(componentType().createQualifiedAccess());
  }

    // Declared in Generics.jrag at line 733

  public Access substitute(Parameterization parTypeDecl) {
    return new ArrayTypeAccess(componentType().substitute(parTypeDecl));
  }

    // Declared in Generics.jrag at line 769

  public Access substituteReturnType(Parameterization parTypeDecl) {
    return new ArrayTypeAccess(componentType().substituteReturnType(parTypeDecl));
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 65

    public ArrayDecl() {
        super();

        setChild(new Opt(), 1);
        setChild(new List(), 2);
        setChild(new List(), 3);

    }

    // Declared in java.ast at line 13


    // Declared in java.ast line 65
    public ArrayDecl(Modifiers p0, String p1, Opt<Access> p2, List<Access> p3, List<BodyDecl> p4) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
    }

    // Declared in java.ast at line 22


    // Declared in java.ast line 65
    public ArrayDecl(Modifiers p0, beaver.Symbol p1, Opt<Access> p2, List<Access> p3, List<BodyDecl> p4) {
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

    // Declared in TypeAnalysis.jrag at line 120
private boolean refined_TypeConversion_ArrayDecl_castingConversionTo_TypeDecl(TypeDecl type)
{
    if(type.isArrayDecl()) {
      TypeDecl SC = componentType();
      TypeDecl TC = type.componentType();
      if(SC.isPrimitiveType() && TC.isPrimitiveType() && SC == TC)
        return true;
      if(SC.isReferenceType() && TC.isReferenceType()) {
        return SC.castingConversionTo(TC);
      }
      return false;
    }
    else if(type.isClassDecl()) {
      return type.isObject();
    }
    else if(type.isInterfaceDecl()) {
      return type == typeSerializable() || type == typeCloneable();
    }
    else return super.castingConversionTo(type);
  }

    // Declared in AccessControl.jrag at line 13
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

    private boolean accessibleFrom_compute(TypeDecl type) {  return elementType().accessibleFrom(type);  }

    // Declared in Arrays.jrag at line 12
 @SuppressWarnings({"unchecked", "cast"})     public int dimension() {
        if(dimension_computed) {
            return dimension_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        dimension_value = dimension_compute();
        if(isFinal && num == state().boundariesCrossed)
            dimension_computed = true;
        return dimension_value;
    }

    private int dimension_compute() {  return componentType().dimension() + 1;  }

    // Declared in Arrays.jrag at line 16
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl elementType() {
        if(elementType_computed) {
            return elementType_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        elementType_value = elementType_compute();
        if(isFinal && num == state().boundariesCrossed)
            elementType_computed = true;
        return elementType_value;
    }

    private TypeDecl elementType_compute() {  return componentType().elementType();  }

    // Declared in Arrays.jrag at line 53
 @SuppressWarnings({"unchecked", "cast"})     public String name() {
        ASTNode$State state = state();
        String name_value = name_compute();
        return name_value;
    }

    private String name_compute() {  return fullName();  }

    // Declared in Arrays.jrag at line 54
 @SuppressWarnings({"unchecked", "cast"})     public String fullName() {
        if(fullName_computed) {
            return fullName_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        fullName_value = fullName_compute();
        if(isFinal && num == state().boundariesCrossed)
            fullName_computed = true;
        return fullName_value;
    }

    private String fullName_compute() {  return getID();  }

    // Declared in QualifiedNames.jrag at line 87
 @SuppressWarnings({"unchecked", "cast"})     public String typeName() {
        if(typeName_computed) {
            return typeName_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        typeName_value = typeName_compute();
        if(isFinal && num == state().boundariesCrossed)
            typeName_computed = true;
        return typeName_value;
    }

    private String typeName_compute() {  return componentType().typeName() + "[]";  }

    // Declared in Generics.jrag at line 76
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
    TypeDecl S = this;
    TypeDecl T = type;
    if(T instanceof TypeVariable) {
      TypeVariable t = (TypeVariable)T;
      if(!type.isReferenceType())
        return false;
      if(t.getNumTypeBound() == 0) return true;
      for(int i = 0; i < t.getNumTypeBound(); i++) {
        TypeDecl bound = t.getTypeBound(i).type();
        if(bound.isObject() || bound == typeSerializable() || bound == typeCloneable())
          return true;
        if(bound.isTypeVariable() && castingConversionTo(bound))
          return true;
        if(bound.isArrayDecl() && castingConversionTo(bound))
          return true;
      }
      return false;
    }
    else
      return refined_TypeConversion_ArrayDecl_castingConversionTo_TypeDecl(type);
  }

    // Declared in TypeAnalysis.jrag at line 214
 @SuppressWarnings({"unchecked", "cast"})     public boolean isArrayDecl() {
        ASTNode$State state = state();
        boolean isArrayDecl_value = isArrayDecl_compute();
        return isArrayDecl_value;
    }

    private boolean isArrayDecl_compute() {  return true;  }

    // Declared in GenericsSubtype.jrag at line 389
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

    // Declared in TypeAnalysis.jrag at line 469
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSupertypeOfArrayDecl(ArrayDecl type) {
        ASTNode$State state = state();
        boolean isSupertypeOfArrayDecl_ArrayDecl_value = isSupertypeOfArrayDecl_compute(type);
        return isSupertypeOfArrayDecl_ArrayDecl_value;
    }

    private boolean isSupertypeOfArrayDecl_compute(ArrayDecl type) {
    if(type.elementType().isPrimitive() && elementType().isPrimitive())
      return type.dimension() == dimension() && type.elementType() == elementType();
    return type.componentType().instanceOf(componentType());
  }

    // Declared in Annotations.jrag at line 133
 @SuppressWarnings({"unchecked", "cast"})     public boolean isValidAnnotationMethodReturnType() {
        ASTNode$State state = state();
        boolean isValidAnnotationMethodReturnType_value = isValidAnnotationMethodReturnType_compute();
        return isValidAnnotationMethodReturnType_value;
    }

    private boolean isValidAnnotationMethodReturnType_compute() {  return componentType().isValidAnnotationMethodReturnType();  }

    // Declared in Annotations.jrag at line 492
 @SuppressWarnings({"unchecked", "cast"})     public boolean commensurateWith(ElementValue value) {
        ASTNode$State state = state();
        boolean commensurateWith_ElementValue_value = commensurateWith_compute(value);
        return commensurateWith_ElementValue_value;
    }

    private boolean commensurateWith_compute(ElementValue value) {  return value.commensurateWithArrayDecl(this);  }

    // Declared in GenericMethodsInference.jrag at line 17
 @SuppressWarnings({"unchecked", "cast"})     public boolean involvesTypeParameters() {
        if(involvesTypeParameters_computed) {
            return involvesTypeParameters_value;
        }
        ASTNode$State state = state();
        if (!involvesTypeParameters_initialized) {
            involvesTypeParameters_initialized = true;
            involvesTypeParameters_value = false;
        }
        if (!state.IN_CIRCLE) {
            state.IN_CIRCLE = true;
            int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
            do {
                involvesTypeParameters_visited = state.CIRCLE_INDEX;
                state.CHANGE = false;
                boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
                if (new_involvesTypeParameters_value!=involvesTypeParameters_value)
                    state.CHANGE = true;
                involvesTypeParameters_value = new_involvesTypeParameters_value; 
                state.CIRCLE_INDEX++;
            } while (state.CHANGE);
            if(isFinal && num == state().boundariesCrossed)
{
            involvesTypeParameters_computed = true;
            }
            else {
            state.RESET_CYCLE = true;
            involvesTypeParameters_compute();
            state.RESET_CYCLE = false;
              involvesTypeParameters_computed = false;
              involvesTypeParameters_initialized = false;
            }
            state.IN_CIRCLE = false; 
            return involvesTypeParameters_value;
        }
        if(involvesTypeParameters_visited != state.CIRCLE_INDEX) {
            involvesTypeParameters_visited = state.CIRCLE_INDEX;
            if (state.RESET_CYCLE) {
                involvesTypeParameters_computed = false;
                involvesTypeParameters_initialized = false;
                involvesTypeParameters_visited = -1;
                return involvesTypeParameters_value;
            }
            boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
            if (new_involvesTypeParameters_value!=involvesTypeParameters_value)
                state.CHANGE = true;
            involvesTypeParameters_value = new_involvesTypeParameters_value; 
            return involvesTypeParameters_value;
        }
        return involvesTypeParameters_value;
    }

    private boolean involvesTypeParameters_compute() {  return componentType().involvesTypeParameters();  }

    // Declared in Generics.jrag at line 320
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl erasure() {
        if(erasure_computed) {
            return erasure_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        erasure_value = erasure_compute();
        if(isFinal && num == state().boundariesCrossed)
            erasure_computed = true;
        return erasure_value;
    }

    private TypeDecl erasure_compute() {  return componentType().erasure().arrayType();  }

    // Declared in Generics.jrag at line 923
 @SuppressWarnings({"unchecked", "cast"})     public boolean usesTypeVariable() {
        if(usesTypeVariable_computed) {
            return usesTypeVariable_value;
        }
        ASTNode$State state = state();
        if (!usesTypeVariable_initialized) {
            usesTypeVariable_initialized = true;
            usesTypeVariable_value = false;
        }
        if (!state.IN_CIRCLE) {
            state.IN_CIRCLE = true;
            int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
            do {
                usesTypeVariable_visited = state.CIRCLE_INDEX;
                state.CHANGE = false;
                boolean new_usesTypeVariable_value = usesTypeVariable_compute();
                if (new_usesTypeVariable_value!=usesTypeVariable_value)
                    state.CHANGE = true;
                usesTypeVariable_value = new_usesTypeVariable_value; 
                state.CIRCLE_INDEX++;
            } while (state.CHANGE);
            if(isFinal && num == state().boundariesCrossed)
{
            usesTypeVariable_computed = true;
            }
            else {
            state.RESET_CYCLE = true;
            usesTypeVariable_compute();
            state.RESET_CYCLE = false;
              usesTypeVariable_computed = false;
              usesTypeVariable_initialized = false;
            }
            state.IN_CIRCLE = false; 
            return usesTypeVariable_value;
        }
        if(usesTypeVariable_visited != state.CIRCLE_INDEX) {
            usesTypeVariable_visited = state.CIRCLE_INDEX;
            if (state.RESET_CYCLE) {
                usesTypeVariable_computed = false;
                usesTypeVariable_initialized = false;
                usesTypeVariable_visited = -1;
                return usesTypeVariable_value;
            }
            boolean new_usesTypeVariable_value = usesTypeVariable_compute();
            if (new_usesTypeVariable_value!=usesTypeVariable_value)
                state.CHANGE = true;
            usesTypeVariable_value = new_usesTypeVariable_value; 
            return usesTypeVariable_value;
        }
        return usesTypeVariable_value;
    }

    private boolean usesTypeVariable_compute() {  return elementType().usesTypeVariable();  }

    // Declared in GenericsSubtype.jrag at line 409
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

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeArrayDecl(this);  }

    // Declared in GenericsSubtype.jrag at line 466
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeArrayDecl(ArrayDecl type) {
        ASTNode$State state = state();
        boolean supertypeArrayDecl_ArrayDecl_value = supertypeArrayDecl_compute(type);
        return supertypeArrayDecl_ArrayDecl_value;
    }

    private boolean supertypeArrayDecl_compute(ArrayDecl type) {
    if(type.elementType().isPrimitive() && elementType().isPrimitive())
      return type.dimension() == dimension() && type.elementType() == elementType();
    return type.componentType().subtype(componentType());
  }

    // Declared in Java2Rewrites.jrag at line 26
 @SuppressWarnings({"unchecked", "cast"})     public String jvmName() {
        if(jvmName_computed) {
            return jvmName_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        jvmName_value = jvmName_compute();
        if(isFinal && num == state().boundariesCrossed)
            jvmName_computed = true;
        return jvmName_value;
    }

    private String jvmName_compute() {
    StringBuffer dim = new StringBuffer();
    for(int i = 0; i < dimension(); i++)
      dim.append("[");
    if(elementType().isReferenceType())
      return dim.toString() + "L" + elementType().jvmName() + ";";
    else
      return dim.toString() + elementType().jvmName(); 
  }

    // Declared in Java2Rewrites.jrag at line 61
 @SuppressWarnings({"unchecked", "cast"})     public String referenceClassFieldName() {
        ASTNode$State state = state();
        String referenceClassFieldName_value = referenceClassFieldName_compute();
        return referenceClassFieldName_value;
    }

    private String referenceClassFieldName_compute() {  return "array" + jvmName().replace('[', '$').replace('.', '$').replace(';', ' ').trim();  }

    // Declared in EmitJimple.jrag at line 44
 @SuppressWarnings({"unchecked", "cast"})     public SootClass getSootClassDecl() {
        if(getSootClassDecl_computed) {
            return getSootClassDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getSootClassDecl_value = getSootClassDecl_compute();
        if(isFinal && num == state().boundariesCrossed)
            getSootClassDecl_computed = true;
        return getSootClassDecl_value;
    }

    private SootClass getSootClassDecl_compute() {  return typeObject().getSootClassDecl();  }

    // Declared in EmitJimple.jrag at line 56
 @SuppressWarnings({"unchecked", "cast"})     public Type getSootType() {
        if(getSootType_computed) {
            return getSootType_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getSootType_value = getSootType_compute();
        if(isFinal && num == state().boundariesCrossed)
            getSootType_computed = true;
        return getSootType_value;
    }

    private Type getSootType_compute() {  return soot.ArrayType.v(elementType().getSootType(), dimension());  }

    // Declared in TypeAnalysis.jrag at line 140
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeSerializable() {
        ASTNode$State state = state();
        TypeDecl typeSerializable_value = getParent().Define_TypeDecl_typeSerializable(this, null);
        return typeSerializable_value;
    }

    // Declared in TypeAnalysis.jrag at line 141
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeCloneable() {
        ASTNode$State state = state();
        TypeDecl typeCloneable_value = getParent().Define_TypeDecl_typeCloneable(this, null);
        return typeCloneable_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
