/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * SableUtil, a clean room implementation of the Collection API.     *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca).   *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $SableUtilVersion: 1.11 $

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on March 27, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca). (*)
   Added a cache for the zip files.  Speeds up retrieval considerably.
   
 - Modified on July 5, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Added getInputStreamOf(String classpath, String className).
   Fixed a platform dependence bug while searching zip files.
   Added support for jar files.

 - Modified on June 15, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First release of this file.
*/

package soot.util;

import java.io.*;
import java.util.zip.*;
import java.util.Enumeration;
import java.util.*;

/**
 * Class which provides a static method to locate any class in your CLASSPATH and returns
 * an InputStream to it.  Does handle .jar files as well.
 */

public class ClassLocator
{
    private static char pathSeparator = System.getProperty("path.separator").charAt(0);
    private static char fileSeparator = System.getProperty("file.separator").charAt(0);

    private ClassLocator() // No instances.
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
                else {
                    // Default: try loading class directly

                    String fileName = className.replace('.', fileSeparator) + ".class";
                    String fullPath;

                    if(location.endsWith(new Character(fileSeparator).toString()))
                        fullPath = location + fileName;
                    else
                        fullPath = location + fileSeparator + fileName;

                    try {
                        File f = new File(fullPath);
                        
                        if (!f.canRead())
                            continue;

                        FileInputStream in = new FileInputStream(f);
                        
                        return in;
                    } catch(IOException e)
                    {
                        continue;
                    }
                }

            }
        }

        throw new ClassNotFoundException(className);
    }
}
