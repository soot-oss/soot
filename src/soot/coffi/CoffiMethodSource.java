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


package soot.coffi;

import soot.*;
import java.util.*;
import soot.jimple.*;

public class CoffiMethodSource implements MethodSource
{
    public ClassFile coffiClass;
    public method_info coffiMethod;

    CoffiMethodSource(soot.coffi.ClassFile coffiClass, soot.coffi.method_info coffiMethod)
    {
        this.coffiClass = coffiClass;
        this.coffiMethod = coffiMethod;
    }

    public Body getBody(SootMethod m, String phaseName)
    {
        ClassFileBody fileBody = new ClassFileBody(m);
        JimpleBody jb = Jimple.v().newBody(m);
	
        Map options = Scene.v().getPhaseOptions(phaseName);
	boolean useOriginalNames = Options.getBoolean(options, "use-original-names");

        if(useOriginalNames)
            soot.coffi.Util.setFaithfulNaming(true);

        /*
            I need to set these to null to free Coffi structures.
        fileBody.coffiClass = null;
        bafBody.coffiMethod = null;

        */
        if(soot.Main.isVerbose)
            System.out.println("[" + m.getName() + "] Constructing JimpleBody...");

        if(m.isAbstract() || m.isNative() || m.isPhantom())
            return jb;
            
        if(soot.Main.isProfilingOptimization)
            soot.Main.conversionTimer.start();

        if (coffiMethod == null)
            System.out.println(m);
        if(coffiMethod.instructions == null)
        {
            if(soot.Main.isVerbose)
                System.out.println("[" + m.getName() +
                    "]     Parsing Coffi instructions...");

             coffiClass.parseMethod(coffiMethod);
        }
                
        if(coffiMethod.cfg == null)
        {
            if(soot.Main.isVerbose)
                System.out.println("[" + m.getName() +
                    "]     Building Coffi CFG...");

             new soot.coffi.CFG(coffiMethod);

         }

         if(soot.Main.isVerbose)
             System.out.println("[" + m.getName() +
                    "]     Producing naive Jimple...");

         boolean oldPhantomValue = Scene.v().getPhantomRefs();

         Scene.v().setPhantomRefs(true);
         coffiMethod.cfg.jimplify(coffiClass.constant_pool,
             coffiClass.this_class, jb);
         Scene.v().setPhantomRefs(oldPhantomValue);

         coffiMethod.instructions = null;
         coffiMethod.cfg = null;
	 
         jb.applyPhaseOptions(options);
         return jb;
    }
}
