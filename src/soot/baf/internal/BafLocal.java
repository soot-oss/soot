/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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


package soot.baf.internal;

import java.util.Collections;
import java.util.List;

import soot.Local;
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.util.Switch;

public class BafLocal implements Local
{
    String name;
    Type type;

    int fixedHashCode;
    boolean isHashCodeChosen;
	private Local originalLocal;
        
    public BafLocal(String name, Type t)
    {
        this.name = name;
        this.type = t;
    }

    /* JimpleLocals are *NOT* equivalent to Baf Locals! */
    @Override
	public boolean equivTo(Object o)
    {
        return this.equals( o );
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    @Override
	public int equivHashCode() 
    {
        return name.hashCode() * 101 + type.hashCode() * 17;
    }

    @Override
	public Object clone()
    {
        BafLocal baf = new BafLocal(name, type);
        baf.originalLocal = originalLocal;
        return baf;
    }
    
    public Local getOriginalLocal() {
    	return originalLocal;
    }
    
    public void setOriginalLocal(Local l) {
    	originalLocal = l;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public Type getType()
    {
        return type;
    }

    @Override
    public void setType(Type t)
    {
        this.type = t;
    }

    @Override
	public String toString()
    {
        return getName();
    }

    @Override
	public void toString( UnitPrinter up ) {
        up.local( this );
    }
    
    @Override
    public List<ValueBox> getUseBoxes()
    {
        return Collections.emptyList();
    }

    @Override
	public void apply(Switch s)
    {
        throw new RuntimeException("invalid case switch");
    }
    @Override
	public final int getNumber() { return number; }
    @Override
	public final void setNumber( int number ) { this.number = number; }

    private int number = 0;
}
