/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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






package soot.util;
import soot.*;

import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;
import java.util.*;
import soot.Main;
import soot.Scene;
import soot.options.Options;

/*
 * xxx should find a .class .jimple or .baf source for a className... presently finds jimple and class
 */

/** Provides utility methods to retrieve an input stream for a class name, given
 * a classfile, or jimple or baf output files. */
public class SourceLocator
{
    public SourceLocator( Singletons.Global g ) {}
    public static SourceLocator v() { return G.v().SourceLocator(); }

    private char pathSeparator = System.getProperty("path.separator").charAt(0);
    private char fileSeparator = System.getProperty("file.separator").charAt(0);

    private List zipFileList = Collections.synchronizedList(new LinkedList()); 

    private HashMap sourceToClassMap;

    public HashMap getSourceToClassMap(){
        return sourceToClassMap;
    }
    public void setSourceToClassMap(HashMap map){
        sourceToClassMap = map;
    }
    public void addToSourceToClassMap(String key, String val) {
        sourceToClassMap.put(key, val);
    }
    /** Given a class name, uses the default soot-class-path to return an input stream for the given class. */
    public InputStream getInputStreamOf(String className) throws ClassNotFoundException
    {
        return getInputStreamOf(Scene.v().getSootClassPath(), className);
    }

    private Map nameToZipFile = new HashMap();
    private List previousLocations = null;
    private String previousCP = "<impossible-class-path>";
    private boolean isRunningUnderBraindeadOS = System.getProperty("os.name").startsWith("Windows");

    private int  count = 0;
    /** Given a class name and class-path, returns an input stream for the given class. */
    public InputStream getInputStreamOf(String classPath, String className) throws ClassNotFoundException
    {
        List locations = null;

        // Either object equality or string equality will do.
        if ( classPath == previousCP || previousCP.equals(classPath) )
        {
            locations = previousLocations;	   
        }
        else
        // Split up the class path into locations
        {
            // Store the classpath and its hash.
            previousCP = classPath;

            locations = new ArrayList();
            int sepIndex;
            boolean absolutePath;

            if(classPath == null) {
                classPath = System.getProperty("java.class.path")+
                    pathSeparator+System.getProperty("java.home")+fileSeparator+
                    "lib"+fileSeparator+"rt.jar";
            }

            String userDir = System.getProperty("user.dir");
            for(boolean done = false; !done;)
            {
                if(classPath.indexOf(fileSeparator) == 0 ||
                       (isRunningUnderBraindeadOS && 
                        classPath.length() >= 2 && classPath.charAt(1) == ':'))
                    absolutePath = true;
                else
                    absolutePath = false;

                sepIndex = classPath.indexOf(pathSeparator);
                
                String candidate = null;

                if(sepIndex == -1)
                {
                    candidate = classPath;
                    done = true;
                }
                else
                    candidate = classPath.substring(0, sepIndex);
                
                // Attempt to provide rudimentary tilde expansion.
                if (candidate.startsWith("~"))
                {
                    if (candidate.startsWith("~"+System.getProperty("user.name")+fileSeparator))
                        candidate = "~"+fileSeparator+candidate.substring(candidate.indexOf(fileSeparator));
                    if (!candidate.startsWith("~"+fileSeparator))
                        throw new RuntimeException
                            ("can't handle tilde expansion of a username; please provide fully-qualified path.");
                    candidate = System.getProperty("user.home")+fileSeparator+candidate.substring(2);
                }
                else if(!absolutePath)
                    candidate = userDir + fileSeparator + candidate;


		if(isArchive(candidate)) {
		    addArchive(candidate);
		} else {		
		    locations.add(candidate);
		}
		classPath = classPath.substring(sepIndex + 1);
            }
            previousLocations = locations;
        }

        setLocationsFound(locations);
        InputStream res = null;
        { // for now types are found on the filesystem.
            List jimple = new SingletonList(JimpleInputRep.v());
            List java = new SingletonList(JavaInputRep.v());
            List clss = new SingletonList(ClassInputRep.v());

            String javaClassName = className;
            if (className.indexOf("$") != -1) {
                // class is an inner class and will be in
                // Outer of Outer$Inner
                javaClassName = className.substring(0, className.indexOf("$"));

                
                //System.out.println("java class name: "+javaClassName); 
            }
            // always do this because an inner class could be in a class
            // thats in the map
            if (sourceToClassMap != null) {
                if (sourceToClassMap.get(javaClassName) != null) {
                    javaClassName = (String)sourceToClassMap.get(javaClassName);
                }
            }
                
            //System.out.println("java class name: "+javaClassName); 

            switch( Options.v().src_prec() ) {
                case Options.src_prec_class:
                    if( (res = getFileInputStream(locations, clss, className)) != null) 
                        return res;
                    if( (res = getFileInputStream(locations, jimple, className)) != null) 
                        return res;
                    if( (res = getFileInputStream(locations, java, javaClassName)) != null) 
                        return res;
                    break;
                case Options.src_prec_java:
                    if( (res = getFileInputStream(locations, java, javaClassName)) != null) 
                        return res;
                    if( (res = getFileInputStream(locations, clss, className)) != null) 
                        return res;
                    if( (res = getFileInputStream(locations, jimple, className)) != null) 
                        return res;
                    break;
                case Options.src_prec_jimple:
                    if( (res = getFileInputStream(locations, jimple, className)) != null) 
                        return res;
                    if( (res = getFileInputStream(locations, clss, className)) != null) 
                        return res;
                    if( (res = getFileInputStream(locations, java, javaClassName)) != null) 
                        return res;
                    break;
                default:
                    throw new RuntimeException("Other source precedences are not currently supported.");
            }
            throw new ClassNotFoundException();
        }
    }


