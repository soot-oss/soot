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

import soot.util.*;
import java.util.*;
import java.io.*;
import soot.baf.toolkits.base.*;
import soot.jimple.toolkits.base.*;
import soot.*;


/*
 * Incomplete and inefficient implementation.
 *
 * Implementation notes:
 *
 * 1. The getFieldOf() method is slow because it traverses the list of fields, comparing the names,
 * one by one.  If you establish a Dictionary of Name->Field, you will need to add a
 * notifyOfNameChange() method, and register fields which belong to classes, because the hashtable
 * will need to be updated.  I will do this later. - kor  16-Sep-97
 */

/**
    Represents a Java class.  They are usually created by a Scene,
    but can also be constructed manually through the given constructors.
*/
public class SootClass extends AbstractHost
{
    private static char fileSeparator = System.getProperty("file.separator").charAt(0);

    String name;
    int modifiers;
    Chain fields = new HashChain();
    Chain methods = new HashChain();
    Chain interfaces = new HashChain();

    Scene scene = Scene.v(); /// added -patrice 
    boolean isInScene;


    SootClass superClass;

    boolean isPhantom;
    
    /**
        Constructs an empty SootClass with the given name and modifiers.
    */

    public SootClass(String name, int modifiers)
    {
        this.name = name;
        this.modifiers = modifiers;

    }

    /**
        Constructs an empty SootClass with the given name and no modifiers.
    */

    public SootClass(String name)
    {
        this.name = name;
        this.modifiers = 0;
    }

    /**
        Is this class being managed by a Scene? A class may be unmanaged  while it is being constructed.
    */

