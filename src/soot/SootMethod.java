/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 * Copyright (C) 2004 Ondrej Lhotak
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import soot.dava.DavaBody;
import soot.dava.toolkits.base.renamer.RemoveFullyQualifiedName;
import soot.jimple.toolkits.callgraph.VirtualCalls;
import soot.tagkit.AbstractHost;
import soot.util.IterableSet;
import soot.util.Numberable;
import soot.util.NumberedString;

/**
    Soot representation of a Java method.  Can be declared to belong to a SootClass. 
    Does not contain the actual code, which belongs to a Body.
    The getActiveBody() method points to the currently-active body.
*/
public class SootMethod 
    extends AbstractHost
    implements ClassMember, Numberable, MethodOrMethodContext {
    public static final String constructorName = "<init>";
    public static final String staticInitializerName = "<clinit>";
    public static boolean DEBUG=false;
    /** Name of the current method. */
    private String name;

    /** A list of parameter types taken by this <code>SootMethod</code> object, 
      * in declaration order. */
    private List<Type> parameterTypes;

    /** The return type of this object. */
    private Type returnType;

    /** True when some <code>SootClass</code> object declares this <code>SootMethod</code> object. */
    private boolean isDeclared;

    /** Holds the class which declares this <code>SootClass</code> method. */
    private SootClass declaringClass;

    /** Modifiers associated with this SootMethod (e.g. private, protected, etc.) */
    private int modifiers;

    /** Is this method a phantom method? */
    private boolean isPhantom = false;

    /** Declared exceptions thrown by this method.  Created upon demand. */
    private List<SootClass> exceptions = null;

    /** Active body associated with this method. */
    private Body activeBody;

    /** Tells this method how to find out where its body lives. */
    protected MethodSource ms;

    /** Uses methodSource to retrieve the method body in question; does not set it
     * to be the active body.
     *
     * @param phaseName       Phase name for body loading. */
    private Body getBodyFromMethodSource(String phaseName) {
        return ms.getBody(this, phaseName);
    }

    /** Sets the MethodSource of the current SootMethod. */
    public void setSource(MethodSource ms) {
        this.ms = ms;
    }

    /** Returns the MethodSource of the current SootMethod. */
    public MethodSource getSource() {
        return ms;
    }

    /** Returns a hash code for this method consistent with structural equality. */
    public int equivHashCode() {
        return returnType.hashCode() * 101 + modifiers * 17 + name.hashCode();
    }

    /** Constructs a SootMethod with the given name, parameter types and return type. */
    public SootMethod(String name, List<Type> parameterTypes, Type returnType) {
        this(name, parameterTypes, returnType, 0, Collections.<SootClass>emptyList());
    }

    /** Constructs a SootMethod with the given name, parameter types, return type and modifiers. */
    public SootMethod(
        String name,
        List<Type> parameterTypes,
        Type returnType,
        int modifiers) {
        this(name, parameterTypes, returnType, modifiers, Collections.<SootClass>emptyList());
    }

    /** Constructs a SootMethod with the given name, parameter types, return type, 
      * and list of thrown exceptions. */
    public SootMethod(
        String name,
        List<Type> parameterTypes,
        Type returnType,
        int modifiers,
        List<SootClass> thrownExceptions) {
        this.name = name;
        this.parameterTypes = new ArrayList<Type>();
        this.parameterTypes.addAll(parameterTypes);
        this.parameterTypes = Collections.unmodifiableList(this.parameterTypes);

        this.returnType = returnType;
        this.modifiers = modifiers;

        if (exceptions == null && !thrownExceptions.isEmpty()) {
            exceptions = new ArrayList<SootClass>();
            this.exceptions.addAll(thrownExceptions);
            /*DEBUG=true;
            if(DEBUG)
            	System.out.println("Added thrown exceptions"+thrownExceptions);
            DEBUG=false;
            */
        }
        Scene.v().getMethodNumberer().add(this);
        subsignature =
            Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
        
        
    }

    /** Returns the name of this method. */
    public String getName() {
        return name;
    }

    /** Nomair A. Naeem , January 14th 2006
     * Need it for the decompiler to create a new SootMethod
     * The SootMethod can be created fine but when one tries to create a SootMethodRef there is an error because
     * there is no declaring class set. Dava cannot add the method to the class until after it has ended decompiling
     * the remaining method (new method added is added in the PackManager)
     * It would make sense to setDeclared to true within this method too. However later when the sootMethod is added it checks
     * that the method is not set to declared (isDeclared).
     */
    public void setDeclaringClass(SootClass declClass){
	if(declClass != null){
	    declaringClass=declClass;
	    //setDeclared(true);
	}
    }
    
    /** Returns the class which declares the current <code>SootMethod</code>. */
    public SootClass getDeclaringClass() {
        if (!isDeclared)
            throw new RuntimeException("not declared: " + getName());

        return declaringClass;
    }

    public void setDeclared(boolean isDeclared) {
        this.isDeclared = isDeclared;
    }

    /** Returns true when some <code>SootClass</code> object declares this <code>SootMethod</code> object. */
    public boolean isDeclared() {
        return isDeclared;
    }

    /** Returns true when this <code>SootMethod</code> object is phantom. */
    @Override
    public boolean isPhantom() {
        return isPhantom;
    }

    /**
     *  Returns true if this method is not phantom, abstract or native, i.e. this method can have a body.
     */

    public boolean isConcrete() {
        return !isPhantom() && !isAbstract() && !isNative();
    }

    /** Sets the phantom flag on this method. */
    @Override
    public void setPhantom(boolean value) {
        if (value) {
            if (!Scene.v().allowsPhantomRefs())
                throw new RuntimeException("Phantom refs not allowed");
            if (declaringClass != null && !declaringClass.isPhantom())
                throw new RuntimeException("Declaring class would have to be phantom");
        }
        isPhantom = value;
    }

    /** Sets the name of this method. */
    public void setName(String name) {
        boolean wasDeclared = isDeclared;
        SootClass oldDeclaringClass = declaringClass;
        if( wasDeclared ) oldDeclaringClass.removeMethod(this);
        this.name = name;
        subsignature =
            Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
        if( wasDeclared) oldDeclaringClass.addMethod(this);
    }

    /** Gets the modifiers of this method.
     * @see soot.Modifier */
    public int getModifiers() {
        return modifiers;
    }

    /** Sets the modifiers of this method.
     * @see soot.Modifier */
    public void setModifiers(int modifiers) {
        if ((declaringClass != null) && (!declaringClass.isApplicationClass()))
            throw new RuntimeException("Cannot set modifiers of a method from a non-app class!");
        this.modifiers = modifiers;
    }

    /** Returns the return type of this method. */
    public Type getReturnType() {
        return returnType;
    }

    /** Sets the return type of this method. */
    public void setReturnType(Type t) {
        boolean wasDeclared = isDeclared;
        SootClass oldDeclaringClass = declaringClass;
        if( wasDeclared ) oldDeclaringClass.removeMethod(this);
        returnType = t;
        subsignature =
            Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
        if( wasDeclared) oldDeclaringClass.addMethod(this);
    }

    /** Returns the number of parameters taken by this method. */
    public int getParameterCount() {
        return parameterTypes.size();
    }

    /** Gets the type of the <i>n</i>th parameter of this method. */
    public Type getParameterType(int n) {
        return parameterTypes.get(n);
    }

    /**
     * Returns a read-only list of the parameter types of this method.
     */
    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Changes the set of parameter types of this method.
     */
    public void setParameterTypes( List<Type> l ) {
        boolean wasDeclared = isDeclared;
        SootClass oldDeclaringClass = declaringClass;
        if( wasDeclared ) oldDeclaringClass.removeMethod(this);
        this.parameterTypes = Collections.unmodifiableList(new ArrayList<Type>(l));
        subsignature =
            Scene.v().getSubSigNumberer().findOrAdd(getSubSignature());
        if( wasDeclared) oldDeclaringClass.addMethod(this);
    }

    /**
        Retrieves the active body for this method.
     */
    public Body getActiveBody() {
        if (declaringClass!=null && declaringClass.isPhantomClass())
            throw new RuntimeException(
                "cannot get active body for phantom class: " + getSignature());

		// ignore empty body exceptions if we are just computing coffi metrics
        if (!soot.jbco.Main.metrics && !hasActiveBody())
            throw new RuntimeException(
                "no active body present for method " + getSignature());

        return activeBody;
    }

    /**
     * Returns the active body if present, else constructs an active body and returns that.
     *
     * If you called Scene.v().loadClassAndSupport() for a class yourself, it will
     * not be an application class, so you cannot get retrieve its active body.
     * Please call setApplicationClass() on the relevant class.
     */

    public Body retrieveActiveBody() {
        declaringClass.checkLevel(SootClass.BODIES);
        if (declaringClass.isPhantomClass())
            throw new RuntimeException(
                "cannot get resident body for phantom class : "
                    + getSignature()
                    + "; maybe you want to call c.setApplicationClass() on this class!");

        if (!hasActiveBody()) {
            //	    G.v().out.println("Retrieving "+this.getSignature());

            setActiveBody(this.getBodyFromMethodSource("jb"));
            ms = null;
        }
        return getActiveBody();
    }

    /**
        Sets the active body for this method. 
     */
    public void setActiveBody(Body body) {
        if ((declaringClass != null)
            && declaringClass.isPhantomClass())
            throw new RuntimeException(
                "cannot set active body for phantom class! " + this);

        if (!isConcrete())
            throw new RuntimeException(
                "cannot set body for non-concrete method! " + this);

        if (body!= null && body.getMethod() != this)
            body.setMethod(this);

        activeBody = body;
    }

    /** Returns true if this method has an active body. */
    public boolean hasActiveBody() {
        return activeBody != null;
    }

    /** Releases the active body associated with this method. */
    public void releaseActiveBody() {
        activeBody = null;
    }

    /** Adds the given exception to the list of exceptions thrown by this method
     * unless the exception is already in the list. */
    public void addExceptionIfAbsent(SootClass e) {
        if( !throwsException(e) ) addException(e);
    }
    /** Adds the given exception to the list of exceptions thrown by this method. */
    public void addException(SootClass e) {
    	if(DEBUG)
    		System.out.println("Adding exception "+e);
    	
        if (exceptions == null)
            exceptions = new ArrayList<SootClass>();
        else if (exceptions.contains(e))
            throw new RuntimeException(
                "already throws exception " + e.getName());

        exceptions.add(e);
    }

    /** Removes the given exception from the list of exceptions thrown by this method. */
    public void removeException(SootClass e) {
    	if(DEBUG)
    		System.out.println("Removing exception "+e);

        if (exceptions == null)
            exceptions = new ArrayList<SootClass>();

        if (!exceptions.contains(e))
            throw new RuntimeException(
                "does not throw exception " + e.getName());

        exceptions.remove(e);
    }

    /** Returns true if this method throws exception <code>e</code>. */
    public boolean throwsException(SootClass e) {
        return exceptions != null && exceptions.contains(e);
    }

    public void setExceptions(List<SootClass> exceptions) {
    	if (exceptions != null && !exceptions.isEmpty()) {
	        this.exceptions = new ArrayList<SootClass>(exceptions);
	    }
    	else
    		this.exceptions = null;
    }

    /**
     * Returns a backed list of the exceptions thrown by this method.
     */

    public List<SootClass> getExceptions() {
        if (exceptions == null)
            exceptions = new ArrayList<SootClass>();

        return exceptions;
    }

    /**
     * Convenience method returning true if this method is static.
     */
    public boolean isStatic() {
        return Modifier.isStatic(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is private.
     */
    public boolean isPrivate() {
        return Modifier.isPrivate(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is public.
     */
    public boolean isPublic() {
        return Modifier.isPublic(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is protected.
     */
    public boolean isProtected() {
        return Modifier.isProtected(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is abstract.
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(this.getModifiers());
    }
    
    /**
     * Convenience method returning true if this method is final.
     */
    public boolean isFinal() {
        return Modifier.isFinal(this.getModifiers());
    }
 
    /**
     * Convenience method returning true if this method is native.
     */
    public boolean isNative() {
        return Modifier.isNative(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is synchronized.
     */
    public boolean isSynchronized() {
        return Modifier.isSynchronized(this.getModifiers());
    }
    
    /**
	 * 
	 * @return yes if this is the main method
	 */
	public boolean isMain()
	{
		if ( isPublic() && isStatic() ) {
			NumberedString main_sig = Scene.v().getSubSigNumberer().findOrAdd( "void main(java.lang.String[])" );
			if ( main_sig.equals( subsignature ) )
				return true;
		}
		
		return false;
	}

	/**
	 * 
	 * @return yes, if this function is a constructor.
	 */
	public boolean isConstructor()
	{
		return name.equals(constructorName);
	}
	
	/**
	 * @return yes, if this is a class initializer or main function.
	 */
	public boolean isEntryMethod()
	{
		if ( isStatic() &&
				subsignature.equals( VirtualCalls.v().sigClinit ) )
			return true;
		
		return isMain();
	}
	
	/**
	 * We rely on the JDK class recognition to decide if a method is JDK method.
	 */
	public boolean isJavaLibraryMethod()
	{
		SootClass cl = getDeclaringClass();
		return cl.isJavaLibraryClass();
	}
    
    /** Returns the parameters part of the signature in the format in which
     * it appears in bytecode. */
    public String getBytecodeParms() {
        StringBuffer buffer = new StringBuffer();
        for( Iterator<Type> typeIt = getParameterTypes().iterator(); typeIt.hasNext(); ) {
            final Type type = typeIt.next();
            buffer.append(AbstractJasminClass.jasminDescriptorOf(type));
        }
        return buffer.toString().intern();
    }

    /**
        Returns the signature of this method in the format in which it appears
        in bytecode (eg. [Ljava/lang/Object instead of java.lang.Object[]).
     */
    public String getBytecodeSignature() {
        String name = getName();

        StringBuffer buffer = new StringBuffer();
        buffer.append(
            "<" + Scene.v().quotedNameOf(getDeclaringClass().getName()) + ": ");
        buffer.append(name);
        buffer.append(AbstractJasminClass.jasminDescriptorOf(makeRef()));
        buffer.append(">");

        return buffer.toString().intern();
    }

    /**
        Returns the Soot signature of this method.  Used to refer to methods unambiguously.
     */
    public String getSignature() {
        return getSignature(getDeclaringClass(), getName(), getParameterTypes(), getReturnType());
    }
    
    public static String getSignature(SootClass cl, String name, List<Type> params, Type returnType) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(
            "<" + Scene.v().quotedNameOf(cl.getName()) + ": ");
        buffer.append(getSubSignatureImpl(name, params, returnType));
        buffer.append(">");

        // Again, memory-usage tweak depending on JDK implementation due
        // to Michael Pan.
        return buffer.toString().intern();
    }

    /**
        Returns the Soot subsignature of this method.  Used to refer to methods unambiguously.
     */
    public String getSubSignature() {
        String name = getName();
        List<Type> params = getParameterTypes();
        Type returnType = getReturnType();

        return getSubSignatureImpl(name, params, returnType);
    }

    public static String getSubSignature(
        String name,
        List<Type> params,
        Type returnType) {
        return getSubSignatureImpl(name, params, returnType);
    }

    private static String getSubSignatureImpl(
        String name,
        List<Type> params,
        Type returnType) {
        StringBuffer buffer = new StringBuffer();
        Type t = returnType;

        buffer.append(t.toString() + " " + Scene.v().quotedNameOf(name) + "(");

        Iterator<Type> typeIt = params.iterator();

        if (typeIt.hasNext()) {
            t = (Type) typeIt.next();

            buffer.append(t);

            while (typeIt.hasNext()) {
                buffer.append(",");

                t = (Type) typeIt.next();
                buffer.append(t);
            }
        }
        buffer.append(")");

        return buffer.toString().intern();
    }

    private NumberedString subsignature;
    public NumberedString getNumberedSubSignature() {
        return subsignature;
    }

    /** Returns the signature of this method. */
    public String toString() {
        return getSignature();
    }

    /*
     * TODO: Nomair A. Naeem .... 8th Feb 2006
     * This is really messy coding
     * So much for modularization!!
     * Should some day look into creating the DavaDeclaration from within
     * DavaBody
     */
    public String getDavaDeclaration() {
        if (getName().equals(staticInitializerName))
            return "static";

        StringBuffer buffer = new StringBuffer();

        // modifiers
        StringTokenizer st =
            new StringTokenizer(Modifier.toString(this.getModifiers()));
        if (st.hasMoreTokens())
            buffer.append(st.nextToken());

        while (st.hasMoreTokens())
            buffer.append(" " + st.nextToken());

        if (buffer.length() != 0)
            buffer.append(" ");

        // return type + name

        if (getName().equals(constructorName))
            buffer.append(getDeclaringClass().getShortJavaStyleName());
        else {
            Type t = this.getReturnType();

            String tempString = t.toString();    
            
            /*
             * Added code to handle RuntimeExcepotion thrown by getActiveBody
             */
            if(hasActiveBody()){
            	DavaBody body = (DavaBody) getActiveBody();
            	IterableSet importSet = body.getImportList();

            	if(!importSet.contains(tempString)){
            		body.addToImportList(tempString);
            	}
            	tempString = RemoveFullyQualifiedName.getReducedName(importSet,tempString,t);
            }
									
			buffer.append(tempString + " ");

            buffer.append(Scene.v().quotedNameOf(this.getName()));
        }

        buffer.append("(");

        // parameters
        Iterator<Type> typeIt = this.getParameterTypes().iterator();
        int count = 0;
        while (typeIt.hasNext()) {
            Type t = (Type) typeIt.next();
			String tempString = t.toString();
            
            /*
			 *  Nomair A. Naeem 7th Feb 2006
			 *  It is nice to remove the fully qualified type names
			 *  of parameters if the package they belong to have been imported
			 *  javax.swing.ImageIcon should be just ImageIcon if javax.swing is imported
			 *  If not imported WHY NOT..import it!! 
			 */
			if(hasActiveBody()){
				DavaBody body = (DavaBody) getActiveBody();
				IterableSet importSet = body.getImportList();

				if(!importSet.contains(tempString)){
					body.addToImportList(tempString);
				}
				tempString = RemoveFullyQualifiedName.getReducedName(importSet,tempString,t);
			}
									
			buffer.append(tempString + " ");
			
            buffer.append(" ");
            if (hasActiveBody()){
                buffer.append(((DavaBody) getActiveBody()).get_ParamMap().get(new Integer(count++)));
            }
            else {
                if (t == BooleanType.v())
                    buffer.append("z" + count++);
                else if (t == ByteType.v())
                    buffer.append("b" + count++);
                else if (t == ShortType.v())
                    buffer.append("s" + count++);
                else if (t == CharType.v())
                    buffer.append("c" + count++);
                else if (t == IntType.v())
                    buffer.append("i" + count++);
                else if (t == LongType.v())
                    buffer.append("l" + count++);
                else if (t == DoubleType.v())
                    buffer.append("d" + count++);
                else if (t == FloatType.v())
                    buffer.append("f" + count++);
                else if (t == StmtAddressType.v())
                    buffer.append("a" + count++);
                else if (t == ErroneousType.v())
                    buffer.append("e" + count++);
                else if (t == NullType.v())
                    buffer.append("n" + count++);
                else
                    buffer.append("r" + count++);
            }

            if (typeIt.hasNext())
                buffer.append(", ");

        }

        buffer.append(")");

        // Print exceptions
        if (exceptions != null) {
            Iterator<SootClass> exceptionIt = this.getExceptions().iterator();

            if (exceptionIt.hasNext()) {
                buffer.append(
                    " throws " + exceptionIt.next().getName());

                while (exceptionIt.hasNext()) {
                    buffer.append(
                        ", " + exceptionIt.next().getName());
                }
            }
        }

        return buffer.toString().intern();
    }

    /**
     * Returns the declaration of this method, as used at the top of textual body representations 
     *  (before the {}'s containing the code for representation.)
     */
    public String getDeclaration() {
        StringBuffer buffer = new StringBuffer();

        // modifiers
        StringTokenizer st =
            new StringTokenizer(Modifier.toString(this.getModifiers()));
        if (st.hasMoreTokens())
            buffer.append(st.nextToken());

        while (st.hasMoreTokens())
            buffer.append(" " + st.nextToken());

        if (buffer.length() != 0)
            buffer.append(" ");

        // return type + name

        buffer.append(this.getReturnType() + " ");
        buffer.append(Scene.v().quotedNameOf(this.getName()));

        buffer.append("(");

        // parameters
        Iterator<Type> typeIt = this.getParameterTypes().iterator();
        //int count = 0;
        while (typeIt.hasNext()) {
            Type t = (Type) typeIt.next();

            buffer.append(t);

            if (typeIt.hasNext())
                buffer.append(", ");

        }

        buffer.append(")");

        // Print exceptions
        if (exceptions != null) {
            Iterator<SootClass> exceptionIt = this.getExceptions().iterator();

            if (exceptionIt.hasNext()) {
                buffer.append(
                    " throws " + exceptionIt.next().getName());

                while (exceptionIt.hasNext()) {
                    buffer.append(
                        ", " + exceptionIt.next().getName());
                }
            }
        }

        return buffer.toString().intern();
    }
    public final int getNumber() {
        return number;
    }
    public final void setNumber(int number) {
        this.number = number;
    }
    private int number = 0;
    public SootMethod method() { return this; }
    public Context context() { return null; }
    public SootMethodRef makeRef() {
        return Scene.v().makeMethodRef( declaringClass, name, parameterTypes, returnType, isStatic() );
    }
    
    @Override
    public int getJavaSourceStartLineNumber() {
    	super.getJavaSourceStartLineNumber();
    	//search statements for first line number
    	if(line==-1 && hasActiveBody()) {
    		PatchingChain<Unit> unit = getActiveBody().getUnits();
    		for (Unit u : unit) {
    			int l = u.getJavaSourceStartLineNumber();
    			if(l>-1) {
    				//store l-1, as method header is usually one line before 1st statement
    				line = l-1;
    				break;
    			}
			}
    	} 
    	return line;
    }
   
}
