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

import soot.tagkit.*;
import soot.util.*;
import java.util.*;
import java.io.*;
import soot.baf.toolkits.base.*;
import soot.jimple.toolkits.base.*;
import soot.*;
import soot.dava.*;
import soot.dava.toolkits.base.misc.*;
import soot.jimple.*;

/*
 * Incomplete and inefficient implementation.
 *
 * Implementation notes:
 *
 * 1. The getFieldOf() method is slow because it traverses the list of fields, comparing the names,
 * one by one.  If you establish a Dictionary of Name->Field, you will need to add a
 * notifyOfNameChange() method, and register fields which belong to classes, because the hashtable
 * will need to be updated.  I will do this later. - kor  16-Sep-97
 *
 * 2. Note 1 is kept for historical (i.e. amusement) reasons.  In fact, there is no longer a list of fields;
 * these are kept in a Chain now.  But that's ok; there is no longer a getFieldOf() method,
 * either.  There still is no efficient way to get a field by name, although one could establish
 * a Chain of EquivalentValue-like objects and do an O(1) search on that.  - plam 2-24-00
 */

/**
    Soot representation of a Java class.  They are usually created by a Scene,
    but can also be constructed manually through the given constructors.
*/
public class SootClass extends AbstractHost implements Numberable
{
    private static char fileSeparator = System.getProperty("file.separator").charAt(0);

    String name, shortName, fixedShortName, packageName, fixedPackageName;
    int modifiers;
    Chain fields = new HashChain();
    SmallNumberedMap subSigToMethods = new SmallNumberedMap( Scene.v().getSubSigNumberer() );
    Chain interfaces = new HashChain();

    boolean isInScene;
    SootClass superClass;

    boolean isPhantom;
    
    /**
        Constructs an empty SootClass with the given name and modifiers.
    */

    public SootClass(String name, int modifiers)
    {
	setName( name);
        this.modifiers = modifiers;
        refType = RefType.v(name);
        refType.setSootClass(this);
    }

    /**
        Constructs an empty SootClass with the given name and no modifiers.
    */

    public SootClass(String name)
    {
	this( name, 0);
    }

    /**
        Returns true if this class is being managed by a Scene. 
        A class may be unmanaged while it is being constructed.
    */

    public boolean isInScene()
    {
        return isInScene;
    }

    /** Tells this class if it is being managed by a Scene. */
    public void setInScene(boolean isInScene)
    {
        this.isInScene = isInScene;
    }

    /**
        Returns the number of fields in this class.
    */

    public int getFieldCount()
    {
        return fields.size();
    }

    /**
     * Returns a backed Chain of fields.
     */

    public Chain getFields()
    {
        return fields;
    }

    /*
    public void setFields(Field[] fields)
    {
        this.fields = new ArraySet(fields);
    }
    */

    /**
        Adds the given field to this class.
    */

    public void addField(SootField f) 
    {
        if(f.isDeclared())
            throw new RuntimeException("already declared: "+f.getName());

	if(declaresField(f.getName()))
            throw new RuntimeException("Field already exists : "+f.getName());
 
        fields.add(f);
        
        f.isDeclared = true;
        f.declaringClass = this;
        
    }

    /**
        Removes the given field from this class.
    */

    public void removeField(SootField f) 
    {
        if(!f.isDeclared() || f.getDeclaringClass() != this)
            throw new RuntimeException("did not declare: "+f.getName());

        fields.remove(f);
        f.isDeclared = false;
    }

    /**
        Returns the field of this class with the given name and type. 
    */

    private SootField findFieldInClass( String name, Type type ) {
        for( Iterator fieldIt = this.getFields().iterator(); fieldIt.hasNext(); ) {
            final SootField field = (SootField) fieldIt.next();
            if(field.name.equals(name) && field.type.equals(type))
                return field;
        }
        return null;
    }
    /**
        Returns the field of this class with the given name and type. 
    */

    public SootField getField(String name, Type type) 
    {
        SootField ret = null;
        ret = findFieldInClass( name, type );
        if( ret != null ) return ret;

        if(Scene.v().allowsPhantomRefs() && this.isPhantom())
        {
            SootField f = new SootField(name, type);
            f.setPhantom(true);
            addField(f);
            return f;
        } else {
            LinkedList queue = new LinkedList();
            queue.addAll( this.getInterfaces() );
            while( !queue.isEmpty() ) {
                SootClass iface = (SootClass) queue.removeFirst();
                ret = iface.findFieldInClass( name, type );
                if( ret != null ) return ret;
                queue.addAll( iface.getInterfaces() );
            }
            if( this.hasSuperclass() ) 
                return this.getSuperclass().getField( name, type );
            throw new RuntimeException("No field " + name + " in class " + getName());
        }
    }

    
    /**
        Returns the field of this class with the given name.  Throws a RuntimeException if there
        are more than one.
    */

