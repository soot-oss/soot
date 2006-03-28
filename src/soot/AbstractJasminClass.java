/* Soot - a J*va Optimization Framework
 * 
 * 
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
 * Copyright (C) 2004 Jennifer Lhotak, Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot;
import soot.options.*;
import soot.tagkit.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;
import java.io.*;
import soot.baf.*;

public abstract class AbstractJasminClass
{
    protected Map unitToLabel;
    protected Map localToSlot;
    protected Map subroutineToReturnAddressSlot;

    protected List code;

    protected boolean isEmittingMethodCode;
    protected int labelCount;

    protected boolean isNextGotoAJsr;
    protected int returnAddressSlot;
    protected int currentStackHeight = 0;
    protected int maxStackHeight = 0;

    protected Map localToGroup;
    protected Map groupToColorCount;
    protected Map localToColor; 


    protected Map blockToStackHeight = new HashMap(); // maps a block to the stack height upon entering it
    protected Map blockToLogicalStackHeight = new HashMap(); // maps a block to the logical stack height upon entering it
    

    public static String slashify(String s)
    {
        return s.replace('.', '/');
    }

    public static int sizeOfType(Type t)
    {
        if(t instanceof DoubleWordType || t instanceof LongType || t instanceof DoubleType)
            return 2;
        else if(t instanceof VoidType)
            return 0;
        else
            return 1;
    }

    public static int argCountOf(SootMethodRef m)
    {
        int argCount = 0;
        Iterator typeIt = m.parameterTypes().iterator();

        while(typeIt.hasNext())
        {
            Type t = (Type) typeIt.next();

            argCount += sizeOfType(t);
        }

        return argCount;
    }

    public static String jasminDescriptorOf(Type type)
    {
        TypeSwitch sw;

        type.apply(sw = new TypeSwitch()
        {
            public void caseBooleanType(BooleanType t)
            {
                setResult("Z");
            }

            public void caseByteType(ByteType t)
            {
                setResult("B");
            }

            public void caseCharType(CharType t)
            {
                setResult("C");
            }

            public void caseDoubleType(DoubleType t)
            {
                setResult("D");
            }

            public void caseFloatType(FloatType t)
            {
                setResult("F");
            }

            public void caseIntType(IntType t)
            {
                setResult("I");
            }

            public void caseLongType(LongType t)
            {
                setResult("J");
            }

            public void caseShortType(ShortType t)
            {
                setResult("S");
            }

            public void defaultCase(Type t)
            {
                throw new RuntimeException("Invalid type: " + t);
            }

            public void caseArrayType(ArrayType t)
            {
                StringBuffer buffer = new StringBuffer();

                for(int i = 0; i < t.numDimensions; i++)
                    buffer.append("[");

                setResult(buffer.toString() + jasminDescriptorOf(t.baseType));
            }

            public void caseRefType(RefType t)
            {
                setResult("L" + t.getClassName().replace('.', '/') + ";");
            }

            public void caseVoidType(VoidType t)
            {
                setResult("V");
            }
        });

        return (String) sw.getResult();

    }

    public static String jasminDescriptorOf(SootMethodRef m)
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("(");

        // Add methods parameters
        {
            Iterator typeIt = m.parameterTypes().iterator();

            while(typeIt.hasNext())
            {
                Type t = (Type) typeIt.next();

                buffer.append(jasminDescriptorOf(t));
            }
        }

        buffer.append(")");

        buffer.append(jasminDescriptorOf(m.returnType()));

        return buffer.toString();
    }

    protected void emit(String s)
    {
        okayEmit(s);
    }
    
    protected void okayEmit(String s)
    {
        if(isEmittingMethodCode && !s.endsWith(":"))
            code.add("    " + s);
        else
            code.add(s);
    }
    
    private String getVisibilityAnnotationAttr(VisibilityAnnotationTag tag){
        StringBuffer sb = new StringBuffer();
        if (tag.getVisibility() == AnnotationConstants.RUNTIME_VISIBLE){
            sb.append(".runtime_visible_annotation\n");
        }
        else {
            sb.append(".runtime_invisible_annotation\n");
        }
        if (tag.hasAnnotations()){
            Iterator it = tag.getAnnotations().iterator();
            while (it.hasNext()){
                AnnotationTag annot = (AnnotationTag)it.next();
                sb.append(".annotation ");
                sb.append(soot.util.StringTools.getQuotedStringOf(annot.getType())+"\n");
                for (int i = 0; i < annot.getNumElems(); i++){
                    sb.append(getElemAttr(annot.getElemAt(i)));
                }
                sb.append(".end .annotation\n");
            }
        }
        sb.append(".end .annotation_attr\n");
        return sb.toString();    
    }
   
    
    private String getVisibilityParameterAnnotationAttr(VisibilityParameterAnnotationTag tag){
        StringBuffer sb = new StringBuffer();
        sb.append(".param ");
        if (tag.getKind() == AnnotationConstants.RUNTIME_VISIBLE){
            sb.append(".runtime_visible_annotation\n");
        }
        else {
            sb.append(".runtime_invisible_annotation\n");
        }
        ArrayList vis_list = tag.getVisibilityAnnotations();
        if (vis_list != null){
            Iterator it = vis_list.iterator();
            while (it.hasNext()){
                sb.append(getVisibilityAnnotationAttr((VisibilityAnnotationTag)it.next()));
            }
        }
        sb.append(".end .param\n");
        return sb.toString();    
    }
   
    private String getElemAttr(AnnotationElem elem){
        StringBuffer result = new StringBuffer(".elem ");
        switch (elem.getKind()){
            case 'Z': {
                        result.append(".bool_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(((AnnotationIntElem)elem).getValue());
                        result.append("\n");
                        break;
                      }
            case 'S': {
                        result.append(".short_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(((AnnotationIntElem)elem).getValue());
                        result.append("\n");
                        break;
                      }
            case 'B': {
                        result.append(".byte_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(((AnnotationIntElem)elem).getValue());
                        result.append("\n");
                        break;
                      }
            case 'C': {
                        result.append(".char_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(((AnnotationIntElem)elem).getValue());
                        result.append("\n");
                        break;
                      }
            case 'I': {
                        result.append(".int_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(((AnnotationIntElem)elem).getValue());
                        result.append("\n");
                        break;
                      }
            case 'J': {
                        result.append(".long_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(((AnnotationLongElem)elem).getValue());
                        result.append("\n");
                        break; 
                      }
            case 'F': {
                        result.append(".float_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(Float.floatToRawIntBits(((AnnotationFloatElem)elem).getValue()));
                        result.append("\n");
                        break;
                      }
            case 'D': {
                        result.append(".doub_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(Double.doubleToRawLongBits(((AnnotationDoubleElem)elem).getValue()));
                        result.append("\n");
                        break;
                      }
            case 's': {
                        result.append(".str_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(soot.util.StringTools.getQuotedStringOf(((AnnotationStringElem)elem).getValue()));
                        result.append("\n");
                        break;
                      }
            case 'e': {
                        result.append(".enum_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(soot.util.StringTools.getQuotedStringOf(((AnnotationEnumElem)elem).getTypeName())); 
                        result.append(" ");
                        result.append(soot.util.StringTools.getQuotedStringOf(((AnnotationEnumElem)elem).getConstantName())); 
                        result.append("\n");
                        break;
                      }
            case 'c': {
                        result.append(".cls_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        result.append(soot.util.StringTools.getQuotedStringOf(((AnnotationClassElem)elem).getDesc())); 
                        result.append("\n");
                        break;
                      }
            case '[': {
                        result.append(".arr_kind ");
                        result.append("\""+elem.getName()+"\" ");
                        AnnotationArrayElem arrayElem = (AnnotationArrayElem)elem;
                        result.append("\n");
                        for (int i = 0; i < arrayElem.getNumValues(); i++){
                            //result.append("\n");
                            result.append(getElemAttr(arrayElem.getValueAt(i)));
                        }
                        result.append(".end .arr_elem\n");
                        break;
                      }
            case '@': {
                        result.append(".ann_kind ");
                        result.append("\""+elem.getName()+"\"\n");
                        AnnotationTag annot = ((AnnotationAnnotationElem)elem).getValue();
                        result.append(".annotation ");
                        result.append(soot.util.StringTools.getQuotedStringOf(annot.getType())+"\n");
                        for (int i = 0; i < annot.getNumElems(); i++){
                            result.append(getElemAttr(annot.getElemAt(i)));
                        }
                        result.append(".end .annotation\n");
                        result.append(".end .annot_elem\n");
                        break;
                      }
            default : {
                throw new RuntimeException("Unknown Elem Attr Kind: "+elem.getKind());
            }
        }
        return result.toString();
    }
    
    public AbstractJasminClass(SootClass sootClass)
    {
        if(Options.v().time())
            Timers.v().buildJasminTimer.start();
        
        if(Options.v().verbose())
            G.v().out.println("[" + sootClass.getName() + "] Constructing baf.JasminClass...");

        code = new LinkedList();

        // Emit the header
        {
            int modifiers = sootClass.getModifiers();

            
            if ((sootClass.getTag("SourceFileTag") != null) && (!Options.v().no_output_source_file_attribute())){
                String srcName = ((SourceFileTag)sootClass.getTag("SourceFileTag")).getSourceFile();
                emit(".source "+soot.util.StringTools.getEscapedStringOf(srcName));
            }
            if(Modifier.isInterface(modifiers))
            {
                modifiers -= Modifier.INTERFACE;

                emit(".interface " + Modifier.toString(modifiers) + " " + slashify(sootClass.getName()));
            }
            else
                emit(".class " + Modifier.toString(modifiers) + " " + slashify(sootClass.getName()));

            if(sootClass.hasSuperclass())
                emit(".super " + slashify(sootClass.getSuperclass().getName()));
            else
                emit(".no_super");

            emit("");
        }

        // Emit the interfaces
        {
            Iterator interfaceIt = sootClass.getInterfaces().iterator();

            while(interfaceIt.hasNext())
            {
                SootClass inter = (SootClass) interfaceIt.next();

                emit(".implements " + slashify(inter.getName()));
            }

            /* why do this????
            if(sootClass.getInterfaceCount() != 0)
                emit("");*/
        }





    
	// emit class attributes.
	Iterator it =  sootClass.getTags().iterator(); 
	while(it.hasNext()) {
	    Tag tag = (Tag) it.next();
	    if(tag instanceof Attribute)
		emit(".class_attribute "  + tag.getName() + " \"" + new String(Base64.encode(((Attribute)tag).getValue()))+"\"");
        /*else {
            emit("");
        }*/
	}


    // emit synthetic attributes
    if (sootClass.hasTag("SyntheticTag")){
        emit(".synthetic\n");
    }
    // emit deprecated attributes
    if (sootClass.hasTag("DeprecatedTag")){
        emit(".deprecated\n");
    }
    // emit inner class attributes
    if (sootClass.hasTag("InnerClassAttribute")){
        if (!Options.v().no_output_inner_classes_attribute()){
            emit(".inner_class_attr ");
            Iterator innersIt = ((InnerClassAttribute)sootClass.getTag("InnerClassAttribute")).getSpecs().iterator();
            while (innersIt.hasNext()){
                InnerClassTag ict = (InnerClassTag)innersIt.next();
                //System.out.println("inner class tag: "+ict);
                emit(".inner_class_spec_attr "+
                    "\""+ict.getInnerClass()+"\" "+
                
                    "\""+ict.getOuterClass()+"\" "+
                
                    "\""+ict.getShortName()+"\" "+
                    Modifier.toString(ict.getAccessFlags())+" "+

                ".end .inner_class_spec_attr");
            }
            emit(".end .inner_class_attr\n");
        }
    }
    if (sootClass.hasTag("EnclosingMethodTag")){
        String encMeth = ".enclosing_method_attr ";
        EnclosingMethodTag eMethTag = (EnclosingMethodTag)sootClass.getTag("EnclosingMethodTag");
        encMeth += "\""+eMethTag.getEnclosingClass()+"\" ";
        encMeth += "\""+eMethTag.getEnclosingMethod()+"\" ";
        encMeth += "\""+eMethTag.getEnclosingMethodSig()+"\"\n";
        emit(encMeth);
    }
    if (sootClass.hasTag("SignatureTag")){
        String sigAttr = ".signature_attr ";
        SignatureTag sigTag = (SignatureTag)sootClass.getTag("SignatureTag");
        sigAttr += "\""+sigTag.getSignature()+"\"\n";
        emit(sigAttr);
    }
    
    Iterator vit = sootClass.getTags().iterator();
    while (vit.hasNext()){
        Tag t = (Tag)vit.next();
        if (t.getName().equals("VisibilityAnnotationTag")){
            emit(getVisibilityAnnotationAttr((VisibilityAnnotationTag)t));
        }
    }

        // Emit the fields
        {
            Iterator fieldIt = sootClass.getFields().iterator();

            while(fieldIt.hasNext())
            {
                SootField field = (SootField) fieldIt.next();

                String fieldString = ".field " + Modifier.toString(field.getModifiers()) + " " +  "\"" + field.getName() + "\"" + " " + jasminDescriptorOf(field.getType());
    
                if (field.hasTag("StringConstantValueTag")){
                    fieldString += " = ";
                    fieldString += soot.util.StringTools.getQuotedStringOf(((StringConstantValueTag)field.getTag("StringConstantValueTag")).getStringValue());
                }
                else if (field.hasTag("IntegerConstantValueTag")){
                    fieldString += " = ";
                    fieldString += ((IntegerConstantValueTag)field.getTag("IntegerConstantValueTag")).getIntValue();
                }
                else if (field.hasTag("LongConstantValueTag")){
                    fieldString += " = ";
                    fieldString += ((LongConstantValueTag)field.getTag("LongConstantValueTag")).getLongValue();
                }
                else if (field.hasTag("FloatConstantValueTag")){
                    fieldString += " = ";
                    float val = ((FloatConstantValueTag)field.getTag("FloatConstantValueTag")).getFloatValue();
                    fieldString += Float.floatToRawIntBits(val);
                }
                else if (field.hasTag("DoubleConstantValueTag")){
                    fieldString += " = ";
                    double val = ((DoubleConstantValueTag)field.getTag("DoubleConstantValueTag")).getDoubleValue();
                    fieldString += Double.doubleToRawLongBits(val);
                }
                if (field.hasTag("SyntheticTag")){
                    fieldString +=" .synthetic";
                }

                fieldString +="\n";
                if (field.hasTag("DeprecatedTag")){
                    fieldString +=".deprecated\n";
                }
                if (field.hasTag("SignatureTag")){
                    fieldString += ".signature_attr ";
                    SignatureTag sigTag = (SignatureTag)field.getTag("SignatureTag");
                    fieldString += "\""+sigTag.getSignature()+"\"\n";
                }
                Iterator vfit = field.getTags().iterator();
                while (vfit.hasNext()){
                    Tag t = (Tag)vfit.next();
                    if (t.getName().equals("VisibilityAnnotationTag")){
                        fieldString += getVisibilityAnnotationAttr((VisibilityAnnotationTag)t);
                    }
                }

                emit(fieldString);

		Iterator attributeIt =  field.getTags().iterator(); 
		while(attributeIt.hasNext()) {
		    Tag tag = (Tag) attributeIt.next();
		    if(tag instanceof Attribute)
			emit(".field_attribute " + tag.getName() + " \"" + new String(Base64.encode(((Attribute)tag).getValue())) +"\"");
		}

            }

            if(sootClass.getFieldCount() != 0)
                emit("");
        }

        // Emit the methods
        {
            Iterator methodIt = sootClass.methodIterator();

            while(methodIt.hasNext())
            {
                emitMethod((SootMethod) methodIt.next());
                emit("");
            }
        }
        
        if(Options.v().time())
            Timers.v().buildJasminTimer.end();
    }

    protected void assignColorsToLocals(Body body)
    {
        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() +
                "] Assigning colors to locals...");
        
        if(Options.v().time())
            Timers.v().packTimer.start();

        localToGroup = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
        groupToColorCount = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
        localToColor = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
        
        // Assign each local to a group, and set that group's color count to 0.
        {
            Iterator localIt = body.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();
                Object g;
                
                if(sizeOfType(l.getType()) == 1)
                    g = IntType.v();
                else
                    g = LongType.v();
                
                localToGroup.put(l, g);
                
                if(!groupToColorCount.containsKey(g))
                {
                    groupToColorCount.put(g, new Integer(0));
                }
            }
        }

        // Assign colors to the parameter locals.
        {
            Iterator codeIt = body.getUnits().iterator();

            while(codeIt.hasNext())
            {
                Stmt s = (Stmt) codeIt.next();

                if(s instanceof IdentityStmt &&
                    ((IdentityStmt) s).getLeftOp() instanceof Local)
                {
                    Local l = (Local) ((IdentityStmt) s).getLeftOp();
                    
                    Object group = localToGroup.get(l);
                    int count = ((Integer) groupToColorCount.get(group)).intValue();
                    
                    localToColor.put(l, new Integer(count));
                    
                    count++;
                    
                    groupToColorCount.put(group, new Integer(count));
                }
            }
        }
        
    }

    protected void emitMethod(SootMethod method)
    {
       if (method.isPhantom())
           return;

       // Emit prologue
            emit(".method " + Modifier.toString(method.getModifiers()) + " " +
                 method.getName() + jasminDescriptorOf(method.makeRef()));

            Iterator throwsIt = method.getExceptions().iterator();
            while (throwsIt.hasNext()){
                SootClass exceptClass = (SootClass)throwsIt.next();
                emit(".throws "+exceptClass.getName());
            }
            if (method.hasTag("SyntheticTag")){
                emit(".synthetic");
            }
            if (method.hasTag("DeprecatedTag")){
                emit(".deprecated");
            }
            if (method.hasTag("SignatureTag")){
                String sigAttr = ".signature_attr ";
                SignatureTag sigTag = (SignatureTag)method.getTag("SignatureTag");
                sigAttr += "\""+sigTag.getSignature()+"\"";
                emit(sigAttr);
            }
            if (method.hasTag("AnnotationDefaultTag")){
                String annotDefAttr = ".annotation_default ";
                AnnotationDefaultTag annotDefTag = (AnnotationDefaultTag)method.getTag("AnnotationDefaultTag");
                annotDefAttr += getElemAttr(annotDefTag.getDefaultVal());
                annotDefAttr += ".end .annotation_default";
                emit(annotDefAttr);
            }
            Iterator vit = method.getTags().iterator();
            while (vit.hasNext()){
                Tag t = (Tag)vit.next();
                if (t.getName().equals("VisibilityAnnotationTag")){
                    emit(getVisibilityAnnotationAttr((VisibilityAnnotationTag)t));
                }
                if (t.getName().equals("VisibilityParameterAnnotationTag")){
                    emit(getVisibilityParameterAnnotationAttr((VisibilityParameterAnnotationTag)t));
                }
            }

       if(method.isConcrete())
       {
            if(!method.hasActiveBody())
                throw new RuntimeException("method: " + method.getName() + " has no active body!");
            else
                emitMethodBody(method);
       }
       
       // Emit epilogue
            emit(".end method");

	    Iterator it =  method.getTags().iterator();
	    while(it.hasNext()) {
		Tag tag = (Tag) it.next();
		if(tag instanceof Attribute)
		    emit(".method_attribute "  + tag.getName() + " \"" + new String(Base64.encode(tag.getValue())) +"\"");
	    }	    
    }
    
    protected abstract void emitMethodBody(SootMethod method);

    public void print(PrintWriter out)
    {
        Iterator it = code.iterator();

        while(it.hasNext())
            out.println(it.next());
    }

}

