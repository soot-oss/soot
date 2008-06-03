
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;


public class WildcardType extends AbstractWildcardType implements Cloneable {
    public void flushCache() {
        super.flushCache();
        subtype_TypeDecl_visited = new java.util.HashMap(4);
        containedIn_TypeDecl_visited = new java.util.HashMap(4);
        instanceOf_TypeDecl_values = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WildcardType clone() throws CloneNotSupportedException {
        WildcardType node = (WildcardType)super.clone();
        node.subtype_TypeDecl_visited = new java.util.HashMap(4);
        node.containedIn_TypeDecl_visited = new java.util.HashMap(4);
        node.instanceOf_TypeDecl_values = null;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WildcardType copy() {
      try {
          WildcardType node = (WildcardType)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WildcardType fullCopy() {
        WildcardType res = (WildcardType)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 23

    public WildcardType() {
        super();

        setChild(new List(), 1);

    }

    // Declared in Generics.ast at line 11


    // Declared in Generics.ast line 23
    public WildcardType(Modifiers p0, String p1, List<BodyDecl> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
    }

    // Declared in Generics.ast at line 18


    // Declared in Generics.ast line 23
    public WildcardType(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
    }

    // Declared in Generics.ast at line 24


  protected int numChildren() {
    return 2;
  }

    // Declared in Generics.ast at line 27

  public boolean mayHaveRewrite() { return false; }

    // Declared in java.ast at line 2
    // Declared in java.ast line 38
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
    // Declared in java.ast line 38
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public int IDstart;

    // Declared in java.ast at line 6

    public int IDend;

    // Declared in java.ast at line 7

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 14

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 38
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 1);
    }

    // Declared in java.ast at line 6


    private int getNumBodyDecl = 0;

    // Declared in java.ast at line 7

    public int getNumBodyDecl() {
        return getBodyDeclList().getNumChild();
    }

    // Declared in java.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public BodyDecl getBodyDecl(int i) {
        return (BodyDecl)getBodyDeclList().getChild(i);
    }

    // Declared in java.ast at line 15


    public void addBodyDecl(BodyDecl node) {
        List<BodyDecl> list = getBodyDeclList();
        list.addChild(node);
    }

    // Declared in java.ast at line 20


    public void setBodyDecl(BodyDecl node, int i) {
        List<BodyDecl> list = getBodyDeclList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 24

    public List<BodyDecl> getBodyDecls() {
        return getBodyDeclList();
    }

    // Declared in java.ast at line 27

    public List<BodyDecl> getBodyDeclsNoTransform() {
        return getBodyDeclListNoTransform();
    }

    // Declared in java.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclList() {
        return (List<BodyDecl>)getChild(1);
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(1);
    }

    // Declared in Generics.jrag at line 563
 @SuppressWarnings({"unchecked", "cast"})     public boolean sameSignature(Access a) {
        boolean sameSignature_Access_value = sameSignature_compute(a);
        return sameSignature_Access_value;
    }

    private boolean sameSignature_compute(Access a) {
    if(a instanceof Wildcard)
      return true;
    return super.sameSignature(a);
  }

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

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeWildcard(this);  }

    // Declared in GenericsSubtype.jrag at line 47
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeWildcard(WildcardType type) {
        boolean supertypeWildcard_WildcardType_value = supertypeWildcard_compute(type);
        return supertypeWildcard_WildcardType_value;
    }

    private boolean supertypeWildcard_compute(WildcardType type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 58
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeWildcardExtends(WildcardExtendsType type) {
        boolean supertypeWildcardExtends_WildcardExtendsType_value = supertypeWildcardExtends_compute(type);
        return supertypeWildcardExtends_WildcardExtendsType_value;
    }

    private boolean supertypeWildcardExtends_compute(WildcardExtendsType type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 67
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeWildcardSuper(WildcardSuperType type) {
        boolean supertypeWildcardSuper_WildcardSuperType_value = supertypeWildcardSuper_compute(type);
        return supertypeWildcardSuper_WildcardSuperType_value;
    }

    private boolean supertypeWildcardSuper_compute(WildcardSuperType type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 75
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeClassDecl(ClassDecl type) {
        boolean supertypeClassDecl_ClassDecl_value = supertypeClassDecl_compute(type);
        return supertypeClassDecl_ClassDecl_value;
    }

    private boolean supertypeClassDecl_compute(ClassDecl type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 76
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeInterfaceDecl(InterfaceDecl type) {
        boolean supertypeInterfaceDecl_InterfaceDecl_value = supertypeInterfaceDecl_compute(type);
        return supertypeInterfaceDecl_InterfaceDecl_value;
    }

    private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 77
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeParClassDecl(ParClassDecl type) {
        boolean supertypeParClassDecl_ParClassDecl_value = supertypeParClassDecl_compute(type);
        return supertypeParClassDecl_ParClassDecl_value;
    }

    private boolean supertypeParClassDecl_compute(ParClassDecl type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 78
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeParInterfaceDecl(ParInterfaceDecl type) {
        boolean supertypeParInterfaceDecl_ParInterfaceDecl_value = supertypeParInterfaceDecl_compute(type);
        return supertypeParInterfaceDecl_ParInterfaceDecl_value;
    }

    private boolean supertypeParInterfaceDecl_compute(ParInterfaceDecl type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 79
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeRawClassDecl(RawClassDecl type) {
        boolean supertypeRawClassDecl_RawClassDecl_value = supertypeRawClassDecl_compute(type);
        return supertypeRawClassDecl_RawClassDecl_value;
    }

    private boolean supertypeRawClassDecl_compute(RawClassDecl type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 80
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeRawInterfaceDecl(RawInterfaceDecl type) {
        boolean supertypeRawInterfaceDecl_RawInterfaceDecl_value = supertypeRawInterfaceDecl_compute(type);
        return supertypeRawInterfaceDecl_RawInterfaceDecl_value;
    }

    private boolean supertypeRawInterfaceDecl_compute(RawInterfaceDecl type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 81
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeTypeVariable(TypeVariable type) {
        boolean supertypeTypeVariable_TypeVariable_value = supertypeTypeVariable_compute(type);
        return supertypeTypeVariable_TypeVariable_value;
    }

    private boolean supertypeTypeVariable_compute(TypeVariable type) {  return true;  }

    // Declared in GenericsSubtype.jrag at line 82
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeArrayDecl(ArrayDecl type) {
        boolean supertypeArrayDecl_ArrayDecl_value = supertypeArrayDecl_compute(type);
        return supertypeArrayDecl_ArrayDecl_value;
    }

    private boolean supertypeArrayDecl_compute(ArrayDecl type) {  return true;  }

    protected java.util.Map containedIn_TypeDecl_visited;
    protected java.util.Set containedIn_TypeDecl_computed = new java.util.HashSet(4);
    protected java.util.Set containedIn_TypeDecl_initialized = new java.util.HashSet(4);
    protected java.util.Map containedIn_TypeDecl_values = new java.util.HashMap(4);
 @SuppressWarnings({"unchecked", "cast"})     public boolean containedIn(TypeDecl type) {
        Object _parameters = type;
if(containedIn_TypeDecl_visited == null) containedIn_TypeDecl_visited = new java.util.HashMap(4);
if(containedIn_TypeDecl_values == null) containedIn_TypeDecl_values = new java.util.HashMap(4);
        if(containedIn_TypeDecl_computed.contains(_parameters))
            return ((Boolean)containedIn_TypeDecl_values.get(_parameters)).booleanValue();
        if (!containedIn_TypeDecl_initialized.contains(_parameters)) {
            containedIn_TypeDecl_initialized.add(_parameters);
            containedIn_TypeDecl_values.put(_parameters, Boolean.valueOf(true));
        }
        if (!IN_CIRCLE) {
            IN_CIRCLE = true;
            int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
            CIRCLE_INDEX = 1;
            boolean new_containedIn_TypeDecl_value;
            do {
                containedIn_TypeDecl_visited.put(_parameters, new Integer(CIRCLE_INDEX));
                CHANGE = false;
                new_containedIn_TypeDecl_value = containedIn_compute(type);
                if (new_containedIn_TypeDecl_value!=((Boolean)containedIn_TypeDecl_values.get(_parameters)).booleanValue())
                    CHANGE = true;
                containedIn_TypeDecl_values.put(_parameters, Boolean.valueOf(new_containedIn_TypeDecl_value));
                CIRCLE_INDEX++;
            } while (CHANGE);
            if(isFinal && num == boundariesCrossed)
{
            containedIn_TypeDecl_computed.add(_parameters);
            }
            else {
            RESET_CYCLE = true;
            containedIn_compute(type);
            RESET_CYCLE = false;
            containedIn_TypeDecl_computed.remove(_parameters);
            containedIn_TypeDecl_initialized.remove(_parameters);
            }
            IN_CIRCLE = false; 
            return new_containedIn_TypeDecl_value;
        }
        if(!new Integer(CIRCLE_INDEX).equals(containedIn_TypeDecl_visited.get(_parameters))) {
            containedIn_TypeDecl_visited.put(_parameters, new Integer(CIRCLE_INDEX));
            if (RESET_CYCLE) {
                containedIn_TypeDecl_computed.remove(_parameters);
                containedIn_TypeDecl_initialized.remove(_parameters);
                return ((Boolean)containedIn_TypeDecl_values.get(_parameters)).booleanValue();
            }
            boolean new_containedIn_TypeDecl_value = containedIn_compute(type);
            if (new_containedIn_TypeDecl_value!=((Boolean)containedIn_TypeDecl_values.get(_parameters)).booleanValue())
                CHANGE = true;
            containedIn_TypeDecl_values.put(_parameters, Boolean.valueOf(new_containedIn_TypeDecl_value));
            return new_containedIn_TypeDecl_value;
        }
        return ((Boolean)containedIn_TypeDecl_values.get(_parameters)).booleanValue();
    }

    private boolean containedIn_compute(TypeDecl type) {
    if(type == this)
      return true;
    else if(type instanceof WildcardExtendsType)
      return typeObject().subtype(((WildcardExtendsType)type).extendsType());
    else 
      return false;
  }

    // Declared in GenericsSubtype.jrag at line 401
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

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
