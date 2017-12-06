/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Nomair A. Naeem
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

package soot.dava.internal.javaRep;

import soot.*;
import soot.grimp.*;
import soot.grimp.internal.*;

public class DDecrementStmt extends GAssignStmt{
    public DDecrementStmt(Value variable, Value rvalue){
	super(variable,rvalue);
    }
    
    public Object clone(){ 
	return new DDecrementStmt(Grimp.cloneIfNecessary(getLeftOp()), 
				  Grimp.cloneIfNecessary(getRightOp()));
	
    }

    public String toString(){
        return getLeftOpBox().getValue().toString() + "--";
    }
    
    public void toString(UnitPrinter up) {
        getLeftOpBox().toString(up);
        up.literal("--");
    }
}






