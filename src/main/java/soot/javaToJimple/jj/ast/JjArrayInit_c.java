/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.util.*;
import polyglot.ext.jl.ast.*;
import java.util.*;

public class JjArrayInit_c extends ArrayInit_c {
    
    public JjArrayInit_c(Position pos, List elements) {
        super(pos, elements);
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av){
        if (elements.isEmpty()) {
            return child.type();
        }

        Type t = av.toType();

        //System.out.println("t type: "+t);
        if (t == null) {
            //System.out.println("t is null");
            return child.type();
        }
        if (! t.isArray()) {
            throw new InternalCompilerError("Type of array initializer must be " +
                                        "an array.", position());
        }

        t = t.toArray().base();

	for (Iterator i = elements.iterator(); i.hasNext(); ) {
	    Expr e = (Expr) i.next();

            if (e == child) {
                return t;
            }
        }

       return child.type(); 
    }
}
