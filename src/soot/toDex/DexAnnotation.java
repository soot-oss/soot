package soot.toDex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jf.dexlib.AnnotationDirectoryItem;
import org.jf.dexlib.AnnotationDirectoryItem.FieldAnnotation;
import org.jf.dexlib.AnnotationDirectoryItem.MethodAnnotation;
import org.jf.dexlib.AnnotationDirectoryItem.ParameterAnnotation;
import org.jf.dexlib.AnnotationItem;
import org.jf.dexlib.AnnotationSetItem;
import org.jf.dexlib.AnnotationSetRefList;
import org.jf.dexlib.AnnotationVisibility;
import org.jf.dexlib.ClassDataItem;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.MethodIdItem;
import org.jf.dexlib.ProtoIdItem;
import org.jf.dexlib.StringIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.TypeListItem;
import org.jf.dexlib.EncodedValue.AnnotationEncodedSubValue;
import org.jf.dexlib.EncodedValue.AnnotationEncodedValue;
import org.jf.dexlib.EncodedValue.ArrayEncodedValue;
import org.jf.dexlib.EncodedValue.BooleanEncodedValue;
import org.jf.dexlib.EncodedValue.ByteEncodedValue;
import org.jf.dexlib.EncodedValue.CharEncodedValue;
import org.jf.dexlib.EncodedValue.DoubleEncodedValue;
import org.jf.dexlib.EncodedValue.EncodedValue;
import org.jf.dexlib.EncodedValue.EnumEncodedValue;
import org.jf.dexlib.EncodedValue.FieldEncodedValue;
import org.jf.dexlib.EncodedValue.FloatEncodedValue;
import org.jf.dexlib.EncodedValue.IntEncodedValue;
import org.jf.dexlib.EncodedValue.LongEncodedValue;
import org.jf.dexlib.EncodedValue.MethodEncodedValue;
import org.jf.dexlib.EncodedValue.NullEncodedValue;
import org.jf.dexlib.EncodedValue.ShortEncodedValue;
import org.jf.dexlib.EncodedValue.StringEncodedValue;
import org.jf.dexlib.EncodedValue.TypeEncodedValue;

import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
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
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.tagkit.VisibilityParameterAnnotationTag;


/**
 * Class to generate Dexlib Annotations from Jimple Annotations
 * @author alex
 *
 */
public class DexAnnotation {
    
    DexFile dexFile = null;
    SootClass currentClass = null;
    
    List<AnnotationItem> classAnnotationItems = new ArrayList<AnnotationItem>();
    
    AnnotationSetItem classAnnotations = null;
    List<FieldAnnotation> fieldAnnotations = null;
    List<MethodAnnotation> methodAnnotations = null;
    List<ParameterAnnotation> parameterAnnotations = null;
    
    public DexAnnotation(DexFile dexFile, SootClass sc) {
        this.dexFile = dexFile;
        this.currentClass = sc;
        //classAnnotations = new AnnotationSetItem;
        fieldAnnotations = new ArrayList<FieldAnnotation>();
        methodAnnotations = new ArrayList<MethodAnnotation>();
        parameterAnnotations = new ArrayList<ParameterAnnotation>();
            
    }
    
    /**
     * Add Annotation Directory to the Dexlib's representation of the target .dex file
     * @return
     */
    public AnnotationDirectoryItem finish() {   
        classAnnotations = AnnotationSetItem.internAnnotationSetItem(dexFile, classAnnotationItems);

        Debug.printDbg("add ", classAnnotations.getAnnotations().length , " class annotations, "
                , fieldAnnotations.size() ," field annotations "
                , methodAnnotations.size() , " method annotations "
                , parameterAnnotations.size() , " parameters annotations.");      

        AnnotationDirectoryItem di = AnnotationDirectoryItem.internAnnotationDirectoryItem(dexFile, classAnnotations, fieldAnnotations, methodAnnotations, parameterAnnotations);
        return di;
    }