    public SootField getFieldByName(String name)
    {
        boolean found = false;
        SootField foundField = null;

        Iterator fieldIt = getFields().iterator();

        while(fieldIt.hasNext())
        {
            SootField field = (SootField) fieldIt.next();

            if(field.name.equals(name))
            {
                if(found)
                    throw new RuntimeException("ambiguous field: "+name);
                else {
                    found = true;
                    foundField = field;
                }
            }
        }

        if(found)
            return foundField;
        else
            throw new RuntimeException("No field " + name + " in class " + getName());
    }

    
    /*    
        Returns the field of this class with the given subsignature.
    */

    public SootField getField(String subsignature)
    {
        for( Iterator fieldIt = this.getFields().iterator(); fieldIt.hasNext(); ) {
            final SootField field = (SootField) fieldIt.next();
            if( field.getSubSignature().equals( subsignature ) ) return field;
        }

        throw new RuntimeException("No field " + subsignature + " in class " + getName());
    }

    
    /**
        Does this class declare a field with the given subsignature?
    */

    public boolean declaresField(String subsignature)
    {
        for( Iterator fieldIt = this.getFields().iterator(); fieldIt.hasNext(); ) {
            final SootField field = (SootField) fieldIt.next();
            if( field.getSubSignature().equals( subsignature ) ) return true;
        }
        return false;
    }

    
    /*    
        Returns the method of this class with the given subsignature.
    */

    public SootMethod getMethod(NumberedString subsignature)
    {
        SootMethod ret = (SootMethod) subSigToMethods.get( subsignature );
        if(ret == null)
            throw new RuntimeException("No method " + subsignature + " in class " + getName());
        else
            return ret;
    }

    /**
        Does this class declare a method with the given subsignature?
    */

    public boolean declaresMethod(NumberedString subsignature)
    {
        SootMethod ret = (SootMethod) subSigToMethods.get( subsignature );
        return ret != null;
    }
    
    
    /*    
        Returns the method of this class with the given subsignature.
    */

    public SootMethod getMethod(String subsignature)
    {
        return getMethod( Scene.v().getSubSigNumberer().findOrAdd( subsignature ) );
    }

    /**
        Does this class declare a method with the given subsignature?
    */

    public boolean declaresMethod(String subsignature)
    {
        return declaresMethod( Scene.v().getSubSigNumberer().findOrAdd( subsignature ) );
    }
    
    
    /**
        Does this class declare a field with the given name?
    */

    public boolean declaresFieldByName(String name)
    {
        Iterator fieldIt = getFields().iterator();

        while(fieldIt.hasNext())
        {
            SootField field = (SootField) fieldIt.next();

            if(field.name.equals(name))
                return true;
        }

        return false;
    }

    
    /**
        Does this class declare a field with the given name and type.
    */

    public boolean declaresField(String name, Type type)
    {
        Iterator fieldIt = getFields().iterator();

        while(fieldIt.hasNext())
        {
            SootField field = (SootField) fieldIt.next();

            if(field.name.equals(name) &&
                field.type.equals(type))
                return true;
        }

        return false;
    }

    /**
        Returns the number of methods in this class.
    */

    public int getMethodCount()
    {
        return subSigToMethods.nonNullSize();
    }

    /**
     * Returns an iterator over the methods in this class.
     */

    public Iterator methodIterator()
    {
        return subSigToMethods.iterator();
    }

    public List getMethods() {
        ArrayList ret = new ArrayList();
        for( Iterator it = methodIterator(); it.hasNext(); )
            ret.add( it.next() );
        return ret;
    }

    private SootMethod findMethodInClass( String name, List parameterTypes,
            Type returnType )
    {
        for( Iterator methodIt = methodIterator(); methodIt.hasNext(); ) {
            final SootMethod method = (SootMethod) methodIt.next();
            if(method.getName().equals(name) &&
                parameterTypes.equals(method.getParameterTypes()) &&
                returnType.equals(method.getReturnType()))
            {
                return method;
            }
        }
        return null;
    }
    /**
        Attempts to retrieve the method with the given name, parameters and return type.  
    */

