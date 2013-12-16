/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrick Lam
 *   extended 2002 Florian Loitsch
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
import soot.util.*;

/** 
 * Encapsulates the Value class, but uses EquivTo for equality comparisons. 
 * Also uses equivHashCode as its hash code. */
public class EquivalentValue implements Value {
    Value e;
    public EquivalentValue(Value e) {
    	if (e instanceof EquivalentValue)
    		e = ((EquivalentValue) e).e;
    	this.e = e;
    }
    
    public boolean equals(Object o) 
    { 
        if (o instanceof EquivalentValue) 
            o = ((EquivalentValue)o).e; 
        return e.equivTo(o);
    }

    /**
     * compares the encapsulated value with <code>v</code>, using
     * <code>equivTo</code>
     **/
    public boolean equivToValue(Value v) {
      return e.equivTo(v);
    }

    /**
     * compares the encapsulated value with <code>v</code>, using
     * <code>equals</code>
     **/
    public boolean equalsToValue(Value v) {
      return e.equals(v);
    }
    
    /**
     * @deprecated
     * @see #getValue()
     **/
    public Value getDeepestValue() {
    	return getValue();
    }

    public int hashCode() { return e.equivHashCode(); }
    public String toString() { return e.toString(); }
    public Value getValue() { return e; }

    /*********************************/
    /* implement the Value-interface */
    /*********************************/
    public List getUseBoxes() {
      return e.getUseBoxes();
    }

    public Type getType() {
      return e.getType();
    }

    public Object clone() {
      EquivalentValue equiVal = new EquivalentValue((Value)e.clone());
      return equiVal;
    }

    public boolean equivTo(Object o) {
      return e.equivTo(o);
    }

    public int equivHashCode() {
      return e.equivHashCode();
    }

    public void apply(Switch sw) {
      e.apply(sw);
    }
    public void toString( UnitPrinter up ) {
        e.toString(up);
    }
}
