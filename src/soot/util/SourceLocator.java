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

/**
 * xxx should find a .class .jimple or .baf source for a className... presently justs finds a .jimple source.
 */

public class SourceLocator
{
    private static char pathSeparator = System.getProperty("path.separator").charAt(0);
    private static char fileSeparator = System.getProperty("file.separator").charAt(0);

    private static final boolean debug = false;

    private SourceLocator() // No instances.
    {
    }
    


    public static InputStream getInputStreamOf(String className) throws ClassNotFoundException
    {
        return getInputStreamOf(System.getProperty("java.class.path"), className);
    }



  

    public static Map nameToZipFile = new HashMap();
        // cache of zip files

    private static List previousLocations = null;
    private static int previousCPHashCode = 0;
    
    public static InputStream getInputStreamOf(String classPath, String className) throws ClassNotFoundException
    {
        List locations = null;
	if(debug)
	    System.err.println("classpath is:  " + classPath);

        if (classPath.hashCode() == previousCPHashCode)
        {
            locations = previousLocations;
        }
        else
        // Split up the class path into locations
        {
            locations = new ArrayList();
            int sepIndex;

            for(;;)
            {
                sepIndex = classPath.indexOf(pathSeparator);

                if(sepIndex == -1)
                {
                    locations.add(classPath);
                    break;
                }

                locations.add(classPath.substring(0, sepIndex));
		
                classPath = classPath.substring(sepIndex + 1);
            }
            previousCPHashCode = classPath.hashCode();
            previousLocations = locations;
        }

        // Go through each location, looking for this class
        {
            for(int i = 0; i < locations.size(); i++)
            {
                String location = (String) locations.get(i);
		/*
                if(location.endsWith(".zip") || location.endsWith(".jar"))
                {
                    String fileName = className.replace('.', '/') + ".class";
                    try {
                        ZipFile zipFile;
                        
                        if(nameToZipFile.containsKey(location))
                            zipFile = (ZipFile) nameToZipFile.get(location);
                        else 
                        {
                            zipFile = new ZipFile(location);    
                            nameToZipFile.put(location, zipFile);
                        }
                        
                        ZipEntry entry = zipFile.getEntry(fileName);

                        if(entry == null)
                            continue;
                        else
                            return zipFile.getInputStream(entry);
                    } catch(IOException e)
                    {
                        continue;
                    }
		    }
		    else*/ {
                    // Default: try loading class directly

                    String fileName = className + ".jimple";
                    String fullPath;

                    if(location.endsWith(new Character(fileSeparator).toString()))
                        fullPath = location + fileName;
                    else
                        fullPath = location + fileSeparator + fileName;

                    try {
                        File f = new File(fullPath);
			if(debug)
			    System.err.println("looking for: " + fullPath);
                        
			InputStream in =  new JimpleInputStream(new FileInputStream(f));                        
			return in;
		    }  catch(IOException e) {
		    }

		    fileName = className.replace('.', '/') + ".class";
                    

		    
			
                    if(location.endsWith(new Character(fileSeparator).toString()))
                        fullPath = location + fileName;
                    else
                        fullPath = location + fileSeparator + fileName;

                    try {
                        File f = new File(fullPath);
			if(debug)
			    System.err.println("looking for: " + fullPath);
                        
			
			InputStream in = new ClassInputStream(new FileInputStream(f));                        
			return in;		      						
			
		    } catch(IOException e){
		    }
		    
		}
	    }
	}
        throw new ClassNotFoundException(className);
    }
}
