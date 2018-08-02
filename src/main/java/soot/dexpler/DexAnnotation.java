package soot.dexpler;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jf.dexlib2.AnnotationVisibility;
import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.AnnotationElement;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodParameter;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.iface.value.AnnotationEncodedValue;
import org.jf.dexlib2.iface.value.ArrayEncodedValue;
import org.jf.dexlib2.iface.value.BooleanEncodedValue;
import org.jf.dexlib2.iface.value.ByteEncodedValue;
import org.jf.dexlib2.iface.value.CharEncodedValue;
import org.jf.dexlib2.iface.value.DoubleEncodedValue;
import org.jf.dexlib2.iface.value.EncodedValue;
import org.jf.dexlib2.iface.value.EnumEncodedValue;
import org.jf.dexlib2.iface.value.FieldEncodedValue;
import org.jf.dexlib2.iface.value.FloatEncodedValue;
import org.jf.dexlib2.iface.value.IntEncodedValue;
import org.jf.dexlib2.iface.value.LongEncodedValue;
import org.jf.dexlib2.iface.value.MethodEncodedValue;
import org.jf.dexlib2.iface.value.ShortEncodedValue;
import org.jf.dexlib2.iface.value.StringEncodedValue;
import org.jf.dexlib2.iface.value.TypeEncodedValue;

import soot.ArrayType;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationBooleanElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationConstants;
import soot.tagkit.AnnotationDefaultTag;
import soot.tagkit.AnnotationDoubleElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationEnumElem;
import soot.tagkit.AnnotationFloatElem;
import soot.tagkit.AnnotationIntElem;
import soot.tagkit.AnnotationLongElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.DeprecatedTag;
import soot.tagkit.EnclosingMethodTag;
import soot.tagkit.Host;
import soot.tagkit.InnerClassAttribute;
import soot.tagkit.InnerClassTag;
import soot.tagkit.ParamNamesTag;
import soot.tagkit.SignatureTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;
import soot.toDex.SootToDexUtils;

/**
 * Converts annotations from Dexlib to Jimple.
 *
 * @author alex
 *
 */
public class DexAnnotation {

  public static final String JAVA_DEPRECATED = "java.lang.Deprecated";
  public static final String DALVIK_ANNOTATION_THROWS = "dalvik.annotation.Throws";
  public static final String DALVIK_ANNOTATION_SIGNATURE = "dalvik.annotation.Signature";
  public static final String DALVIK_ANNOTATION_MEMBERCLASSES = "dalvik.annotation.MemberClasses";
  public static final String DALVIK_ANNOTATION_INNERCLASS = "dalvik.annotation.InnerClass";
  public static final String DALVIK_ANNOTATION_ENCLOSINGMETHOD = "dalvik.annotation.EnclosingMethod";
  public static final String DALVIK_ANNOTATION_ENCLOSINGCLASS = "dalvik.annotation.EnclosingClass";
  public static final String DALVIK_ANNOTATION_DEFAULT = "dalvik.annotation.AnnotationDefault";
  private final Type ARRAY_TYPE = RefType.v("Array");
  private final SootClass clazz;
  private final Dependencies deps;

  public DexAnnotation(SootClass clazz, Dependencies deps) {
    this.clazz = clazz;
    this.deps = deps;
  }

  /**
   * Converts Class annotations from Dexlib to Jimple.
   *
   * @param h
   * @param classDef
   */
  // .annotation "Ldalvik/annotation/AnnotationDefault;"
  // .annotation "Ldalvik/annotation/EnclosingClass;"
  // .annotation "Ldalvik/annotation/EnclosingMethod;"
  // .annotation "Ldalvik/annotation/InnerClass;"
  // .annotation "Ldalvik/annotation/MemberClasses;"
  // .annotation "Ldalvik/annotation/Signature;"
  // .annotation "Ldalvik/annotation/Throws;"