    private List locationsFound;

    private void setLocationsFound(List locs) {
        locationsFound = locs;
    }
    public List getLocationsFound(){
        return locationsFound;
    }
    
    private String fullPathFound;

    private void setFullPathFound(String fp) {
        fullPathFound = fp;
    }
    public String getFullPathFound(){
        return fullPathFound;
    }
    
    private InputStream getFileInputStream(List locations, List reps, String className)
    {    
        Iterator it = locations.iterator();
        //className = className.replace('/','.');  // so that you can give for example either spec.bench.main or spec/bench/main
        if( className.indexOf("/") >= 0 )
            throw new RuntimeException( "Class names may not contain slashes! "+className );
        String classNameSlashed = className.replace('.', '/'); // now it's back in canonical / form.

        while(it.hasNext()) 
        {
            StringBuffer locationBuf = new StringBuffer();
            String locationPath = (String)it.next();
            locationBuf.append(locationPath);

            if(!locationPath.endsWith(new Character(fileSeparator).toString()))
                locationBuf.append(fileSeparator);

            String path = locationBuf.toString();
                
            Iterator repsIt = reps.iterator();    
            while(repsIt.hasNext()) 
            { 
                SootInputRepresentation inputRep = (SootInputRepresentation) repsIt.next();

                String adjustedClassName = path + className;
                if(inputRep instanceof ClassInputRep)
                    adjustedClassName = path + classNameSlashed;
                
                if (inputRep instanceof JavaInputRep) 
                    adjustedClassName = path + classNameSlashed;

                String fullPath = adjustedClassName + inputRep.getFileExtension();
                File f = new File(fullPath);
                
                setFullPathFound(fullPath);
                if (f.canRead()) {
                    try {       
                        return inputRep.createInputStream(new FileInputStream(f));                    
                    } catch(IOException e) { 
                        G.v().out.println(e); throw new RuntimeException("!"); 
                    }
                }
            }
        }

        // all right, none of the paths had it.
        // check if file is in an archive(ie in a .zip or jar file)
        // this looks pretty slow.  is it n^2?
        Iterator zipFileIt = zipFileList.iterator();
        while(zipFileIt.hasNext()) {
            ZipFile zip = (ZipFile) zipFileIt.next();

            Iterator repsIt = reps.iterator();    
            while(repsIt.hasNext()) 
            { 
                SootInputRepresentation inputRep = (SootInputRepresentation) repsIt.next();

                ZipEntry entry = zip.getEntry(classNameSlashed + inputRep.getFileExtension());
                if (entry != null)
                {
                    try {
                        InputStream is = new BufferedInputStream(zip.getInputStream(entry));
                        InputStream bugFreeInputStream = doJDKBugWorkaround(is, entry.getSize());				
                        return inputRep.createInputStream(bugFreeInputStream);
                    } catch(IOException e) {
                        G.v().out.println("error reading file:" + zip.getName() + e.toString());
                        throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
                    }
                }
            }
        }

        // drat!
        return null;
    }    
    