    public SootMethod getMethod(String name, List parameterTypes, Type returnType) 
    {
        SootMethod ret = null;
        SootClass cl = this;
        while(true) {
            ret = cl.findMethodInClass( name, parameterTypes, returnType );
            if( ret != null ) return ret;
            if(Scene.v().allowsPhantomRefs() && cl.isPhantom())
            {
                SootMethod m = new SootMethod(name, parameterTypes, returnType);
                m.setPhantom(true);
                cl.addMethod(m);
                return m;
            }
            if( cl.hasSuperclass() ) cl = cl.getSuperclass();
            else break;
        }
        cl = this;
        while(true) {
            LinkedList queue = new LinkedList();
            queue.addAll( cl.getInterfaces() );
            while( !queue.isEmpty() ) {
                SootClass iface = (SootClass) queue.removeFirst();
                ret = iface.findMethodInClass( name, parameterTypes, returnType );
                if( ret != null ) return ret;
                queue.addAll( iface.getInterfaces() );
            }
            if( cl.hasSuperclass() ) cl = cl.getSuperclass();
            else break;
        }
        throw new RuntimeException(
                "Class "+getName()+" doesn't have method "+
            name + "(" + parameterTypes + ")" + " : " + returnType +
            "; failed to resolve in superclasses and interfaces" );
    }

    /**
        Attempts to retrieve the method with the given name and parameters.  This method
        may throw an AmbiguousMethodException if there is more than one method with the
        given name and parameter.
    */

