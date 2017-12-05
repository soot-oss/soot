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



/*
  Nomair A. Naeem
  Used to represent !value in Dava AST
*/

package soot.dava.internal.javaRep;

import soot.*;
import soot.util.*;
import soot.grimp.*;
//import soot.jimple.*;
import soot.jimple.internal.*;

public class DNotExpr extends AbstractUnopExpr
{
    public DNotExpr(Value op)
    {
        super(Grimp.v().newExprBox(op));
    }
      
    public Object clone() 
    {
        return new DNotExpr(Grimp.cloneIfNecessary(getOpBox().getValue()));
    }

    public void toString( UnitPrinter up ) {
        up.literal( " ! (" );
        getOpBox().toString(up);
        up.literal( ")" );
    }

    public String toString()
    {
	return " ! (" + ( getOpBox().getValue()).toString() +")"; 
    }

    
    public Type getType(){
	Value op = getOpBox().getValue();
    
	if(op.getType().equals(IntType.v()) || op.getType().equals(ByteType.v()) ||
	   op.getType().equals(ShortType.v()) || op.getType().equals(BooleanType.v()) || 
	   op.getType().equals(CharType.v()))
            return IntType.v();
        else if(op.getType().equals(LongType.v()))
            return LongType.v();
        else if(op.getType().equals(DoubleType.v()))
            return DoubleType.v();
        else if(op.getType().equals(FloatType.v()))
            return FloatType.v();
        else
            return UnknownType.v();
    }

    /*
      NOTE THIS IS AN EMPTY IMPLEMENTATION OF APPLY METHOD
    */
    public void apply(Switch sw){
    }










    /** Compares the specified object with this one for structural equality. */
    public boolean equivTo(Object o)
    {
        if (o instanceof DNotExpr)
        {
            return getOpBox().getValue().equivTo(((DNotExpr)o).getOpBox().getValue());
        }
        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    public int equivHashCode() 
    {
        return getOpBox().getValue().equivHashCode();
    }
}
