
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;
// 9.6 Annotation Types

public class AnnotationDecl extends InterfaceDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        getSuperInterfaceIdList_computed = false;
        getSuperInterfaceIdList_value = null;
        containsElementOf_TypeDecl_visited = new java.util.HashMap(4);
    }
     @SuppressWarnings({"unchecked", "cast"})  public AnnotationDecl clone() throws CloneNotSupportedException {
        AnnotationDecl node = (AnnotationDecl)super.clone();
        node.getSuperInterfaceIdList_computed = false;
        node.getSuperInterfaceIdList_value = null;
        node.containsElementOf_TypeDecl_visited = new java.util.HashMap(4);
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
    s.append(" {\n");
    indent++;
    for(int i=0; i < getNumBodyDecl(); i++) {
      getBodyDecl(i).toString(s);
    }
    indent--;
    s.append(indent() + "}\n");
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

  public boolean mayHaveRewrite() { return false; }

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
    // Declared in java.ast line 64
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

    // Declared in Annotations.ast at line 2
    // Declared in Annotations.ast line 2
    public void setSuperInterfaceIdList(List<Access> list) {
        setChild(list, 2);
    }

    // Declared in Annotations.ast at line 6


    private int getNumSuperInterfaceId = 0;

    // Declared in Annotations.ast at line 7

    public int getNumSuperInterfaceId() {
        return getSuperInterfaceIdList().getNumChild();
    }

    // Declared in Annotations.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public Access getSuperInterfaceId(int i) {
        return (Access)getSuperInterfaceIdList().getChild(i);
    }

    // Declared in Annotations.ast at line 15


    public void addSuperInterfaceId(Access node) {
        List<Access> list = getSuperInterfaceIdList();
        list.addChild(node);
    }

    // Declared in Annotations.ast at line 20


    public void setSuperInterfaceId(Access node, int i) {
        List<Access> list = getSuperInterfaceIdList();
        list.setChild(node, i);
    }

    // Declared in Annotations.ast at line 24

    public List<Access> getSuperInterfaceIds() {
        return getSuperInterfaceIdList();
    }

    // Declared in Annotations.ast at line 27

    public List<Access> getSuperInterfaceIdsNoTransform() {
        return getSuperInterfaceIdListNoTransform();
    }

    // Declared in Annotations.ast at line 31


    public List<Access> getSuperInterfaceIdListNoTransform() {
        return (List<Access>)getChildNoTransform(2);
    }

    // Declared in Annotations.ast at line 35


    protected int getSuperInterfaceIdListChildPosition() {
        return 2;
    }

    protected boolean getSuperInterfaceIdList_computed = false;
    protected List getSuperInterfaceIdList_value;
    // Declared in Annotations.jrag at line 99
 @SuppressWarnings({"unchecked", "cast"})     public List getSuperInterfaceIdList() {
        if(getSuperInterfaceIdList_computed)
            return (List)ASTNode.getChild(this, getSuperInterfaceIdListChildPosition());
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        getSuperInterfaceIdList_value = getSuperInterfaceIdList_compute();
        setSuperInterfaceIdList(getSuperInterfaceIdList_value);
        if(isFinal && num == boundariesCrossed)
            getSuperInterfaceIdList_computed = true;
        return (List)ASTNode.getChild(this, getSuperInterfaceIdListChildPosition());
    }

    private List getSuperInterfaceIdList_compute() {
    return new List().add(new TypeAccess("java.lang.annotation", "Annotation"));
  }

    // Declared in Annotations.jrag at line 134
 @SuppressWarnings({"unchecked", "cast"})     public boolean isValidAnnotationMethodReturnType() {
        boolean isValidAnnotationMethodReturnType_value = isValidAnnotationMethodReturnType_compute();
        return isValidAnnotationMethodReturnType_value;
    }

    private boolean isValidAnnotationMethodReturnType_compute() {  return true;  }

    protected java.util.Map containsElementOf_TypeDecl_visited;
    protected java.util.Set containsElementOf_TypeDecl_computed = new java.util.HashSet(4);
    protected java.util.Set containsElementOf_TypeDecl_initialized = new java.util.HashSet(4);
    protected java.util.Map containsElementOf_TypeDecl_values = new java.util.HashMap(4);
 @SuppressWarnings({"unchecked", "cast"})     public boolean containsElementOf(TypeDecl typeDecl) {
        Object _parameters = typeDecl;
if(containsElementOf_TypeDecl_visited == null) containsElementOf_TypeDecl_visited = new java.util.HashMap(4);
if(containsElementOf_TypeDecl_values == null) containsElementOf_TypeDecl_values = new java.util.HashMap(4);
        if(containsElementOf_TypeDecl_computed.contains(_parameters))
            return ((Boolean)containsElementOf_TypeDecl_values.get(_parameters)).booleanValue();
        if (!containsElementOf_TypeDecl_initialized.contains(_parameters)) {
            containsElementOf_TypeDecl_initialized.add(_parameters);
            containsElementOf_TypeDecl_values.put(_parameters, Boolean.valueOf(false));
        }
        if (!IN_CIRCLE) {
            IN_CIRCLE = true;
            int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
            CIRCLE_INDEX = 1;
            boolean new_containsElementOf_TypeDecl_value;
            do {
                containsElementOf_TypeDecl_visited.put(_parameters, new Integer(CIRCLE_INDEX));
                CHANGE = false;
                new_containsElementOf_TypeDecl_value = containsElementOf_compute(typeDecl);
                if (new_containsElementOf_TypeDecl_value!=((Boolean)containsElementOf_TypeDecl_values.get(_parameters)).booleanValue())
                    CHANGE = true;
                containsElementOf_TypeDecl_values.put(_parameters, Boolean.valueOf(new_containsElementOf_TypeDecl_value));
                CIRCLE_INDEX++;
            } while (CHANGE);
            if(isFinal && num == boundariesCrossed)
{
            containsElementOf_TypeDecl_computed.add(_parameters);
            }
            else {
            RESET_CYCLE = true;
            containsElementOf_compute(typeDecl);
            RESET_CYCLE = false;
            containsElementOf_TypeDecl_computed.remove(_parameters);
            containsElementOf_TypeDecl_initialized.remove(_parameters);
            }
            IN_CIRCLE = false; 
            return new_containsElementOf_TypeDecl_value;
        }
        if(!new Integer(CIRCLE_INDEX).equals(containsElementOf_TypeDecl_visited.get(_parameters))) {
            containsElementOf_TypeDecl_visited.put(_parameters, new Integer(CIRCLE_INDEX));
            if (RESET_CYCLE) {
                containsElementOf_TypeDecl_computed.remove(_parameters);
                containsElementOf_TypeDecl_initialized.remove(_parameters);
                return ((Boolean)containsElementOf_TypeDecl_values.get(_parameters)).booleanValue();
            }
            boolean new_containsElementOf_TypeDecl_value = containsElementOf_compute(typeDecl);
            if (new_containsElementOf_TypeDecl_value!=((Boolean)containsElementOf_TypeDecl_values.get(_parameters)).booleanValue())
                CHANGE = true;
            containsElementOf_TypeDecl_values.put(_parameters, Boolean.valueOf(new_containsElementOf_TypeDecl_value));
            return new_containsElementOf_TypeDecl_value;
        }
        return ((Boolean)containsElementOf_TypeDecl_values.get(_parameters)).booleanValue();
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
        boolean isAnnotationDecl_value = isAnnotationDecl_compute();
        return isAnnotationDecl_value;
    }

    private boolean isAnnotationDecl_compute() {  return true;  }

    // Declared in AnnotationsCodegen.jrag at line 290
 @SuppressWarnings({"unchecked", "cast"})     public int sootTypeModifiers() {
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
