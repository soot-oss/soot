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






package soot.grimp.internal;

import soot.*;
import soot.grimp.*;
import soot.jimple.internal.*;
import soot.jimple.*;
import soot.grimp.*;
import soot.jimple.internal.*;
import soot.util.*;
import java.util.*;

public class GTableSwitchStmt extends JTableSwitchStmt
{
    // This method is necessary to deal with constructor-must-be-first-ism.
    private static UnitBox[] getTargetBoxesArray(List targets)
    {
        UnitBox[] targetBoxes = new UnitBox[targets.size()];

        for(int i = 0; i < targetBoxes.length; i++)
            targetBoxes[i] = Grimp.v().newStmtBox((Stmt) targets.get(i));

        return targetBoxes;
    }

    public GTableSwitchStmt(Value key, int lowIndex, int highIndex, List targets,
                    Unit defaultTarget)
    {
        super(Grimp.v().newExprBox(key), lowIndex, highIndex,
              getTargetBoxesArray(targets), 
              Grimp.v().newStmtBox(defaultTarget));
    }

    
    public Object clone() 
    {
        return new GTableSwitchStmt(Grimp.cloneIfNecessary(getKey()), getLowIndex(), getHighIndex(), 
            getTargets(), getDefaultTarget());
    }

}


