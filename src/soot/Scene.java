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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParser;

import soot.dexpler.DalvikThrowAnalysis;
import soot.jimple.spark.internal.ClientAccessibilityOracle;
import soot.jimple.spark.internal.PublicAndProtectedAccessibility;
import soot.jimple.spark.pag.SparkField;
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
	
	private final int defaultSdkVersion = 15;
	private final Map<String, Integer> maxAPIs = new HashMap<String, Integer>();

	public Scene ( Singletons.Global g )
    {
    	setReservedNames();
    	
        // load soot.class.path system property, if defined
        String scp = System.getProperty("soot.class.path");

        if (scp != null)
            setSootClassPath(scp);

        kindNumberer = new ArrayNumberer<Kind>(new Kind[] {
        	Kind.INVALID,
        	Kind.STATIC,
        	Kind.VIRTUAL,
        	Kind.INTERFACE,
        	Kind.SPECIAL,
        	Kind.CLINIT,
        	Kind.THREAD,
        	Kind.EXECUTOR,
        	Kind.ASYNCTASK,
        	Kind.FINALIZE,
        	Kind.INVOKE_FINALIZE,
        	Kind.PRIVILEGED,
        	Kind.NEWINSTANCE});
        
        addSootBasicClasses();
        
        determineExcludedPackages();
    }
	private void determineExcludedPackages() {
		excludedPackages = new LinkedList<String>();
        if (Options.v().exclude() != null)
            excludedPackages.addAll(Options.v().exclude());

        // do not kill contents of the APK if we want a working new APK afterwards
        if( !Options.v().include_all()
        		&& Options.v().output_format() != Options.output_format_dex
        		&& Options.v().output_format() != Options.output_format_force_dex) {
            excludedPackages.add("java.*");
            excludedPackages.add("sun.*");
            excludedPackages.add("javax.*");
            excludedPackages.add("com.sun.*");
            excludedPackages.add("com.ibm.*");
            excludedPackages.add("org.xml.*");
            excludedPackages.add("org.w3c.*");
            excludedPackages.add("apple.awt.*");
            excludedPackages.add("com.apple.*");
        }
	}
    public static Scene  v() { return G.v().soot_Scene (); }
    
    Chain<SootClass> classes = new HashChain<SootClass>();
    Chain<SootClass> applicationClasses = new HashChain<SootClass>();
    Chain<SootClass> libraryClasses = new HashChain<SootClass>();
    Chain<SootClass> phantomClasses = new HashChain<SootClass>();
    
    private final Map<String,RefType> nameToClass = new HashMap<String,RefType>();

    final ArrayNumberer<Kind> kindNumberer;
    ArrayNumberer<Type> typeNumberer = new ArrayNumberer<Type>();
    ArrayNumberer<SootMethod> methodNumberer = new ArrayNumberer<SootMethod>();
    Numberer<Unit> unitNumberer = new MapNumberer<Unit>();
    Numberer<Context> contextNumberer = null;
    Numberer<SparkField> fieldNumberer = new ArrayNumberer<SparkField>();
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
    private ClientAccessibilityOracle accessibilityOracle;

    boolean allowsPhantomRefs = false;

    SootClass mainClass;
    String sootClassPath = null;

    // Two default values for constructing ExceptionalUnitGraphs:
    private ThrowAnalysis defaultThrowAnalysis = null;
    
    private int androidAPIVersion = -1;
    
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
     * If this name is in the set of reserved names, then return a quoted
     * version of it.  Else pass it through. If the name consists of multiple
     * parts separated by dots, the individual names are checked as well.
     */
    public String quotedNameOf(String s)
    {
    	// Pre-check: Is there a chance that we need to escape something?
    	// If not, skip the transformation altogether.
    	boolean found = s.contains("-");
    	for (String token : reservedNames)
    		if (s.contains(token)) {
    			found = true;
    			break;
    		}
    	if (!found)
    		return s;
    	
    	StringBuilder res = new StringBuilder(s.length());
    	for (String part : s.split("\\.")) {
    		if (res.length() > 0)
    			res.append('.');
	        if(part.startsWith("-") || reservedNames.contains(part)) {
	            res.append('\'');
	            res.append(part);
	            res.append('\'');
	        }
	        else
	            res.append(part);
    	}
    	return res.toString();
    }
    
    /**
     * This method is the inverse of quotedNameOf(). It takes a possible escaped
     * class and reconstructs the original version of it.
     * @param s The possibly escaped name
     * @return The original, non-escaped name
     */
    public String unescapeName(String s) {
    	// If the name is not escaped, there is nothing to do here
    	if (!s.contains("'"))
    		return s;
    	
    	StringBuilder res = new StringBuilder(s.length());
    	for (String part : s.split("\\.")) {
    		if (res.length() > 0)
    			res.append('.');
	        if(part.startsWith("'") && part.endsWith("'")) {
	            res.append(part.substring(1, part.length() - 1));
	        }
	        else
	            res.append(part);
    	}
    	return res.toString();
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
        
        SootMethod mainMethod = mainClass.getMethodUnsafe("main",
        		Collections.<Type>singletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ),
        		VoidType.v());
        if (mainMethod == null) {
            throw new RuntimeException("Main class declares no main method!");
        }
        return mainMethod;   
    }
    
    
    public void setSootClassPath(String p)
    {
        sootClassPath = p;
        SourceLocator.v().invalidateClassPath();
    }
    
    public void extendSootClassPath(String newPathElement){
    	sootClassPath += File.pathSeparator + newPathElement;
    	SourceLocator.v().extendClassPath(newPathElement);
    }
    
    public String getSootClassPath()
    {
        if( sootClassPath == null ) {
            String optionscp = Options.v().soot_classpath();
            if( optionscp != null && optionscp.length() > 0 )
                sootClassPath = optionscp;
            
	        //if no classpath is given on the command line, take the default
	        if( sootClassPath == null || sootClassPath.isEmpty() ) {
	        	sootClassPath = defaultClassPath();
	        } else {
	        	//if one is given...
	            if(Options.v().prepend_classpath()) {
	            	//if the prepend flag is set, append the default classpath
	            	sootClassPath += File.pathSeparator + defaultClassPath();
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
    	Integer mapi = this.maxAPIs.get(dir);
    	if (mapi != null)
    		return mapi;
    	
        File d = new File(dir);
        if (!d.exists())
        	throw new RuntimeException("The Android platform directory you have"
        			+ "specified (" + dir + ") does not exist. Please check.");
        
        File[] files = d.listFiles();
        if (files == null)
        	return -1;
        
        int maxApi = -1;
        for (File f: files) {
            String name = f.getName();
            if (f.isDirectory() && name.startsWith("android-")) {
            	try {
                int v = Integer.decode(name.split("android-")[1]);
                if (v > maxApi)
                    maxApi = v;
            	}
            	catch (NumberFormatException ex) {
            		// We simply ignore directories that do not follow the
            		// Android naming structure
            	}
            }
        }
        this.maxAPIs.put(dir, maxApi);
        return maxApi;
    }

	public String getAndroidJarPath(String jars, String apk) {
		int APIVersion = getAndroidAPIVersion(jars,apk);

		String jarPath = jars + File.separator + "android-" + APIVersion + File.separator + "android.jar";

		// check that jar exists
		File f = new File(jarPath);
		if (!f.isFile())
		    throw new RuntimeException("error: target android.jar ("+ jarPath +") does not exist.");

		return jarPath;
	}
	
	public int getAndroidAPIVersion() {
		return androidAPIVersion > 0 ? androidAPIVersion : (Options.v().android_api_version() > 0 
				? Options.v().android_api_version() : defaultSdkVersion);
	}

	private int getAndroidAPIVersion(String jars, String apk) {
		// Do we already have an API version?
		if (androidAPIVersion > 0)
			return androidAPIVersion;
		
		// get path to appropriate android.jar
		File jarsF = new File(jars);
		File apkF = apk == null ? null : new File(apk);

		if (!jarsF.exists())
			throw new RuntimeException("file '" + jars + "' does not exist!");

		if (apkF != null && !apkF.exists())
			throw new RuntimeException("file '" + apk + "' does not exist!");

		// Use the default if we don't have any other information
		androidAPIVersion = defaultSdkVersion;

		// Do we have an explicit API version?
		if (Options.v().android_api_version() > 0)
			androidAPIVersion = Options.v().android_api_version();
		// Look into the manifest file
		else if (apk != null)
			if (apk.toLowerCase().endsWith(".apk"))
				androidAPIVersion = getTargetSDKVersion(apk, jars);
		
		// If we don't have that API version installed, we take the most recent
		// one we have
		final int maxAPI = getMaxAPIAvailable(jars);
		if (androidAPIVersion > maxAPI)
			androidAPIVersion = maxAPI;
		
		return androidAPIVersion;
	}
	
	private int getTargetSDKVersion(String apkFile, String platformJARs) {
		// get AndroidManifest
		InputStream manifestIS = null;
		ZipFile archive = null;
		try {
			try {
				archive = new ZipFile(apkFile);
				for (Enumeration<? extends ZipEntry> entries = archive.entries(); entries.hasMoreElements();) {
					ZipEntry entry = entries.nextElement();
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
		
		if (manifestIS == null) {
			G.v().out.println("Could not find sdk version in Android manifest! Using default: "+defaultSdkVersion);
			return defaultSdkVersion;
		}
		
		// process AndroidManifest.xml
		int maxAPI = getMaxAPIAvailable(platformJARs);
		int sdkTargetVersion = -1;
		int minSdkVersion = -1;
		int platformBuildVersionCode = -1;
		try {
			AXmlResourceParser parser = new AXmlResourceParser();
			parser.open(manifestIS);
			int depth = 0;
			loop: while (true) {
				int type = parser.next();
				switch (type) {
					case XmlPullParser.START_DOCUMENT: {
						break;
					}
					case XmlPullParser.END_DOCUMENT:
						break loop;
					case XmlPullParser.START_TAG: {
						depth++;
						String tagName = parser.getName();
						if (depth == 1 && tagName.equals("manifest")) {
							for (int i = 0; i != parser.getAttributeCount(); ++i) {
								String attributeName = parser.getAttributeName(i);
								String attributeValue = AXMLPrinter.getAttributeValue(parser, i);
								if (attributeName.equals("platformBuildVersionCode")) {
									platformBuildVersionCode = Integer.parseInt(attributeValue);
								}
							}
						}
						else if (depth == 2 && tagName.equals("uses-sdk")) {
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
		
			int APIVersion = -1;
			if (sdkTargetVersion != -1) {
			    if (sdkTargetVersion > maxAPI
			            && minSdkVersion != -1
			            && minSdkVersion <= maxAPI) {
			        G.v().out.println("warning: Android API version '"+ sdkTargetVersion +"' not available, using minApkVersion '"+ minSdkVersion +"' instead");
			        APIVersion = minSdkVersion;
			    } else {
			        APIVersion = sdkTargetVersion;
			    }
			} else if (platformBuildVersionCode != -1) {
			    if (platformBuildVersionCode > maxAPI
			            && minSdkVersion != -1
			            && minSdkVersion <= maxAPI) {
			        G.v().out.println("warning: Android API version '"+ platformBuildVersionCode +"' not available, using minApkVersion '"+ minSdkVersion +"' instead");
			        APIVersion = minSdkVersion;
			    } else {
			        APIVersion = platformBuildVersionCode;
			    }
			} else if (minSdkVersion != -1) {
				APIVersion = minSdkVersion;
			} else {
				G.v().out.println("Could not find sdk version in Android manifest! Using default: "+defaultSdkVersion);
				APIVersion = defaultSdkVersion;
			}
			
			if (APIVersion <= 2)
					APIVersion = 3;
			return APIVersion;
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
		// If we have an apk file on the process dir and do not have a src-prec option
		// that loads APK files, we give a warning
		if (Options.v().src_prec() != Options.src_prec_apk) {
			for (String entry : Options.v().process_dir()) {
				if (entry.toLowerCase().endsWith(".apk")) {
					System.err.println("APK file on process dir, but chosen src-prec does not support loading APKs");
					break;
				}
			}
		}
		
		if (Options.v().src_prec() == Options.src_prec_apk)
			return defaultAndroidClassPath();
		else
			return defaultJavaClassPath();
	}


    private String defaultAndroidClassPath() {
		// check that android.jar is not in classpath
		String androidJars = Options.v().android_jars();
		String forceAndroidJar = Options.v().force_android_jar();
		if ((androidJars == null || androidJars.equals(""))
				&& (forceAndroidJar == null || forceAndroidJar.equals(""))) {
			throw new RuntimeException("You are analyzing an Android application but did "
					+ "not define android.jar. Options -android-jars or -force-android-jar should be used.");
		}
		
		// Get the platform JAR file. It either directly specified, or
		// we detect it from the target version of the APK we are
		// analyzing
		String jarPath = "";
		if (forceAndroidJar != null && !forceAndroidJar.isEmpty()) {
			jarPath = forceAndroidJar;
			
			if (Options.v().android_api_version() > 0)
				androidAPIVersion = Options.v().android_api_version();
			else if (forceAndroidJar.contains("android-")) {
				Pattern pt = Pattern.compile("\\" + File.separatorChar + "android-(\\d+)"
						+ "\\" + File.separatorChar);
				Matcher m = pt.matcher(forceAndroidJar);
				if (m.find())
					androidAPIVersion = Integer.valueOf(m.group(1));
			}
			else
				androidAPIVersion = defaultSdkVersion;
		}
		else if (androidJars != null && !androidJars.isEmpty()) {
			List<String> classPathEntries = new LinkedList<String>(Arrays.asList(
					Options.v().soot_classpath().split(File.pathSeparator)));
			classPathEntries.addAll(Options.v().process_dir());
			
			String targetApk = "";
			Set<String> targetDexs = new HashSet<String>();
			for (String entry : classPathEntries) {
				if(entry.toLowerCase().endsWith(".apk")) {	// on Windows, file names are case-insensitive
					// We cannot have multiple APKs, because this would give us multiple
					// manifests which we do not support right now
					if (targetApk != null && !targetApk.isEmpty())
						throw new RuntimeException("only one Android application can be analyzed when using option -android-jars.");
					targetApk = entry;
				}
				if(entry.toLowerCase().endsWith(".dex"))	// on Windows, file names are case-insensitive
					targetDexs.add(entry);
			}
			
			// We need at least one file to process
			if (targetApk == null || targetApk.isEmpty()) {
				if (targetDexs.isEmpty())
					throw new RuntimeException("no apk file given");
				jarPath = getAndroidJarPath(androidJars, null);
			}
			else
				jarPath = getAndroidJarPath(androidJars, targetApk);
		}
		
		// We must have a platform JAR file when analyzing Android apps
		if (jarPath.equals(""))
			throw new RuntimeException("android.jar not found.");
		
		// Check the platform JAR file
		File f = new File (jarPath);
		if (!f.exists())
			throw new RuntimeException("file '"+ jarPath +"' does not exist!");
		else
			G.v().out.println("Using '"+ jarPath +"' as android.jar");
		
		return jarPath;
	}
    
	private String defaultJavaClassPath() {
		StringBuilder sb = new StringBuilder();
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

        File rtJar = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar");
        if (rtJar.exists() && rtJar.isFile()) {
            // G.v().out.println("Using JRE runtime: " + rtJar.getAbsolutePath());
            sb.append(rtJar.getAbsolutePath());
        } else {
            // in case we're not in JRE environment, try JDK
            rtJar = new File(System.getProperty("java.home") + File.separator + "jre" + File.separator + "lib" + File.separator + "rt.jar");
            if (rtJar.exists() && rtJar.isFile()) {
                // G.v().out.println("Using JDK runtime: " + rtJar.getAbsolutePath());
                sb.append(rtJar.getAbsolutePath());
            } else {
                // not in JDK either
                throw new RuntimeException("Error: cannot find rt.jar.");
            }
        }

		if(Options.v().whole_program() || Options.v().output_format()==Options.output_format_dava) {
			//add jce.jar, which is necessary for whole program mode
			//(java.security.Signature from rt.jar import javax.crypto.Cipher from jce.jar            	
			sb.append(File.pathSeparator+
				System.getProperty("java.home")+File.separator+"lib"+File.separator+"jce.jar");
		}
		
		return sb.toString();
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

    /**
     * Adds the given class to the Scene. This method marks the given class
     * as a library class and invalidates the class hierarchy.
     * @param c The class to add
     */
    public void addClass(SootClass c) 
    {
    	addClassSilent(c);
        c.setLibraryClass();
       	modifyHierarchy();
    }

    /**
     * Adds the given class to the Scene. This method does not handle any
     * dependencies such as invalidating the hierarchy. The class is neither
     * marked as application class, nor library class.
     * @param c The class to add
     */
    private void addClassSilent(SootClass c) 
    {
        if(c.isInScene())
            throw new RuntimeException("already managed: "+c.getName());

        if(containsClass(c.getName()))
            throw new RuntimeException("duplicate class: "+c.getName());

        classes.add(c);
        nameToClass.put(c.getName(), c.getType());
        c.getType().setSootClass(c);
        c.setInScene(true);
        
        // Phantom classes are not really part of the hierarchy anyway, so
        // we can keep the old one
        if (!c.isPhantom)
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
        RefType type = nameToClass.get(className);
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

    public SootField grabField(String fieldSignature)
    {
        String cname = signatureToClass( fieldSignature );
        String fname = signatureToSubsignature( fieldSignature );
        if( !containsClass(cname) ) return null;
        SootClass c = getSootClass(cname);
        return c.getFieldUnsafe( fname );
    }

    public boolean containsField(String fieldSignature)
    {
        return grabField(fieldSignature) != null;
    }
    
    public SootMethod grabMethod(String methodSignature)
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
        ClassSource source = SourceLocator.v().getClassSource(className);
        try {
	        if( !getPhantomRefs() && source == null) {
	            setPhantomRefs(false);
	            return null;
	        }
        }
        finally {
        	if (source != null)
        		source.close();
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
     * Returns the RefType with the given class name or primitive type.  
     * @throws RuntimeException if the Type for this name cannot be found.
     * Use {@link #getRefTypeUnsafe(String)} to check if type is an registered RefType.
     */
    public Type getType(String arg) {
    	String type = arg.replaceAll("([^\\[\\]]*)(.*)", "$1");
    	int arrayCount = arg.contains("[") ? arg.replaceAll("([^\\[\\]]*)(.*)", "$2").length() / 2 : 0;
    	
    	Type result = getRefTypeUnsafe(type);
    	
    	if (result == null) {
    		if (type.equals("long"))
              result = LongType.v();
    		else if (type.equals("short"))
              result = ShortType.v();
    		else if (type.equals("double"))
              result = DoubleType.v();
    		else if (type.equals("int"))
              result = IntType.v();
    		else if (type.equals("float"))
              result = FloatType.v();
    		else if (type.equals("byte"))
              result = ByteType.v();
    		else if (type.equals("char"))
              result = CharType.v();
    		else if (type.equals("void"))
              result = VoidType.v();
    		else if (type.equals("boolean"))
              result = BooleanType.v();
    		else
              throw new RuntimeException("unknown type: '" + type + "'");
    	}
    	
    	if (arrayCount != 0) {
    		result = ArrayType.v(result, arrayCount);
    	}
    	return result;
    }
    
    /**
     * Returns the RefType with the given className.  
     * @throws IllegalStateException if the RefType for this class cannot be found.
     * Use {@link #containsType(String)} to check if type is registered
     */
    public RefType getRefType(String className) 
    {
        RefType refType = getRefTypeUnsafe(className);
        if(refType==null) {
        	throw new IllegalStateException("RefType "+className+" not loaded. " +
        			"If you tried to get the RefType of a library class, did you call loadNecessaryClasses()? " +
        			"Otherwise please check Soot's classpath.");
        }
		return refType;
    }
    
    /**
     * Returns the RefType with the given className. Returns null if no type
     * with the given name can be found.
     */
    public RefType getRefTypeUnsafe(String className) 
    {
        RefType refType = nameToClass.get(unescapeName(className));
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
     * Returns the SootClass with the given className. If no class with the
     * given name exists, null is returned
     * @param className The name of the class to get
     * @return The class if it exists, otherwise null  
     */
	public SootClass getSootClassUnsafe(String className) {
		RefType type = nameToClass.get(className);
		if (type != null) {
			SootClass tsc = type.getSootClass();
			if (tsc != null)
				return tsc;
		}
		
		if (allowsPhantomRefs() ||
				   className.equals(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME)) {
			SootClass c = new SootClass(className);
            c.isPhantom = true;
			addClassSilent(c);
			c.setPhantomClass();
			return c;
		}
		
		return null;
	}
	
    /**
     * Returns the SootClass with the given className.  
     */
	public SootClass getSootClass(String className) {
		SootClass sc = getSootClassUnsafe(className);
		if (sc != null)
			return sc;
		
		throw new RuntimeException(System.getProperty("line.separator")
				+ "Aborting: can't find classfile " + className);
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
    /**
     * Retrieves the active client accessibility oracle
     */
    public ClientAccessibilityOracle getClientAccessibilityOracle() {
    	if (!hasClientAccessibilityOracle()) {
    		return PublicAndProtectedAccessibility.v();
    	}
    	
    	return accessibilityOracle;
    }
    
    public boolean hasClientAccessibilityOracle() {
    	return accessibilityOracle != null;
    }
    
    public void setClientAccessibilityOracle(ClientAccessibilityOracle oracle) {
    	accessibilityOracle = oracle;
    }
    
    public void releaseClientAccessibilityOracle() {
    	accessibilityOracle = null;
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

    private ContextSensitiveCallGraph cscg = null;
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
    public Numberer<Kind> kindNumberer() { return kindNumberer; }
    public ArrayNumberer<Type> getTypeNumberer() { return typeNumberer; }
    public ArrayNumberer<SootMethod> getMethodNumberer() { return methodNumberer; }
    public Numberer<Context> getContextNumberer() { return contextNumberer; }
    public Numberer<Unit> getUnitNumberer() { return unitNumberer; }
    public Numberer<SparkField> getFieldNumberer() { return fieldNumberer; }
    public ArrayNumberer<SootClass> getClassNumberer() { return classNumberer; }
    public StringNumberer getSubSigNumberer() { return subSigNumberer; }
    public ArrayNumberer<Local> getLocalNumberer() { return localNumberer; }

    public void setContextNumberer( Numberer<Context> n ) {
        if( contextNumberer != null )
            throw new RuntimeException(
                    "Attempt to set context numberer when it is already set." );
        contextNumberer = n;
    }

	/**
	 * Returns the {@link ThrowAnalysis} to be used by default when constructing
	 * CFGs which include exceptional control flow.
	 *
	 * @return the default {@link ThrowAnalysis}
	 */
	public ThrowAnalysis getDefaultThrowAnalysis() {
		if (defaultThrowAnalysis == null) {
			int optionsThrowAnalysis = Options.v().throw_analysis();
			switch (optionsThrowAnalysis) {
			case Options.throw_analysis_pedantic:
				defaultThrowAnalysis = PedanticThrowAnalysis.v();
				break;
			case Options.throw_analysis_unit:
				defaultThrowAnalysis = UnitThrowAnalysis.v();
				break;
			case Options.throw_analysis_dalvik:
				defaultThrowAnalysis = DalvikThrowAnalysis.v();
				break;
			default:
				throw new IllegalStateException("Options.v().throw_analysi() == " + Options.v().throw_analysis());
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
        rn.add("enum");
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
        rn.add("with");
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
		addBasicClass("java.lang.NullPointerException", SootClass.SIGNATURES);
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

		addBasicClass("java.lang.invoke.LambdaMetafactory");
	}

	public void addBasicClass(String name) {
		addBasicClass(name, SootClass.HIERARCHY);
	}

	public void addBasicClass(String name, int level) {
		basicclasses[level].add(name);
	}

    /** Load just the set of basic classes soot needs, ignoring those
     *  specified on the command-line. You don't need to use both this and 
     *  loadNecessaryClasses, though it will only waste time.
     */
    public void loadBasicClasses() {
    	addReflectionTraceClasses();
    	
		for(int i=SootClass.BODIES;i>=SootClass.HIERARCHY;i--) {
		    for(String name: basicclasses[i]){
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

	private List<SootClass> dynamicClasses = null;
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
        
    	for (String name : Options.v().classes()) {
            loadNecessaryClass(name);
        }

        loadDynamicClasses();

        if(Options.v().oaat()) {
        	if(Options.v().process_dir().isEmpty()) {
        		throw new IllegalArgumentException("If switch -oaat is used, then also -process-dir must be given.");
        	}
        } else {
	        for( final String path : Options.v().process_dir() ) {
	            for (String cl : SourceLocator.v().getClassesUnder(path)) {
	            	SootClass theClass = loadClassAndSupport(cl);
	            	if (!theClass.isPhantom)
	            		theClass.setApplicationClass();
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

            final String path = pathIt.next();
            dynClasses.addAll(SourceLocator.v().getClassesUnder(path));
        }

        for( Iterator<String> pkgIt = Options.v().dynamic_package().iterator(); pkgIt.hasNext(); ) {

            final String pkg = pkgIt.next();
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
                if(s.isApplicationClass() && isExcluded(s)){
                    s.setLibraryClass();
                }
                if(isIncluded(s)){
                    s.setApplicationClass();
                }
                if(s.isApplicationClass()) {
                    // make sure we have the support
                    loadClassAndSupport(s.getName());
                }
            }
        }
    }
    
    public boolean isExcluded(SootClass sc){
        String name = sc.getName();
        for (String pkg : excludedPackages) {
        	if(name.equals(pkg) || ((pkg.endsWith(".*") || pkg.endsWith("$*")) && name.startsWith(pkg.substring(0, pkg.length() - 1)))){
        		return !isIncluded(sc);
        	}
        }
        return false;
    }
    
    public boolean isIncluded(SootClass sc){
        String name = sc.getName();
        for (String inc : (List<String>) Options.v().include()) {
            if (name.equals(inc) || ((inc.endsWith(".*") || inc.endsWith("$*")) && name.startsWith(inc.substring(0, inc.length() - 1)))) {
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
            final SootClass cl = clIt.next();
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
                    SootClass c = classIter.next();
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

