
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;


public class GenericClassDeclSubstituted extends GenericClassDecl implements Cloneable, MemberSubstitutor {
    public void flushCache() {
        super.flushCache();
        sourceTypeDecl_computed = false;
        sourceTypeDecl_value = null;
        instanceOf_TypeDecl_values = null;
        subtype_TypeDecl_visited = new java.util.HashMap(4);
        localMethodsSignatureMap_computed = false;
        localMethodsSignatureMap_value = null;
        localFields_String_values = null;
        localTypeDecls_String_values = null;
        constructors_computed = false;
        constructors_value = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericClassDeclSubstituted clone() throws CloneNotSupportedException {
        GenericClassDeclSubstituted node = (GenericClassDeclSubstituted)super.clone();
        node.sourceTypeDecl_computed = false;
        node.sourceTypeDecl_value = null;
        node.instanceOf_TypeDecl_values = null;
        node.subtype_TypeDecl_visited = new java.util.HashMap(4);
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
     @SuppressWarnings({"unchecked", "cast"})  public GenericClassDeclSubstituted copy() {
      try {
          GenericClassDeclSubstituted node = (GenericClassDeclSubstituted)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericClassDeclSubstituted fullCopy() {
        GenericClassDeclSubstituted res = (GenericClassDeclSubstituted)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 35

    public GenericClassDeclSubstituted() {
        super();

        setChild(new Opt(), 1);
        setChild(new List(), 2);
        setChild(new List(), 3);
        setChild(new List(), 4);
        setChild(new List(), 5);

    }

    // Declared in Generics.ast at line 15


    // Declared in Generics.ast line 35
    public GenericClassDeclSubstituted(Modifiers p0, String p1, Opt<Access> p2, List<Access> p3, List<BodyDecl> p4, List<TypeVariable> p5, TypeDecl p6) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
        setOriginal(p6);
        setChild(new List(), 5);
    }

    // Declared in Generics.ast at line 27


    // Declared in Generics.ast line 35
    public GenericClassDeclSubstituted(Modifiers p0, beaver.Symbol p1, Opt<Access> p2, List<Access> p3, List<BodyDecl> p4, List<TypeVariable> p5, TypeDecl p6) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(p5, 4);
        setOriginal(p6);
        setChild(new List(), 5);
    }

    // Declared in Generics.ast at line 38


  protected int numChildren() {
    return 5;
  }

    // Declared in Generics.ast at line 41

  public boolean mayHaveRewrite() { return false; }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 2
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in Generics.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in Generics.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 2
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in Generics.ast at line 5

    public int IDstart;

    // Declared in Generics.ast at line 6

    public int IDend;

    // Declared in Generics.ast at line 7

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in Generics.ast at line 14

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 2
    public void setSuperClassAccessOpt(Opt<Access> opt) {
        setChild(opt, 1);
    }

    // Declared in Generics.ast at line 6


    public boolean hasSuperClassAccess() {
        return getSuperClassAccessOpt().getNumChild() != 0;
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getSuperClassAccess() {
        return (Access)getSuperClassAccessOpt().getChild(0);
    }

    // Declared in Generics.ast at line 14


    public void setSuperClassAccess(Access node) {
        getSuperClassAccessOpt().setChild(node, 0);
    }

    // Declared in Generics.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Access> getSuperClassAccessOpt() {
        return (Opt<Access>)getChild(1);
    }

    // Declared in Generics.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Access> getSuperClassAccessOptNoTransform() {
        return (Opt<Access>)getChildNoTransform(1);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 2
    public void setImplementsList(List<Access> list) {
        setChild(list, 2);
    }

    // Declared in Generics.ast at line 6


    private int getNumImplements = 0;

    // Declared in Generics.ast at line 7

    public int getNumImplements() {
        return getImplementsList().getNumChild();
    }

    // Declared in Generics.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public Access getImplements(int i) {
        return (Access)getImplementsList().getChild(i);
    }

    // Declared in Generics.ast at line 15


    public void addImplements(Access node) {
        List<Access> list = getImplementsList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 20


    public void setImplements(Access node, int i) {
        List<Access> list = getImplementsList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 24

    public List<Access> getImplementss() {
        return getImplementsList();
    }

    // Declared in Generics.ast at line 27

    public List<Access> getImplementssNoTransform() {
        return getImplementsListNoTransform();
    }

    // Declared in Generics.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getImplementsList() {
        return (List<Access>)getChild(2);
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getImplementsListNoTransform() {
        return (List<Access>)getChildNoTransform(2);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 2
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 3);
    }

    // Declared in Generics.ast at line 6


    private int getNumBodyDecl = 0;

    // Declared in Generics.ast at line 7

    public int getNumBodyDecl() {
        return getBodyDeclList().getNumChild();
    }

    // Declared in Generics.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public BodyDecl getBodyDecl(int i) {
        return (BodyDecl)getBodyDeclList().getChild(i);
    }

    // Declared in Generics.ast at line 15


    public void addBodyDecl(BodyDecl node) {
        List<BodyDecl> list = getBodyDeclList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 20


    public void setBodyDecl(BodyDecl node, int i) {
        List<BodyDecl> list = getBodyDeclList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 24

    public List<BodyDecl> getBodyDecls() {
        return getBodyDeclList();
    }

    // Declared in Generics.ast at line 27

    public List<BodyDecl> getBodyDeclsNoTransform() {
        return getBodyDeclListNoTransform();
    }

    // Declared in Generics.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclList() {
        return (List<BodyDecl>)getChild(3);
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(3);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 2
    public void setTypeParameterList(List<TypeVariable> list) {
        setChild(list, 4);
    }

    // Declared in Generics.ast at line 6


    private int getNumTypeParameter = 0;

    // Declared in Generics.ast at line 7

    public int getNumTypeParameter() {
        return getTypeParameterList().getNumChild();
    }

    // Declared in Generics.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public TypeVariable getTypeParameter(int i) {
        return (TypeVariable)getTypeParameterList().getChild(i);
    }

    // Declared in Generics.ast at line 15


    public void addTypeParameter(TypeVariable node) {
        List<TypeVariable> list = getTypeParameterList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 20


    public void setTypeParameter(TypeVariable node, int i) {
        List<TypeVariable> list = getTypeParameterList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 24

    public List<TypeVariable> getTypeParameters() {
        return getTypeParameterList();
    }

    // Declared in Generics.ast at line 27

    public List<TypeVariable> getTypeParametersNoTransform() {
        return getTypeParameterListNoTransform();
    }

    // Declared in Generics.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<TypeVariable> getTypeParameterList() {
        return (List<TypeVariable>)getChild(4);
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<TypeVariable> getTypeParameterListNoTransform() {
        return (List<TypeVariable>)getChildNoTransform(4);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 35
    protected TypeDecl tokenTypeDecl_Original;

    // Declared in Generics.ast at line 3

    public void setOriginal(TypeDecl value) {
        tokenTypeDecl_Original = value;
    }

    // Declared in Generics.ast at line 6

    public TypeDecl getOriginal() {
        return tokenTypeDecl_Original;
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 2
    public void setParTypeDeclList(List<ParClassDecl> list) {
        setChild(list, 5);
    }

    // Declared in Generics.ast at line 6


    private int getNumParTypeDecl = 0;

    // Declared in Generics.ast at line 7

    public int getNumParTypeDecl() {
        return getParTypeDeclList().getNumChild();
    }

    // Declared in Generics.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public ParClassDecl getParTypeDecl(int i) {
        return (ParClassDecl)getParTypeDeclList().getChild(i);
    }

    // Declared in Generics.ast at line 15


    public void addParTypeDecl(ParClassDecl node) {
        List<ParClassDecl> list = getParTypeDeclList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 20


    public void setParTypeDecl(ParClassDecl node, int i) {
        List<ParClassDecl> list = getParTypeDeclList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 24

    public List<ParClassDecl> getParTypeDecls() {
        return getParTypeDeclList();
    }

    // Declared in Generics.ast at line 27

    public List<ParClassDecl> getParTypeDeclsNoTransform() {
        return getParTypeDeclListNoTransform();
    }

    // Declared in Generics.ast at line 31


    public List<ParClassDecl> getParTypeDeclListNoTransform() {
        return (List<ParClassDecl>)getChildNoTransform(5);
    }

    // Declared in Generics.ast at line 35


    protected int getParTypeDeclListChildPosition() {
        return 5;
    }

    // Declared in Generics.jrag at line 1098
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl original() {
        TypeDecl original_value = original_compute();
        return original_value;
    }

    private TypeDecl original_compute() {  return getOriginal().original();  }

    // Declared in Generics.jrag at line 1296
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl sourceTypeDecl() {
        if(sourceTypeDecl_computed)
            return sourceTypeDecl_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        sourceTypeDecl_value = sourceTypeDecl_compute();
        if(isFinal && num == boundariesCrossed)
            sourceTypeDecl_computed = true;
        return sourceTypeDecl_value;
    }

    private TypeDecl sourceTypeDecl_compute() {  return original().sourceTypeDecl();  }

    // Declared in GenericsSubtype.jrag at line 488
 @SuppressWarnings({"unchecked", "cast"})     public boolean instanceOf(TypeDecl type) {
        Object _parameters = type;
if(instanceOf_TypeDecl_values == null) instanceOf_TypeDecl_values = new java.util.HashMap(4);
        if(instanceOf_TypeDecl_values.containsKey(_parameters))
            return ((Boolean)instanceOf_TypeDecl_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean instanceOf_TypeDecl_value = instanceOf_compute(type);
        if(isFinal && num == boundariesCrossed)
            instanceOf_TypeDecl_values.put(_parameters, Boolean.valueOf(instanceOf_TypeDecl_value));
        return instanceOf_TypeDecl_value;
    }

    private boolean instanceOf_compute(TypeDecl type) {  return subtype(type);  }

    protected java.util.Map subtype_TypeDecl_visited;
    protected java.util.Set subtype_TypeDecl_computed = new java.util.HashSet(4);
    protected java.util.Set subtype_TypeDecl_initialized = new java.util.HashSet(4);
    protected java.util.Map subtype_TypeDecl_values = new java.util.HashMap(4);
 @SuppressWarnings({"unchecked", "cast"})     public boolean subtype(TypeDecl type) {
        Object _parameters = type;
if(subtype_TypeDecl_visited == null) subtype_TypeDecl_visited = new java.util.HashMap(4);
if(subtype_TypeDecl_values == null) subtype_TypeDecl_values = new java.util.HashMap(4);
        if(subtype_TypeDecl_computed.contains(_parameters))
            return ((Boolean)subtype_TypeDecl_values.get(_parameters)).booleanValue();
        if (!subtype_TypeDecl_initialized.contains(_parameters)) {
            subtype_TypeDecl_initialized.add(_parameters);
            subtype_TypeDecl_values.put(_parameters, Boolean.valueOf(true));
        }
        if (!IN_CIRCLE) {
            IN_CIRCLE = true;
            int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
            CIRCLE_INDEX = 1;
            boolean new_subtype_TypeDecl_value;
            do {
                subtype_TypeDecl_visited.put(_parameters, new Integer(CIRCLE_INDEX));
                CHANGE = false;
                new_subtype_TypeDecl_value = subtype_compute(type);
                if (new_subtype_TypeDecl_value!=((Boolean)subtype_TypeDecl_values.get(_parameters)).booleanValue())
                    CHANGE = true;
                subtype_TypeDecl_values.put(_parameters, Boolean.valueOf(new_subtype_TypeDecl_value));
                CIRCLE_INDEX++;
            } while (CHANGE);
            if(isFinal && num == boundariesCrossed)
{
            subtype_TypeDecl_computed.add(_parameters);
            }
            else {
            RESET_CYCLE = true;
            subtype_compute(type);
            RESET_CYCLE = false;
            subtype_TypeDecl_computed.remove(_parameters);
            subtype_TypeDecl_initialized.remove(_parameters);
            }
            IN_CIRCLE = false; 
            return new_subtype_TypeDecl_value;
        }
        if(!new Integer(CIRCLE_INDEX).equals(subtype_TypeDecl_visited.get(_parameters))) {
            subtype_TypeDecl_visited.put(_parameters, new Integer(CIRCLE_INDEX));
            if (RESET_CYCLE) {
                subtype_TypeDecl_computed.remove(_parameters);
                subtype_TypeDecl_initialized.remove(_parameters);
                return ((Boolean)subtype_TypeDecl_values.get(_parameters)).booleanValue();
            }
            boolean new_subtype_TypeDecl_value = subtype_compute(type);
            if (new_subtype_TypeDecl_value!=((Boolean)subtype_TypeDecl_values.get(_parameters)).booleanValue())
                CHANGE = true;
            subtype_TypeDecl_values.put(_parameters, Boolean.valueOf(new_subtype_TypeDecl_value));
            return new_subtype_TypeDecl_value;
        }
        return ((Boolean)subtype_TypeDecl_values.get(_parameters)).booleanValue();
    }

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeGenericClassDeclSubstituted(this);  }

    // Declared in GenericsSubtype.jrag at line 515
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeGenericClassDeclSubstituted(GenericClassDeclSubstituted type) {
        boolean supertypeGenericClassDeclSubstituted_GenericClassDeclSubstituted_value = supertypeGenericClassDeclSubstituted_compute(type);
        return supertypeGenericClassDeclSubstituted_GenericClassDeclSubstituted_value;
    }

    private boolean supertypeGenericClassDeclSubstituted_compute(GenericClassDeclSubstituted type) {  return original() == type.original() && type.enclosingType().subtype(enclosingType()) || super.supertypeGenericClassDeclSubstituted(type);  }

    // Declared in GenericsSubtype.jrag at line 518
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeGenericClassDecl(GenericClassDecl type) {
        boolean supertypeGenericClassDecl_GenericClassDecl_value = supertypeGenericClassDecl_compute(type);
        return supertypeGenericClassDecl_GenericClassDecl_value;
    }

    private boolean supertypeGenericClassDecl_compute(GenericClassDecl type) {  return super.supertypeGenericClassDecl(type) || original().supertypeGenericClassDecl(type);  }

    // Declared in Generics.jrag at line 925
 @SuppressWarnings({"unchecked", "cast"})     public HashMap localMethodsSignatureMap() {
        if(localMethodsSignatureMap_computed)
            return localMethodsSignatureMap_value;
        int num = boundariesCrossed;
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
        if(localFields_String_values.containsKey(_parameters))
            return (SimpleSet)localFields_String_values.get(_parameters);
        int num = boundariesCrossed;
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
        if(localTypeDecls_String_values.containsKey(_parameters))
            return (SimpleSet)localTypeDecls_String_values.get(_parameters);
        int num = boundariesCrossed;
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
        if(constructors_computed)
            return constructors_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        constructors_value = constructors_compute();
        if(isFinal && num == boundariesCrossed)
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
