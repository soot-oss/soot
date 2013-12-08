package soot.dexpler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.AnnotationElement;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
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

import soot.Type;
import soot.tagkit.AnnotationAnnotationElem;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationBooleanElem;
import soot.tagkit.AnnotationClassElem;
import soot.tagkit.AnnotationConstants;
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
import soot.tagkit.SignatureTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;

/**
 * Converts annotations from Dexlib to Jimple.
 *
 * @author alex
 *
 */
public class DexAnnotation {
    
    DexFile dexFile = null;
    
    DexAnnotation(DexFile df) {
        this.dexFile = df;
    }

    /**
     * Converts Class annotations from Dexlib to Jimple.
     * 
     * @param h
     * @param classDef
     */
//    .annotation "Ldalvik/annotation/AnnotationDefault;"
//    .annotation "Ldalvik/annotation/EnclosingClass;"
//    .annotation "Ldalvik/annotation/EnclosingMethod;"
//    .annotation "Ldalvik/annotation/InnerClass;"
//    .annotation "Ldalvik/annotation/MemberClasses;"
//    .annotation "Ldalvik/annotation/Signature;"
//    .annotation "Ldalvik/annotation/Throws;"

    void handleClassAnnotation(Host h, ClassDef classDef) {
        Set<? extends Annotation> aSet = classDef.getAnnotations();
        if (aSet == null || aSet.isEmpty())
            return;
        for (Tag t : handleAnnotation(aSet, classDef.getType()))
            h.addTag(t);

    }

    /**
     * Converts field annotations from Dexlib to Jimple
     * @param h
     * @param f
     */
    void handleFieldAnnotation(Host h, Field f) {
        Set<? extends Annotation> aSet = f.getAnnotations();
        if (aSet == null || aSet.isEmpty())
            return;
        for (Tag t : handleAnnotation(aSet, null))
            h.addTag(t);
    }

    /**
     * Converts method and method parameters annotations from Dexlib to Jimple
     * @param h
     * @param method
     */
    void handleMethodAnnotation(Host h, Method method) {

        Set<? extends Annotation> aSet = method.getAnnotations();
        if (!(aSet == null || aSet.isEmpty())) {
            for (Tag t : handleAnnotation(aSet, null))
                h.addTag(t);
        }
        
        // Is there any parameter annotation?
        boolean doParam = false;
        List<? extends MethodParameter> parameters = method.getParameters();
        for (MethodParameter p : parameters)
            if (p.getAnnotations().size() > 0) {
                doParam = true;
                break;
            }
        if (doParam) {
            VisibilityParameterAnnotationTag tag = new VisibilityParameterAnnotationTag(parameters.size(), 0);
            for (MethodParameter p : parameters) {
                
                List<Tag> tags = handleAnnotation(p.getAnnotations(), null);
                for (Tag t : tags) {
                    if (! (t instanceof VisibilityAnnotationTag))
                        continue;
                    VisibilityAnnotationTag vat = (VisibilityAnnotationTag)t;
//                    int visibility = AnnotationConstants.RUNTIME_VISIBLE;
//                    VisibilityAnnotationTag vat = new VisibilityAnnotationTag(visibility);
//                    vat.addAnnotation(at);
                    tag.addVisibilityAnnotation(vat);
                }
                
            }
            h.addTag(tag);
            
        }

    }


    
    class MyAnnotations {
        List<AnnotationTag> annotationList = new ArrayList<AnnotationTag>();
        List<Integer> visibilityList = new ArrayList<Integer>();
        public void add(AnnotationTag a, int visibility) { 
            annotationList.add(a); 
            visibilityList.add(new Integer(visibility));
        }    
        public List<AnnotationTag> getAnnotations() { return annotationList; }
        public List<Integer> getVisibilityList() { return visibilityList; }
    }