    public SootMethod getMethod(String name, List parameterTypes) 
    {
        boolean found = false;
        SootMethod foundMethod = null;
        
        Iterator methodIt = methodIterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                parameterTypes.equals(method.getParameterTypes()))
            {
                if(found)
                    throw new RuntimeException("ambiguous method");
                else {                    
                    found = true;
                    foundMethod = method;
                }
            }
        }

        if(found)
            return foundMethod;
        else
            throw new RuntimeException("couldn't find method");
    }

    
     /**
        Attempts to retrieve the method with the given name.  This method
        may throw an AmbiguousMethodException if there are more than one method with the
        given name.
    */

    public SootMethod getMethodByName(String name) 
    {
        boolean found = false;
        SootMethod foundMethod = null;
        
        Iterator methodIt = methodIterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name))
            {
                if(found)
                    throw new RuntimeException("ambiguous method");
                else {                    
                    found = true;
                    foundMethod = method;
                }
            }
        }

        if(found)
            return foundMethod;
        else
            throw new RuntimeException("couldn't find method");
    }

    /**
        Does this class declare a method with the given name and parameter types?
    */

    public boolean declaresMethod(String name, List parameterTypes)
    {
        Iterator methodIt = methodIterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                method.getParameterTypes().equals(parameterTypes))
                return true;
        }
        
        return false;
    }

    /**
        Does this class declare a method with the given name, parameter types, and return type?
    */

    public boolean declaresMethod(String name, List parameterTypes, Type returnType)
    {
        Iterator methodIt = methodIterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                method.getParameterTypes().equals(parameterTypes) &&
                method.getReturnType().equals(returnType))
                
                return true;
        }
        
        return false;
    }

    /**
        Does this class declare a method with the given name?
    */

    public boolean declaresMethodByName(String name)
    {
        Iterator methodIt = methodIterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name))
                return true;
        }
        
        return false;
    }

    /*
    public void setMethods(Method[] method)
    {
        methods = new ArraySet(method);
    }
    */

    /**
        Adds the given method to this class.
    */

    public void addMethod(SootMethod m) 
    {
        if(m.isDeclared())
            throw new RuntimeException("already declared: "+m.getName());

        /*
        if(declaresMethod(m.getName(), m.getParameterTypes()))
            throw new RuntimeException("duplicate signature for: " + m.getName());
        */
        
        subSigToMethods.put(m.getNumberedSubSignature(),m);
        m.isDeclared = true;
        m.declaringClass = this;
        
    }

    /**
        Removes the given method from this class.
    */

    public void removeMethod(SootMethod m) 
    {
        if(!m.isDeclared() || m.getDeclaringClass() != this)
            throw new RuntimeException("incorrect declarer for remove: "+m.getName());

        subSigToMethods.put(m.getNumberedSubSignature(),null);
        m.isDeclared = false;
    }

    /**
        Returns the modifiers of this class.
    */

    public int getModifiers()
    {
        return modifiers;
    }

    /**
        Sets the modifiers for this class.
    */

    public void setModifiers(int modifiers)
    {
        this.modifiers = modifiers;
    }

    /**
        Returns the number of interfaces being directly implemented by this class.  Note that direct
        implementation corresponds to an "implements" keyword in the Java class file and that this class may
        still be implementing additional interfaces in the usual sense by being a subclass of a class
        which directly implements some interfaces.
    */

    public int getInterfaceCount()
    {
        return interfaces.size();
    }

    /**
     * Returns a backed Chain of the interfaces that are directly implemented by this class. (see getInterfaceCount())
     */

    public Chain getInterfaces()
    {
        return interfaces;
    }

    /**
        Does this class directly implement the given interface? (see getInterfaceCount())
    */

    public boolean implementsInterface(String name)
    {
        Iterator interfaceIt = getInterfaces().iterator();

        while(interfaceIt.hasNext())
        {
            SootClass SootClass = (SootClass) interfaceIt.next();

            if(SootClass.getName().equals(name))
                return true;
        }

        return false;
    }

    /**
        Add the given class to the list of interfaces which are directly implemented by this class.
    */

    public void addInterface(SootClass interfaceClass)
    {
        if(implementsInterface(interfaceClass.getName()))
            throw new RuntimeException("duplicate interface: "+interfaceClass.getName());
        interfaces.add(interfaceClass);
    }

    /**
        Removes the given class from the list of interfaces which are direclty implemented by this class.
    */

    public void removeInterface(SootClass interfaceClass) 
    {
        if(!implementsInterface(interfaceClass.getName()))
            throw new RuntimeException("no such interface: "+interfaceClass.getName());

        interfaces.remove(interfaceClass);
    }

    /**
	WARNING: interfaces are subclasses of the java.lang.Object class!
        Does this class have a superclass? False implies that this is
        the java.lang.Object class.  Note that interfaces are
        subclasses of the java.lang.Object class.  */

    
    public boolean hasSuperclass()
    {
        return superClass != null;
    }

    /**
	WARNING: interfaces are subclasses of the java.lang.Object class!
        Returns the superclass of this class. (see hasSuperclass())
    */

    public SootClass getSuperclass() 
    {
        if(superClass == null) 
            throw new RuntimeException("no superclass");
        else
            return superClass;
    }

    /**
        Sets the superclass of this class.  Note that passing a null will cause the class to have no superclass.
    */

    public void setSuperclass(SootClass c)
    {
        superClass = c;
    }

    /**
        Returns the name of this class.
    */

    public String getName()
    {
	if (Main.getJavaStyle())
	    return getJavaStyleName();
	else
	    return name;
    }

    public String getFullName()
    {
	return name;
    }

    public String getJavaStyleName()
    {
	if (PackageNamer.v().has_FixedNames()) {
	    if (fixedShortName == null)
		fixedShortName = PackageNamer.v().get_FixedClassName( name);

	    if (PackageNamer.v().use_ShortName( getJavaPackageName(), fixedShortName) == false)
		return getJavaPackageName() + "." + fixedShortName;

	    return fixedShortName;
	}

	return shortName;
    }

    public String getShortJavaStyleName()
    {
	if (PackageNamer.v().has_FixedNames()) {
	    if (fixedShortName == null)
		fixedShortName = PackageNamer.v().get_FixedClassName( name);

	    return fixedShortName;
	}

	return shortName;
    }

    /**
        Returns the package name of this class.
    */

    public String getPackageName()
    {
	if (Main.getJavaStyle())
	    return getJavaPackageName();

	return packageName;
    }

    public String getJavaPackageName()
    {
	if (PackageNamer.v().has_FixedNames()) {
	    if (fixedPackageName == null)
		fixedPackageName = PackageNamer.v().get_FixedPackageName( packageName);
	    
	    return fixedPackageName;
	}

	return packageName;
    }

    /**
        Sets the name of this class.
    */

    public void setName(String name)
    {
        this.name = name;
	
	shortName = name;
	packageName = "";

	int index = name.lastIndexOf( '.');
	if (index > 0) {
	    shortName = name.substring( index + 1);
	    packageName = name.substring( 0, index);
	}

	fixedShortName = null;
	fixedPackageName = null;
    }

    /** Convenience method; returns true if this class is an interface. */
    public boolean isInterface()
    {
        return Modifier.isInterface(this.getModifiers());
    }

    /** Convenience method; returns true if this class is public. */
    public boolean isPublic()
    {
        return Modifier.isPublic(this.getModifiers());
    }

    /** Prints this SootClass to the given PrintWriter, including active bodies of methods. */
    public void printTo(PrintWriter out)
    {
        printTo(out, 0);
    }
	
    public void printXMLTo(PrintWriter out)
    {
        XMLPrinter xmlOut = new XMLPrinter();
	XMLNode xmlRootNode = null;
	XMLNode xmlHistoryNode = null;
        XMLNode xmlClassNode = null;
        XMLNode xmlTempNode = null;

        // Print XML class output
        {
	    // add header nodes
	    xmlRootNode = xmlOut.root.addElement("jil");

	    // add history node
	    // TODO: grab the software version and command line
	    String cmdlineStr = "";
	    for( int i = 0; i < Main.getCmdLineArgs().length; i++ )
	    {
		cmdlineStr += Main.getCmdLineArgs()[ i ] + " ";
	    }
	    String dateStr = new Date().toString();
	    xmlHistoryNode = xmlRootNode.addChild("history");
	    xmlHistoryNode.addAttribute("created", dateStr );
	    xmlHistoryNode.addChild("soot",new String[] {"version", "command", "timestamp"},new String[] {Main.versionString, cmdlineStr.trim(), dateStr});
            
	    // add class root node
            xmlClassNode = xmlRootNode.addChild("class",new String[] {"name"},new String[] {Scene.v().quotedNameOf(this.getName()).toString()});
            if(this.getPackageName().length()>0)
                xmlClassNode.addAttribute("package",this.getPackageName());
            if(this.hasSuperclass())
                xmlClassNode.addAttribute("extends",Scene.v().quotedNameOf(this.getSuperclass().getName()).toString());

            // add modifiers subnode
            xmlTempNode = xmlClassNode.addChild("modifiers");
            StringTokenizer st = new StringTokenizer(Modifier.toString(this.getModifiers()));
            while(st.hasMoreTokens())
		xmlTempNode.addChild("modifier",new String[] {"name"},new String[] {st.nextToken()+""});
            xmlTempNode.addAttribute("count",xmlTempNode.getNumberOfChildren()+"");		
        } 

        // Print interfaces
        {
            xmlTempNode = xmlClassNode.addChild("interfaces","",new String[] {"count"},new String[] {this.getInterfaceCount()+""});

	    Iterator interfaceIt = this.getInterfaces().iterator();
            if(interfaceIt.hasNext())
            {
                while(interfaceIt.hasNext())
		    xmlTempNode.addChild("implements","",new String[] {"class"},new String[] {Scene.v().quotedNameOf(((SootClass) interfaceIt.next()).getName()).toString()});
            }
        }
        
        // Print fields
        {   
	    xmlTempNode = xmlClassNode.addChild( "fields", "", new String[] { "count" }, new String[] { this.getFieldCount()+"" } );
                
	    Iterator fieldIt = this.getFields().iterator();
            if(fieldIt.hasNext())
            {
                int i = 0;
                while(fieldIt.hasNext())
                {
                    SootField f = (SootField) fieldIt.next();
                    
                    if(f.isPhantom())
                        continue;
                    
                    String type = f.getType().toString();
                    String name = f.getName().toString();
                    String decl = f.getDeclaration();
							
                    // add the field node
                    XMLNode xmlFieldNode = xmlTempNode.addChild( "field", "", new String[] { "id", "name", "type" }, new String[] { (i++)+"", name, type } );
                    XMLNode xmlModifiersNode = xmlFieldNode.addChild( "modifiers" );
							
                    StringTokenizer st = new StringTokenizer(Modifier.toString(f.getModifiers()));
                    while(st.hasMoreTokens())
                        xmlModifiersNode.addChild( "modifier", new String[] {"name"},new String[]{st.nextToken()+""} );
			
                    xmlModifiersNode.addAttribute( "count", xmlModifiersNode.getNumberOfChildren()+"" );
                }
            }
        }

        // Print methods
        {
            Iterator methodIt = this.methodIterator();

            if( Scene.v().getJimpleStmtPrinter() instanceof XMLStmtPrinter )
            {
                XMLStmtPrinter xmlStmtPrinter = ( XMLStmtPrinter )Scene.v().getJimpleStmtPrinter();
                xmlStmtPrinter.setXMLNode( xmlClassNode.addChild( "methods", new String[] { "count" }, new String[] { this.getMethodCount()+"" } ) );
            }

            while(methodIt.hasNext())
            {
                SootMethod method = (SootMethod) methodIt.next();

                if(method.isPhantom())
		        continue;
					    
                if(!Modifier.isAbstract(method.getModifiers()) && 
		   !Modifier.isNative(method.getModifiers()))
                {
                    if(!method.hasActiveBody())
                        throw new RuntimeException("method " + method.getName() + " has no active body!");
                    else
                        method.getActiveBody().printTo(out, PrintJimpleBodyOption.XML_OUTPUT);
                }
            }
        }
        out.println(xmlOut.toString());
    }
  

    public void printJimpleStyleTo(PrintWriter out, int printBodyOptions)
    {
        // write this class as XML
        boolean xmlOutput = PrintJimpleBodyOption.xmlOutput(printBodyOptions);
        if(xmlOutput)
        {
            this.printXMLTo(out);
            return;
        }

       // Print class name + modifiers
        {
            StringTokenizer st = new StringTokenizer(Modifier.toString(this.getModifiers()));
            while(st.hasMoreTokens())
                out.print(st.nextToken() + " ");

            String classPrefix = "";

            if(!isInterface())
             {
                 classPrefix = classPrefix + " class";
                 classPrefix = classPrefix.trim();
             }

            out.print(classPrefix + " " + Scene.v().quotedNameOf(this.getName()) + "");
        }

        // Print extension
        {
            if(this.hasSuperclass())
                out.print(" extends " + Scene.v().quotedNameOf(this.getSuperclass().getName()) + "");
        }

        // Print interfaces
        {
            Iterator interfaceIt = this.getInterfaces().iterator();
            
            if(interfaceIt.hasNext())
            {
                out.print(" implements ");
                    
                out.print("" + Scene.v().quotedNameOf(((SootClass) interfaceIt.next()).getName()) + "");
                
                while(interfaceIt.hasNext())
                {
                    out.print(",");
                    out.print(" " + Scene.v().quotedNameOf(((SootClass) interfaceIt.next()).getName()) + "");
                }
            }
        }
        
        out.println();
        out.println("{");
        
        // Print fields
        {
            Iterator fieldIt = this.getFields().iterator();
            
            if(fieldIt.hasNext())
            {
                while(fieldIt.hasNext())
                {
                    SootField f = (SootField) fieldIt.next();
                    
                    if(f.isPhantom())
                        continue;
                    
                    out.println("    " + f.getDeclaration() + ";");
                }
            }
        }
        
        // Print methods
        {
            Iterator methodIt = this.methodIterator();

            if(methodIt.hasNext())
            {
                if(getMethodCount() != 0)
                    out.println();
                
                while(methodIt.hasNext())
                {
                    SootMethod method = (SootMethod) methodIt.next();

                    if(method.isPhantom())
                        continue;
                    
                    if(!Modifier.isAbstract(method.getModifiers()) &&
                       !Modifier.isNative(method.getModifiers()))
                    {
                        if(!method.hasActiveBody())
                            throw new RuntimeException("method " + method.getName() + " has no active body!");
                        else
                            method.getActiveBody().printTo(out, printBodyOptions);

                        if(methodIt.hasNext())
                            out.println();
                    }
                    else 
                    {
                        out.print("    ");
                        out.print(method.getDeclaration());
                        out.println(";");
                        
                        if(methodIt.hasNext())
                            out.println();
                    }
                }
            }
        }
        out.println("}");
    }
    
    public void printTo( PrintWriter out, int printBodyOptions)
    {
	// Optionally print the package info for Dava files.
	if (Main.getJavaStyle()) {

	    String curPackage = getJavaPackageName();

	    if (curPackage.equals( "") == false) {
		out.println( "package " + curPackage + ";");
		out.println();
	    }

	    IterableSet packagesUsed = new IterableSet();

	    if (this.hasSuperclass()) {
		SootClass superClass = this.getSuperclass();
		packagesUsed.add( superClass.getJavaPackageName());
	    }
	    
	    Iterator interfaceIt = this.getInterfaces().iterator();
	    while (interfaceIt.hasNext()) {
		String interfacePackage = ((SootClass) interfaceIt.next()).getJavaPackageName();
		if (packagesUsed.contains( interfacePackage) == false)
		    packagesUsed.add( interfacePackage);
	    }

	    Iterator methodIt = this.methodIterator();
	    while (methodIt.hasNext()) {
		SootMethod dm = (SootMethod) methodIt.next();
		
		if (dm.hasActiveBody())
		    packagesUsed = packagesUsed.union( ((DavaBody) dm.getActiveBody()).get_PackagesUsed());
		    
		Iterator eit = dm.getExceptions().iterator();
		while (eit.hasNext()) {
		    String thrownPackage = ((SootClass) eit.next()).getJavaPackageName();
		    if (packagesUsed.contains( thrownPackage) == false)
			packagesUsed.add( thrownPackage);
		}

		Iterator pit = dm.getParameterTypes().iterator();
		while (pit.hasNext()) {
		    Type t = (Type) pit.next();

		    if (t instanceof RefType) {
			String paramPackage = ((RefType) t).getSootClass().getJavaPackageName();
			if (packagesUsed.contains( paramPackage) == false)
			    packagesUsed.add( paramPackage);
		    }
		}

		Type t = dm.getReturnType();
		if (t instanceof RefType) {
		    String returnPackage = ((RefType) t).getSootClass().getJavaPackageName();
		    if (packagesUsed.contains( returnPackage) == false)
			packagesUsed.add( returnPackage);
		}
	    }
	    
	    Iterator fieldIt = this.getFields().iterator();
	    while (fieldIt.hasNext()) {
		SootField f = (SootField) fieldIt.next();

		if (f.isPhantom())
		    continue;

		Type t = f.getType();

		if (t instanceof RefType) {
		    String fieldPackage = ((RefType) t).getSootClass().getJavaPackageName();
		    if (packagesUsed.contains( fieldPackage) == false)
			packagesUsed.add( fieldPackage);
		}
	    }


	    if (packagesUsed.contains( curPackage))
		packagesUsed.remove( curPackage);

	    if (packagesUsed.contains( "java.lang"))
		packagesUsed.remove( "java.lang");

	    Iterator pit = packagesUsed.iterator();
	    while (pit.hasNext())
		out.println( "import " + (String) pit.next() + ".*;");

	    if (packagesUsed.isEmpty() == false)
		out.println();

	    packagesUsed.add( "java.lang");
	    packagesUsed.add( curPackage);

	    Dava.v().set_CurrentPackageContext( packagesUsed);
	    Dava.v().set_CurrentPackage( curPackage);
	}


        // Print class name + modifiers
        {
            String classPrefix = "";
            
            classPrefix = classPrefix + " " + Modifier.toString(this.getModifiers());
            classPrefix = classPrefix.trim();

            if(!isInterface())
            {
                classPrefix = classPrefix + " class";
                classPrefix = classPrefix.trim();
            }

	    if (Main.getJavaStyle())
		out.print(classPrefix + " " + this.getShortJavaStyleName());
	    else 
		out.print(classPrefix + " " + this.getName());
        }

        // Print extension
	if ((hasSuperclass()) && 
	    ((Main.getJavaStyle() == false) || (getSuperclass().getFullName().equals( "java.lang.Object") == false)))
	    out.print(" extends " + this.getSuperclass().getName() + "");

        // Print interfaces
        {
            Iterator interfaceIt = this.getInterfaces().iterator();
            
            if(interfaceIt.hasNext())
            {
                out.print(" implements ");
                
                out.print("" + ((SootClass) interfaceIt.next()).getName() + "");
                
                while(interfaceIt.hasNext())
                    out.print(", " + ((SootClass) interfaceIt.next()).getName() + "");
            }
        }
        
        out.println();
        out.println("{");
        
        // Print fields
        {
            Iterator fieldIt = this.getFields().iterator();
            
            if(fieldIt.hasNext())
            {
                while(fieldIt.hasNext())
                {
                    SootField f = (SootField) fieldIt.next();
                    
                    if(f.isPhantom())
                        continue;
                        
                    out.println("    " + f.getDeclaration() + ";");
                }
            }
        }

        // Print methods
        {
            Iterator methodIt = this.methodIterator();
            
            if(methodIt.hasNext())
            {
                if(getMethodCount() != 0)
                    out.println();
                
                while(methodIt.hasNext())
                {
                    SootMethod method = (SootMethod) methodIt.next();
                    
                    if(method.isPhantom())
                        continue;
                    
                    if(!Modifier.isAbstract(method.getModifiers()) &&
                       !Modifier.isNative(method.getModifiers()))
                    {
                        if(!method.hasActiveBody())
                            throw new RuntimeException("method " + method.getName() + " has no active body!");
                        else
                            method.getActiveBody().printTo(out, printBodyOptions);
                            
                        if(methodIt.hasNext())
                            out.println();
                    }
                    else 
                    {
                        out.print("    ");
                        out.print(method.getDeclaration());
                        out.println(";");
                        
                        if(methodIt.hasNext())
                            out.println();
                    }
                }
            }
        }
        out.println("}");
    }

    /**
        Writes the class out to a file.
     */
    public void write()
    {
        write("");
    }

    /** Returns true if some method in this class has an active Baf body. */
    public boolean containsBafBody()
    {
        Iterator methodIt = methodIterator();
        
        while(methodIt.hasNext())
        {
            SootMethod m = (SootMethod) methodIt.next();
            
            if(m.hasActiveBody() && 
                m.getActiveBody() instanceof soot.baf.BafBody)
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
        Writes the class out to a file.
     */
    public void write(String outputDir)
    {
        String outputDirWithSep = "";
            
        if(!outputDir.equals(""))
            outputDirWithSep = outputDir + fileSeparator;
            
        try {
            File tempFile = new File(outputDirWithSep + this.getName() + ".jasmin");
 
            FileOutputStream streamOut = new FileOutputStream(tempFile);

            PrintWriter writerOut = new PrintWriter(new EscapedWriter(new OutputStreamWriter(streamOut)));

            if(containsBafBody())
                new soot.baf.JasminClass(this).print(writerOut);
            else
                new soot.jimple.JasminClass(this).print(writerOut);

            writerOut.close();

            if(soot.Main.opts.time())
                soot.Main.assembleJasminTimer.start(); 

            // Invoke jasmin
            {
                String[] args;
                
                if(outputDir.equals(""))
                {
                    args = new String[1];
                    
                    args[0] = this.getName() + ".jasmin";
                }
                else
                {
                    args = new String[3];
                    
                    args[0] = "-d";
                    args[1] = outputDir;
                    args[2] = outputDirWithSep + this.getName() + ".jasmin";
                }
                
                jasmin.Main.main(args);
            }
            
            tempFile.delete();
            
            if(soot.Main.opts.time())
                soot.Main.assembleJasminTimer.end(); 
            
        } catch(IOException e)
        {
            throw new RuntimeException("Could not produce new classfile! (" + e + ")");
        }        
    }

    private RefType refType;
    void setRefType( RefType refType ) { this.refType = refType; }
    public boolean hasRefType() { return refType != null; }
    
    /** Returns the RefType corresponding to this class. */
    public RefType getType()
    {
        return refType;
    }

    /** Returns the name of this class. */
    public String toString()
    {
        return getName();
    }

    /* Renames private fields and methods with numeric names. */
    public void renameFieldsAndMethods(boolean privateOnly)
    {
        // Rename fields.  Ignore collisions for now.
        {
            Iterator fieldIt = this.getFields().iterator();
            int fieldCount = 0;

            if(fieldIt.hasNext())
            {
                while(fieldIt.hasNext())
                  {
                      SootField f = (SootField)fieldIt.next();
                      if (!privateOnly || Modifier.isPrivate(f.getModifiers()))
                        {
                          String newFieldName = "__field"+(fieldCount++);
                          f.setName(newFieldName);
                        }
                  }
            }
        }

        // Rename methods.  Again, ignore collisions for now.
        {
            Iterator methodIt = this.methodIterator();
            int methodCount = 0;

            if(methodIt.hasNext())
            {
                while(methodIt.hasNext())
                  {
                      SootMethod m = (SootMethod)methodIt.next();
                      if (!privateOnly || Modifier.isPrivate(m.getModifiers()))
                        {
                          String newMethodName = "__method"+(methodCount++);
                          m.setName(newMethodName);
                        }
                  }
            }
        }
    }

    /** Convenience method returning true if this class is an application class. 
     *
     * @see Scene#getApplicationClasses() */
    public boolean isApplicationClass()
    {
        return Scene.v().getApplicationClasses().contains(this);
    }

    /** Makes this class an application class. */
    public void setApplicationClass()
    {
        Chain c = Scene.v().getContainingChain(this);
        if (c != null)
            c.remove(this);
        Scene.v().getApplicationClasses().add(this);

        isPhantom = false;
    }

    /** Convenience method returning true if this class is a library class.
     *
     * @see Scene#getLibraryClasses() */
    public boolean isLibraryClass()
    {
        return Scene.v().getLibraryClasses().contains(this);
    }

    /** Makes this class a library class. */
    public void setLibraryClass()
    {
        Chain c = Scene.v().getContainingChain(this);
        if (c != null)
            c.remove(this);
        Scene.v().getLibraryClasses().add(this);

        isPhantom = false;
    }

    /** Convenience method returning true if this class is a context class.
     *
     * @see Scene#getContextClasses() */
    public boolean isContextClass()
    {
        return Scene.v().getContextClasses().contains(this);
    }

    /** Makes this class a context class. */
    public void setContextClass()
    {
        Chain c = Scene.v().getContainingChain(this);
        if (c != null)
            c.remove(this);
        Scene.v().getContextClasses().add(this);

        isPhantom = false;
    }

    /** Convenience method returning true if this class is a phantom class.
     *
     * @see Scene#getPhantomClasses() */
    public boolean isPhantomClass()
    {
        return Scene.v().getPhantomClasses().contains(this);
    }

    /** Makes this class a phantom class. */
    public void setPhantomClass()
    {
        Chain c = Scene.v().getContainingChain(this);
        if (c != null)
            c.remove(this);
        Scene.v().getPhantomClasses().add(this);
        isPhantom = true;
    }
    
    /** Convenience method returning true if this class is phantom. */
    public boolean isPhantom()
    {
        return isPhantom;
    }
    
    /** Marks this class as phantom, without notifying the Scene. */
    public void setPhantom(boolean value)
    {
        if (value == false)
            if (isPhantom)
                throw new RuntimeException("don't know how to de-phantomize this class");
            else
                return;
        
        setPhantomClass();
    }
    /**
     * Convenience method returning true if this class is private.
     */
    public boolean isPrivate()
    {
        return Modifier.isPrivate(this.getModifiers());
    }

    /**
     * Convenience method returning true if this class is protected.
     */
    public boolean isProtected()
    {
        return Modifier.isProtected(this.getModifiers());
    }

    /**
     * Convenience method returning true if this class is abstract.
     */
    public boolean isAbstract()
    {
        return Modifier.isAbstract(this.getModifiers());
    }

    public final int getNumber() { return number; }
    public final void setNumber( int number ) { this.number = number; }

    private int number = 0;
}

