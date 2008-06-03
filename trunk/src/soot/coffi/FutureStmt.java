/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
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







package soot.coffi;

import soot.util.*;
import java.util.*;
import soot.*;

class FutureStmt extends soot.jimple.internal.AbstractStmt
{
    public Object object;

    List myEmptyList = Collections.unmodifiableList(new ArrayList());

    public FutureStmt(Object object)
    {
        this.object = object;
    }

    public FutureStmt()
    {
    }

    public String toString()
    {
        return "<futurestmt>";
    }
    
    public void toString(UnitPrinter up) {
        up.literal("<futurestmt>");
    }

    public List getDefBoxes()
    {
        return myEmptyList;
    }

    public List getUseBoxes()
    {
        return myEmptyList;
    }

    public List getUnitBoxes()
    {
        return myEmptyList;
    }

    public void apply(Switch sw)
    {
        ((soot.jimple.StmtSwitch) sw).defaultCase(this);
    }
    public boolean fallsThrough() {throw  new RuntimeException(); }
    public boolean branches() {throw new RuntimeException(); }
    public Object clone() {throw new RuntimeException();}
    
}