    /**
     * 
     * @param annotations
     * @return
     */
    List<Tag> handleAnnotation(Set<? extends org.jf.dexlib2.iface.Annotation> annotations, String classType) {
        List<Tag> tags = new ArrayList<Tag>();

        ArrayList<Tag> innerClassList = new ArrayList<Tag>();
        VisibilityAnnotationTag vat = new VisibilityAnnotationTag(0);
        
        if (annotations.size() == 0)
            return tags;

        for (Annotation a: annotations) {
            Tag t = null;
            //AnnotationTag aTag = new AnnotationTag(DexType.toSoot(a.getType()).toString());
            Type atype = DexType.toSoot(a.getType());
            String atypes = atype.toString();
            int eSize = a.getElements().size();
            Debug.printDbg("annotation type: ", atypes ," elements: ", eSize);

            if (atypes.equals("dalvik.annotation.AnnotationDefault")) {
                if (eSize != 1)
                    throw new RuntimeException("error: expected 1 element for annotation Default. Got "+ eSize +" instead.");
                // get element
                AnnotationElem e = getElements(a.getElements()).get(0);
                AnnotationTag adt = new AnnotationTag(a.getType());
                adt.addElem(e);
                VisibilityAnnotationTag tag = new VisibilityAnnotationTag(getVisibility(2));
                tag.addAnnotation(adt);
                t = tag;
                
            } else if (atypes.equals("dalvik.annotation.EnclosingClass")) {
                if (eSize != 1)
                    throw new RuntimeException("error: expected 1 element for annotation EnclosingClass. Got "+ eSize +" instead.");
                // EnclosingClass comes in pair with InnerClass.
                // Those are generated from a single InnerClassTag,
                // that is re-constructed only for the InnerClass Dalvik
                // annotation.
                continue;
                
            } else if (atypes.equals("dalvik.annotation.EnclosingMethod")) {
                if (eSize != 1)
                    throw new RuntimeException("error: expected 1 element for annotation EnclosingMethod. Got "+ eSize +" instead.");
                AnnotationStringElem e = (AnnotationStringElem) getElements(a.getElements()).get(0);
                String[] split1 = e.getValue().split("\\ \\|");
                String classString = split1[0];
                String methodString = split1[1];
                String parameters = split1[2];
                String returnType = split1[3];
                String methodSigString = "("+ parameters +")"+ returnType;
                t = new EnclosingMethodTag(
                        classString, 
                        methodString, 
                        methodSigString);       
                
            } else if (atypes.equals("dalvik.annotation.InnerClass")) {
                if (eSize != 2)
                    throw new RuntimeException("error: expected 2 elements for annotation InnerClass. Got "+ eSize +" instead.");
                List<AnnotationElem> elements = getElements(a.getElements());
                AnnotationIntElem i = null;
                AnnotationStringElem s = null;
                for (AnnotationElem e: elements) {
                    Debug.printDbg("class: ", e.getClass());
                }
                if (elements.get(0) instanceof AnnotationIntElem) {
                    i = (AnnotationIntElem) elements.get(0);
                    s = (AnnotationStringElem) elements.get(1);
                } else {
                    i = (AnnotationIntElem) elements.get(1);
                    s = (AnnotationStringElem) elements.get(0);
                }
                String name = s.getValue();
                String outerClass = null;
                if (name == null)
                    outerClass = null;
                else
                    outerClass = classType.replaceFirst("\\$"+ name, "");
                String innerClass = classType;
                int accessFlags = i.getValue();
                Tag innerTag = new InnerClassTag(
                        DexType.toSootICAT(innerClass), 
                        DexType.toSootICAT(outerClass),
                        name, 
                        accessFlags);
                innerClassList.add(innerTag);
                continue;
                
            } else if (atypes.equals("dalvik.annotation.MemberClasses")) {
                
                AnnotationArrayElem e = (AnnotationArrayElem) getElements(a.getElements()).get(0); 
                String sig = "";
                Debug.printDbg("memberclasses size: ", e.getValues().size());
                for (AnnotationElem ae : e.getValues()) {
                    Debug.printDbg("annotation ", ae);
                }
                for (AnnotationElem ae : e.getValues()) {
                    AnnotationClassElem c = (AnnotationClassElem) ae;
                    sig += c.getDesc() +" -- "+ c.getName() +" ;; ";
                    Debug.printDbg("s: ", c.getDesc());
                    Debug.printDbg("signature: ", sig);
                    String innerClass = c.getDesc();
                    String outerClass = innerClass.replaceFirst("\\$[^\\$]*", "");
                    String name = classType.replaceFirst("\\.*\\$", "");
                    int accessFlags = 0; // seems like this information is lost during the .class -- dx --> .dex process.
                    Tag innerTag = new InnerClassTag(
                            DexType.toSootICAT(innerClass), 
                            DexType.toSootICAT(outerClass), 
                            name, 
                            accessFlags);
                    innerClassList.add(innerTag);
                }               
                continue;
                
            } else if (atypes.equals("dalvik.annotation.Signature")) {
                if (eSize != 1)
                    throw new RuntimeException("error: expected 1 element for annotation Signature. Got "+ eSize +" instead.");
                AnnotationArrayElem e = (AnnotationArrayElem) getElements(a.getElements()).get(0); 
                String sig = "";
                for (AnnotationElem ae : e.getValues()) {
                    AnnotationStringElem s = (AnnotationStringElem) ae;
                    sig += s.getValue();
                }                   
                t = new SignatureTag(sig);
                
            } else if (atypes.equals("dalvik.annotation.Throws")) {
                // this is handled in soot.dexpler.DexMethod
                continue; 
                
            } else if (atypes.equals("java.lang.Deprecated")) {
                if (eSize != 0)
                    throw new RuntimeException("error: expected 1 element for annotation Deprecated. Got "+ eSize +" instead.");
                t = new DeprecatedTag(); 
                
            } else {
                Debug.printDbg("read visibility tag: ");
                AnnotationTag at = new AnnotationTag(a.getType());
                for (AnnotationElem e: getElements(a.getElements()))
                    at.addElem(e);              
                vat.addAnnotation(at);
                continue;
            }

            tags.add(t);
        }
        
        if (innerClassList.size() > 0) {
            InnerClassAttribute ica = new InnerClassAttribute(innerClassList);
            tags.add(ica);
        }
        
        if (vat.getAnnotations() != null && vat.getAnnotations().size() > 0 )
            tags.add(vat);
        
        return tags;

    }
    
