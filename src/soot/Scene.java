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
import soot.options.*;


import soot.util.*;

import java.util.*;
import java.io.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.pointer.*;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.exceptions.UnitThrowAnalysis;

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

        kindNumberer.add( Kind.INVALID );
        kindNumberer.add( Kind.STATIC );
        kindNumberer.add( Kind.VIRTUAL );
        kindNumberer.add( Kind.INTERFACE );
        kindNumberer.add( Kind.SPECIAL );
        kindNumberer.add( Kind.CLINIT );
        kindNumberer.add( Kind.THREAD );
        kindNumberer.add( Kind.FINALIZE );
        kindNumberer.add( Kind.INVOKE_FINALIZE );
        kindNumberer.add( Kind.PRIVILEGED );
        kindNumberer.add( Kind.NEWINSTANCE );

	addSootBasicClasses();
    }
    public static Scene  v() { return G.v().soot_Scene (); }

    Chain classes = new HashChain();
    Chain applicationClasses = new HashChain();
    Chain libraryClasses = new HashChain();
    Chain phantomClasses = new HashChain();
    
    private Map nameToClass = new HashMap();

    ArrayNumberer kindNumberer = new ArrayNumberer();
    ArrayNumberer typeNumberer = new ArrayNumberer();
    ArrayNumberer methodNumberer = new ArrayNumberer();
    Numberer unitNumberer = new MapNumberer();
    Numberer contextNumberer = null;
    ArrayNumberer fieldNumberer = new ArrayNumberer();
    ArrayNumberer classNumberer = new ArrayNumberer();
    StringNumberer subSigNumberer = new StringNumberer();
    ArrayNumberer localNumberer = new ArrayNumberer();

    private Hierarchy activeHierarchy;
    private FastHierarchy activeFastHierarchy;
    private CallGraph activeCallGraph;
    private ReachableMethods reachableMethods;
    private PointsToAnalysis activePointsToAnalysis;
    private SideEffectAnalysis activeSideEffectAnalysis;
    private List entryPoints;

    boolean allowsPhantomRefs = false;

    // temporary for testing cfgs in plugin
    
    public ArrayList cfgList = new ArrayList();
    
    SootClass mainClass;
    String sootClassPath = null;

    // Two default values for constructing ExceptionalUnitGraphs:
    private ThrowAnalysis defaultThrowAnalysis = null;
    
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
            throw new RuntimeException("There is no main class set!");
            
        return mainClass;
    }
    
    
    public void setSootClassPath(String p)
    {
        sootClassPath = p;
        SourceLocator.v().invalidateClassPath();
    }
    
    public String getSootClassPath()
    {
        if( sootClassPath == null ) {
            String optionscp = Options.v().soot_classpath();
            if( optionscp.length() > 0 )
                sootClassPath = optionscp;
        }
        if( sootClassPath == null ) {
            sootClassPath = System.getProperty("java.class.path")+File.pathSeparator+
                System.getProperty("java.home")+File.separator+
                "lib"+File.separator+"rt.jar";
        }
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

    public void addClass(SootClass c) 
    {
        if(c.isInScene())
            throw new RuntimeException("already managed: "+c.getName());

        if(containsClass(c.getName()))
            throw new RuntimeException("duplicate class: "+c.getName());

        classes.add(c);
        c.setLibraryClass();

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
        
        if(c.isLibraryClass()) {
            libraryClasses.remove(c);
        } else if(c.isPhantomClass()) {
            phantomClasses.remove(c);
        } else if(c.isApplicationClass()) {
            applicationClasses.remove(c);
        }
        
        c.getType().setSootClass(null);
        c.setInScene(false);
        modifyHierarchy();
    }

    public boolean containsClass(String className)
    {
        RefType type = (RefType) nameToClass.get(className);
        if( type == null ) return false;
        if( !type.hasSootClass() ) return false;
        SootClass c = type.getSootClass();
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
     * Attempts to load the given class and all of the required support classes.
     * Returns the original class if it was loaded, or null otherwise.
     */
     
    public SootClass tryLoadClass(String className, int desiredLevel) 
    {   
        /*
        if(Options.v().time())
            Main.v().resolveTimer.start();
        */
        
        Scene.v().setPhantomRefs(true);
        //SootResolver resolver = new SootResolver();
        if( !getPhantomRefs() 
        && SourceLocator.v().getClassSource(className) == null ) {
            Scene.v().setPhantomRefs(false);
            return null;
        }
        SootResolver resolver = SootResolver.v();
        SootClass toReturn = resolver.resolveClass(className, desiredLevel);
        Scene.v().setPhantomRefs(false);

        return toReturn;
        
        /*
        if(Options.v().time())
            Main.v().resolveTimer.end(); */
    }
    
    /** 
     * Loads the given class and all of the required support classes.  Returns the first class.
     */
     
    public SootClass loadClassAndSupport(String className) 
    {
        SootClass ret = loadClass(className, SootClass.SIGNATURES);
        if( !ret.isPhantom() ) ret = loadClass(className, SootClass.BODIES);
        return ret;
    }

    public SootClass loadClass(String className, int desiredLevel) 
    {   
        /*
        if(Options.v().time())
            Main.v().resolveTimer.start();
        */
        
        Scene.v().setPhantomRefs(true);
        //SootResolver resolver = new SootResolver();
        SootResolver resolver = SootResolver.v();
        SootClass toReturn = resolver.resolveClass(className, desiredLevel);
        Scene.v().setPhantomRefs(false);

        return toReturn;
        
        /*
        if(Options.v().time())
            Main.v().resolveTimer.end(); */
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
	    throw new RuntimeException( System.getProperty("line.separator") + "Aborting: can't find classfile " + className );            
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
        else if (c.isPhantomClass())
            return getPhantomClasses();

        return null;
    }

    /****************************************************************************/
    /**
        Retrieves the active side-effect analysis
     */

    public SideEffectAnalysis getSideEffectAnalysis() 
    {
        if(!hasSideEffectAnalysis()) {
	    setSideEffectAnalysis( new SideEffectAnalysis(
			getPointsToAnalysis(),
			getCallGraph() ) );
	}
            
        return activeSideEffectAnalysis;
    }
    
    /**
        Sets the active side-effect analysis
     */
     
    public void setSideEffectAnalysis(SideEffectAnalysis sea)
    {
        activeSideEffectAnalysis = sea;
    }

    public boolean hasSideEffectAnalysis()
    {
        return activeSideEffectAnalysis != null;
    }
    
    public void releaseSideEffectAnalysis()
    {
        activeSideEffectAnalysis = null;
    }

    /****************************************************************************/
    /**
        Retrieves the active pointer analysis
     */

    public PointsToAnalysis getPointsToAnalysis() 
    {
        if(!hasPointsToAnalysis()) {
	    return DumbPointerAnalysis.v();
	}
            
        return activePointsToAnalysis;
    }
    
    /**
        Sets the active pointer analysis
     */
     
    public void setPointsToAnalysis(PointsToAnalysis pa)
    {
        activePointsToAnalysis = pa;
    }

    public boolean hasPointsToAnalysis()
    {
        return activePointsToAnalysis != null;
    }
    
    public void releasePointsToAnalysis()
    {
        activePointsToAnalysis = null;
    }

    /****************************************************************************/
    /** Makes a new fast hierarchy is none is active, and returns the active
     * fast hierarchy. */
    public FastHierarchy getOrMakeFastHierarchy() {
	if(!hasFastHierarchy() ) {
	    setFastHierarchy( new FastHierarchy() );
	}
	return getFastHierarchy();
    }
    /**
        Retrieves the active fast hierarchy
     */

    public FastHierarchy getFastHierarchy() 
    {
        if(!hasFastHierarchy())
            throw new RuntimeException("no active FastHierarchy present for scene");
            
        return activeFastHierarchy;
    }
    
    /**
        Sets the active hierarchy
     */
     
    public void setFastHierarchy(FastHierarchy hierarchy)
    {
        activeFastHierarchy = hierarchy;
    }

    public boolean hasFastHierarchy()
    {
        return activeFastHierarchy != null;
    }
    
    public void releaseFastHierarchy()
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

    /** Get the set of entry points that are used to build the call graph. */
    public List getEntryPoints() {
        if( entryPoints == null ) {
            entryPoints = EntryPoints.v().all();
        }
        return entryPoints;
    }

    /** Change the set of entry point methods used to build the call graph. */
    public void setEntryPoints( List entryPoints ) {
        this.entryPoints = entryPoints;
    }

    private ContextSensitiveCallGraph cscg;
    public ContextSensitiveCallGraph getContextSensitiveCallGraph() {
        if(cscg == null) throw new RuntimeException("No context-sensitive call graph present in Scene. You can bulid one with Paddle.");
        return cscg;
    }

    public void setContextSensitiveCallGraph(ContextSensitiveCallGraph cscg) {
        this.cscg = cscg;
    }

    public CallGraph getCallGraph() 
    {
        if(!hasCallGraph()) {
            throw new RuntimeException( "No call graph present in Scene. Maybe you want Whole Program mode (-w)." );
        }
            
        return activeCallGraph;
    }
    
    public void setCallGraph(CallGraph cg)
    {
        reachableMethods = null;
        activeCallGraph = cg;
    }

    public boolean hasCallGraph()
    {
        return activeCallGraph != null;
    }
    
    public void releaseCallGraph()
    {
        activeCallGraph = null;
        reachableMethods = null;
    }
    public ReachableMethods getReachableMethods() {
        if( reachableMethods == null ) {
            reachableMethods = new ReachableMethods(
                    getCallGraph(), getEntryPoints() );
        }
        reachableMethods.update();
        return reachableMethods;
    }
    public void setReachableMethods( ReachableMethods rm ) {
        reachableMethods = rm;
    }
    public boolean hasReachableMethods() {
        return reachableMethods != null;
    }
    public void releaseReachableMethods() {
        reachableMethods = null;
    }
   
    public boolean getPhantomRefs()
    {
        if( !Options.v().allow_phantom_refs() ) return false;
        return allowsPhantomRefs;
    }

    public void setPhantomRefs(boolean value)
    {
        allowsPhantomRefs = value;
    }
    
    public boolean allowsPhantomRefs()
    {
        return getPhantomRefs();
    }
    public Numberer kindNumberer() { return kindNumberer; }
    public ArrayNumberer getTypeNumberer() { return typeNumberer; }
    public ArrayNumberer getMethodNumberer() { return methodNumberer; }
    public Numberer getContextNumberer() { return contextNumberer; }
    public Numberer getUnitNumberer() { return unitNumberer; }
    public ArrayNumberer getFieldNumberer() { return fieldNumberer; }
    public ArrayNumberer getClassNumberer() { return classNumberer; }
    public StringNumberer getSubSigNumberer() { return subSigNumberer; }
    public ArrayNumberer getLocalNumberer() { return localNumberer; }

    public void setContextNumberer( Numberer n ) {
        if( contextNumberer != null )
            throw new RuntimeException(
                    "Attempt to set context numberer when it is already set." );
        contextNumberer = n;
    }

    /**
     * Returns the {@link ThrowAnalysis} to be used by default when
     * constructing CFGs which include exceptional control flow.
     *
     * @return the default {@link ThrowAnalysis}
     */
    public ThrowAnalysis getDefaultThrowAnalysis() 
    {
	if( defaultThrowAnalysis == null ) {
	    int optionsThrowAnalysis = Options.v().throw_analysis();
	    switch (optionsThrowAnalysis) {
	    case Options.throw_analysis_pedantic:
		defaultThrowAnalysis = PedanticThrowAnalysis.v();
		break;
	    case Options.throw_analysis_unit:
		defaultThrowAnalysis = UnitThrowAnalysis.v();
		break;
	    default:
		throw new IllegalStateException("Options.v().throw_analysi() == " +
						Options.v().throw_analysis());
	    }
	}
	return defaultThrowAnalysis;
    }

    /**
     * Sets the {@link ThrowAnalysis} to be used by default when
     * constructing CFGs which include exceptional control flow.
     *
     * @param the default {@link ThrowAnalysis}.
     */
    public void setDefaultThrowAnalysis(ThrowAnalysis ta) 
    {
	defaultThrowAnalysis = ta;
    }

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

    private Set[]/*<String>*/ basicclasses=new Set[4];

    private void addSootBasicClasses() {
        basicclasses[SootClass.HIERARCHY] = new HashSet();
        basicclasses[SootClass.SIGNATURES] = new HashSet();
        basicclasses[SootClass.BODIES] = new HashSet();

	addBasicClass("java.lang.Object");
	addBasicClass("java.lang.Class", SootClass.SIGNATURES);

	addBasicClass("java.lang.Void", SootClass.SIGNATURES);
	addBasicClass("java.lang.Boolean", SootClass.SIGNATURES);
	addBasicClass("java.lang.Byte", SootClass.SIGNATURES);
	addBasicClass("java.lang.Character", SootClass.SIGNATURES);
	addBasicClass("java.lang.Short", SootClass.SIGNATURES);
	addBasicClass("java.lang.Integer", SootClass.SIGNATURES);
	addBasicClass("java.lang.Long", SootClass.SIGNATURES);
	addBasicClass("java.lang.Float", SootClass.SIGNATURES);
	addBasicClass("java.lang.Double", SootClass.SIGNATURES);

	addBasicClass("java.lang.String");
	addBasicClass("java.lang.StringBuffer", SootClass.SIGNATURES);

	addBasicClass("java.lang.Error");
	addBasicClass("java.lang.AssertionError", SootClass.SIGNATURES);
	addBasicClass("java.lang.Throwable", SootClass.SIGNATURES);
	addBasicClass("java.lang.NoClassDefFoundError", SootClass.SIGNATURES);
	addBasicClass("java.lang.ExceptionInInitializerError");
	addBasicClass("java.lang.RuntimeException");
	addBasicClass("java.lang.ClassNotFoundException");
	addBasicClass("java.lang.ArithmeticException");
	addBasicClass("java.lang.ArrayStoreException");
	addBasicClass("java.lang.ClassCastException");
	addBasicClass("java.lang.IllegalMonitorStateException");
	addBasicClass("java.lang.IndexOutOfBoundsException");
	addBasicClass("java.lang.ArrayIndexOutOfBoundsException");
	addBasicClass("java.lang.NegativeArraySizeException");
	addBasicClass("java.lang.NullPointerException");
	addBasicClass("java.lang.InstantiationError");
	addBasicClass("java.lang.InternalError");
	addBasicClass("java.lang.OutOfMemoryError");
	addBasicClass("java.lang.StackOverflowError");
	addBasicClass("java.lang.UnknownError");
	addBasicClass("java.lang.ThreadDeath");
	addBasicClass("java.lang.ClassCircularityError");
	addBasicClass("java.lang.ClassFormatError");
	addBasicClass("java.lang.IllegalAccessError");
	addBasicClass("java.lang.IncompatibleClassChangeError");
	addBasicClass("java.lang.LinkageError");
	addBasicClass("java.lang.VerifyError");
	addBasicClass("java.lang.NoSuchFieldError");
	addBasicClass("java.lang.AbstractMethodError");
	addBasicClass("java.lang.NoSuchMethodError");
	addBasicClass("java.lang.UnsatisfiedLinkError");

	addBasicClass("java.lang.Thread");
	addBasicClass("java.lang.Runnable");
	addBasicClass("java.lang.Cloneable");

	addBasicClass("java.io.Serializable");	

	addBasicClass("java.lang.ref.Finalizer");
    }

    public void addBasicClass(String name) {
	addBasicClass(name,SootClass.HIERARCHY);
    }
    
    public void addBasicClass(String name,int level) {
	basicclasses[level].add(name);
    }

    /** Load just the set of basic classes soot needs, ignoring those
     *  specified on the command-line. You don't need to use both this and 
     *  loadNecessaryClasses, though it will only waste time.
     */
    public void loadBasicClasses() {
	Iterator it;

	for(int i=SootClass.BODIES;i>=SootClass.HIERARCHY;i--) {
	    it = basicclasses[i].iterator();
	    while(it.hasNext()) {
		String name=(String) it.next();
		tryLoadClass(name,i);
	    }
	}
    }

    private List dynamicClasses;
    public Collection dynamicClasses() {
        return dynamicClasses;
    }

    private void loadNecessaryClass(String name) {
        SootClass c;

        c = Scene.v().loadClassAndSupport(name);

        if (mainClass == null) {
            mainClass = c;
            Scene.v().setMainClass(c);
        }
        c.setApplicationClass();
    }
    /** Load the set of classes that soot needs, including those specified on the
     *  command-line. This is the standard way of initialising the list of
     *  classes soot should use.
     */
    public void loadNecessaryClasses() {
	loadBasicClasses();

        Iterator it = Options.v().classes().iterator();

        while (it.hasNext()) {
            String name = (String) it.next();
            loadNecessaryClass(name);
        }

        loadDynamicClasses();

        for( Iterator pathIt = Options.v().process_dir().iterator(); pathIt.hasNext(); ) {

            final String path = (String) pathIt.next();
            for( Iterator clIt = SourceLocator.v().getClassesUnder(path).iterator(); clIt.hasNext(); ) {
                final String cl = (String) clIt.next();
                Scene.v().loadClassAndSupport(cl).setApplicationClass();
            }
        }

        prepareClasses();
        setMainClassFromOptions();
        setDoneResolving();
    }

    public void loadDynamicClasses() {
        dynamicClasses = new ArrayList();
        HashSet dynClasses = new HashSet();
        dynClasses.addAll(Options.v().dynamic_class());

        for( Iterator pathIt = Options.v().dynamic_dir().iterator(); pathIt.hasNext(); ) {

            final String path = (String) pathIt.next();
            dynClasses.addAll(SourceLocator.v().getClassesUnder(path));
        }

        for( Iterator pkgIt = Options.v().dynamic_package().iterator(); pkgIt.hasNext(); ) {

            final String pkg = (String) pkgIt.next();
            dynClasses.addAll(SourceLocator.v().classesInDynamicPackage(pkg));
        }

        for( Iterator classNameIt = dynClasses.iterator(); classNameIt.hasNext(); ) {

            final String className = (String) classNameIt.next();
            dynamicClasses.add( Scene.v().loadClassAndSupport(className) );
        }
    }


    /* Generate classes to process, adding or removing package marked by
     * command line options. 
     */
    private void prepareClasses() {

        LinkedList excludedPackages = new LinkedList();
        if (Options.v().exclude() != null)
            excludedPackages.addAll(Options.v().exclude());

        if( !Options.v().include_all() ) {
            excludedPackages.add("java.");
            excludedPackages.add("sun.");
            excludedPackages.add("javax.");
            excludedPackages.add("com.sun.");
            excludedPackages.add("com.ibm.");
            excludedPackages.add("org.xml.");
            excludedPackages.add("org.w3c.");
            excludedPackages.add("org.apache.");
        }

        // Remove/add all classes from packageInclusionMask as per -i option
        Set processedClasses = new HashSet();
        while(true) {
            Set unprocessedClasses = new HashSet(Scene.v().getClasses());
            unprocessedClasses.removeAll(processedClasses);
            if( unprocessedClasses.isEmpty() ) break;
            processedClasses.addAll(unprocessedClasses);
            for( Iterator sIt = unprocessedClasses.iterator(); sIt.hasNext(); ) {
                final SootClass s = (SootClass) sIt.next();
                if( s.isPhantom() ) continue;
                if(Options.v().app()) {
                    s.setApplicationClass();
                }
                if (Options.v().classes().contains(s.getName())) {
                    s.setApplicationClass();
                    continue;
                }
                for( Iterator pkgIt = excludedPackages.iterator(); pkgIt.hasNext(); ) {
                    final String pkg = (String) pkgIt.next();
                    if (s.isApplicationClass()
                    && s.getPackageName().startsWith(pkg)) {
                            s.setLibraryClass();
                    }
                }
                for( Iterator pkgIt = Options.v().include().iterator(); pkgIt.hasNext(); ) {
                    final String pkg = (String) pkgIt.next();
                    if (s.getPackageName().startsWith(pkg))
                        s.setApplicationClass();
                }
                if(s.isApplicationClass()) {
                    // make sure we have the support
                    Scene.v().loadClassAndSupport(s.getName());
                }
            }
        }
    }

    ArrayList pkgList;

    public void setPkgList(ArrayList list){
        pkgList = list;
    }

    public ArrayList getPkgList(){
        return pkgList;
    }


    /** Create an unresolved reference to a method. */
    public SootMethodRef makeMethodRef( 
            SootClass declaringClass,
            String name,
            List/*Type*/ parameterTypes,
            Type returnType,
            boolean isStatic ) {
        return new AbstractSootMethodRef(declaringClass, name, parameterTypes,
                returnType, isStatic);
    }

    /** Create an unresolved reference to a constructor. */
    public SootMethodRef makeConstructorRef( 
            SootClass declaringClass,
            List/*Type*/ parameterTypes) {
        return makeMethodRef(declaringClass, SootMethod.constructorName, 
                                         parameterTypes, VoidType.v(), false );
    }


    /** Create an unresolved reference to a field. */
    public SootFieldRef makeFieldRef( 
            SootClass declaringClass,
            String name,
            Type type,
            boolean isStatic) {
        return new AbstractSootFieldRef(declaringClass, name, type, isStatic);
    }
    /** Returns the list of SootClasses that have been resolved at least to 
     * the level specified. */
    public List/*SootClass*/ getClasses(int desiredLevel) {
        List ret = new ArrayList();
        for( Iterator clIt = getClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            if( cl.resolvingLevel() >= desiredLevel ) ret.add(cl);
        }
        return ret;
    }
    private boolean doneResolving = false;
    public boolean doneResolving() { return doneResolving; }
    public void setDoneResolving() { doneResolving = true; }
    public void setMainClassFromOptions() {
        if(mainClass != null) return;
        if( Options.v().main_class() != null
                && Options.v().main_class().length() > 0 ) {
            setMainClass(getSootClass(Options.v().main_class()));
        } else {        	
        	// try to infer a main class if none is given 
        	for (Iterator classIter = getApplicationClasses().iterator(); classIter.hasNext();) {
                    SootClass c = (SootClass) classIter.next();
                    if (c.declaresMethod ("main", new SingletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ), VoidType.v()))
                    {
                        G.v().out.println("No main class given. Inferred '"+c.getName()+"' as main class.");					
                        setMainClass(c);
                        break;
                    }
                }
        }
    }
}

