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






package soot.util;

import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;
import java.util.*;
import soot.*;

/*
 * xxx should find a .class .jimple or .baf source for a className... presently finds jimple and class
 */

/** Provides utility methods to retrieve an input stream for a class name, given
 * a classfile, or jimple or baf output files. */
public class SourceLocator
{
    private static char pathSeparator = System.getProperty("path.separator").charAt(0);
    private static char fileSeparator = System.getProperty("file.separator").charAt(0);

    /** Constant for default source precedence. */
    public static final int PRECEDENCE_NONE = 0;

    /** Constant indicating that classfiles (via Coffi) are preferred input stream. */
    public static final int PRECEDENCE_CLASS = 1;

    /** Constant indicating that Jimple is preferred input stream. */
    public static final int PRECEDENCE_JIMPLE = 2;

    /** Constant indicating that Baf is preferred input stream. */
    public static final int PRECEDENCE_BAF = 3;

    private static int srcPrecedence = PRECEDENCE_NONE;

    private static List zipFileList = Collections.synchronizedList(new LinkedList()); 

    private SourceLocator() // No instances.
    {
    }

    /** Sets the source precedence. */
    public static void setSrcPrecedence(int precedence)
    {	
        srcPrecedence = precedence;
    }    

    /** Given a class name, uses the default soot-class-path to return an input stream for the given class. */
    public static InputStream getInputStreamOf(String className) throws ClassNotFoundException
    {
        return getInputStreamOf(Scene.v().getSootClassPath(), className);
    }

    private static Map nameToZipFile = new HashMap();
    private static List previousLocations = null;
    private static String previousCP = null;
    private static int previousCPHashCode = 0;
    private static boolean isRunningUnderBraindeadOS = System.getProperty("os.name").startsWith("Windows");

    private static int  count = 0;
    /** Given a class name and class-path, returns an input stream for the given class. */
    public static InputStream getInputStreamOf(String classPath, String className) throws ClassNotFoundException
    {
        List locations = null;

        // Either object equality or string equality will do.
        if (classPath.hashCode() == previousCPHashCode && (classPath == previousCP || classPath.equals(previousCP)))
        {
            locations = previousLocations;	   
        }
        else
        // Split up the class path into locations
        {
            // Store the classpath and its hash.
            previousCPHashCode = classPath.hashCode();
            previousCP = classPath;

            locations = new ArrayList();
            int sepIndex;
            boolean absolutePath;

            if(classPath.equals("<external-class-path>"))
                classPath = System.getProperty("java.class.path");

            String userDir = System.getProperty("user.dir");
            for(boolean done = false; !done;)
            {
                if(classPath.indexOf(fileSeparator) == 0 ||
                         (isRunningUnderBraindeadOS && classPath.charAt(1) == ':'))
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

        InputStream res = null;
        { // for now types are found on the filesystem.
            List reps = new ArrayList(4);
            reps.add(ClassInputRep.v());
            reps.add(JimpleInputRep.v());

            if(srcPrecedence == PRECEDENCE_CLASS || srcPrecedence == PRECEDENCE_NONE) {		
                List lst = new LinkedList();
                lst.add(ClassInputRep.v());
                if( (res = getFileInputStream(locations, lst, className)) != null) {
                    return res;
		}
                if( (res = getFileInputStream(locations, reps, className)) != null) {
                    return res;
		}
            } 
            else if (srcPrecedence == PRECEDENCE_JIMPLE) {
                List lst = new LinkedList();
                lst.add(JimpleInputRep.v());
                if( (res = getFileInputStream(locations, lst, className)) != null)
                    return res;
                if( (res = getFileInputStream(locations, reps, className)) != null)
                    return res;

            } else
                throw new RuntimeException("Other source precedences are not currently supported.");
            throw new ClassNotFoundException();
        }
        
    }


    static private InputStream getFileInputStream(List locations, List reps, String className)
    {    
        Iterator it = locations.iterator();
        className = className.replace('/','.');  // so that you can give for example either spec.bench.main or spec/bench/main
        String className2 = className.replace('.', '/');

        
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
                    adjustedClassName = path + className2;

                String fullPath = adjustedClassName + inputRep.getFileExtension();
		
                File f = new File(fullPath);
                InputStream in;

                if (f.canRead()) {
                    try {       
                        return in = inputRep.createInputStream(new FileInputStream(f));                    
                    } catch(IOException e) { 
                        System.out.println(e); throw new RuntimeException("!"); 
                    }
                } else {  // check if file  is in an archive(ie in a .zip or .zip file)
		    Iterator zipFileIt = zipFileList.iterator();
		    while(zipFileIt.hasNext()) {
			ZipFile zip = (ZipFile) zipFileIt.next();

			ZipEntry entry = zip.getEntry(className2 + inputRep.getFileExtension());    

			try {
			    InputStream is = new BufferedInputStream(zip.getInputStream(entry));
			    InputStream bugFreeInputStream = doJDKBugWorkaround(is, entry.getSize());				
				
			    return inputRep.createInputStream(bugFreeInputStream);
			} catch(IOException e) {
			    System.err.println("error reading file:" + zip.getName() + e.toString());
			    System.exit(1);
			}
		    }
		}
            }
        }
        return null;
    }    
    
    private static boolean isArchive(String path) {
	File f = new File(path);	
	if(f.isFile() && f.canRead()) { 		
	    if(path.endsWith("zip") || path.endsWith("jar")) {
		return true;
	    } else {
		System.err.println("Warning: the following soot-classpath entry is not a supported archive file (must be .zip or .jar): " + path);
	    }
	}  
	return false;
    }

    private static void addArchive(String fileName) {
	try {
	    ZipFile zipFile = new ZipFile(fileName);
	    zipFileList.add(zipFile);	
	} catch (IOException e) {
	    System.err.println("error loading file:" + fileName + e.toString());
	}
    }    
    
    private static InputStream doJDKBugWorkaround(InputStream is, long size) throws IOException {
	
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

}

