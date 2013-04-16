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

import org.jf.dexlib.AnnotationDirectoryItem.MethodAnnotationIteratorDelegate;
import org.jf.dexlib.AnnotationItem;
import org.jf.dexlib.AnnotationSetItem;
import org.jf.dexlib.ClassDataItem;
import org.jf.dexlib.DebugInfoItem;
import org.jf.dexlib.DexFile;
import org.jf.dexlib.Item;
import org.jf.dexlib.MethodIdItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.EncodedValue.AnnotationEncodedSubValue;
import org.jf.dexlib.EncodedValue.ArrayEncodedValue;
import org.jf.dexlib.EncodedValue.EncodedValue;
import org.jf.dexlib.EncodedValue.TypeEncodedValue;

import soot.Body;
import soot.MethodSource;
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
    protected DexType returnType;
    protected List<DexType> parameterTypes;

    private DexBody dexBody;

    public DexMethod(DexFile dexFile, ClassDataItem.EncodedMethod method, DexClass dexClass) {
        this.dexClass = dexClass;
        this.accessFlags = method.accessFlags;
        parameterTypes = new ArrayList<DexType>();
        // get the name of the method
        this.name = method.method.getMethodName().getStringValue();
        Debug.printDbg("processing method '", dexClass.name ," ", name ,"'");

        // this delegator processes the set of annotations acording to the dexlib interface
        class myMethodDelegator implements MethodAnnotationIteratorDelegate {
            MethodIdItem method;
            AnnotationSetItem methodAnnotations;

            public AnnotationSetItem getMethodAnnotations() {
                return methodAnnotations;
            }
            public void setMethodAnnotations(AnnotationSetItem methodAnnotations) {
                this.methodAnnotations = methodAnnotations;
            }

            public myMethodDelegator(MethodIdItem method) {
                this.method = method;
            }
            public void processMethodAnnotations(MethodIdItem method,
                                                 AnnotationSetItem methodAnnotations) {
                if(method.equals(this.method)) {
                    setMethodAnnotations(methodAnnotations);
                }

            }
        }

        // the following snippet retrieves all exceptions that this class throws by analyzing the class annotation
        thrownExceptions = new ArrayList<String>();
        myMethodDelegator delegate = new myMethodDelegator(method.method);
        if(dexClass.annotations!=null) {
            dexClass.annotations.iterateMethodAnnotations(delegate);
            AnnotationSetItem  annotations = delegate.getMethodAnnotations();
            if(annotations!=null) {
                for(AnnotationItem annotationItem : annotations.getAnnotations()) {
                    AnnotationEncodedSubValue value = annotationItem.getEncodedAnnotation();
                    for(EncodedValue encodedValue : value.values) {
                        if(encodedValue instanceof ArrayEncodedValue) {
                            for(EncodedValue encodedValueSub : ((ArrayEncodedValue) encodedValue).values) {
                                if(encodedValueSub instanceof TypeEncodedValue) {
                                    TypeEncodedValue valueType = (TypeEncodedValue) encodedValueSub;
                                    thrownExceptions.add(valueType.value.getTypeDescriptor());
                                }
                            }
                        }

                    }
                }

            }
        }

        // retrieve all parameter types
        if (method.method.getPrototype().getParameters() != null) {
            List<TypeIdItem> parameters = method.method.getPrototype().getParameters().getTypes();

            for(TypeIdItem t : parameters) {
                DexType type = new DexType(t);
                this.parameterTypes.add(type);
                dexClass.types.add(type);
            }
        }

        // retrieve the return type of this method
        returnType = new DexType(method.method.getPrototype().getReturnType());
        dexClass.types.add(this.returnType);

        // if the method is abstract, no code needs to be transformed
        if (method.codeItem == null || method.codeItem.getInstructions() == null)
            return;

        // retrieve all local types of the method
        DebugInfoItem debugInfo = method.codeItem.getDebugInfo();
        if(debugInfo!=null) {
			for(Item<?> item : debugInfo.getReferencedItems()) {
	            if (item instanceof TypeIdItem) {
	                DexType type = new DexType((TypeIdItem) item);
	                dexClass.types.add(type);
	            }
	
	        }
        }
        
        //add the body of this code item
        dexBody = new DexBody(dexFile, method.codeItem, (RefType) DexType.toSoot(dexClass.getType()));

        for (DexType t : dexBody.usedTypes())
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
    public DexType getReturnType() {
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
    public List<DexType> getParameterTypes() {
        return this.parameterTypes;
    }

    /**
     * Retrieve the SootMethod equivalent of this method
     * @return the SootMethod of this method
     */
    public SootMethod toSoot() {
        List<Type> parameters = new ArrayList<Type>();
        for(DexType t : parameterTypes) {
            parameters.add(t.toSoot());
        }
        List<SootClass> exceptions = new ArrayList<SootClass>();
        for (String exceptionName : thrownExceptions()) {
            String dottedName = Util.dottedClassName(exceptionName);
            exceptions.add(SootResolver.v().makeClassRef(dottedName));
        }

        //Build soot method by all available parameters
        SootMethod m = new SootMethod(name, parameters, returnType.toSoot(),
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
