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

import java.util.*;
import java.io.*;

/** Provides methods to deal with Tags. */
public class TagManager 
{
    /** Writes to <code>aOut</code> a summary of the tag information contained within this Scene. */
    public static void printReportFor(PrintWriter aOut) 
    {
	//	Set set = new HashSet();
	//set.addAll(aTagNames);
	
       	printContentsOfHost("<<scene>>", "", Scene.v(), aOut);
	
	Iterator it = Scene.v().getApplicationClasses().iterator();
	while(it.hasNext()) 
        {
	    SootClass cl = (SootClass) it.next();

	    printContentsOfHost("<" + cl.getName() +">", "    ", cl, aOut);
	    Iterator methodIt = cl.getMethods().iterator();
	    while(methodIt.hasNext()) 
            {
		SootMethod mtd = (SootMethod) methodIt.next();
		//printContentsOfHost(mtd.toString(), "        ", mtd, aOut);
	    }
	}
    }

    /** Writes a summary of the information contained in the given host to aOut. */
    public static void printContentsOfHost(String aSignature, String aIndent, Host aHost, PrintWriter aOut)
    {
	aOut.println(aIndent + aSignature);
	
	Iterator it = aHost.getTags().iterator();
	while(it.hasNext()) {
	    aOut.println(aIndent + "    " + it.next());
	}

	aOut.println("");
    }

    /** Adds all of the method tags named <code>aTagName</code> and stores the result
     * in an identically-named tag on the class. */
    public static void sumTagsUpMethods(String aTagName, SootClass aClass)
    {
	long sum = 0;
	aClass.destroyTag(aTagName);
	
	Iterator it = aClass.getMethods().iterator();
	while(it.hasNext()) {
	    SootMethod method = (SootMethod) it.next();
	    sum += ((Long) method.getTag(aTagName).getValue()).longValue();	    
	}
	
	aClass.newTag(aTagName, new Long(sum));	
    }

    /** Adds all of the class tags named <code>aTagName</code> and stores the result
     * in an identically-named tag on the Scene. */
    public static void sumTagsUp(String aTagName, Scene aScene)
    {
	long sum = 0;
	aScene.destroyTag(aTagName);

	Iterator it = aScene.getApplicationClasses().iterator();
	while(it.hasNext()) {
	    SootClass c =  (SootClass) it.next();
	    TagManager.sumTagsUpMethods(aTagName, c);
	    sum += ((Long)c.getTag(aTagName).getValue()).longValue();
	}
	
	aScene.newTag(aTagName, new Long(sum));	
    }
}


    
