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

import soot.tagkit.*;
import soot.*;
import soot.util.*;
import java.util.*;

/** Provides default implementations for the methods in Unit. */
public abstract class AbstractUnit extends AbstractHost implements Unit 
{

    /** Returns a deep clone of this object. */
    public abstract Object clone();
    
    /** Returns a list of Boxes containing Values used in this Unit.
     * The list of boxes is dynamically updated as the structure changes.
     * Note that they are returned in usual evaluation order.
     * (this is important for aggregation)
     */
    public List getUseBoxes()
    {
        return emptyList;
    }

    /** Returns a list of Boxes containing Values defined in this Unit.
     * The list of boxes is dynamically updated as the structure changes.
     */
    public List getDefBoxes()
    {
        return emptyList;
    }


    /** Returns a list of Boxes containing Units defined in this Unit; typically
     * branch targets.
     * The list of boxes is dynamically updated as the structure changes.
     */
    public List getUnitBoxes()
    {
        return emptyList;
    }

    /** Canonical AbstractUnit.emptyList list. */
    static final public List emptyList = Collections.EMPTY_LIST;

    /** List of UnitBoxes pointing to this Unit. */
    List boxesPointingToThis = null;

    /** List of ValueBoxes contained in this Unit. */
    List valueBoxes = null;

    /** Returns a list of Boxes pointing to this Unit. */
    public List getBoxesPointingToThis()
    {
        if( boxesPointingToThis == null ) return emptyList;
        return Collections.unmodifiableList( boxesPointingToThis );
    }

    public void addBoxPointingToThis( UnitBox b ) {
        if( boxesPointingToThis == null ) boxesPointingToThis = new ArrayList();
        boxesPointingToThis.add( b );
    }

    public void removeBoxPointingToThis( UnitBox b ) {
        if( boxesPointingToThis != null ) boxesPointingToThis.remove( b );
    }

    public void clearUnitBoxes() {
        for( Iterator it = getUnitBoxes().iterator(); it.hasNext(); ) {
            UnitBox ub = (UnitBox) it.next();
            ub.setUnit(null);
        }
    }
    
    /** Returns a list of ValueBoxes, either used or defined in this Unit. */
    public List getUseAndDefBoxes()
    {
        List useBoxes = getUseBoxes();
        List defBoxes = getDefBoxes();
        if( useBoxes.isEmpty() ) {
            if( defBoxes.isEmpty() ) {
                return emptyList;
            } else {
                return Collections.unmodifiableList(defBoxes);
            }
        } else {
            if( defBoxes.isEmpty() ) {
                return Collections.unmodifiableList(useBoxes);
            } else {
                valueBoxes = new ArrayList();

                valueBoxes.addAll(defBoxes);
                valueBoxes.addAll(useBoxes);

                valueBoxes = Collections.unmodifiableList(valueBoxes);

                return valueBoxes;
            }
        }
    }

    /** Used to implement the Switchable construct. */
    public void apply(Switch sw)
    {
    }

    public void redirectJumpsToThisTo(Unit newLocation)
    {
        List boxesPointing = this.getBoxesPointingToThis();

        Object[] boxes = boxesPointing.toArray();
        // important to change this to an array to have a static copy
        
        for(int i = 0; i < boxes.length; i++)
        {
            UnitBox box = (UnitBox) boxes[i];

            if(box.getUnit() != this)
                throw new RuntimeException("Something weird's happening");

            if(box.isBranchTarget())
                box.setUnit(newLocation);
        }

    }
}
