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
import soot.jimple.toolkits.invoke.*;
import soot.jimple.toolkits.scalar.*;
import soot.toolkits.scalar.*;

public class Scene extends AbstractHost
{
    private static Scene constant = new Scene();

    public final int OUTPUT_JIMPLE = 1;
    private int outputMode = OUTPUT_JIMPLE;

    Chain classes = new HashChain();
    Chain applicationClasses = new HashChain();
    Chain libraryClasses = new HashChain();
    Chain contextClasses = new HashChain();
    Chain phantomClasses = new HashChain();
    
    Map nameToClass = new HashMap();
    Map methodSignatureToMethod = new HashMap();
    Map fieldSignatureToField = new HashMap();

    Map phaseToOptionMaps = new HashMap();

    Hierarchy activeHierarchy;
    InvokeGraph activeInvokeGraph;

    boolean allowsPhantomRefs = false;
    private boolean allowsLazyResolving = false;

    Map packNameToPack = new HashMap();
    SootClass mainClass;
    String sootClassPath = "<external-class-path>";

    private Vector classesToResolve = new Vector();

    public int getOutputMode()
    {
        return outputMode;
    }


    public void addClassToResolve(String c) 
    {
	classesToResolve.add(c);
    }

    public String getNextClassToResolve()
    {
	if(!classesToResolve.isEmpty())
	    return (String) classesToResolve.firstElement();
	else
	    return null;
    }

    public List getClassesToResolve()
    {
	return classesToResolve;
    }

    public static Scene v()
    {
        return constant;
    }
    
    public void setMainClass(SootClass m)
    {
        mainClass = m;
    }
    
    public SootClass getMainClass()
    {
        if(mainClass == null)
            throw new NullPointerException("There is no main class set!");
            
        return mainClass;
    }
    
    
    public void setSootClassPath(String p)
    {
        sootClassPath = p;
    }
    
    public String getSootClassPath()
    {
        return sootClassPath;
    }
    
    private Scene()
    {
        Pack p;

        packNameToPack.put("jtp", p = new Pack());

        packNameToPack.put("jop", p = new Pack());
        {
            p.add(new Transform("jop.cp",   CopyPropagator.v()));
            p.add(new Transform("jop.cpf",  ConstantPropagatorAndFolder.v()));
            p.add(new Transform("jop.cbf",  ConditionalBranchFolder.v()));
            p.add(new Transform("jop.dae",  DeadAssignmentEliminator.v()));
            p.add(new Transform("jop.uce1", UnreachableCodeEliminator.v()));
            p.add(new Transform("jop.ubf1", UnconditionalBranchFolder.v()));
            p.add(new Transform("jop.uce2", UnreachableCodeEliminator.v()));
            p.add(new Transform("jop.ubf2", UnconditionalBranchFolder.v()));

            p.add(new Transform("jop.ule",  UnusedLocalEliminator.v()));
        }

        packNameToPack.put("wjtp", p = new Pack());
        packNameToPack.put("wjop", p = new Pack());
        {
            p.add(new Transform("wjop.smb", StaticMethodBinder.v(), "disabled"));
            p.add(new Transform("wjop.si", StaticInliner.v()));
        }
        packNameToPack.put("bop", p = new Pack());
        packNameToPack.put("gop", p = new Pack());
    }

    public Pack getPack(String phaseName)
    {
        Pack p = (Pack)packNameToPack.get(phaseName);
        if (p == null)
            throw new RuntimeException("tried to get nonexistant pack "+phaseName);
        return p;
    }

    private int stateCount;
    public int getState() { return this.stateCount; }

    /** Returns the default options map associated with phaseName.
     * Note that this map is special and will not return 'null' 
     * if the option does not exist.  Instead, it returns the string "false". 
     * If a leading . is present in phaseName, strip it! */
    public Map getPhaseOptions(String phaseName)
    {
        if (phaseName.startsWith("."))
            phaseName = phaseName.substring(1);

        Map m = (Map)phaseToOptionMaps.get(phaseName);
        if (m == null)
        {
            HashMap newMap = new HashMap();
            phaseToOptionMaps.put(phaseName, newMap);
            return newMap;
        }
        return m;
    }

    public Map computePhaseOptions(String phaseName, String optionsString)
    {
        Map options = new HashMap();

        StringTokenizer tokenizer = new StringTokenizer(optionsString, " ");
        while(tokenizer.hasMoreElements()) 
        {
            String option = tokenizer.nextToken();
            int colonLoc = option.indexOf(':');
            String key = null, value = null;

            if (colonLoc == -1)
            {
                key = option;
                value = "true";
            }
            else 
            {
                key = option.substring(0, option.indexOf(':'));
                value = option.substring(option.indexOf(':')+1);
            }

            options.put(key, value);
        }

        Map oldOptions = Scene.v().getPhaseOptions(phaseName);
        Iterator optionKeysIt = oldOptions.keySet().iterator();
        while (optionKeysIt.hasNext())
        {
            String s = (String)optionKeysIt.next();
            
            options.put(s, oldOptions.get(s));
        }

        return options;
    }

    public void addClass(SootClass c) throws AlreadyManagedException, DuplicateNameException
    {
        if(c.isInScene())
            throw new AlreadyManagedException(c.getName());

        if(containsClass(c.getName()))
            throw new DuplicateNameException(c.getName());

        classes.add(c);

        nameToClass.put(c.getName(), c);
        c.isInScene = true;
        c.scene = this;
        this.stateCount++;
    }

