/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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
import soot.*;

import soot.tagkit.*;
import soot.util.*;
import java.util.*;
import soot.baf.*;
import soot.jimple.*;
import soot.dava.*;
import java.io.*;
import java.util.jar.*;

/**
    Soot representation of a Java method.  Can be declared to belong to a SootClass. 
    Does not contain the actual code, which belongs to a Body.
    The getActiveBody() method points to the currently-active body.
*/
public class SootMethod extends AbstractHost implements ClassMember, Numberable
{
    public static final String constructorName = "<init>";
    public static final String staticInitializerName = "<clinit>";

    /** Name of the current method. */
    String name;

    /** A list of parameter types taken by this <code>SootMethod</code> object, 
      * in declaration order. */
    List parameterTypes;

    /** The return type of this object. */
    Type returnType;

    /** True when some <code>SootClass</code> object declares this <code>SootMethod</code> object. */
    boolean isDeclared;

    /** Holds the class which declares this <code>SootClass</code> method. */
    SootClass declaringClass;

    /** Modifiers associated with this SootMethod (e.g. private, protected, etc.) */
    int modifiers;

    /** Is this method a phantom method? */
    boolean isPhantom = false;
    
    /** Declared exceptions thrown by this method.  Created upon demand. */
    List exceptions = null;

    /** Active body associated with this method. */
    Body activeBody;

    /** Tells this method how to find out where its body lives. */
    protected MethodSource ms;

    /** Uses methodSource to retrieve the method body in question; does not set it
     * to be the active body.
     *
     * @param phaseName       Phase name for body loading. */
    public Body getBodyFromMethodSource(String phaseName)
    {
        return ms.getBody(this, phaseName);
    }

    /** Sets the MethodSource of the current SootMethod. */
    public void setSource(MethodSource ms)
    {        
        this.ms = ms;
    }

    /** Returns the MethodSource of the current SootMethod. */
    public MethodSource getSource() 
    {
        return ms;
    }

    /** Returns a hash code for this method consistent with structural equality. */
    public int equivHashCode()
    {
        return returnType.hashCode() * 101 + modifiers * 17 + name.hashCode();
    }

