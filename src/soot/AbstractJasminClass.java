/* Soot - a J*va Optimization Framework
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
                emit(".source "+srcName);
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

            if(sootClass.getInterfaceCount() != 0)
                emit("");
        }





    
	// emit class attributes.
	Iterator it =  sootClass.getTags().iterator(); 
	while(it.hasNext()) {
	    Tag tag = (Tag) it.next();
	    if(tag instanceof Attribute)
		emit(".class_attribute "  + tag.getName() + " \"" + new String(Base64.encode(((Attribute)tag).getValue()))+"\"");
        else if (tag instanceof InnerClassAttribute){
            if (!Options.v().no_output_inner_classes_attribute()){
                emit(".inner_class_attr ");
                Iterator innersIt = ((InnerClassAttribute)tag).getSpecs().iterator();
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
        else {
            emit("");
        }
	}




        // Emit the fields
        {
            Iterator fieldIt = sootClass.getFields().iterator();

            while(fieldIt.hasNext())
            {
                SootField field = (SootField) fieldIt.next();

                if (field.hasTag("SyntheticTag")){
                    emit(".field " + Modifier.toString(field.getModifiers()) + " " +
                     "\"" + field.getName() + "\"" + " " + jasminDescriptorOf(field.getType())+" .synthetic");
                }
                else {
                    emit(".field " + Modifier.toString(field.getModifiers()) + " " +
                     "\"" + field.getName() + "\"" + " " + jasminDescriptorOf(field.getType()));
                }

		Iterator attributeIt =  field.getTags().iterator(); 
		while(attributeIt.hasNext()) {
		    Tag tag = (Tag) attributeIt.next();
		    if(tag instanceof Attribute)
			emit(".field_attribute " + tag.getName() + " \"" + new String(Base64.encode(((Attribute)tag).getValue())) +"\"");
            /*else if (tag instanceof SyntheticTag){
                emit(".synthetic");
            }*/
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

