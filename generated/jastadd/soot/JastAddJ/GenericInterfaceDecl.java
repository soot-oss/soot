
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class GenericInterfaceDecl extends InterfaceDecl implements Cloneable, GenericTypeDecl {
    public void flushCache() {
        super.flushCache();
        rawType_computed = false;
        rawType_value = null;
        getParTypeDeclList_computed = false;
        getParTypeDeclList_value = null;
        lookupParTypeDecl_ParTypeAccess_values = null;
        lookupParTypeDecl_ArrayList_values = null;
        usesTypeVariable_visited = -1;
        usesTypeVariable_computed = false;
        usesTypeVariable_initialized = false;
        subtype_TypeDecl_values = null;
        instanceOf_TypeDecl_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericInterfaceDecl clone() throws CloneNotSupportedException {
        GenericInterfaceDecl node = (GenericInterfaceDecl)super.clone();
        node.rawType_computed = false;
        node.rawType_value = null;
        node.getParTypeDeclList_computed = false;
        node.getParTypeDeclList_value = null;
        node.lookupParTypeDecl_ParTypeAccess_values = null;
        node.lookupParTypeDecl_ArrayList_values = null;
        node.usesTypeVariable_visited = -1;
        node.usesTypeVariable_computed = false;
        node.usesTypeVariable_initialized = false;
        node.subtype_TypeDecl_values = null;
        node.instanceOf_TypeDecl_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericInterfaceDecl copy() {
      try {
          GenericInterfaceDecl node = (GenericInterfaceDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GenericInterfaceDecl fullCopy() {
        GenericInterfaceDecl res = (GenericInterfaceDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.jrag at line 397

  public void typeCheck() {
    super.typeCheck();
    if(instanceOf(typeThrowable()))
      error(" generic interface " + typeName() + " may not directly or indirectly inherit java.lang.Throwable");
  }

    // Declared in Generics.jrag at line 1101

  public InterfaceDecl p(Parameterization parTypeDecl) {
    GenericInterfaceDecl c = new GenericInterfaceDeclSubstituted(
      (Modifiers)getModifiers().fullCopy(),
      getID(),
      getSuperInterfaceIdList().substitute(parTypeDecl),
      new List(),
      new List(), // delegates TypeParameter lookup to original
      this
    );
    return c;
  }

    // Declared in GenericsPrettyPrint.jrag at line 121

  
	public void toString(StringBuffer s) {
		getModifiers().toString(s);
		s.append("interface " + getID());
		s.append('<');
    	if (getNumTypeParameter() > 0) {
    		getTypeParameter(0).toString(s);
    		for (int i = 1; i < getNumTypeParameter(); i++) {
    			s.append(", ");
    			getTypeParameter(i).toString(s);
    		}
    	}
    	s.append('>');
		if(getNumSuperInterfaceId() > 0) {
			s.append(" extends ");
			getSuperInterfaceId(0).toString(s);
      for(int i = 1; i < getNumSuperInterfaceId(); i++) {
        s.append(", ");
			  getSuperInterfaceId(i).toString(s);
      }
    }

    /*

    s.append(" instantiated with: ");
    for(int i = 0; i < getNumParTypeDecl(); i++) {
      if(i != 0) s.append(", ");
      ParTypeDecl decl = getParTypeDecl(i);
      s.append("<");
      for(int j = 0; j < decl.getNumArgument(); j++) {
        if(j != 0) s.append(", ");
        s.append(decl.getArgument(j).type().fullName());
      }
      s.append(">");
    }
    */
    
		s.append(" {");
		for(int i=0; i < getNumBodyDecl(); i++) {
			getBodyDecl(i).toString(s);
		}
		s.append(indent() + "}");
    
    /*
    for(int i = 0; i < getNumParTypeDecl(); i++) {
      ParInterfaceDecl decl = getParTypeDecl(i);
      decl.toString(s);
    }
    */
	}

    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 3

    public GenericInterfaceDecl() {
        super();

        setChild(new List(), 1);
        setChild(new List(), 2);
        setChild(new List(), 3);
        setChild(new List(), 4);

    }

    // Declared in Generics.ast at line 14


    // Declared in Generics.ast line 3
    public GenericInterfaceDecl(Modifiers p0, String p1, List<Access> p2, List<BodyDecl> p3, List<TypeVariable> p4) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(new List(), 4);
    }

    // Declared in Generics.ast at line 24


    // Declared in Generics.ast line 3
    public GenericInterfaceDecl(Modifiers p0, beaver.Symbol p1, List<Access> p2, List<BodyDecl> p3, List<TypeVariable> p4) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
        setChild(p4, 3);
        setChild(new List(), 4);
    }

    // Declared in Generics.ast at line 33


  protected int numChildren() {
    return 4;
  }

    // Declared in Generics.ast at line 36

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 3
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
    // Declared in Generics.ast line 3
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in Generics.ast at line 5

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in Generics.ast at line 12

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 3
    public void setSuperInterfaceIdList(List<Access> list) {
        setChild(list, 1);
    }

    // Declared in Generics.ast at line 6


    public int getNumSuperInterfaceId() {
        return getSuperInterfaceIdList().getNumChild();
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getSuperInterfaceId(int i) {
        return (Access)getSuperInterfaceIdList().getChild(i);
    }

    // Declared in Generics.ast at line 14


    public void addSuperInterfaceId(Access node) {
        List<Access> list = (parent == null || state == null) ? getSuperInterfaceIdListNoTransform() : getSuperInterfaceIdList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 19


    public void addSuperInterfaceIdNoTransform(Access node) {
        List<Access> list = getSuperInterfaceIdListNoTransform();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 24


    public void setSuperInterfaceId(Access node, int i) {
        List<Access> list = getSuperInterfaceIdList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 28

    public List<Access> getSuperInterfaceIds() {
        return getSuperInterfaceIdList();
    }

    // Declared in Generics.ast at line 31

    public List<Access> getSuperInterfaceIdsNoTransform() {
        return getSuperInterfaceIdListNoTransform();
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getSuperInterfaceIdList() {
        List<Access> list = (List<Access>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in Generics.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getSuperInterfaceIdListNoTransform() {
        return (List<Access>)getChildNoTransform(1);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 3
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 2);
    }

    // Declared in Generics.ast at line 6


    public int getNumBodyDecl() {
        return getBodyDeclList().getNumChild();
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public BodyDecl getBodyDecl(int i) {
        return (BodyDecl)getBodyDeclList().getChild(i);
    }

    // Declared in Generics.ast at line 14


    public void addBodyDecl(BodyDecl node) {
        List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 19


    public void addBodyDeclNoTransform(BodyDecl node) {
        List<BodyDecl> list = getBodyDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 24


    public void setBodyDecl(BodyDecl node, int i) {
        List<BodyDecl> list = getBodyDeclList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 28

    public List<BodyDecl> getBodyDecls() {
        return getBodyDeclList();
    }

    // Declared in Generics.ast at line 31

    public List<BodyDecl> getBodyDeclsNoTransform() {
        return getBodyDeclListNoTransform();
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclList() {
        List<BodyDecl> list = (List<BodyDecl>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in Generics.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(2);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 3
    public void setTypeParameterList(List<TypeVariable> list) {
        setChild(list, 3);
    }

    // Declared in Generics.ast at line 6


    public int getNumTypeParameter() {
        return getTypeParameterList().getNumChild();
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public TypeVariable getTypeParameter(int i) {
        return (TypeVariable)getTypeParameterList().getChild(i);
    }

    // Declared in Generics.ast at line 14


    public void addTypeParameter(TypeVariable node) {
        List<TypeVariable> list = (parent == null || state == null) ? getTypeParameterListNoTransform() : getTypeParameterList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 19


    public void addTypeParameterNoTransform(TypeVariable node) {
        List<TypeVariable> list = getTypeParameterListNoTransform();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 24


    public void setTypeParameter(TypeVariable node, int i) {
        List<TypeVariable> list = getTypeParameterList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 28

    public List<TypeVariable> getTypeParameters() {
        return getTypeParameterList();
    }

    // Declared in Generics.ast at line 31

    public List<TypeVariable> getTypeParametersNoTransform() {
        return getTypeParameterListNoTransform();
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<TypeVariable> getTypeParameterList() {
        List<TypeVariable> list = (List<TypeVariable>)getChild(3);
        list.getNumChild();
        return list;
    }

    // Declared in Generics.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<TypeVariable> getTypeParameterListNoTransform() {
        return (List<TypeVariable>)getChildNoTransform(3);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 3
    public void setParTypeDeclList(List<ParInterfaceDecl> list) {
        setChild(list, 4);
    }

    // Declared in Generics.ast at line 6


    public int getNumParTypeDecl() {
        return getParTypeDeclList().getNumChild();
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public ParInterfaceDecl getParTypeDecl(int i) {
        return (ParInterfaceDecl)getParTypeDeclList().getChild(i);
    }

    // Declared in Generics.ast at line 14


    public void addParTypeDecl(ParInterfaceDecl node) {
        List<ParInterfaceDecl> list = (parent == null || state == null) ? getParTypeDeclListNoTransform() : getParTypeDeclList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 19


    public void addParTypeDeclNoTransform(ParInterfaceDecl node) {
        List<ParInterfaceDecl> list = getParTypeDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 24


    public void setParTypeDecl(ParInterfaceDecl node, int i) {
        List<ParInterfaceDecl> list = getParTypeDeclList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 28

    public List<ParInterfaceDecl> getParTypeDecls() {
        return getParTypeDeclList();
    }

    // Declared in Generics.ast at line 31

    public List<ParInterfaceDecl> getParTypeDeclsNoTransform() {
        return getParTypeDeclListNoTransform();
    }

    // Declared in Generics.ast at line 35


    public List<ParInterfaceDecl> getParTypeDeclListNoTransform() {
        return (List<ParInterfaceDecl>)getChildNoTransform(4);
    }

    // Declared in Generics.ast at line 39


    protected int getParTypeDeclListChildPosition() {
        return 4;
    }

    // Declared in Generics.jrag at line 211

  public TypeDecl makeGeneric(Signatures.ClassSignature s) {
    return (TypeDecl)this;
  }

    // Declared in Generics.jrag at line 456


  public SimpleSet addTypeVariables(SimpleSet c, String name) {
    GenericTypeDecl original = (GenericTypeDecl)original();
    for(int i = 0; i < original.getNumTypeParameter(); i++) {
      TypeVariable p = original.getTypeParameter(i);
      if(p.name().equals(name))
        c = c.add(p);
    }
    return c;
  }

    // Declared in Generics.jrag at line 657

  public List createArgumentList(ArrayList params) {
    GenericTypeDecl original = (GenericTypeDecl)original();
    List list = new List();
    if(params.isEmpty())
      for(int i = 0; i < original.getNumTypeParameter(); i++)
        list.add(original.getTypeParameter(i).erasure().createBoundAccess());
    else
      for(Iterator iter = params.iterator(); iter.hasNext(); )
        list.add(((TypeDecl)iter.next()).createBoundAccess());
    return list;
  }

    protected boolean rawType_computed = false;
    protected TypeDecl rawType_value;
    // Declared in Generics.jrag at line 153
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl rawType() {
        if(rawType_computed) {
            return rawType_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        rawType_value = rawType_compute();
        if(isFinal && num == state().boundariesCrossed)
            rawType_computed = true;
        return rawType_value;
    }

    private TypeDecl rawType_compute() {  return lookupParTypeDecl(new ArrayList());  }

    protected boolean getParTypeDeclList_computed = false;
    protected List getParTypeDeclList_value;
    // Declared in Generics.jrag at line 589
 @SuppressWarnings({"unchecked", "cast"})     public List getParTypeDeclList() {
        if(getParTypeDeclList_computed) {
            return (List)ASTNode.getChild(this, getParTypeDeclListChildPosition());
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getParTypeDeclList_value = getParTypeDeclList_compute();
        setParTypeDeclList(getParTypeDeclList_value);
        if(true)
            getParTypeDeclList_computed = true;
        return (List)ASTNode.getChild(this, getParTypeDeclListChildPosition());
    }

    private List getParTypeDeclList_compute() {  return new List();  }

    protected java.util.Map lookupParTypeDecl_ParTypeAccess_values;
    // Declared in Generics.jrag at line 609
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lookupParTypeDecl(ParTypeAccess p) {
        Object _parameters = p;
if(lookupParTypeDecl_ParTypeAccess_values == null) lookupParTypeDecl_ParTypeAccess_values = new java.util.HashMap(4);
        if(lookupParTypeDecl_ParTypeAccess_values.containsKey(_parameters)) {
            return (TypeDecl)lookupParTypeDecl_ParTypeAccess_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        TypeDecl lookupParTypeDecl_ParTypeAccess_value = lookupParTypeDecl_compute(p);
        if(isFinal && num == state().boundariesCrossed)
            lookupParTypeDecl_ParTypeAccess_values.put(_parameters, lookupParTypeDecl_ParTypeAccess_value);
        return lookupParTypeDecl_ParTypeAccess_value;
    }

    private TypeDecl lookupParTypeDecl_compute(ParTypeAccess p) {
    for(int i = 0; i < getNumParTypeDecl(); i++) {
      ParTypeDecl decl = (ParTypeDecl)getParTypeDecl(i);
      if(!decl.isRawType() && decl.sameSignature(p))
        return (TypeDecl)decl;
    }
    ParInterfaceDecl typeDecl = new ParInterfaceDecl();
    typeDecl.setModifiers((Modifiers)getModifiers().fullCopy());
    typeDecl.setID(getID());
    addParTypeDecl(typeDecl);
    List list = new List();
    for(int i = 0; i < p.getNumTypeArgument(); i++)
      list.add(p.getTypeArgument(i).type().createBoundAccess());
    typeDecl.setArgumentList(list);
    typeDecl.is$Final = true;
    return typeDecl;
  }

    protected java.util.Map lookupParTypeDecl_ArrayList_values;
    // Declared in Generics.jrag at line 643
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lookupParTypeDecl(ArrayList list) {
        Object _parameters = list;
if(lookupParTypeDecl_ArrayList_values == null) lookupParTypeDecl_ArrayList_values = new java.util.HashMap(4);
        if(lookupParTypeDecl_ArrayList_values.containsKey(_parameters)) {
            return (TypeDecl)lookupParTypeDecl_ArrayList_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        TypeDecl lookupParTypeDecl_ArrayList_value = lookupParTypeDecl_compute(list);
        if(true)
            lookupParTypeDecl_ArrayList_values.put(_parameters, lookupParTypeDecl_ArrayList_value);
        return lookupParTypeDecl_ArrayList_value;
    }

    private TypeDecl lookupParTypeDecl_compute(ArrayList list) {
    for(int i = 0; i < getNumParTypeDecl(); i++) {
      ParTypeDecl decl = (ParTypeDecl)getParTypeDecl(i);
      if(decl.isRawType() ? list.isEmpty() : (!list.isEmpty() && decl.sameSignature(list)))
        return (TypeDecl)decl;
    }
    ParInterfaceDecl typeDecl = list.size() == 0 ? new RawInterfaceDecl() : new ParInterfaceDecl();
    typeDecl.setModifiers((Modifiers)getModifiers().fullCopy());
    typeDecl.setID(getID());
    addParTypeDecl(typeDecl);
    typeDecl.setArgumentList(createArgumentList(list));
    typeDecl.is$Final = true;
    return typeDecl;
  }

    // Declared in Generics.jrag at line 919
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

    private boolean usesTypeVariable_compute() {  return true;  }

    // Declared in GenericsSubtype.jrag at line 19
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

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeGenericInterfaceDecl(this);  }

    // Declared in GenericsSubtype.jrag at line 277
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeParClassDecl(ParClassDecl type) {
        ASTNode$State state = state();
        boolean supertypeParClassDecl_ParClassDecl_value = supertypeParClassDecl_compute(type);
        return supertypeParClassDecl_ParClassDecl_value;
    }

    private boolean supertypeParClassDecl_compute(ParClassDecl type) {  return type.genericDecl().original().subtype(this);  }

    // Declared in GenericsSubtype.jrag at line 279
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeParInterfaceDecl(ParInterfaceDecl type) {
        ASTNode$State state = state();
        boolean supertypeParInterfaceDecl_ParInterfaceDecl_value = supertypeParInterfaceDecl_compute(type);
        return supertypeParInterfaceDecl_ParInterfaceDecl_value;
    }

    private boolean supertypeParInterfaceDecl_compute(ParInterfaceDecl type) {  return type.genericDecl().original().subtype(this);  }

    // Declared in GenericsSubtype.jrag at line 395
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

    // Declared in Generics.jrag at line 156
 @SuppressWarnings({"unchecked", "cast"})     public boolean isGenericType() {
        ASTNode$State state = state();
        boolean isGenericType_value = isGenericType_compute();
        return isGenericType_value;
    }

    private boolean isGenericType_compute() {  return true;  }

    // Declared in Generics.jrag at line 403
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeThrowable() {
        ASTNode$State state = state();
        TypeDecl typeThrowable_value = getParent().Define_TypeDecl_typeThrowable(this, null);
        return typeThrowable_value;
    }

    // Declared in Generics.jrag at line 453
    public boolean Define_boolean_isNestedType(ASTNode caller, ASTNode child) {
        if(caller == getTypeParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return true;
        }
        return super.Define_boolean_isNestedType(caller, child);
    }

    // Declared in Generics.jrag at line 454
    public TypeDecl Define_TypeDecl_enclosingType(ASTNode caller, ASTNode child) {
        if(caller == getTypeParameterListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return this;
        }
        return super.Define_TypeDecl_enclosingType(caller, child);
    }

    // Declared in Generics.jrag at line 498
    public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
        if(caller == getBodyDeclListNoTransform()) { 
   int index = caller.getIndexOfChild(child);
{
    SimpleSet c = memberTypes(name);
    if(getBodyDecl(index).visibleTypeParameters())
      c = addTypeVariables(c, name);
    if(!c.isEmpty())
      return c;
    // 8.5.2
    if(isClassDecl() && isStatic() && !isTopLevelType()) {
      for(Iterator iter = lookupType(name).iterator(); iter.hasNext(); ) {
        TypeDecl d = (TypeDecl)iter.next();
        if(d.isStatic() || (d.enclosingType() != null && instanceOf(d.enclosingType()))) {
          c = c.add(d);
        }
      }
    }
    else
      c = lookupType(name);
    if(!c.isEmpty())
      return c;
    return topLevelType().lookupType(name); // Fix to search imports
    // include type parameters if not static
  }
}
        if(caller == getTypeParameterListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    SimpleSet c = memberTypes(name);
    c = addTypeVariables(c, name);
    if(!c.isEmpty()) return c;
    // 8.5.2
    if(isClassDecl() && isStatic() && !isTopLevelType()) {
      for(Iterator iter = lookupType(name).iterator(); iter.hasNext(); ) {
        TypeDecl d = (TypeDecl)iter.next();
        if(d.isStatic() || (d.enclosingType() != null && instanceOf(d.enclosingType()))) {
          c = c.add(d);
        }
      }
    }
    else
      c = lookupType(name);
    if(!c.isEmpty())
      return c;
    return topLevelType().lookupType(name); // Fix to search imports
  }
}
        if(caller == getSuperInterfaceIdListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    SimpleSet c = addTypeVariables(SimpleSet.emptySet, name);
    return !c.isEmpty() ? c : lookupType(name);
  }
}
        return super.Define_SimpleSet_lookupType(caller, child, name);
    }

    // Declared in GenericsParTypeDecl.jrag at line 48
    public TypeDecl Define_TypeDecl_genericDecl(ASTNode caller, ASTNode child) {
        if(caller == getParTypeDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return this;
        }
        return getParent().Define_TypeDecl_genericDecl(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
