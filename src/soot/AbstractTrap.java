/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


package soot;

import java.util.*;
import java.io.*;

/** Partial implementation of trap (exception catcher), used within Body
 * classes.  */
public class AbstractTrap implements Trap, Serializable
{
    /** The exception being caught. */
    protected transient SootClass exception;

    /** The first unit being trapped. */
    protected UnitBox beginUnitBox;

    /** The unit just before the last unit being trapped. */
    protected UnitBox endUnitBox;

    /** The unit to which execution flows after the caught exception is triggered. */
    protected UnitBox handlerUnitBox;

    /** The list of unitBoxes referred to in this Trap (begin, end and handler. */
    protected List<UnitBox> unitBoxes;

    private void readObject( ObjectInputStream in) throws IOException, ClassNotFoundException
    {
	in.defaultReadObject();
	exception = Scene.v().getSootClass( (String) in.readObject());
    }

    private void writeObject( ObjectOutputStream out) throws IOException
    {
	out.defaultWriteObject();
	out.writeObject( exception.getName());
    }


    /** Creates an AbstractTrap with the given exception, handler, begin and end units. */
    protected AbstractTrap(SootClass exception, UnitBox beginUnitBox,
                   UnitBox endUnitBox, UnitBox handlerUnitBox)
    {
        this.exception = exception; this.beginUnitBox = beginUnitBox;
        this.endUnitBox = endUnitBox; this.handlerUnitBox = handlerUnitBox;

        unitBoxes = new ArrayList<UnitBox>();
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
        return handlerUnitBox;
    }

    public UnitBox getBeginUnitBox()
    {
        return beginUnitBox;
    }

    public UnitBox getEndUnitBox()
    {
        return endUnitBox;
    }

    public List<UnitBox> getUnitBoxes()
    {
        return unitBoxes;
    }

    public void clearUnitBoxes()
    {
        Iterator<UnitBox> boxesIt = getUnitBoxes().iterator();
        while(boxesIt.hasNext()){
            UnitBox box = boxesIt.next();
            box.setUnit(null);
        }
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
