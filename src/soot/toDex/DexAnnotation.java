package soot.toDex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import soot.G;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.dexpler.DexType;
import soot.dexpler.Util;
import soot.options.Options;
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
import soot.tagkit.EnclosingMethodTag;
import soot.tagkit.InnerClassAttribute;
import soot.tagkit.InnerClassTag;
import soot.tagkit.SignatureTag;
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

    
//  .annotation "Ldalvik/annotation/AnnotationDefault;"
//  .annotation "Ldalvik/annotation/EnclosingClass;"
//  .annotation "Ldalvik/annotation/EnclosingMethod;"
//  .annotation "Ldalvik/annotation/InnerClass;"
//  .annotation "Ldalvik/annotation/MemberClasses;"
//  .annotation "Ldalvik/annotation/Signature;"
//  .annotation "Ldalvik/annotation/Throws;"
    public void handleClass(SootClass c, ClassDataItem citem) {
        Debug.printDbg("1");
        
        // handle inner class tags
        if (c.hasTag("InnerClassAttribute") && !Options.v().no_output_inner_classes_attribute()){
            InnerClassTag innerClass = null;
            List<InnerClassTag> memberClasses = new ArrayList<InnerClassTag>();
            InnerClassAttribute innerClassAttribute = (InnerClassAttribute) c.getTag("InnerClassAttribute");
            Debug.printDbg("has tag: class attribute");
            // separate inner class from member classes
            for (Tag t : innerClassAttribute.getSpecs()){ 
                Debug.printDbg("t: ", t);
                InnerClassTag tag = (InnerClassTag) t;
                String innerC = tag.getInnerClass();
                String innerCSootFormat = innerC.replaceAll("/", "\\.");
                Debug.printDbg("innercc: ", innerCSootFormat);
                if (innerCSootFormat.equals(c.toString())) {
                    if (innerClass != null)
                        G.v().out.println("warning: multiple inner class tags!");
                    innerClass = tag;
                } else {
                    Debug.printDbg("1 add ", tag);
                    memberClasses.add(tag);
                }
            }
            
            // inner class   
            if (innerClass != null) {
                // enclosing class            
                AnnotationItem enclosingItem = makeEnclosingClassAnnotation(innerClass);
                classAnnotationItems.add(enclosingItem);
       
                // inner class
                AnnotationItem innerItem = makeInnerClassAnnotation(innerClass);
                classAnnotationItems.add(innerItem);
            }
            
            // member classes
            if (memberClasses.size() > 0) {
                Debug.printDbg("here:");
                AnnotationItem memberItem = makeMemberClasses(memberClasses);
                classAnnotationItems.add(memberItem);
            }
                    
        }
        
        // handle enclosing method tags
        if (c.hasTag("EnclosingMethodTag")){
          EnclosingMethodTag eMethTag = (EnclosingMethodTag)c.getTag("EnclosingMethodTag");
          AnnotationItem enclosingMethodItem = makeEnclosingMethod(eMethTag);
          if (enclosingMethodItem != null)
        	  classAnnotationItems.add(enclosingMethodItem);
        }
        
        // handle deprecated tag
        if (c.hasTag("DeprecatedTag")){
            AnnotationItem deprecatedItem = makeDeprecatedItem();
            classAnnotationItems.add(deprecatedItem);
        }
        
        // handle visibility annotation tags
        for (Tag t : c.getTags()){
            if (t.getName().equals("VisibilityAnnotationTag")){
                Debug.printDbg("class visibility annotation tag: ", t);
                List<AnnotationItem> visibilityItems = makeVisibilityItem(t);
                for (AnnotationItem i : visibilityItems)
                    classAnnotationItems.add(i);
            }
        }
 
//            Debug.printDbg("\n   tag: ", t.getName());
//            
//            List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
//            List<StringIdItem> namesList = new ArrayList<StringIdItem>();
//            
//            VisibilityAnnotationTag vat = (VisibilityAnnotationTag)t;
//            List<AnnotationTag> atList = vat.getAnnotations();
//            for (AnnotationTag at: atList) {
//                Debug.printDbg("annotation tag name: ", at.getName(), " class: ", at.getClass());
//                //String type = soot2DalvikType(at.getType());
//                String type = at.getType();
//                Debug.printDbg("tag type: ", type);
//                
//                for (AnnotationElem ae : at.getElems()) {
//                    EncodedValue value = getAnnotationElement(ae);
//                    encodedValueList.add(value);
//                    namesList.add(StringIdItem.internStringIdItem(dexFile, ae.getName()));
//                    Debug.printDbg("new class annotation: ", value ," ", ae.getName() ," ", at.getName() ," ", ae.getClass());
//                }
//                
//                TypeIdItem annotationType = TypeIdItem.internTypeIdItem(dexFile, type);
//                StringIdItem[] names = namesList.toArray(new StringIdItem[namesList.size()]);
//                EncodedValue[] values = encodedValueList.toArray(new EncodedValue[encodedValueList.size()]);
//                AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(annotationType, names, values);
//                AnnotationItem aItem = AnnotationItem.internAnnotationItem(dexFile, getVisibility(vat.getVisibility()), annotationValue);
//                classAnnotationItems.add(aItem);
//            }
//                     
//        }

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
        
        // handle deprecated tag
        if (sf.hasTag("DeprecatedTag")){
            AnnotationItem deprecatedItem = makeDeprecatedItem();
            aList.add(deprecatedItem);
        }
        
        // handle signature tag
        if (sf.hasTag("SignatureTag")){
            SignatureTag tag = (SignatureTag)sf.getTag("SignatureTag");
            AnnotationItem deprecatedItem = makeSignatureItem(tag);
            aList.add(deprecatedItem);
        }
        
        // handle visibility annotation tags
        for (Tag t : sf.getTags()){
            if (t.getName().equals("VisibilityAnnotationTag")){
                Debug.printDbg("field visibility annotation tag: ", t);
                List<AnnotationItem> visibilityItems = makeVisibilityItem(t);
                for (AnnotationItem ai : visibilityItems)
                    aList.add(ai);
            }
        }
        
//        // handle constant tag
//        int cst = 0;
//        for (Tag t : sf.getTags()) {
//            if (t instanceof ConstantValueTag) {
//                cst++;
//                if (cst > 1)
//                    G.v().out.println("warning: more than one constant tag for field: "+ sf);
//                AnnotationItem ai = makeConstantItem(t);
//                aList.add(ai);
//            }
//        }
        
        

        AnnotationSetItem set = AnnotationSetItem.internAnnotationSetItem(dexFile, aList);
        FieldAnnotation fa = new FieldAnnotation(fid, set);
        fieldAnnotations.add(fa);

    }
    
    private Set<SootMethod> alreadyDoneMethods = new HashSet<SootMethod>();

    /**
     * Handles Method and Parameter Annotations
     * @param sm SootMethod
     * @param mid Dexlib Method Item
     */
    public void handleMethod(SootMethod sm, MethodIdItem mid) {
        if (!sm.getDeclaringClass().getName().equals(currentClass.getName()))
            return;
        if (!alreadyDoneMethods.add(sm))
            return;
        Debug.printDbg("handle annotations for method: '", sm ,"' current class: ", currentClass);
        
        List<AnnotationItem> aList = new ArrayList<AnnotationItem>();
        List<AnnotationSetItem> setList = new ArrayList<AnnotationSetItem>();
        Set<String> skipList = new HashSet<String>();
        
        if (sm.hasTag("DeprecatedTag")){
            AnnotationItem deprecatedItem = makeDeprecatedItem();
            aList.add(deprecatedItem);
            skipList.add("Ljava/lang/Deprecated;");
        }
        
        if (sm.hasTag("SignatureTag")){
            SignatureTag tag = (SignatureTag)sm.getTag("SignatureTag");
            AnnotationItem signatureItem = makeSignatureItem(tag);
            aList.add(signatureItem);
            skipList.add("Ldalvik/annotation/Signature;");
        }
        
        if (sm.hasTag("AnnotationDefaultTag")){
//            AnnotationDefaultTag tag = (AnnotationDefaultTag)sm.getTag("AnnotationDefaultTag");
            Debug.printDbg("TODO");
        }
        
        for (Tag t : sm.getTags()) {
            if (t.getName().equals("VisibilityAnnotationTag")){
              VisibilityAnnotationTag vat = (VisibilityAnnotationTag)t;
              aList.addAll(handleMethodTag(vat, mid, skipList));
            }
            if (t.getName().equals("VisibilityParameterAnnotationTag")){
              VisibilityParameterAnnotationTag vat = (VisibilityParameterAnnotationTag)t;
              setList.addAll(handleMethodParamTag(vat, mid, Collections.<String>emptySet()));
            }
        }
        
        
        // Sort the annotation list
        Collections.sort(aList, new Comparator<AnnotationItem>() {

			@Override
			public int compare(AnnotationItem o1, AnnotationItem o2) {
				int idx1 = o1.getEncodedAnnotation().annotationType.getIndex();
				int idx2 = o2.getEncodedAnnotation().annotationType.getIndex();
				int res = idx1 - idx2;
				
				// Check that our generated APK file will not be broken
				if (res == 0 && !(idx1 == -1 && idx2 == -1))
					throw new RuntimeException("Duplicate annotation type:" + o1);
				if (o1.getEncodedAnnotation().annotationType.getTypeDescriptor().equals
						(o2.getEncodedAnnotation().annotationType.getTypeDescriptor()))
					throw new RuntimeException("Duplicate annotation type:" + o1);

				return res;
			}
        	
        });

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
    private List<AnnotationItem> handleMethodTag(VisibilityAnnotationTag vat, MethodIdItem mid,
    		Set<String> skipList) {
        List<AnnotationTag> atList = vat.getAnnotations();
        
        List<AnnotationItem> aList = new ArrayList<AnnotationItem>();
        for (AnnotationTag at: atList) {
            //String type = soot2DalvikType(at.getType());
            String type = at.getType();
            if (skipList.contains(type))
            	continue;

        	List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
            List<StringIdItem> namesList = new ArrayList<StringIdItem>();
            for (AnnotationElem ae : at.getElems()) {
                EncodedValue value = getAnnotationElement(ae);
                encodedValueList.add(value);
                namesList.add(StringIdItem.internStringIdItem(dexFile, ae.getName()));
                Debug.printDbg("new method annotation: ", value ," ", ae.getName());
            }
            
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
    private List<AnnotationSetItem> handleMethodParamTag(VisibilityParameterAnnotationTag vat1,
    		MethodIdItem mid, Set<String> skipList) {
      List<VisibilityAnnotationTag> vatList = vat1.getVisibilityAnnotations();
      if (vatList == null)
    	  return Collections.emptyList();
      
      List<AnnotationSetItem> setList = new ArrayList<AnnotationSetItem>();
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
    			  if (skipList.contains(type))
    				  continue;
                  
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
            
            String classT = soot2DalvikType(e.getTypeName());
            String fieldT = soot2DalvikType(e.getTypeName());
            String fieldNameString = e.getName();
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
    
    
    private AnnotationItem makeEnclosingClassAnnotation(InnerClassTag tag) {
        String outerClass = tag.getOuterClass();
        TypeIdItem string = TypeIdItem.internTypeIdItem(dexFile,
                "L" + outerClass + ";");
        TypeEncodedValue a = new TypeEncodedValue(string);            
        TypeIdItem annotationType = TypeIdItem.internTypeIdItem(
                dexFile, "Ldalvik/annotation/EnclosingClass;");           
        List<StringIdItem> namesList = new ArrayList<StringIdItem>();
        List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
        namesList.add(StringIdItem.internStringIdItem(dexFile, "value"));
        encodedValueList.add(a);            
        StringIdItem[] names = namesList.toArray(
                new StringIdItem[namesList.size()]);
        EncodedValue[] values = encodedValueList.toArray(
                new EncodedValue[encodedValueList.size()]);
        AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(
                annotationType, names, values);
        AnnotationItem aItem = AnnotationItem.internAnnotationItem(
                dexFile, AnnotationVisibility.SYSTEM, annotationValue);
        return aItem;
    }
    
    private AnnotationItem makeInnerClassAnnotation(InnerClassTag tag) {
        IntEncodedValue flags = new IntEncodedValue(tag.getAccessFlags());
        TypeIdItem annotationType = TypeIdItem.internTypeIdItem(
                dexFile, "Ldalvik/annotation/InnerClass;");           
        
        List<StringIdItem> namesList = new ArrayList<StringIdItem>();
        
        List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
        namesList.add(StringIdItem.internStringIdItem(dexFile, "accessFlags"));
        encodedValueList.add(flags);

        if (tag.getShortName() != null) {
	        StringIdItem nameId = StringIdItem.internStringIdItem(dexFile, tag.getShortName());
	        StringEncodedValue nameV = new StringEncodedValue(nameId);
	        namesList.add(StringIdItem.internStringIdItem(dexFile, "name"));
	        encodedValueList.add(nameV);
        }
        
        StringIdItem[] names = namesList.toArray(
                new StringIdItem[namesList.size()]);
        EncodedValue[] values = encodedValueList.toArray(
                new EncodedValue[encodedValueList.size()]);
        AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(
                annotationType, names, values);
        AnnotationItem aItem = AnnotationItem.internAnnotationItem(
                dexFile, AnnotationVisibility.SYSTEM, annotationValue);
        return aItem;
    }
    
    private AnnotationItem makeMemberClasses(List<InnerClassTag> tags) {
        TypeIdItem annotationType = TypeIdItem.internTypeIdItem(
                dexFile, "Ldalvik/annotation/MemberClasses;"); 
        List<StringIdItem> namesList = new ArrayList<StringIdItem>();
        List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
        namesList.add(StringIdItem.internStringIdItem(dexFile, "value"));
        
        List<EncodedValue> valueList = new ArrayList<EncodedValue>();
        for (InnerClassTag t : tags) {
            TypeIdItem memberType = TypeIdItem.internTypeIdItem(dexFile,
                    "L" + t.getInnerClass() + ";");
            TypeEncodedValue memberEv = new TypeEncodedValue(memberType); 
            valueList.add(memberEv);
        }
        ArrayEncodedValue a = new ArrayEncodedValue(valueList.toArray(
                new EncodedValue[valueList.size()]));
        encodedValueList.add(a);
        
        StringIdItem[] names = namesList.toArray(
                new StringIdItem[namesList.size()]);
        EncodedValue[] values = encodedValueList.toArray(
                new EncodedValue[encodedValueList.size()]);
        AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(
                annotationType, names, values);
        AnnotationItem aItem = AnnotationItem.internAnnotationItem(
                dexFile, AnnotationVisibility.SYSTEM, annotationValue);
        return aItem;
    }
    
    private AnnotationItem makeEnclosingMethod(EnclosingMethodTag tag) {
        TypeIdItem annotationType = TypeIdItem.internTypeIdItem(
                dexFile, "Ldalvik/annotation/EnclosingMethod;");
        
        String enclosingClass = DexType.toDalvikICAT(tag.getEnclosingClass());
        TypeIdItem classType = TypeIdItem.internTypeIdItem(dexFile, enclosingClass);

        String enclosingMethod = tag.getEnclosingMethod();
        String methodSig = tag.getEnclosingMethodSig();
        
        // Sometimes we don't have an enclosing method
        if (methodSig == null || methodSig.isEmpty())
        	return null;
        
        String[] split1 = methodSig.split("\\)");
	    String parametersS = split1[0].replaceAll("\\(", "");
	    String returnTypeS = split1[1];
        
        TypeIdItem returnType = TypeIdItem.internTypeIdItem(dexFile, returnTypeS);
        List<TypeIdItem> typeList = new ArrayList<TypeIdItem>();
        Debug.printDbg("parameters:", parametersS);
        if (!parametersS.equals("")) {
            for (String p : Util.splitParameters(parametersS)) {
                if (p.equals(""))
                    continue;
                Debug.printDbg("parametr: ", p);
                TypeIdItem i = TypeIdItem.internTypeIdItem(dexFile, p);
                typeList.add(i);
            }
        }
        TypeListItem parameters = TypeListItem.internTypeListItem(dexFile, typeList);
        ProtoIdItem methodPrototype = ProtoIdItem.internProtoIdItem(dexFile, returnType, parameters);
        StringIdItem methodName = StringIdItem.internStringIdItem(dexFile, enclosingMethod);
        MethodIdItem methodId = MethodIdItem.internMethodIdItem(dexFile, 
                classType, methodPrototype, methodName);
        MethodEncodedValue a = new MethodEncodedValue(methodId);                      
        List<StringIdItem> namesList = new ArrayList<StringIdItem>();
        List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
        namesList.add(StringIdItem.internStringIdItem(dexFile, "value"));
        encodedValueList.add(a);            
        StringIdItem[] names = namesList.toArray(
                new StringIdItem[namesList.size()]);
        EncodedValue[] values = encodedValueList.toArray(
                new EncodedValue[encodedValueList.size()]);
        AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(
                annotationType, names, values);
        AnnotationItem aItem = AnnotationItem.internAnnotationItem(
                dexFile, AnnotationVisibility.SYSTEM, annotationValue);
        return aItem;
    }
    
    private AnnotationItem makeDeprecatedItem() {
        TypeIdItem annotationType = TypeIdItem.internTypeIdItem(
                dexFile, "Ljava/lang/Deprecated;");
        List<StringIdItem> namesList = new ArrayList<StringIdItem>();
        List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();;            
        StringIdItem[] names = namesList.toArray(
                new StringIdItem[namesList.size()]);
        EncodedValue[] values = encodedValueList.toArray(
                new EncodedValue[encodedValueList.size()]);
        AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(
                annotationType, names, values);
        AnnotationItem aItem = AnnotationItem.internAnnotationItem(
                dexFile, AnnotationVisibility.SYSTEM, annotationValue);
        return aItem;
    }
    
    
    private AnnotationItem makeSignatureItem(SignatureTag t) {
        TypeIdItem annotationType = TypeIdItem.internTypeIdItem(
                dexFile, "Ldalvik/annotation/Signature;");           
        List<StringIdItem> namesList = new ArrayList<StringIdItem>();
        List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
        namesList.add(StringIdItem.internStringIdItem(dexFile, "value"));
        
        List<EncodedValue> valueList = new ArrayList<EncodedValue>();
        for (String s : t.getSignature().split(" ")) {
            StringIdItem member = StringIdItem.internStringIdItem(dexFile,
                    s);
            StringEncodedValue memberEv = new StringEncodedValue(member);
            //TypeEncodedValue memberEv = new TypeEncodedValue(memberType); 
            valueList.add(memberEv);
        }
        ArrayEncodedValue a = new ArrayEncodedValue(valueList.toArray(
                new EncodedValue[valueList.size()]));
        encodedValueList.add(a);
        
        StringIdItem[] names = namesList.toArray(
                new StringIdItem[namesList.size()]);
        EncodedValue[] values = encodedValueList.toArray(
                new EncodedValue[encodedValueList.size()]);
        AnnotationEncodedSubValue annotationValue = new AnnotationEncodedSubValue(
                annotationType, names, values);
        AnnotationItem aItem = AnnotationItem.internAnnotationItem(
                dexFile, AnnotationVisibility.SYSTEM, annotationValue);
        return aItem;
    }
    
    private List<AnnotationItem> makeVisibilityItem(Tag t) {

      List<AnnotationItem> aList = new ArrayList<AnnotationItem>();
      
      if (!(t instanceof VisibilityAnnotationTag))
          return aList;
      
      VisibilityAnnotationTag vat = (VisibilityAnnotationTag)t;
      List<AnnotationTag> atList = vat.getAnnotations();
          
      for (AnnotationTag at: atList) {
          List<EncodedValue> encodedValueList = new ArrayList<EncodedValue>();
          List<StringIdItem> namesList = new ArrayList<StringIdItem>();
          
          for (AnnotationElem ae : at.getElems()) {
              EncodedValue value = getAnnotationElement(ae);
              encodedValueList.add(value);
              namesList.add(StringIdItem.internStringIdItem(dexFile, ae.getName()));
              Debug.printDbg("new  annotation: ", value ," ", ae.getName() ," type: ", value.getClass());
          }
      
          String type = at.getType();
          Debug.printDbg(" annotation type: ", type);
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
    
//    private AnnotationItem makeConstantItem(Tag t) {
//        if (!(t instanceof ConstantValueTag))
//            throw new RuntimeException("error: t not ConstantValueTag.");
//        return null;
//    }
        
    
    
    /**
     * Converts type such as "a.b.c[][]" to "[[a/b/c".
     * 
     * @param sootType Soot type to convert to Dexlib type.
     * @return String representation of the Dexlib type
     */
    public static String soot2DalvikType(String sootType) {
        return sootType;
//        if (sootType.startsWith("L") || sootType.startsWith("[")) {
//            throw new RuntimeException("error: wrong type format: "+ sootType);
//        }
//        
//        int arraySize = 0;
//        while (sootType.endsWith("[]")) {
//            arraySize++;
//            sootType = sootType.replaceFirst("\\[\\]", "");
//        }
//        String type = "";
//        if (sootType.equals("int")) {
//            type = "I";
//        } else if (sootType.equals("float")) {
//            type = "F";
//        } else if (sootType.equals("long")) {
//            type = "J";
//        } else if (sootType.equals("double")) {
//            type = "D";
//        } else if (sootType.equals("char")) {
//            type = "C";
//        } else if (sootType.equals("boolean")) {
//            type = "Z";
//        } else if (sootType.equals("byte")) {
//            type = "B";
//        } else if (sootType.equals("void")) {
//            type = "V";
//        } else {
//            type = "L"+ sootType.replaceAll("\\.", "/") +";";
//        }
//        
//        if (type.startsWith("LL")) {
//            throw new RuntimeException("error: wrong type format: "+ type);
//        }
//        
//        for (int i = 0; i< arraySize; i++)
//            type = "[" + type;
//        return type;     
        
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
