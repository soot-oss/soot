/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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


import soot.*;

import java.util.*;
import java.io.*;

public class HierarchyBrowser
{
    public static void usage()
    {
        G.v().out.println("Usage: java HierarchyBrowser <command> <class> [context-class1] [context-class2] ...");
        G.v().out.println("    where command is one of: [d]subc[_i], [d]subi[_i], [d]imp\n");
        G.v().out.println("OR:    java HierarchyBrowser interactive <context-class1> [context-class2] ...");
        G.v().out.println("\nExample: java HierarchyBrowser subi soot.Unit soot.Main");
    }

    public static void doCommand(Hierarchy h, String command, String className)
    {
        List target = null;
        SootClass c = Scene.v().getSootClass(className);

        if (command.equals("subc_i"))
            target = h.getSubclassesOfIncluding(c);
        else if (command.equals("subc"))
            target = h.getSubclassesOf(c);
        else if (command.equals("dsubc_i"))
            target = h.getDirectSubclassesOfIncluding(c);
        else if (command.equals("dsubc"))
            target = h.getDirectSubclassesOf(c);
        else if (command.equals("supc"))
            target = h.getSuperclassesOf(c);
        else if (command.equals("subi_i"))
            target = h.getSubinterfacesOfIncluding(c);
        else if (command.equals("subi"))
            target = h.getSubinterfacesOf(c);
        else if (command.equals("dsubi_i"))
            target = h.getDirectSubinterfacesOfIncluding(c);
        else if (command.equals("dsubi"))
            target = h.getDirectSubinterfacesOf(c);
        else if (command.equals("dimp"))
            target = h.getDirectImplementersOf(c);
        else if (command.equals("imp"))
            target = h.getImplementersOf(c);

        ArrayList al = new ArrayList(); 
        if (target != null) 
            al.addAll(target);
        G.v().out.println(command + " on "+className+" gives: "+al);
    }

    public static void main(String[] argv)
    {
        if (argv.length < 2)
            { usage(); System.exit(1); }

        String command = argv[0];
        String className = argv[1];

        for (int i = 1; i < argv.length; i++)
        {
            G.v().out.println("Resolving "+argv[i]+"...");
            Scene.v().loadClassAndSupport(argv[i]);
        }

        G.v().out.print("Building hierarchy... ");
        Hierarchy h = new Hierarchy();
        G.v().out.println("done!");

        if (command.equals("interactive"))
        {
            while(true)
            {
                try
                {
                    InputStreamReader isr=new InputStreamReader(System.in);
                    BufferedReader stdin=new BufferedReader(isr);
                
                    G.v().out.print("[Command]: ");
                    command = stdin.readLine();
                    if (command.equals("quit"))
                        break;
                    
                    className = command.substring(command.indexOf(' ')+1, command.length());
                    command = command.substring(0, command.indexOf(' '));

                    doCommand(h, command, className);
                }
                catch (IOException e) { }
            }
        }
        else
            doCommand(h, command, className);
    }
}
