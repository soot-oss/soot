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
import java.util.List;

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

/**
 * DexMethod is a container for all methods that are declared in a class.
 * It holds information about its name, the class it belongs to, its access flags, thrown exceptions, the return type and parameter types as well as the encoded method itself.
 *
 */
public class DexMethod {

    protected String name;
    protected DexClass dexClass;
    protected int accessFlags;
    protected List<String> thrownExceptions;
    protected Type returnType;
    protected List<Type> parameterTypes;

    private DexBody dexBody;

    public DexMethod(String dexFile, Method method, DexClass dexClass) {
        this.dexClass = dexClass;
        this.accessFlags = method.getAccessFlags();
        parameterTypes = new ArrayList<Type>();
        // get the name of the method
        this.name = method.getName();
        Debug.printDbg("processing method '", method.getDefiningClass() ,": ", method.getReturnType(), " ", method.getName(), " p: ", method.getParameters(), "'");


        // the following snippet retrieves all exceptions that this method throws by analyzing its annotations
        thrownExceptions = new ArrayList<String>();
        for (Annotation a : method.getAnnotations()) {
            for (AnnotationElement ae : a.getElements()) {
                EncodedValue ev = ae.getValue();
                if(ev instanceof ArrayEncodedValue) {
                    for(EncodedValue evSub : ((ArrayEncodedValue) ev).getValue()) {
                        if(evSub instanceof TypeEncodedValue) {
                            TypeEncodedValue valueType = (TypeEncodedValue) evSub;
                            thrownExceptions.add(valueType.getValue());
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
                this.parameterTypes.add(type);
                dexClass.types.add(type);
            }
        }

        // retrieve the return type of this method
        returnType = DexType.toSoot(method.getReturnType());
        dexClass.types.add(this.returnType);

        // if the method is abstract or native, no code needs to be transformed
        int flags = method.getAccessFlags();
        if (Modifier.isAbstract(flags)|| Modifier.isNative(flags))
            return;

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
        dexBody = new DexBody(dexFile, method, (RefType) DexType.toSoot(dexClass.getType()));

        for (Type t : dexBody.usedTypes())
            dexClass.types.add(t);
    }
    /**
     * @return The name of the method.
     */
    public String getName() {
        return this.name;
    }
    /**
     * @return the class name concatenated with the method name
     */
    public String getFullName() {
        return this.dexClass.getName() + "->" + name;
    }

    /**
     * Returns the dexClass that this method belongs to
     * @return DexClass
     */
    public DexClass getDexClass() {
        return this.dexClass;
    }
    /**
     *
     * @return the return type of the method
     */
    public Type getReturnType() {
        return this.returnType;
    }
    /**
     *
     * @return the modifiers of this method
     */
    public int getModifiers() {
        return this.accessFlags;
    }

    /**
     * Return the exceptions that are declared by the method via a throws clause.
     *
     * @return the byte code names of the declared exceptions
     */
    public List<String> thrownExceptions() {
        return thrownExceptions;
    }

    /**
     *
     * @return a list of types that the parameters of this method use
     */
    public List<Type> getParameterTypes() {
        return this.parameterTypes;
    }

    /**
     * Retrieve the SootMethod equivalent of this method
     * @return the SootMethod of this method
     */
    public SootMethod toSoot() {
        List<Type> parameters = new ArrayList<Type>();
        for(Type t : parameterTypes) {
            parameters.add(t);
        }
        List<SootClass> exceptions = new ArrayList<SootClass>();
        for (String exceptionName : thrownExceptions()) {
            String dottedName = Util.dottedClassName(exceptionName);
            exceptions.add(SootResolver.v().makeClassRef(dottedName));
        }

        //Build soot method by all available parameters
        SootMethod m = new SootMethod(name, parameters, returnType,
                                      accessFlags, exceptions);
        if (dexBody != null) {
            // sets the method source by adding its body as the active body
            m.setSource(new MethodSource() {
                    public Body getBody(SootMethod m, String phaseName) {
                        m.setActiveBody(dexBody.jimplify(m));
                        return m.getActiveBody();
                    }
                });
        }

        return m;
    }
}
