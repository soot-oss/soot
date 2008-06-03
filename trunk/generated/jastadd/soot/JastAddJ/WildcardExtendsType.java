
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;


public class WildcardExtendsType extends AbstractWildcardType implements Cloneable {
    public void flushCache() {
        super.flushCache();
        involvesTypeParameters_visited = 0;
        usesTypeVariable_visited = 0;
        usesTypeVariable_computed = false;
        usesTypeVariable_initialized = false;
        subtype_TypeDecl_visited = new java.util.HashMap(4);
        containedIn_TypeDecl_visited = new java.util.HashMap(4);
        sameStructure_TypeDecl_visited = new java.util.HashMap(4);
        instanceOf_TypeDecl_values = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WildcardExtendsType clone() throws CloneNotSupportedException {
        WildcardExtendsType node = (WildcardExtendsType)super.clone();
        node.involvesTypeParameters_visited = 0;
        node.usesTypeVariable_visited = 0;
        node.usesTypeVariable_computed = false;
        node.usesTypeVariable_initialized = false;
        node.subtype_TypeDecl_visited = new java.util.HashMap(4);
        node.containedIn_TypeDecl_visited = new java.util.HashMap(4);
        node.sameStructure_TypeDecl_visited = new java.util.HashMap(4);
        node.instanceOf_TypeDecl_values = null;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WildcardExtendsType copy() {
      try {
          WildcardExtendsType node = (WildcardExtendsType)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WildcardExtendsType fullCopy() {
        WildcardExtendsType res = (WildcardExtendsType)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.jrag at line 755


  public Access substitute(Parameterization parTypeDecl) {
    if(!usesTypeVariable())
      return super.substitute(parTypeDecl);
    return new WildcardExtends(getAccess().type().substitute(parTypeDecl));
  }

    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 24

    public WildcardExtendsType() {
        super();

        setChild(new List(), 1);

    }

    // Declared in Generics.ast at line 11


    // Declared in Generics.ast line 24
    public WildcardExtendsType(Modifiers p0, String p1, List<BodyDecl> p2, Access p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in Generics.ast at line 19


    // Declared in Generics.ast line 24
    public WildcardExtendsType(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2, Access p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in Generics.ast at line 26


  protected int numChildren() {
    return 3;
  }

    // Declared in Generics.ast at line 29

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

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 24
    public void setAccess(Access node) {
        setChild(node, 2);
    }

    // Declared in Generics.ast at line 5

    public Access getAccess() {
        return (Access)getChild(2);
    }

    // Declared in Generics.ast at line 9


    public Access getAccessNoTransform() {
        return (Access)getChildNoTransform(2);
    }

    protected int involvesTypeParameters_visited;
    protected boolean involvesTypeParameters_computed = false;
    protected boolean involvesTypeParameters_initialized = false;
    protected boolean involvesTypeParameters_value;
 @SuppressWarnings({"unchecked", "cast"})     public boolean involvesTypeParameters() {
        if(involvesTypeParameters_computed)
            return involvesTypeParameters_value;
        if (!involvesTypeParameters_initialized) {
            involvesTypeParameters_initialized = true;
            involvesTypeParameters_value = false;
        }
        if (!IN_CIRCLE) {
            IN_CIRCLE = true;
            int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
            CIRCLE_INDEX = 1;
            do {
                involvesTypeParameters_visited = CIRCLE_INDEX;
                CHANGE = false;
                boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
                if (new_involvesTypeParameters_value!=involvesTypeParameters_value)
                    CHANGE = true;
                involvesTypeParameters_value = new_involvesTypeParameters_value; 
                CIRCLE_INDEX++;
            } while (CHANGE);
            if(isFinal && num == boundariesCrossed)
{
            involvesTypeParameters_computed = true;
            }
            else {
            RESET_CYCLE = true;
            involvesTypeParameters_compute();
            RESET_CYCLE = false;
              involvesTypeParameters_computed = false;
              involvesTypeParameters_initialized = false;
            }
            IN_CIRCLE = false; 
            return involvesTypeParameters_value;
        }
        if(involvesTypeParameters_visited != CIRCLE_INDEX) {
            involvesTypeParameters_visited = CIRCLE_INDEX;
            if (RESET_CYCLE) {
                involvesTypeParameters_computed = false;
                involvesTypeParameters_initialized = false;
                return involvesTypeParameters_value;
            }
            boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
            if (new_involvesTypeParameters_value!=involvesTypeParameters_value)
                CHANGE = true;
            involvesTypeParameters_value = new_involvesTypeParameters_value; 
            return involvesTypeParameters_value;
        }
        return involvesTypeParameters_value;
    }

    private boolean involvesTypeParameters_compute() {  return extendsType().involvesTypeParameters();  }

    // Declared in Generics.jrag at line 568
 @SuppressWarnings({"unchecked", "cast"})     public boolean sameSignature(Access a) {
        boolean sameSignature_Access_value = sameSignature_compute(a);
        return sameSignature_Access_value;
    }

    private boolean sameSignature_compute(Access a) {
    if(a instanceof WildcardExtends)
      return getAccess().type().sameSignature(((WildcardExtends)a).getAccess());
    return super.sameSignature(a);
  }

    protected int usesTypeVariable_visited;
    protected boolean usesTypeVariable_computed = false;
    protected boolean usesTypeVariable_initialized = false;
    protected boolean usesTypeVariable_value;
 @SuppressWarnings({"unchecked", "cast"})     public boolean usesTypeVariable() {
        if(usesTypeVariable_computed)
            return usesTypeVariable_value;
        if (!usesTypeVariable_initialized) {
            usesTypeVariable_initialized = true;
            usesTypeVariable_value = false;
        }
        if (!IN_CIRCLE) {
            IN_CIRCLE = true;
            int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
            CIRCLE_INDEX = 1;
            do {
                usesTypeVariable_visited = CIRCLE_INDEX;
                CHANGE = false;
                boolean new_usesTypeVariable_value = usesTypeVariable_compute();
                if (new_usesTypeVariable_value!=usesTypeVariable_value)
                    CHANGE = true;
                usesTypeVariable_value = new_usesTypeVariable_value; 
                CIRCLE_INDEX++;
            } while (CHANGE);
            if(isFinal && num == boundariesCrossed)
{
            usesTypeVariable_computed = true;
            }
            else {
            RESET_CYCLE = true;
            usesTypeVariable_compute();
            RESET_CYCLE = false;
              usesTypeVariable_computed = false;
              usesTypeVariable_initialized = false;
            }
            IN_CIRCLE = false; 
            return usesTypeVariable_value;
        }
        if(usesTypeVariable_visited != CIRCLE_INDEX) {
            usesTypeVariable_visited = CIRCLE_INDEX;
            if (RESET_CYCLE) {
                usesTypeVariable_computed = false;
                usesTypeVariable_initialized = false;
                return usesTypeVariable_value;
            }
            boolean new_usesTypeVariable_value = usesTypeVariable_compute();
            if (new_usesTypeVariable_value!=usesTypeVariable_value)
                CHANGE = true;
            usesTypeVariable_value = new_usesTypeVariable_value; 
            return usesTypeVariable_value;
        }
        return usesTypeVariable_value;
    }

    private boolean usesTypeVariable_compute() {  return getAccess().type().usesTypeVariable();  }

    // Declared in Generics.jrag at line 1155
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl extendsType() {
        TypeDecl extendsType_value = extendsType_compute();
        return extendsType_value;
    }

    private TypeDecl extendsType_compute() {  return getAccess().type();  }

    // Declared in GenericsSubtype.jrag at line 51
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeWildcard(WildcardType type) {
        boolean supertypeWildcard_WildcardType_value = supertypeWildcard_compute(type);
        return supertypeWildcard_WildcardType_value;
    }

    private boolean supertypeWildcard_compute(WildcardType type) {  return typeObject().subtype(this);  }

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

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeWildcardExtends(this);  }

    // Declared in GenericsSubtype.jrag at line 62
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeWildcardExtends(WildcardExtendsType type) {
        boolean supertypeWildcardExtends_WildcardExtendsType_value = supertypeWildcardExtends_compute(type);
        return supertypeWildcardExtends_WildcardExtendsType_value;
    }

    private boolean supertypeWildcardExtends_compute(WildcardExtendsType type) {  return type.extendsType().subtype(extendsType());  }

    // Declared in GenericsSubtype.jrag at line 84
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeClassDecl(ClassDecl type) {
        boolean supertypeClassDecl_ClassDecl_value = supertypeClassDecl_compute(type);
        return supertypeClassDecl_ClassDecl_value;
    }

    private boolean supertypeClassDecl_compute(ClassDecl type) {  return type.subtype(extendsType());  }

    // Declared in GenericsSubtype.jrag at line 85
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeInterfaceDecl(InterfaceDecl type) {
        boolean supertypeInterfaceDecl_InterfaceDecl_value = supertypeInterfaceDecl_compute(type);
        return supertypeInterfaceDecl_InterfaceDecl_value;
    }

    private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {  return type.subtype(extendsType());  }

    // Declared in GenericsSubtype.jrag at line 86
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeParClassDecl(ParClassDecl type) {
        boolean supertypeParClassDecl_ParClassDecl_value = supertypeParClassDecl_compute(type);
        return supertypeParClassDecl_ParClassDecl_value;
    }

    private boolean supertypeParClassDecl_compute(ParClassDecl type) {  return type.subtype(extendsType());  }

    // Declared in GenericsSubtype.jrag at line 87
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeParInterfaceDecl(ParInterfaceDecl type) {
        boolean supertypeParInterfaceDecl_ParInterfaceDecl_value = supertypeParInterfaceDecl_compute(type);
        return supertypeParInterfaceDecl_ParInterfaceDecl_value;
    }

    private boolean supertypeParInterfaceDecl_compute(ParInterfaceDecl type) {  return type.subtype(extendsType());  }

    // Declared in GenericsSubtype.jrag at line 88
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeRawClassDecl(RawClassDecl type) {
        boolean supertypeRawClassDecl_RawClassDecl_value = supertypeRawClassDecl_compute(type);
        return supertypeRawClassDecl_RawClassDecl_value;
    }

    private boolean supertypeRawClassDecl_compute(RawClassDecl type) {  return type.subtype(extendsType());  }

    // Declared in GenericsSubtype.jrag at line 89
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeRawInterfaceDecl(RawInterfaceDecl type) {
        boolean supertypeRawInterfaceDecl_RawInterfaceDecl_value = supertypeRawInterfaceDecl_compute(type);
        return supertypeRawInterfaceDecl_RawInterfaceDecl_value;
    }

    private boolean supertypeRawInterfaceDecl_compute(RawInterfaceDecl type) {  return type.subtype(extendsType());  }

    // Declared in GenericsSubtype.jrag at line 90
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeTypeVariable(TypeVariable type) {
        boolean supertypeTypeVariable_TypeVariable_value = supertypeTypeVariable_compute(type);
        return supertypeTypeVariable_TypeVariable_value;
    }

    private boolean supertypeTypeVariable_compute(TypeVariable type) {  return type.subtype(extendsType());  }

    // Declared in GenericsSubtype.jrag at line 91
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeArrayDecl(ArrayDecl type) {
        boolean supertypeArrayDecl_ArrayDecl_value = supertypeArrayDecl_compute(type);
        return supertypeArrayDecl_ArrayDecl_value;
    }

    private boolean supertypeArrayDecl_compute(ArrayDecl type) {  return type.subtype(extendsType());  }

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
    if(type == this || type instanceof WildcardType) 
      return true;
    else if(type instanceof WildcardExtendsType)
      return extendsType().subtype(((WildcardExtendsType)type).extendsType());
    else 
      return false;
  }

    protected java.util.Map sameStructure_TypeDecl_visited;
    protected java.util.Set sameStructure_TypeDecl_computed = new java.util.HashSet(4);
    protected java.util.Set sameStructure_TypeDecl_initialized = new java.util.HashSet(4);
    protected java.util.Map sameStructure_TypeDecl_values = new java.util.HashMap(4);
 @SuppressWarnings({"unchecked", "cast"})     public boolean sameStructure(TypeDecl t) {
        Object _parameters = t;
if(sameStructure_TypeDecl_visited == null) sameStructure_TypeDecl_visited = new java.util.HashMap(4);
if(sameStructure_TypeDecl_values == null) sameStructure_TypeDecl_values = new java.util.HashMap(4);
        if(sameStructure_TypeDecl_computed.contains(_parameters))
            return ((Boolean)sameStructure_TypeDecl_values.get(_parameters)).booleanValue();
        if (!sameStructure_TypeDecl_initialized.contains(_parameters)) {
            sameStructure_TypeDecl_initialized.add(_parameters);
            sameStructure_TypeDecl_values.put(_parameters, Boolean.valueOf(true));
        }
        if (!IN_CIRCLE) {
            IN_CIRCLE = true;
            int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
            CIRCLE_INDEX = 1;
            boolean new_sameStructure_TypeDecl_value;
            do {
                sameStructure_TypeDecl_visited.put(_parameters, new Integer(CIRCLE_INDEX));
                CHANGE = false;
                new_sameStructure_TypeDecl_value = sameStructure_compute(t);
                if (new_sameStructure_TypeDecl_value!=((Boolean)sameStructure_TypeDecl_values.get(_parameters)).booleanValue())
                    CHANGE = true;
                sameStructure_TypeDecl_values.put(_parameters, Boolean.valueOf(new_sameStructure_TypeDecl_value));
                CIRCLE_INDEX++;
            } while (CHANGE);
            if(isFinal && num == boundariesCrossed)
{
            sameStructure_TypeDecl_computed.add(_parameters);
            }
            else {
            RESET_CYCLE = true;
            sameStructure_compute(t);
            RESET_CYCLE = false;
            sameStructure_TypeDecl_computed.remove(_parameters);
            sameStructure_TypeDecl_initialized.remove(_parameters);
            }
            IN_CIRCLE = false; 
            return new_sameStructure_TypeDecl_value;
        }
        if(!new Integer(CIRCLE_INDEX).equals(sameStructure_TypeDecl_visited.get(_parameters))) {
            sameStructure_TypeDecl_visited.put(_parameters, new Integer(CIRCLE_INDEX));
            if (RESET_CYCLE) {
                sameStructure_TypeDecl_computed.remove(_parameters);
                sameStructure_TypeDecl_initialized.remove(_parameters);
                return ((Boolean)sameStructure_TypeDecl_values.get(_parameters)).booleanValue();
            }
            boolean new_sameStructure_TypeDecl_value = sameStructure_compute(t);
            if (new_sameStructure_TypeDecl_value!=((Boolean)sameStructure_TypeDecl_values.get(_parameters)).booleanValue())
                CHANGE = true;
            sameStructure_TypeDecl_values.put(_parameters, Boolean.valueOf(new_sameStructure_TypeDecl_value));
            return new_sameStructure_TypeDecl_value;
        }
        return ((Boolean)sameStructure_TypeDecl_values.get(_parameters)).booleanValue();
    }

    private boolean sameStructure_compute(TypeDecl t) {  return super.sameStructure(t) || 
    t instanceof WildcardExtendsType && ((WildcardExtendsType)t).extendsType().sameStructure(extendsType());  }

    // Declared in GenericsSubtype.jrag at line 402
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
