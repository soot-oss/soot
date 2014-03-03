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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParser;

import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.ContextSensitiveCallGraph;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.pointer.DumbPointerAnalysis;
import soot.jimple.toolkits.pointer.SideEffectAnalysis;
import soot.options.CGOptions;
import soot.options.Options;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.exceptions.UnitThrowAnalysis;
import soot.util.ArrayNumberer;
import soot.util.Chain;
import soot.util.HashChain;
import soot.util.MapNumberer;
import soot.util.Numberer;
import soot.util.StringNumberer;
import test.AXMLPrinter;
import android.content.res.AXmlResourceParser;

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
        kindNumberer.add( Kind.ASYNCTASK );
        kindNumberer.add( Kind.FINALIZE );
        kindNumberer.add( Kind.INVOKE_FINALIZE );
        kindNumberer.add( Kind.PRIVILEGED );
        kindNumberer.add( Kind.NEWINSTANCE );

        addSootBasicClasses();
        
        determineExcludedPackages();
    }
	private void determineExcludedPackages() {
		excludedPackages = new LinkedList<String>();
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
            excludedPackages.add("apple.awt.");
            excludedPackages.add("com.apple.");
        }
	}
    public static Scene  v() { return G.v().soot_Scene (); }
    
    Chain<SootClass> classes = new HashChain<SootClass>();
    Chain<SootClass> applicationClasses = new HashChain<SootClass>();
    Chain<SootClass> libraryClasses = new HashChain<SootClass>();
    Chain<SootClass> phantomClasses = new HashChain<SootClass>();
    
    private final Map<String,Type> nameToClass = new HashMap<String,Type>();

    ArrayNumberer<Kind> kindNumberer = new ArrayNumberer<Kind>();
    ArrayNumberer<Type> typeNumberer = new ArrayNumberer<Type>();
    ArrayNumberer<SootMethod> methodNumberer = new ArrayNumberer<SootMethod>();
    Numberer unitNumberer = new MapNumberer();
    Numberer contextNumberer = null;
    ArrayNumberer fieldNumberer = new ArrayNumberer<SootField>();
    ArrayNumberer<SootClass> classNumberer = new ArrayNumberer<SootClass>();
    StringNumberer subSigNumberer = new StringNumberer();
    ArrayNumberer<Local> localNumberer = new ArrayNumberer<Local>();

    private Hierarchy activeHierarchy;
    private FastHierarchy activeFastHierarchy;
    private CallGraph activeCallGraph;
    private ReachableMethods reachableMethods;
    private PointsToAnalysis activePointsToAnalysis;
    private SideEffectAnalysis activeSideEffectAnalysis;
    private List<SootMethod> entryPoints;

    boolean allowsPhantomRefs = false;

    SootClass mainClass;
    String sootClassPath = null;

    // Two default values for constructing ExceptionalUnitGraphs:
    private ThrowAnalysis defaultThrowAnalysis = null;
    
    public void setMainClass(SootClass m)
    {
        mainClass = m;
        if(!m.declaresMethod(getSubSigNumberer().findOrAdd( "void main(java.lang.String[])" ))) {
        	throw new RuntimeException("Main-class has no main method!");
        }
    }
    
    Set<String> reservedNames = new HashSet<String>();
    
    /**
        Returns a set of tokens which are reserved.  Any field, class, method, or local variable with such a name will be quoted.
     */
     
    public Set<String> getReservedNames()
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
    
    public boolean hasMainClass() {
        if(mainClass == null) {
        	setMainClassFromOptions();
        }
        return mainClass!=null;
    }
    
    public SootClass getMainClass()
    {
        if(!hasMainClass())
            throw new RuntimeException("There is no main class set!");
            
        return mainClass;
    }
    public SootMethod getMainMethod() {
        if(!hasMainClass()) {
            throw new RuntimeException("There is no main class set!");
        } 
        if (!mainClass.declaresMethod ("main", Collections.<Type>singletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ), VoidType.v())) {
            throw new RuntimeException("Main class declares no main method!");
        }
        return mainClass.getMethod ("main", Collections.<Type>singletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ), VoidType.v());   
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

            String defaultSootClassPath = defaultClassPath();
	
	        //if no classpath is given on the command line, take the default
	        if( sootClassPath == null ) {
	        	sootClassPath = defaultSootClassPath;
	        } else {
	        	//if one is given...
	            if(Options.v().prepend_classpath()) {
	            	//if the prepend flag is set, append the default classpath
	            	sootClassPath += File.pathSeparator + defaultSootClassPath;
	            } 
	            //else, leave it as it is
	        }   
	        
	        //add process-dirs
	        List<String> process_dir = Options.v().process_dir();
	        StringBuffer pds = new StringBuffer();
	        for (String path : process_dir) {
	        	if(!sootClassPath.contains(path)) {
		        	pds.append(path);
		        	pds.append(File.pathSeparator);
	        	}
			}
	        sootClassPath = pds + sootClassPath;
        }



        return sootClassPath;
    }

    /**
     * Returns the max Android API version number available
     * in directory 'dir'
     * @param dir
     * @return
     */
    private int getMaxAPIAvailable(String dir) {
        File d = new File(dir);
        if (!d.exists())
        	throw new RuntimeException("The Android platform directory you have"
        			+ "specified (" + dir + ") does not exist. Please check.");
        
        File[] files = d.listFiles();
        int maxApi = -1;
        for (File f: files) {
            String name = f.getName();
            if (f.isDirectory() && name.startsWith("android-")) {
                int v = Integer.decode(name.split("android-")[1]);
                if (v > maxApi)
                    maxApi = v;
            }
        }
        return maxApi;
    }

	public String getAndroidJarPath(String jars, String apk) {
		File jarsF = new File(jars);
		File apkF = new File(apk);

		int APIVersion = -1;
		String jarPath = "";

		int maxAPI = getMaxAPIAvailable(jars);

		if (!jarsF.exists())
			throw new RuntimeException("file '" + jars + "' does not exist!");

		if (!apkF.exists())
			throw new RuntimeException("file '" + apk + "' does not exist!");

		// get AndroidManifest
		InputStream manifestIS = null;
		ZipFile archive = null;
		try {
		try {
			archive = new ZipFile(apkF);
			for (@SuppressWarnings("rawtypes") Enumeration entries = archive.entries(); entries.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String entryName = entry.getName();
				// We are dealing with the Android manifest
				if (entryName.equals("AndroidManifest.xml")) {
					manifestIS = archive.getInputStream(entry);
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error when looking for manifest in apk: " + e);
		}
		final int defaultSdkVersion = 15;
		if (manifestIS == null) {
			G.v().out.println("Could not find sdk version in Android manifest! Using default: "+defaultSdkVersion);
			APIVersion = defaultSdkVersion;
		} else {

			// process AndroidManifest.xml
			int sdkTargetVersion = -1;
			int minSdkVersion = -1;
			try {
				AXmlResourceParser parser = new AXmlResourceParser();
				parser.open(manifestIS);
				int depth = 0;
				while (true) {
					int type = parser.next();
					if (type == XmlPullParser.END_DOCUMENT) {
						// throw new RuntimeException
						// ("target sdk version not found in Android manifest ("+
						// apkF +")");
						break;
					}
					switch (type) {
					case XmlPullParser.START_DOCUMENT: {
						break;
					}
					case XmlPullParser.START_TAG: {
						depth++;
						String tagName = parser.getName();
						if (depth == 2 && tagName.equals("uses-sdk")) {
							for (int i = 0; i != parser.getAttributeCount(); ++i) {
								String attributeName = parser.getAttributeName(i);
								String attributeValue = AXMLPrinter.getAttributeValue(parser, i);
								if (attributeName.equals("targetSdkVersion")) {
									sdkTargetVersion = Integer.parseInt(attributeValue);
								} else if (attributeName.equals("minSdkVersion")) {
									minSdkVersion = Integer.parseInt(attributeValue);
								}
							}
						}
						break;
					}
					case XmlPullParser.END_TAG:
						depth--;
						break;
					case XmlPullParser.TEXT:
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (sdkTargetVersion != -1) {
			    if (sdkTargetVersion > maxAPI
			            && minSdkVersion != -1
			            && minSdkVersion <= maxAPI) {
			        G.v().out.println("warning: Android API version '"+ sdkTargetVersion +"' not available, using minApkVersion '"+ minSdkVersion +"' instead");
			        APIVersion = minSdkVersion;
			    } else {
			        APIVersion = sdkTargetVersion;
			    }
			} else if (minSdkVersion != -1) {
				APIVersion = minSdkVersion;
			} else {
				G.v().out.println("Could not find sdk version in Android manifest! Using default: "+defaultSdkVersion);
				APIVersion = defaultSdkVersion;
			}
			
			if (APIVersion <= 2)
					APIVersion = 3;
		}

		// get path to appropriate android.jar
		jarPath = jars + File.separator + "android-" + APIVersion + File.separator + "android.jar";

		// check that jar exists
		File f = new File(jarPath);
		if (!f.isFile())
		    throw new RuntimeException("error: target android.jar ("+ jarPath +") does not exist.");

		return jarPath;
		}
		finally {
			if (archive != null)
				try {
					archive.close();
				} catch (IOException e) {
					throw new RuntimeException("Error when looking for manifest in apk: " + e);
				}
		}
	}

	public String defaultClassPath() {
		StringBuffer sb = new StringBuffer();
        if(System.getProperty("os.name").equals("Mac OS X")) {
	        //in older Mac OS X versions, rt.jar was split into classes.jar and ui.jar
	        sb.append(System.getProperty("java.home"));
	        sb.append(File.separator);
	        sb.append("..");
	        sb.append(File.separator);
	        sb.append("Classes");
	        sb.append(File.separator);
	        sb.append("classes.jar");
	
	        sb.append(File.pathSeparator);
	        sb.append(System.getProperty("java.home"));
	        sb.append(File.separator);
	        sb.append("..");
	        sb.append(File.separator);
	        sb.append("Classes");
	        sb.append(File.separator);
	        sb.append("ui.jar");
	        sb.append(File.pathSeparator);
        }
        
        sb.append(System.getProperty("java.home"));
        sb.append(File.separator);
        sb.append("lib");
        sb.append(File.separator);
        sb.append("rt.jar");
        
		if(Options.v().whole_program() || Options.v().output_format()==Options.output_format_dava) {
			//add jce.jar, which is necessary for whole program mode
			//(java.security.Signature from rt.jar import javax.crypto.Cipher from jce.jar            	
			sb.append(File.pathSeparator+
				System.getProperty("java.home")+File.separator+"lib"+File.separator+"jce.jar");
		}

		String defaultClassPath = sb.toString();
		
		if (Options.v().src_prec() == Options.src_prec_apk) {
			// check that android.jar is not in classpath
			if (!defaultClassPath.contains ("android.jar")) {
				String androidJars = Options.v().android_jars();
				String forceAndroidJar = Options.v().force_android_jar();
				if (androidJars.equals("") && forceAndroidJar.equals("")) {
					throw new RuntimeException("You are analyzing an Android application but did not define android.jar. Options -android-jars or -force-android-jar should be used.");
				}

				String jarPath = "";
				if (!forceAndroidJar.equals("")) {
					jarPath = forceAndroidJar;
				} else if (!androidJars.equals("")) {
					List<String> classPathEntries = new LinkedList<String>(Arrays.asList(Options.v().soot_classpath().split(File.pathSeparator)));
					classPathEntries.addAll(Options.v().process_dir());
					Set<String> targetApks = new HashSet<String>();
					for (String entry : classPathEntries) {
						if(entry.toLowerCase().endsWith(".apk"))	// on Windows, file names are case-insensitive
							targetApks.add(entry);
					}					
					if (targetApks.size() == 0)
						throw new RuntimeException("no apk file given");
					else if (targetApks.size() > 1)
						throw new RuntimeException("only one Android application can be analyzed when using option -android-jars.");
					jarPath = getAndroidJarPath (androidJars, (String)targetApks.toArray()[0]);
				}
				if (jarPath.equals(""))
					throw new RuntimeException("android.jar not found.");
				File f = new File (jarPath);
				if (!f.exists())
					throw new RuntimeException("file '"+ jarPath +"' does not exist!");
				else
					G.v().out.println("Using '"+ jarPath +"' as android.jar");
				defaultClassPath = jarPath + File.pathSeparator + defaultClassPath;

			} else {
				G.v().out.println("warning: defaultClassPath contains android.jar! Options -android-jars and -force-android-jar are ignored!");
			}
		}

		return defaultClassPath;
	}


    private int stateCount;
    public int getState() { return this.stateCount; }
    private void modifyHierarchy() {
        stateCount++;
        activeHierarchy = null;
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
    
    public boolean containsType(String className)
    {
        return nameToClass.containsKey(className);
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
        
        setPhantomRefs(true);
        //SootResolver resolver = new SootResolver();
        if( !getPhantomRefs() 
        && SourceLocator.v().getClassSource(className) == null ) {
            setPhantomRefs(false);
            return null;
        }
        SootResolver resolver = SootResolver.v();
        SootClass toReturn = resolver.resolveClass(className, desiredLevel);
        setPhantomRefs(false);

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
        
        setPhantomRefs(true);
        //SootResolver resolver = new SootResolver();
        SootResolver resolver = SootResolver.v();
        SootClass toReturn = resolver.resolveClass(className, desiredLevel);
        setPhantomRefs(false);

        return toReturn;
        
        /*
        if(Options.v().time())
            Main.v().resolveTimer.end(); */
    }
    
    /**
     * Returns the RefType with the given className.  
     * @throws IllegalStateException if the RefType for this class cannot be found.
     * Use {@link #containsType(String)} to check if type is registered
     */
    public RefType getRefType(String className) 
    {
        RefType refType = (RefType) nameToClass.get(className);
        if(refType==null) {
        	throw new IllegalStateException("RefType "+className+" not loaded. " +
        			"If you tried to get the RefType of a library class, did you call loadNecessaryClasses()? " +
        			"Otherwise please check Soot's classpath.");
        }
		return refType;
    }
    
    /**
     * Returns the {@link RefType} for {@link Object}.
     */
    public RefType getObjectType() {
    	return getRefType("java.lang.Object");
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

	public SootClass getSootClass(String className) {
		RefType type = (RefType) nameToClass.get(className);
		SootClass toReturn = null;
		if (type != null)
			toReturn = type.getSootClass();

		if (toReturn != null) {
			return toReturn;
		} else if (allowsPhantomRefs() ||
				   className.equals(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME)) {
			SootClass c = new SootClass(className);
			addClass(c);
            c.setPhantom(true);
			return c;
		} else {
			throw new RuntimeException(System.getProperty("line.separator")
					+ "Aborting: can't find classfile " + className);
		}
	}

    /**
     * Returns an backed chain of the classes in this manager.
     */
     
    public Chain<SootClass> getClasses()
    {
        return classes;
    }

    /* The four following chains are mutually disjoint. */

    /**
     * Returns a chain of the application classes in this scene.
     * These classes are the ones which can be freely analysed & modified.
     */
    public Chain<SootClass> getApplicationClasses()
    {
        return applicationClasses;
    }

    /**
     * Returns a chain of the library classes in this scene.
     * These classes can be analysed but not modified.
     */
    public Chain<SootClass> getLibraryClasses()
    {
        return libraryClasses;
    }

    /**
     * Returns a chain of the phantom classes in this scene.
     * These classes are referred to by other classes, but cannot be loaded.
     */
    public Chain<SootClass> getPhantomClasses()
    {
        return phantomClasses;
    }

    Chain<SootClass> getContainingChain(SootClass c)
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

    public boolean hasCustomEntryPoints() {
    	return entryPoints!=null;
    }
    
    /** Get the set of entry points that are used to build the call graph. */
    public List<SootMethod> getEntryPoints() {
        if( entryPoints == null ) {
            entryPoints = EntryPoints.v().all();
        }
        return entryPoints;
    }

    /** Change the set of entry point methods used to build the call graph. */
    public void setEntryPoints( List<SootMethod> entryPoints ) {
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
                    getCallGraph(), new ArrayList<MethodOrMethodContext>(getEntryPoints()) );
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
        //if( !Options.v().allow_phantom_refs() ) return false;
        //return allowsPhantomRefs;
    	return Options.v().allow_phantom_refs();
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
    public ArrayNumberer<Type> getTypeNumberer() { return typeNumberer; }
    public ArrayNumberer<SootMethod> getMethodNumberer() { return methodNumberer; }
    public Numberer getContextNumberer() { return contextNumberer; }
    public Numberer getUnitNumberer() { return unitNumberer; }
    public ArrayNumberer getFieldNumberer() { return fieldNumberer; }
    public ArrayNumberer<SootClass> getClassNumberer() { return classNumberer; }
    public StringNumberer getSubSigNumberer() { return subSigNumberer; }
    public ArrayNumberer<Local> getLocalNumberer() { return localNumberer; }

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
     * @param ta the default {@link ThrowAnalysis}.
     */
    public void setDefaultThrowAnalysis(ThrowAnalysis ta) 
    {
	defaultThrowAnalysis = ta;
    }

    private void setReservedNames()
    {
        Set<String> rn = getReservedNames();        
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
        rn.add("annotation");
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

    private final Set<String>[] basicclasses=new Set[4];

    private void addSootBasicClasses() {
        basicclasses[SootClass.HIERARCHY] = new HashSet<String>();
        basicclasses[SootClass.SIGNATURES] = new HashSet<String>();
        basicclasses[SootClass.BODIES] = new HashSet<String>();

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
    	addReflectionTraceClasses();
    	
		for(int i=SootClass.BODIES;i>=SootClass.HIERARCHY;i--) {
		    for(String name: basicclasses[i]) {
		    	tryLoadClass(name,i);
		    }
		}
    }
    
    public Set<String> getBasicClasses() {
    	Set<String> all = new HashSet<String>();
    	for(int i=0;i<basicclasses.length;i++) {
    		Set<String> classes = basicclasses[i];
    		if(classes!=null)
    			all.addAll(classes);
    	}
		return all; 
	}

    private void addReflectionTraceClasses() {
    	CGOptions options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
    	String log = options.reflection_log();
    	
    	Set<String> classNames = new HashSet<String>();
    	if(log!=null && log.length()>0) {
			BufferedReader reader = null;
			String line="";
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
				while((line=reader.readLine())!=null) {
					if(line.length()==0) continue;
					String[] portions = line.split(";",-1);
					String kind = portions[0];
					String target = portions[1];
					String source = portions[2];
					String sourceClassName = source.substring(0,source.lastIndexOf("."));
					classNames.add(sourceClassName);
					if(kind.equals("Class.forName")) {
						classNames.add(target);
					} else if(kind.equals("Class.newInstance")) {
						classNames.add(target);
					} else if(kind.equals("Method.invoke") || kind.equals("Constructor.newInstance")) {
						classNames.add(signatureToClass(target));
					} else if(kind.equals("Field.set*") || kind.equals("Field.get*")) {
						classNames.add(signatureToClass(target));
					} else throw new RuntimeException("Unknown entry kind: "+kind);
				}
			} catch (Exception e) {
				throw new RuntimeException("Line: '"+line+"'", e);
			}
			finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
			}
    	}
    	
    	for (String c : classNames) {
    		addBasicClass(c, SootClass.BODIES);
		}
	}

	private List<SootClass> dynamicClasses;
    public Collection<SootClass> dynamicClasses() {
    	if(dynamicClasses==null) {
    		throw new IllegalStateException("Have to call loadDynamicClasses() first!");
    	}
        return dynamicClasses;
    }

    private void loadNecessaryClass(String name) {
        SootClass c;
        c = loadClassAndSupport(name);
        c.setApplicationClass();
    }
    /** Load the set of classes that soot needs, including those specified on the
     *  command-line. This is the standard way of initialising the list of
     *  classes soot should use.
     */
    public void loadNecessaryClasses() {
	loadBasicClasses();

        Iterator<String> it = Options.v().classes().iterator();

        while (it.hasNext()) {
            String name = (String) it.next();
            loadNecessaryClass(name);
        }

        loadDynamicClasses();

        if(Options.v().oaat()) {
        	if(Options.v().process_dir().isEmpty()) {
        		throw new IllegalArgumentException("If switch -oaat is used, then also -process-dir must be given.");
        	}
        } else {
	        for( Iterator<String> pathIt = Options.v().process_dir().iterator(); pathIt.hasNext(); ) {
	
	            final String path = (String) pathIt.next();
	            for (String cl : SourceLocator.v().getClassesUnder(path)) {
	                loadClassAndSupport(cl).setApplicationClass();
	            }
	        }
        }

        prepareClasses();
        setDoneResolving();
    }

    public void loadDynamicClasses() {
        dynamicClasses = new ArrayList<SootClass>();
        HashSet<String> dynClasses = new HashSet<String>();
        dynClasses.addAll(Options.v().dynamic_class());

        for( Iterator<String> pathIt = Options.v().dynamic_dir().iterator(); pathIt.hasNext(); ) {

            final String path = (String) pathIt.next();
            dynClasses.addAll(SourceLocator.v().getClassesUnder(path));
        }

        for( Iterator<String> pkgIt = Options.v().dynamic_package().iterator(); pkgIt.hasNext(); ) {

            final String pkg = (String) pkgIt.next();
            dynClasses.addAll(SourceLocator.v().classesInDynamicPackage(pkg));
        }

        for (String className : dynClasses) {

            dynamicClasses.add( loadClassAndSupport(className) );
        }
        
        //remove non-concrete classes that may accidentally have been loaded
        for (Iterator<SootClass> iterator = dynamicClasses.iterator(); iterator.hasNext();) {
			SootClass c = iterator.next();
			if(!c.isConcrete()) {
				if(Options.v().verbose()) {
					G.v().out.println("Warning: dynamic class "+c.getName()+" is abstract or an interface, and it will not be considered.");
				}
				iterator.remove();
			}
		}
    }


    /* Generate classes to process, adding or removing package marked by
     * command line options. 
     */
    private void prepareClasses() {
        // Remove/add all classes from packageInclusionMask as per -i option
        Chain<SootClass> processedClasses = new HashChain<SootClass>();
        while(true) {
            Chain<SootClass> unprocessedClasses = new HashChain<SootClass>(getClasses());
            unprocessedClasses.removeAll(processedClasses);
            if( unprocessedClasses.isEmpty() ) break;
            processedClasses.addAll(unprocessedClasses);
            for (SootClass s : unprocessedClasses) {
                if( s.isPhantom() ) continue;
                if(Options.v().app()) {
                    s.setApplicationClass();
                }
                if (Options.v().classes().contains(s.getName())) {
                    s.setApplicationClass();
                    continue;
                }
                for( Iterator<String> pkgIt = excludedPackages.iterator(); pkgIt.hasNext(); ) {
                    final String pkg = (String) pkgIt.next();
                    if (s.isApplicationClass()
                    && (s.getPackageName()+".").startsWith(pkg)) {
                            s.setLibraryClass();
                    }
                }
                for( Iterator<String> pkgIt = Options.v().include().iterator(); pkgIt.hasNext(); ) {
                    final String pkg = (String) pkgIt.next();
                    if ((s.getPackageName()+".").startsWith(pkg))
                        s.setApplicationClass();
                }
                if(s.isApplicationClass()) {
                    // make sure we have the support
                    loadClassAndSupport(s.getName());
                }
            }
        }
    }

	public boolean isExcluded(SootClass sc) {
		String name = sc.getName();
		for (String pkg : excludedPackages) {
			if (name.startsWith(pkg)) {
				for (String inc : (List<String>) Options.v().include()) {
					if (name.startsWith(inc)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	List<String> pkgList;

    public void setPkgList(List<String> list){
        pkgList = list;
    }

    public List<String> getPkgList(){
        return pkgList;
    }


    /** Create an unresolved reference to a method. */
    public SootMethodRef makeMethodRef( 
            SootClass declaringClass,
            String name,
            List<Type> parameterTypes,
            Type returnType,
            boolean isStatic ) {
        return new SootMethodRefImpl(declaringClass, name, parameterTypes,
                returnType, isStatic);
    }

    /** Create an unresolved reference to a constructor. */
    public SootMethodRef makeConstructorRef( 
            SootClass declaringClass,
            List<Type> parameterTypes) {
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
    public List<SootClass> getClasses(int desiredLevel) {
        List<SootClass> ret = new ArrayList<SootClass>();
        for( Iterator<SootClass> clIt = getClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            if( cl.resolvingLevel() >= desiredLevel ) ret.add(cl);
        }
        return ret;
    }
    private boolean doneResolving = false;
	private boolean incrementalBuild;
	protected LinkedList<String> excludedPackages;
    public boolean doneResolving() { return doneResolving; }
    public void setDoneResolving() { doneResolving = true; }
    public void setMainClassFromOptions() {
        if(mainClass != null) return;
        if( Options.v().main_class() != null
                && Options.v().main_class().length() > 0 ) {
            setMainClass(getSootClass(Options.v().main_class()));
        } else {             	
        	// try to infer a main class from the command line if none is given 
        	for (Iterator<String> classIter = Options.v().classes().iterator(); classIter.hasNext();) {
                    SootClass c = getSootClass(classIter.next());
                    if (c.declaresMethod ("main", Collections.<Type>singletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ), VoidType.v()))
                    {
                        G.v().out.println("No main class given. Inferred '"+c.getName()+"' as main class.");					
                        setMainClass(c);
                        return;
                    }
            }
        	
        	// try to infer a main class from the usual classpath if none is given 
        	for (Iterator<SootClass> classIter = getApplicationClasses().iterator(); classIter.hasNext();) {
                    SootClass c = (SootClass) classIter.next();
                    if (c.declaresMethod ("main", Collections.<Type>singletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ), VoidType.v()))
                    {
                        G.v().out.println("No main class given. Inferred '"+c.getName()+"' as main class.");					
                        setMainClass(c);
                        return;
                    }
            }
        }
    }
    
    /**
     * This method returns true when in incremental build mode.
     * Other classes can query this flag and change the way in which they use the Scene,
     * depending on the flag's value.
     */
    public boolean isIncrementalBuild() {
    	return incrementalBuild;
    }
    
    public void initiateIncrementalBuild() {
    	this.incrementalBuild = true;
    }

    public void incrementalBuildFinished() {
    	this.incrementalBuild = false;
    }
    
    /*
     * Forces Soot to resolve the class with the given name to the given level,
     * even if resolving has actually already finished.
     */
    public SootClass forceResolve(String className, int level) {
    	boolean tmp = doneResolving;
    	doneResolving = false;
    	SootClass c;
    	try {
			c = SootResolver.v().resolveClass(className, level);
    	} finally {
    		doneResolving = tmp;
    	}    	    	
    	return c;
    }
}

