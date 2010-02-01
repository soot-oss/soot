
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;
// 9.6 Annotation Types

public class AnnotationDecl extends InterfaceDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        getSuperInterfaceIdList_computed = false;
        getSuperInterfaceIdList_value = null;
        containsElementOf_TypeDecl_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public AnnotationDecl clone() throws CloneNotSupportedException {
        AnnotationDecl node = (AnnotationDecl)super.clone();
        node.getSuperInterfaceIdList_computed = false;
        node.getSuperInterfaceIdList_value = null;
        node.containsElementOf_TypeDecl_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AnnotationDecl copy() {
      try {
          AnnotationDecl node = (AnnotationDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AnnotationDecl fullCopy() {
        AnnotationDecl res = (AnnotationDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Annotations.jrag at line 103


  public void typeCheck() {
    super.typeCheck();
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof MethodDecl) {
        MethodDecl m = (MethodDecl)getBodyDecl(i);
        if(!m.type().isValidAnnotationMethodReturnType())
          m.error("invalid type for annotation member");
        if(m.annotationMethodOverride())
          m.error("annotation method overrides " + m.signature());
      }
    }
    if(containsElementOf(this))
      error("cyclic annotation element type");
  }

    // Declared in Annotations.jrag at line 558


  public void toString(StringBuffer s) {
    getModifiers().toString(s);
    s.append("@interface " + name());
    s.append(" {");
    for(int i=0; i < getNumBodyDecl(); i++) {
      getBodyDecl(i).toString(s);
    }
    s.append(indent() + "}");
  }

    // Declared in Annotations.ast at line 3
    // Declared in Annotations.ast line 2

    public AnnotationDecl() {
        super();

        setChild(new List(), 1);
        setChild(new List(), 2);

    }

    // Declared in Annotations.ast at line 12


    // Declared in Annotations.ast line 2
    public AnnotationDecl(Modifiers p0, String p1, List<BodyDecl> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(new List(), 2);
    }

    // Declared in Annotations.ast at line 20


    // Declared in Annotations.ast line 2
    public AnnotationDecl(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
        setChild(new List(), 2);
    }

    // Declared in Annotations.ast at line 27


  protected int numChildren() {
    return 2;
  }

    // Declared in Annotations.ast at line 30

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
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 1);
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
        List<BodyDecl> list = (List<BodyDecl>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(1);
    }

    // Declared in Annotations.ast at line 2
    // Declared in Annotations.ast line 2
    public void setSuperInterfaceIdList(List<Access> list) {
        setChild(list, 2);
    }

    // Declared in Annotations.ast at line 6


    public int getNumSuperInterfaceId() {
        return getSuperInterfaceIdList().getNumChild();
    }

    // Declared in Annotations.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Access getSuperInterfaceId(int i) {
        return (Access)getSuperInterfaceIdList().getChild(i);
    }

    // Declared in Annotations.ast at line 14


    public void addSuperInterfaceId(Access node) {
        List<Access> list = (parent == null || state == null) ? getSuperInterfaceIdListNoTransform() : getSuperInterfaceIdList();
        list.addChild(node);
    }

    // Declared in Annotations.ast at line 19


    public void addSuperInterfaceIdNoTransform(Access node) {
        List<Access> list = getSuperInterfaceIdListNoTransform();
        list.addChild(node);
    }

    // Declared in Annotations.ast at line 24


    public void setSuperInterfaceId(Access node, int i) {
        List<Access> list = getSuperInterfaceIdList();
        list.setChild(node, i);
    }

    // Declared in Annotations.ast at line 28

    public List<Access> getSuperInterfaceIds() {
        return getSuperInterfaceIdList();
    }

    // Declared in Annotations.ast at line 31

    public List<Access> getSuperInterfaceIdsNoTransform() {
        return getSuperInterfaceIdListNoTransform();
    }

    // Declared in Annotations.ast at line 35


    public List<Access> getSuperInterfaceIdListNoTransform() {
        return (List<Access>)getChildNoTransform(2);
    }

    // Declared in Annotations.ast at line 39


    protected int getSuperInterfaceIdListChildPosition() {
        return 2;
    }

    protected boolean getSuperInterfaceIdList_computed = false;
    protected List getSuperInterfaceIdList_value;
    // Declared in Annotations.jrag at line 99
 @SuppressWarnings({"unchecked", "cast"})     public List getSuperInterfaceIdList() {
        if(getSuperInterfaceIdList_computed) {
            return (List)ASTNode.getChild(this, getSuperInterfaceIdListChildPosition());
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getSuperInterfaceIdList_value = getSuperInterfaceIdList_compute();
        setSuperInterfaceIdList(getSuperInterfaceIdList_value);
        if(isFinal && num == state().boundariesCrossed)
            getSuperInterfaceIdList_computed = true;
        return (List)ASTNode.getChild(this, getSuperInterfaceIdListChildPosition());
    }

    private List getSuperInterfaceIdList_compute() {
    return new List().add(new TypeAccess("java.lang.annotation", "Annotation"));
  }

    // Declared in Annotations.jrag at line 134
 @SuppressWarnings({"unchecked", "cast"})     public boolean isValidAnnotationMethodReturnType() {
        ASTNode$State state = state();
        boolean isValidAnnotationMethodReturnType_value = isValidAnnotationMethodReturnType_compute();
        return isValidAnnotationMethodReturnType_value;
    }

    private boolean isValidAnnotationMethodReturnType_compute() {  return true;  }

    protected java.util.Map containsElementOf_TypeDecl_values;
    // Declared in Annotations.jrag at line 144
 @SuppressWarnings({"unchecked", "cast"})     public boolean containsElementOf(TypeDecl typeDecl) {
        Object _parameters = typeDecl;
if(containsElementOf_TypeDecl_values == null) containsElementOf_TypeDecl_values = new java.util.HashMap(4);
        ASTNode$State.CircularValue _value;
        if(containsElementOf_TypeDecl_values.containsKey(_parameters)) {
            Object _o = containsElementOf_TypeDecl_values.get(_parameters);
            if(!(_o instanceof ASTNode$State.CircularValue)) {
                return ((Boolean)_o).booleanValue();
            }
            else
                _value = (ASTNode$State.CircularValue)_o;
        }
        else {
            _value = new ASTNode$State.CircularValue();
            containsElementOf_TypeDecl_values.put(_parameters, _value);
            _value.value = Boolean.valueOf(false);
        }
        ASTNode$State state = state();
        if (!state.IN_CIRCLE) {
            state.IN_CIRCLE = true;
            int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
            boolean new_containsElementOf_TypeDecl_value;
            do {
                _value.visited = new Integer(state.CIRCLE_INDEX);
                state.CHANGE = false;
                new_containsElementOf_TypeDecl_value = containsElementOf_compute(typeDecl);
                if (new_containsElementOf_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
                    state.CHANGE = true;
                    _value.value = Boolean.valueOf(new_containsElementOf_TypeDecl_value);
                }
                state.CIRCLE_INDEX++;
            } while (state.CHANGE);
            if(isFinal && num == state().boundariesCrossed)
{
                containsElementOf_TypeDecl_values.put(_parameters, new_containsElementOf_TypeDecl_value);
            }
            else {
                containsElementOf_TypeDecl_values.remove(_parameters);
            state.RESET_CYCLE = true;
            containsElementOf_compute(typeDecl);
            state.RESET_CYCLE = false;
            }
            state.IN_CIRCLE = false; 
            return new_containsElementOf_TypeDecl_value;
        }
        if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
            _value.visited = new Integer(state.CIRCLE_INDEX);
            boolean new_containsElementOf_TypeDecl_value = containsElementOf_compute(typeDecl);
            if (state.RESET_CYCLE) {
                containsElementOf_TypeDecl_values.remove(_parameters);
            }
            else if (new_containsElementOf_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
                state.CHANGE = true;
                _value.value = new_containsElementOf_TypeDecl_value;
            }
            return new_containsElementOf_TypeDecl_value;
        }
        return ((Boolean)_value.value).booleanValue();
    }

    private boolean containsElementOf_compute(TypeDecl typeDecl) {
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof MethodDecl) {
        MethodDecl m = (MethodDecl)getBodyDecl(i);
        if(m.type() == typeDecl)
          return true;
        if(m.type() instanceof AnnotationDecl && ((AnnotationDecl)m.type()).containsElementOf(typeDecl))
          return true;
      }
    }
    return false;
  }

    // Declared in Annotations.jrag at line 542
 @SuppressWarnings({"unchecked", "cast"})     public boolean isAnnotationDecl() {
        ASTNode$State state = state();
        boolean isAnnotationDecl_value = isAnnotationDecl_compute();
        return isAnnotationDecl_value;
    }

    private boolean isAnnotationDecl_compute() {  return true;  }

    // Declared in AnnotationsCodegen.jrag at line 291
 @SuppressWarnings({"unchecked", "cast"})     public int sootTypeModifiers() {
        ASTNode$State state = state();
        int sootTypeModifiers_value = sootTypeModifiers_compute();
        return sootTypeModifiers_value;
    }

    private int sootTypeModifiers_compute() {  return super.sootTypeModifiers() | Modifiers.ACC_ANNOTATION;  }

    // Declared in Annotations.jrag at line 77
    public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
        if(caller == getModifiersNoTransform()) {
            return name.equals("ANNOTATION_TYPE") || name.equals("TYPE");
        }
        return super.Define_boolean_mayUseAnnotationTarget(caller, child, name);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
