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






package soot.jimple.internal;

import soot.*;
import soot.jimple.*;
import java.util.*;

public abstract class AbstractDefinitionStmt extends AbstractStmt 
    implements DefinitionStmt
{
	public ValueBox leftBox;
    public ValueBox rightBox;

    List defBoxes;

    public Value getLeftOp()
    {
        return leftBox.getValue();
    }

    public Value getRightOp()
    {
        return rightBox.getValue();
    }

    public ValueBox getLeftOpBox()
    {
        return leftBox;
    }

    public ValueBox getRightOpBox()
    {
        return rightBox;
    }

    public List getDefBoxes()
    {
        return defBoxes;
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();

        list.addAll(leftBox.getValue().getUseBoxes());
        list.addAll(rightBox.getValue().getUseBoxes());
        list.add(rightBox);
        return list;
    }

    public boolean fallsThrough() { return true;}        
    public boolean branches() { return false;}
}






