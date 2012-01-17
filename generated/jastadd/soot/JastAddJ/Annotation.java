package soot.JastAddJ;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;


/**
 * @ast node
 * @declaredat Annotations.ast:6
 */
public class Annotation extends Modifier implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    decl_computed = false;
    decl_value = null;
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Annotation clone() throws CloneNotSupportedException {
    Annotation node = (Annotation)super.clone();
    node.decl_computed = false;
    node.decl_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Annotation copy() {
      try {
        Annotation node = (Annotation)clone();
        if(children != null) node.children = (ASTNode[])children.clone();
        return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
  }
  /**
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Annotation fullCopy() {
    Annotation res = (Annotation)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:41
   */
  public void checkModifiers() {
    super.checkModifiers();
    if(decl() instanceof AnnotationDecl) {
      AnnotationDecl T = (AnnotationDecl)decl();
      Annotation m = T.annotation(lookupType("java.lang.annotation", "Target"));
      if(m != null && m.getNumElementValuePair() == 1 && m.getElementValuePair(0).getName().equals("value")) {
        ElementValue v = m.getElementValuePair(0).getElementValue();
        //System.out.println("ElementValue: \n" + v.dumpTree());
        //System.out.println("Annotation: \n" + dumpTree());
        if(!v.validTarget(this))
          error("annotation type " + T.typeName() + " is not applicable to this kind of declaration");
      }
    }
  }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:241
   */
  public void checkOverride() {
    if(decl().fullName().equals("java.lang.Override") && enclosingBodyDecl() instanceof MethodDecl) {
      MethodDecl m = (MethodDecl)enclosingBodyDecl();
      if(!m.hostType().isClassDecl())
        error("override annotation not valid for interface methods");
      else {
        boolean found = false;
        for(Iterator iter = m.hostType().ancestorMethods(m.signature()).iterator(); iter.hasNext(); ) {
          MethodDecl decl = (MethodDecl)iter.next();
          if(m.overrides(decl) && decl.hostType().isClassDecl())
            found = true;
        }
        if(!found)
          error("method does not override a method from its superclass");
      }
    }
  }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:383
   */
  public void typeCheck() {
    if(!decl().isAnnotationDecl()) {
      /* TypeName names the annotation type corresponding to the annotation. It is a
      compile-time error if TypeName does not name an annotation type.*/
      if(!decl().isUnknown())
        error(decl().typeName() + " is not an annotation type");
    } else {
      TypeDecl typeDecl = decl();
      /* It is a compile-time error if a declaration is annotated with more than one
      annotation for a given annotation type.*/
      if(lookupAnnotation(typeDecl) != this)
        error("duplicate annotation " + typeDecl.typeName());
      /* Annotations must contain an element-value pair for every element of the
      corresponding annotation type, except for those elements with default
      values, or a compile-time error occurs. Annotations may, but are not
      required to, contain element-value pairs for elements with default values.*/
      for(int i = 0; i < typeDecl.getNumBodyDecl(); i++) {
        if(typeDecl.getBodyDecl(i) instanceof MethodDecl) {
          MethodDecl decl = (MethodDecl)typeDecl.getBodyDecl(i);
          if(elementValueFor(decl.name()) == null && (!(decl instanceof AnnotationMethodDecl) || !((AnnotationMethodDecl)decl).hasDefaultValue()))
            error("missing value for " + decl.name());
        }
      }
      /* The Identifier in an ElementValuePair must be the simple name of one of the
      elements of the annotation type identified by TypeName in the containing
      annotation. Otherwise, a compile-time error occurs. (In other words, the
      identifier in an element-value pair must also be a method name in the interface
      identified by TypeName.) */
      for(int i = 0; i < getNumElementValuePair(); i++) {
        ElementValuePair pair = getElementValuePair(i);
        if(typeDecl.memberMethods(pair.getName()).isEmpty())
          error("can not find element named " + pair.getName() + " in " + typeDecl.typeName());
      }
    }
    checkOverride();
  }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:578
   */
  public void toString(StringBuffer s) {
    s.append("@");
    getAccess().toString(s);
    s.append("(");
    for(int i = 0; i < getNumElementValuePair(); i++) {
      if(i != 0)
        s.append(", ");
      getElementValuePair(i).toString(s);
    }
    s.append(")");
  }
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:305
   */
  public void appendAsAttributeTo(Collection list) {
      soot.tagkit.AnnotationTag tag = new soot.tagkit.AnnotationTag(decl().typeDescriptor(), getNumElementValuePair());
      ArrayList elements = new ArrayList(getNumElementValuePair());
      for(int i = 0; i < getNumElementValuePair(); i++) {
        String name = getElementValuePair(i).getName();
        ElementValue value = getElementValuePair(i).getElementValue();
        value.appendAsAttributeTo(elements, name);
      }
      tag.setElems(elements);
      list.add(tag);
  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:1
   */
  public Annotation() {
    super();

    setChild(new List(), 1);

  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:8
   */
  public Annotation(String p0, Access p1, List<ElementValuePair> p2) {
    setID(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:13
   */
  public Annotation(beaver.Symbol p0, Access p1, List<ElementValuePair> p2) {
    setID(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:21
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Annotations.ast:27
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:5
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:8
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Getter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:19
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for Access
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:5
   */
  public void setAccess(Access node) {
    setChild(node, 0);
  }
  /**
   * Getter for Access
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:12
   */
  public Access getAccess() {
    return (Access)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:18
   */
  public Access getAccessNoTransform() {
    return (Access)getChildNoTransform(0);
  }
  /**
   * Setter for ElementValuePairList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:5
   */
  public void setElementValuePairList(List<ElementValuePair> list) {
    setChild(list, 1);
  }
  /**
   * @return number of children in ElementValuePairList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:12
   */
  public int getNumElementValuePair() {
    return getElementValuePairList().getNumChild();
  }
  /**
   * Getter for child in list ElementValuePairList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementValuePair getElementValuePair(int i) {
    return (ElementValuePair)getElementValuePairList().getChild(i);
  }
  /**
   * Add element to list ElementValuePairList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:27
   */
  public void addElementValuePair(ElementValuePair node) {
    List<ElementValuePair> list = (parent == null || state == null) ? getElementValuePairListNoTransform() : getElementValuePairList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:34
   */
  public void addElementValuePairNoTransform(ElementValuePair node) {
    List<ElementValuePair> list = getElementValuePairListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ElementValuePairList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:42
   */
  public void setElementValuePair(ElementValuePair node, int i) {
    List<ElementValuePair> list = getElementValuePairList();
    list.setChild(node, i);
  }
  /**
   * Getter for ElementValuePair list.
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:50
   */
  public List<ElementValuePair> getElementValuePairs() {
    return getElementValuePairList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:56
   */
  public List<ElementValuePair> getElementValuePairsNoTransform() {
    return getElementValuePairListNoTransform();
  }
  /**
   * Getter for list ElementValuePairList
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ElementValuePair> getElementValuePairList() {
    List<ElementValuePair> list = (List<ElementValuePair>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ElementValuePair> getElementValuePairListNoTransform() {
    return (List<ElementValuePair>)getChildNoTransform(1);
  }
  /**
   * @apilevel internal
   */
  protected boolean decl_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl decl_value;
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:420
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl decl() {
    if(decl_computed) {
      return decl_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    decl_value = decl_compute();
if(isFinal && num == state().boundariesCrossed) decl_computed = true;
    return decl_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl decl_compute() {  return getAccess().type();  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:432
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementValue elementValueFor(String name) {
      ASTNode$State state = state();
    ElementValue elementValueFor_String_value = elementValueFor_compute(name);
    return elementValueFor_String_value;
  }
  /**
   * @apilevel internal
   */
  private ElementValue elementValueFor_compute(String name) {
    for(int i = 0; i < getNumElementValuePair(); i++) {
      ElementValuePair pair = getElementValuePair(i);
      if(pair.getName().equals(name))
        return pair.getElementValue();
    }
    return null;
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:510
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
      ASTNode$State state = state();
    TypeDecl type_value = type_compute();
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {  return getAccess().type();  }
  /* An annotation on an annotation type declaration is known as a meta-annotation.
  An annotation type may be used to annotate its own declaration. More generally,
  circularities in the transitive closure of the "annotates" relation are
  permitted. For example, it is legal to annotate an annotation type declaration
  with another annotation type, and to annotate the latter type's declaration
  with the former type. (The pre-defined meta-annotation types contain several
  such circularities.)
  Comment: no problems with reference attributes.
  * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:539
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isMetaAnnotation() {
      ASTNode$State state = state();
    boolean isMetaAnnotation_value = isMetaAnnotation_compute();
    return isMetaAnnotation_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isMetaAnnotation_compute() {  return hostType().isAnnotationDecl();  }
  /**
   * @attribute syn
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:144
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isRuntimeVisible() {
      ASTNode$State state = state();
    boolean isRuntimeVisible_value = isRuntimeVisible_compute();
    return isRuntimeVisible_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isRuntimeVisible_compute() {
    Annotation a = decl().annotation(lookupType("java.lang.annotation", "Retention"));
    if(a == null) return false;
    ElementConstantValue value = (ElementConstantValue)a.getElementValuePair(0).getElementValue();
    Variable v = value.getExpr().varDecl();
    return v != null && v.name().equals("RUNTIME");
  }
  /**
   * @attribute syn
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:154
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isRuntimeInvisible() {
      ASTNode$State state = state();
    boolean isRuntimeInvisible_value = isRuntimeInvisible_compute();
    return isRuntimeInvisible_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isRuntimeInvisible_compute() {
    Annotation a = decl().annotation(lookupType("java.lang.annotation", "Retention"));
    if(a == null) return true; // default bahavior if not annotated
    ElementConstantValue value = (ElementConstantValue)a.getElementValuePair(0).getElementValue();
    Variable v = value.getExpr().varDecl();
    return v != null &&  v.name().equals("CLASS");
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:55
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupType(String packageName, String typeName) {
      ASTNode$State state = state();
    TypeDecl lookupType_String_String_value = getParent().Define_TypeDecl_lookupType(this, null, packageName, typeName);
    return lookupType_String_String_value;
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:69
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean mayUseAnnotationTarget(String name) {
      ASTNode$State state = state();
    boolean mayUseAnnotationTarget_String_value = getParent().Define_boolean_mayUseAnnotationTarget(this, null, name);
    return mayUseAnnotationTarget_String_value;
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:258
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl enclosingBodyDecl() {
      ASTNode$State state = state();
    BodyDecl enclosingBodyDecl_value = getParent().Define_BodyDecl_enclosingBodyDecl(this, null);
    return enclosingBodyDecl_value;
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:422
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Annotation lookupAnnotation(TypeDecl typeDecl) {
      ASTNode$State state = state();
    Annotation lookupAnnotation_TypeDecl_value = getParent().Define_Annotation_lookupAnnotation(this, null, typeDecl);
    return lookupAnnotation_TypeDecl_value;
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:540
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl hostType() {
      ASTNode$State state = state();
    TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
    return hostType_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:460
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingAnnotationDecl(ASTNode caller, ASTNode child) {
    if(caller == getElementValuePairListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return decl();
    }
    return getParent().Define_TypeDecl_enclosingAnnotationDecl(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:545
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getAccessNoTransform()) {
      return NameType.TYPE_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
