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





package soot.examples.printclass;

import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;

/**
    PrintClass example.
    
    Prints the given class in .jimple format.
 */
 
public class Main
{
    public static void main(String[] args)
    {
        int printOptions = 0;  

        // Parse arguments
            if(args.length == 0)
            {
                System.out.println("Usage: java PrintClass <classname> [ --jimple | --jimp ]");
                System.exit(0);
            }
            else if(args.length == 2)
            {
                if(args[1].equals("--jimp"))
                    printOptions = PrintJimpleBodyOption.USE_ABBREVIATIONS;
            }       
        
        // Retrieve and print class.
        {
            SootClass sClass = Scene.v().loadClassAndSupport(args[0]);
            PrintWriter out = new PrintWriter(System.out, true);
            
            Iterator methodIt = sClass.getMethods().iterator();
            
            while(methodIt.hasNext())
            {
                SootMethod m = (SootMethod) methodIt.next();
                
                m.setActiveBody(Jimple.v().newBody(new ClassFileBody(m)));
            }
            
            sClass.printTo(out, printOptions);
        }
    }
}