  public void handleClassAnnotation(ClassDef classDef) {
    Set<? extends Annotation> aSet = classDef.getAnnotations();
    if (aSet == null || aSet.isEmpty()) {
      return;
    }

    List<Tag> tags = handleAnnotation(aSet, classDef.getType());
    if (tags == null) {
      return;
    }

    InnerClassAttribute ica = null;
    for (Tag t : tags) {
      if (t != null) {
        if (t instanceof InnerClassTag) {
          if (ica == null) {
            // Do we already have an InnerClassAttribute?
            ica = (InnerClassAttribute) clazz.getTag("InnerClassAttribute");
            // If not, create one
            if (ica == null) {
              ica = new InnerClassAttribute();
              clazz.addTag(ica);
            }
          }
          ica.add((InnerClassTag) t);
        } else if (t instanceof VisibilityAnnotationTag) {
          // If a dalvik/annotation/AnnotationDefault tag is present
          // in a class, its AnnotationElements must be propagated
          // to methods through the creation of new
          // AnnotationDefaultTag.
          VisibilityAnnotationTag vt = (VisibilityAnnotationTag) t;
          for (AnnotationTag a : vt.getAnnotations()) {
            if (a.getType().equals("Ldalvik/annotation/AnnotationDefault;")) {
              for (AnnotationElem ae : a.getElems()) {
                if (ae instanceof AnnotationAnnotationElem) {
                  AnnotationAnnotationElem aae = (AnnotationAnnotationElem) ae;
                  AnnotationTag at = aae.getValue();
                  // extract default elements
                  Map<String, AnnotationElem> defaults = new HashMap<String, AnnotationElem>();
                  for (AnnotationElem aelem : at.getElems()) {
                    defaults.put(aelem.getName(), aelem);
                  }
                  // create default tags containing default
                  // elements
                  // and add tags on methods
                  for (SootMethod sm : clazz.getMethods()) {
                    String methodName = sm.getName();
                    if (defaults.containsKey(methodName)) {
                      AnnotationElem e = defaults.get(methodName);

                      // Okay, the name is the same, but
                      // is it actually the same type?
                      Type annotationType = getSootType(e);
                      boolean isCorrectType = false;
                      if (annotationType == null) {
                        // we do not know the type of
                        // the annotation, so we guess
                        // it's the correct type.
                        isCorrectType = true;
                      } else {
                        if (annotationType.equals(sm.getReturnType())) {
                          isCorrectType = true;
                        } else if (annotationType.equals(ARRAY_TYPE)) {
                          if (sm.getReturnType() instanceof ArrayType) {
                            isCorrectType = true;
                          }
                        }
                      }

                      if (isCorrectType && sm.getParameterCount() == 0) {
                        e.setName("default");
                        AnnotationDefaultTag d = new AnnotationDefaultTag(e);
                        sm.addTag(d);

                        // In case there is more than
                        // one matching method, we only
                        // use the first one
                        defaults.remove(sm.getName());
                      }
                    }
                  }
                  for (Entry<String, AnnotationElem> leftOverEntry : defaults.entrySet()) {
                    // We were not able to find a matching
                    // method for the tag, because the
                    // return signature
                    // does not match
                    SootMethod found = clazz.getMethodByNameUnsafe(leftOverEntry.getKey());
                    AnnotationElem element = leftOverEntry.getValue();
                    if (found != null) {
                      element.setName("default");
                      AnnotationDefaultTag d = new AnnotationDefaultTag(element);
                      found.addTag(d);
                    }
                  }
                }
              }
            }
          }
          if (!(vt.getVisibility() == AnnotationConstants.RUNTIME_INVISIBLE)) {
            clazz.addTag(vt);
          }
        } else {
          clazz.addTag(t);
        }
      }
    }
  }

  private Type getSootType(AnnotationElem e) {
    Type annotationType;
    switch (e.getKind()) {
      case '[': // array
        // Until now we only know it's some kind of array.
        annotationType = ARRAY_TYPE;
        AnnotationArrayElem array = (AnnotationArrayElem) e;
        if (array.getNumValues() > 0) {
          // Try to determine type of the array
          AnnotationElem firstElement = array.getValueAt(0);
          Type type = getSootType(firstElement);
          if (type == null) {
            return null;
          }

          if (type.equals(ARRAY_TYPE)) {
            return ARRAY_TYPE;
          }

          return ArrayType.v(type, 1);
        }
        break;
      case 's': // string
        annotationType = RefType.v("java.lang.String");
        break;
      case 'c': // class
        annotationType = RefType.v("java.lang.Class");
        break;
      case 'e': // enum
        AnnotationEnumElem enumElem = (AnnotationEnumElem) e;
        annotationType = Util.getType(enumElem.getTypeName());
        ;
        break;

      case 'L':
      case 'J':
      case 'S':
      case 'D':
      case 'I':
      case 'F':
      case 'B':
      case 'C':
      case 'V':
      case 'Z':
        annotationType = Util.getType(String.valueOf(e.getKind()));
        break;
      default:
        annotationType = null;
        break;
    }
    return annotationType;
  }

