/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Jerome Miecznikowski
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

package soot.dava;

import java.util.*;
import soot.*;
// import soot.dava.internal.*;
import soot.jimple.*;


public class Dava
{
    private static Dava instance = new Dava();

    public static Dava v() 
    {
        return instance;
    }

    private Dava() 
    {
    }
    /*
    public EmptyTrunk newEmptyTrunk()
    {
        return new DEmptyTrunk();
    }
    */
    public DavaBody newBody(SootMethod m)
    {
        return new DavaBody( m);
    }

        /** Returns a DavaBody constructed from the given body b. */
    public DavaBody newBody(Body b, String phase)
    {
        Map options = Scene.v().computePhaseOptions(phase, "");
        return new DavaBody(b, options);
    }

    /** Returns a DavaBody constructed from b. */
    public DavaBody newBody(Body b, String phase, String optionsString)
    {
        Map options = Scene.v().computePhaseOptions(phase, optionsString);
        return new DavaBody(b, options);
    }

    
    public Local newLocal(String name, Type t)
    {
        return Jimple.v().newLocal(name, t);
    }
    
    public IfElseTrunk newIfElseTrunk(ConditionExpr e, Trunk ifTrunk, Trunk elseTrunk)
    {
        return new IfElseTrunk(e, ifTrunk, elseTrunk);
    }

    public IfTrunk newIfTrunk(ConditionExpr e, Trunk ifTrunk)
    {
        return new IfTrunk(e, ifTrunk);
    }

    public WhileTrunk newWhileTrunk(ConditionExpr e, Trunk wTrunk)
    {
        return new WhileTrunk(e, wTrunk);
    }

    public TrunkTrunk newTrunkTrunk( Trunk t0, Trunk t1) 
    {
	return new TrunkTrunk( t0, t1);
    }
    
}






