/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot.jimple.paddle;
import soot.*;
import java.util.*;

/** Represents an allocation site node (Blue) in the pointer assignment graph.
 * @author Ondrej Lhotak
 */
public class LocalAllocNode extends AllocNode {
    public SootMethod getMethod() {
        return method;
    }
    public String toString() {
        if( PaddleScene.v().options().number_nodes() ) {
            return "LocalAllocNode "+getNumber()+" "+newExpr+" "+method+" type "+getType();
        } else {
            return "LocalAllocNode "+newExpr+" "+method+" type "+getType();
        }
    }
    /* End of public methods. */

    LocalAllocNode( Object newExpr, Type t, SootMethod m ) {
	super( newExpr, t );
        this.method = m;
        if( m == null ) throw new RuntimeException( "method shouldn't be null" );
    }

    /* End of package methods. */

    protected SootMethod method;
}

