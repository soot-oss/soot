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





package soot.jimple;

import soot.tagkit.*;
import soot.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;
import java.io.*;

public class StaticFieldRef implements FieldRef, ConvertToBaf
{
    SootField field;

    private void readObject( ObjectInputStream in) throws IOException, ClassNotFoundException
    {
	field = Scene.v().getField( (String) in.readObject());
    }

    private void writeObject( ObjectOutputStream out) throws IOException
    {
	out.writeObject( field.getSignature());
    }

    protected StaticFieldRef(SootField field)
    {
        this.field = field;
    }

    public Object clone() 
    {
        return new StaticFieldRef(field);
    }

    public String toString()
    {
        return field.getSignature();
    }

    public void toString( UnitPrinter up ) {
        up.fieldRef(field);
    }

    public SootField getField()
    {
        return field;
    }

    public void setField(SootField field)
    {
        this.field = field;
    }

    public List getUseBoxes()
    {
        return AbstractUnit.emptyList;
    }

    public Type getType()
    {
        return field.getType();
    }

    public void apply(Switch sw)
    {
        ((RefSwitch) sw).caseStaticFieldRef(this);
    }
    
    public boolean equivTo(Object o)
    {
        if (o instanceof StaticFieldRef)
            return ((StaticFieldRef)o).field.equals(field);
        
        return false;
    }

    public int equivHashCode()
    {
        return field.equivHashCode();
    }

    public void convertToBaf(JimpleToBafContext context, List out)
    {
        Unit u = Baf.v().newStaticGetInst(field);
        out.add(u);

        Iterator it = context.getCurrentUnit().getTags().iterator();
        while(it.hasNext()) {
            u.addTag((Tag) it.next());
        }
    }
	public SootField XgetField() { return getField(); }

}