    public void removeClass(SootClass c)
    {
        if(!c.isInScene())
            throw new RuntimeException();

        classes.remove(c);
        nameToClass.remove(c.getName());
        c.isInScene = false;
        this.stateCount++;
    }

    public boolean containsClass(String className)
    {
        return nameToClass.containsKey(className);
    }

    public boolean containsField(String fieldSignature)
    {
        return fieldSignatureToField.containsKey(fieldSignature);
    }
    
    public boolean containsMethod(String methodSignature)
    {
        return methodSignatureToMethod.containsKey(methodSignature);
    }

    public SootField getField(String fieldSignature)
    {
        SootField f = (SootField) fieldSignatureToField.get(fieldSignature);
        if (f != null)
            return f;

	throw new RuntimeException("tried to get nonexistent field!");
    }

    public SootMethod getMethod(String methodSignature)
    {
        SootMethod m = (SootMethod) methodSignatureToMethod.get(methodSignature);
        if (m != null)
            return m;
        throw new RuntimeException("tried to get nonexistent method!");
    }

    /** 
     * Loads the given class and all of the required support classes.  Returns the first class.
     */
     
    public SootClass loadClassAndSupport(String className) throws ClassFileNotFoundException,
                                             CorruptClassFileException,
                                             DuplicateNameException
    {   
        /*
        if(Main.isProfilingOptimization)
            Main.resolveTimer.start();
        */
        
        Scene.v().setPhantomRefs(true);
	//        SootClass toReturn = soot.coffi.Util.resolveClassAndSupportClasses(className, this);
	SootResolver resolver = new SootResolver(this);
	SootClass toReturn = resolver.resolveClassAndSupportClasses(className);
        Scene.v().setPhantomRefs(false);

        return toReturn;
        
        /*
        if(Main.isProfilingOptimization)
            Main.resolveTimer.end(); */
    }
    
    /**
     * Returns the SootClass with the given className.  
     */

    public SootClass getSootClass(String className) throws ClassFileNotFoundException
    {   
        SootClass toReturn = (SootClass) nameToClass.get(className);
        
        if(toReturn == null)
        {	 


            if(Scene.v().allowsPhantomRefs())
            {
                SootClass c = new SootClass(className);
                c.setPhantom(true);
                addClass(c);
		classesToResolve.add(c.getName());
                return c;
            }
            else { System.out.println("can find classfile" + className );
	    throw new ClassFileNotFoundException();
	    }
        }
        else
            return toReturn;
    }

    /**
     * Returns an backed chain of the classes in this manager.
     */
     
    public Chain getClasses()
    {
        return classes;
    }

    /* The four following chains are mutually disjoint. */

    /**
     * Returns a chain of the application classes in this scene.
     * These classes are the ones which can be freely analysed & modified.
     */
    public Chain getApplicationClasses()
    {
        return applicationClasses;
    }

    /**
     * Returns a chain of the library classes in this scene.
     * These classes can be analysed but not modified.
     */
    public Chain getLibraryClasses()
    {
        return libraryClasses;
    }

    /**
     * Returns a chain of the context classes in this scene.
     * These classes may not be analysed, typically for speed reasons.
     */
    public Chain getContextClasses()
    {
        return contextClasses;
    }

    /**
     * Returns a chain of the phantom classes in this scene.
     * These classes are referred to by other classes, but cannot be loaded.
     */
    public Chain getPhantomClasses()
    {
        return phantomClasses;
    }

    Chain getContainingChain(SootClass c)
    {
        if (c.isApplicationClass())
            return getApplicationClasses();
        else if (c.isLibraryClass())
            return getLibraryClasses();
        else if (c.isContextClass())
            return getContextClasses();
        else if (c.isPhantomClass())
            return getPhantomClasses();

        return null;
    }

    /**
        Retrieves the active hierarchy for this method.
     */

    public Hierarchy getActiveHierarchy() 
    {
        if(!hasActiveHierarchy())
            throw new RuntimeException("no active Hierarchy present for scene");
            
        return activeHierarchy;
    }
    
    /**
        Sets the active hierarchy for this method. 
     */
     
    public void setActiveHierarchy(Hierarchy hierarchy)
    {
        activeHierarchy = hierarchy;
    }

    public boolean hasActiveHierarchy()
    {
        return activeHierarchy != null;
    }
    
    public void releaseActiveHierarchy()
    {
        activeHierarchy = null;
    }

    /**
        Retrieves the active invokeGraph for this method.
     */

    public InvokeGraph getActiveInvokeGraph() 
    {
        if(!hasActiveInvokeGraph())
            throw new RuntimeException("no active InvokeGraph present for scene");
            
        return activeInvokeGraph;
    }
    
    /**
        Sets the active invokeGraph for this method. 
     */
     
    public void setActiveInvokeGraph(InvokeGraph invokeGraph)
    {
        activeInvokeGraph = invokeGraph;
    }

    public boolean hasActiveInvokeGraph()
    {
        return activeInvokeGraph != null;
    }
    
    public void releaseActiveInvokeGraph()
    {
        activeInvokeGraph = null;
    }
    
    public boolean getPhantomRefs()
    {
        return allowsPhantomRefs;
    }

    public void setPhantomRefs(boolean value)
    {
        allowsPhantomRefs = value;
    }
    
    public void setLazyResolving(boolean value) 
    {
	allowsLazyResolving = value;
    }


    public boolean allowsPhantomRefs()
    {
        return allowsPhantomRefs;
    }
    public boolean allowsLazyResolving() 
    {
	return allowsLazyResolving;
    }
    


}










