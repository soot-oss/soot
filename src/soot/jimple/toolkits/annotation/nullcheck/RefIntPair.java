/*
 * Copyright (C) 2000 Janus
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
    RefIntPair class

    Immutable pair of an EquivalentValue representing a reference
    and an interger (representing a constant).
    With pretty printing for BranchedRefVarsAnalysis.
*/

package soot.jimple.toolkits.annotation.nullcheck;
import soot.*;

public class RefIntPair
{
    private EquivalentValue _ref;
    private int _val;
    private BranchedRefVarsAnalysis brva;
    
    // constructor is not public so that people go throught the ref pair constants factory on the analysis
    RefIntPair(EquivalentValue r, int v, BranchedRefVarsAnalysis brva)
    {
	this._ref = r;
	this._val = v;
        this.brva = brva;
    }

    public EquivalentValue ref ()
    { return this._ref; }

    public int val ()
    { return this._val; }

    public String toString()
    {
	String prefix = "("+_ref+", ";
	if (_val == brva.kNull)
	    return prefix+"null)";
	else if (_val == brva.kNonNull)
	    return prefix+"non-null)";
	else if (_val == brva.kTop)
	    return prefix+"top)";
	else if (_val == brva.kBottom)
	    return prefix+"bottom)";
	else
	    return prefix+_val+")";
    }
} // end class RefIntPair