    private boolean isArchive(String path) {
	File f = new File(path);	
	if(f.isFile() && f.canRead()) { 		
	    if(path.endsWith("zip") || path.endsWith("jar")) {
		return true;
	    } else {
		G.v().out.println("Warning: the following soot-classpath entry is not a supported archive file (must be .zip or .jar): " + path);
	    }
	}  
	return false;
    }

    private void addArchive(String fileName) {
	try {
	    ZipFile zipFile = new ZipFile(fileName);
	    zipFileList.add(zipFile);	
	} catch (IOException e) {
	    G.v().out.println("error loading file:" + fileName + e.toString());
	}
    }    
    
    private InputStream doJDKBugWorkaround(InputStream is, long size) throws IOException {
	
	int sz = (int) size;
	byte[] buf = new byte[sz];					
				
				    
	final int N = 1024;
	int ln = 0;
	int count = 0;
	while (sz > 0 &&  
	       (ln = is.read(buf, count, Math.min(N, sz))) != -1) {
	    count += ln;
	    sz -= ln;
	}
	return  new ByteArrayInputStream(buf);		
    }

    public List getClassesUnder(String aPath) {
        List fileNames = new ArrayList();

	if (isArchive(aPath)) {
	    List inputExtensions = new ArrayList(2);
            inputExtensions.add(ClassInputRep.v().getFileExtension());
            inputExtensions.add(JimpleInputRep.v().getFileExtension());
            inputExtensions.add(JavaInputRep.v().getFileExtension());

	    try {
		ZipFile archive = new ZipFile(aPath);
		for (Enumeration entries = archive.entries(); 
		     entries.hasMoreElements(); ) {
		    ZipEntry entry = (ZipEntry) entries.nextElement();
		    String entryName = entry.getName();
		    int extensionIndex = entryName.lastIndexOf('.');
		    if (extensionIndex >= 0) {
			String entryExtension = entryName.substring(extensionIndex);
			if (inputExtensions.contains(entryExtension)) {
			    entryName = entryName.substring(0, extensionIndex);
			    entryName = entryName.replace(fileSeparator, '.');
			    fileNames.add(entryName);
			}
		    }
		}
	    } catch(IOException e) {
		G.v().out.println("Error reading " + aPath + ": " 
				  + e.toString());
		throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
	    }
	} else {
	    File file = new File(aPath);

	    File[] files = file.listFiles();
	    if (files == null) {
		files = new File[1];
		files[0] = file;
	    }

	    for (int i = 0; i < files.length; i++) {
		if (files[i].isDirectory()) {
		    List l =
			getClassesUnder(
					aPath + fileSeparator + files[i].getName());
		    Iterator it = l.iterator();
		    while (it.hasNext()) {
			String s = (String) it.next();
			fileNames.add(files[i].getName() + "." + s);
		    }
		} else {
		    String fileName = files[i].getName();

		    if (fileName.endsWith(".class")) {
			int index = fileName.lastIndexOf(".class");
			fileNames.add(fileName.substring(0, index));
		    }

		    if (fileName.endsWith(".jimple")) {
			int index = fileName.lastIndexOf(".jimple");
			fileNames.add(fileName.substring(0, index));
		    }

		    if (fileName.endsWith(".java")) {
			int index = fileName.lastIndexOf(".java");
			fileNames.add(fileName.substring(0, index));
		    }
		}
	    }
	}
        return fileNames;
    }

