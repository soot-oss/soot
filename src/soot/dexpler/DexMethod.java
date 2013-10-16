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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.AnnotationElement;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.value.ArrayEncodedValue;
import org.jf.dexlib2.iface.value.EncodedValue;
import org.jf.dexlib2.iface.value.TypeEncodedValue;

import soot.Body;
import soot.MethodSource;
import soot.Modifier;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;
import soot.options.Options;

/**
 * DexMethod is a container for all methods that are declared in a class.
 * It holds information about its name, the class it belongs to, its access flags, thrown exceptions, the return type and parameter types as well as the encoded method itself.
 *
 */
public class DexMethod {

    private DexMethod() {}

    /**
     * Retrieve the SootMethod equivalent of this method
     * @return the SootMethod of this method
     */
    public static SootMethod makeSootMethod(String dexFile, Method method, SootClass declaringClass) {

        Set<Type> types = new HashSet<Type>();

        int accessFlags = method.getAccessFlags();
        List<Type> parameterTypes = new ArrayList<Type>();

        // get the name of the method
        String name = method.getName();
        Debug.printDbg("processing method '", method.getDefiningClass() ,": ", method.getReturnType(), " ", method.getName(), " p: ", method.getParameters(), "'");


        // the following snippet retrieves all exceptions that this method throws by analyzing its annotations
        List<SootClass> thrownExceptions = new ArrayList<SootClass>();
        for (Annotation a : method.getAnnotations()) {
            Type atype = DexType.toSoot(a.getType());
            String atypes = atype.toString();
            if (!(atypes.equals("dalvik.annotation.Throws")))
                continue;
            for (AnnotationElement ae : a.getElements()) {
                EncodedValue ev = ae.getValue();
                if(ev instanceof ArrayEncodedValue) {
                    for(EncodedValue evSub : ((ArrayEncodedValue) ev).getValue()) {
                        if(evSub instanceof TypeEncodedValue) {
                            TypeEncodedValue valueType = (TypeEncodedValue) evSub;
                            String exceptionName = valueType.getValue();
                            String dottedName = Util.dottedClassName(exceptionName);
                            thrownExceptions.add(SootResolver.v().makeClassRef(dottedName));
                        }
                    }
                }
            }
        }


        // retrieve all parameter types
        if (method.getParameters() != null) {
            List<? extends CharSequence> parameters = method.getParameterTypes();

            for(CharSequence t : parameters) {
                Type type = DexType.toSoot(t.toString());
                parameterTypes.add(type);
                types.add(type);
            }
        }

        // retrieve the return type of this method
        Type returnType = DexType.toSoot(method.getReturnType());
        types.add(returnType);

        //Build soot method by all available parameters
        SootMethod sm = null;
        if (declaringClass.declaresMethod(name, parameterTypes, returnType)) {
            sm = declaringClass.getMethod(name, parameterTypes, returnType);
        } else {
            sm = new SootMethod(name, parameterTypes, returnType, accessFlags, thrownExceptions);
        }

        // if the method is abstract or native, no code needs to be transformed
        int flags = method.getAccessFlags();
        if (Modifier.isAbstract(flags)|| Modifier.isNative(flags))
            return sm;

        if (Options.v().oaat() && declaringClass.resolvingLevel() <= SootClass.SIGNATURES)
            return sm;

//        // retrieve all local types of the method
//        DebugInfoItem debugInfo = method.g.codeItem.getDebugInfo();
//        if(debugInfo!=null) {
//			for(Item<?> item : debugInfo.getReferencedItems()) {
//	            if (item instanceof TypeIdItem) {
//	                Type type = DexType.toSoot((TypeIdItem) item);
//	                dexClass.types.add(type);
//	            }
//
//	        }
//        }

        //add the body of this code item
        final DexBody dexBody = new DexBody(dexFile, method, (RefType) declaringClass.getType());

        for (Type t : dexBody.usedTypes())
            types.add(t);



        if (dexBody != null) {
            // sets the method source by adding its body as the active body
            sm.setSource(new MethodSource() {
                    public Body getBody(SootMethod m, String phaseName) {
                        m.setActiveBody(dexBody.jimplify(m));
                        return m.getActiveBody();
                    }
                });
        }

        return sm;
    }
}
