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





package soot;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public class AbstractTrap implements Trap
{
    protected SootClass exception;
    protected UnitBox beginUnitBox;
    protected UnitBox endUnitBox;
    protected UnitBox handlerUnitBox;
    protected List unitBoxes;

    protected AbstractTrap(SootClass exception, UnitBox beginUnitBox,
                   UnitBox endUnitBox, UnitBox handlerUnitBox)
    {
        this.exception = exception; this.beginUnitBox = beginUnitBox;
        this.endUnitBox = endUnitBox; this.handlerUnitBox = handlerUnitBox;

        unitBoxes = new ArrayList();
        unitBoxes.add(beginUnitBox);
        unitBoxes.add(endUnitBox);
        unitBoxes.add(handlerUnitBox);
        unitBoxes = Collections.unmodifiableList(unitBoxes);
    }

    public Unit getBeginUnit()
    {
        return  beginUnitBox.getUnit();
    }

    public Unit getEndUnit()
    {
        return endUnitBox.getUnit();
    }

    public Unit getHandlerUnit()
    {
        return handlerUnitBox.getUnit();
    }

    public UnitBox getHandlerUnitBox()
    {
        return beginUnitBox;
    }

    public UnitBox getBeginUnitBox()
    {
        return beginUnitBox;
    }

    public UnitBox getEndUnitBox()
    {
        return endUnitBox;
    }

    public List getUnitBoxes()
    {
        return unitBoxes;
    }

    public SootClass getException()
    {
        return exception;
    }

    public void setBeginUnit(Unit beginUnit)
    {
        beginUnitBox.setUnit(beginUnit);
    }

    public void setEndUnit(Unit endUnit)
    {
        endUnitBox.setUnit(endUnit);
    }

    public void setHandlerUnit(Unit handlerUnit)
    {
        handlerUnitBox.setUnit(handlerUnit);
    }

    public void setException(SootClass exception)
    {
        this.exception = exception;
    }

    public Object clone()
    {
        throw new RuntimeException();
    }
}
