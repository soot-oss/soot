/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville and Feng Qian
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

package soot.tagkit;
import soot.*;


import java.util.*;
import javax.swing.*;
import java.io.*;


public class TagManager
{
    private static TagPrinter tagPrinter = new StdTagPrinter();



    public static Tag getTagFor(String tagName) {
	try {
	    Class cc = Class.forName("soot.tagkit." + tagName);
	    return (Tag)  cc.newInstance();
	} 
	catch (ClassNotFoundException e) {
	    return null;
	} catch(IllegalAccessException e) {
	    throw new RuntimeException();
	} catch (InstantiationException e) {
	    throw new RuntimeException(e.toString());
	}	
    }


    public static void setTagPrinter(TagPrinter p) 
    {
	tagPrinter = p;
    }

    public static String print(String aClassName, String aFieldOrMtdSignature, Tag aTag)
    {
	return tagPrinter.print(aClassName, aFieldOrMtdSignature,  aTag);
    }
}



