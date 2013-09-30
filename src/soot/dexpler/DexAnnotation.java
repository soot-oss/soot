package soot.dexpler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jf.dexlib2.AnnotationVisibility;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.iface.value.MethodEncodedValue;
import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.AnnotationElement;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.MethodParameter;
import org.jf.dexlib2.iface.value.AnnotationEncodedValue;
import org.jf.dexlib2.iface.value.ArrayEncodedValue;
import org.jf.dexlib2.iface.value.EncodedValue;
import org.jf.dexlib2.iface.value.NullEncodedValue;
import org.jf.dexlib2.iface.value.StringEncodedValue;
import org.jf.dexlib2.iface.value.BooleanEncodedValue;
import org.jf.dexlib2.iface.value.ByteEncodedValue;
import org.jf.dexlib2.iface.value.CharEncodedValue;
import org.jf.dexlib2.iface.value.DoubleEncodedValue;
import org.jf.dexlib2.iface.value.EnumEncodedValue;
import org.jf.dexlib2.iface.value.FieldEncodedValue;
import org.jf.dexlib2.iface.value.FloatEncodedValue;
import org.jf.dexlib2.iface.value.IntEncodedValue;
import org.jf.dexlib2.iface.value.LongEncodedValue;
import org.jf.dexlib2.iface.value.ShortEncodedValue;
import org.jf.dexlib2.iface.value.StringEncodedValue;
import org.jf.dexlib2.iface.value.TypeEncodedValue;

import soot.RefType;
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
import soot.tagkit.Host;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;
import soot.dexpler.Debug;

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
    void handleClassAnnotation(Host h, ClassDef classDef) {
        Set<? extends Annotation> aSet = classDef.getAnnotations();
        if (aSet == null || aSet.isEmpty())
            return;
        MyAnnotations ma = handleAnnotation(aSet);
        for (int i = 0; i < ma.getAnnotations().size(); i++) {
            AnnotationTag at = ma.getAnnotations().get(i);
            int visibility = ma.getVisibilityList().get(i);
            VisibilityAnnotationTag vat = new VisibilityAnnotationTag(visibility);
            vat.addAnnotation(at);
            h.addTag(vat);
        }

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
        MyAnnotations ma = handleAnnotation(aSet);
        for (int i = 0; i < ma.getAnnotations().size(); i++) {
            AnnotationTag at = ma.getAnnotations().get(i);
            int visibility = ma.getVisibilityList().get(i);
            VisibilityAnnotationTag vat = new VisibilityAnnotationTag(visibility);
            vat.addAnnotation(at);
            h.addTag(vat);
        }
    }

    /**
     * Converts method and method parameters annotations from Dexlib to Jimple
     * @param h
     * @param method
     */
    void handleMethodAnnotation(Host h, Method method) {

        Set<? extends Annotation> aSet = method.getAnnotations();
        if (!(aSet == null || aSet.isEmpty())) {
            MyAnnotations ma = handleAnnotation(aSet);
            for (int i = 0; i < ma.getAnnotations().size(); i++) {
                AnnotationTag at = ma.getAnnotations().get(i);
                int visibility = ma.getVisibilityList().get(i);
                VisibilityAnnotationTag vat = new VisibilityAnnotationTag(visibility);
                vat.addAnnotation(at);
                h.addTag(vat);
            }
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
            for (MethodParameter p : parameters) {
                VisibilityParameterAnnotationTag tag = new VisibilityParameterAnnotationTag(parameters.size(), 0);
                MyAnnotations ma = handleAnnotation(p.getAnnotations());
                for (int i = 0; i < ma.getAnnotations().size(); i++) {
                    AnnotationTag at = ma.getAnnotations().get(i);
                    int visibility = ma.getVisibilityList().get(i);
                    VisibilityAnnotationTag vat = new VisibilityAnnotationTag(visibility);
                    vat.addAnnotation(at);
                    tag.addVisibilityAnnotation(vat);
                }
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
        public List<AnnotationTag> getAnnotations() { return annotationList; }
        public List<Integer> getVisibilityList() { return visibilityList; }
    }

    /**
     * 
     * @param annotations
     * @return
     */
    MyAnnotations handleAnnotation(Set<? extends org.jf.dexlib2.iface.Annotation> annotations) {
        MyAnnotations my_annotations = new MyAnnotations();

        if (annotations.size() == 0)
            return my_annotations;

        for (Annotation a: annotations) {
            
            //AnnotationTag aTag = new AnnotationTag(DexType.toSoot(a.getType()).toString());
            AnnotationTag aTag = new AnnotationTag(a.getType());
            
            Debug.printDbg("\nAnnotationTag -> ", a);
            
            for (AnnotationElement ae: a.getElements()) {
                
                Debug.printDbg("element: ", ae.getName() ," ", ae.getValue() ," type: ", ae.getClass());
                Debug.printDbg("value type: ", ae.getValue().getValueType() ," class: ", ae.getValue().getClass());

                List<EncodedValue> evList = new ArrayList<EncodedValue>();
                evList.add(ae.getValue());
                List<AnnotationElem> eList = handleAnnotationElement(ae, evList);
                for (AnnotationElem e : eList)
                    aTag.addElem(e);
            }
    
            my_annotations.add(aTag, getVisibility(a.getVisibility()));
        }
        
        return my_annotations;

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
                elem = new AnnotationClassElem(DexType.toSoot(v.getValue()).toString(), 'c', ae.getName());
                break;
            }
            case 0x19: // FIELD (Dalvik specific?)
            {
                FieldEncodedValue v = (FieldEncodedValue)ev;
                FieldReference fr = v.getValue();
                String fieldSig = "";
                fieldSig += DexType.toSoot(fr.getDefiningClass()) +": ";
                fieldSig += DexType.toSoot(fr.getType()) +" ";
                fieldSig += fr.getName();
                Debug.printDbg("FIELD: ", fieldSig);
                elem = new AnnotationStringElem(fieldSig, 'f', ae.getName());
                break;
            }
            case 0x1a: // METHOD (Dalvik specific?)
            {
                MethodEncodedValue v = (MethodEncodedValue)ev;
                MethodReference mr = v.getValue();
                String mSig = "";
                mSig += DexType.toSoot(mr.getDefiningClass()) +": ";
                mSig += DexType.toSoot(mr.getReturnType()) +" ";
                mSig += mr.getName() +"(";
                int i = 0;
                int params = mr.getParameterTypes().size();
                for (CharSequence p : mr.getParameterTypes()) {
                    mSig += DexType.toSoot(p.toString());
                    if (++i < params)
                        mSig += ",";
                }
                mSig +=")";
                Debug.printDbg("METHOD: ", mSig);
                elem = new AnnotationStringElem(mSig, 'M', ae.getName());
                break;
            }
            case 0x1b: // ENUM : Warning -> encoding Dalvik specific!
            {
                EnumEncodedValue v = (EnumEncodedValue)ev;
                FieldReference fr = v.getValue();
                elem = new AnnotationEnumElem(DexType.toSoot(fr.getType()).toString() +":"+ fr.getName(), 
                        DexType.toSoot(fr.getDefiningClass()).toString(), 
                        'e', ae.getName());
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
                //AnnotationTag t = new AnnotationTag(DexType.toSoot(v.getType()).toString());
                AnnotationTag t = new AnnotationTag(v.getType());
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
