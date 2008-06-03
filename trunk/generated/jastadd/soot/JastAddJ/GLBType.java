
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;


public class GLBType extends ReferenceType implements Cloneable {
    public void flushCache() {
        super.flushCache();
        subtype_TypeDecl_visited = new java.util.HashMap(4);
        getSootClassDecl_computed = false;
        getSootClassDecl_value = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GLBType clone() throws CloneNotSupportedException {
        GLBType node = (GLBType)super.clone();
        node.subtype_TypeDecl_visited = new java.util.HashMap(4);
        node.getSootClassDecl_computed = false;
        node.getSootClassDecl_value = null;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GLBType copy() {
      try {
          GLBType node = (GLBType)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public GLBType fullCopy() {
        GLBType res = (GLBType)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.jrag at line 1270


  public HashSet implementedInterfaces(){
    HashSet ret = new HashSet();
    for (int i = 0; i < getNumTypeBound(); i++) {
      ret.addAll(getTypeBound(i).type().implementedInterfaces());
    }
    return ret;
  }

    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 39

    public GLBType() {
        super();

        setChild(new List(), 1);
        setChild(new List(), 2);

    }

    // Declared in Generics.ast at line 12


    // Declared in Generics.ast line 39
    public GLBType(Modifiers p0, String p1, List<BodyDecl> p2, List<Access> p3) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in Generics.ast at line 20


    // Declared in Generics.ast line 39
    public GLBType(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2, List<Access> p3) {
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

  public boolean mayHaveRewrite() { return false; }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 39
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
    // Declared in Generics.ast line 39
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
    // Declared in Generics.ast line 39
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 1);
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
        return (List<BodyDecl>)getChild(1);
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(1);
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 39
    public void setTypeBoundList(List<Access> list) {
        setChild(list, 2);
    }

    // Declared in Generics.ast at line 6


    private int getNumTypeBound = 0;

    // Declared in Generics.ast at line 7

    public int getNumTypeBound() {
        return getTypeBoundList().getNumChild();
    }

    // Declared in Generics.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public Access getTypeBound(int i) {
        return (Access)getTypeBoundList().getChild(i);
    }

    // Declared in Generics.ast at line 15


    public void addTypeBound(Access node) {
        List<Access> list = getTypeBoundList();
        list.addChild(node);
    }

    // Declared in Generics.ast at line 20


    public void setTypeBound(Access node, int i) {
        List<Access> list = getTypeBoundList();
        list.setChild(node, i);
    }

    // Declared in Generics.ast at line 24

    public List<Access> getTypeBounds() {
        return getTypeBoundList();
    }

    // Declared in Generics.ast at line 27

    public List<Access> getTypeBoundsNoTransform() {
        return getTypeBoundListNoTransform();
    }

    // Declared in Generics.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeBoundList() {
        return (List<Access>)getChild(2);
    }

    // Declared in Generics.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Access> getTypeBoundListNoTransform() {
        return (List<Access>)getChildNoTransform(2);
    }

    // Declared in Generics.jrag at line 1260
 @SuppressWarnings({"unchecked", "cast"})     public String typeName() {
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

    // Declared in GenericsSubtype.jrag at line 353
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeLUBType(LUBType type) {
        boolean supertypeLUBType_LUBType_value = supertypeLUBType_compute(type);
        return supertypeLUBType_LUBType_value;
    }

    private boolean supertypeLUBType_compute(LUBType type) {
    ArrayList bounds = new ArrayList(getNumTypeBound());
    for (int i = 0; i < getNumTypeBound(); i++) {
      bounds.add(getTypeBound(i));
    }
    return type == lookupLUBType(bounds);
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

    private boolean subtype_compute(TypeDecl type) {  return type.supertypeGLBType(this);  }

    // Declared in GenericsSubtype.jrag at line 374
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeGLBType(GLBType type) {
        boolean supertypeGLBType_GLBType_value = supertypeGLBType_compute(type);
        return supertypeGLBType_GLBType_value;
    }

    private boolean supertypeGLBType_compute(GLBType type) {  return this == type;  }

    // Declared in GenericsCodegen.jrag at line 414
 @SuppressWarnings({"unchecked", "cast"})     public SootClass getSootClassDecl() {
        if(getSootClassDecl_computed)
            return getSootClassDecl_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        getSootClassDecl_value = getSootClassDecl_compute();
        if(isFinal && num == boundariesCrossed)
            getSootClassDecl_computed = true;
        return getSootClassDecl_value;
    }

    private SootClass getSootClassDecl_compute() {  return typeObject().getSootClassDecl();  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
