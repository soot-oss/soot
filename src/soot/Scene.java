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
import soot.jimple.toolkits.scalar.pre.*;
import soot.jimple.toolkits.pointer.*;
import soot.toolkits.scalar.*;
import soot.jimple.spark.PointsToAnalysis;
import soot.jimple.spark.SparkTransformer;

/** Manages the SootClasses of the application being analyzed. */
public class Scene  //extends AbstractHost
{
    private static Scene constant = new Scene();

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
    FastHierarchy activeFastHierarchy;
    InvokeGraph activeInvokeGraph;
    PointsToAnalysis activePointsToAnalysis;
    SideEffectAnalysis activeSideEffectAnalysis;

    boolean allowsPhantomRefs = false;
    private boolean allowsLazyResolving = false;

    Map packNameToPack = new HashMap();
    SootClass mainClass;
    String sootClassPath = "<external-class-path>";

    StmtPrinter jimpleStmtPrinter = soot.jimple.DefaultStmtPrinter.v();
    LocalPrinter localPrinter = soot.jimple.DefaultLocalPrinter.v();

    /**
        Resets this scene to zero.
    */    
    public void reset()
    {
        Scene.constant = new Scene();
    }
    
    public static Scene v()
    {
        return constant;
    }
    
    public void setMainClass(SootClass m)
    {
        mainClass = m;
    }
    
    Set reservedNames = new HashSet();
    
    /**
        Returns a set of tokens which are reserved.  Any field, class, method, or local variable with such a name will be quoted.
     */
     
    public Set getReservedNames()
    {
        return reservedNames;
    }
    
    /**
        If this name is in the set of reserved names, then return a quoted version of it.  Else pass it through.
     */
    
    public String quotedNameOf(String s)
    {
        if(reservedNames.contains(s))
            return "\'" + s + "\'";
        else
            return s;
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

        // Jimple transformation pack
        packNameToPack.put("jtp", p = new Pack());

        // Jimple optimization pack (-O)
        packNameToPack.put("jop", p = new Pack());
        {
            p.add(new Transform("jop.cse",  CommonSubexpressionEliminator.v(),
                  "disabled"));
            p.add(new Transform("jop.bcm",  BusyCodeMotion.v(), "disabled"));
            p.add(new Transform("jop.lcm",  LazyCodeMotion.v(), "disabled"));
            p.add(new Transform("jop.cp",   CopyPropagator.v()));
            p.add(new Transform("jop.cpf",  ConstantPropagatorAndFolder.v()));
            p.add(new Transform("jop.cbf",  ConditionalBranchFolder.v()));
            p.add(new Transform("jop.dae",  DeadAssignmentEliminator.v()));
            p.add(new Transform("jop.uce1", UnreachableCodeEliminator.v()));
            p.add(new Transform("jop.ubf1", UnconditionalBranchFolder.v()));
            p.add(new Transform("jop.uce2", UnreachableCodeEliminator.v()));
            p.add(new Transform("jop.ubf2", UnconditionalBranchFolder.v()));
            p.add(new Transform("jop.ule",  UnusedLocalEliminator.v()));
//              p.add(new Transform("jop.pre", PartialRedundancyEliminator.v()));
        }

        // Whole-Jimple transformation pack (--app)
        packNameToPack.put("wjtp", p = new Pack());
        {
            p.add(new Transform("wjtp.Spark", SparkTransformer.v(), "disabled"));
        }

        // Whole-Jimple Optimization pack (--app -W)
        packNameToPack.put("wjop", p = new Pack());
        {
            p.add(new Transform("wjop.smb", StaticMethodBinder.v(), "disabled"));
            p.add(new Transform("wjop.si", StaticInliner.v()));
        }

	// Give another chance to do Whole-Jimple transformation
	// The RectangularArrayFinder will be put into this package.
	packNameToPack.put("wjtp2", p = new Pack());	
		
        // Baf optimization pack
        packNameToPack.put("bop", p = new Pack());

        // Grimp optimization pack
        packNameToPack.put("gop", p = new Pack());

        // load soot.class.path system property, if defined
        String scp = System.getProperty("soot.class.path");

        if (scp != null)
            setSootClassPath(scp);
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
    private void modifyHierarchy() {
        stateCount++;
        activeFastHierarchy = null;
        activeSideEffectAnalysis = null;
        activePointsToAnalysis = null;
    }

    /** Returns the options map associated with phaseName. 
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

    /* Adds optionsString to the Scene's options for phaseName 
     * and returns the corresponding Map.  Does not change
     * the getPhaseOptions() map in the Scene. 
     *
     * The previous options (getPhaseOptions()) get precedence. */
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

        Map oldOptions = getPhaseOptions(phaseName);
        Iterator optionKeysIt = oldOptions.keySet().iterator();
        while (optionKeysIt.hasNext())
        {
            String s = (String)optionKeysIt.next();
            
            options.put(s, oldOptions.get(s));
        }

        return options;
    }