    public String getFileNameFor(SootClass c, int rep) {
        if (rep == Options.output_format_none)
            return null;

        StringBuffer b = new StringBuffer();

        b.append(getOutputDir());

        if ((b.length() > 0) && (b.charAt(b.length() - 1) != fileSeparator))
            b.append(fileSeparator);

        if (rep != Options.output_format_dava) {
            b.append(c.getName());
            b.append(getExtensionFor(rep));

            return b.toString();
        }

        b.append("dava");
        b.append(fileSeparator);
        {
            String classPath = b.toString() + "classes";
            File dir = new File(classPath);

            if (!dir.exists())
                try {
                    dir.mkdirs();
                } catch (SecurityException se) {
                    G.v().out.println("Unable to create " + classPath);
                    throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
                }
        }

        b.append("src");
        b.append(fileSeparator);

        String fixedPackageName = c.getJavaPackageName();
        if (fixedPackageName.equals("") == false) {
            b.append(fixedPackageName.replace('.', fileSeparator));
            b.append(fileSeparator);
        }

        {
            String path = b.toString();
            File dir = new File(path);

            if (!dir.exists())
                try {
                    dir.mkdirs();
                } catch (SecurityException se) {
                    G.v().out.println("Unable to create " + path);
                    throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
                }
        }

        b.append(c.getShortJavaStyleName());
        b.append(".java");

        return b.toString();
    }

    /* This is called after sootClassPath has been defined. */
    public Set classesInDynamicPackage(String str) {
        HashSet set = new HashSet(0);
        StringTokenizer strtok = new StringTokenizer(
                Scene.v().getSootClassPath(), String.valueOf(pathSeparator));
        while (strtok.hasMoreTokens()) {
            String path = strtok.nextToken();

            // For jimple files
            List l = getClassesUnder(path);
            for( Iterator filenameIt = l.iterator(); filenameIt.hasNext(); ) {
                final String filename = (String) filenameIt.next();
                if (filename.startsWith(str))
                    set.add(filename);
            }

            // For class files;
            path = path + pathSeparator;
            StringTokenizer tokenizer = new StringTokenizer(str, ".");
            while (tokenizer.hasMoreTokens()) {
                path = path + tokenizer.nextToken();
                if (tokenizer.hasMoreTokens())
                    path = path + pathSeparator;
            }
            l = getClassesUnder(path);
            for (Iterator it = l.iterator(); it.hasNext();)
                set.add(str + "." + ((String) it.next()));
        }
        return set;
    }

    public String getExtensionFor(int rep) {
        switch (rep) {
            case Options.output_format_baf:      return ".baf";
            case Options.output_format_b:        return ".b";
            case Options.output_format_jimple:   return ".jimple";
            case Options.output_format_jimp:     return ".jimp";
            case Options.output_format_shimple:  return ".shimple";
            case Options.output_format_shimp:    return ".shimp";
            case Options.output_format_grimp:    return ".grimp";
            case Options.output_format_grimple:  return ".grimple";
            case Options.output_format_class:    return ".class";
            case Options.output_format_dava:     return ".java";
            case Options.output_format_jasmin:   return ".jasmin";
            case Options.output_format_xml:      return ".xml";
            default:
                throw new RuntimeException();
        }
    }

    public String getOutputDir() {
        String ret = Options.v().output_dir();
        if( ret.length() == 0 ) ret = "sootOutput";
        File dir = new File(ret);

        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (SecurityException se) {
                G.v().out.println("Unable to create " + ret);
                throw new CompilationDeathException(CompilationDeathException.COMPILATION_ABORTED);
            }
        }
        return ret;
    }
}