  /**
   * Converts field annotations from Dexlib to Jimple
   *
   * @param h
   * @param f
   */
  public void handleFieldAnnotation(Host h, Field f) {
    Set<? extends Annotation> aSet = f.getAnnotations();
    if (aSet != null && !aSet.isEmpty()) {
      List<Tag> tags = handleAnnotation(aSet, null);
      if (tags != null) {
        for (Tag t : tags) {
          if (t != null) {
            h.addTag(t);
          }
        }
      }
    }
  }

  /**
   * Converts method and method parameters annotations from Dexlib to Jimple
   *
   * @param h
   * @param method
   */
  public void handleMethodAnnotation(Host h, Method method) {
    Set<? extends Annotation> aSet = method.getAnnotations();
    if (!(aSet == null || aSet.isEmpty())) {
      List<Tag> tags = handleAnnotation(aSet, null);
      if (tags != null) {
        for (Tag t : tags) {
          if (t != null) {
            h.addTag(t);
          }
        }
      }
    }

    String[] parameterNames = null;
    int i = 0;
    for (MethodParameter p : method.getParameters()) {
      String name = p.getName();
      if (name != null) {
        parameterNames = new String[method.getParameters().size()];
        parameterNames[i] = name;
      }
      i++;
    }
    if (parameterNames != null) {
      h.addTag(new ParamNamesTag(parameterNames));
    }

    // Is there any parameter annotation?
    boolean doParam = false;
    List<? extends MethodParameter> parameters = method.getParameters();
    for (MethodParameter p : parameters) {
      if (p.getAnnotations().size() > 0) {
        doParam = true;
        break;
      }
    }

    if (doParam) {
      VisibilityParameterAnnotationTag tag
          = new VisibilityParameterAnnotationTag(parameters.size(), AnnotationConstants.RUNTIME_VISIBLE);
      for (MethodParameter p : parameters) {
        List<Tag> tags = handleAnnotation(p.getAnnotations(), null);

        // If we have no tag for this parameter, add a placeholder
        // so that we keep the order intact.
        if (tags == null) {
          tag.addVisibilityAnnotation(null);
          continue;
        }

        VisibilityAnnotationTag paramVat = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE);
        tag.addVisibilityAnnotation(paramVat);

        for (Tag t : tags) {
          if (t == null) {
            continue;
          }

          AnnotationTag vat = null;
          if (!(t instanceof VisibilityAnnotationTag)) {
            if (t instanceof DeprecatedTag) {
              vat = new AnnotationTag("Ljava/lang/Deprecated;");
            } else if (t instanceof SignatureTag) {
              SignatureTag sig = (SignatureTag) t;

              ArrayList<AnnotationElem> sigElements = new ArrayList<AnnotationElem>();
              for (String s : SootToDexUtils.splitSignature(sig.getSignature())) {
                sigElements.add(new AnnotationStringElem(s, 's', "value"));
              }

              AnnotationElem elem = new AnnotationArrayElem(sigElements, '[', "value");
              vat = new AnnotationTag("Ldalvik/annotation/Signature;", Collections.singleton(elem));
            } else {
              throw new RuntimeException("error: unhandled tag for parameter annotation in method " + h + " (" + t + ").");
            }
          } else {
            vat = ((VisibilityAnnotationTag) t).getAnnotations().get(0);
          }

          paramVat.addAnnotation(vat);
        }
      }
      if (tag.getVisibilityAnnotations().size() > 0) {
        h.addTag(tag);
      }
    }

  }

  class MyAnnotations {
    List<AnnotationTag> annotationList = new ArrayList<AnnotationTag>();
    List<Integer> visibilityList = new ArrayList<Integer>();

    public void add(AnnotationTag a, int visibility) {
      annotationList.add(a);
      visibilityList.add(new Integer(visibility));
    }

    public List<AnnotationTag> getAnnotations() {
      return annotationList;
    }

    public List<Integer> getVisibilityList() {
      return visibilityList;
    }
  }

  /**
   *
   * @param annotations
   * @return
   */
  private List<Tag> handleAnnotation(Set<? extends org.jf.dexlib2.iface.Annotation> annotations, String classType) {
    if (annotations == null || annotations.size() == 0) {
      return null;
    }

    List<Tag> tags = new ArrayList<Tag>();
    VisibilityAnnotationTag[] vatg = new VisibilityAnnotationTag[3]; // RUNTIME_VISIBLE,
    // RUNTIME_INVISIBLE,
    // SOURCE_VISIBLE,
    // see
    // soot.tagkit.AnnotationConstants

    for (Annotation a : annotations) {
      addAnnotation(classType, tags, vatg, a);
    }

    for (VisibilityAnnotationTag vat : vatg) {
      if (vat != null) {
        tags.add(vat);
      }
    }

    return tags;

  }

  protected void addAnnotation(String classType, List<Tag> tags, VisibilityAnnotationTag[] vatg, Annotation a) {
    int v = getVisibility(a.getVisibility());

    Tag t = null;
    Type atype = DexType.toSoot(a.getType());
    String atypes = atype.toString();
    int eSize = a.getElements().size();

    switch (atypes) {
      case DALVIK_ANNOTATION_DEFAULT:
        if (eSize != 1) {
          throw new RuntimeException("error: expected 1 element for annotation Default. Got " + eSize + " instead.");
        }
        // get element
        AnnotationElem anne = getElements(a.getElements()).get(0);
        AnnotationTag adt = new AnnotationTag(a.getType());
        adt.addElem(anne);
        if (vatg[v] == null) {
          vatg[v] = new VisibilityAnnotationTag(v);
        }
        vatg[v].addAnnotation(adt);
        break;
      case DALVIK_ANNOTATION_ENCLOSINGCLASS:
        if (eSize != 1) {
          throw new RuntimeException("error: expected 1 element for annotation EnclosingClass. Got " + eSize + " instead.");
        }
        for (AnnotationElement elem : a.getElements()) {
          String outerClass = ((TypeEncodedValue) elem.getValue()).getValue();
          outerClass = Util.dottedClassName(outerClass);

          // If this APK specifies an invalid outer class, we try to
          // repair it
          if (outerClass.equals(clazz.getName())) {
            if (outerClass.contains("$-")) {
              /*
               * This is a special case for generated lambda classes of jack and jill compiler. Generated lambda classes may
               * contain '$' which do not indicate an inner/outer class separator if the '$' occurs after a inner class with
               * a name starting with '-'. Thus we search for '$-' and anything after it including '-' is the inner classes
               * name and anything before it is the outer classes name.
               */
              outerClass = outerClass.substring(0, outerClass.indexOf("$-"));
            } else if (outerClass.contains("$")) {
              // remove everything after the last '$' including
              // the last '$'
              outerClass = outerClass.substring(0, outerClass.lastIndexOf("$"));
            }
          }

          deps.typesToSignature.add(RefType.v(outerClass));
          clazz.setOuterClass(SootResolver.v().makeClassRef(outerClass));
          assert clazz.getOuterClass() != clazz;
        }
        // Do not add annotation tag
        return;
      case DALVIK_ANNOTATION_ENCLOSINGMETHOD:
        // If we don't have any pointer to the enclosing method, we just
        // ignore the annotation
        if (eSize == 0) {
          // Do not add annotation tag
          return;
        }
        // If the pointer is ambiguous, we are in trouble
        if (eSize != 1) {
          throw new RuntimeException("error: expected 1 element for annotation EnclosingMethod. Got " + eSize + " instead.");
        }
        AnnotationStringElem e = (AnnotationStringElem) getElements(a.getElements()).get(0);
        String[] split1 = e.getValue().split("\\ \\|");
        String classString = split1[0];
        String methodString = split1[1];
        String parameters = split1[2];
        String returnType = split1[3];
        String methodSigString = "(" + parameters + ")" + returnType;
        t = new EnclosingMethodTag(classString, methodString, methodSigString);
        String outerClass = classString.replace("/", ".");
        deps.typesToSignature.add(RefType.v(outerClass));
        clazz.setOuterClass(SootResolver.v().makeClassRef(outerClass));
        assert clazz.getOuterClass() != clazz;
        break;
      case DALVIK_ANNOTATION_INNERCLASS:
        int accessFlags = -1; // access flags of the inner class
        String name = null; // name of the inner class
        for (AnnotationElem ele : getElements(a.getElements())) {
          if (ele instanceof AnnotationIntElem && ele.getName().equals("accessFlags")) {
            accessFlags = ((AnnotationIntElem) ele).getValue();
          } else if (ele instanceof AnnotationStringElem && ele.getName().equals("name")) {
            name = ((AnnotationStringElem) ele).getValue();
          } else {
            throw new RuntimeException("Unexpected inner class annotation element");
          }
        }
        if (clazz.hasOuterClass()) {
          // If we have already set an outer class from some other
          // annotation, we use that
          // one.
          outerClass = clazz.getOuterClass().getName();
        } else if (classType.contains("$-")) {
          /*
           * This is a special case for generated lambda classes of jack and jill compiler. Generated lambda classes may
           * contain '$' which do not indicate an inner/outer class separator if the '$' occurs after a inner class with a
           * name starting with '-'. Thus we search for '$-' and anything after it including '-' is the inner classes name
           * and anything before it is the outer classes name.
           */
          outerClass = classType.substring(0, classType.indexOf("$-"));
          if (Util.isByteCodeClassName(classType)) {
            outerClass += ";";
          }
        } else if (classType.contains("$")) {
          // remove everything after the last '$' including the last
          // '$'
          outerClass = classType.substring(0, classType.lastIndexOf("$")) + ";";
          if (Util.isByteCodeClassName(classType)) {
            outerClass += ";";
          }
        } else {
          // Make sure that no funny business is going on if the
          // annotation is broken and does not end in $nn.
          outerClass = null;
        }
        Tag innerTag = new InnerClassTag(DexType.toSootICAT(classType),
            outerClass == null ? null : DexType.toSootICAT(outerClass), name, accessFlags);
        tags.add(innerTag);
        if (outerClass != null && !clazz.hasOuterClass()) {
          String sootOuterClass = Util.dottedClassName(outerClass);
          deps.typesToSignature.add(RefType.v(sootOuterClass));
          clazz.setOuterClass(SootResolver.v().makeClassRef(sootOuterClass));
          assert clazz.getOuterClass() != clazz;
        }
        // Do not add annotation tag
        return;
      case DALVIK_ANNOTATION_MEMBERCLASSES:
        AnnotationArrayElem arre = (AnnotationArrayElem) getElements(a.getElements()).get(0);
        for (AnnotationElem ae : arre.getValues()) {
          AnnotationClassElem c = (AnnotationClassElem) ae;
          String innerClass = c.getDesc();
          if (innerClass.contains("$-")) {
            /*
             * This is a special case for generated lambda classes of jack and jill compiler. Generated lambda classes may
             * contain '$' which do not indicate an inner/outer class separator if the '$' occurs after a inner class with a
             * name starting with '-'. Thus we search for '$-' and anything after it including '-' is the inner classes name
             * and anything before it is the outer classes name.
             */
            int i = innerClass.indexOf("$-");
            outerClass = innerClass.substring(0, i);
            name = innerClass.substring(i + 2).replaceAll(";$", "");
          } else if (innerClass.contains("$")) {
            // remove everything after the last '$' including the
            // last '$'
            int i = innerClass.lastIndexOf("$");
            outerClass = innerClass.substring(0, i);
            name = innerClass.substring(i + 1).replaceAll(";$", "");
          } else {
            // Make sure that no funny business is going on if the
            // annotation is broken and does not end in $nn.
            outerClass = null;
            name = null;
          }

          if (name != null && name.matches("^\\d*$")) {
            // local inner
            // classes
            name = null;
          }

          accessFlags = 0; // seems like this information is lost
          // during the .class -- dx --> .dex
          // process.
          innerTag = new InnerClassTag(DexType.toSootICAT(innerClass),
              outerClass == null ? null : DexType.toSootICAT(outerClass), name, accessFlags);
          tags.add(innerTag);
        }
        // Do not add annotation tag
        return;
      case DALVIK_ANNOTATION_SIGNATURE:
        if (eSize != 1) {
          throw new RuntimeException("error: expected 1 element for annotation Signature. Got " + eSize + " instead.");
        }
        arre = (AnnotationArrayElem) getElements(a.getElements()).get(0);
        String sig = "";
        for (AnnotationElem ae : arre.getValues()) {
          AnnotationStringElem s = (AnnotationStringElem) ae;
          sig += s.getValue();
        }
        t = new SignatureTag(sig);
        break;
      case DALVIK_ANNOTATION_THROWS:
        // Do not add annotation tag
        return;
      case JAVA_DEPRECATED:
        if (eSize != 0) {
          throw new RuntimeException("error: expected 1 element for annotation Deprecated. Got " + eSize + " instead.");
        }
        t = new DeprecatedTag();
        AnnotationTag deprecated = new AnnotationTag("Ljava/lang/Deprecated;");
        if (vatg[v] == null) {
          vatg[v] = new VisibilityAnnotationTag(v);
        }
        vatg[v].addAnnotation(deprecated);
        break;
      default:
        addNormalAnnotation(vatg, a, v);
        break;
    }

    tags.add(t);
  }

  /**
   * Processes a normal annotation and adds it to the proper visibility annotation tag in the given array
   * 
   * @param vatg
   *          the visibility annotation tags for different visibility levels
   * @param a
   *          the annotation
   * @param v
   *          the visibility
   */
  protected void addNormalAnnotation(VisibilityAnnotationTag[] vatg, Annotation a, int v) {
    if (vatg[v] == null) {
      vatg[v] = new VisibilityAnnotationTag(v);
    }

    AnnotationTag tag = new AnnotationTag(a.getType());
    for (AnnotationElem e : getElements(a.getElements())) {
      tag.addElem(e);
    }
    vatg[v].addAnnotation(tag);
  }

  private ArrayList<AnnotationElem> getElements(Set<? extends AnnotationElement> set) {
    ArrayList<AnnotationElem> aelemList = new ArrayList<AnnotationElem>();
    for (AnnotationElement ae : set) {

      // Debug.printDbg("element: ", ae.getName() ," ", ae.getValue() ,"
      // type: ", ae.getClass());
      // Debug.printDbg("value type: ", ae.getValue().getValueType() ,"
      // class: ", ae.getValue().getClass());

      List<AnnotationElem> eList = handleAnnotationElement(ae, Collections.singletonList(ae.getValue()));
      if (eList != null) {
        aelemList.addAll(eList);
      }
    }
    return aelemList;
  }

  private ArrayList<AnnotationElem> handleAnnotationElement(AnnotationElement ae, List<? extends EncodedValue> evList) {
    ArrayList<AnnotationElem> aelemList = new ArrayList<AnnotationElem>();

    for (EncodedValue ev : evList) {
      int type = ev.getValueType();
      AnnotationElem elem = null;
      switch (type) {
        case 0x00: // BYTE
        {
          ByteEncodedValue v = (ByteEncodedValue) ev;
          elem = new AnnotationIntElem(v.getValue(), 'B', ae.getName());
          break;
        }
        case 0x02: // SHORT
        {
          ShortEncodedValue v = (ShortEncodedValue) ev;
          elem = new AnnotationIntElem(v.getValue(), 'S', ae.getName());
          break;
        }
        case 0x03: // CHAR
        {
          CharEncodedValue v = (CharEncodedValue) ev;
          elem = new AnnotationIntElem(v.getValue(), 'C', ae.getName());
          break;
        }
        case 0x04: // INT
        {
          IntEncodedValue v = (IntEncodedValue) ev;
          elem = new AnnotationIntElem(v.getValue(), 'I', ae.getName());
          break;
        }
        case 0x06: // LONG
        {
          LongEncodedValue v = (LongEncodedValue) ev;
          elem = new AnnotationLongElem(v.getValue(), 'J', ae.getName());
          break;
        }
        case 0x10: // FLOAT
        {
          FloatEncodedValue v = (FloatEncodedValue) ev;
          elem = new AnnotationFloatElem(v.getValue(), 'F', ae.getName());
          break;
        }
        case 0x11: // DOUBLE
        {
          DoubleEncodedValue v = (DoubleEncodedValue) ev;
          elem = new AnnotationDoubleElem(v.getValue(), 'D', ae.getName());
          break;
        }
        case 0x17: // STRING
        {
          StringEncodedValue v = (StringEncodedValue) ev;
          elem = new AnnotationStringElem(v.getValue(), 's', ae.getName());
          break;
        }
        case 0x18: // TYPE
        {
          TypeEncodedValue v = (TypeEncodedValue) ev;
          elem = new AnnotationClassElem(v.getValue(), 'c', ae.getName());
          break;
        }
        case 0x19: // FIELD (Dalvik specific?)
        {
          FieldEncodedValue v = (FieldEncodedValue) ev;
          FieldReference fr = v.getValue();
          String fieldSig = "";
          fieldSig += DexType.toSootAT(fr.getDefiningClass()) + ": ";
          fieldSig += DexType.toSootAT(fr.getType()) + " ";
          fieldSig += fr.getName();
          elem = new AnnotationStringElem(fieldSig, 'f', ae.getName());
          break;
        }
        case 0x1a: // METHOD (Dalvik specific?)
        {
          MethodEncodedValue v = (MethodEncodedValue) ev;
          MethodReference mr = v.getValue();

          String className = DexType.toSootICAT(mr.getDefiningClass());
          String returnType = DexType.toSootAT(mr.getReturnType());
          String methodName = mr.getName();
          String parameters = "";
          for (CharSequence p : mr.getParameterTypes()) {
            parameters += DexType.toSootAT(p.toString());
          }
          String mSig = className + " |" + methodName + " |" + parameters + " |" + returnType;
          elem = new AnnotationStringElem(mSig, 'M', ae.getName());
          break;
        }
        case 0x1b: // ENUM : Warning -> encoding Dalvik specific!
        {
          EnumEncodedValue v = (EnumEncodedValue) ev;
          FieldReference fr = v.getValue();
          elem = new AnnotationEnumElem(DexType.toSootAT(fr.getType()).toString(), fr.getName(), 'e', ae.getName());
          break;
        }
        case 0x1c: // ARRAY
        {
          ArrayEncodedValue v = (ArrayEncodedValue) ev;
          ArrayList<AnnotationElem> l = handleAnnotationElement(ae, v.getValue());
          if (l != null) {
            elem = new AnnotationArrayElem(l, '[', ae.getName());
          }
          break;
        }
        case 0x1d: // ANNOTATION
        {
          AnnotationEncodedValue v = (AnnotationEncodedValue) ev;
          AnnotationTag t = new AnnotationTag(DexType.toSootAT(v.getType()).toString());
          for (AnnotationElement newElem : v.getElements()) {
            List<EncodedValue> l = new ArrayList<EncodedValue>();
            l.add(newElem.getValue());
            List<AnnotationElem> aList = handleAnnotationElement(newElem, l);
            if (aList != null) {
              for (AnnotationElem e : aList) {
                t.addElem(e);
              }
            }
          }
          elem = new AnnotationAnnotationElem(t, '@', ae.getName());
          break;
        }
        case 0x1e: // NULL (Dalvik specific?)
        {
          elem = new AnnotationStringElem(null, 'N', ae.getName());
          break;
        }
        case 0x1f: // BOOLEAN
        {
          BooleanEncodedValue v = (BooleanEncodedValue) ev;
          elem = new AnnotationBooleanElem(v.getValue(), 'Z', ae.getName());
          break;
        }
        default: {
          throw new RuntimeException("Unknown annotation element 0x" + Integer.toHexString(type));
        }
      } // switch (type)

      if (elem != null) {
        aelemList.add(elem);
      }

    } // for (EncodedValue)

    return aelemList;
  }

  /**
   * Converts Dexlib visibility to Jimple visibility.
   *
   * In Dalvik: VISIBILITY_BUILD 0x00 intended only to be visible at build time (e.g., during compilation of other code)
   * VISIBILITY_RUNTIME 0x01 intended to visible at runtime VISIBILITY_SYSTEM 0x02 intended to visible at runtime, but only
   * to the underlying system (and not to regular user code)
   *
   * @param visibility
   *          Dexlib visibility
   * @return Jimple visibility
   */
  protected static int getVisibility(int visibility) {
    switch (AnnotationVisibility.getVisibility(visibility)) {
      case "runtime":
        return AnnotationConstants.RUNTIME_VISIBLE;
      case "system":
        return AnnotationConstants.RUNTIME_INVISIBLE;
      case "build":
        return AnnotationConstants.SOURCE_VISIBLE;
      default:
        throw new RuntimeException(String.format("error: unknown annotation visibility: '%d'", visibility));
    }
  }

}
