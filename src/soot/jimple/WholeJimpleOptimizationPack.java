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


package soot.jimple;

import soot.*;
import soot.jimple.toolkits.invoke.*;
import soot.*;
import soot.util.*;
import java.util.*;

public class WholeJimpleOptimizationPack extends SceneTransformer
{
    private static WholeJimpleOptimizationPack instance = new WholeJimpleOptimizationPack();
    private WholeJimpleOptimizationPack() {}

    public static WholeJimpleOptimizationPack v() { return instance; }

    protected void internalTransform(String phaseName, Map options)
    {
        System.out.print("Building InvokeGraph...");
        System.out.flush();
            
        InvokeGraph invokeGraph = ClassHierarchyAnalysis.newInvokeGraph();
        Scene.v().setActiveInvokeGraph(invokeGraph);
                                
        System.out.println();
            
        if(Main.isVerbose)
            System.out.println("Starting whole-program jimple optimizations...");

        System.out.print("Binding static methods...");
        System.out.flush();

        StaticMethodBinder.v().transform(phaseName + ".smb");
        System.out.println();

//          Iterator applicationClassesIt = Scene.v().getApplicationClasses().iterator();
//          while (applicationClassesIt.hasNext())
//          {
//              SootClass c = (SootClass)applicationClassesIt.next();

//              if (c.isApplicationClass())
//              {
//                  Iterator methodIt = c.getMethods().iterator();
            
//                  while(methodIt.hasNext())
//                  {   
//                      SootMethod m = (SootMethod) methodIt.next();
//                      if (!m.isPhantom())
//                      {
//                          Body body = m.getActiveBody();

//                          soot.jimple.toolkits.invoke.JimpleInliner.inlineAll(body);
//                      }
//                  }
//              }
//          }
    }
}