    /**
     * Handle Class Annotations
     * @param c SootClass
     * @param citem Dexlib class item
     */
    public void handleClass(SootClass c, ClassDataItem citem) {
        for (Tag t: c.getTags()) {
            if (!(t instanceof VisibilityAnnotationTag))
                continue;
            
            Debug.printDbg("\n   tag: ", t.getName());
            
            List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
            List<StringIdItem> namesList = new ArrayList<StringIdItem>();
            
            VisibilityAnnotationTag vat = (VisibilityAnnotationTag)t;
            List<AnnotationTag> atList = vat.getAnnotations();
            for (AnnotationTag at: atList) {
                Debug.printDbg("annotation tag name: ", at.getName(), " class: ", at.getClass());
                //String type = soot2DalvikType(at.getType());
                String type = at.getType();
                Debug.printDbg("tag type: ", type);
                
                for (AnnotationElem ae : at.getElems()) {
                    EncodedValue value = getAnnotationElement(ae);
                    encodedValueList.add(value);
                    namesList.add(StringIdItem.internStringIdItem(dexFile, ae.getName()));
                    Debug.printDbg("new class annotation: ", value ," ", ae.getName() ," ", at.getName() ," ", ae.getClass());
                }
                
                TypeIdItem annotationType = TypeIdItem.internTypeIdItem(dexFile, type);
                StringIdItem[] names = namesList.toArray(new StringIdItem[namesList.size()]);
                EncodedValue[] values = encodedValueList.toArray(new EncodedValue[encodedValueList.size()]);
                AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(annotationType, names, values);
                AnnotationItem aItem = AnnotationItem.internAnnotationItem(dexFile, getVisibility(vat.getVisibility()), annotationValue);
                classAnnotationItems.add(aItem);
            }
                     
        }

    }

    /**
     * Handle Field Annotations
     * @param sf SootField
     * @param fid DexLib Field
     */
    private Set<String> alreadyDone = new HashSet<String>();
    public void handleField(SootField sf, FieldIdItem fid) {
        if (!sf.getDeclaringClass().getName().equals(currentClass.getName()))
            return;
        if (alreadyDone.contains(sf.getSignature()))
            return;
        alreadyDone.add(sf.getSignature());
                
        Debug.printDbg("handle annotations for field: '", sf ,"' current class: ", currentClass);
        List<AnnotationItem> aList = new ArrayList<AnnotationItem>();
        for (Tag t: sf.getTags()) {
            if (!(t instanceof VisibilityAnnotationTag))
                continue;
            
            List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
            List<StringIdItem> namesList = new ArrayList<StringIdItem>();
            
            VisibilityAnnotationTag vat = (VisibilityAnnotationTag)t;
            List<AnnotationTag> atList = vat.getAnnotations();
            
            
            for (AnnotationTag at: atList) {
                              
                for (AnnotationElem ae : at.getElems()) {
                    EncodedValue value = getAnnotationElement(ae);
                    encodedValueList.add(value);
                    namesList.add(StringIdItem.internStringIdItem(dexFile, ae.getName()));
                    Debug.printDbg("new field annotation: ", value ," ", ae.getName() ," type: ", value.getClass());
                }
            
                //String type = soot2DalvikType(at.getType());
                String type = at.getType();
                Debug.printDbg("field annotation type: ", type);
                TypeIdItem annotationType = TypeIdItem.internTypeIdItem(dexFile, type);
                StringIdItem[] names = namesList.toArray(new StringIdItem[namesList.size()]);
                EncodedValue[] values = encodedValueList.toArray(new EncodedValue[encodedValueList.size()]);
                AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(annotationType, names, values);
                
                AnnotationItem a = AnnotationItem.internAnnotationItem(dexFile, 
                        getVisibility(vat.getVisibility()), annotationValue);
                aList.add(a);

            }
            

       }
        AnnotationSetItem set = AnnotationSetItem.internAnnotationSetItem(dexFile, aList);
        FieldAnnotation fa = new FieldAnnotation(fid, set);
        fieldAnnotations.add(fa);

    }
    