    public boolean isInScene()
    {
        return isInScene;
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

    public void addField(SootField f) throws AlreadyDeclaredException 
    {
        if(f.isDeclared())
            throw new AlreadyDeclaredException(f.getName());

            /*
        if(declaresField(f.getName()))
            throw new DuplicateNameException(f.getName());
 */
 
        fields.add(f);
        
        f.isDeclared = true;
        f.declaringClass = this;
        
        scene.fieldSignatureToField.put(f.getSignature(), f);        
    }

    /**
        Removes the given field from this class.
    */

    public void removeField(SootField f) throws IncorrectDeclarerException
    {
        if(!f.isDeclared() || f.getDeclaringClass() != this)
            throw new IncorrectDeclarerException(f.getName());

        fields.remove(f);
        f.isDeclared = false;
    }

    /**
        Returns the field of this class with the given name and type. 
    */

    public SootField getField(String name, Type type) throws soot.NoSuchFieldException
    {
        Iterator fieldIt = getFields().iterator();

        while(fieldIt.hasNext())
        {
            SootField field = (SootField) fieldIt.next();

            if(field.name.equals(name) && field.type.equals(type))
                return field;
        }

        if(Scene.v().allowsPhantomRefs())
        {
            SootField f = new SootField(name, type);
            f.setPhantom(true);
            addField(f);
            return f;
        }
        else
            throw new soot.NoSuchFieldException("No field " + name + " in class " + getName());
    }

    
    /**
        Returns the field of this class with the given name.  May throw an AmbiguousFieldException if there
        are more than one.
    */

    public SootField getFieldByName(String name) throws soot.NoSuchFieldException, soot.AmbiguousFieldException
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
                    throw new AmbiguousFieldException();
                else {
                    found = true;
                    foundField = field;
                }
            }
        }

        if(found)
            return foundField;
        else
            throw new soot.NoSuchFieldException("No field " + name + " in class " + getName());
    }

    
    /*    
        Returns the field of this class with the given subsignature.
    */

    public SootField getField(String subsignature) throws soot.NoSuchFieldException
    {
        SootField toReturn = (SootField) scene.fieldSignatureToField.get("<" + getName() + ": " + subsignature + ">");
        
        if(toReturn == null)
            throw new soot.NoSuchFieldException("No field " + name + " in class " + getName());
        else
            return toReturn;
    }

    
    /**
        Does this class declare a field with the given subsignature?
    */

    public boolean declaresField(String subsignature)
    {
        return scene.fieldSignatureToField.containsKey("<" + getName() + ": " + subsignature + ">");
    }

    
    /*    
        Returns the method of this class with the given subsignature.
    */

    public SootMethod getMethod(String subsignature) throws soot.NoSuchMethodException
    {
        SootMethod toReturn = (SootMethod) scene.methodSignatureToMethod.get("<" + getName() + ": " + subsignature + ">");
        if(toReturn == null)
            throw new soot.NoSuchMethodException("No method " + subsignature + " in class " + getName());
        else
            return toReturn;
    }

    /**
        Does this class declare a method with the given subsignature?
    */

    public boolean declaresMethod(String subsignature)
    {
        return scene.methodSignatureToMethod.containsKey("<" + getName() + ": " + subsignature + ">");
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
        return methods.size();
    }

    /**
     * Returns a backed Chain of methods.
     */

    public Chain getMethods()
    {
        return methods;
    }

    
    /**
        Attempts to retrieve the method with the given name, parameters and return type.  
        
    */

    public SootMethod getMethod(String name, List parameterTypes, Type returnType) throws
        soot.NoSuchMethodException
    {

        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                parameterTypes.equals(method.getParameterTypes()) &&
                returnType.equals(method.getReturnType()))
            {
                return method;
            }
        }

        if(Scene.v().allowsPhantomRefs())
        {
            SootMethod m = new SootMethod(name, parameterTypes, returnType);
            m.setPhantom(true);
            this.addMethod(m);
            return m;
        }
        else
            throw new soot.NoSuchMethodException(getName() + "." + name + "(" + 
		     parameterTypes + ")" + " : " + returnType);
	
    }

    /**
        Attempts to retrieve the method with the given name and parameters.  This method
        may throw an AmbiguousMethodException if there are more than one method with the
        given name and parameter.
    */

    public SootMethod getMethod(String name, List parameterTypes) throws
        soot.NoSuchMethodException, soot.AmbiguousMethodException
    {
        boolean found = false;
        SootMethod foundMethod = null;
        
        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                parameterTypes.equals(method.getParameterTypes()))
            {
                if(found)
                    throw new soot.AmbiguousMethodException();
                else {                    
                    found = true;
                    foundMethod = method;
                }
            }
        }

        if(found)
            return foundMethod;
        else
            throw new soot.NoSuchMethodException();
    }

    
     /**
        Attempts to retrieve the method with the given name.  This method
        may throw an AmbiguousMethodException if there are more than one method with the
        given name.
    */

    public SootMethod getMethodByName(String name) throws
        soot.NoSuchMethodException, soot.AmbiguousMethodException
    {
        boolean found = false;
        SootMethod foundMethod = null;
        
        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name))
            {
                if(found)
                    throw new soot.AmbiguousMethodException();
                else {                    
                    found = true;
                    foundMethod = method;
                }
            }
        }

        if(found)
            return foundMethod;
        else
            throw new soot.NoSuchMethodException();
    }

    /**
        Does this class declare a method with the given name and parameter types?
    */

    public boolean declaresMethod(String name, List parameterTypes)
    {
        Iterator methodIt = getMethods().iterator();

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
        Iterator methodIt = getMethods().iterator();

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
        Iterator methodIt = getMethods().iterator();

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

    public void addMethod(SootMethod m) throws AlreadyDeclaredException
    {
        if(m.isDeclared())
            throw new AlreadyDeclaredException(m.getName());

        /*
        if(declaresMethod(m.getName(), m.getParameterTypes()))
            throw new DuplicateNameException("duplicate signature for: " + m.getName());
        */
        
        methods.add(m);
        m.isDeclared = true;
        m.declaringClass = this;
        
	if(scene == null) {
	    System.out.println("sootclass::addMethod   -- fix me"); //xxx
	    scene = Scene.v();
	}
        scene.methodSignatureToMethod.put(m.getSignature(), m);        
    }

    /**
        Removes the given method from this class.
    */

    public void removeMethod(SootMethod m) throws IncorrectDeclarerException
    {
        if(!m.isDeclared() || m.getDeclaringClass() != this)
            throw new IncorrectDeclarerException(m.getName());

        methods.remove(m);
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

    public void addInterface(SootClass interfaceClass) throws DuplicateNameException
    {
        if(implementsInterface(interfaceClass.getName()))
            throw new DuplicateNameException(interfaceClass.getName());

        interfaces.add(interfaceClass);
    }

    /**
        Removes the given class from the list of interfaces which are direclty implemented by this class.
    */

    public void removeInterface(SootClass interfaceClass) throws NoSuchInterfaceException
    {
        if(!implementsInterface(interfaceClass.getName()))
            throw new NoSuchInterfaceException(interfaceClass.getName());

        interfaces.remove(interfaceClass);
    }

    /*
    public void setInterfaces(SootClass[] interfaces)
    {
        this.interfaces = new ArraySet(interfaces);
    }
    */

    /**
        Does this class have a superclass? False implies that this is the java.lang.Object class.  Note that interfaces are subclasses
        of the java.lang.Object class.
    */

    
    public boolean hasSuperclass()
    {
        return superClass != null;
    }

    /**
        Returns the superclass of this class. (see hasSuperclass())
    */

    public SootClass getSuperclass() throws NoSuperclassException
    {
        if(superClass == null) 
            throw new NoSuperclassException();
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
        return name;
    }

    /**
        Returns the package name of this class.
    */

    public String getPackageName()
    {
        int index = getName().lastIndexOf(".");
        
        if(index == -1)
            return "";
        else
            return name.substring(0, index);
    }

    /**
        Sets the name of this class.
    */

    public void setName(String name) throws DuplicateNameException
    {
        this.name = name;
    }

    public boolean isInterface()
    {
        return Modifier.isInterface(this.getModifiers());
    }

    public boolean isPublic()
    {
        return Modifier.isPublic(this.getModifiers());
    }

    public void printTo(PrintWriter out)
    {
        printTo(out, 0);
    }

    

  public void printJimpleStyleTo(PrintWriter out, int printBodyOptions)
  {
    // Print class name + modifiers
    {

      StringTokenizer st = new StringTokenizer(Modifier.toString(this.getModifiers()));
      while(st.hasMoreTokens())
	out.print("." + st.nextToken() + " ");
	  
      String classPrefix = "";



      if(!isInterface())
	{
	  classPrefix = classPrefix + " .class";
	  classPrefix = classPrefix.trim();
	}

      out.print(classPrefix + " " + this.getName() + "");
    }

    // Print extension
    {
      if(this.hasSuperclass())
	out.print(" .extends " + this.getSuperclass().getName() + "");
    }

    // Print interfaces
    {
      Iterator interfaceIt = this.getInterfaces().iterator();

      if(interfaceIt.hasNext())
	{
	  out.print(" .implements ");

	  out.print("" + ((SootClass) interfaceIt.next()).getName() + "");

	  while(interfaceIt.hasNext())
	    {
	      out.print(",");
	      out.print(" " + ((SootClass) interfaceIt.next()).getName() + "");
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
      Iterator methodIt = this.getMethods().iterator();

      if(methodIt.hasNext())
	{
	  if(this.getMethods().size() != 0)
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
		  // ((soot.jimple.GrimpBody) method.getActiveBody()).printDebugTo(out);
			    
                            

		  if(methodIt.hasNext())
		    out.println();
		}
	      else {
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



  public void printTo(PrintWriter out, int printBodyOptions)
  {
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

      out.print(classPrefix + " " + this.getName() + "");
    }

    // Print extension
    {
      if(this.hasSuperclass())
	out.print(" extends " + this.getSuperclass().getName() + "");
    }

    // Print interfaces
    {
      Iterator interfaceIt = this.getInterfaces().iterator();

      if(interfaceIt.hasNext())
	{
	  out.print(" implements ");

	  out.print("" + ((SootClass) interfaceIt.next()).getName() + "");

	  while(interfaceIt.hasNext())
	    {
	      out.print(",");
	      out.print(" " + ((SootClass) interfaceIt.next()).getName() + "");
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
      Iterator methodIt = this.getMethods().iterator();

      if(methodIt.hasNext())
	{
	  if(this.getMethods().size() != 0)
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
	      else {
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

    boolean containsBafBody()
    {
        Iterator methodIt = getMethods().iterator();
        
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

            if(soot.Main.isProfilingOptimization)
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
                /*        
            Process p;
            
            if(outputDir.equals(""))
                p = Runtime.getRuntime().exec("java jasmin.Main " + this.getName() + ".jasmin");
            else 
                p = Runtime.getRuntime().exec("java jasmin.Main -d " + outputDir + " " + outputDirWithSep + this.getName() + ".jasmin");
            
            try {
                p.waitFor();
            } catch(InterruptedException e)
            {
            }
            */
            
            tempFile.delete();
            
            if(soot.Main.isProfilingOptimization)
                soot.Main.assembleJasminTimer.end(); 
            
        } catch(IOException e)
        {
            throw new RuntimeException("Could not produce new classfile! (" + e + ")");
        }        
    }

    public RefType getType()
    {
        return RefType.v(getName());
    }

    public String toString()
    {
        return getName();
    }

    // gives numeric names to private fields and methods.
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
            Iterator methodIt = this.getMethods().iterator();
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

    public boolean isApplicationClass()
    {
        return Scene.v().getApplicationClasses().contains(this);
    }

    public void setApplicationClass()
    {
        Chain c = Scene.v().getContainingChain(this);
        if (c != null)
            c.remove(this);
        Scene.v().getApplicationClasses().add(this);
    }

    public boolean isLibraryClass()
    {
        return Scene.v().getLibraryClasses().contains(this);
    }

    public void setLibraryClass()
    {
        Chain c = Scene.v().getContainingChain(this);
        if (c != null)
            c.remove(this);
        Scene.v().getLibraryClasses().add(this);
    }

    public boolean isContextClass()
    {
        return Scene.v().getContextClasses().contains(this);
    }

    public void setContextClass()
    {
        Chain c = Scene.v().getContainingChain(this);
        if (c != null)
            c.remove(this);
        Scene.v().getContextClasses().add(this);
    }

    public boolean isPhantomClass()
    {
        return Scene.v().getPhantomClasses().contains(this);
    }

    public void setPhantomClass()
    {
        Chain c = Scene.v().getContainingChain(this);
        if (c != null)
            c.remove(this);
        Scene.v().getPhantomClasses().add(this);
    }
    
    
    public boolean isPhantom()
    {
        return isPhantom();
    }
    
    public void setPhantom(boolean value)
    {
        isPhantom = value;
    }

    public String getXML()
    {
	return XMLManager.getXML(this);
    }


}