    /** Returns the current StmtPrinter class for Jimple. */
    public StmtPrinter getJimpleStmtPrinter()
    {
        return jimpleStmtPrinter;
    }

    public LocalPrinter getLocalPrinter()
    {
	return localPrinter;
    }

    /** Sets the current StmtPrinter class for Jimple. */
    public void setJimpleStmtPrinter(StmtPrinter jsp)
    {
        this.jimpleStmtPrinter = jsp;
    }

    public void setLocalPrinter( LocalPrinter lp)
    {
	this.localPrinter = lp;
    }

    public void addClass(SootClass c) 
    {
        if(c.isInScene())
            throw new RuntimeException("already managed: "+c.getName());

        if(containsClass(c.getName()))
            throw new RuntimeException("duplicate class: "+c.getName());

        classes.add(c);

        nameToClass.put(c.getName(), c);
        c.setInScene(true);
        modifyHierarchy();
    }

    public void removeClass(SootClass c)
    {
        if(!c.isInScene())
            throw new RuntimeException();

        classes.remove(c);
        nameToClass.remove(c.getName());
        c.setInScene(false);
        modifyHierarchy();
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

	public SootMethod forceGetMethod(String methodSignature) {
		SootMethod m = 
			(SootMethod)methodSignatureToMethod.get(methodSignature);
		return m;
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
     
    public SootClass loadClassAndSupport(String className) 
    {   
        /*
        if(Main.isProfilingOptimization)
            Main.resolveTimer.start();
        */
        
        Scene.v().setPhantomRefs(true);
        SootResolver resolver = new SootResolver();
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

    public SootClass getSootClass(String className) 
    {   
        SootClass toReturn = (SootClass) nameToClass.get(className);
        
        if(toReturn != null) {
	    return toReturn;
	} else  if(Scene.v().allowsPhantomRefs()) {            
	    SootClass c = new SootClass(className);
	    c.setPhantom(true);
	    addClass(c);
	    return c;
	}
	else {          
	    throw new RuntimeException( System.getProperty("line.separator") + "Aborting: can't find classfile" + className );            
	}
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

    /****************************************************************************/
    /**
        Retrieves the active side-effect analysis
     */

    public SideEffectAnalysis getActiveSideEffectAnalysis() 
    {
        if(!hasActiveSideEffectAnalysis()) {
	    setActiveSideEffectAnalysis( new SideEffectAnalysis(
			getActivePointsToAnalysis(),
			getActiveInvokeGraph() ) );
	}
            
        return activeSideEffectAnalysis;
    }
    
    /**
        Sets the active side-effect analysis
     */
     
    public void setActiveSideEffectAnalysis(SideEffectAnalysis sea)
    {
        activeSideEffectAnalysis = sea;
    }

    public boolean hasActiveSideEffectAnalysis()
    {
        return activeSideEffectAnalysis != null;
    }
    
    public void releaseActiveSideEffectAnalysis()
    {
        activeSideEffectAnalysis = null;
    }

    /****************************************************************************/
    /**
        Retrieves the active pointer analysis
     */

    public PointsToAnalysis getActivePointsToAnalysis() 
    {
        if(!hasActivePointsToAnalysis()) {
	    return new DumbPointerAnalysis();
	}
            
        return activePointsToAnalysis;
    }
    
    /**
        Sets the active pointer analysis
     */
     
    public void setActivePointsToAnalysis(PointsToAnalysis pa)
    {
        activePointsToAnalysis = pa;
    }

    public boolean hasActivePointsToAnalysis()
    {
        return activePointsToAnalysis != null;
    }
    
    public void releaseActivePointsToAnalysis()
    {
        activePointsToAnalysis = null;
    }

    /****************************************************************************/
    /** Makes a new fast hierarchy is none is active, and returns the active
     * fast hierarchy. */
    public FastHierarchy getOrMakeFastHierarchy() {
	if(!hasActiveFastHierarchy() ) {
	    setActiveFastHierarchy( new FastHierarchy() );
	}
	return getActiveFastHierarchy();
    }
    /**
        Retrieves the active fast hierarchy
     */

    public FastHierarchy getActiveFastHierarchy() 
    {
        if(!hasActiveFastHierarchy())
            throw new RuntimeException("no active FastHierarchy present for scene");
            
        return activeFastHierarchy;
    }
    
    /**
        Sets the active hierarchy
     */
     
    public void setActiveFastHierarchy(FastHierarchy hierarchy)
    {
        activeFastHierarchy = hierarchy;
    }

    public boolean hasActiveFastHierarchy()
    {
        return activeFastHierarchy != null;
    }
    
    public void releaseActiveFastHierarchy()
    {
        activeFastHierarchy = null;
    }

    /****************************************************************************/
    /**
        Retrieves the active hierarchy
     */

    public Hierarchy getActiveHierarchy() 
    {
        if(!hasActiveHierarchy())
            throw new RuntimeException("no active Hierarchy present for scene");
            
        return activeHierarchy;
    }
    
    /**
        Sets the active hierarchy
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

    /****************************************************************************/
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
