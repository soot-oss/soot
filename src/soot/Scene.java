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


import soot.util.*;
import java.util.*;
import soot.jimple.spark.PointsToAnalysis;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.toolkits.pointer.*;

/** Manages the SootClasses of the application being analyzed. */
public class Scene  //extends AbstractHost
{
    public Scene ( Singletons.Global g )
    {
    	setReservedNames();
    	
        // load soot.class.path system property, if defined
        String scp = System.getProperty("soot.class.path");

        if (scp != null)
            setSootClassPath(scp);
    }
    public static Scene  v() { return G.v().Scene (); }

    Chain classes = new HashChain();
    Chain applicationClasses = new HashChain();
    Chain libraryClasses = new HashChain();
    Chain contextClasses = new HashChain();
    Chain phantomClasses = new HashChain();
    
    private Map nameToClass = new HashMap();

    Numberer typeNumberer = new Numberer();
    Numberer methodNumberer = new Numberer();
    Numberer classNumberer = new Numberer();
    StringNumberer subSigNumberer = new StringNumberer();
    Numberer localNumberer = new Numberer();

    Hierarchy activeHierarchy;
    FastHierarchy activeFastHierarchy;
    InvokeGraph activeInvokeGraph;
    PointsToAnalysis activePointsToAnalysis;
    SideEffectAnalysis activeSideEffectAnalysis;

    boolean allowsPhantomRefs = false;
    private boolean allowsLazyResolving = false;

    SootClass mainClass;
    String sootClassPath = "<external-class-path>";

    StmtPrinter jimpleStmtPrinter = soot.jimple.DefaultStmtPrinter.v();
    LocalPrinter localPrinter = soot.jimple.DefaultLocalPrinter.v();

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


    private int stateCount;
    public int getState() { return this.stateCount; }
    private void modifyHierarchy() {
        stateCount++;
        activeFastHierarchy = null;
        activeSideEffectAnalysis = null;
        activePointsToAnalysis = null;
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

        nameToClass.put(c.getName(), c.getType());
        c.getType().setSootClass(c);
        c.setInScene(true);
        modifyHierarchy();
    }

    public void removeClass(SootClass c)
    {
        if(!c.isInScene())
            throw new RuntimeException();

        classes.remove(c);
        c.getType().setSootClass(null);
        c.setInScene(false);
        modifyHierarchy();
    }

    public boolean containsClass(String className)
    {
        RefType type = (RefType) nameToClass.get(className);
        if( type == null ) return false;
        SootClass c = type.getSootClass();
        if( c == null ) return false;
        return c.isInScene();
    }

    public String signatureToClass(String sig) {
        if( sig.charAt(0) != '<' ) throw new RuntimeException("oops "+sig);
        if( sig.charAt(sig.length()-1) != '>' ) throw new RuntimeException("oops "+sig);
        int index = sig.indexOf( ":" );
        if( index < 0 ) throw new RuntimeException("oops "+sig);
        return sig.substring(1,index);
    }

    public String signatureToSubsignature(String sig) {
        if( sig.charAt(0) != '<' ) throw new RuntimeException("oops "+sig);
        if( sig.charAt(sig.length()-1) != '>' ) throw new RuntimeException("oops "+sig);
        int index = sig.indexOf( ":" );
        if( index < 0 ) throw new RuntimeException("oops "+sig);
        return sig.substring(index+2,sig.length()-1);
    }

    private SootField grabField(String fieldSignature)
    {
        String cname = signatureToClass( fieldSignature );
        String fname = signatureToSubsignature( fieldSignature );
        if( !containsClass(cname) ) return null;
        SootClass c = getSootClass(cname);
        if( !c.declaresField( fname ) ) return null;
        return c.getField( fname );
    }

    public boolean containsField(String fieldSignature)
    {
        return grabField(fieldSignature) != null;
    }
    
    private SootMethod grabMethod(String methodSignature)
    {
        String cname = signatureToClass( methodSignature );
        String mname = signatureToSubsignature( methodSignature );
        if( !containsClass(cname) ) return null;
        SootClass c = getSootClass(cname);
        if( !c.declaresMethod( mname ) ) return null;
        return c.getMethod( mname );
    }

