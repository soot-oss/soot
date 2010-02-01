
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class Modifiers extends ASTNode<ASTNode> implements Cloneable {
    public void flushCache() {
        super.flushCache();
        isPublic_computed = false;
        isPrivate_computed = false;
        isProtected_computed = false;
        isStatic_computed = false;
        isFinal_computed = false;
        isAbstract_computed = false;
        isVolatile_computed = false;
        isTransient_computed = false;
        isStrictfp_computed = false;
        isSynchronized_computed = false;
        isNative_computed = false;
        isSynthetic_computed = false;
        numModifier_String_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public Modifiers clone() throws CloneNotSupportedException {
        Modifiers node = (Modifiers)super.clone();
        node.isPublic_computed = false;
        node.isPrivate_computed = false;
        node.isProtected_computed = false;
        node.isStatic_computed = false;
        node.isFinal_computed = false;
        node.isAbstract_computed = false;
        node.isVolatile_computed = false;
        node.isTransient_computed = false;
        node.isStrictfp_computed = false;
        node.isSynchronized_computed = false;
        node.isNative_computed = false;
        node.isSynthetic_computed = false;
        node.numModifier_String_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Modifiers copy() {
      try {
          Modifiers node = (Modifiers)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Modifiers fullCopy() {
        Modifiers res = (Modifiers)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Modifiers.jrag at line 312


  // 8.4.3
  public void checkModifiers() {
    super.checkModifiers();
    if(numProtectionModifiers() > 1)
      error("only one public, protected, private allowed");
    if(numModifier("static") > 1)
      error("only one static allowed");
    // 8.4.3.1
    // 8.4.3.2
    // 8.1.1.2
    if(numCompletenessModifiers() > 1)
      error("only one of final, abstract, volatile allowed");
    if(numModifier("synchronized") > 1)
      error("only one synchronized allowed");
    if(numModifier("transient") > 1)
      error("only one transient allowed");
    if(numModifier("native") > 1)
      error("only one native allowed");
    if(numModifier("strictfp") > 1)
      error("only one strictfp allowed");

    if(isPublic() && !mayBePublic())
      error("modifier public not allowed in this context");
    if(isPrivate() && !mayBePrivate())
      error("modifier private not allowed in this context");
    if(isProtected() && !mayBeProtected())
      error("modifier protected not allowed in this context");
    if(isStatic() && !mayBeStatic())
      error("modifier static not allowed in this context");
    if(isFinal() && !mayBeFinal())
      error("modifier final not allowed in this context");
    if(isAbstract() && !mayBeAbstract())
      error("modifier abstract not allowed in this context");
    if(isVolatile() && !mayBeVolatile())
      error("modifier volatile not allowed in this context");
    if(isTransient() && !mayBeTransient())
      error("modifier transient not allowed in this context");
    if(isStrictfp() && !mayBeStrictfp())
      error("modifier strictfp not allowed in this context");
    if(isSynchronized() && !mayBeSynchronized())
      error("modifier synchronized not allowed in this context");
    if(isNative() && !mayBeNative())
      error("modifier native not allowed in this context");
  }

    // Declared in PrettyPrint.jadd at line 434


  public void toString(StringBuffer s) {
    for(int i = 0; i < getNumModifier(); i++) {
      getModifier(i).toString(s);
      s.append(" ");
    }
  }

    // Declared in AnnotationsCodegen.jrag at line 83

  /*
  refine EmitJimple public void VariableDeclaration.jimplify2(Body b) {
    EmitJimple.VariableDeclaration.jimplify2(b);
    ArrayList c = new ArrayList();
    getModifiers().addAllAnnotations(c);
    for(Iterator iter = c.iterator(); iter.hasNext(); ) {
      soot.tagkit.Tag tag = (soot.tagkit.Tag)iter.next();
      local.addTag(tag);
    }
  }
  */

  public void addSourceOnlyAnnotations(Collection c) {
    if(new soot.options.JBOptions(soot.PhaseOptions.v().getPhaseOptions("jb")).
       preserve_source_annotations()) {
	    for(int i = 0; i < getNumModifier(); i++) {
	      if(getModifier(i) instanceof Annotation) {
	        Annotation a = (Annotation)getModifier(i);
	        if(!a.isRuntimeVisible() && !a.isRuntimeInvisible()) {
      		    soot.tagkit.VisibilityAnnotationTag tag = new soot.tagkit.VisibilityAnnotationTag(soot.tagkit.AnnotationConstants.SOURCE_VISIBLE);
        		ArrayList elements = new ArrayList(1);
        		a.appendAsAttributeTo(elements);
        		tag.addAnnotation((soot.tagkit.AnnotationTag)elements.get(0));
        		c.add(tag);
	        }
	      }
	    }
    }
  }

    // Declared in AnnotationsCodegen.jrag at line 101

  
  public void addAllAnnotations(Collection c) {
    for(int i = 0; i < getNumModifier(); i++) {
      if(getModifier(i) instanceof Annotation) {
        Annotation a = (Annotation)getModifier(i);
        a.appendAsAttributeTo(c);
      }
    }
  }

    // Declared in AnnotationsCodegen.jrag at line 110


  public void addRuntimeVisibleAnnotationsAttribute(Collection c) {
    Collection annotations = runtimeVisibleAnnotations();
    if(!annotations.isEmpty()) {
      soot.tagkit.VisibilityAnnotationTag tag = new soot.tagkit.VisibilityAnnotationTag(soot.tagkit.AnnotationConstants.RUNTIME_VISIBLE);
      for(Iterator iter = annotations.iterator(); iter.hasNext(); ) {
        Annotation annotation = (Annotation)iter.next();
        ArrayList elements = new ArrayList(1);
        annotation.appendAsAttributeTo(elements);
        tag.addAnnotation((soot.tagkit.AnnotationTag)elements.get(0));
      }
      c.add(tag);
    }
  }

    // Declared in AnnotationsCodegen.jrag at line 125


  // 4.8.16
  public void addRuntimeInvisibleAnnotationsAttribute(Collection c) {
    Collection annotations = runtimeInvisibleAnnotations();
    if(!annotations.isEmpty()) {
      soot.tagkit.VisibilityAnnotationTag tag = new soot.tagkit.VisibilityAnnotationTag(soot.tagkit.AnnotationConstants.RUNTIME_INVISIBLE);
      for(Iterator iter = annotations.iterator(); iter.hasNext(); ) {
        Annotation annotation = (Annotation)iter.next();
        ArrayList elements = new ArrayList(1);
        annotation.appendAsAttributeTo(elements);
        tag.addAnnotation((soot.tagkit.AnnotationTag)elements.get(0));
      }
      c.add(tag);
    }
  }

    // Declared in AnnotationsCodegen.jrag at line 210


  public Collection runtimeVisibleAnnotations() {
    Collection annotations = new ArrayList();
    for(int i = 0; i < getNumModifier(); i++)
      if(getModifier(i).isRuntimeVisible())
        annotations.add(getModifier(i));
    return annotations;
  }

    // Declared in AnnotationsCodegen.jrag at line 266


  public Collection runtimeInvisibleAnnotations() {
    Collection annotations = new ArrayList();
    for(int i = 0; i < getNumModifier(); i++)
      if(getModifier(i).isRuntimeInvisible())
        annotations.add(getModifier(i));
    return annotations;
  }

    // Declared in AnnotationsCodegen.jrag at line 290


  // Add ACC_ANNOTATION flag to generated class file
  public static final int ACC_ANNOTATION = 0x2000;

    // Declared in EnumsCodegen.jrag at line 12

    // add flags to enums
  public static final int ACC_ENUM = 0x4000;

    // Declared in GenericsCodegen.jrag at line 325



  public static final int ACC_BRIDGE = 0x0040;

    // Declared in VariableArityParametersCodegen.jrag at line 78


  public static final int ACC_VARARGS = 0x0080;

    // Declared in java.ast at line 3
    // Declared in java.ast line 193

    public Modifiers() {
        super();

        setChild(new List(), 0);

    }

    // Declared in java.ast at line 11


    // Declared in java.ast line 193
    public Modifiers(List<Modifier> p0) {
        setChild(p0, 0);
    }

    // Declared in java.ast at line 15


  protected int numChildren() {
    return 1;
  }

    // Declared in java.ast at line 18

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 193
    public void setModifierList(List<Modifier> list) {
        setChild(list, 0);
    }

    // Declared in java.ast at line 6


    public int getNumModifier() {
        return getModifierList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Modifier getModifier(int i) {
        return (Modifier)getModifierList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addModifier(Modifier node) {
        List<Modifier> list = (parent == null || state == null) ? getModifierListNoTransform() : getModifierList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addModifierNoTransform(Modifier node) {
        List<Modifier> list = getModifierListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setModifier(Modifier node, int i) {
        List<Modifier> list = getModifierList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Modifier> getModifiers() {
        return getModifierList();
    }

    // Declared in java.ast at line 31

    public List<Modifier> getModifiersNoTransform() {
        return getModifierListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Modifier> getModifierList() {
        List<Modifier> list = (List<Modifier>)getChild(0);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Modifier> getModifierListNoTransform() {
        return (List<Modifier>)getChildNoTransform(0);
    }

    protected boolean isPublic_computed = false;
    protected boolean isPublic_value;
    // Declared in Modifiers.jrag at line 370
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPublic() {
        if(isPublic_computed) {
            return isPublic_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isPublic_value = isPublic_compute();
        if(isFinal && num == state().boundariesCrossed)
            isPublic_computed = true;
        return isPublic_value;
    }

    private boolean isPublic_compute() {  return numModifier("public") != 0;  }

    protected boolean isPrivate_computed = false;
    protected boolean isPrivate_value;
    // Declared in Modifiers.jrag at line 371
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPrivate() {
        if(isPrivate_computed) {
            return isPrivate_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isPrivate_value = isPrivate_compute();
        if(isFinal && num == state().boundariesCrossed)
            isPrivate_computed = true;
        return isPrivate_value;
    }

    private boolean isPrivate_compute() {  return numModifier("private") != 0;  }

    protected boolean isProtected_computed = false;
    protected boolean isProtected_value;
    // Declared in Modifiers.jrag at line 372
 @SuppressWarnings({"unchecked", "cast"})     public boolean isProtected() {
        if(isProtected_computed) {
            return isProtected_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isProtected_value = isProtected_compute();
        if(isFinal && num == state().boundariesCrossed)
            isProtected_computed = true;
        return isProtected_value;
    }

    private boolean isProtected_compute() {  return numModifier("protected") != 0;  }

    protected boolean isStatic_computed = false;
    protected boolean isStatic_value;
    // Declared in Modifiers.jrag at line 373
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStatic() {
        if(isStatic_computed) {
            return isStatic_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isStatic_value = isStatic_compute();
        if(isFinal && num == state().boundariesCrossed)
            isStatic_computed = true;
        return isStatic_value;
    }

    private boolean isStatic_compute() {  return numModifier("static") != 0;  }

    protected boolean isFinal_computed = false;
    protected boolean isFinal_value;
    // Declared in Modifiers.jrag at line 374
 @SuppressWarnings({"unchecked", "cast"})     public boolean isFinal() {
        if(isFinal_computed) {
            return isFinal_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isFinal_value = isFinal_compute();
        if(isFinal && num == state().boundariesCrossed)
            isFinal_computed = true;
        return isFinal_value;
    }

    private boolean isFinal_compute() {  return numModifier("final") != 0;  }

    protected boolean isAbstract_computed = false;
    protected boolean isAbstract_value;
    // Declared in Modifiers.jrag at line 375
 @SuppressWarnings({"unchecked", "cast"})     public boolean isAbstract() {
        if(isAbstract_computed) {
            return isAbstract_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isAbstract_value = isAbstract_compute();
        if(isFinal && num == state().boundariesCrossed)
            isAbstract_computed = true;
        return isAbstract_value;
    }

    private boolean isAbstract_compute() {  return numModifier("abstract") != 0;  }

    protected boolean isVolatile_computed = false;
    protected boolean isVolatile_value;
    // Declared in Modifiers.jrag at line 376
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVolatile() {
        if(isVolatile_computed) {
            return isVolatile_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isVolatile_value = isVolatile_compute();
        if(isFinal && num == state().boundariesCrossed)
            isVolatile_computed = true;
        return isVolatile_value;
    }

    private boolean isVolatile_compute() {  return numModifier("volatile") != 0;  }

    protected boolean isTransient_computed = false;
    protected boolean isTransient_value;
    // Declared in Modifiers.jrag at line 377
 @SuppressWarnings({"unchecked", "cast"})     public boolean isTransient() {
        if(isTransient_computed) {
            return isTransient_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isTransient_value = isTransient_compute();
        if(isFinal && num == state().boundariesCrossed)
            isTransient_computed = true;
        return isTransient_value;
    }

    private boolean isTransient_compute() {  return numModifier("transient") != 0;  }

    protected boolean isStrictfp_computed = false;
    protected boolean isStrictfp_value;
    // Declared in Modifiers.jrag at line 378
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStrictfp() {
        if(isStrictfp_computed) {
            return isStrictfp_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isStrictfp_value = isStrictfp_compute();
        if(isFinal && num == state().boundariesCrossed)
            isStrictfp_computed = true;
        return isStrictfp_value;
    }

    private boolean isStrictfp_compute() {  return numModifier("strictfp") != 0;  }

    protected boolean isSynchronized_computed = false;
    protected boolean isSynchronized_value;
    // Declared in Modifiers.jrag at line 379
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSynchronized() {
        if(isSynchronized_computed) {
            return isSynchronized_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isSynchronized_value = isSynchronized_compute();
        if(isFinal && num == state().boundariesCrossed)
            isSynchronized_computed = true;
        return isSynchronized_value;
    }

    private boolean isSynchronized_compute() {  return numModifier("synchronized") != 0;  }

    protected boolean isNative_computed = false;
    protected boolean isNative_value;
    // Declared in Modifiers.jrag at line 380
 @SuppressWarnings({"unchecked", "cast"})     public boolean isNative() {
        if(isNative_computed) {
            return isNative_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isNative_value = isNative_compute();
        if(isFinal && num == state().boundariesCrossed)
            isNative_computed = true;
        return isNative_value;
    }

    private boolean isNative_compute() {  return numModifier("native") != 0;  }

    protected boolean isSynthetic_computed = false;
    protected boolean isSynthetic_value;
    // Declared in Modifiers.jrag at line 382
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSynthetic() {
        if(isSynthetic_computed) {
            return isSynthetic_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        isSynthetic_value = isSynthetic_compute();
        if(isFinal && num == state().boundariesCrossed)
            isSynthetic_computed = true;
        return isSynthetic_value;
    }

    private boolean isSynthetic_compute() {  return numModifier("synthetic") != 0;  }

    // Declared in Modifiers.jrag at line 384
 @SuppressWarnings({"unchecked", "cast"})     public int numProtectionModifiers() {
        ASTNode$State state = state();
        int numProtectionModifiers_value = numProtectionModifiers_compute();
        return numProtectionModifiers_value;
    }

    private int numProtectionModifiers_compute() {  return numModifier("public") + numModifier("protected") + numModifier("private");  }

    // Declared in Modifiers.jrag at line 387
 @SuppressWarnings({"unchecked", "cast"})     public int numCompletenessModifiers() {
        ASTNode$State state = state();
        int numCompletenessModifiers_value = numCompletenessModifiers_compute();
        return numCompletenessModifiers_value;
    }

    private int numCompletenessModifiers_compute() {  return numModifier("abstract") + numModifier("final") + numModifier("volatile");  }

    protected java.util.Map numModifier_String_values;
    // Declared in Modifiers.jrag at line 390
 @SuppressWarnings({"unchecked", "cast"})     public int numModifier(String name) {
        Object _parameters = name;
if(numModifier_String_values == null) numModifier_String_values = new java.util.HashMap(4);
        if(numModifier_String_values.containsKey(_parameters)) {
            return ((Integer)numModifier_String_values.get(_parameters)).intValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        int numModifier_String_value = numModifier_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            numModifier_String_values.put(_parameters, Integer.valueOf(numModifier_String_value));
        return numModifier_String_value;
    }

    private int numModifier_compute(String name) {
    int n = 0;
    for(int i = 0; i < getNumModifier(); i++) {
      String s = getModifier(i).getID();
      if(s.equals(name))
        n++;
    }
    return n;
  }

    // Declared in Annotations.jrag at line 214
 @SuppressWarnings({"unchecked", "cast"})     public Annotation annotation(TypeDecl typeDecl) {
        ASTNode$State state = state();
        Annotation annotation_TypeDecl_value = annotation_compute(typeDecl);
        return annotation_TypeDecl_value;
    }

    private Annotation annotation_compute(TypeDecl typeDecl) {
    for(int i = 0; i < getNumModifier(); i++) {
      if(getModifier(i) instanceof Annotation) {
        Annotation a = (Annotation)getModifier(i);
        if(a.type() == typeDecl)
          return a;
      }
    }
    return null;
  }

    // Declared in Annotations.jrag at line 289
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasAnnotationSuppressWarnings(String s) {
        ASTNode$State state = state();
        boolean hasAnnotationSuppressWarnings_String_value = hasAnnotationSuppressWarnings_compute(s);
        return hasAnnotationSuppressWarnings_String_value;
    }

    private boolean hasAnnotationSuppressWarnings_compute(String s) {
    Annotation a = annotation(lookupType("java.lang", "SuppressWarnings"));
    if(a != null && a.getNumElementValuePair() == 1 && a.getElementValuePair(0).getName().equals("value"))
      return a.getElementValuePair(0).getElementValue().hasValue(s);
    return false;
  }

    // Declared in Annotations.jrag at line 319
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasDeprecatedAnnotation() {
        ASTNode$State state = state();
        boolean hasDeprecatedAnnotation_value = hasDeprecatedAnnotation_compute();
        return hasDeprecatedAnnotation_value;
    }

    private boolean hasDeprecatedAnnotation_compute() {  return annotation(lookupType("java.lang", "Deprecated")) != null;  }

    // Declared in Modifiers.jrag at line 356
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl hostType() {
        ASTNode$State state = state();
        TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
        return hostType_value;
    }

    // Declared in Modifiers.jrag at line 358
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBePublic() {
        ASTNode$State state = state();
        boolean mayBePublic_value = getParent().Define_boolean_mayBePublic(this, null);
        return mayBePublic_value;
    }

    // Declared in Modifiers.jrag at line 359
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBePrivate() {
        ASTNode$State state = state();
        boolean mayBePrivate_value = getParent().Define_boolean_mayBePrivate(this, null);
        return mayBePrivate_value;
    }

    // Declared in Modifiers.jrag at line 360
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBeProtected() {
        ASTNode$State state = state();
        boolean mayBeProtected_value = getParent().Define_boolean_mayBeProtected(this, null);
        return mayBeProtected_value;
    }

    // Declared in Modifiers.jrag at line 361
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBeStatic() {
        ASTNode$State state = state();
        boolean mayBeStatic_value = getParent().Define_boolean_mayBeStatic(this, null);
        return mayBeStatic_value;
    }

    // Declared in Modifiers.jrag at line 362
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBeFinal() {
        ASTNode$State state = state();
        boolean mayBeFinal_value = getParent().Define_boolean_mayBeFinal(this, null);
        return mayBeFinal_value;
    }

    // Declared in Modifiers.jrag at line 363
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBeAbstract() {
        ASTNode$State state = state();
        boolean mayBeAbstract_value = getParent().Define_boolean_mayBeAbstract(this, null);
        return mayBeAbstract_value;
    }

    // Declared in Modifiers.jrag at line 364
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBeVolatile() {
        ASTNode$State state = state();
        boolean mayBeVolatile_value = getParent().Define_boolean_mayBeVolatile(this, null);
        return mayBeVolatile_value;
    }

    // Declared in Modifiers.jrag at line 365
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBeTransient() {
        ASTNode$State state = state();
        boolean mayBeTransient_value = getParent().Define_boolean_mayBeTransient(this, null);
        return mayBeTransient_value;
    }

    // Declared in Modifiers.jrag at line 366
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBeStrictfp() {
        ASTNode$State state = state();
        boolean mayBeStrictfp_value = getParent().Define_boolean_mayBeStrictfp(this, null);
        return mayBeStrictfp_value;
    }

    // Declared in Modifiers.jrag at line 367
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBeSynchronized() {
        ASTNode$State state = state();
        boolean mayBeSynchronized_value = getParent().Define_boolean_mayBeSynchronized(this, null);
        return mayBeSynchronized_value;
    }

    // Declared in Modifiers.jrag at line 368
 @SuppressWarnings({"unchecked", "cast"})     public boolean mayBeNative() {
        ASTNode$State state = state();
        boolean mayBeNative_value = getParent().Define_boolean_mayBeNative(this, null);
        return mayBeNative_value;
    }

    // Declared in Annotations.jrag at line 56
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lookupType(String packageName, String typeName) {
        ASTNode$State state = state();
        TypeDecl lookupType_String_String_value = getParent().Define_TypeDecl_lookupType(this, null, packageName, typeName);
        return lookupType_String_String_value;
    }

    // Declared in Annotations.jrag at line 424
    public Annotation Define_Annotation_lookupAnnotation(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
        if(caller == getModifierListNoTransform()) { 
   int index = caller.getIndexOfChild(child);
{
    return annotation(typeDecl);
  }
}
        return getParent().Define_Annotation_lookupAnnotation(this, caller, typeDecl);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