    private ArrayList<AnnotationElem> getElements(Set<? extends AnnotationElement> set) {
        ArrayList<AnnotationElem> aelemList = new ArrayList<AnnotationElem>();
        for (AnnotationElement ae: set) {
            
            //Debug.printDbg("element: ", ae.getName() ," ", ae.getValue() ," type: ", ae.getClass());
            //Debug.printDbg("value type: ", ae.getValue().getValueType() ," class: ", ae.getValue().getClass());

            List<EncodedValue> evList = new ArrayList<EncodedValue>();
            evList.add(ae.getValue());
            Debug.printDbg("   element type: ", ae.getValue().getClass());
            List<AnnotationElem> eList = handleAnnotationElement(ae, evList);
            aelemList.addAll(eList);
        }
        return aelemList;
    }

    private ArrayList<AnnotationElem> handleAnnotationElement(AnnotationElement ae, List<EncodedValue> evList) {

        ArrayList<AnnotationElem> aelemList = new ArrayList<AnnotationElem>();

        for (EncodedValue ev: evList) {
            int type = ev.getValueType();
            AnnotationElem elem = null;
            switch (type) {
            case 0x00: // BYTE
            {
                ByteEncodedValue v = (ByteEncodedValue)ev;
                elem = new AnnotationIntElem(v.getValue(), 'B', ae.getName());
                break;
            }
            case 0x02: // SHORT
            {
                ShortEncodedValue v = (ShortEncodedValue)ev;
                elem = new AnnotationIntElem(v.getValue(), 'S', ae.getName());
                break;
            }
            case 0x03: // CHAR
            {
                CharEncodedValue v = (CharEncodedValue)ev;
                elem = new AnnotationIntElem(v.getValue(), 'C', ae.getName());
                break;
            }
            case 0x04: // INT
            {
                IntEncodedValue v = (IntEncodedValue)ev;
                elem = new AnnotationIntElem(v.getValue(), 'I', ae.getName());
                break;
            }
            case 0x06: // LONG
            {
                LongEncodedValue v = (LongEncodedValue)ev;
                elem = new AnnotationLongElem(v.getValue(), 'J', ae.getName());
                break;
            }
            case 0x10: // FLOAT
            {
                FloatEncodedValue v = (FloatEncodedValue)ev;
                elem = new AnnotationFloatElem(v.getValue(), 'F', ae.getName());
                break;
            }
            case 0x11: // DOUBLE
            {
                DoubleEncodedValue v = (DoubleEncodedValue)ev;
                elem = new AnnotationDoubleElem(v.getValue(), 'D', ae.getName());
                break;
            }
            case 0x17: // STRING
            {
                StringEncodedValue v = (StringEncodedValue)ev;
                elem = new AnnotationStringElem(v.getValue(), 's', ae.getName());
                Debug.printDbg("value for string: ", v.getValue());
                break;
            }
            case 0x18: // TYPE
            {
                TypeEncodedValue v = (TypeEncodedValue)ev;
                elem = new AnnotationClassElem(
                        DexType.toSootAT(v.getValue()), 
                        'c', 
                        ae.getName());
                break;
            }
            case 0x19: // FIELD (Dalvik specific?)
            {
                FieldEncodedValue v = (FieldEncodedValue)ev;
                FieldReference fr = v.getValue();
                String fieldSig = "";
                fieldSig += DexType.toSootAT(fr.getDefiningClass()) +": ";
                fieldSig += DexType.toSootAT(fr.getType()) +" ";
                fieldSig += fr.getName();
                Debug.printDbg("FIELD: ", fieldSig);
                elem = new AnnotationStringElem(fieldSig, 'f', ae.getName());
                break;
            }
            case 0x1a: // METHOD (Dalvik specific?)
            {
                MethodEncodedValue v = (MethodEncodedValue)ev;
                MethodReference mr = v.getValue();

                String className = DexType.toSootICAT(mr.getDefiningClass());
                String returnType = DexType.toSootAT(mr.getReturnType());
                String methodName = mr.getName();
                String parameters = "";
                for (CharSequence p : mr.getParameterTypes()) {
                    parameters += DexType.toSootAT(p.toString());
                }
                String mSig = className +" |"+ methodName +" |"+ parameters +" |"+ returnType;
                elem = new AnnotationStringElem(mSig, 'M', ae.getName());
                break;
            }
            case 0x1b: // ENUM : Warning -> encoding Dalvik specific!
            {
                EnumEncodedValue v = (EnumEncodedValue)ev;
                FieldReference fr = v.getValue();
                elem = new AnnotationEnumElem(
                        DexType.toSootAT(fr.getType()).toString(),
                        fr.getName(), 
                        'e', 
                        ae.getName());
                break;
            }
            case 0x1c: // ARRAY
            {
                ArrayEncodedValue v = (ArrayEncodedValue)ev;
                ArrayList<AnnotationElem> l = handleAnnotationElement(ae, (List<EncodedValue>) v.getValue());
                elem = new AnnotationArrayElem(l, '[', ae.getName());
                break;
            }
            case 0x1d: // ANNOTATION
            {
                AnnotationEncodedValue v = (AnnotationEncodedValue)ev;
                AnnotationTag t = new AnnotationTag(DexType.toSootAT(v.getType()).toString());
                for (AnnotationElement newElem : v.getElements()) {
                    List<EncodedValue> l = new ArrayList<EncodedValue>();
                    l.add(newElem.getValue());
                    List<AnnotationElem> aList = handleAnnotationElement(newElem, l);
                    
                    for (AnnotationElem e: aList)
                        t.addElem(e);
                }
                elem = new AnnotationAnnotationElem(t, '@', ae.getName());
                break;
            }
            case 0x1e: // NULL (Dalvik specific?)
            {
                elem = new AnnotationStringElem("null", 'N', ae.getName());
                break;
            }
            case 0x1f: // BOOLEAN
            {
                BooleanEncodedValue v = (BooleanEncodedValue)ev;
                elem = new AnnotationBooleanElem(v.getValue(), 'Z', ae.getName());
                break;
            }  
            default:
            {
                throw new RuntimeException("Unknown annotation element 0x"+ Integer.toHexString(type));
            }
            } // switch (type)

            aelemList.add(elem);
            
        } // for (EncodedValue)
        
        return aelemList;
    }
    
    /**
     * Converts Dexlib visibility to Jimple visibility
     * @param visibility Dexlib visibility
     * @return Jimple visibility
     */
    private int getVisibility(int visibility) {
        if (visibility == 1) // 1 == RUNTIME
            return AnnotationConstants.RUNTIME_VISIBLE; // 0 
        if (visibility == 0) // 0 == BUILD
            return AnnotationConstants.RUNTIME_INVISIBLE; // 1
        if (visibility == 2)// 2 == SYSTEM
            return AnnotationConstants.SOURCE_VISIBLE; // 2
        throw new RuntimeException("Unknown annotation visibility: '"+ visibility +"'");
    }


}