    /**
     * Handles Method and Parameter Annotations
     * @param sm SootMethod
     * @param mid Dexlib Method Item
     */
    private Set<String> alreadyDoneMethods = new HashSet<String>();
    public void handleMethod(SootMethod sm, MethodIdItem mid) {
        if (!sm.getDeclaringClass().getName().equals(currentClass.getName()))
            return;
        if (alreadyDoneMethods.contains(sm.getSignature()))
            return;
        alreadyDoneMethods.add(sm.getSignature());
        Debug.printDbg("handle annotations for method: '", sm ,"' current class: ", currentClass);
        
        List<AnnotationItem> aList = new ArrayList<AnnotationItem>();
        List<AnnotationSetItem> setList = new ArrayList<AnnotationSetItem>();
        for (Tag t: sm.getTags()) {
            if (!(t instanceof VisibilityAnnotationTag) && !(t instanceof VisibilityParameterAnnotationTag))
                continue;
            if (t instanceof VisibilityAnnotationTag) {
                VisibilityAnnotationTag vat = (VisibilityAnnotationTag)t;
                aList.addAll(handleMethodTag(vat, mid));
            } else {
                Debug.printDbg("new parameter annotation.");
                VisibilityParameterAnnotationTag vat = (VisibilityParameterAnnotationTag)t;
                setList.addAll(handleMethodParamTag(vat, mid));
            }
        }
        
        AnnotationSetItem set = AnnotationSetItem.internAnnotationSetItem(dexFile, aList);
        MethodAnnotation ma = new MethodAnnotation(mid, set);
        methodAnnotations.add(ma);
        
        AnnotationSetRefList asrList = AnnotationSetRefList.internAnnotationSetRefList(dexFile, setList);
        ParameterAnnotation pa = new ParameterAnnotation(mid, asrList);
        parameterAnnotations.add(pa);
    }
    
    /**
     * Handle Method Annotations
     * @param vat
     * @param mid
     * @return 
     */
    private List<AnnotationItem> handleMethodTag(VisibilityAnnotationTag vat, MethodIdItem mid) {
        List<AnnotationTag> atList = vat.getAnnotations();
        
        List<AnnotationItem> aList = new ArrayList<AnnotationItem>();
        for (AnnotationTag at: atList) {         
            List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
            List<StringIdItem> namesList = new ArrayList<StringIdItem>();
            for (AnnotationElem ae : at.getElems()) {
                EncodedValue value = getAnnotationElement(ae);
                encodedValueList.add(value);
                namesList.add(StringIdItem.internStringIdItem(dexFile, ae.getName()));
                Debug.printDbg("new method annotation: ", value ," ", ae.getName());
            }

            //String type = soot2DalvikType(at.getType());
            String type = at.getType();
            TypeIdItem annotationType = TypeIdItem.internTypeIdItem(dexFile, type);
            StringIdItem[] names = namesList.toArray(new StringIdItem[namesList.size()]);
            EncodedValue[] values = encodedValueList.toArray(new EncodedValue[encodedValueList.size()]);
            AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(annotationType, names, values);
                      
            AnnotationItem a = AnnotationItem.internAnnotationItem(dexFile, 
                    getVisibility(vat.getVisibility()), annotationValue);
            aList.add(a);
                    
        }
        return aList;
        
    }
    
    /**
     * Handle (Method) Parameter Annotations
     * @param vat1
     * @param mid
     * @return 
     */
    private List<AnnotationSetItem> handleMethodParamTag(VisibilityParameterAnnotationTag vat1, MethodIdItem mid) {
      List<VisibilityAnnotationTag> vatList = vat1.getVisibilityAnnotations();
      
      List<AnnotationSetItem> setList = new ArrayList<AnnotationSetItem>();
      if (vatList != null)
          for (VisibilityAnnotationTag vat: vatList) {
              if (vat == null)
                  continue;
              
              List<AnnotationItem> aList = new ArrayList<AnnotationItem>();
              if (vat.getAnnotations() != null)
                  for (AnnotationTag at: vat.getAnnotations()) {
                      List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
                      List<StringIdItem> namesList = new ArrayList<StringIdItem>();
                      for (AnnotationElem ae : at.getElems()) {
                          EncodedValue value = getAnnotationElement(ae);
                          encodedValueList.add(value);
                          namesList.add(StringIdItem.internStringIdItem(dexFile, ae.getName()));
                          Debug.printDbg("new annotation: ", value ," ", ae.getName());
                      }  
                  
                      //String type = soot2DalvikType(at.getType());
                      String type = at.getType();
                      TypeIdItem annotationType = TypeIdItem.internTypeIdItem(dexFile, type);
                      StringIdItem[] names = namesList.toArray(new StringIdItem[namesList.size()]);
                      EncodedValue[] values = encodedValueList.toArray(new EncodedValue[encodedValueList.size()]);
                      AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(annotationType, names, values);            
        
                      AnnotationItem a = AnnotationItem.internAnnotationItem(dexFile, getVisibility(vat.getVisibility()), annotationValue);
                      aList.add(a);
                             
                  }
              
              AnnotationSetItem annotationSets = AnnotationSetItem.internAnnotationSetItem(dexFile, aList);
              setList.add(annotationSets);
              
           }
      
          return setList;
    }

