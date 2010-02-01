
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class InterfaceDeclSubstituted extends InterfaceDecl implements Cloneable, MemberSubstitutor {
    public void flushCache() {
        super.flushCache();
        sourceTypeDecl_computed = false;
        sourceTypeDecl_value = null;
        instanceOf_TypeDecl_values = null;
        subtype_TypeDecl_values = null;
        localMethodsSignatureMap_computed = false;
        localMethodsSignatureMap_value = null;
        localFields_String_values = null;
        localTypeDecls_String_values = null;
        constructors_computed = false;
        constructors_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public InterfaceDeclSubstituted clone() throws CloneNotSupportedException {
        InterfaceDeclSubstituted node = (InterfaceDeclSubstituted)super.clone();
        node.sourceTypeDecl_computed = false;
        node.sourceTypeDecl_value = null;
        node.instanceOf_TypeDecl_values = null;
        node.subtype_TypeDecl_values = null;
        node.localMethodsSignatureMap_computed = false;
        node.localMethodsSignatureMap_value = null;
        node.localFields_String_values = null;
        node.localTypeDecls_String_values = null;
        node.constructors_computed = false;
        node.constructors_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public InterfaceDeclSubstituted copy() {
      try {
          InterfaceDeclSubstituted node = (InterfaceDeclSubstituted)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public InterfaceDeclSubstituted fullCopy() {
        InterfaceDeclSubstituted res = (InterfaceDeclSubstituted)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 34

    public InterfaceDeclSubstituted() {
        super();

        setChild(new List(), 1);
        setChild(new List(), 2);

    }

    // Declared in Generics.ast at line 12


    // Declared in Generics.ast line 34
    public InterfaceDeclSubstituted(Modifiers p0, String p1, List<Access> p2, List<BodyDecl> p3, TypeDecl p4) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setOriginal(p4);
    }

    // Declared in Generics.ast at line 21


    // Declared in Generics.ast line 34
    public InterfaceDeclSubstituted(Modifiers p0, beaver.Symbol p1, List<Access> p2, List<BodyDecl> p3, TypeDecl p4) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setOriginal(p4);
    }

    // Declared in Generics.ast at line 29


  protected int numChildren() {
    return 3;
  }

    // Declared in Generics.ast at line 32

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

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 34
    protected TypeDecl tokenTypeDecl_Original;

    // Declared in Generics.ast at line 3

    public void setOriginal(TypeDecl value) {
        tokenTypeDecl_Original = value;
    }

    // Declared in Generics.ast at line 6

    public TypeDecl getOriginal() {
        return tokenTypeDecl_Original;
    }

    // Declared in Generics.jrag at line 1063
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl original() {
        ASTNode$State state = state();
        TypeDecl original_value = original_compute();
        return original_value;
    }

    private TypeDecl original_compute() {  return getOriginal().original();  }

    // Declared in Generics.jrag at line 1261
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl sourceTypeDecl() {
        if(sourceTypeDecl_computed) {
            return sourceTypeDecl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        sourceTypeDecl_value = sourceTypeDecl_compute();
        if(isFinal && num == state().boundariesCrossed)
            sourceTypeDecl_computed = true;
        return sourceTypeDecl_value;
    }

    private TypeDecl sourceTypeDecl_compute() {  return original().sourceTypeDecl();  }

    // Declared in GenericsSubtype.jrag at line 487
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

    private boolean instanceOf_compute(TypeDecl type) {  return subtype(type);  }

    // Declared in GenericsSubtype.jrag at line 501
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

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeInterfaceDeclSubstituted(this);  }

    // Declared in GenericsSubtype.jrag at line 505
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeInterfaceDeclSubstituted(InterfaceDeclSubstituted type) {
        ASTNode$State state = state();
        boolean supertypeInterfaceDeclSubstituted_InterfaceDeclSubstituted_value = supertypeInterfaceDeclSubstituted_compute(type);
        return supertypeInterfaceDeclSubstituted_InterfaceDeclSubstituted_value;
    }

    private boolean supertypeInterfaceDeclSubstituted_compute(InterfaceDeclSubstituted type) {  return original() == type.original() && type.enclosingType().subtype(enclosingType()) || super.supertypeInterfaceDeclSubstituted(type);  }

    // Declared in GenericsSubtype.jrag at line 508
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeInterfaceDecl(InterfaceDecl type) {
        ASTNode$State state = state();
        boolean supertypeInterfaceDecl_InterfaceDecl_value = supertypeInterfaceDecl_compute(type);
        return supertypeInterfaceDecl_InterfaceDecl_value;
    }

    private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {  return super.supertypeInterfaceDecl(type) || original().supertypeInterfaceDecl(type);  }

    // Declared in Generics.jrag at line 925
 @SuppressWarnings({"unchecked", "cast"})     public HashMap localMethodsSignatureMap() {
        if(localMethodsSignatureMap_computed) {
            return localMethodsSignatureMap_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        localMethodsSignatureMap_value = localMethodsSignatureMap_compute();
        if(true)
            localMethodsSignatureMap_computed = true;
        return localMethodsSignatureMap_value;
    }

    private HashMap localMethodsSignatureMap_compute() {
    HashMap map = new HashMap();
    for(Iterator iter = original().localMethodsIterator(); iter.hasNext(); ) {
      MethodDecl decl = (MethodDecl)iter.next();
      if(!decl.isStatic() && (decl.usesTypeVariable() || isRawType())) {
        BodyDecl b = decl.p(this);
        b.is$Final = true;
        addBodyDecl(b);
        decl = (MethodDecl)b;
      }
      map.put(decl.signature(), decl);
    }
    return map;
  }

    // Declared in Generics.jrag at line 940
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet localFields(String name) {
        Object _parameters = name;
if(localFields_String_values == null) localFields_String_values = new java.util.HashMap(4);
        if(localFields_String_values.containsKey(_parameters)) {
            return (SimpleSet)localFields_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet localFields_String_value = localFields_compute(name);
        if(true)
            localFields_String_values.put(_parameters, localFields_String_value);
        return localFields_String_value;
    }

    private SimpleSet localFields_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(Iterator iter = original().localFields(name).iterator(); iter.hasNext(); ) {
      FieldDeclaration f = (FieldDeclaration)iter.next();
      if(!f.isStatic() && (f.usesTypeVariable() || isRawType())) {
        BodyDecl b = f.p(this);
        b.is$Final = true;
        addBodyDecl(b);
        f = (FieldDeclaration)b;
      }
      set = set.add(f);
    }
    return set;
  }

    // Declared in Generics.jrag at line 955
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet localTypeDecls(String name) {
        Object _parameters = name;
if(localTypeDecls_String_values == null) localTypeDecls_String_values = new java.util.HashMap(4);
        if(localTypeDecls_String_values.containsKey(_parameters)) {
            return (SimpleSet)localTypeDecls_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet localTypeDecls_String_value = localTypeDecls_compute(name);
        if(true)
            localTypeDecls_String_values.put(_parameters, localTypeDecls_String_value);
        return localTypeDecls_String_value;
    }

    private SimpleSet localTypeDecls_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(Iterator iter = original().localTypeDecls(name).iterator(); iter.hasNext(); ) {
      TypeDecl t = (TypeDecl)iter.next();
      if(t.isStatic())
        set = set.add(t);
      else {
        BodyDecl b;
        TypeDecl typeDecl;
        if(t instanceof ClassDecl) {
          ClassDecl classDecl = (ClassDecl)t;
          typeDecl = classDecl.p(this);
          b = new MemberClassDecl((ClassDecl)typeDecl);
          b.is$Final = true;
          addBodyDecl(b);
          set = set.add(typeDecl);
        }
        else if(t instanceof InterfaceDecl) {
          InterfaceDecl interfaceDecl = (InterfaceDecl)t;
          typeDecl = interfaceDecl.p(this);
          b = new MemberInterfaceDecl((InterfaceDecl)typeDecl);
          b.is$Final = true;
          addBodyDecl(b);
          set = set.add(typeDecl);
        }
      }
    }
    return set;
  }

    // Declared in Generics.jrag at line 985
 @SuppressWarnings({"unchecked", "cast"})     public Collection constructors() {
        if(constructors_computed) {
            return constructors_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        constructors_value = constructors_compute();
        if(isFinal && num == state().boundariesCrossed)
            constructors_computed = true;
        return constructors_value;
    }

    private Collection constructors_compute() {
    Collection set = new ArrayList();
    for(Iterator iter = original().constructors().iterator(); iter.hasNext(); ) {
      ConstructorDecl c = (ConstructorDecl)iter.next();
      BodyDecl b = c.p(this);
      b.is$Final = true;
      addBodyDecl(b);
      set.add(b);
    }
    return set;
  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
