
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class LUBType extends ReferenceType implements Cloneable {
    public void flushCache() {
        super.flushCache();
        lub_computed = false;
        lub_value = null;
        subtype_TypeDecl_values = null;
        getSootClassDecl_computed = false;
        getSootClassDecl_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public LUBType clone() throws CloneNotSupportedException {
        LUBType node = (LUBType)super.clone();
        node.lub_computed = false;
        node.lub_value = null;
        node.subtype_TypeDecl_values = null;
        node.getSootClassDecl_computed = false;
        node.getSootClassDecl_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public LUBType copy() {
      try {
          LUBType node = (LUBType)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public LUBType fullCopy() {
        LUBType res = (LUBType)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in GenericMethodsInference.jrag at line 668


    // the erased candidate set for type parameter Tj, EC,
    // is the intersection of all the sets EST(U) for each
    // U in U1...Uk
    public HashSet EC(ArrayList list) {
      HashSet result = new HashSet();
      boolean first = true;
      for(Iterator iter = list.iterator(); iter.hasNext(); ) {
        TypeDecl U = (TypeDecl)iter.next();
        // erased supertype set of U
        HashSet EST = EST(U); 
        if(first) {
          result.addAll(EST);
          first = false;
        }
        else
          result.retainAll(EST);
      }
      return result;
    }

    // Declared in GenericMethodsInference.jrag at line 687


    // The minimal erased candidate set for Tj
    // is MEC = {V | V in EC, forall  W != V in EC, not W <: V}
    public HashSet MEC(ArrayList list) {
      HashSet EC = EC(list);
      if(EC.size() == 1)
        return EC;
      HashSet MEC = new HashSet();
      for(Iterator iter = EC.iterator(); iter.hasNext(); ) {
        TypeDecl V = (TypeDecl)iter.next();
        boolean keep = true;
        for(Iterator i2 = EC.iterator(); i2.hasNext(); ) {
          TypeDecl W = (TypeDecl)i2.next();
          if(!(V instanceof TypeVariable) && V != W && W.instanceOf(V))
            keep = false;
        }
        if(keep)
          MEC.add(V);
      }
      return MEC;
    }

    // Declared in GenericMethodsInference.jrag at line 708


    // relevant invocations of G, Inv(G)
    // Inv(G) = {V | 1 <= i <= k, V in ST(Ui), V = G<...>}
    public HashSet Inv(TypeDecl G, ArrayList Us) {
      HashSet result = new HashSet();
      for(Iterator iter = Us.iterator(); iter.hasNext(); ) {
        TypeDecl U = (TypeDecl)iter.next();
        for(Iterator i2 = ST(U).iterator(); i2.hasNext(); ) {
          TypeDecl V = (TypeDecl)i2.next();
          if(V instanceof ParTypeDecl && !V.isRawType() && ((ParTypeDecl)V).genericDecl() == G)
            result.add(V);
        }
      }
      return result;
    }

    // Declared in GenericMethodsInference.jrag at line 722


    // least containing invocation (lci)
    public TypeDecl lci(HashSet set, TypeDecl G) {
      ArrayList list = new ArrayList();
      boolean first = true;
      for(Iterator iter = set.iterator(); iter.hasNext(); ) {
        ParTypeDecl decl = (ParTypeDecl)iter.next();
        if(first) {
          first = false;
          for(int i = 0; i < decl.getNumArgument(); i++)
            list.add(decl.getArgument(i).type());
        }
        else {
          for(int i = 0; i < decl.getNumArgument(); i++)
            list.set(i, lcta((TypeDecl)list.get(i), decl.getArgument(i).type()));
        }
      }
      return ((GenericTypeDecl)G).lookupParTypeDecl(list);
    }

    // Declared in GenericMethodsInference.jrag at line 741


    // least containing type arguments
    public TypeDecl lcta(TypeDecl X, TypeDecl Y) {
      //System.err.println("Computing lcta for " + X.typeName() + " and " + Y.typeName());
      if(!X.isWildcard() && !Y.isWildcard()) {
        TypeDecl U = X;
        TypeDecl V = Y;
        return U == V ? U : lub(U, V).asWildcardExtends();
      }
      else if(!X.isWildcard() && Y instanceof WildcardExtendsType) {
        TypeDecl U = X;
        TypeDecl V = ((WildcardExtendsType)Y).getAccess().type();
        return lub(U, V).asWildcardExtends();
      }
      else if(!X.isWildcard() && Y instanceof WildcardSuperType) {
        TypeDecl U = X;
        TypeDecl V = ((WildcardSuperType)Y).getAccess().type();
        ArrayList bounds = new ArrayList();
        bounds.add(U);
        bounds.add(V);
        return GLBTypeFactory.glb(bounds).asWildcardSuper();
      }
      else if(X instanceof WildcardExtendsType && Y instanceof WildcardExtendsType) {
        TypeDecl U = ((WildcardExtendsType)X).getAccess().type();
        TypeDecl V = ((WildcardExtendsType)Y).getAccess().type();
        return lub(U, V).asWildcardExtends();
      }
      else if(X instanceof WildcardExtendsType && Y instanceof WildcardSuperType) {
        TypeDecl U = ((WildcardExtendsType)X).getAccess().type();
        TypeDecl V = ((WildcardSuperType)Y).getAccess().type();
        return U == V ? U : U.typeWildcard();
      }
      else if(X instanceof WildcardSuperType && Y instanceof WildcardSuperType) {
        TypeDecl U = ((WildcardSuperType)X).getAccess().type();
        TypeDecl V = ((WildcardSuperType)Y).getAccess().type();
        ArrayList bounds = new ArrayList();
        bounds.add(U);
        bounds.add(V);
        return GLBTypeFactory.glb(bounds).asWildcardSuper();
      }
      else
        throw new Error("lcta not defined for (" + X.getClass().getName() + ", " + Y.getClass().getName());
    }

    // Declared in GenericMethodsInference.jrag at line 783


    public TypeDecl lub(TypeDecl X, TypeDecl Y) {
      ArrayList list = new ArrayList(2);
      list.add(X);
      list.add(Y);
      return lub(list);
    }

    // Declared in GenericMethodsInference.jrag at line 790


    public TypeDecl lub(ArrayList list) {
      return lookupLUBType(list);
    }

    // Declared in GenericMethodsInference.jrag at line 795


    // erased supertype set of T
    public HashSet EST(TypeDecl t) {
      HashSet result = new HashSet();
      for(Iterator iter = ST(t).iterator(); iter.hasNext(); ) {
        TypeDecl typeDecl = (TypeDecl)iter.next();
        if(typeDecl instanceof TypeVariable)
          result.add(typeDecl);
        else
          result.add(typeDecl.erasure());
      }
      return result;
    }

    // Declared in GenericMethodsInference.jrag at line 808


    // supertype set of T
    public HashSet ST(TypeDecl t) {
      HashSet result = new HashSet();
      addSupertypes(result, t);
      return result;
    }

    // Declared in GenericMethodsInference.jrag at line 814


    public void addSupertypes(HashSet set, TypeDecl t) {
      set.add(t);
      if(t instanceof ClassDecl) {
        ClassDecl type = (ClassDecl)t;
        if(type.hasSuperclass()) {
          addSupertypes(set, type.superclass());
        }
        for(int i = 0; i < type.getNumImplements(); i++) {
          addSupertypes(set, type.getImplements(i).type());
        }
      }
      else if(t instanceof InterfaceDecl) {
        InterfaceDecl type = (InterfaceDecl)t;
        for(int i = 0; i < type.getNumSuperInterfaceId(); i++) {
          addSupertypes(set, type.getSuperInterfaceId(i).type());
        }
        if(type.getNumSuperInterfaceId() == 0)
          set.add(type.typeObject());
      }
      else if(t instanceof TypeVariable) {
        TypeVariable type = (TypeVariable)t;
        for(int i = 0; i < type.getNumTypeBound(); i++) {
          addSupertypes(set, type.getTypeBound(i).type());
        }
        if(type.getNumTypeBound() == 0)
          set.add(type.typeObject());
      }
      else if(t instanceof LUBType) {
        LUBType type = (LUBType)t;
        for(int i = 0; i < type.getNumTypeBound(); i++) {
          addSupertypes(set, type.getTypeBound(i).type());
        }
        if(type.getNumTypeBound() == 0)
          set.add(type.typeObject());
      }
      else
        throw new Error("Operation not supported for " + t.fullName() + ", " + t.getClass().getName());
    }

    // Declared in Generics.jrag at line 1198


  public HashSet implementedInterfaces(){
       HashSet ret = new HashSet();
       for (int i = 0; i < getNumTypeBound(); i++) {
           ret.addAll(getTypeBound(i).type().implementedInterfaces());
       }
       return ret;
   }

    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 38

    public LUBType() {
        super();

        setChild(new List(), 1);
        setChild(new List(), 2);

    }

    // Declared in Generics.ast at line 12


    // Declared in Generics.ast line 38
    public LUBType(Modifiers p0, String p1, List<BodyDecl> p2, List<Access> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in Generics.ast at line 20


    // Declared in Generics.ast line 38
    public LUBType(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2, List<Access> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in Generics.ast at line 27


  protected int numChildren() {
    return 3;
  }

    // Declared in Generics.ast at line 30

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 38
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
    // Declared in Generics.ast line 38
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
    // Declared in Generics.ast line 38
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 1);
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
        List<BodyDecl> list = (List<BodyDecl>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in Generics.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(1);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 38
    public void setTypeBoundList(List<Access> list) {
        setChild(list, 2);
    }

    // Declared in Generics.ast at line 6


    public int getNumTypeBound() {
        return getTypeBoundList().getNumChild();
    }

    // Declared in Generics.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getTypeBound(int i) {
        return (Access)getTypeBoundList().getChild(i);
    }

    // Declared in Generics.ast at line 14


    public void addTypeBound(Access node) {
        List<Access> list = (parent == null || state == null) ? getTypeBoundListNoTransform() : getTypeBoundList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 19


    public void addTypeBoundNoTransform(Access node) {
        List<Access> list = getTypeBoundListNoTransform();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 24


    public void setTypeBound(Access node, int i) {
        List<Access> list = getTypeBoundList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 28

    public List<Access> getTypeBounds() {
        return getTypeBoundList();
    }

    // Declared in Generics.ast at line 31

    public List<Access> getTypeBoundsNoTransform() {
        return getTypeBoundListNoTransform();
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeBoundList() {
        List<Access> list = (List<Access>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in Generics.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeBoundListNoTransform() {
        return (List<Access>)getChildNoTransform(2);
    }

    protected boolean lub_computed = false;
    protected TypeDecl lub_value;
    // Declared in GenericMethodsInference.jrag at line 650
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lub() {
        if(lub_computed) {
            return lub_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        lub_value = lub_compute();
        if(isFinal && num == state().boundariesCrossed)
            lub_computed = true;
        return lub_value;
    }

    private TypeDecl lub_compute() {
    ArrayList list = new ArrayList();
    for(int i = 0; i < getNumTypeBound(); i++)
      list.add(getTypeBound(i).type());
    ArrayList bounds = new ArrayList();
    for(Iterator iter = MEC(list).iterator(); iter.hasNext(); ) {
      TypeDecl W = (TypeDecl)iter.next();
      TypeDecl C = W instanceof GenericTypeDecl ? lci(Inv(W, list), W) : W;
      bounds.add(C);
    }
    if(bounds.size() == 1)
      return (TypeDecl)bounds.iterator().next();
    return lookupLUBType(bounds);
  }

    // Declared in Generics.jrag at line 1188
 @SuppressWarnings({"unchecked", "cast"})     public String typeName() {
        ASTNode$State state = state();
        String typeName_value = typeName_compute();
        return typeName_value;
    }

    private String typeName_compute() {
    if(getNumTypeBound() == 0)
      return "<NOTYPE>";
    StringBuffer s = new StringBuffer();
    s.append(getTypeBound(0).type().typeName());
    for(int i = 1; i < getNumTypeBound(); i++)
      s.append(" & " + getTypeBound(i).type().typeName());
    return s.toString();
  }

    // Declared in GenericsSubtype.jrag at line 346
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

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeLUBType(this);  }

    // Declared in GenericsSubtype.jrag at line 361
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeClassDecl(ClassDecl type) {
        ASTNode$State state = state();
        boolean supertypeClassDecl_ClassDecl_value = supertypeClassDecl_compute(type);
        return supertypeClassDecl_ClassDecl_value;
    }

    private boolean supertypeClassDecl_compute(ClassDecl type) {  return type.subtype(lub());  }

    // Declared in GenericsSubtype.jrag at line 362
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeInterfaceDecl(InterfaceDecl type) {
        ASTNode$State state = state();
        boolean supertypeInterfaceDecl_InterfaceDecl_value = supertypeInterfaceDecl_compute(type);
        return supertypeInterfaceDecl_InterfaceDecl_value;
    }

    private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {  return type.subtype(lub());  }

    // Declared in GenericsSubtype.jrag at line 375
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeGLBType(GLBType type) {
        ASTNode$State state = state();
        boolean supertypeGLBType_GLBType_value = supertypeGLBType_compute(type);
        return supertypeGLBType_GLBType_value;
    }

    private boolean supertypeGLBType_compute(GLBType type) {
    ArrayList bounds = new ArrayList(getNumTypeBound());
    for (int i = 0; i < getNumTypeBound(); i++) {
      bounds.add(getTypeBound(i));
    }
    return type == lookupGLBType(bounds);
  }

    // Declared in GenericsCodegen.jrag at line 422
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

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
