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

import soot.util.*;
import java.util.*;

/** A trap (exception catcher), used within Body
 * classes.  Intermediate representations must use an implementation
 * of Trap to describe caught exceptions.
 *  */
public interface Trap extends UnitBoxOwner
{
    /** Returns the first trapped unit. */
    public Unit getBeginUnit();

    /** Returns the last trapped unit. */
    public Unit getEndUnit();

    /** Returns the unit handling the exception being trapped. */
    public Unit getHandlerUnit();

    /** Returns the box holding this trap's first trapped unit. */
    public UnitBox getBeginUnitBox();

    /** Returns the box holding this trap's last trapped unit. */
    public UnitBox getEndUnitBox();

    /** Returns the box holding the exception handler's unit. */
    public UnitBox getHandlerUnitBox();

    /** Returns the boxes for first, last and handler units. */
    public List getUnitBoxes();

    /** Returns the exception being caught. */
    public SootClass getException();

    /** Sets the first unit being trapped to <code>beginUnit</code>. */
    public void setBeginUnit(Unit beginUnit);

    /** Sets the last unit being trapped to <code>endUnit</code>. */
    public void setEndUnit(Unit endUnit);

    /** Sets the unit handling the exception to <code>handlerUnit</code>. */
    public void setHandlerUnit(Unit handlerUnit);

    /** Sets the exception being caught to <code>exception</code>. */
    public void setException(SootClass exception);

    /** Performs a shallow clone of this trap. */
    public Object clone();
}
