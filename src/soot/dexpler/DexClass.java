/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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

package soot.dexpler;

import java.util.Iterator;
import java.util.Set;

import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;

import soot.Modifier;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;
import soot.tagkit.InnerClassAttribute;
import soot.tagkit.InnerClassTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.Tag;

/**
 * DexClass is a container for all relevant information of that class
 * the name of the superclass, interfaces and its annotations, and modifier are stored, as well as all fields, methods, and types that are referenced throughout the class are available here.
 *
 */

public class DexClass {

    protected String name;
    protected String type;

    protected String superClassName;
    protected String[] interfaceNames;

    protected Set<? extends Annotation> annotations;
    protected Set<SootField> fields;
    protected Set<SootMethod> soot_methods;
    protected Set<Type> types;

    protected int accessFlags;
    

    /**
     * The constructor consumes a class definition item of dexlib and retrieves all subsequent methods, types and fields.
     * @param classDef
     */
    private DexClass(ClassDef classDef) {}


    public static Dependencies makeSootClass(SootClass sc, ClassDef defItem, DexFile dexFile) {
        String superClass = defItem.getSuperclass();
        Dependencies deps = new Dependencies();

        // source file
        String sourceFile = defItem.getSourceFile();
        if (sourceFile != null) {
            sc.addTag(new SourceFileTag(sourceFile));
        }

        // super class for hierarchy level
        if (superClass != null) {
	        String superClassName = Util.dottedClassName(superClass);
	        SootClass sootSuperClass = SootResolver.v().makeClassRef(superClassName);
	        sc.setSuperclass(sootSuperClass);
	        deps.typesToHierarchy.add(sootSuperClass.getType());
        }

        // access flags
        int accessFlags = defItem.getAccessFlags();
        sc.setModifiers(accessFlags);

        // Retrieve interface names
        if (defItem.getInterfaces() != null) {
            for (String interfaceName : defItem.getInterfaces()) {
                String interfaceClassName = Util.dottedClassName(interfaceName);
                if (sc.implementsInterface(interfaceClassName))
                    continue;
                
                SootClass interfaceClass = SootResolver.v().makeClassRef(interfaceClassName);
                interfaceClass.setModifiers(interfaceClass.getModifiers() | Modifier.INTERFACE);
                sc.addInterface(interfaceClass);
                deps.typesToHierarchy.add(interfaceClass.getType());
            }
        }
        
        if (Options.v().oaat() && sc.resolvingLevel() <= SootClass.HIERARCHY) {
            return deps;
        }
        DexAnnotation da = new DexAnnotation(sc, deps);
        
        // get the fields of the class
        for (Field sf : defItem.getStaticFields()) {
            if (sc.declaresField(sf.getName(), DexType.toSoot(sf.getType())))
                continue;
            SootField sootField = DexField.makeSootField(sf);
            sc.addField(sootField);
            da.handleFieldAnnotation(sootField, sf);
        }
        for (Field f: defItem.getInstanceFields()) {
            if (sc.declaresField(f.getName(), DexType.toSoot(f.getType())))
                continue;
            SootField sootField = DexField.makeSootField(f);
            sc.addField(sootField);
            da.handleFieldAnnotation(sootField, f);
        }
        
        // get the methods of the class
        for (Method method : defItem.getDirectMethods()) {
            SootMethod sm = DexMethod.makeSootMethod(dexFile, method, sc);
            if (sc.declaresMethod(sm.getName(), sm.getParameterTypes(), sm.getReturnType()))
                continue;
            sc.addMethod(sm);
            da.handleMethodAnnotation(sm, method);
        }
        for (Method method : defItem.getVirtualMethods()) {
            SootMethod sm = DexMethod.makeSootMethod(dexFile, method, sc);
            if (sc.declaresMethod(sm.getName(), sm.getParameterTypes(), sm.getReturnType()))
                continue;
            sc.addMethod(sm);
            da.handleMethodAnnotation(sm, method);
        }
                
        da.handleClassAnnotation(defItem);
                
        // In contrast to Java, Dalvik associates the InnerClassAttribute
        // with the inner class, not the outer one. We need to copy the
        // tags over to correspond to the Soot semantics.
        InnerClassAttribute ica = (InnerClassAttribute) sc.getTag("InnerClassAttribute");
        if (ica != null) {
        	Iterator<InnerClassTag> innerTagIt = ica.getSpecs().iterator();
        	while (innerTagIt.hasNext()) {
        		Tag t = innerTagIt.next();
        		if (t instanceof InnerClassTag) {
        			InnerClassTag ict = (InnerClassTag) t;
        			
        			// Check the inner class to make sure that this tag actually
        			// refers to the current class as the inner class
        			String inner = ict.getInnerClass().replaceAll("/", ".");
        			if (!inner.equals(sc.getName()))
        				continue;
        			
        			String outer = null;
					if (ict.getOuterClass() == null) { // anonymous and local classes
						outer = ict.getInnerClass().replaceAll("\\$[0-9].*$", "").replaceAll("/", ".");
        			} else {
        				outer = ict.getOuterClass().replaceAll("/", ".");
        			}
        			
        			SootClass osc = SootResolver.v().makeClassRef(outer);
        			if (osc == sc) {
        				if (!sc.hasOuterClass())
        					continue;
        				osc = sc.getOuterClass();
        			}
        			
        			// Get the InnerClassAttribute of the outer class
        			InnerClassAttribute icat = (InnerClassAttribute)osc.getTag("InnerClassAttribute");
        			if (icat == null) {
        				icat = new InnerClassAttribute();
        				osc.addTag(icat);
        			}
        			
        			// Transfer the tag from the inner class to the outer class
        			InnerClassTag newt = new InnerClassTag(ict.getInnerClass(), ict.getOuterClass(),
        					ict.getShortName(), ict.getAccessFlags());
        			icat.add(newt);
        			
        			// Remove the tag from the inner class as inner classes do
        			// not have these tags in the Java / Soot semantics. The
        			// DexPrinter will copy it back if we do dex->dex.
					innerTagIt.remove();
        		}
        	}
        }
        
        return deps;
    }


}
