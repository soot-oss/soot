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


package soot.dava.internal.AST;

import soot.*;
import soot.dava.toolkits.base.AST.analysis.*;

public abstract class ASTCondition{
    public abstract void apply(Analysis a);
    public abstract void toString(UnitPrinter up);
    public abstract void flip();
    
    /*
     * should return true if there is a not symbol infront of it
     * for ASTBinaryCondition it should always return true since u can always flip it
     */
    public abstract boolean isNotted();
}
