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

/**
 * xxx should find a .class .jimple or .baf source for a className... presently finds jimple and class
 */

public class SourceLocator
{
    private static char pathSeparator = System.getProperty("path.separator").charAt(0);
    private static char fileSeparator = System.getProperty("file.separator").charAt(0);

    public static final int PRECEDENCE_NONE = 0;
    public static final int PRECEDENCE_CLASS = 1;
    public static final int PRECEDENCE_JIMPLE = 2;
    public static final int PRECEDENCE_BAF = 3;

    private static int srcPrecedence = PRECEDENCE_NONE;

    public static void setSrcPrecedence(int precedence)
    {
	srcPrecedence = precedence;
    }


    private SourceLocator() // No instances.
    {
    }
    


    public static InputStream getInputStreamOf(String className) throws ClassNotFoundException
    {
        return getInputStreamOf(Scene.v().getSootClassPath(), className);
    }

    public static Map nameToZipFile = new HashMap();
    // cache of zip files

    private static List previousLocations = null;
    private static int previousCPHashCode = 0;
    
    public static InputStream getInputStreamOf(String classPath, String className) throws ClassNotFoundException
    {

        List locations = null;

        if (classPath.hashCode() == previousCPHashCode)
        {
            locations = previousLocations;
        }
        else
        // Split up the class path into locations
        {
            locations = new ArrayList();
            int sepIndex;
	    boolean absolutePath;

            if(classPath.equals("<external-class-path>"))
                classPath = System.getProperty("java.class.path");

	    String userDir = System.getProperty("user.dir");
            for(;;)
            {
		// temporary fix xxxxx fixme .
		if(classPath.indexOf(fileSeparator) != 0)
		    absolutePath = false;
		else
		    absolutePath = true;
		
                sepIndex = classPath.indexOf(pathSeparator);
		
                if(sepIndex == -1)
		{
		    if(absolutePath)
			locations.add(classPath);
		    else
			locations.add(userDir + fileSeparator + classPath);
                    break;
                }
		if(absolutePath)
		    locations.add(classPath.substring(0, sepIndex));
		else
		    locations.add(userDir + fileSeparator + classPath.substring(0, sepIndex));
		
                classPath = classPath.substring(sepIndex + 1);
            }
            previousCPHashCode = classPath.hashCode();
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
		if( (res = getFileInputStream(locations, lst, className)) != null)
		    return res;
		if( (res = getFileInputStream(locations, reps, className)) != null)
		    return res;
	    } 
	    else if (srcPrecedence == PRECEDENCE_JIMPLE) {
		List lst = new LinkedList();
		lst.add(JimpleInputRep.v());
		if( (res = getFileInputStream(locations, lst, className)) != null)
		    return res;
		if( (res = getFileInputStream(locations, reps, className)) != null)
		    return res;

	    } else
		throw new RuntimeException("IMPOSSIBLE");
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
		}
	    }
	}
	return null;
    }    
}

