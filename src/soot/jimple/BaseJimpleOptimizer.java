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

import soot.jimple.toolkits.scalar.*;
import soot.*;
import soot.util.*;
import java.util.*;

public class BaseJimpleOptimizer
{
    public static void optimize(JimpleBody body)
    {
        if(Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() +
                "] Starting base jimple optimizations...");

        // This order is important.  Don't mess with it.
        // Examples to demonstrate this are left as an exercise for the reader.
        CopyPropagator.v().transform(body, "bjo.cp", "ignore-stack-locals");
        
//        ConstantPropagatorAndFolder.v().transform(body, "bjo.cpf");
//        ConditionalBranchFolder.v().transform(body, "bjo.cbf");
        DeadAssignmentEliminator.v().transform(body, "bjo.dae");
//        UnreachableCodeEliminator.v().transform(body, "bjo.uce1");
//        UnconditionalBranchFolder.v().transform(body, "bjo.ubf");
//        UnreachableCodeEliminator.v().transform(body, "bjo.uce2");
    }
}