    public boolean containsMethod(String methodSignature)
    {
        return grabMethod(methodSignature) != null;
    }

    public SootField getField(String fieldSignature)
    {
        SootField f = grabField( fieldSignature );
        if (f != null)
            return f;

        throw new RuntimeException("tried to get nonexistent field "+fieldSignature);
    }

    public SootMethod getMethod(String methodSignature)
    {
        SootMethod m = grabMethod( methodSignature );
        if (m != null)
            return m;
        throw new RuntimeException("tried to get nonexistent method "+methodSignature);
    }

    /** 
     * Loads the given class and all of the required support classes.  Returns the first class.
     */
     
    public SootClass loadClassAndSupport(String className) 
    {   
        /*
        if(Main.opts.time())
            Main.resolveTimer.start();
        */
        
        Scene.v().setPhantomRefs(true);
        SootResolver resolver = new SootResolver();
        SootClass toReturn = resolver.resolveClassAndSupportClasses(className);
        Scene.v().setPhantomRefs(false);

        return toReturn;
        
        /*
        if(Main.opts.time())
            Main.resolveTimer.end(); */
    }
    
    /**
     * Returns the RefType with the given className.  
     */
    public RefType getRefType(String className) 
    {
        return (RefType) nameToClass.get(className);
    }

    /**
     * Returns the RefType with the given className.  
     */
    public void addRefType(RefType type) 
    {
        nameToClass.put(type.getClassName(), type);
    }

    /**
     * Returns the SootClass with the given className.  
     */

    public SootClass getSootClass(String className) 
    {   
        RefType type = (RefType) nameToClass.get(className);
        SootClass toReturn = null;
        if( type != null ) toReturn = type.getSootClass();
        
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
	    return DumbPointerAnalysis.v();
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
            //throw new RuntimeException("no active Hierarchy present for scene");
            setActiveHierarchy( new Hierarchy() );
            
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
        if( !soot.Main.opts.allow_phantom_refs() ) return false;
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
        return getPhantomRefs();
    }
    public boolean allowsLazyResolving() 
    {
        return allowsLazyResolving;
    }
    public Numberer getTypeNumberer() { return typeNumberer; }
    public Numberer getMethodNumberer() { return methodNumberer; }
    public Numberer getClassNumberer() { return classNumberer; }
    public StringNumberer getSubSigNumberer() { return subSigNumberer; }
    public Numberer getLocalNumberer() { return localNumberer; }

    private void setReservedNames()
    {
        Set rn = getReservedNames();        
        rn.add("newarray");
        rn.add("newmultiarray");
        rn.add("nop");
        rn.add("ret");
        rn.add("specialinvoke");
        rn.add("staticinvoke");
        rn.add("tableswitch");
        rn.add("virtualinvoke");
        rn.add("null_type");
        rn.add("unknown");
        rn.add("cmp");
        rn.add("cmpg");
        rn.add("cmpl");
        rn.add("entermonitor");
        rn.add("exitmonitor");
        rn.add("interfaceinvoke");
        rn.add("lengthof");
        rn.add("lookupswitch");
        rn.add("neg");
        rn.add("if");
        rn.add("abstract");
        rn.add("boolean");
        rn.add("break");
        rn.add("byte");
        rn.add("case");
        rn.add("catch");
        rn.add("char");
        rn.add("class");
        rn.add("final");
        rn.add("native");
        rn.add("public");
        rn.add("protected");
        rn.add("private");
        rn.add("static");
        rn.add("synchronized");
        rn.add("transient");
        rn.add("volatile");
	rn.add("interface");
        rn.add("void");
        rn.add("short");
        rn.add("int");
        rn.add("long");
        rn.add("float");
        rn.add("double");
        rn.add("extends");
        rn.add("implements");
        rn.add("breakpoint");
        rn.add("default");
        rn.add("goto");
        rn.add("instanceof");
        rn.add("new");
        rn.add("return");
        rn.add("throw");
        rn.add("throws");
        rn.add("null");
        rn.add("from");
	rn.add("to");
    }


}