    /** Constructs a SootMethod with the given name, parameter types and return type. */
    public SootMethod(String name, List parameterTypes, Type returnType)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);
        this.returnType = returnType;
        Scene.v().getMethodNumberer().add( this );
        subsignature = Scene.v().getSubSigNumberer().findOrAdd( getSubSignature() );
    }

    /** Constructs a SootMethod with the given name, parameter types, return type and modifiers. */
    public SootMethod(String name, List parameterTypes, Type returnType, int modifiers)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);

        this.returnType = returnType;
        this.modifiers = modifiers;        
        Scene.v().getMethodNumberer().add( this );
        subsignature = Scene.v().getSubSigNumberer().findOrAdd( getSubSignature() );
    }

    /** Constructs a SootMethod with the given name, parameter types, return type, 
      * and list of thrown exceptions. */
    public SootMethod(String name, List parameterTypes, Type returnType, int modifiers,
                      List thrownExceptions)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);

        this.returnType = returnType;
        this.modifiers = modifiers;

        if (exceptions == null && !thrownExceptions.isEmpty())
        {
            exceptions = new ArrayList();
            this.exceptions.addAll(thrownExceptions);
        }
        Scene.v().getMethodNumberer().add( this );
        subsignature = Scene.v().getSubSigNumberer().findOrAdd( getSubSignature() );
    }   

    /** Returns the name of this method. */
    public String getName()
    {
        return name;
    }

    /** Returns the class which declares the current <code>SootMethod</code>. */
    public SootClass getDeclaringClass() 
    {
        if(!isDeclared)
            throw new RuntimeException("not declared: "+getName());

        return declaringClass;
    }

    public void setDeclared( boolean isDeclared)
    {
	this.isDeclared = isDeclared;
    }

    /** Returns true when some <code>SootClass</code> object declares this <code>SootMethod</code> object. */    
    public boolean isDeclared()
    {
        return isDeclared;
    }

    /** Returns true when this <code>SootMethod</code> object is phantom. */
    public boolean isPhantom()
    {
        return isPhantom;
    }
    
    /**
     *  Returns true if this method is not phantom, abstract or native, i.e. this method can have a body.
     */
     
    public boolean isConcrete()
    {
        if ((declaringClass != null) && (declaringClass.isContextClass()))
	    return false;

        return !isPhantom() && !isAbstract() && !isNative();
    }

    /** Sets the phantom flag on this method. */
    public void setPhantom(boolean value)
    {
        if( value ) {
            if( !Scene.v().allowsPhantomRefs() ) 
                throw new RuntimeException( "Phantom refs not allowed" );
            if( declaringClass != null && !declaringClass.isPhantom() )
                throw new 
                    RuntimeException( "Declaring class would have to be phantom" );
        }
        isPhantom = value;
    }

    /** Sets the name of this method. */
    public void setName(String name)
    {
        this.name = name;
    }

    /** Gets the modifiers of this method.
     * @see soot.Modifier */
    public int getModifiers()
    {
        return modifiers;
    }

    /** Sets the modifiers of this method.
     * @see soot.Modifier */
    public void setModifiers(int modifiers)
    {
        if ((declaringClass != null) && (!declaringClass.isApplicationClass()))
            throw new RuntimeException("Cannot set modifiers of a method from a non-app class!");
        this.modifiers = modifiers;
    }

    /** Returns the return type of this method. */
    public Type getReturnType()
    {
        return returnType;
    }

    /** Sets the return type of this method. */
    public void setReturnType(Type t)
    {
        returnType = t;
    }

    /** Returns the number of parameters taken by this method. */
    public int getParameterCount()
    {
        return parameterTypes.size();
    }

    /** Gets the type of the <i>n</i>th parameter of this method. */
    public Type getParameterType(int n)
    {
        return (Type) parameterTypes.get(n);
    }

    /**
     * Returns a backed list of the parameter types of this method.
     */
    public List getParameterTypes()
    {
        return parameterTypes;
    }

    /**
        Retrieves the active body for this method.
     */
    public Body getActiveBody() 
    {
        if (declaringClass.isContextClass())
            throw new RuntimeException("cannot get active body for context class: " + getSignature());
        if (declaringClass.isPhantomClass())
            throw new RuntimeException("cannot get active body for phantom class: " + getSignature());

        if(!hasActiveBody())
            throw new RuntimeException("no active body present for method " + getSignature());
            
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
        if (declaringClass.isContextClass())
            throw new RuntimeException(
                "cannot get resident body for context class : "
                    + getSignature()
                    + "; maybe you want to call c.setApplicationClass() on this class!");
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
        

    private static String cachePathName = null;

    private String classFileAttr = null;

    private String getCacheFileAttr()
    {
	if (classFileAttr == null) {

	    StringBuffer b = new StringBuffer();
	    char fileSep = System.getProperty( "file.separator").charAt( 0);
	    String className = getDeclaringClass().getFullName().replace( '.', fileSep) + ".class";

	    StringTokenizer st = new StringTokenizer( System.getProperty( "java.class.path"), System.getProperty( "path.separator"));
	    while (st.hasMoreTokens()) {
		String classPath = st.nextToken();

		if (classPath.length() == 0)
		    continue;

		File p = new File( classPath);

		if (p.exists() == false)
		    continue;

		if (p.isDirectory()) {
		    if (classPath.charAt( classPath.length() - 1) != fileSep)
			classPath += fileSep;

		    File f = new File( classPath + className);
		    if (f.exists()) {

			b.append( " ");
			b.append( Long.toString( f.length()));
			b.append( "-");
			b.append( Long.toString( f.lastModified()));

			break;
		    }
		}

		else {
		    JarFile jf = null;

		    try {
			jf = new JarFile( classPath);
		    }
		    catch( IOException ioe) {
			continue;
		    }

		    if (jf.getEntry( className) != null) {
			
			b.append( " ");
			b.append( Long.toString( p.length()));
			b.append( "-");
			b.append( Long.toString( p.lastModified()));

			break;
		    }
		}
	    }

	    if (b.length() == 0)
		throw new RuntimeException( "Unable to generate cache filename for: " + getSignature());

	    classFileAttr = b.toString();
	}

	return classFileAttr;
    }


    /**
        Sets the active body for this method. 
     */
    public void setActiveBody(Body body)
    {
        if ((declaringClass != null) && (declaringClass.isContextClass() || declaringClass.isPhantomClass()))
            throw new RuntimeException("cannot set active body for context or phantom class!");

        if(!isConcrete())
            throw new RuntimeException("cannot set body for non-concrete method!");
            
        if (body.getMethod() != this)
            body.setMethod(this);

        activeBody = body;
    }

    /** Returns true if this method has an active body. */
    public boolean hasActiveBody()
    {
        return activeBody != null;
    }
    
    /** Releases the active body associated with this method. */
    public void releaseActiveBody()
    {
        activeBody = null;
    }

    /** Adds the given exception to the list of exceptions thrown by this method. */
    public void addException(SootClass e) 
    {
        if (exceptions == null)
            exceptions = new ArrayList();
        else if (exceptions.contains(e))
            throw new RuntimeException("already throws exception "+e.getName());

        exceptions.add(e);
    }

    /** Removes the given exception from the list of exceptions thrown by this method. */
    public void removeException(SootClass e) 
    {
        if (exceptions == null)
            exceptions = new ArrayList();

        if (!exceptions.contains(e))
            throw new RuntimeException("does not throw exception "+e.getName());

        exceptions.remove(e);
    }

    /** Returns true if this method throws exception <code>e</code>. */
    public boolean throwsException(SootClass e)
    {
        return exceptions != null && exceptions.contains(e);
    }

    
    public void setExceptions( List exceptions)
    {
	this.exceptions = new ArrayList();
	this.exceptions.addAll( exceptions);
    }

    /**
     * Returns a backed list of the exceptions thrown by this method.
     */

    public List getExceptions()
    {
        if (exceptions == null)
            exceptions = new ArrayList();

        return exceptions;
    }

    /** Sets the list of parameter types for this method as given. 
     * This method makes a copy of the given list. */
    public void setParameterTypes(List parameterTypes)
    {
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);
    }

    /**
     * Convenience method returning true if this method is static.
     */
    public boolean isStatic()
    {
        return Modifier.isStatic(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is private.
     */
    public boolean isPrivate()
    {
        return Modifier.isPrivate(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is public.
     */
    public boolean isPublic()
    {
        return Modifier.isPublic(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is protected.
     */
    public boolean isProtected()
    {
        return Modifier.isProtected(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is abstract.
     */
    public boolean isAbstract()
    {
        return Modifier.isAbstract(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is native.
     */
    public boolean isNative()
    {
        return Modifier.isNative(this.getModifiers());
    }

    /**
     * Convenience method returning true if this method is synchronized.
     */
    public boolean isSynchronized()
    {
        return Modifier.isSynchronized(this.getModifiers());
    }
    
    /**
        Returns the signature of this method in the format in which it appears
        in bytecode (eg. [Ljava/lang/Object instead of java.lang.Object[]).
     */
    public String getBytecodeSignature()
    {
        String name = getName();
        List params =  getParameterTypes();
        Type returnType = getReturnType();
        
        StringBuffer buffer = new StringBuffer();
        buffer.append("<" + Scene.v().quotedNameOf(getDeclaringClass().getName()) + ": ");
        buffer.append(name);
        buffer.append(soot.jimple.JasminClass.jasminDescriptorOf(this));
        buffer.append(">");
        
        // Again, memory-usage tweak depending on JDK implementation due
        // to Michael Pan.
        return new String(buffer.toString());
    }

    /**
        Returns the Soot signature of this method.  Used to refer to methods unambiguously.
     */
    public String getSignature()
    {
        String name = getName();
        List params =  getParameterTypes();
        Type returnType = getReturnType();
        
        StringBuffer buffer = new StringBuffer();
        buffer.append("<" + Scene.v().quotedNameOf(getDeclaringClass().getName()) + ": ");
        buffer.append(getSubSignatureImpl(name, params, returnType));
        buffer.append(">");
        
        // Again, memory-usage tweak depending on JDK implementation due
        // to Michael Pan.
        return new String(buffer.toString());
    }

    /**
        Returns the Soot subsignature of this method.  Used to refer to methods unambiguously.
     */
    public String getSubSignature()
    {
        String name = getName();
        List params =  getParameterTypes();
        Type returnType = getReturnType();

        return getSubSignatureImpl(name, params, returnType);
    }

    public static String getSubSignature(String name, List params, Type returnType)
    {
        return getSubSignatureImpl(name, params, returnType); 
    }
    
    private static String getSubSignatureImpl(String name, List params, Type returnType) 
    {
        StringBuffer buffer = new StringBuffer();
        Type t = returnType;

        buffer.append(t.toString() + " " + Scene.v().quotedNameOf(name) + "(");
        
        Iterator typeIt = params.iterator();

        if(typeIt.hasNext())
        {
            t = (Type) typeIt.next();
            
	    buffer.append(t);
            
            while(typeIt.hasNext())
            {
                buffer.append(",");
                
                t = (Type) typeIt.next();
                buffer.append(t);
            }
        }
        buffer.append(")");
        
        return buffer.toString();
    }

    private NumberedString subsignature;
    public NumberedString getNumberedSubSignature() {
        return subsignature;
    }

    /** Returns the signature of this method. */
    public String toString()
    {
        return getSignature();
    }

    /**
     * Returns the declaration of this method, as used at the top of textual body representations 
     *  (before the {}'s containing the code for representation.)
     */
    public String getDeclaration()
    {
	if ((Main.getJavaStyle()) && (getName().equals( staticInitializerName)))
	    return "static";

        StringBuffer buffer = new StringBuffer();

        // modifiers
        StringTokenizer st = new StringTokenizer(Modifier.toString(this.getModifiers()));
        if (st.hasMoreTokens())
            buffer.append(st.nextToken());

        while(st.hasMoreTokens())
            buffer.append(" " + st.nextToken());

        if(buffer.length() != 0)
            buffer.append(" ");

        // return type + name

	if ((Main.getJavaStyle()) && (getName().equals( constructorName)))
	    buffer.append( getDeclaringClass().getShortJavaStyleName());
	else {
	    Type t = this.getReturnType();

	    buffer.append(t + " ");
	    buffer.append(Scene.v().quotedNameOf(this.getName()));
	}

	buffer.append("(");

        // parameters
        Iterator typeIt = this.getParameterTypes().iterator();
	int count = 0;
	while (typeIt.hasNext()) {
	    Type t = (Type) typeIt.next();
	    
	    buffer.append( t);
	    buffer.append( " ");

	    if (Main.getJavaStyle()) {
		if (hasActiveBody()) 
		    buffer.append( ((DavaBody) getActiveBody()).get_ParamMap().get( new Integer( count++)));
		else {
		    if (t ==BooleanType.v())
			buffer.append( "z" + count++);
		    else if (t == ByteType.v())
			buffer.append( "b" + count++);
		    else if (t == ShortType.v())
			buffer.append( "s" + count++);
		    else if (t == CharType.v())
			buffer.append( "c" + count++);
		    else if (t == IntType.v())
			buffer.append( "i" + count++);
		    else if (t == LongType.v())
			buffer.append( "l" + count++);
		    else if (t == DoubleType.v())
			buffer.append( "d" + count++);
		    else if (t == FloatType.v())
			buffer.append( "f" + count++);
		    else if (t == StmtAddressType.v())
			buffer.append( "a" + count++);
		    else if (t == ErroneousType.v())
			buffer.append( "e" + count++);
		    else if (t == NullType.v())
			buffer.append( "n" + count++);
		    else 
			buffer.append( "r" + count++);
		}
	    }

	    if (typeIt.hasNext())
		buffer.append( ", ");

	}

        buffer.append(")");

        // Print exceptions
        if (exceptions != null)
        {
            Iterator exceptionIt = this.getExceptions().iterator();
            
            if(exceptionIt.hasNext())
            {
                buffer.append(" throws "+((SootClass) exceptionIt.next()).getName());

                while(exceptionIt.hasNext())
                {
                    buffer.append(", " + ((SootClass) exceptionIt.next()).getName());
                }
            }
        }

        return buffer.toString();
    }
    public final int getNumber() { return number; }
    public final void setNumber( int number ) { this.number = number; }
    private int number = 0;
}