    /**
     * Encodes Annotations Elements from Jimple to Dexlib
     * @param elem Jimple Element
     * @return Dexlib encoded element
     */
    private EncodedValue getAnnotationElement(AnnotationElem elem){
        EncodedValue v = null;
        Debug.printDbg("annotation kind: ", elem.getKind());
        switch (elem.getKind()) {
        case 'Z': {
        	if (elem instanceof AnnotationIntElem) {
	            AnnotationIntElem e = (AnnotationIntElem)elem;
	            if (e.getValue() == 0) {
	                v = BooleanEncodedValue.FalseValue;
	            } else if (e.getValue() == 1) {
	                v = BooleanEncodedValue.TrueValue;
	            } else {
	                throw new RuntimeException("error: boolean value from int with value != 0 or 1.");
	            }
        	}
        	else if (elem instanceof AnnotationBooleanElem) {
        		AnnotationBooleanElem e = (AnnotationBooleanElem) elem;
        		if (e.getValue())
        			v = BooleanEncodedValue.TrueValue;
        		else
        			v = BooleanEncodedValue.FalseValue;
        	}
        	else
        		throw new RuntimeException("Annotation type incompatible with target type boolean");
            break;
        }
        case 'S': {
            AnnotationIntElem e = (AnnotationIntElem)elem;
            ShortEncodedValue a = new ShortEncodedValue((short)e.getValue());
            v = a;
            break;
        }
        case 'B': {
            AnnotationIntElem e = (AnnotationIntElem)elem;
            ByteEncodedValue a = new ByteEncodedValue((byte)e.getValue());
            v = a;
            break;
        }
        case 'C': {
            AnnotationIntElem e = (AnnotationIntElem)elem;
            CharEncodedValue a = new CharEncodedValue((char)e.getValue());
            v = a;
            break;
        }
        case 'I': {
            AnnotationIntElem e = (AnnotationIntElem)elem;
            IntEncodedValue a = new IntEncodedValue(e.getValue());
            v = a;
            break;
        }
        case 'J': {
            AnnotationLongElem e = (AnnotationLongElem)elem;
            LongEncodedValue a = new LongEncodedValue(e.getValue());
            v = a;
            break; 
        }
        case 'F': {
            AnnotationFloatElem e = (AnnotationFloatElem)elem;
            FloatEncodedValue a = new FloatEncodedValue(e.getValue());
            v = a;
            break;
        }
        case 'D': {
            AnnotationDoubleElem e = (AnnotationDoubleElem)elem;
            DoubleEncodedValue a = new DoubleEncodedValue(e.getValue());
            v = a;
            break;
        }
        case 's': {
            AnnotationStringElem e = (AnnotationStringElem)elem;
            StringIdItem string = StringIdItem.internStringIdItem(dexFile, e.getValue());
            StringEncodedValue a = new StringEncodedValue(string);
            v = a;
            break;
        }
        case 'e': {
            AnnotationEnumElem e = (AnnotationEnumElem)elem;
            String classT = soot2DalvikType(e.getConstantName());
            String fieldT = soot2DalvikType(e.getTypeName().split(":")[0]);
            String fieldNameString = e.getTypeName().split(":")[1];
            TypeIdItem classType = TypeIdItem.internTypeIdItem(dexFile, classT);
            TypeIdItem fieldType = TypeIdItem.internTypeIdItem(dexFile, fieldT);
            StringIdItem fieldName = StringIdItem.internStringIdItem(dexFile, fieldNameString);
            FieldIdItem fId = FieldIdItem.internFieldIdItem(dexFile, classType, fieldType, fieldName);
            EnumEncodedValue a = new EnumEncodedValue(fId);
            v = a;
            break;
        }
        case 'c': {
            AnnotationClassElem e = (AnnotationClassElem)elem;
            String type = soot2DalvikType(e.getDesc());
            StringIdItem strId = StringIdItem.internStringIdItem(dexFile, type);
            TypeIdItem typeId = TypeIdItem.internTypeIdItem(dexFile, strId);
            TypeEncodedValue a = new TypeEncodedValue(typeId);
            v = a;
            break;
        }
        case '[': {
            AnnotationArrayElem e = (AnnotationArrayElem)elem;
            List<EncodedValue> valueList = new ArrayList<EncodedValue>();
            for (int i = 0; i < e.getNumValues(); i++){
                EncodedValue val = getAnnotationElement(e.getValueAt(i));
                valueList.add(val);
            }
            ArrayEncodedValue a = new ArrayEncodedValue(valueList.toArray(
                    new EncodedValue[valueList.size()]));
            v = a;
            break;
        }
        case '@': {
            AnnotationAnnotationElem e = (AnnotationAnnotationElem)elem;
            
            List<StringIdItem> nameList = new ArrayList<StringIdItem>();
            List<EncodedValue> valueList = new ArrayList<EncodedValue>();
            for (AnnotationElem ae : e.getValue().getElems()){
                EncodedValue val = getAnnotationElement(ae);
                valueList.add(val);
                nameList.add(StringIdItem.internStringIdItem(dexFile, ae.getName()));
            }
            //String type = soot2DalvikType(e.getValue().getType());
            String type = e.getValue().getType();
            TypeIdItem annotationType = TypeIdItem.internTypeIdItem(dexFile, type);
            StringIdItem[] names = nameList.toArray(new StringIdItem[nameList.size()]);
            EncodedValue[] values = valueList.toArray(new EncodedValue[valueList.size()]);
            AnnotationEncodedValue a = new AnnotationEncodedValue(annotationType, names, values);
            v = a;
            break;
        }
        case 'f': { // field (Dalvik specific?)
            AnnotationStringElem e = (AnnotationStringElem)elem;
            String fSig = e.getValue();
            String[] sp = fSig.split(" ");
            String classString = soot2DalvikType(sp[0].split(":")[0]);
            String typeString = soot2DalvikType(sp[1]);
            String fieldName = sp[2];
            StringIdItem ctypeDescriptor = StringIdItem.internStringIdItem(dexFile, classString);
            StringIdItem ftypeDescriptor = StringIdItem.internStringIdItem(dexFile, typeString);
            StringIdItem fieldNameItem = StringIdItem.internStringIdItem(dexFile, fieldName);
            
            Debug.printDbg("field item: ", classString ," ", typeString, " ", fieldName);

            TypeIdItem classType = TypeIdItem.internTypeIdItem(dexFile, ctypeDescriptor);
            TypeIdItem fieldType = TypeIdItem.internTypeIdItem(dexFile, ftypeDescriptor);    
            FieldIdItem fId = FieldIdItem.internFieldIdItem(dexFile, classType, fieldType, fieldNameItem);
            FieldEncodedValue a = new FieldEncodedValue(fId);
            v = a;
            break;
        }
        case 'M': { // method (Dalvik specific?)
            AnnotationStringElem e = (AnnotationStringElem)elem;
            String mSig = e.getValue();
            Debug.printDbg("msig: ", mSig);
            //
            String[] sp = mSig.split(" ");
            String classString = soot2DalvikType(sp[0].split(":")[0]);
            String returnType = soot2DalvikType(sp[1]);
            String[] sp2 = sp[2].split("\\(");
            String methodNameString = sp2[0];
            String parameters = sp2[1].replaceAll("\\)", "");
            Debug.printDbg("parameters: '", parameters ,"'");
            List<String> paramTypeList = new ArrayList<String>();
            if (parameters.length() > 0)
                for (String p: parameters.split(",")) {
                    String type = soot2DalvikType(p);
                    paramTypeList.add(type);
                }
            //
            StringIdItem ctypeDescriptor = StringIdItem.internStringIdItem(dexFile, classString);
            StringIdItem returnDescriptor = StringIdItem.internStringIdItem(dexFile, returnType);
            List<TypeIdItem> parametersItemList = new ArrayList<TypeIdItem>();
            for (String p: paramTypeList) {
                StringIdItem t = StringIdItem.internStringIdItem(dexFile, p);
                parametersItemList.add(TypeIdItem.internTypeIdItem(dexFile, t));
            }
            //
            TypeIdItem classType = TypeIdItem.internTypeIdItem(dexFile, ctypeDescriptor);
            TypeIdItem returnTypeItem = TypeIdItem.internTypeIdItem(dexFile, returnDescriptor);
            TypeListItem parametersItem = TypeListItem.internTypeListItem(dexFile, parametersItemList);
            ProtoIdItem methodPrototype = ProtoIdItem.internProtoIdItem(dexFile, returnTypeItem, parametersItem);
            StringIdItem methodName = StringIdItem.internStringIdItem(dexFile, methodNameString);            
            MethodIdItem mId = MethodIdItem.internMethodIdItem(dexFile, classType, methodPrototype, methodName);
            MethodEncodedValue a = new MethodEncodedValue(mId);
            v = a;
            break;
        }
        case 'N': { // null (Dalvik specific?)
            NullEncodedValue a = NullEncodedValue.NullValue;
            v = a;
            break;
        }
        default : {
            throw new RuntimeException("Unknown Elem Attr Kind: "+elem.getKind());
        }
        }
        return v;
    }
    
    
    /**
     * Converts type such as "a.b.c[][]" to "[[a/b/c".
     * 
     * @param sootType Soot type to convert to Dexlib type.
     * @return String representation of the Dexlib type
     */
    public static String soot2DalvikType(String sootType) {
        
        if (sootType.startsWith("L") || sootType.startsWith("[")) {
            throw new RuntimeException("error: wrong type format: "+ sootType);
        }
        
        int arraySize = 0;
        while (sootType.endsWith("[]")) {
            arraySize++;
            sootType = sootType.replaceFirst("\\[\\]", "");
        }
        String type = "";
        if (sootType.equals("int")) {
            type = "I";
        } else if (sootType.equals("float")) {
            type = "F";
        } else if (sootType.equals("long")) {
            type = "J";
        } else if (sootType.equals("double")) {
            type = "D";
        } else if (sootType.equals("char")) {
            type = "C";
        } else if (sootType.equals("boolean")) {
            type = "Z";
        } else if (sootType.equals("byte")) {
            type = "B";
        } else if (sootType.equals("void")) {
            type = "V";
        } else {
            type = "L"+ sootType.replaceAll("\\.", "/") +";";
        }
        
        if (type.startsWith("LL")) {
            throw new RuntimeException("error: wrong type format: "+ type);
        }
        
        for (int i = 0; i< arraySize; i++)
            type = "[" + type;
        return type;     
        
    }
    
    /**
     * Converts Jimple visibility to Dexlib visibility
     * @param visibility Jimple visibility
     * @return Dexlib visibility
     */
    private static AnnotationVisibility getVisibility(int visibility) {
        if (visibility == AnnotationConstants.RUNTIME_VISIBLE) // 0 
            return AnnotationVisibility.RUNTIME; // 1
        if (visibility == AnnotationConstants.RUNTIME_INVISIBLE) // 1
            return AnnotationVisibility.BUILD; // 0
        if (visibility == AnnotationConstants.SOURCE_VISIBLE) // 2
            return AnnotationVisibility.SYSTEM; // 2
        throw new RuntimeException("Unknown annotation visibility: '"+ visibility +"'");
    }

}
