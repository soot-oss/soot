/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
import soot.options.*;

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
        JimpleBody jb = Jimple.v().newBody(m);
        
        Map options = PhaseOptions.v().getPhaseOptions(phaseName);
        boolean useOriginalNames = PhaseOptions.getBoolean(options, "use-original-names");

        if(useOriginalNames)
            soot.coffi.Util.v().setFaithfulNaming(true);

        /*
            I need to set these to null to free Coffi structures.
        fileBody.coffiClass = null;
        bafBody.coffiMethod = null;

        */
        if(Options.v().verbose())
            G.v().out.println("[" + m.getName() + "] Constructing JimpleBody from coffi...");

        if(m.isAbstract() || m.isNative() || m.isPhantom())
            return jb;
            
        if(Options.v().time())
            Timers.v().conversionTimer.start();

        if (coffiMethod == null)
            G.v().out.println(m);
        if(coffiMethod.instructions == null)
        {
            if(Options.v().verbose())
                G.v().out.println("[" + m.getName() +
                    "]     Parsing Coffi instructions...");

             coffiClass.parseMethod(coffiMethod);
        }
                
        if(coffiMethod.cfg == null)
        {
            if(Options.v().verbose())
                G.v().out.println("[" + m.getName() +
                    "]     Building Coffi CFG...");

             new soot.coffi.CFG(coffiMethod);
             
             // if just computing metrics, we don't need to actually return body
             if (soot.jbco.Main.metrics) return null;
         }

         if(Options.v().verbose())
             G.v().out.println("[" + m.getName() +
                    "]     Producing naive Jimple...");

         boolean oldPhantomValue = Scene.v().getPhantomRefs();

         Scene.v().setPhantomRefs(true);
         coffiMethod.cfg.jimplify(coffiClass.constant_pool,
             coffiClass.this_class, coffiClass.bootstrap_methods_attribute, jb);
         Scene.v().setPhantomRefs(oldPhantomValue);

        if(Options.v().time())
            Timers.v().conversionTimer.end();

         coffiMethod.instructions = null;
         coffiMethod.cfg = null;
         coffiMethod.attributes = null;
         coffiMethod.code_attr = null;
         coffiMethod.jmethod = null;
         coffiMethod.instructionList = null;

         coffiMethod = null;
         coffiClass = null;
         
         PackManager.v().getPack("jb").apply(jb);
         return jb;
    }
}
