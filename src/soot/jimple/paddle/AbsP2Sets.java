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
import soot.jimple.paddle.queue.*;

/** Manages the points-to sets for nodes.
 * @author Ondrej Lhotak
 */
public abstract class AbsP2Sets
{ 
    public PointsToSetReadOnly get( Context c, VarNode v ) {
        ContextVarNode cvn = ContextVarNode.get(c, v);
        if( cvn == null ) return EmptyPointsToSet.v();
        return get(cvn);
    }
    public PointsToSetReadOnly get( Context c, AllocDotField adf ) {
        ContextAllocDotField cadf = ContextAllocDotField.get(c, adf);
        if( cadf == null ) return EmptyPointsToSet.v();
        return get(cadf);
    }
    public PointsToSetInternal make( Context c, VarNode v ) {
        return make( ContextVarNode.make(c, v) );
    }
    public PointsToSetInternal make( Context c, AllocDotField adf ) {
        return make( ContextAllocDotField.make(c, adf) );
    }
    public PointsToSetReadOnly get( ContextVarNode cvn ) {
        return get(cvn.ctxt(), cvn.var());
    }
    public PointsToSetReadOnly get( ContextAllocDotField cadf ) {
        return get(cadf.ctxt(), cadf.adf());
    }
    public PointsToSetInternal make( ContextVarNode cvn ) {
        return make(cvn.ctxt(), cvn.var());
    }
    public PointsToSetInternal make( ContextAllocDotField cadf ) {
        return make(cadf.ctxt(), cadf.adf());
    }
    public abstract Rvarc_var_objc_obj getReader( VarNode cvn );
}
