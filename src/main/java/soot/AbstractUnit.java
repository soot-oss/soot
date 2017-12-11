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
import soot.util.*;

import java.util.*;

/** Provides default implementations for the methods in Unit. */
@SuppressWarnings("serial")
public abstract class AbstractUnit extends AbstractHost implements Unit 
{

    /** Returns a deep clone of this object. */
    public abstract Object clone();
    
    /** Returns a list of Boxes containing Values used in this Unit.
     * The list of boxes is dynamically updated as the structure changes.
     * Note that they are returned in usual evaluation order.
     * (this is important for aggregation)
     */
    @Override
    public List<ValueBox> getUseBoxes()
    {
        return Collections.emptyList();
    }

    /**
     * Returns a list of Boxes containing Values defined in this Unit.
     * The list of boxes is dynamically updated as the structure changes.
     */
    @Override
    public List<ValueBox> getDefBoxes()
    {
        return Collections.emptyList();
    }


    /** 
     * Returns a list of Boxes containing Units defined in this Unit; typically
     * branch targets.
     * The list of boxes is dynamically updated as the structure changes.
     */
    @Override
    public List<UnitBox> getUnitBoxes()
    {
        return Collections.emptyList();
    }
    
    /** List of UnitBoxes pointing to this Unit. */
    List<UnitBox> boxesPointingToThis = null;
    
    /** Returns a list of Boxes pointing to this Unit. */
    @Override
    public List<UnitBox> getBoxesPointingToThis()
    {
        if( boxesPointingToThis == null ) return Collections.emptyList();
        return Collections.unmodifiableList( boxesPointingToThis );
    }

    @Override
    public void addBoxPointingToThis( UnitBox b ) {
        if( boxesPointingToThis == null ) boxesPointingToThis = new ArrayList<UnitBox>();
        boxesPointingToThis.add( b );
    }

    @Override
    public void removeBoxPointingToThis( UnitBox b ) {
        if( boxesPointingToThis != null ) boxesPointingToThis.remove( b );
    }

    @Override
    public void clearUnitBoxes() {
    	for (UnitBox ub : getUnitBoxes())
    		ub.setUnit(null);
    }
    
    /** Returns a list of ValueBoxes, either used or defined in this Unit. */
    @Override
    public List<ValueBox> getUseAndDefBoxes()
    {
        List<ValueBox> useBoxes = getUseBoxes();
        List<ValueBox> defBoxes = getDefBoxes();
        if( useBoxes.isEmpty() ) {
        	return defBoxes;
        }
        else {
            if( defBoxes.isEmpty() ) {
                return useBoxes;
            }
            else {
                List<ValueBox> valueBoxes = new ArrayList<ValueBox>();
                valueBoxes.addAll(defBoxes);
                valueBoxes.addAll(useBoxes);
                return valueBoxes;
            }
        }
    }

    /** Used to implement the Switchable construct. */
    @Override
    public void apply(Switch sw)
    {
    }

    @Override
    public void redirectJumpsToThisTo(Unit newLocation)
    {
        List<UnitBox> boxesPointing = getBoxesPointingToThis();

        UnitBox[] boxes = boxesPointing.toArray(new UnitBox[boxesPointing.size()]);
        // important to change this to an array to have a static copy
        
        for (UnitBox element : boxes) {
            UnitBox box = element;

            if(box.getUnit() != this)
                throw new RuntimeException("Something weird's happening");

            if(box.isBranchTarget())
                box.setUnit(newLocation);
        }

    }
}
