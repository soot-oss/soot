/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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

import soot.*;
import soot.util.*;
import java.util.*;

public interface Stmt extends Unit
{
    public String toBriefString();
    public String toBriefString(Map stmtToName);
    public String toBriefString(String indentation);
    public String toBriefString(Map stmtToName, String indentation);
    public String toString();
    public String toString(Map stmtToName);
    public String toString(String indentation);
    public String toString(Map stmtToName, String indentation);
    public void toString(UnitPrinter up);

    public boolean containsInvokeExpr();
    public InvokeExpr getInvokeExpr();
    public ValueBox getInvokeExprBox();

    public boolean containsArrayRef();
    public ArrayRef getArrayRef();
    public ValueBox getArrayRefBox();

    public boolean containsFieldRef();
    public FieldRef getFieldRef();
    public ValueBox getFieldRefBox();
}

